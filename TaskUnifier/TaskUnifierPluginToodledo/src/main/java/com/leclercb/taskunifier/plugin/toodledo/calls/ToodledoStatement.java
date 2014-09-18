/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.models.beans.LocationBean;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoConnection;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;

public class ToodledoStatement {

    private static CallAuthorize callAuthorize = new CallAuthorize();
	private static CallGetAccountInfo callGetAccountInfo = new CallGetAccountInfo();
	private static CallGetContexts callGetContexts = new CallGetContexts();
	private static CallGetFolders callGetFolders = new CallGetFolders();
	private static CallGetGoals callGetGoals = new CallGetGoals();
	private static CallGetLocations callGetLocations = new CallGetLocations();
	private static CallGetNotes callGetNotes = new CallGetNotes();
	private static CallGetTasks callGetTasks = new CallGetTasks();
	private static CallGetDeletedNotes callGetDeletedNotes = new CallGetDeletedNotes();
	private static CallGetDeletedTasks callGetDeletedTasks = new CallGetDeletedTasks();
	private static CallAddContext callAddContext = new CallAddContext();
	private static CallAddFolder callAddFolder = new CallAddFolder();
	private static CallAddGoal callAddGoal = new CallAddGoal();
	private static CallAddLocation callAddLocation = new CallAddLocation();
	private static CallAddNote callAddNote = new CallAddNote();
	private static CallAddTask callAddTask = new CallAddTask();
	private static CallEditContext callEditContext = new CallEditContext();
	private static CallEditFolder callEditFolder = new CallEditFolder();
	private static CallEditGoal callEditGoal = new CallEditGoal();
	private static CallEditLocation callEditLocation = new CallEditLocation();
	private static CallEditNote callEditNote = new CallEditNote();
	private static CallEditTask callEditTask = new CallEditTask();
	private static CallEditTaskParent callEditTaskParent = new CallEditTaskParent();
	private static CallEditTaskMeta callEditTaskMeta = new CallEditTaskMeta();
	private static CallDeleteContext callDeleteContext = new CallDeleteContext();
	private static CallDeleteFolder callDeleteFolder = new CallDeleteFolder();
	private static CallDeleteGoal callDeleteGoal = new CallDeleteGoal();
	private static CallDeleteLocation callDeleteLocation = new CallDeleteLocation();
	private static CallDeleteNote callDeleteNote = new CallDeleteNote();
	private static CallDeleteTask callDeleteTask = new CallDeleteTask();
	
	public static URI getAuthorizeUrl() throws SynchronizerException {
		return callAuthorize.getAuthorizeUrl();
	}
	
	private ToodledoConnection connection;
	
	public ToodledoStatement(ToodledoConnection connection) {
		CheckUtils.isNotNull(connection);
		this.connection = connection;
	}
	
	public ToodledoAccountInfo getAccountInfo() throws SynchronizerException {
		try {
			// this.checkConnection();
			return callGetAccountInfo.getAccountInfo(this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetAccountInfo.getAccountInfo(this.connection.getKey());
		}
	}
	
	public ContextBean[] getContexts(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetContexts.getContexts(
					accountInfo,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetContexts.getContexts(
					accountInfo,
					this.connection.getKey());
		}
	}
	
	public FolderBean[] getFolders(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetFolders.getFolders(
					accountInfo,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetFolders.getFolders(
					accountInfo,
					this.connection.getKey());
		}
	}
	
	public GoalBean[] getGoals(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetGoals.getGoals(accountInfo, this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetGoals.getGoals(accountInfo, this.connection.getKey());
		}
	}
	
	public LocationBean[] getLocations(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetLocations.getLocations(
					accountInfo,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetLocations.getLocations(
					accountInfo,
					this.connection.getKey());
		}
	}
	
	public NoteBean[] getNotes(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetNotes.getNotes(
					accountInfo,
					this.connection,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetNotes.getNotes(
					accountInfo,
					this.connection,
					this.connection.getKey());
		}
	}
	
	public NoteBean[] getNotesModifiedAfter(
			ToodledoAccountInfo accountInfo,
			Calendar modifiedAfter) throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetNotes.getNotesModifiedAfter(
					accountInfo,
					this.connection,
					this.connection.getKey(),
					modifiedAfter);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetNotes.getNotesModifiedAfter(
					accountInfo,
					this.connection,
					this.connection.getKey(),
					modifiedAfter);
		}
	}
	
	public NoteBean[] getDeletedNotes(
			ToodledoAccountInfo accountInfo,
			Calendar deletedAfter) throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetDeletedNotes.getDeletedNotes(
					accountInfo,
					this.connection.getKey(),
					deletedAfter);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetDeletedNotes.getDeletedNotes(
					accountInfo,
					this.connection.getKey(),
					deletedAfter);
		}
	}
	
	public TaskBean[] getTasks(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetTasks.getTasks(
					accountInfo,
					this.connection,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetTasks.getTasks(
					accountInfo,
					this.connection,
					this.connection.getKey());
		}
	}
	
	public TaskBean[] getTasksNotCompleted(ToodledoAccountInfo accountInfo)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetTasks.getTasksNotCompleted(
					accountInfo,
					this.connection,
					this.connection.getKey());
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetTasks.getTasksNotCompleted(
					accountInfo,
					this.connection,
					this.connection.getKey());
		}
	}
	
	public TaskBean[] getTasksModifiedAfter(
			ToodledoAccountInfo accountInfo,
			Calendar modifiedAfter) throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetTasks.getTasksModifiedAfter(
					accountInfo,
					this.connection,
					this.connection.getKey(),
					modifiedAfter);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetTasks.getTasksModifiedAfter(
					accountInfo,
					this.connection,
					this.connection.getKey(),
					modifiedAfter);
		}
	}
	
	public TaskBean[] getDeletedTasks(
			ToodledoAccountInfo accountInfo,
			Calendar deletedAfter) throws SynchronizerException {
		try {
			this.checkConnection();
			return callGetDeletedTasks.getDeletedTasks(
					accountInfo,
					this.connection.getKey(),
					deletedAfter);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callGetDeletedTasks.getDeletedTasks(
					accountInfo,
					this.connection.getKey(),
					deletedAfter);
		}
	}
	
	public String addContext(ToodledoAccountInfo accountInfo, Context context)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddContext.addContext(
					accountInfo,
					this.connection.getKey(),
					context);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddContext.addContext(
					accountInfo,
					this.connection.getKey(),
					context);
		}
	}
	
	public String addFolder(ToodledoAccountInfo accountInfo, Folder folder)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddFolder.addFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddFolder.addFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		}
	}
	
	public String addGoal(ToodledoAccountInfo accountInfo, Goal goal)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddGoal.addGoal(
					accountInfo,
					this.connection.getKey(),
					goal);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddGoal.addGoal(
					accountInfo,
					this.connection.getKey(),
					goal);
		}
	}
	
	public String addLocation(ToodledoAccountInfo accountInfo, Location location)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddLocation.addLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddLocation.addLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		}
	}
	
	public List<String> addNote(ToodledoAccountInfo accountInfo, Note note)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddNote.addNote(
					accountInfo,
					this.connection.getKey(),
					note);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddNote.addNote(
					accountInfo,
					this.connection.getKey(),
					note);
		}
	}
	
	public List<String> addNotes(ToodledoAccountInfo accountInfo, Note[] notes)
			throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddNote.addNotes(
					accountInfo,
					this.connection.getKey(),
					notes);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddNote.addNotes(
					accountInfo,
					this.connection.getKey(),
					notes);
		}
	}
	
	public List<String> addTask(
			ToodledoAccountInfo accountInfo,
			Task task,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddTask.addTask(
					accountInfo,
					this.connection.getKey(),
					task,
					syncSubTasks,
					syncMeta);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddTask.addTask(
					accountInfo,
					this.connection.getKey(),
					task,
					syncSubTasks,
					syncMeta);
		}
	}
	
	public List<String> addTasks(
			ToodledoAccountInfo accountInfo,
			Task[] tasks,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		try {
			this.checkConnection();
			return callAddTask.addTasks(
					accountInfo,
					this.connection.getKey(),
					tasks,
					syncSubTasks,
					syncMeta);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			return callAddTask.addTasks(
					accountInfo,
					this.connection.getKey(),
					tasks,
					syncSubTasks,
					syncMeta);
		}
	}
	
	public void editContext(ToodledoAccountInfo accountInfo, Context context)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditContext.editContext(
					accountInfo,
					this.connection.getKey(),
					context);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditContext.editContext(
					accountInfo,
					this.connection.getKey(),
					context);
		}
	}
	
	public void editFolder(ToodledoAccountInfo accountInfo, Folder folder)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditFolder.editFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditFolder.editFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		}
	}
	
	public void editGoal(ToodledoAccountInfo accountInfo, Goal goal)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditGoal.editGoal(accountInfo, this.connection.getKey(), goal);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditGoal.editGoal(accountInfo, this.connection.getKey(), goal);
		}
	}
	
	public void editLocation(ToodledoAccountInfo accountInfo, Location location)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditLocation.editLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditLocation.editLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		}
	}
	
	public void editNote(ToodledoAccountInfo accountInfo, Note note)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditNote.editNote(accountInfo, this.connection.getKey(), note);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditNote.editNote(accountInfo, this.connection.getKey(), note);
		}
	}
	
	public void editNotes(ToodledoAccountInfo accountInfo, Note[] notes)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditNote.editNotes(accountInfo, this.connection.getKey(), notes);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditNote.editNotes(accountInfo, this.connection.getKey(), notes);
		}
	}
	
	public void editTask(
			ToodledoAccountInfo accountInfo,
			Task task,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTask.editTask(
					accountInfo,
					this.connection.getKey(),
					task,
					syncSubTasks,
					syncMeta);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTask.editTask(
					accountInfo,
					this.connection.getKey(),
					task,
					syncSubTasks,
					syncMeta);
		}
	}
	
	public void editTasks(
			ToodledoAccountInfo accountInfo,
			Task[] tasks,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTask.editTasks(
					accountInfo,
					this.connection.getKey(),
					tasks,
					syncSubTasks,
					syncMeta);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTask.editTasks(
					accountInfo,
					this.connection.getKey(),
					tasks,
					syncSubTasks,
					syncMeta);
		}
	}
	
	public void editTaskParent(ToodledoAccountInfo accountInfo, Task task)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTaskParent.editTaskParent(
					accountInfo,
					this.connection.getKey(),
					task);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTaskParent.editTaskParent(
					accountInfo,
					this.connection.getKey(),
					task);
		}
	}
	
	public void editTasksParent(ToodledoAccountInfo accountInfo, Task[] tasks)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTaskParent.editTasksParent(
					accountInfo,
					this.connection.getKey(),
					tasks);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTaskParent.editTasksParent(
					accountInfo,
					this.connection.getKey(),
					tasks);
		}
	}
	
	public void editTaskMeta(ToodledoAccountInfo accountInfo, Task task)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTaskMeta.editTaskMeta(
					accountInfo,
					this.connection.getKey(),
					task);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTaskMeta.editTaskMeta(
					accountInfo,
					this.connection.getKey(),
					task);
		}
	}
	
	public void editTasksMeta(ToodledoAccountInfo accountInfo, Task[] tasks)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callEditTaskMeta.editTasksMeta(
					accountInfo,
					this.connection.getKey(),
					tasks);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callEditTaskMeta.editTasksMeta(
					accountInfo,
					this.connection.getKey(),
					tasks);
		}
	}
	
	public void deleteContext(ToodledoAccountInfo accountInfo, Context context)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteContext.deleteContext(
					accountInfo,
					this.connection.getKey(),
					context);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteContext.deleteContext(
					accountInfo,
					this.connection.getKey(),
					context);
		}
	}
	
	public void deleteFolder(ToodledoAccountInfo accountInfo, Folder folder)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteFolder.deleteFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteFolder.deleteFolder(
					accountInfo,
					this.connection.getKey(),
					folder);
		}
	}
	
	public void deleteGoal(ToodledoAccountInfo accountInfo, Goal goal)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteGoal.deleteGoal(
					accountInfo,
					this.connection.getKey(),
					goal);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteGoal.deleteGoal(
					accountInfo,
					this.connection.getKey(),
					goal);
		}
	}
	
	public void deleteLocation(
			ToodledoAccountInfo accountInfo,
			Location location) throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteLocation.deleteLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteLocation.deleteLocation(
					accountInfo,
					this.connection.getKey(),
					location);
		}
	}
	
	public void deleteNote(ToodledoAccountInfo accountInfo, Note note)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteNote.deleteNote(
					accountInfo,
					this.connection.getKey(),
					note);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteNote.deleteNote(
					accountInfo,
					this.connection.getKey(),
					note);
		}
	}
	
	public void deleteNotes(ToodledoAccountInfo accountInfo, Note[] notes)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteNote.deleteNotes(
					accountInfo,
					this.connection.getKey(),
					notes);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteNote.deleteNotes(
					accountInfo,
					this.connection.getKey(),
					notes);
		}
	}
	
	public void deleteTask(ToodledoAccountInfo accountInfo, Task task)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteTask.deleteTask(
					accountInfo,
					this.connection.getKey(),
					task);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteTask.deleteTask(
					accountInfo,
					this.connection.getKey(),
					task);
		}
	}
	
	public void deleteTasks(ToodledoAccountInfo accountInfo, Task[] tasks)
			throws SynchronizerException {
		try {
			this.checkConnection();
			callDeleteTask.deleteTasks(
					accountInfo,
					this.connection.getKey(),
					tasks);
		} catch (ToodledoConnectionException e) {
			this.connection.reconnect();
			callDeleteTask.deleteTasks(
					accountInfo,
					this.connection.getKey(),
					tasks);
		}
	}
	
	private void checkConnection() throws SynchronizerException {
		if (!this.connection.isConnected())
			throw new SynchronizerConnectionException(
					false,
					ToodledoApi.getInstance().getApiId(),
					null,
					"The connection is closed");
	}
	
}
