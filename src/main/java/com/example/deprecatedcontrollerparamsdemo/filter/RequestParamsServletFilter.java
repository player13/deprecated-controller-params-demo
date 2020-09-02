package com.example.deprecatedcontrollerparamsdemo.filter;

import com.example.deprecatedcontrollerparamsdemo.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

@Order(2)
@Component
public class RequestParamsServletFilter implements Filter {

    @Value("${app.version}")
    private String version;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // TODO: 02.09.2020 in production code use FilterRegistrationBean instead of matching request URI
        if (httpRequest.getRequestURI().contains("request-params/third-method") && hasDeprecatedParams(httpRequest.getParameterNames())) {
            Utils.checkVersion(version);
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getParameter(String name) {
                    String param = super.getParameter(name);
                    if ((param == null || param.isEmpty()) && name.contains("actual")) {
                        String deprecatedParam = super.getParameter(name.replaceAll("actual", "deprecated"));
                        if (deprecatedParam != null && !deprecatedParam.isEmpty()) {
                            return deprecatedParam;
                        }
                    }
                    return param;
                }

                @Override
                public Map<String, String[]> getParameterMap() {
                    return super.getParameterMap().entrySet().stream().map(entry -> {
                        if (entry.getKey().contains("deprecated")) {
                            return new Map.Entry<String, String[]>() {
                                private String[] value = entry.getValue();

                                @Override
                                public String getKey() {
                                    return entry.getKey().replaceAll("deprecated", "actual");
                                }

                                @Override
                                public String[] getValue() {
                                    return value;
                                }

                                @Override
                                public String[] setValue(String[] value) {
                                    String[] oldValue = this.value;
                                    this.value = value;
                                    return oldValue;
                                }
                            };
                        }
                        return entry;
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }

                @Override
                public Enumeration<String> getParameterNames() {
                    return Collections.enumeration(super.getParameterMap().keySet().stream()
                            .map(name -> name.replaceAll("deprecated", "actual"))
                            .collect(Collectors.toSet()));
                }

                @Override
                public String[] getParameterValues(String name) {
                    String[] paramValues = super.getParameterValues(name);
                    if ((paramValues == null || paramValues.length == 0) && name.contains("actual")) {
                        String[] deprecatedParamValues = super.getParameterValues(name.replaceAll("actual", "deprecated"));
                        if (deprecatedParamValues != null && deprecatedParamValues.length > 0) {
                            return deprecatedParamValues;
                        }
                    }
                    return paramValues;
                }
            };
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean hasDeprecatedParams(Enumeration<String> parameterNames) {
        while (parameterNames.hasMoreElements()) {
            if (parameterNames.nextElement().contains("deprecated")) {
                return true;
            }
        }
        return false;
    }
}