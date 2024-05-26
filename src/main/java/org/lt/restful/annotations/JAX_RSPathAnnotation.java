package org.lt.restful.annotations;


public enum JAX_RSPathAnnotation {

    PATH("Path", "javax.ws.rs.Path"),
    ;

    private final String shortName;
    private final String qualifiedName;

    JAX_RSPathAnnotation(String shortName, String qualifiedName) {
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