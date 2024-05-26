package org.lt.restful.common.JAX_RS;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.lt.restful.annotations.JAX_RSHttpMethodAnnotation;
import org.lt.restful.annotations.JAX_RSPathAnnotation;
import org.lt.restful.common.PsiAnnotationHelper;
import org.lt.restful.constants.FieldConstant;
import org.lt.restful.method.RequestPath;
import org.lt.utils.ToolUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AnnotationHelper {

    public static @Nullable PsiAnnotation findPsiAnnotation(PsiMethod psiMethod, String qualifiedName) {
        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.findAnnotation(qualifiedName);
    }

    public static @Nullable PsiAnnotation findPsiAnnotation(PsiClass psiClass, String qualifiedName) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return null;
        }
        return modifierList.findAnnotation(qualifiedName);
    }

    /**
     * 过滤所有注解
     */
    public static RequestPath[] getRequestPaths(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();
        PsiAnnotation wsPathAnnotation = findPsiAnnotation(psiMethod, JAX_RSPathAnnotation.PATH.getQualifiedName());
        String path = wsPathAnnotation == null ? psiMethod.getName() : getWsPathValue(wsPathAnnotation);

        JAX_RSHttpMethodAnnotation[] values = JAX_RSHttpMethodAnnotation.values();
        List<RequestPath> tempList = new ArrayList<>();
        for (PsiAnnotation a : annotations) {
            for (JAX_RSHttpMethodAnnotation methodAnnotation : values) {
                if (Objects.equals(a.getQualifiedName(), methodAnnotation.getQualifiedName())) {
                    tempList.add(new RequestPath(path, methodAnnotation.getShortName()));
                }
            }
        }
        return tempList.toArray(new RequestPath[0]);
    }


    public static String getClassUriPath(PsiClass psiClass) {
        PsiAnnotation annotation = findPsiAnnotation(psiClass, JAX_RSPathAnnotation.PATH.getQualifiedName());
        if (annotation == null) {
            return ToolUtils.EMPTY;
        }
        return getWsPathValue(annotation);
    }

    public static String getMethodUriPath(PsiMethod psiMethod) {
        boolean present = Arrays.stream(JAX_RSHttpMethodAnnotation.values()).anyMatch(annotation -> findPsiAnnotation(psiMethod, annotation.getQualifiedName()) != null);

        if (present) {
            PsiAnnotation annotation = psiMethod.getModifierList().findAnnotation(JAX_RSPathAnnotation.PATH .getQualifiedName());
            return getWsPathValue(annotation);
        }

        String methodName = psiMethod.getName();
        return ToolUtils.uncapitalize(methodName);
    }

    private static String getWsPathValue(PsiAnnotation annotation) {
        String value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, FieldConstant.VALUE_STRING);
        return ToolUtils.trimToEmpty(value);
    }

}