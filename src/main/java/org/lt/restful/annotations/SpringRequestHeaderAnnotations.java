package org.lt.restful.annotations;


public enum SpringRequestHeaderAnnotations {
    REQUEST_HEADER("RequestHeader", "org.springframework.web.bind.annotation.RequestHeader"),
    ;

    private final String shortName;
    private final String qualifiedName;

    SpringRequestHeaderAnnotations(String shortName, String qualifiedName) {
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