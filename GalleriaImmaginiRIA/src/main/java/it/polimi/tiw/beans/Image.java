package it.polimi.tiw.beans;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public class Image {

	private int id;
	private String path;
	private String title;
	private Timestamp date;
	private String description;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Image image = (Image) obj;
        return this.description.equals(image.description) &&
        		this.date.equals(image.date) &&
        		this.id == image.id &&
        		this.title.equals(image.title) &&
        		this.path.equals(image.path);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, path, title, date, description);
	}
	
}

