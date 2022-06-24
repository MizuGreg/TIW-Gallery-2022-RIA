package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/SignupCheck")
public class SignupCheck extends HttpServlet {


	private Connection connection;
	private static final long serialVersionUID = 1L;

	/**
	 * Opens the database connection for this servlet
	 */
	public void init() throws ServletException{
		
		connection = ConnectionUtility.getConnection(getServletContext());
		
		// String s1 = getInitParameter("parameter exclusive to the servlet")
		// String s1 = getServletContext().getInitParameter("parameter of the servlet container")
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeatPassword = request.getParameter("repeatPassword");
		
		UserDAO userDAO = new UserDAO(connection);
		String path = getServletContext().getContextPath();
		String errorMessage = null;
		
		//Check validity of inputs
		if(!(CheckerUtility.checkAvailability(username) ||
			 CheckerUtility.checkAvailability(email) ||
			 CheckerUtility.checkAvailability(password) ||
			 CheckerUtility.checkAvailability(repeatPassword))){
			errorMessage = "Invalid inputs received"; // btw this should never happen cause the client's javascript shouldn't allow it
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		//Check validity email-format
		if(errorMessage == null) {
			
			// Simplified email Regex Format (excludes any special character
			//beside . (dot), doesn't cover all possible emails):
			// ([A-z]|[0-9])+(\.([A-z]|[0-9])+)*@([A-z]|[0-9])+\.([A-z]|[0-9])+
		
			String patternString = "([A-z]|[0-9])+(\\.([A-z]|[0-9])+)*@([A-z]|[0-9])+\\.([A-z]|[0-9])+";
			
			if(!email.matches(patternString)) {
				//Match not found, returning error
				errorMessage = "Bad email format";
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		
		//Check username not already in use
		if(errorMessage == null) {
			try {
				if(userDAO.getUserFromUsername(username) != null) {
					// Username is already present
					errorMessage = "Username already taken";
		        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} catch (SQLException e) {
				errorMessage = "Failure in database checking username";
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
		}
		
		//Check matching passwords
		if(errorMessage == null) {
			if(!password.equals(repeatPassword)) {
				errorMessage = "Passwords not matching";
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		
		// Create the user and add it to the database
		try {
			userDAO.createUser(username, email, password);
		} catch (SQLException e) {
			errorMessage = "Failure in database user creation";
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
		}
		
		//If an error occurred, send it as a json message
		if(errorMessage != null) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
        	String errorJson = gson.toJson(errorMessage);
        	response.setContentType("application/json");
    		response.setCharacterEncoding("UTF-8");
    		// Write JSON on the response
    		response.getWriter().write(errorJson);
		}
		else {
			// Add session creation here
			HttpSession session = request.getSession(true);
			//It should always be new, since the session is just now starting after sign up
			if(session.isNew()){
				request.getSession().setAttribute("username", username);
			}
			response.sendRedirect(path + "/Home"); 
		}
		// We don't want a refreshing issue, so we redirect
				
	}
	
	public void destroy() {
		//Closes the database connection for this servlet
		try {
			ConnectionUtility.closeConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
