package org.lt.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.lt.restful.action.AbstractBaseAction;
import org.lt.restful.annotations.SpringControllerAnnotation;
import org.lt.restful.annotations.SpringRequestMethodAnnotation;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.Arrays;
import java.util.List;

/**
 * Restful method （restful 方法添加方法 ）
 */
public abstract class SpringAnnotatedMethodAction extends AbstractBaseAction {

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
        if (psiElement instanceof KtNamedFunction) {
            KtNamedFunction ktNamedFunction = (KtNamedFunction) psiElement;
            PsiElement parentPsi = psiElement.getParent().getParent();
            if (parentPsi instanceof KtClassOrObject) {
                KtLightClass ktLightClass = LightClassUtilsKt.toLightClass(((KtClassOrObject) parentPsi));

                List<PsiMethod> psiMethods = LightClassUtilsKt.toLightMethods(ktNamedFunction);

                visible = (isRestController(ktLightClass) || isRestfulMethod(psiMethods.get(0)));

            }
        }

        setActionPresentationVisible(e, visible);
    }

    //包含 "RestController" "Controller"
    private boolean isRestController(PsiClass containingClass) {
        if (containingClass == null || containingClass.getModifierList() == null) {
            return false;
        }
        PsiModifierList modifierList = containingClass.getModifierList();

        /*return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;*/

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null /*||
                modifierList.findAnnotation(JaxrsRequestAnnotation.PATH.getQualifiedName()) != null*/;
    }

    private boolean isRestfulMethod(PsiMethod psiMethod) {
        final PsiModifierList modifierList = psiMethod.getModifierList();
        if (modifierList == null) {
            return false;
        }
        PsiAnnotation[] annotations = modifierList.getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(SpringRequestMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) return match;
        }

        return false;
    }


}
