package org.lt.restful.navigator;

import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.UIUtil;
import org.lt.restful.common.RequestHelper;
import org.lt.utils.JsonUtils;
import org.lt.utils.ToolUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

//import com.intellij.openapi.editor.colors.impl.AppEditorFontOptions;
//import com.intellij.ui.components.JBPanelWithEmptyText;

public class RestServiceDetail extends JBPanel/*WithEmptyText*/ {
    private static RestServiceDetail restServiceDetail;
    public JTextField urlField;
    public JPanel urlPanel;
    public JTextField methodField;
    public JButton sendButton;
    public JTabbedPane requestTabbedPane;

    public RSyntaxTextArea requestHeadersTextArea;

    public RSyntaxTextArea requestParamsTextArea;
    public RSyntaxTextArea requestBodyTextArea;
    public RSyntaxTextArea responseTextArea;

    private RestServiceDetail() {
        super();
        initComponent();
    }

    public static RestServiceDetail getInstance(Project p) {
        return p.getComponent(RestServiceDetail.class);
    }

    public void initComponent() {
        initUI();
        initActions();
        initTab();
    }

    private void initActions() {
        bindSendButtonActionListener();
        bindUrlTextActionListener();
    }

    public void initTab() {
        String jsonFormat = "Try press 'Ctrl(Cmd) Enter'";
        RSyntaxTextArea textArea = createTextArea("", SyntaxConstants.SYNTAX_STYLE_JSON);
        addRequestTabbedPane(jsonFormat, textArea);
    }

    @Override
    protected void printComponent(Graphics g) {
        super.printComponent(g);
    }

    private void initUI() {
        urlField.setAutoscrolls(true);
        urlPanel = new JBPanel();
        GridLayoutManager mgr = new GridLayoutManager(1, 3);
        mgr.setHGap(1);
        mgr.setVGap(1);
        urlPanel.setLayout(mgr);

        urlPanel.add(methodField,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
        urlPanel.add(urlField,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
        urlPanel.add(sendButton,
                new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new GridLayoutManager(2, 1));

        this.add(urlPanel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
        this.add(requestTabbedPane,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
    }

    private void bindSendButtonActionListener() {
        sendButton.addActionListener(e -> {
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Sending Request") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    final Runnable runnable = () -> {
                        String url = urlField.getText();
                        Map<String, String> params = ToolUtils.textToParamMap(requestParamsTextArea.getText());
                        Map<String, String> headers = ToolUtils.textToParamMap(requestHeadersTextArea.getText());
                        String response = RequestHelper.request(url, methodField.getText(), params, headers, requestBodyTextArea.getText());
                        System.err.println(response);
                        addResponseTabPanel(response);
                    };
                    runnable.run();
                }
            });

        });
    }

    private void bindUrlTextActionListener() {
        requestTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getClickCount());
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                urlField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mousePressed(e);
                urlField.selectAll();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mousePressed(e);
                urlField.selectAll();
            }
        });

        methodField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                methodField.selectAll();
            }
        });
    }


    public void addHeadersTab(String requestParams) {
        StringBuilder paramBuilder = new StringBuilder();

        if (ToolUtils.isNotBlank(requestParams)) {
            String[] paramArray = requestParams.split("&");
            for (String paramPairStr : paramArray) {
                String[] paramPair = paramPairStr.split("=");

                String param = paramPair[0];
                String value = paramPairStr.substring(param.length() + 1);
                paramBuilder.append(param).append(" : ").append(value).append("\n");
            }
        }

        if (requestHeadersTextArea == null) {
            requestHeadersTextArea = createTextArea(paramBuilder.toString(), SyntaxConstants.SYNTAX_STYLE_NONE);
        } else {
            requestHeadersTextArea.setText(paramBuilder.toString());
        }

        addRequestTabbedPane("RequestHeaders", requestHeadersTextArea);
    }

    public void addRequestParamsTab(String requestParams) {
        StringBuilder paramBuilder = new StringBuilder();

        if (ToolUtils.isNotBlank(requestParams)) {
            String[] paramArray = requestParams.split("&");
            for (String paramPairStr : paramArray) {
                String[] paramPair = paramPairStr.split("=");

                String param = paramPair[0];
                String value = paramPairStr.substring(param.length() + 1);
                paramBuilder.append(param).append(" : ").append(value).append("\n");
            }
        }

        if (requestParamsTextArea == null) {
            requestParamsTextArea = createTextArea(paramBuilder.toString(), SyntaxConstants.SYNTAX_STYLE_NONE);
        } else {
            requestParamsTextArea.setText(paramBuilder.toString());
        }

        addRequestTabbedPane("RequestParams", requestParamsTextArea);
    }

    public void addRequestBodyTabPanel(String text) {
        String reqBodyTitle = "RequestBody";
        if (requestBodyTextArea == null) {
            requestBodyTextArea = createTextArea(text, SyntaxConstants.SYNTAX_STYLE_JSON);
        } else {
            requestBodyTextArea.setText(text);
        }
        addRequestTabbedPane(reqBodyTitle, this.requestBodyTextArea);
    }


    public void addRequestTabbedPane(String title, RSyntaxTextArea jTextArea) {

        if (UIUtil.isUnderDarcula()) {
            jTextArea.setBackground(new Color(0x2B2B2B));
            jTextArea.setForeground(new Color(0xBBBBBB));

            jTextArea.setSelectionColor(new Color(0x28437F));
            jTextArea.setCurrentLineHighlightColor(new Color(0x323232));
        } else {
            jTextArea.setBackground(new Color(0xFFFFFF));
            jTextArea.setForeground(new Color(0x000000));
            jTextArea.setSelectionColor(new Color(0xA6D2FF));
            jTextArea.setCurrentLineHighlightColor(new Color(0xFCFAED));
        }

        RTextScrollPane jbScrollPane = new RTextScrollPane(jTextArea);
        jTextArea.addKeyListener(new TextAreaKeyAdapter(jTextArea));

        requestTabbedPane.addTab(title, jbScrollPane);
        requestTabbedPane.setSelectedComponent(jbScrollPane);
    }


    public void addResponseTabPanel(String text) {
        //FIXME RSyntaxTextArea 中文乱码
        String responseTabTitle = "Response";
        if (responseTextArea == null) {
            responseTextArea = createTextArea(text, SyntaxConstants.SYNTAX_STYLE_JSON);
            addRequestTabbedPane(responseTabTitle, responseTextArea);
        } else {
            Component componentAt = null;
            responseTextArea.setText(text);
            int tabCount = requestTabbedPane.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                if (requestTabbedPane.getTitleAt(i).equals(responseTabTitle)) {
                    componentAt = requestTabbedPane.getComponentAt(i);
                    requestTabbedPane.addTab(responseTabTitle, componentAt);
                    requestTabbedPane.setSelectedComponent(componentAt);
                    break;
                }
            }
            if (componentAt == null) {
                addRequestTabbedPane(responseTabTitle, responseTextArea);
            }
        }
    }

    @NotNull
    public RSyntaxTextArea createTextArea(String text, String style) {
        Font font = getTextAreaFont();

        RSyntaxTextArea jTextArea = new RSyntaxTextArea(text);
        jTextArea.setFont(font);
        jTextArea.setSyntaxEditingStyle(style);
        jTextArea.setCodeFoldingEnabled(true);

        jTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = jTextArea.getText();
                getEffectiveFont(text);
            }
        });

        jTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    CopyPasteManager.getInstance().setContents(new StringSelection(jTextArea.getText()));
                }
            }
        });
        return jTextArea;
    }

    public Font getTextAreaFont() {
        if (SystemInfo.isWindows) {
            return new java.awt.Font("宋体", Font.PLAIN, 14);
        }
        if (SystemInfoRt.isMac) {
            return new Font("Menlo", Font.PLAIN, 14);
        }
        return new Font("Monospaced", Font.PLAIN, 14);
    }

    @NotNull
    private Font getEffectiveFont(String text) {
        FontPreferences fontPreferences = this.getFontPreferences();
        List<String> effectiveFontFamilies = fontPreferences.getEffectiveFontFamilies();

        int size = fontPreferences.getSize(fontPreferences.getFontFamily());
        Font font = new Font(FontPreferences.DEFAULT_FONT_NAME, Font.PLAIN, size);
        for (String effectiveFontFamily : effectiveFontFamilies) {
            Font effectiveFont = new Font(effectiveFontFamily, Font.PLAIN, size);
            if (effectiveFont.canDisplayUpTo(text) == -1) {
                return effectiveFont;
            }
        }
        return font;
    }

    @NotNull
    private final FontPreferences getFontPreferences() {
        return new FontPreferences();
    }

    @NotNull
    private Font getEffectiveFont() {
        FontPreferences fontPreferences = this.getFontPreferences();
        String fontFamily = fontPreferences.getFontFamily();
        int size = fontPreferences.getSize(fontFamily);
        return new Font(FontPreferences.DEFAULT_FONT_NAME, Font.PLAIN, size);
    }


    public void resetRequestTabbedPane() {
        this.requestTabbedPane.removeAll();
        resetTextComponent(requestParamsTextArea);
        resetTextComponent(requestBodyTextArea);
        resetTextComponent(responseTextArea);
    }

    private void resetTextComponent(JTextArea textComponent) {
        if (textComponent != null && ToolUtils.isNotBlank(textComponent.getText())) {
            textComponent.setText("");
        }
    }

    public void setMethodValue(String method) {
        methodField.setText(String.valueOf(method));
    }

    public void setUrlValue(String url) {
        urlField.setText(url);
    }

    private static class TextAreaKeyAdapter extends KeyAdapter {
        private final JTextArea jTextArea;

        public TextAreaKeyAdapter(JTextArea jTextArea) {
            this.jTextArea = jTextArea;
        }

        @Override
        public void keyPressed(KeyEvent event) {
            super.keyPressed(event);
            if ((event.getKeyCode() == KeyEvent.VK_ENTER)
                    && (event.isControlDown() || event.isMetaDown())) {
                String oldValue = jTextArea.getText();
                if (!JsonUtils.isValidJson(oldValue)) {
                    return;
                }
                jTextArea.setText(ToolUtils.toString(oldValue));
            }
        }
    }


}