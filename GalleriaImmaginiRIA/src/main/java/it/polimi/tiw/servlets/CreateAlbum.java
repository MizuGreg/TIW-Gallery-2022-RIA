package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

/**
 * This action creates a new album with default values for this user 
 * and then redirects to the album edit page
 */
//@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {

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
		
		String username = (String) request.getSession().getAttribute("username"); //Present thanks to filter
		AlbumDAO albumDAO = new AlbumDAO(connection);		
		Album newlyCreatedAlbum = null;
		int newlyCreatedAlbumId = -1;
		String errorMessage = null;
		
		try {
			albumDAO.createAlbum("newAlbum", username); // This is the only db-altering operation, we don't disable autocommit
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			errorMessage = "There was a problem with creating the album";
		}
		
		if(errorMessage == null) {
			try {
				//The new album will always be the newest one
				newlyCreatedAlbum = albumDAO.getLatestAlbumOfUser(username); //There is at least one album, the newest one
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "There was a problem getting the newly created album, you can find it in the initial page view";
			}
		}
		
		if(errorMessage == null) {
			newlyCreatedAlbumId = newlyCreatedAlbum.getId();
		}
		
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
		String jsonResponse;
		
		//If an error was found, send it as a json message
		if (errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		} else { // everything went smoothly
			response.setStatus(HttpServletResponse.SC_OK);
			valuesToSend.put("albumId", newlyCreatedAlbumId); 
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
