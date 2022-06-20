package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.UserDAO;

//@WebServlet("/LoginCheck")
public class LoginCheck extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Connection connection;

    @Override
    public void init() throws ServletException {
    	
        final String DB_URL = getServletContext().getInitParameter("dbUrl");
		final String USER = getServletContext().getInitParameter("dbUser");
		final String PASS = getServletContext().getInitParameter("dbPasswordGreg");
//		final String PASS = getServletContext().getInitParameter("dbPasswordDani");
		final String DRIVER_STRING = getServletContext().getInitParameter("dbDriver");
		
	
		try {
			Class.forName(DRIVER_STRING);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		}
		catch (ClassNotFoundException e){
			throw new UnavailableException("Can't load db driver");
		}
		catch (SQLException e) {
			throw new UnavailableException("Can't connect to database");
		}
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO(connection);
        String path = getServletContext().getContextPath();
        
        //final WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        
        if(username == null || password == null ||
           username.isEmpty() || password.isEmpty() || 
     	   username.isBlank() || password.isBlank() ){
        
        	response.sendRedirect(path + "/?errorId=5"); //Send with error status = 5 (null/invalid inputs (login))
            return;
		}

        try {
			if(userDAO.checkCredentials(username, password) == null) {
				// User is present
				
				response.sendRedirect(path + "/?errorId=6"); //Send with error status = 6 (incorrect credentials)
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database checking user credentials");
			return;
		}

        
        // Add session creation here
		HttpSession session = request.getSession(true);
		//It should always be new, since the session is just now starting after sign in
		if(session.isNew()){
			request.getSession().setAttribute("username", username);
		}

        response.sendRedirect(path + "/Home");
    }

    @Override
    public void destroy() {
        try {
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
			
		}
    }


}