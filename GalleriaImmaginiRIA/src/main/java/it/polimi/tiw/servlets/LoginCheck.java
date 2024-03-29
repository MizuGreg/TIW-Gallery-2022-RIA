package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/LoginCheck")
@MultipartConfig
public class LoginCheck extends HttpServlet {

	private Connection connection;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    	
        connection = ConnectionUtility.getConnection(getServletContext());
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("loginPassword");

        UserDAO userDAO = new UserDAO(connection);
        String errorMessage = null;
        
        if(!(CheckerUtility.checkAvailability(username) || CheckerUtility.checkAvailability(password))) {
        	errorMessage = "Invalid inputs received"; // btw this should never happen cause the client's javascript shouldn't allow it
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        if(errorMessage == null) {
	        try {
				if(userDAO.checkCredentials(username, password) == null) {
					// User is NOT present
					errorMessage = "Wrong credentials";
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} catch (SQLException e) {
				errorMessage = "Failure in database checking user credentials";
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
        }
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
        HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
        String jsonResponse;
        
        //If an error was found, send it as a json message
        if (errorMessage != null) {
        	valuesToSend.put("errorMessage", errorMessage);
        } else { // everything went smoothly
        	response.setStatus(HttpServletResponse.SC_OK);
	        // Add session creation here
			HttpSession session = request.getSession(true);
			//It should always be new, since the session is just now starting after sign in
			if(!session.isNew()) session.invalidate();
			
			//Now it should be new
			session = request.getSession(true);
			request.getSession().setAttribute("username", username);
			
			
			valuesToSend.put("username", username);

        }
        
        jsonResponse = gson.toJson(valuesToSend);   	
    	response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
    	try {
			ConnectionUtility.closeConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
