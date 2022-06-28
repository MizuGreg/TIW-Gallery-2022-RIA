package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/Image")
public class GetImageInfo extends HttpServlet{

	private Connection connection;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    	
        connection = ConnectionUtility.getConnection(getServletContext());
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String errorMessage = null;
    	String readImageId = request.getParameter("id");
    	int imageId = -1;
    	ImageDAO imageDAO = new ImageDAO(connection);
    	CommentDAO commentDAO = new CommentDAO(connection);
    	Image image = null;
    	List<Comment> comments = null;  
    	
    	
    	if(!CheckerUtility.checkAvailability(readImageId)) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		errorMessage = "Missing image id";
    	}
    	
    	if(errorMessage == null) {
    		try {
				imageId = Integer.parseInt(readImageId);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		errorMessage = "Invalid image id";
			}
    	}
    	
    	if(errorMessage == null) {
    		//Get the image
    		try {
				image = imageDAO.getImageFromId(imageId);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Couldn't get the image from the database";
			}
    	}
    	
    	if(errorMessage == null) {
    		if(image == null) {
    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		errorMessage = "Invalid image id";
    		}
    	}
    	
    	if(errorMessage == null) {
    		//...and its comments
    		try {
				comments = commentDAO.getAllCommentsForImage(imageId);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Couldn't get the comments from the database";
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
			valuesToSend.put("image", image);
			valuesToSend.put("comments", comments);
		}
	   
	    jsonResponse = gson.toJson(valuesToSend);   	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
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
