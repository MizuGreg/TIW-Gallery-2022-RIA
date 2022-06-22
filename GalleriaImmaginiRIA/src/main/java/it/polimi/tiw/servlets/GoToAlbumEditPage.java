package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/AlbumEdit")
public class GoToAlbumEditPage extends HttpServlet {
	
	private Connection connection;
	private static final long serialVersionUID = 1L;
	
	
    public void init() throws ServletException {
    	
		connection = ConnectionUtility.getConnection(getServletContext());
		
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlPath = "/WEB-INF/album_edit.html";
		ServletContext servletContext = getServletContext();    	
    	
    	//Query string components: album id
    	//If no parameters are found: no album id -> go back to home page

		//This attribute is always present thanks to the filter
		String loggedUserUsername = (String) request.getSession().getAttribute("username");
		
		String readAlbumId = null;
		int albumId = 0;
		AlbumDAO albumDAO = new AlbumDAO(connection);
		ImageDAO imageDAO = new ImageDAO(connection);
		List<Image> imageList = null;
		LinkedHashMap<Image, Boolean> isContainedList = null;
		List<Image> containedImageList = null;
		Album album = null;
		    
		readAlbumId = request.getParameter("id");
    	
		if(!CheckerUtility.checkAvailability(readAlbumId)) {
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}
		
		try {
			albumId = Integer.parseInt(readAlbumId);
		} catch (NumberFormatException e) {
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}
		
		//Try to find the album with the specified id
		//If that album is NOT an album of the logged in user then redirect to home
		try {
			album = albumDAO.getAlbumFromId(albumId);
		} catch (SQLException e) {
			//There was a problem with retrieving the album
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "There was a problem with retrieving the album");
			return;
		}
		
		if(album == null || !album.getCreator_username().equals(loggedUserUsername)) {
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}
		
		//From here on, we know that the album exists and the user is the owner
		
		try {
			imageList = imageDAO.getImagesOfUser(loggedUserUsername);
			containedImageList = imageDAO.getImagesInAlbum(albumId);
		} catch (SQLException e) {
			//There was a problem with retrieving the images
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "There was a problem with retrieving the user images");
			return;
		}
		
		isContainedList = new LinkedHashMap<Image, Boolean>();
		
		//For each image of this user, check if it's already contained in the album, and store
		// the result in a list
		for (Iterator<Image> iterator = imageList.iterator(); iterator.hasNext();) {
			Image image = iterator.next();
			if (containedImageList.contains(image)) {
				isContainedList.put(image, true);
			}
			else isContainedList.put(image, false);
		}
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionUtility.closeConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
