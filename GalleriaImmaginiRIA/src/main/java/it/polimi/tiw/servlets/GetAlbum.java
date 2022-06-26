package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

public class GetAlbum extends HttpServlet{

	private Connection connection;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    	
        connection = ConnectionUtility.getConnection(getServletContext());
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String readAlbumId = null;
		int albumId = 0;
		boolean getFirstUserAlbum = false;
		AlbumDAO albumDAO = new AlbumDAO(connection);
		ImageDAO imageDAO = new ImageDAO(connection);
		List<Image> imageList = null;

		
        String errorMessage = null;
       
        readAlbumId = request.getParameter("id");
        //Get the id from the url
    	//if there is no parameter, default to getting the newest one for the currently logged in user
    	if (!CheckerUtility.checkAvailability(readAlbumId)) {
    		errorMessage = "Invalid inputs received";
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	}
    	else {
    		try {
    			//same if the parameter is badly formatted
    			albumId = Integer.parseInt(readAlbumId);
			} catch (NumberFormatException e) {
				errorMessage = "Invalid inputs received";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
    	}
    	
    	if(errorMessage == null) {
	    	try { 
				imageList = imageDAO.getImagesInAlbum(albumId);
				//If the result is null, send an error
				if(imageList == null) {
					errorMessage = "Couldn't retrieve the images from the album with id " + albumId;
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			}
	    	catch (SQLException e) {
				errorMessage = "Failure in retrieving the images from the database";
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
			valuesToSend.put("imagesList", imageList); 
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
