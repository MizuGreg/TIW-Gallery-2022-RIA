package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoggedFilter extends HttpFilter {
	
	private static final long serialVersionUID = 1L;
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession(false);
        
        if(session == null || session.isNew() || session.getAttribute("username") == null){
            response.sendRedirect(getServletContext().getContextPath() + "/");
            return;
        }

        chain.doFilter(request, response);
    }

}
