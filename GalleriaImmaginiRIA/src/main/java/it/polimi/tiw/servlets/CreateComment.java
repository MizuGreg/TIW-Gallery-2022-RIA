package it.polimi.tiw.servlets;

import java.io.IOError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/CreateComment")
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
		String readImagePosition = request.getParameter("imagePosition");
		String readAlbumId = request.getParameter("albumId");
		String readPageNumber = request.getParameter("pageNumber");
		String commentText = request.getParameter("commentText");
		String path = getServletContext().getContextPath();
		ImageDAO imageDAO = new ImageDAO(connection);
		CommentDAO commentDAO = new CommentDAO(connection);
		
		List<Image> images = new ArrayList<Image>();
		
		Integer imagePosition;
		Integer imageId;
		Integer albumId;

		if( !CheckerUtility.checkAvailability(readImagePosition) ||
			!CheckerUtility.checkAvailability(readAlbumId) ||
			!CheckerUtility.checkAvailability(commentText) )
		{
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}

		try {
			imagePosition = Integer.parseInt(readImagePosition);
			albumId = Integer.parseInt(readAlbumId);
		} catch (NumberFormatException e) {
			//This might need to go somewhere else, but for now it's easiest to redirect to Home
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}

		//Get the image list
		try {
			images = imageDAO.getImagesInAlbum(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database connection");
			return;
		}
		
		//There is at least the same amount of images as the position read from the form
		imageId = images.get(imagePosition-1).getId();
		
		try {
			commentDAO.createComment(imageId, username, commentText);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database connection");
			return;
		}
		
		response.sendRedirect( path + "/Album?id=" + readAlbumId + "&page=" + readPageNumber + "&image=" + readImagePosition);
    	
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
