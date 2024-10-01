package ru.tecon.admTools.utils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Что бы пропустить CORS запрос.
 * <h4>Происходит промежуточный запрос:
 * <ul>
 * <li>Access-Control-Request-Method: GET
 * <li>Access-Control-Request-Headers: content-type,x-requested-with
 * <li>Origin: https://foo.bar.org
 * </ul>
 *
 * <h4>На него надо ответить:
 * <ul>
 * <li>Content-Length: 0
 * <li>Connection: keep-alive
 * <li>Access-Control-Allow-Origin: https://foo.bar.org
 * <li>Access-Control-Allow-Methods: POST, GET, OPTIONS, DELETE
 * <li>Access-Control-Allow-Headers: Content-Type, x-requested-with
 * <li>Access-Control-Max-Age: 86400
 * </ul>
 *
 * Если клиент будет читать какие-то заголовки, то их надо прописать в Access-Control-Expose-Headers
 *
 * @author Maksim Shchelkonogov
 * 30.09.2024
 */
@WebFilter(
        urlPatterns = {
                "/linker/report",
                "/specificModel/report/modeMap",
                "/specificModel/report/modeControl",
                "/specificModel/report/technicalLimitsChangeReport",
                "/ecoSpecificModel/report/technicalLimitsChangeReport"
        }
)
public class CorsFilter implements Filter {

    public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    public static final int MAX_AGE = 24 * 60 * 60;
    public static final String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
    public static final String DEFAULT_EXPOSED_HEADERS = "location,info,Content-Disposition";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", getRequestedAllowedHeaders(req));
        res.setHeader("Access-Control-Expose-Headers", getRequestedExposedHeaders(req));
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        res.setIntHeader("Access-Control-Max-Age", MAX_AGE);
        res.setHeader("x-responded-by", "cors-response-filter");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    String getRequestedAllowedHeaders(HttpServletRequest response) {
        List<String> headers = Collections.list(response.getHeaders("Access-Control-Request-Headers"));
        return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
    }

    String getRequestedExposedHeaders(HttpServletRequest response) {
        List<String> headers = Collections.list(response.getHeaders("Access-Control-Expose-Headers"));
        return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
    }

    String createHeaderList(List<String> headers, String defaultHeaders) {
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders;
        }
        StringBuilder retVal = new StringBuilder();
        for (String s: headers) {
            retVal.append(s);
            retVal.append(',');
        }
        retVal.append(defaultHeaders);
        return retVal.toString();
    }
}
