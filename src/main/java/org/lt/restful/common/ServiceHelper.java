package org.lt.restful.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.lt.restful.common.resolver.JAX_RSResolver;
import org.lt.restful.common.resolver.ServiceResolver;
import org.lt.restful.common.resolver.SpringResolver;
import org.lt.restful.navigation.action.RestServiceItem;
import org.lt.restful.navigator.RestServiceProject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务相关工具类
 */
public class ServiceHelper {
    public static final Logger LOG = Logger.getInstance(ServiceHelper.class);
    PsiMethod psiMethod;

    public ServiceHelper(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public static List<RestServiceProject> buildRestServiceProjectListUsingResolver(Project project) {
//        System.out.println("buildRestServiceProjectList");
        List<RestServiceProject> serviceProjectList = new ArrayList<>();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<RestServiceItem> restServices = buildRestServiceItemListUsingResolver(module);
            if (restServices.size() > 0) {
                serviceProjectList.add(new RestServiceProject(module, restServices));
            }
        }

        return serviceProjectList;
    }

    public static List<RestServiceItem> buildRestServiceItemListUsingResolver(Module module) {

        List<RestServiceItem> itemList = new ArrayList<>();

        SpringResolver springResolver = new SpringResolver(module);
        JAX_RSResolver jaxrsResolver = new JAX_RSResolver(module);
        ServiceResolver[] resolvers = {springResolver,jaxrsResolver};

        for (ServiceResolver resolver : resolvers) {
            List<RestServiceItem> allSupportedServiceItemsInModule = resolver.findAllSupportedServiceItemsInModule();

            itemList.addAll(allSupportedServiceItemsInModule);
        }

        return itemList;
    }

    @NotNull
    public static List<RestServiceItem> buildRestServiceItemListUsingResolver(Project project) {
        List<RestServiceItem> itemList = new ArrayList<>();

        SpringResolver springResolver = new SpringResolver(project);
        JAX_RSResolver jaxrsResolver = new JAX_RSResolver(project);

        ServiceResolver[] resolvers = {springResolver,jaxrsResolver};
        for (ServiceResolver resolver : resolvers) {
            List<RestServiceItem> allSupportedServiceItemsInProject = resolver.findAllSupportedServiceItemsInProject();

            itemList.addAll(allSupportedServiceItemsInProject);
        }

        return itemList;
    }
}
