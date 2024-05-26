package org.lt.restful.annotations;


import java.util.Arrays;

public enum SpringRequestMethodAnnotation {

    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", null),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", "PATCH"),
    ;
    private final String qualifiedName;
    private final String methodName;

    SpringRequestMethodAnnotation(String qualifiedName, String methodName) {
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

    public static SpringRequestMethodAnnotation getByQualifiedName(String qualifiedName) {
        return Arrays.stream(SpringRequestMethodAnnotation.values())
                .filter(springRequestAnnotation -> springRequestAnnotation.getQualifiedName().equals(qualifiedName))
                .findFirst().orElse(null);
    }

    public static SpringRequestMethodAnnotation getByShortName(String requestMapping) {
        return Arrays.stream(SpringRequestMethodAnnotation.values())
                .filter(springRequestAnnotation -> springRequestAnnotation.getQualifiedName().endsWith(requestMapping))
                .findFirst().orElse(null);
    }
}