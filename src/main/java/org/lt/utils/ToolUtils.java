package org.lt.utils;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.DisposeAwareRunnable;
import org.lt.restful.constants.SymbolConstant;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ToolUtils extends StringUtils {

    public static final String EMPTY_STRING = StringUtils.EMPTY;

    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    public static <K, V> SortedMap<K, V> emptySortedMap() {
        return Collections.emptySortedMap();
    }

    public static void smartInvokeLater(Project project, Runnable runnable) {
        DumbService.getInstance(project).smartInvokeLater(runnable);
    }

    public static void runWriteAction(@NotNull Runnable action) {
        ApplicationManager.getApplication().runWriteAction(action);
    }

    public static void runWhenSmart(Project project, Runnable r) {
        if (DumbService.isDumbAware(r)) {
            r.run();
        } else {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        }
    }

    public static void invokeLater(Project p, ModalityState state, Runnable r) {
        if (isNoBackgroundMode()) {
            r.run();
        } else {
            ApplicationManager.getApplication().invokeLater(DisposeAwareRunnable.create(r, p), state);
        }
    }

    public static boolean isNoBackgroundMode() {
        return (ApplicationManager.getApplication().isUnitTestMode()
                || ApplicationManager.getApplication().isHeadlessEnvironment());
    }


    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }


    public static String formatHtmlImage(URL url) {
        return "<img src=\"" + url + "\"> ";
    }


    public static PsiClass findPsiClass(String qualifiedName, Module module, Project project) {
        GlobalSearchScope scope = module == null ? GlobalSearchScope.projectScope(project) :
                GlobalSearchScope.moduleWithDependenciesScope(module);
        return JavaPsiFacade.getInstance(project).findClass(qualifiedName, scope);
    }

    public static PsiPackage getContainingPackage(@NotNull PsiClass psiClass) {
        PsiDirectory directory = psiClass.getContainingFile().getContainingDirectory();
        return directory == null ? null : JavaDirectoryService.getInstance().getPackage(directory);
    }

    @NotNull
    public static String removeRedundancyMarkup(String pattern) {
        String localhostRegex = "(http(s?)://)?(localhost)(:\\d+)?";
        String hostAndPortRegex = "(http(s?)://)?" +
                "( " +
                "([a-zA-Z0-9]([a-zA-Z0-9\\\\-]{0,61}[a-zA-Z0-9])?\\\\.)+[a-zA-Z]{2,6} |" +  // domain
                "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)" + // ip address
                ")";

        String localhost = "localhost";
        if (pattern.contains(localhost)) {
            pattern = pattern.replaceFirst(localhostRegex, "");
        }
        // quick test if reg exp should be used
        if (pattern.contains("http:") || pattern.contains("https:")) {
            pattern = pattern.replaceFirst(hostAndPortRegex, "");
        }

        //TODO : resolve RequestMapping(params="method=someMethod")
        if (!pattern.contains(SymbolConstant.INTERROGATION_MARK)) {
            return pattern;
        }

        return pattern.substring(0, pattern.indexOf(SymbolConstant.INTERROGATION_MARK));
    }


    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor
     * of length 0.
     * <p>Note: this method returns {@code true} for a {@code CharSequence}
     * that purely consists of whitespace.
     * <p><pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
     * @see #hasText(String)
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Copy the given {@code Collection} into a {@code String} array.
     * <p>The {@code Collection} must contain {@code String} elements only.
     *
     * @param collection the {@code Collection} to copy
     * @return the {@code String} array ({@code null} if the supplied
     * {@code Collection} was {@code null})
     */
    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }

        return collection.toArray(new String[collection.size()]);
    }

    /**
     * Tokenize the given {@code String} into a {@code String} array via a
     * {@link StringTokenizer}.
     * <p>The given {@code delimiters} string can consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using {@link #delimitedListToStringArray}.
     *
     * @param str               the {@code String} to tokenize
     * @param delimiters        the delimiter characters, assembled as a {@code String}
     *                          (each of the characters is individually considered as a delimiter)
     * @param trimTokens        trim the tokens via {@link String#trim()}
     * @param ignoreEmptyTokens omit empty tokens from the result array
     *                          (only applies to tokens that are empty after trimming; StringTokenizer
     *                          will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens ({@code null} if the input {@code String}
     * was {@code null})
     * @see StringTokenizer
     * @see String#trim()
     * @see #delimitedListToStringArray
     */
    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static Map<String, String> textToParamMap(String text) {
        if (text == null) {
            return Collections.emptyMap();
        }

        Map<String, String> paramMap = new HashMap<>();
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (!line.startsWith("//") && line.contains(SymbolConstant.COLON)) {

                String[] prop = line.split(SymbolConstant.COLON);

                if (prop.length > 1) {
                    String key = prop[0].trim();
                    String value = prop[1].trim();
                    paramMap.put(key, value);
                }
            }
        }
        return paramMap;
    }

    public static String mapToParamString(Map<String, Object> baseTypeParamMap) {
        return mapToParamString(baseTypeParamMap, true);
    }

    public static String mapToParamString(Map<String, Object> baseTypeParamMap, boolean encode) {
        StringJoiner joiner = new StringJoiner("&");
        baseTypeParamMap.entrySet().stream()
                .map(entry -> new StringBuilder().append(entry.getKey()).append("=").append(encode(entry.getValue(), encode)))
                .forEach(joiner::add);
        return joiner.toString();
    }

    public static String toString(Object o) {
        if (o instanceof String) {
            return (String) o;
        }
        return JsonUtils.toString(o);
    }

    private static String encode(Object obj, boolean encode) {
        String str = toString(obj);
        if (encode) {
            return urlDecode(str);
        }
        return str;
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
        return part;
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String part) {

        try {
            return URLDecoder.decode(part, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
        return part;
    }

}
