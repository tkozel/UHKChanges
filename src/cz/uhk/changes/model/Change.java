package cz.uhk.changes.model;

import java.util.Date;

/**
 * Entita zmeny v rozvrhu
 * 
 * @author Tomas Kozel
 * 
 */
public class Change {
	private String author;
	private String title;
	private Date startDate;
	private Date endDate;
	private String description;

	public Change() {
	}

	public Change(String author, String title, Date startDate, Date endDate,
			String description) {
		this.author = author;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
}
