package it.polimi.tiw.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class ConnectionTester extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Object variable;
	
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		// a template resolver is an object in charge of resolving templates and containing additional information
		// related to the template, like the template mode, if it can be cached and for how long. a servletcontext
		// resolver specifically computes the resource from which to resolve the template based on a ServletContext
		// object.
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final String DB_URL = "jdbc:mysql://localhost:3306/dbtest?serverTimezone=UTC";
		final String USER = "root";
		final String PASS_greg = "C0ntinu@zione";
		final String PASS_dani = "$nnH68bmJ4X4r*EXMR";
		
		String htmlPath = "/WEB-INF/login_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		// A webContext is used to pass variables to the template engine, just like the pageContext does
		// in JSP.
		context.setVariable("variable", variable);
		templateEngine.process(htmlPath, context, response.getWriter());
	}
}