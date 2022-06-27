package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {

	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public int createUser(String username, String email, String password) throws SQLException{
		
		String query = "INSERT into user (username, email, password) values (?, ?, ?)";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, password);
			code = preparedStatement.executeUpdate();
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return code;
	}
	
	// insecure? should we only have checkCredentials?
	public User getUserFromUsername(String username) throws SQLException {
		// We need to create the database first, these names might change

		String query = "SELECT username, email FROM user WHERE username = ?";
		ResultSet resultSet = null; 
		User resultUser = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			//The result set should be at most 1 row
			if(!resultSet.next()) {
				// User not found
				// resultUser is already null
			}
			else {
				resultUser = new User();
				resultUser.setNickname(resultSet.getString("username"));
				resultUser.setEmail(resultSet.getString("email"));
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return resultUser;
	}

	public User checkCredentials(String username, String password) throws SQLException{
		// We need to create the database first, these names might change

		String query = "SELECT username, email "
				+ 		"FROM user "
				+ 		"WHERE BINARY username = ? "
				+ 		"AND BINARY password = ?";		ResultSet resultSet = null; 
		User resultUser = null;
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			resultSet = preparedStatement.executeQuery();
			//The result set should be at most 1 row
			if(!resultSet.next()) {
				// User not found
				// resultUser is already null
			}
			else {
				resultUser = new User();
				resultUser.setNickname(resultSet.getString("username"));
				resultUser.setEmail(resultSet.getString("email"));
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return resultUser;
	}
}
