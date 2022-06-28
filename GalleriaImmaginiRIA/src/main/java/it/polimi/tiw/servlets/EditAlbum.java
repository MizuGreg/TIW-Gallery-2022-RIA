package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
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
//@WebServlet("/EditAlbum")
public class EditAlbum extends HttpServlet {

	private Connection connection;
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		
		connection = ConnectionUtility.getConnection(getServletContext());
	
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String readAlbumId = request.getParameter("id");
		String albumTitle = request.getParameter("albumTitle");
		Integer albumId = -1;
		ImageDAO imageDAO = new ImageDAO(connection);
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album = null;
		List<Image> userImages = null;
		Map<Integer, Boolean> selectedUserImages = null;
		String[] readCheckboxes = null;
		List<String> listOfReadCheckboxes = null;
		String errorMessage = null;
		String username = (String)request.getSession().getAttribute("username");


		if (!CheckerUtility.checkAvailability(readAlbumId) || !CheckerUtility.checkAvailability(albumTitle)) {
			// todo go back to the previous screen? back to just the album?
			errorMessage = "Missing parameters, change aborted";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		if(errorMessage == null) {
			try {
				albumId = Integer.parseInt(readAlbumId); // Hidden parameter in ALBUM_EDIT_PAGE that will be submitted on
															// pressing the submit button
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				errorMessage = "Missing album id, change aborted";
			}
		}
		
		//Ensure this album belongs to the user making the request
		if(errorMessage == null) {
			try {
				album = albumDAO.getAlbumFromId(albumId);	
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage =  "Couldn't get the album, change aborted";
			}
		}
		if(errorMessage == null) {
			if (album == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				errorMessage = "The album wasn't found, change aborted";
			}
		}
		
		if(errorMessage == null) {
			if(!album.getCreator_username().equals(username)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				errorMessage = "You can't edit someone else's album!";
			}
		}
		

		if(errorMessage == null) {
			try {
				userImages = imageDAO.getImagesOfUser((String) request.getSession().getAttribute("username"));
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Couldn't get the images for this user, change aborted";
			}
		}
		
		if(errorMessage == null) {
		
			selectedUserImages = new LinkedHashMap<Integer, Boolean>();
			readCheckboxes = request.getParameterValues("checkedImages");
			
			try {
				listOfReadCheckboxes = Arrays.asList(readCheckboxes);
			} catch (NullPointerException e) {
				// If no image was selected, create an empty list
				listOfReadCheckboxes = new ArrayList<String>();
			}
			
			for (Image image : userImages) {
				if (!listOfReadCheckboxes.contains(String.valueOf(image.getId()))) { // This image wasn't selected
					selectedUserImages.put(image.getId(), false);
				} else {
					selectedUserImages.put(image.getId(), true);
				}
			}
	
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Failure in database connection";
			}
		}
		if(errorMessage == null) {
			try { 
				albumDAO.updateTitleOfAlbum(albumTitle, albumId);
				albumDAO.deleteAllImagesInAlbum(albumId);
				for (Integer imageId : selectedUserImages.keySet()) {
					if (selectedUserImages.get(imageId)) {
						albumDAO.addImageToAlbum(imageId, albumId);
					}
				}
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Failure in update operation, the edit was cancelled";
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		// If everything went smoothly
		try {
			connection.commit();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			errorMessage = "Failure in database connection, couldn't commit the update";
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
		String jsonResponse;
		
		//If an error was found, send it as a json message
		if (errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		} else { // everything went smoothly
			// Do nothing
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
