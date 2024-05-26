package org.lt.restful.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import org.lt.utils.I18nBundleUtils;

import java.awt.*;


public abstract class AbstractBaseAction extends AnAction {

    protected JBColor fillColor = new JBColor(new Color(186, 238, 186), new Color(73, 117, 73));

    protected Module myModule(AnActionEvent e) {
        return e.getData(LangDataKeys.MODULE);
    }

    protected Project myProject(AnActionEvent e) {
        return getEventProject(e);
    }

    protected void setActionPresentationVisible(AnActionEvent e, boolean visible) {
        e.getPresentation().setVisible(visible);
    }

    protected void showPopupBalloon(final Editor myEditor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory factory = JBPopupFactory.getInstance();
            String htmlContent = I18nBundleUtils.message("copied.successfully");
            factory.createHtmlTextBalloonBuilder(htmlContent , null, fillColor, null)
                    .setFadeoutTime(1000)
                    .createBalloon()
                    .show(factory.guessBestPopupLocation(myEditor), Balloon.Position.atRight);
        });
    }

}
