package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

//@WebServlet("/UpdateOrdering")
public class UpdateOrdering extends HttpServlet {

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
    	AlbumDAO albumDAO = new AlbumDAO(connection);
    	List<Album> userAlbumList = null;
    	List<Integer> userAlbumIdsList = null;
        String[] readIds = null;
        List<String> listOfReadIds = null;
        List<Integer> integerIds = null;
        String username = (String) request.getSession().getAttribute("username");
        String errorMessage = null;
    	
    	// This user will send a list of album ids
    	readIds = request.getParameterValues("albumIds");
    	if(!CheckerUtility.checkAvailability(readIds)) {
    		errorMessage = "Missing inputs";
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	}
    	
    	
    	
    	if(errorMessage == null) {
    		integerIds = new ArrayList<Integer>();
    		listOfReadIds = Arrays.asList(readIds);
    		
    		int realId;

    		for(String id : listOfReadIds) {
    			realId = -1;
    			
    			try {
					realId = Integer.parseInt(id);
					integerIds.add(realId);
				} catch (NumberFormatException e) {
					errorMessage = "Invalid inputs";
		    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    		break;
				}
    		}
    	}
    	
    	if(errorMessage == null) {
    		try{
    			userAlbumList = albumDAO.getAlbumsOfUser(username);
    		} catch (SQLException e) {
    			errorMessage = "Failure in retrieving the albums from the database";
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
    	}
    	
    	
    	if(errorMessage == null) {
    		// Some of the ids may have been tampered with
    		userAlbumIdsList = userAlbumList.stream()
    				.map(x -> x.getId())
    				.collect(Collectors.toList());
    		for(Integer id : integerIds) {
    			if(!userAlbumIdsList.contains(id)) {
    				errorMessage = "You selected someone else's albums!";
		    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    		break;
    			}
    		}
    	}
    	
    	//Finally update the ordering
    	if(errorMessage == null) {
    		try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Failure in database connection";
			}
    	}
		
    	if(errorMessage == null) {
    		try { 
				albumDAO.resetOrdering(username);
				for(int i = 0; i < integerIds.size(); i++) {
					albumDAO.setOrdering(integerIds.get(i), i+1);
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
    	
    	if(errorMessage == null) {
	    	try {
				connection.commit();
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				errorMessage = "Failure in database connection";
			} finally {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
    	}
    	
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		HashMap<String, Object> valuesToSend = new HashMap<String, Object>();
		String jsonResponse;
		
		//If an error was found, send it as a json message
		if (errorMessage != null) {
			valuesToSend.put("errorMessage", errorMessage);
		} else { // everything went smoothly
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
