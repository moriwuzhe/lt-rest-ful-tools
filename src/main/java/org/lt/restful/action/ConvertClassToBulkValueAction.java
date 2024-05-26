package org.lt.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.lt.restful.common.PsiClassHelper;
import org.lt.restful.constants.FieldConstant;
import org.lt.utils.ToolUtils;
import org.jetbrains.kotlin.asJava.LightClassUtil;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.KtClassOrObject;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertClassToBulkValueAction extends AbstractBaseAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiClass psiClass = getPsiClass(psiElement);
        if (psiClass == null) {
            return;
        }

        final List<PsiField> fields = getFields(psiClass);
        if (ToolUtils.isEmpty(fields)) {
            return;
        }

        String collect = fields.stream()
                .filter(field -> !FieldConstant.SERIAL_VERSION_UID.equals(field.getName()))
                .map(field -> String.format("%s:%s\r\n", field.getName(), PsiClassHelper.getJavaBaseTypeDefaultValue(field.getType().getPresentableText())))
                .collect(Collectors.joining());

        CopyPasteManager.getInstance().setContents(new StringSelection(collect));
    }

    protected List<PsiClass> getPsiClassLinkList(PsiClass psiClass) {
        List<PsiClass> psiClassList = new ArrayList<>();
        PsiClass currentClass = psiClass;
        while (null != currentClass && !FieldConstant.OBJECT_STRING.equals(currentClass.getName())) {
            psiClassList.add(currentClass);
            currentClass = currentClass.getSuperClass();
        }
        Collections.reverse(psiClassList);
        return psiClassList;
    }

    protected List<PsiField> getFields(PsiClass psiClass) {
        return getPsiClassLinkList(psiClass).stream().flatMap(pc -> Arrays.stream(pc.getFields())).collect(Collectors.toList());
    }

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
