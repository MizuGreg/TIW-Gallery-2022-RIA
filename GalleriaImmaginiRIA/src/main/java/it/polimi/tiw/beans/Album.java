package it.polimi.tiw.beans;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public class Album {

	private int id;
	private String title;
	private Timestamp date;
	private String creator_username;
	private int ordering;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public String getCreator_username() {
		return creator_username;
	}
	public void setCreator_username(String creator_username) {
		this.creator_username = creator_username;
	}
	public int getOrdering() {
		return ordering;
	}
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return this.creator_username.equals(album.creator_username) &&
        		this.date.equals(album.date) &&
        		this.id == album.id &&
        		this.title.equals(album.title) &&
        		this.ordering == album.ordering;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(creator_username,date,id,title, ordering);
	}
	
}
