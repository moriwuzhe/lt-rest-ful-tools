package org.lt.restful.navigator;

import com.intellij.ide.util.treeView.TreeState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleTree;
import org.lt.utils.IconUtils;
import org.lt.utils.I18nBundleUtils;
import org.lt.utils.ToolUtils;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.net.URL;

@State(name = "RestServicesNavigator", storages = { @Storage(StoragePathMacros.WORKSPACE_FILE) })
public class RestServicesNavigator extends AbstractProjectComponent
		implements PersistentStateComponent<RestServicesNavigatorState>, ProjectComponent {

	public static final Logger LOG = Logger.getInstance(RestServicesNavigator.class);

	public static final String TOOL_WINDOW_ID = "RestServices";

	private static final URL SYNC_ICON_URL = RestServicesNavigator.class.getResource("/actions/refresh.png");

	protected final Project myProject;

	// private JTree myTree;
	protected RestServiceStructure myStructure;

	RestServicesNavigatorState myState = new RestServicesNavigatorState();

	private SimpleTree myTree;

	private ToolWindowEx myToolWindow;

	private RestServiceProjectsManager myProjectsManager;

	public RestServicesNavigator(Project myProject) {
		super(myProject);
		this.myProject = myProject;
		myProjectsManager = RestServiceProjectsManager.getInstance(myProject);
	}

	public static RestServicesNavigator getInstance(Project p) {
		return p.getComponent(RestServicesNavigator.class);
	}

	private void initTree() {
		myTree = new SimpleTree() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				final JLabel myLabel = new JLabel(I18nBundleUtils.message("navigator.nothing.to.display",
						ToolUtils.formatHtmlImage(SYNC_ICON_URL)));

				if (myProject.isInitialized()) {
					return;
				}
				myLabel.setFont(getFont());
				myLabel.setBackground(getBackground());
				myLabel.setForeground(getForeground());
				Rectangle bounds = getBounds();
				Dimension size = myLabel.getPreferredSize();
				myLabel.setBounds(0, 0, size.width, size.height);

				int x = (bounds.width - size.width) / 2;
				Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
				try {
					myLabel.paint(g2);
				}
				finally {
					g2.dispose();
				}
			}
		};
		myTree.getEmptyText().clear();

		myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}

	@Override
	public void initComponent() {
		listenForProjectsChanges();
		ToolUtils.runWhenSmart(myProject, () -> {
			if (myProject.isDisposed()) {
				return;
			}
			if (myProject.isOpen()) {
				initToolWindow();
			}
		});
	}

	private void initToolWindow() {
		final ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(myProject);
		myToolWindow = (ToolWindowEx) manager.getToolWindow(TOOL_WINDOW_ID);
		if (myToolWindow != null) {
			return;
		}

		initTree();

		myToolWindow = (ToolWindowEx) manager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.RIGHT,
				myProject, true);
		myToolWindow.setIcon(IconUtils.SERVICE);

		JPanel panel = new RestServicesNavigatorPanel(myProject, myTree);
		final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
		final Content content = contentFactory.createContent(panel, "", false);
		ContentManager contentManager = myToolWindow.getContentManager();
		contentManager.addContent(content);
		contentManager.setSelectedContent(content, false);

		// final ToolWindowManagerAdapter listener = new ToolWindowManagerAdapter() {
		// boolean wasVisible = false;
		//
		// @Override
		// public void stateChanged() {
		// if (myToolWindow.isDisposed()) {
		// return;
		// }
		// boolean visible = myToolWindow.isVisible();
		// if (!visible || wasVisible) {
		// return;
		// }
		// scheduleStructureUpdate();
		// wasVisible = true;
		// }
		// };
		// manager.addToolWindowManagerListener(listener, myProject);

		// todo: extend toolWindows right click
		/*
		 * ActionManager actionManager = ActionManager.getInstance(); DefaultActionGroup
		 * group = new DefaultActionGroup();
		 * group.add(actionManager.getAction("Maven.GroupProjects"));
		 * group.add(actionManager.getAction("Maven.ShowIgnored"));
		 * group.add(actionManager.getAction("Maven.ShowBasicPhasesOnly"));
		 * group.add(actionManager.getAction("Maven.AlwaysShowArtifactId")); // 默认显示 app
		 * serviceName group.add(actionManager.getAction("Maven.ShowVersions")); //
		 * myToolWindow.setAdditionalGearActions(group);
		 */

	}

	boolean wasVisible = false;

	public void stateChanged() {
		if (myToolWindow == null) {
			return;
		}
		if (myToolWindow.isDisposed()) {
			return;
		}
		boolean visible = myToolWindow.isVisible();
		if (!visible || wasVisible) {
			return;
		}
		scheduleStructureUpdate();
		wasVisible = true;
	}

	public void scheduleStructureUpdate() {
		scheduleStructureRequest(() -> myStructure.update());

	}

	private void scheduleStructureRequest(final Runnable r) {
		if (myToolWindow == null) {
			return;
		}
		ToolUtils.smartInvokeLater(myProject, () -> {
			if (!myToolWindow.isVisible()) {
				return;
			}

			boolean shouldCreate = myStructure == null;
			if (shouldCreate) {
				initStructure();
			}

			r.run();
			// fixme: compat
			// if (shouldCreate) {
			// TreeState.createFrom(myState.treeState).applyTo(myTree);
			// }
		});

	}

	private void initStructure() {
		myStructure = new RestServiceStructure(myProject, myProjectsManager, myTree);

	}

	private void listenForProjectsChanges() {
		// todo :
	}

	@Nullable
	@Override
	public RestServicesNavigatorState getState() {
		ApplicationManager.getApplication().assertIsDispatchThread();
		if (myStructure != null) {
			try {
				myState.treeState = new Element("root");
				TreeState.createOn(myTree).writeExternal(myState.treeState);
			}
			catch (WriteExternalException e) {
				LOG.warn(e);
			}
		}
		return myState;
	}

	@Override
	public void loadState(RestServicesNavigatorState state) {
		myState = state;
		scheduleStructureUpdate();
	}

}
