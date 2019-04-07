package com.xmessenger.controllers.webservices.authenticated.resftul.chatter.filters;

import com.xmessenger.controllers.security.user.ContextUserRetriever;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.chatter.ChatterFlowExecutor;
import com.xmessenger.model.services.chatter.decorators.Chat;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ChatterValidationFilter implements Filter {
    private final ContextUserRetriever contextUserRetriever;
    private final ChatterFlowExecutor flowExecutor;

    @Autowired
    public ChatterValidationFilter(ContextUserRetriever contextUserRetriever, ChatterFlowExecutor flowExecutor) {
        this.contextUserRetriever = contextUserRetriever;
        this.flowExecutor = flowExecutor;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Integer chatId = this.getChatIdFromRequestUri(request.getRequestURI());
        AppUser user = this.contextUserRetriever.getContextUser();
        if (chatId == null || this.flowExecutor.hasAuthorityToOperateWithChat(user, new Chat(chatId))) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter writer = response.getWriter();
            writer.println("Cross-relation operation type detected!");
        }
    }

    @Override
    public void destroy() {
    }

    private Integer getChatIdFromRequestUri(String requestUri) {
        Integer chatId = null;
        for (String part : requestUri.split("/")) {
            if (Utility.isNumeric(part)) {
                chatId = Integer.valueOf(part);
                break;
            }
        }
        return chatId;
    }
}
