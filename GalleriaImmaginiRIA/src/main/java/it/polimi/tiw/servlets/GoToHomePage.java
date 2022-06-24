package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utility.ConnectionUtility;

//@WebServlet("/Galleria")
public class GoToHomePage extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
    
	@Override
    public void init() throws ServletException {
    
		connection = ConnectionUtility.getConnection(getServletContext());
		
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String htmlPath = "/WEB-INF/home_page.jsp";
		ServletContext servletContext = getServletContext();
    	
    	//Query string components: none
    	
    	//Gets this user's albums (username in the session)
    	AlbumDAO albumDAO = new AlbumDAO(connection);
    	HttpSession session = request.getSession();
    	List<Album> userAlbums = null;
    	List<Album> othersAlbums = null;
    	
    	try {
    		//The username is present and not null thanks to the filter 
			userAlbums = albumDAO.getAlbumsOfUser((String)session.getAttribute("username"));
			othersAlbums = albumDAO.getAllAlbums();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in retrieving albums");
		}
    	
    	//Gets other users' albums, excluding this user's
    	othersAlbums.removeAll(userAlbums);  
    	
    	//They are already in descending order (see DAO implementation)

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