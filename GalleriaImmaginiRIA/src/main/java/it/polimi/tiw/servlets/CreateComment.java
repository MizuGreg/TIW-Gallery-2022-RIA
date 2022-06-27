package it.polimi.tiw.servlets;

import java.io.IOError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/CreateComment")
@MultipartConfig
public class CreateComment extends HttpServlet{
    
    private Connection connection;
	private static final long serialVersionUID = 1L;
    
    @Override
    public void init() throws ServletException {
    	
    	connection = ConnectionUtility.getConnection(getServletContext());
    
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
		String username = (String) request.getSession().getAttribute("username"); // Guaranteed to exist thanks to filters
		String readImageId = request.getParameter("imageId");
		String commentText = request.getParameter("commentText");
		String path = getServletContext().getContextPath();
		ImageDAO imageDAO = new ImageDAO(connection);
		CommentDAO commentDAO = new CommentDAO(connection);
		String errorMessage = null;
		
		Integer imageId = -1;

		if( !CheckerUtility.checkAvailability(readImageId) ||
			!CheckerUtility.checkAvailability(commentText) )
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			errorMessage = "Missing id or comment text";
		}

		if(errorMessage == null) {
			try {
				imageId = Integer.parseInt(readImageId);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				errorMessage = "Invalid id or comment text";
			}
		}
		
		//todo check if the image actually exists
		try {
			if(imageDAO.getImageFromId(imageId) == null) {
				errorMessage = "The image doesn't exist";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			errorMessage = "Could not validate image existence";
		}

		if(errorMessage == null) {
			try {
				commentDAO.createComment(imageId, username, commentText);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Could not create the comment";
			}
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
        HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
        String jsonResponse;
		
		if(errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
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
