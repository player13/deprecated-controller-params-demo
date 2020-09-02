package com.example.deprecatedcontrollerparamsdemo.filter;

import com.example.deprecatedcontrollerparamsdemo.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Order(1)
@Component
public class PathVariablesServletFilter implements Filter {

    @Value("${app.version}")
    private String version;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // TODO: 02.09.2020 in production code use FilterRegistrationBean instead of matching request URI
        if (httpRequest.getRequestURI().contains("path-variables/fourth-deprecated-method")) {
            Utils.checkVersion(version);
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getRequestURI() {
                    return super.getRequestURI().replaceAll("deprecated", "actual");
                }
            };
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}