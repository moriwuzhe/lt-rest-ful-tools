package org.lt.restful.method.action;


import com.intellij.openapi.module.Module;
import org.lt.restful.constants.SymbolConstant;
import org.lt.utils.ToolUtils;
import org.jetbrains.annotations.NotNull;

//PathAndQuery  AbsolutePath AbsoluteUri Query
public class ModuleHelper {
    Module module;

    // URL
    private static final String SCHEME = "http://"; //PROTOCOL
    private static final String HOST = "localhost";
    private static final String PORT = "8080"; // int
    public static String DEFAULT_URI = "http://localhost"+ SymbolConstant.COLON+PORT;
//    private static final String PATH = "http://localhost"+SymbolConstant.COLON+PORT; // PATH or FILE

    public static String getAUTHORITY() {
        return null;
    }

    PropertiesHandler propertiesHandler;

    public ModuleHelper(Module module) {
        this.module = module;
        propertiesHandler = new PropertiesHandler(module);
    }


    public static ModuleHelper create(Module module) {
        return new ModuleHelper(module);
    }

    @NotNull
    public String getServiceHostPrefix() {
        if (module == null) {
            return DEFAULT_URI;
        }

        String port = propertiesHandler.getServerPort();
        if (ToolUtils.isEmpty(port)) port = PORT;

        String contextPath = propertiesHandler.getContextPath();
        return new StringBuilder(SCHEME).append(HOST).append(SymbolConstant.COLON).append(port).append(contextPath).toString();
    }

}
