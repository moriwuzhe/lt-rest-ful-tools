package org.lt.restful.common;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.lt.utils.ToolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.List;
import java.util.Map;

/**
 * KtFunction处理类
 */
public class KtFunctionHelper extends PsiMethodHelper {
    KtNamedFunction ktNamedFunction;
    Project myProject;
    Module myModule;

    public static KtFunctionHelper create(@NotNull KtNamedFunction psiMethod) {
        return new KtFunctionHelper(psiMethod);
    }

    public KtFunctionHelper withModule(Module module) {
        this.myModule = module;
        return this;
    }

    protected KtFunctionHelper(@NotNull KtNamedFunction ktNamedFunction) {
        super(null);
        List<PsiMethod> psiMethods = LightClassUtilsKt.toLightMethods(ktNamedFunction);
        super.psiMethod = psiMethods.get(0);
        this.ktNamedFunction = ktNamedFunction;
    }

    @Override
    @NotNull
    protected Project getProject() {
        myProject =  psiMethod.getProject();
        return myProject;
    }

    /**
     * 构建URL参数 key value
     * @return
     */
    @Override
    public String buildParamString() {
        Map<String, Object> baseTypeParamMap = getBaseTypeParameterMap();
        return ToolUtils.mapToParamString(baseTypeParamMap);
    }

}
