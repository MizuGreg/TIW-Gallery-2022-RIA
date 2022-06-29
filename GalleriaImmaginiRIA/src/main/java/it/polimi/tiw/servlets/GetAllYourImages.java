package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

@MultipartConfig
//@WebServlet("/GetYourImages")
public class GetAllYourImages extends HttpServlet {

	private Connection connection;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    	
        connection = ConnectionUtility.getConnection(getServletContext());
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String readAlbumId = request.getParameter("id");
		int albumId = 0;
		AlbumDAO albumDAO = new AlbumDAO(connection);
		ImageDAO imageDAO = new ImageDAO(connection);
		Album album = null;
		List<Image> imageList = null;
		List<Image> selectedImageList = null;
		List<Boolean> isContainedList = null;
		
		String username = (String)request.getSession().getAttribute("username");
        String errorMessage = null;
        
        //Sanitization
        if(!CheckerUtility.checkAvailability(readAlbumId)) {
        	errorMessage = "Missing album id";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
    	if(errorMessage == null) {
	        try {
				albumId = Integer.parseInt(readAlbumId);
			} catch (NumberFormatException e) {
				errorMessage = "Invlid album id";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
    	}
    	
    	//Check that the id is of an album of this user
    	if(errorMessage == null) {
    		try {
				album = albumDAO.getAlbumFromId(albumId);
			} catch (SQLException e) {
				errorMessage = "Failure in retrieving the album from the database";
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
    	}
    	
    	if(errorMessage == null) {
    		if(album == null) {
    			errorMessage = "Invalid album id";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	}
    	}
    	
    	if(errorMessage == null) {
    		if(!album.getCreator_username().equals(username)) {
    			errorMessage = "You can't modify someone else's album!";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		}
    	}
        
    	if(errorMessage == null) {
	        //Get the images
	        try {
				imageList = imageDAO.getImagesOfUser(username);
				selectedImageList = imageDAO.getImagesInAlbum(albumId);
			} catch (SQLException e) {
				errorMessage = "Failure in retrieving the images from the database";
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
    	}
        
    	//Put the checkboxes
    	if(errorMessage == null) {
    		// A map doesn't work because when we serialize it the keys will become
    		// the references, not the objects themselves
    		isContainedList = new ArrayList<>();
    		for (Iterator<Image> iterator = imageList.iterator(); iterator.hasNext();) {
    			Image image = iterator.next();
    			if (selectedImageList.contains(image)) {
    				isContainedList.add(true);
    			}
    			else isContainedList.add(false);
    		}
    	}
        
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		HashMap<String, Object> valuesToSend = new LinkedHashMap<String, Object>();
		String jsonResponse;
		
		//If an error was found, send it as a json message
		if (errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		} else { // everything went smoothly
			response.setStatus(HttpServletResponse.SC_OK);
			//This doesn't work so easily, json needs the map 
			// to be processed first 
			
			valuesToSend.put("images", imageList);
			valuesToSend.put("isPresentList", isContainedList);
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
