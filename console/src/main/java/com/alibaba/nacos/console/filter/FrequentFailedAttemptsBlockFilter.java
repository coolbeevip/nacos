package com.alibaba.nacos.console.filter;

import com.alibaba.nacos.client.utils.LogUtils;
import org.slf4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class FrequentFailedAttemptsBlockFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LogUtils.logger(FrequentFailedAttemptsBlockFilter.class);
    private static final ConcurrentMap<String, Integer> ipAccessCount = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Long> ipBlockTime = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 10;
    private static final long BLOCK_DURATION = TimeUnit.MINUTES.toMillis(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        if (isBlocked(clientIp)) {
            LOGGER.warn("Blocked access {} from {}", request.getContextPath(), clientIp);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED || response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            recordFailedAttempt(clientIp);
        }
    }

    private boolean isBlocked(String clientIp) {
        Long blockTime = ipBlockTime.get(clientIp);
        if (blockTime == null) {
            return false;
        }
        if (System.currentTimeMillis() - blockTime > BLOCK_DURATION) {
            ipBlockTime.remove(clientIp);
            ipAccessCount.remove(clientIp);
            return false;
        }
        return true;
    }

    private void recordFailedAttempt(String clientIp) {
        ipAccessCount.merge(clientIp, 1, Integer::sum);
        if (ipAccessCount.get(clientIp) >= MAX_ATTEMPTS) {
            ipBlockTime.put(clientIp, System.currentTimeMillis());
        }
    }
}