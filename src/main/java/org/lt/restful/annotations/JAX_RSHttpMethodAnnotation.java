package org.lt.restful.annotations;


public enum JAX_RSHttpMethodAnnotation {
    GET("javax.ws.rs.GET", "GET"),
    POST("javax.ws.rs.POST", "POST"),
    PUT("javax.ws.rs.PUT", "PUT"),
    DELETE("javax.ws.rs.DELETE", "DELETE"),
    HEAD("javax.ws.rs.HEAD", "HEAD"),
    PATCH("javax.ws.rs.PATCH", "PATCH"),
    ;

    private final String qualifiedName;
    private final String methodName;

    JAX_RSHttpMethodAnnotation(String qualifiedName, String methodName) {
        this.qualifiedName = qualifiedName;
        this.methodName = methodName;
    }

    public String methodName() {
        return this.methodName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") - 1);
    }

}