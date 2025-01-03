package com.app.librarymgtsystem.middleware;

import com.app.librarymgtsystem.services.MemberServiceImpl;
import jakarta.servlet.*;
import java.io.IOException;

public class ThreadLocalCleanupFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            MemberServiceImpl.LoggedInUserContext.clear();
        }
    }
}
