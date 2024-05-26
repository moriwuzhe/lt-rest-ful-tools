package org.lt.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.*;
import org.lt.restful.action.AbstractBaseAction;
import org.lt.restful.annotations.JAX_RSHttpMethodAnnotation;
import org.lt.restful.annotations.JAX_RSRequestAnnotation;
import org.lt.restful.annotations.SpringControllerAnnotation;
import org.lt.restful.annotations.SpringRequestMethodAnnotation;
import org.lt.restful.common.PsiMethodHelper;

import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 * todo: 没考虑RequestMapping 多个值的情况
 */
public class GenerateUrlAction /*extends RestfulMethodSpringSupportedAction*/ extends AbstractBaseAction {
    Editor myEditor;

    @Override
    public void actionPerformed(AnActionEvent e) {

        myEditor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        PsiMethod psiMethod = (PsiMethod) psiElement;
        if (psiMethod == null) {
            return;
        }

        //TODO: 需完善 jaxrs 支持
        String servicePath;
        if (isJaxrsRestMethod(psiMethod)) {
            servicePath = PsiMethodHelper.create(psiMethod).buildServiceUriPath();
        } else {
            servicePath = PsiMethodHelper.create(psiMethod).buildServiceUriPathWithParams();
        }

        CopyPasteManager.getInstance().setContents(new StringSelection(servicePath));
        showPopupBalloon(myEditor);
    }

    private boolean isJaxrsRestMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JAX_RSHttpMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        return false;
    }

    /**
     * spring rest 方法被选中才触发
     *
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

        boolean visible = false;

        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            // rest method 或标注了RestController 注解
            visible = (isRestController(psiMethod.getContainingClass()) || isRestfulMethod(psiMethod));
        }

        setActionPresentationVisible(e, visible);
    }

    //包含 "RestController" "Controller"
    private boolean isRestController(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        /*return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;*/

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(JAX_RSRequestAnnotation.PATH.getQualifiedName()) != null;
    }

    private boolean isRestfulMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(SpringRequestMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JAX_RSHttpMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        return false;
    }


    // private void showPopupBalloon(final String result) {
    //     ApplicationManager.getApplication().invokeLater(new Runnable() {
    //         @Override
    //         public void run() {
    //             JBPopupFactory factory = JBPopupFactory.getInstance();
    //             factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
    //                     .setFadeoutTime(5000)
    //                     .createBalloon()
    //                     .show(factory.guessBestPopupLocation(myEditor), Balloon.Position.above);
    //         }
    //     });
    // }

}
