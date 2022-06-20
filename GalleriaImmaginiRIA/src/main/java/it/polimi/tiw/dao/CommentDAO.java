package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Comment;

public class CommentDAO {

	private Connection connection;
	
	public CommentDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Comment> getAllCommentsForImage(int image_id) throws SQLException{
		
		List<Comment> commentsList = new ArrayList<Comment>();
		String query = "SELECT progressive, image_id, user, text FROM comment WHERE image_id = ?";
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, image_id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Comment comment = new Comment();
				comment.setProgressive(resultSet.getInt("progressive"));
				comment.setImage_id(resultSet.getInt("image_id"));
				comment.setUser(resultSet.getString("user"));
				comment.setText(resultSet.getString("text"));
				commentsList.add(comment);
			}
		}
		catch (Exception e) {
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
		return commentsList;
	}
	
	public Comment getCommentFromProgressiveImageId(int progressive, int image_id) throws SQLException{
		String query = "SELECT progressive, image_id, user, text FROM comment WHERE progressive = ?, image_id = ?";
		ResultSet resultSet = null; 
		Comment resultComment = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, progressive);
			preparedStatement.setInt(2, image_id);
			resultSet = preparedStatement.executeQuery();
			//The result set should be at most 1 row
			if(!resultSet.next()) {
				// Comment not found
			}
			else {
				resultComment = new Comment();
				resultComment.setProgressive(resultSet.getInt("progressive"));
				resultComment.setImage_id(resultSet.getInt("image_id"));
				resultComment.setUser(resultSet.getString("user"));
				resultComment.setText(resultSet.getString("text"));
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
		return resultComment;
	}
	
	public int createComment(int image_id, String user, String text) throws SQLException{
		
		int code = 0;
		String query = "INSERT into comment (image_id, user, text) values (?, ?, ?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, image_id);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, text);
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
}
