package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tomcat.jni.Time;
import org.thymeleaf.expression.Calendars;

import it.polimi.tiw.beans.Album;

public class AlbumDAO {

	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Retrieves the album from the database
	 * @param albumId
	 * @return the requested album, or null if the album was not found
	 * @throws SQLException
	 */
	public Album getAlbumFromId(int albumId) throws SQLException {

		String query = "SELECT id, title, date, creator_username "
				+ 		"FROM album "
				+ 		"WHERE id = ?";
		
		ResultSet resultSet = null; 
		Album resultAlbum = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, albumId);
			resultSet = preparedStatement.executeQuery();
			//The result set should be at most 1 row
			if(!resultSet.next()) {
				// Album not found
			}
			else {
				resultAlbum = new Album();
				resultAlbum.setId(resultSet.getInt("id"));
				resultAlbum.setTitle(resultSet.getString("title"));
				resultAlbum.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				resultAlbum.setCreator_username(resultSet.getString("creator_username"));
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			// The order of closing is, for better safety, result -> statement -> connection
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
		return resultAlbum;
	}
	
	/**
	 * Gets them from newest to oldest
	 */
	public List<Album> getAlbumsOfUser(String username) throws SQLException{
		String query = "SELECT id, title, date, creator_username "
				+ 		"FROM album "
				+ 		"WHERE creator_username = ?"
				+ 		"ORDER BY date DESC";
		
		ResultSet resultSet = null; 
		List<Album> albumList = new ArrayList<Album>();
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Album album = new Album();
				album.setId(resultSet.getInt("id"));
				album.setTitle(resultSet.getString("title"));
				album.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				album.setCreator_username(resultSet.getString("creator_username"));
				albumList.add(album);
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			// The order of closing is, for better safety, result -> statement -> connection
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
		return albumList;
	}

	/**
	 * Gets them from newest to oldest
	 */
	public List<Album> getAllAlbums() throws SQLException{
		List<Album> albumList = new ArrayList<Album>();
		String query = "SELECT id, title, date, creator_username "
				+ 		"FROM album A "
				+ 		"ORDER BY date DESC";
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Album album = new Album();
				album.setId(resultSet.getInt("id"));
				album.setTitle(resultSet.getString("title"));
				album.setDate(resultSet.getTimestamp("date", Calendar.getInstance()));
				album.setCreator_username(resultSet.getString("creator_username"));
				albumList.add(album);
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
		return albumList;
	}
	
	/**
	 * Creates a new album with the given title and username
	 * The date of creation will be the moment of execution of this function
	 * @param title the title of the album to create
	 * @param creator_username the username of the creator
	 * @return A code signaling the result of the operation
	 * @throws SQLException
	 */
	public int createAlbum(String title, String creator_username) throws SQLException {
		String query = "INSERT into album (title, date, creator_username) values (?, ?, ?)";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, title);
			Timestamp currentDate = new Timestamp(Instant.now().toEpochMilli());
			preparedStatement.setTimestamp(2, currentDate, Calendar.getInstance());
			preparedStatement.setString(3, creator_username);
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
	
	public int addImageToAlbum(int image_id, int album_id) throws SQLException {
		String query = "INSERT into containment (image_id, album_id) values (?, ?)";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, image_id);
			preparedStatement.setInt(2, album_id);
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
	
	public int removeImageFromAlbum(int image_id, int album_id) throws SQLException {
		String query = "DELETE FROM containment WHERE image_id = ? AND album_id = ?";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, image_id);
			preparedStatement.setInt(2, album_id);
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

	public int deleteAllImagesInAlbum(Integer album_id) throws SQLException{
		String query = "DELETE FROM containment WHERE album_id = ?";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, album_id);
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

	public int updateTitleOfAlbum(String title, Integer album_id) throws SQLException {
		String query = "UPDATE album SET title = ? WHERE id = ?";
		int code = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, title);
			preparedStatement.setInt(2, album_id);
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
