package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ldap.SortControl;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.coyote.http11.filters.IdentityInputFilter;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationTargetTypeConstants;
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
import it.polimi.tiw.test.ConnectionTester;
import it.polimi.tiw.utility.CheckerUtility;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/Album")
public class GoToAlbumPage extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private final int IMAGES_PER_PAGE = 5;

    
    @Override
    public void init() throws ServletException {
    	
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    	
		connection = ConnectionUtility.getConnection(getServletContext());
		
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    	String htmlPath = "/WEB-INF/album_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
    	
    	
    	//Query string components: album id, album page (each containing 5 images), image position
    	//If no parameters are found: no album id -> default to this user's (session) newest album
    	//                            no album page -> page 1 (start counting from 1)
    	//							  no image position -> the album page doesn't show the image but only the thumbnails
    	
		String readAlbumId = null, readAlbumPage = null, readImagePosition = null;
		int albumId = 0, albumPage = 0, imagePosition = 0;
		AlbumDAO albumDAO = new AlbumDAO(connection);
		ImageDAO imageDAO = new ImageDAO(connection);
		CommentDAO commentDAO = new CommentDAO(connection);
		List<Album> retrievedAlbumList = null;
		List<Image> imageList = null;
		Map<Image, Integer> imageMap = null;
		Map<Image, Integer> imagesToShow = null;
		List<Image> tempImagesToShow = null;
		List<Comment> comments = null;
		Album album = null;
		Image shownImage = null;
		boolean showNext = false;
		boolean showPrev = false;
		boolean showEditButton = false;
    	    
		boolean isImageShown = false;
		boolean getFirstUserAlbum = false;
    	readAlbumId = request.getParameter("id");
    	readAlbumPage = request.getParameter("page");
    	readImagePosition = request.getParameter("image");
    	
    	//Get the id from the url
    	//if there is no parameter, default to getting the newest one for the currently logged in user
    	if (!CheckerUtility.checkAvailability(readAlbumId)) {
    		albumId = 0;
    		getFirstUserAlbum = true;
    	}
    	else {
    		try {
    			//same if the parameter is badly formatted
    			albumId = Integer.parseInt(readAlbumId);
			} catch (NumberFormatException e) {
				albumId = 0;
				getFirstUserAlbum = true;
			}
    	}
    	try {
			
    		// We try to get the album at the index given, which is either 0 (initialization)
    		// or the correct one read from the query string 
			album = albumDAO.getAlbumFromId(albumId);
			//If the result is null OR we had to get the first album anyways, then we do so
			if(getFirstUserAlbum || album == null) {

				retrievedAlbumList = albumDAO.getAlbumsOfUser((String)request.getSession().getAttribute("username"));
	    		if(retrievedAlbumList == null || retrievedAlbumList.size() == 0) {
	    			//The user doesn't have any albums
	    			//Redirect to the home page

	    			response.sendRedirect(getServletContext().getContextPath() + "/Home");
	    			return;
	    		}
	    		// I don't want to have a query string without an id, so i redirect to the same page but adding the id
	    		else {
	    			response.sendRedirect(getServletContext().getContextPath() + "/Album?id=" + retrievedAlbumList.get(0).getId());
	    			return;
	    		}
	    	}
		}
		catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in retrieving albums");
			return;
		}
    	albumId = album.getId();
    	
    	//Get all images of this album
    	try {
			imageList = imageDAO.getImagesInAlbum(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in retrieving album images");
			return;
		}
    	
    	imageMap = new LinkedHashMap<Image, Integer>();
    	for(int i = 0; i<imageList.size(); i++) {
    		imageMap.put(imageList.get(i), i+1);
    	}
    	
    	// Compute which images to show
    	// Get only the ones in position [(page-1)*5+1, page*5)
    	if (!CheckerUtility.checkAvailability(readAlbumPage)) {
    		//We don't want to have an invalide page parameter
    		response.sendRedirect(getServletContext().getContextPath() + "/Album?id=" + readAlbumId + "&page=1");
    		return;
    	}
    	else {
    		try {
				albumPage = Integer.parseInt(readAlbumPage);
			} catch (NumberFormatException e) {
				//We don't want to have an invalide page parameter
	    		response.sendRedirect(getServletContext().getContextPath() + "/Album?id=" + readAlbumId + "&page=1");
	    		return;
			}
    	}
    	
    	if(albumPage <= 0) {
    		//We don't want to have an invalide page parameter
    		response.sendRedirect(getServletContext().getContextPath() + "/Album?id=" + readAlbumId + "&page=1");
    		return;
    	}
    	
    	int lowerBound = (albumPage-1)*IMAGES_PER_PAGE + 1;
    	int upperBound = albumPage * IMAGES_PER_PAGE;
    	int availablePages;
    	
    	if(imageList.size() == 0) {
    		lowerBound = 1;
    		upperBound = IMAGES_PER_PAGE;
    	}
    	
    	//If (page-1)*5+1 > maxImages then default to last page
    	else if(imageList.size() < lowerBound) {
    		//These are only the completely filled pages
    		availablePages = imageList.size() / IMAGES_PER_PAGE;
    		
    		if(imageList.size() > availablePages * IMAGES_PER_PAGE) {
    			//There is another not full page
    			availablePages++;
    		}
    		lowerBound = (availablePages-1)*IMAGES_PER_PAGE + 1;
    		upperBound = availablePages * IMAGES_PER_PAGE;
    	}
    	
    	//To avoid going out of bounds
    	if(imageList.size() < upperBound) upperBound = imageList.size();
    	
    	tempImagesToShow = imageList.subList(lowerBound-1, upperBound);
    	//A linked hashmap is needed to keep the order
    	imagesToShow = new LinkedHashMap<Image, Integer>();
    	
    	for(Image image : tempImagesToShow) {
    		imagesToShow.put(image, imageMap.get(image));
    	}
    	
    	
    	//If there are more images to the right or left, set a boolean to true that a
    	// th:if will pick up to show PRECEDENTI or SUCCESSIVE
    	if(lowerBound > 1) showPrev = true;
    	if(imageList.size() > upperBound) showNext = true;
    	
    	// If the image position is present...
    	if(CheckerUtility.checkAvailability(readImagePosition)) {
    		try {
				imagePosition = Integer.parseInt(readImagePosition);
				isImageShown = true;
			} catch (NumberFormatException e) {
				// Don't show the image
				isImageShown = false;
			}
    	}
    	
    	// ...and if it's a valid image in this album
    	if(isImageShown) {
    		if(imagePosition > imageList.size()) {
    			isImageShown = false;
    		}
    	}
    	
    	int imageId = -1;
    	if(isImageShown) {
    		shownImage = imageList.get(imagePosition-1);
    		imageId = shownImage.getId();
    	
			//Also show the form with all the elements in it:
			// CommentDAO.getAllCommentsForImage(curImage.getId()) 
			// Show the form, with a hidden field containing the image id
			try {
				comments = commentDAO.getAllCommentsForImage(imageId);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in retrieving comments");
				return;
			}
			
    	}
    	// If this album is the user's, show the Edit button
    	if(album.getCreator_username().equals((String)request.getSession().getAttribute("username"))) {
    		showEditButton = true;
    	}
    	
    	context.setVariable("thumbnailList", imagesToShow);
    	context.setVariable("showNext", showNext);
    	context.setVariable("showPrev", showPrev);
    	context.setVariable("isImageShown", isImageShown);
    	context.setVariable("shownImage", shownImage);
    	if(isImageShown) {
	    	context.setVariable("comments", comments);
	    	context.setVariable("imageId", imageId);
    	}
    	context.setVariable("showEditButton", showEditButton);
    	
		templateEngine.process(htmlPath, context, response.getWriter());
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