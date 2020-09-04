package com.example.deprecatedcontrollerparamsdemo.interceptor;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Component
public class DeprecatedParamsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest) request);
    }
}
