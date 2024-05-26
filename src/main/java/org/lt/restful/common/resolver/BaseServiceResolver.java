package org.lt.restful.common.resolver;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.lt.restful.constants.SymbolConstant;
import org.lt.restful.method.RequestPath;
import org.lt.restful.navigation.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseServiceResolver implements ServiceResolver {
    protected Module myModule;
    protected Project myProject;

    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInModule() {
        if (myModule == null) {
            return Collections.emptyList();
        }
        GlobalSearchScope globalSearchScope = GlobalSearchScope.moduleScope(myModule);
        return getRestServiceItemList(myModule.getProject(), globalSearchScope);
    }


    public abstract List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope);

    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInProject() {
        if (myProject == null && myModule != null) {
            myProject = myModule.getProject();
        }

        if (myProject == null) {
            return Collections.emptyList();
        }

        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(myProject);
        return getRestServiceItemList(myProject, globalSearchScope);
    }

    @NotNull
    protected RestServiceItem createRestServiceItem(PsiElement psiMethod, String classUriPath, RequestPath requestMapping) {
        String slash = SymbolConstant.SLASH;
        if (!classUriPath.startsWith(slash)) {
            classUriPath = slash.concat(classUriPath);
        }
        if (!classUriPath.endsWith(slash)) {
            classUriPath = classUriPath.concat(slash);
        }

        String methodPath = requestMapping.getPath();

        if (methodPath.startsWith(slash)) {
            methodPath = methodPath.substring(1);
        }
        String requestPath = classUriPath + methodPath;
        return new RestServiceItem(myModule, psiMethod, requestMapping.getMethod(), requestPath);
    }

}
