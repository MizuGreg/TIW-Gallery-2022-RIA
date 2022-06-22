package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polimi.tiw.beans.Image;

public class ImageDAO {

	private Connection connection;
	
	public ImageDAO(Connection connection) {
		this.connection = connection;
	}
	
	//Creating an image is not necessary for this project
	
	public Image getImageFromId(int id) throws SQLException {
	
		String query = "SELECT id, path, title, date, description, uploader_username FROM image WHERE id = ?";
		ResultSet resultSet = null; 
		Image resultImage = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			//The result set should be at most 1 row
			if(!resultSet.next()) {
				// Image not found
			}
			else {
				resultImage = new Image();
				resultImage.setId(resultSet.getInt("id"));
				resultImage.setPath(resultSet.getString("path"));
				resultImage.setTitle(resultSet.getString("title"));
				resultImage.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				resultImage.setDescription(resultSet.getString("description"));
				resultImage.setUploader_username(resultSet.getString("uploader_username"));
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
		return resultImage;
		}
	
	/**
	 * Gets them from newest to oldest
	 */
	public List<Image> getImagesInAlbum(int albumId) throws SQLException{
		List<Image> imageList = new ArrayList<Image>();
		String query = "SELECT I.id, I.path, I.title, I.date, I.description, I.uploader_username "
				+ 		"FROM image I, containment C "
				+ 		"WHERE C.image_id = I.id "
				+ 		"AND C.album_id = ? "
				+ 		"ORDER BY I.date DESC ";
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, albumId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Image image = new Image();
				image.setId(resultSet.getInt("id"));
				image.setPath(resultSet.getString("path"));
				image.setTitle(resultSet.getString("title"));
				image.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				image.setDescription(resultSet.getString("description"));
				image.setUploader_username(resultSet.getString("uploader_username"));
				imageList.add(image);
			}
		} catch (SQLException e) {
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
		return imageList;
	}
	
	/**
	 * Gets them from newest to oldest
	 */
	public List<Image> getImagesOfUser(String username) throws SQLException{
		List<Image> imageList = new ArrayList<Image>();
		String query = "SELECT I.id, I.path, I.title, I.date, I.description, I.uploader_username "
				+ 		"FROM image I "
				+ 		"WHERE I.uploader_username = ? "
				+ 		"ORDER BY date DESC;";
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Image image = new Image();
				image.setId(resultSet.getInt("id"));
				image.setPath(resultSet.getString("path"));
				image.setTitle(resultSet.getString("title"));
				image.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				image.setDescription(resultSet.getString("description"));
				image.setUploader_username(resultSet.getString("uploader_username"));
				imageList.add(image);
			}
		} catch (SQLException e) {
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
		return imageList;
	}
}
