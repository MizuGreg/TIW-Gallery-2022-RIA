package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utility.ConnectionUtility;

public class GetAlbums extends HttpServlet{

	private Connection connection;
	private static final long serialVersionUID = 1L;
	
	@Override
    public void init() throws ServletException {
    
		connection = ConnectionUtility.getConnection(getServletContext());
		
    }
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Gets this user's albums (username in the session)
		AlbumDAO albumDAO = new AlbumDAO(connection);
		HttpSession session = request.getSession();
		List<Album> userAlbums = null;
		List<Album> othersAlbums = null;
		String errorMessage = null;
		
		try {
    		//The username is present and not null thanks to the filter 
			userAlbums = albumDAO.getAlbumsOfUser((String)session.getAttribute("username"));
			othersAlbums = albumDAO.getAllAlbums();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			errorMessage = "Failure in retrieving albums";
		}
    	
		if(errorMessage == null) {
	    	//Gets other users' albums, excluding this user's
	    	othersAlbums.removeAll(userAlbums);  
		}
    	
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
        HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
        String jsonResponse;
		
		if(errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		}
		else {
			valuesToSend.put("userAlbums", userAlbums);
			valuesToSend.put("othersAlbums", othersAlbums);
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
