package org.lt.restful.method;

import org.lt.restful.constants.SymbolConstant;

public class RequestPath {
    String path;
    String method;

    public RequestPath(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void concat(RequestPath classRequestPath) {
        String classUri = classRequestPath.getPath();
        String methodUri = this.path;
        //TODO
        String slash = SymbolConstant.SLASH;
        if (!classUri.startsWith(slash)) classUri = slash.concat(classUri);
        if (!classUri.endsWith(slash)) classUri = classUri.concat(slash);
        if (this.path.startsWith(slash)) methodUri = this.path.substring(1);

        this.path = classUri.concat(methodUri) ;
    }
}
