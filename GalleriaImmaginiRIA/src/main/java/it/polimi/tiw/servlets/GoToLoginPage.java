package it.polimi.tiw.servlets;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@WebServlet("/")
public class GoToLoginPage extends HttpServlet {

	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String htmlPath = "/WEB-INF/login_page.html";
		ServletContext servletContext = getServletContext();
		
		//Checking whether the user is already logged in, in that case send them to the home page
		HttpSession session = request.getSession(false);
        
        if(!(session == null || session.isNew() || session.getAttribute("username") == null)){
        	response.sendRedirect(getServletContext().getContextPath() + "/Home");
            return;
        }

        // TODO: send login_page to user
		
    }

    @Override
    public void destroy() {
    }


}