package org.lt.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.lt.restful.common.PsiClassHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.LightClassUtil;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.KtClassOrObject;

import java.awt.datatransfer.StringSelection;

public class ConvertClassToJSONAction extends AbstractBaseAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiClass psiClass = getPsiClass(psiElement);

        if (psiClass == null) {
            return;
        }

        String json = PsiClassHelper.create(psiClass).convertClassToJSON(myProject(e), true);
        CopyPasteManager.getInstance().setContents(new StringSelection(json));
    }

    @Nullable
    protected PsiClass getPsiClass(PsiElement psiElement) {
        if (psiElement instanceof PsiClass) {
            return (PsiClass) psiElement;
        }

        if (psiElement instanceof KtClassOrObject && LightClassUtil.INSTANCE.canGenerateLightClass((KtClassOrObject) psiElement)) {
            return LightClassUtilsKt.toLightClass((KtClassOrObject) psiElement);
        }

        return null;
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        setActionPresentationVisible(e, psiElement instanceof PsiClass || psiElement instanceof KtClassOrObject);
    }
}
