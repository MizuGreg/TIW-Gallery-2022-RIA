package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.utility.ConnectionUtility;

public class Logout extends HttpServlet{
   
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() {}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Clear the user session and go to the login page
		HttpSession session = request.getSession(false);
		String path = getServletContext().getContextPath();
		if(session != null) {
			session.invalidate();
		}
		
		response.sendRedirect(path + "/");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	public void destroy() {
		
	}
	
}
