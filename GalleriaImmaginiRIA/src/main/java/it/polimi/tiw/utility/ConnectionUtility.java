package it.polimi.tiw.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionUtility {

	public static Connection getConnection(ServletContext context) throws UnavailableException {

		Connection connection = null;
		String DB_URL = context.getInitParameter("dbUrl");
		String USER = context.getInitParameter("dbUser");
		String PASS = context.getInitParameter("dbPasswordGreg");
//		String PASS = context.getInitParameter("dbPasswordDani");
		String DRIVER_STRING = context.getInitParameter("dbDriver");
		
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
		
		return connection;
	}
	
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

}
