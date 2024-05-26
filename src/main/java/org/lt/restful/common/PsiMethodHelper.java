package org.lt.restful.common;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.lt.restful.annotations.JAX_RSRequestAnnotation;
import org.lt.restful.annotations.SpringControllerAnnotation;
import org.lt.restful.annotations.SpringRequestHeaderAnnotations;
import org.lt.restful.common.JAX_RS.AnnotationHelper;
import org.lt.restful.common.spring.RequestMappingAnnotationHelper;
import org.lt.restful.constants.FieldConstant;
import org.lt.restful.constants.SymbolConstant;
import org.lt.restful.method.Parameter;
import org.lt.restful.method.action.ModuleHelper;
import org.lt.utils.ToolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static org.lt.restful.annotations.SpringRequestParamAnnotations.*;

/**
 * PsiMethod处理类
 */
public class PsiMethodHelper {
    PsiMethod psiMethod;
    Project myProject;
    Module myModule;

    public static PsiMethodHelper create(@NotNull PsiMethod psiMethod) {
        return new PsiMethodHelper(psiMethod);
    }

    public PsiMethodHelper withModule(Module module) {
        this.myModule = module;
        return this;
    }

    protected PsiMethodHelper(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    @NotNull
    protected Project getProject() {
        myProject = psiMethod.getProject();
        return myProject;
    }

    /**
     * 构建URL参数 key value
     *
     * @return
     */
    public String buildHeaderString() {
        List<Parameter> parameterList = getHeaderMap();
        Map<String, Object> collect = parameterList.stream().collect(Collectors.toMap(Parameter::getParamName,
                i -> PsiClassHelper.getJavaBaseTypeDefaultValue(i.getShortTypeName())));
        return ToolUtils.mapToParamString(collect);
    }

    /**
     * 构建URL参数 key value
     *
     * @return
     */
    public String buildParamString() {
        Map<String, Object> baseTypeParameterMap = getBaseTypeParameterMap();
        return ToolUtils.mapToParamString(baseTypeParameterMap);
    }

    /*获取方法中基础类型（primitive和string、date等以及这些类型数组）*/
    @NotNull
    public Map<String, Object> getBaseTypeParameterMap() {
        List<Parameter> parameterList = getParameterList();

        Map<String, Object> baseTypeParamMap = new LinkedHashMap<>();

        // 拼接参数
        for (Parameter parameter : parameterList) {
            //跳过标注 RequestBody 注解的参数
            if (parameter.isRequestBodyFound()) {
                continue;
            }

            // todo 判断类型
            // 8 PsiPrimitiveType
            // 8 boxed types; String,Date:PsiClassReferenceType == field.getType().getPresentableText()
            String shortTypeName = parameter.getShortTypeName();
            Object defaultValue = PsiClassHelper.getJavaBaseTypeDefaultValue(shortTypeName);
            //简单常用类型
            if (defaultValue != null) {
                baseTypeParamMap.put(parameter.getParamName(), (defaultValue));
                continue;
            }

            PsiClassHelper psiClassHelper = PsiClassHelper.create(Objects.requireNonNull(psiMethod.getContainingClass()));
            PsiClass psiClass = psiClassHelper.findOnePsiClassByClassName(parameter.getParamType(), getProject());

            if (psiClass != null) {
                PsiField[] fields = psiClass.getFields();
                for (PsiField field : fields) {
                    if ("serialVersionUID".equals(field.getName())) {
                        continue;
                    }
                    Object fieldDefaultValue = PsiClassHelper.getJavaBaseTypeDefaultValue(field.getType().getPresentableText());
                    if (fieldDefaultValue != null) {
                        baseTypeParamMap.put(field.getName(), fieldDefaultValue);
                    }
                }
            }

          /*  PsiClass psiClass2 = psiClassHelper.findOnePsiClassByClassName2(parameter.getParamType(), getProject());
            if (psiClass2 != null) {
                PsiField[] fields = psiClass2.getFields();
                for (PsiField field : fields) {
                    Object fieldDefaultValue  = PsiClassHelper.getJavaBaseTypeDefaultValue(field.getType().getPresentableText());
                    if(fieldDefaultValue != null)
                        baseTypeParamMap.put(field.getName(), fieldDefaultValue);
                }
            }*/
        }
        return baseTypeParamMap;
    }

    /* 基础类型默认值 */
    @Nullable
    public Map<String, Object> getJavaBaseTypeDefaultValue(String paramName, String paramType) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        Object paramValue = null;
        paramValue = PsiClassHelper.getJavaBaseTypeDefaultValue(paramType);
        if (paramValue != null) {
            paramMap.put(paramType, paramValue);
        }
        return paramMap;
    }

    @NotNull
    public List<Parameter> getParameterList() {
        List<Parameter> parameterList = new ArrayList<>();

        PsiParameterList psiParameterList = psiMethod.getParameterList();
        PsiParameter[] psiParameters = psiParameterList.getParameters();
        for (PsiParameter psiParameter : psiParameters) {
            //忽略 request response

            String paramType = psiParameter.getType().getCanonicalText();
            if (paramType.equals("javax.servlet.http.HttpServletRequest")
                    || paramType.equals("javax.servlet.http.HttpServletResponse"))
                continue;
            //必传参数 @RequestParam
            PsiModifierList modifierList = psiParameter.getModifierList();
            boolean requestBodyFound = modifierList.findAnnotation(REQUEST_BODY.getQualifiedName()) != null;
            // 没有 RequestParam 注解, 有注解使用注解value
            String paramName = psiParameter.getName();
            String requestName = null;


            PsiAnnotation headerAnno = modifierList.findAnnotation(SpringRequestHeaderAnnotations.REQUEST_HEADER.getQualifiedName());
            if (headerAnno != null) {
                continue;
            }

            PsiAnnotation pathVariableAnno = modifierList.findAnnotation(PATH_VARIABLE.getQualifiedName());
            if (pathVariableAnno != null) {
                requestName = getAnnotationValue(pathVariableAnno);
                Parameter parameter = new Parameter(paramType, requestName != null ? requestName : paramName).setRequired(true).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }

            PsiAnnotation requestParamAnno = modifierList.findAnnotation(REQUEST_PARAM.getQualifiedName());
            if (requestParamAnno != null) {
                requestName = getAnnotationValue(requestParamAnno);
                Parameter parameter = new Parameter(paramType, requestName != null ? requestName : paramName).setRequired(true).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }

            if (pathVariableAnno == null && requestParamAnno == null) {
                Parameter parameter = new Parameter(paramType, paramName).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }
        }
        return parameterList;
    }

    public List<Parameter> getHeaderMap() {
        List<Parameter> parameterList = new ArrayList<>();

        PsiParameterList psiParameterList = psiMethod.getParameterList();
        PsiParameter[] psiParameters = psiParameterList.getParameters();
        for (PsiParameter psiParameter : psiParameters) {
            //必传参数 @RequestParam
            PsiModifierList modifierList = psiParameter.getModifierList();

            PsiAnnotation headerAnno = modifierList.findAnnotation(SpringRequestHeaderAnnotations.REQUEST_HEADER.getQualifiedName());
            if (headerAnno == null) {
                continue;
            }
            String paramType = psiParameter.getType().getCanonicalText();
            String name = getAnnotationValue(headerAnno);
            Parameter parameter = new Parameter(paramType, name).setRequired(true);
            parameterList.add(parameter);
        }
        return parameterList;
    }

    public String getAnnotationValue(PsiAnnotation annotation) {
        String paramName = null;
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue(FieldConstant.VALUE_STRING);

        if (attributeValue != null && attributeValue instanceof PsiLiteralExpression) {
            return (String) ((PsiLiteralExpression) attributeValue).getValue();
        }

        attributeValue  = annotation.findDeclaredAttributeValue("name");
        if (attributeValue != null && attributeValue instanceof PsiLiteralExpression) {
            return (String) ((PsiLiteralExpression) attributeValue).getValue();
        }
        return paramName;
    }

    /**
     * 构建RequestBody json 参数
     *
     * @param parameter
     * @return
     */
    public String buildRequestBodyJson(Parameter parameter) {
//        JavaFullClassNameIndex.getInstance();

        Project project = psiMethod.getProject();
        final String className = parameter.getParamType();

        return PsiClassHelper.create(psiMethod.getContainingClass()).withModule(myModule).convertClassToJSON(className, project);
    }


    public String buildRequestBodyJson() {
        List<Parameter> parameterList = this.getParameterList();
        for (Parameter parameter : parameterList) {
            if (parameter.isRequestBodyFound()) {
                return buildRequestBodyJson(parameter);
            }
        }
        return null;
    }

    @NotNull
    public String buildServiceUriPath() {
        String ctrlPath = null;
        String methodPath = null;

        //判断rest服务提供方式 spring or jaxrs
        PsiClass containingClass = psiMethod.getContainingClass();
        RestSupportedAnnotationHelper annotationHelper;
        if (isSpringRestSupported(containingClass)) {
            ctrlPath = RequestMappingAnnotationHelper.getOneRequestMappingPath(containingClass);
            methodPath = RequestMappingAnnotationHelper.getOneRequestMappingPath(psiMethod);
        } else if (isJaxrsRestSupported(containingClass)) {
            ctrlPath = AnnotationHelper.getClassUriPath(containingClass);
            methodPath = AnnotationHelper.getMethodUriPath(psiMethod);
        }

        if (ctrlPath == null) {
            return null;
        }

        String pathSeparator = SymbolConstant.SLASH;
        if (!ctrlPath.startsWith(pathSeparator)) {
            ctrlPath = pathSeparator.concat(ctrlPath);
        }
        if (!ctrlPath.endsWith(pathSeparator)) {
            ctrlPath = ctrlPath.concat(pathSeparator);
        }
        if (methodPath == null) {
            methodPath = "";
        }
        if (methodPath.startsWith(pathSeparator)) {
            methodPath = methodPath.substring(1);
        }

        return ctrlPath + methodPath;
    }

    @NotNull
    public String buildServiceUriPathWithParams() {
        String serviceUriPath = buildServiceUriPath();

        String params = PsiMethodHelper.create(psiMethod).buildParamString();
        // RequestMapping 注解设置了 param
        if (!params.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(serviceUriPath);
            return urlBuilder.append(serviceUriPath.contains(SymbolConstant.INTERROGATION_MARK) ? "&" : SymbolConstant.INTERROGATION_MARK).append(params).toString();
        }
        return serviceUriPath;
    }

    //包含 "RestController" "Controller"
    public static boolean isSpringRestSupported(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        /*return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;*/

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null;
    }

    //包含 "RestController" "Controller"
    public static boolean isJaxrsRestSupported(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        return modifierList.findAnnotation(JAX_RSRequestAnnotation.PATH.getQualifiedName()) != null;
    }


    /* 生成完整 URL , 附带参数 */
    @NotNull
    public String buildFullUrlWithParams() {

        String fullUrl = buildFullUrl();

        String params = buildParamString();

        // RequestMapping 注解设置了 param
        if (!params.isEmpty()) {
            return fullUrl + (fullUrl.contains(SymbolConstant.INTERROGATION_MARK) ? "&" : SymbolConstant.INTERROGATION_MARK) + params;
        }
        return fullUrl;
    }

    @NotNull
    public String buildFullUrl() {

        String hostUri = myModule != null ? ModuleHelper.create(myModule).getServiceHostPrefix() : ModuleHelper.DEFAULT_URI;

        String servicePath = buildServiceUriPath();

        return hostUri.concat(servicePath);
    }

}
