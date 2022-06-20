package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utility.CheckerUtility;

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
		final String DB_URL = getServletContext().getInitParameter("dbUrl");
		final String USER = getServletContext().getInitParameter("dbUser");
		final String PASS = getServletContext().getInitParameter("dbPasswordGreg");
//		final String PASS = getServletContext().getInitParameter("dbPasswordDani");
		final String DRIVER_STRING = getServletContext().getInitParameter("dbDriver");
		
	
		try {
			Class.forName(DRIVER_STRING);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		}
		catch (ClassNotFoundException e){
			throw new UnavailableException("Can't load db driver");
		}
		catch (SQLException e) {
			throw new UnavailableException("Can't connect to database");
		}
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
		
		try {
			albumDAO.createAlbum("newAlbum", username);
			newlyCreatedAlbum = albumDAO.getAlbumsOfUser(username).get(0); //There is at least one album, the newest one
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "There was a problem with creating the album");
			return;
		}
		
		//After creating the album, redirect to the album edit page
		response.sendRedirect(getServletContext().getContextPath() + "/AlbumEdit?id=" + newlyCreatedAlbum.getId());
	}
	
	@Override
	public void destroy() {	
	
	}

}
