package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

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
		List<Image> userImages = null;
		Map<Integer, Boolean> selectedUserImages = null;
		String[] readCheckboxes = null;
		List<String> listOfReadCheckboxes = null;

		if (!CheckerUtility.checkAvailability(readAlbumId) || !CheckerUtility.checkAvailability(albumTitle)) {
			// todo go back to the previous screen? back to just the album?
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}

		try {
			albumId = Integer.parseInt(readAlbumId); // Hidden parameter in ALBUM_EDIT_PAGE that will be submitted on
														// pressing the submit button
		} catch (NumberFormatException e) {
			response.sendRedirect(getServletContext().getContextPath() + "/Home");
			return;
		}

		try {
			userImages = imageDAO.getImagesOfUser((String) request.getSession().getAttribute("username"));
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database connection");
		}

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
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database connection");
		}

		try { 
			albumDAO.updateTitleOfAlbum(albumTitle, albumId);
			albumDAO.deleteAllImagesInAlbum(albumId);
			for (Integer imageId : selectedUserImages.keySet()) {
				if (selectedUserImages.get(imageId)) {
					albumDAO.addImageToAlbum(imageId, albumId);
				}
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
		// If everything went smoothly
		try {
			connection.commit();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database connection");
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		response.sendRedirect(getServletContext().getContextPath() + "/Home");
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
