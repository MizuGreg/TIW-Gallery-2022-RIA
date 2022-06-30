package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
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
    
	@Override
    public void init() throws ServletException {
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String htmlPath = "/gallery.jsp";
    	
    	//Query string components: none
    	//Thanks to the filter, this page can't be accessed by someone not logged in
    	
    	RequestDispatcher requestDispatcher = request.getRequestDispatcher(htmlPath);
        requestDispatcher.include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void destroy() {
    	
    }
}