package com.alibaba.nacos.console.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ConsoleUIBlockFilter extends OncePerRequestFilter {
    private final boolean consoleUiEnabled;

    public ConsoleUIBlockFilter(boolean consoleUiEnabled) {
        this.consoleUiEnabled = consoleUiEnabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!consoleUiEnabled && (uri.equals("/nacos/") || uri.startsWith("/nacos/console-ui/"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied, console ui is disabled");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
