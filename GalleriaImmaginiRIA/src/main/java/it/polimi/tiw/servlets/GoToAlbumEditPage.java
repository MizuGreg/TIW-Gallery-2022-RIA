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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utility.CheckerUtility;

//@WebServlet("/AlbumEdit")
public class GoToAlbumEditPage extends HttpServlet {
	
	private Connection connection;
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	
    public void init() throws ServletException {
    	
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    	
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlPath = "/WEB-INF/album_edit.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
    	
    	
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
		
		context.setVariable("imagesMap", isContainedList);
		context.setVariable("albumTitle", album.getTitle());

		templateEngine.process(htmlPath, context, response.getWriter());		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		
	}
}
