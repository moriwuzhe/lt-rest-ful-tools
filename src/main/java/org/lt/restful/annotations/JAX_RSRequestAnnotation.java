package org.lt.restful.annotations;


public enum JAX_RSRequestAnnotation {

    PATH("Path", "javax.ws.rs.Path", null),
    ;

    private final String shortName;
    private final String qualifiedName;

    JAX_RSRequestAnnotation(String shortName, String qualifiedName, String methodName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return shortName;
    }

}