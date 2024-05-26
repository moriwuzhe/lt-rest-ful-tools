package org.lt.restful.common;

import org.lt.restful.constants.SymbolConstant;
import org.lt.utils.JsonUtils;
import org.lt.utils.ToolUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class RequestHelper {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public static String request(String url, String method, Map<String, String> params, Map<String, String> headers, String body) {
        if (method == null) {
            return "method is null";
        }

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            url = completed(url);
            HttpRequestBase httpMethod = getHttpMethod(url, method, body, params);
            Optional.of(headers).ifPresent(map -> map.forEach(httpMethod::setHeader));
            response = httpClient.execute(httpMethod);
            return toString(response);
        } catch (IOException e) {
            return "There was an error accessing to URL: " + url + "," + e.getMessage();
        } finally {
            release(response, httpClient);
        }
    }

    private static HttpRequestBase getHttpMethod(String url, String method, String body, Map<String, String> params) throws IOException {
        switch (method.toUpperCase()) {
            case "GET":
                return get(url, params);
            case "POST":
                return post(url, body, params);
            case "PUT":
                return put(url, body, params);
            case "DELETE":
                return delete(url);
            case "OPTIONS":
                return options(url);
            case "HEAD":
                return head(url);
            default:
        }
        throw new IOException("not supported method : " + method + ".");
    }

    private static HttpRequestBase head(String url) {
        return null;
    }

    private static HttpRequestBase options(String url) {
        return null;
    }

    public static HttpGet get(String url, Map<String, String> params) throws IOException {
        if (ToolUtils.isNotEmpty(params)) {
            url += SymbolConstant.INTERROGATION_MARK + params.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining("&"));
        }
        return new HttpGet(url);
    }

    private static HttpRequestBase post(String url, String body, Map<String, String> params) throws IOException {
        if (ToolUtils.isEmpty(body)) {
            return postUrlEncodedForm(url, params);
        }

        HttpPost postMethod = new HttpPost(url);
        postMethod.setEntity(new StringEntity(body));
        return postMethod;
    }

    public static HttpPost postUrlEncodedForm(String url, Map<String, String> params) throws IOException {
        if (ToolUtils.isEmpty(params)) {
            return new HttpPost(url);
        }
        List<BasicNameValuePair> nameValuePairs =
                params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs);
        httpPost.setEntity(httpEntity);
        return httpPost;
    }


    public static HttpPut put(String url, String body, Map<String, String> params) throws IOException {
        if (ToolUtils.isEmpty(body)) {
            return putUrlEncodedForm(url, params);
        }

        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(body));
        return httpPut;
    }


    public static HttpPut putUrlEncodedForm(String url, Map<String, String> params) throws IOException {
        if (ToolUtils.isEmpty(params)) {
            return new HttpPut(url);
        }
        List<BasicNameValuePair> nameValuePairs =
                params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        HttpPut httpPut = new HttpPut(url);
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs);
        httpPut.setEntity(httpEntity);
        return httpPut;
    }

    public static HttpDelete delete(String url) {
        return new HttpDelete(url);
    }


    public static String post(String url, String json) {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost postMethod = new HttpPost(completed(url));

            StringEntity httpEntity = new StringEntity(json);

            httpEntity.setContentType("application/json");
            httpEntity.setContentEncoding("UTF-8");
            postMethod.addHeader("Content-type", "application/json; charset=utf-8");
            postMethod.setHeader("Accept", "application/json");

            postMethod.setEntity(httpEntity);                                          //设置post请求实体

            return toString(httpClient.execute(postMethod));
        } catch (IOException e) {
            return "There was an error accessing to URL: " + url + "," + e.getMessage();
        }
    }

    private static void release(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException ignored) {
            }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static String completed(String url) {
        assert url != null;

        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = HTTP + url;
        }
        return url;
    }

    private static String toString(CloseableHttpResponse response) {
        if (response == null) {
            return ToolUtils.EMPTY_STRING;
        }
        HttpEntity entity = response.getEntity();
        try {
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if (result != null && JsonUtils.isValidJson(result)) {
                return JsonUtils.format(result);
            }
        } catch (IOException e) {
            return "Exception in parsing result set:" + e.getMessage();
        }

        return ToolUtils.EMPTY_STRING;
    }


}
