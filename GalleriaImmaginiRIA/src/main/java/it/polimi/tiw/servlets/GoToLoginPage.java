package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
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
    	String htmlPath = "/WEB-INF/login_page.jsp"; // using HTML extension causes a loop for some damn reason!
		
		//Checking whether the user is already logged in, in that case send them to the home page
		HttpSession session = request.getSession(false);
        
        if(!(session == null || session.isNew() || session.getAttribute("username") == null)){
        	response.sendRedirect(getServletContext().getContextPath() + "/Home");
            return;
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(htmlPath);
        requestDispatcher.include(request, response);
    }

    @Override
    public void destroy() {
    }


}