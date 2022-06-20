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

public class Logout extends HttpServlet{
   
	private Connection connection;
	private static final long serialVersionUID = 1L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Clear the user session and go to the login page
		HttpSession session = request.getSession(false);
		String path = getServletContext().getContextPath();
		if(session != null) {
			session.invalidate();
		}
		
		response.sendRedirect(path + "/");
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	public void destroy() {
		try {
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
			
		}
	}
	
}
