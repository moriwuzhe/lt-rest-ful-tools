package org.lt.restful.method;

import java.util.Map;

/**
 * 方法参数
 */
public class Parameter {
    private String paramType;
    private String paramName;

    private Map<String, String> requestHeader;
    private String defaultValue = null;
    private boolean required = false;
    private boolean requestBodyFound = false;

    public Parameter() { }

    public Parameter(String paramType, String paramName) {
        this.paramType = paramType;
        this.paramName = paramName;
    }

    public Parameter(String paramType, String paramName, String defaultValue) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.defaultValue = defaultValue;
    }

    public String getParamType() {
        return paramType;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public Parameter setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isRequestBodyFound() {
        return requestBodyFound;
    }

    public void setRequestBodyFound(boolean requestBodyFound) {
        this.requestBodyFound = requestBodyFound;
    }

    public Parameter requestBodyFound(boolean requestBodyFound) {
        this.requestBodyFound = requestBodyFound;
        return this;
    }

    public String getShortTypeName() {
    //todo : List
        return paramType.substring(paramType.lastIndexOf(".") + 1);
    }


}
