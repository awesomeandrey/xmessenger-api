package com.xmessenger.controllers.webservices.secured.resftul.chatter.filters;

import com.xmessenger.controllers.security.user.details.ContextUserRetriever;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.chatter.ChattingService;
import com.xmessenger.model.services.chatter.decorators.Chat;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ChatterValidationFilter implements Filter {
    private final ContextUserRetriever contextUserRetriever;
    private final ChattingService chattingService;

    @Autowired
    public ChatterValidationFilter(ContextUserRetriever contextUserRetriever, ChattingService chattingService) {
        this.contextUserRetriever = contextUserRetriever;
        this.chattingService = chattingService;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Integer chatId = this.getChatIdFromRequestUri(request.getRequestURI());
        AppUser user = this.contextUserRetriever.getContextUser();
        if (chatId == null || this.chattingService.hasAuthorityToOperateWithChat(user, new Chat(chatId))) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cross-relation operation type detected!");
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
