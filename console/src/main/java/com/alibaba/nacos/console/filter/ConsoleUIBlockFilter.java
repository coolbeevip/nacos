package com.alibaba.nacos.console.filter;

import com.alibaba.nacos.core.cluster.Member;
import com.alibaba.nacos.core.cluster.ServerMemberManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class ConsoleUIBlockFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(ConsoleUIBlockFilter.class);
    private boolean consoleUiEnabled = true;
    private final ServerMemberManager memberManager;

    public ConsoleUIBlockFilter(ServerMemberManager memberManager) {
        this.memberManager = memberManager;
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

    @Scheduled(fixedRate = 60000)
    public void checkUIStatus() {
        File file = new File("UI_OFF.txt");
        boolean disable_flag = file.exists();
        if (disable_flag) {
            if (this.consoleUiEnabled) {
                log.info("Console UI is off");
            }
            this.consoleUiEnabled = false;
        } else {
            if (!this.consoleUiEnabled) {
                log.info("Console UI is on");
            }
            this.consoleUiEnabled = true;
        }

        log.info("====== 节点状态报告 ======");
        for (Member member : memberManager.allMembers()) {
            log.info("member: {} state: {}", member.getAddress(), member.getState().name());
        }
    }
}
