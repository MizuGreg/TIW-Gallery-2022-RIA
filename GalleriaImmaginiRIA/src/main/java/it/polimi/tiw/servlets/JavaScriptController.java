package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.utility.CheckerUtility;

//@WebServlet("/js/*")
public class JavaScriptController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		//Doesn't need to access DB or a template engine
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("helloooooooo\n\n\n\njavascript\n\n\n");
		String readString = request.getPathInfo();
		
		if(!CheckerUtility.checkValidJs(readString)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the requested JavaScript file is not valid");
			return;
		}
		
		ServletContext context = getServletContext();
		InputStream inputStream;
    	inputStream = context.getResourceAsStream("/resources/js" + readString);

    	OutputStream outStream = response.getOutputStream();

        if (inputStream == null) {
            response.setContentType("text/plain");
            outStream.write("Failed to send JavaScript".getBytes());
        } else {
            response.setContentType("text/javascript");

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                outStream.write(buffer, 0, bytesRead);
            }
        }        
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	public void destroy() {	
	
	}


}