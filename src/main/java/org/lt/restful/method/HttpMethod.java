package org.lt.restful.method;

import org.lt.utils.ToolUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT;

    private static final Map<String, HttpMethod> methodMap =
            Arrays.stream(values()).collect(Collectors.toMap(HttpMethod::name, i -> i));

    public static HttpMethod getByRequestMethod(String method) {
        if (ToolUtils.isEmpty(method)) {
            return null;
        }

        String[] split = method.split("\\.");

        if (split.length > 1) {
            method = split[split.length - 1].toUpperCase();
            return HttpMethod.valueOf(method);
        }

        return HttpMethod.valueOf(method.toUpperCase());
    }

}

