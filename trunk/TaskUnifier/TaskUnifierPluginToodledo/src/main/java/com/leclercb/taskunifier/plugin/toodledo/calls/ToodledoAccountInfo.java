/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.leclercb.taskunifier.api.models.enums.TaskPriority;

public final class ToodledoAccountInfo {
	
	private String userId;
	private String alias;
	private boolean proMember;
    private String email;
	private String dateFormat;
	private int timeZone;
	private int showTabNums;
	private int hideMonths;
	private TaskPriority hotListPriority;
	private int hotListDueDate;
	private Calendar lastTaskEdit;
	private Calendar lastTaskDelete;
	private Calendar lastFolderEdit;
	private Calendar lastContextEdit;
	private Calendar lastLocationEdit;
	private Calendar lastGoalEdit;
	private Calendar lastNoteEdit;
	private Calendar lastNoteDelete;
	
	protected ToodledoAccountInfo() {
		
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	protected void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	protected void setAlias(String alias) {
		this.alias = alias;
	}
	
	public boolean isProMember() {
		return this.proMember;
	}
	
	protected void setProMember(boolean proMember) {
		this.proMember = proMember;
	}

    public String getEmail() {
        return this.email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }
	
	public String getDateFormat() {
		return this.dateFormat;
	}
	
	protected void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public int getTimeZone() {
		return this.timeZone;
	}
	
	protected void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}
	
	public int getShowTabNums() {
		return this.showTabNums;
	}
	
	protected void setShowTabNums(int showTabNums) {
		this.showTabNums = showTabNums;
	}
	
	public int getHideMonths() {
		return this.hideMonths;
	}
	
	protected void setHideMonths(int hideMonths) {
		this.hideMonths = hideMonths;
	}
	
	public TaskPriority getHotListPriority() {
		return this.hotListPriority;
	}
	
	protected void setHotListPriority(TaskPriority hotListPriority) {
		this.hotListPriority = hotListPriority;
	}
	
	public int getHotListDueDate() {
		return this.hotListDueDate;
	}
	
	protected void setHotListDueDate(int hotListDueDate) {
		this.hotListDueDate = hotListDueDate;
	}
	
	public Calendar getLastTaskEdit() {
		return this.lastTaskEdit;
	}
	
	protected void setLastTaskEdit(Calendar lastTaskEdit) {
		this.lastTaskEdit = lastTaskEdit;
	}
	
	public Calendar getLastTaskDelete() {
		return this.lastTaskDelete;
	}
	
	protected void setLastTaskDelete(Calendar lastTaskDelete) {
		this.lastTaskDelete = lastTaskDelete;
	}
	
	public Calendar getLastFolderEdit() {
		return this.lastFolderEdit;
	}
	
	protected void setLastFolderEdit(Calendar lastFolderEdit) {
		this.lastFolderEdit = lastFolderEdit;
	}
	
	public Calendar getLastContextEdit() {
		return this.lastContextEdit;
	}
	
	protected void setLastContextEdit(Calendar lastContextEdit) {
		this.lastContextEdit = lastContextEdit;
	}
	
	public Calendar getLastLocationEdit() {
		return this.lastLocationEdit;
	}
	
	protected void setLastLocationEdit(Calendar lastLocationEdit) {
		this.lastLocationEdit = lastLocationEdit;
	}
	
	public Calendar getLastGoalEdit() {
		return this.lastGoalEdit;
	}
	
	protected void setLastGoalEdit(Calendar lastGoalEdit) {
		this.lastGoalEdit = lastGoalEdit;
	}
	
	public Calendar getLastNoteEdit() {
		return this.lastNoteEdit;
	}
	
	protected void setLastNoteEdit(Calendar lastNoteEdit) {
		this.lastNoteEdit = lastNoteEdit;
	}
	
	public Calendar getLastNoteDelete() {
		return this.lastNoteDelete;
	}
	
	protected void setLastNoteDelete(Calendar lastNoteDelete) {
		this.lastNoteDelete = lastNoteDelete;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		
		buffer.append("UserId: " + this.userId + "\n");
		buffer.append("Alias: " + this.alias + "\n");
		buffer.append("Pro Member: " + this.proMember + "\n");
        buffer.append("Email: " + this.email + "\n");
		buffer.append("Date Format: " + dateFormat + "\n");
		buffer.append("Time Zone: " + this.timeZone + "\n");
		buffer.append("Show Tab Numbers: " + this.showTabNums + "\n");
		buffer.append("Hide Months: " + this.hideMonths + "\n");
		buffer.append("Hot List Priority: " + this.hotListPriority + "\n");
		buffer.append("Hot List Due Date: " + this.hotListDueDate + "\n");
		buffer.append("Last Task Edit: "
				+ dateFormat.format(this.lastTaskEdit.getTime())
				+ "\n");
		buffer.append("Last Task Delete: "
				+ dateFormat.format(this.lastTaskDelete.getTime())
				+ "\n");
		buffer.append("Last Folder Edit: "
				+ dateFormat.format(this.lastFolderEdit.getTime())
				+ "\n");
		buffer.append("Last Context Edit: "
				+ dateFormat.format(this.lastContextEdit.getTime())
				+ "\n");
		buffer.append("Last Location Edit: "
				+ dateFormat.format(this.lastLocationEdit.getTime())
				+ "\n");
		buffer.append("Last Goal Edit: "
				+ dateFormat.format(this.lastGoalEdit.getTime())
				+ "\n");
		buffer.append("Last Note Edit: "
				+ dateFormat.format(this.lastNoteEdit.getTime()));
		buffer.append("Last Note Delete: "
				+ dateFormat.format(this.lastNoteDelete.getTime()));
		
		return buffer.toString();
	}
	
}
