/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.*;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoConnection;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;

import java.util.Calendar;
import java.util.List;

public class ToodledoStatement {

    private static CallOAuth callOAuth = new CallOAuth();
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

    public static String getAuthorizeUrl() throws SynchronizerException {
        return callOAuth.getAuthorizeUrl();
    }

    public static ToodledoToken getToken(String code) throws SynchronizerException {
        return callOAuth.getToken(code);
    }

    public static ToodledoToken refreshToken(String refreshToken) throws SynchronizerException {
        return callOAuth.refreshToken(refreshToken);
    }

    private ToodledoConnection connection;

    public ToodledoStatement(ToodledoConnection connection) {
        CheckUtils.isNotNull(connection);
        this.connection = connection;
    }

    public ToodledoAccountInfo getAccountInfo() throws SynchronizerException {
        try {
            // this.checkConnection();
            return callGetAccountInfo.getAccountInfo(this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetAccountInfo.getAccountInfo(this.connection.getAccessToken());
        }
    }

    public ContextBean[] getContexts(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetContexts.getContexts(
                    accountInfo,
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetContexts.getContexts(
                    accountInfo,
                    this.connection.getAccessToken());
        }
    }

    public FolderBean[] getFolders(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetFolders.getFolders(
                    accountInfo,
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetFolders.getFolders(
                    accountInfo,
                    this.connection.getAccessToken());
        }
    }

    public GoalBean[] getGoals(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetGoals.getGoals(accountInfo, this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetGoals.getGoals(accountInfo, this.connection.getAccessToken());
        }
    }

    public LocationBean[] getLocations(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetLocations.getLocations(
                    accountInfo,
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetLocations.getLocations(
                    accountInfo,
                    this.connection.getAccessToken());
        }
    }

    public NoteBean[] getNotes(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetNotes.getNotes(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetNotes.getNotes(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken());
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
                    this.connection.getAccessToken(),
                    modifiedAfter);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetNotes.getNotesModifiedAfter(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    deletedAfter);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedNotes.getDeletedNotes(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetTasks.getTasks(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken());
        }
    }

    public TaskBean[] getTasksNotCompleted(ToodledoAccountInfo accountInfo)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetTasks.getTasksNotCompleted(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken());
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetTasks.getTasksNotCompleted(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken());
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
                    this.connection.getAccessToken(),
                    modifiedAfter);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetTasks.getTasksModifiedAfter(
                    accountInfo,
                    this.connection,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    deletedAfter);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedTasks.getDeletedTasks(
                    accountInfo,
                    this.connection.getAccessToken(),
                    deletedAfter);
        }
    }

    public String addContext(ToodledoAccountInfo accountInfo, Context context)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddContext.addContext(
                    accountInfo,
                    this.connection.getAccessToken(),
                    context);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddContext.addContext(
                    accountInfo,
                    this.connection.getAccessToken(),
                    context);
        }
    }

    public String addFolder(ToodledoAccountInfo accountInfo, Folder folder)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddFolder.addFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddFolder.addFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        }
    }

    public String addGoal(ToodledoAccountInfo accountInfo, Goal goal)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddGoal.addGoal(
                    accountInfo,
                    this.connection.getAccessToken(),
                    goal);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddGoal.addGoal(
                    accountInfo,
                    this.connection.getAccessToken(),
                    goal);
        }
    }

    public String addLocation(ToodledoAccountInfo accountInfo, Location location)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddLocation.addLocation(
                    accountInfo,
                    this.connection.getAccessToken(),
                    location);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddLocation.addLocation(
                    accountInfo,
                    this.connection.getAccessToken(),
                    location);
        }
    }

    public List<String> addNote(ToodledoAccountInfo accountInfo, Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddNote.addNote(
                    accountInfo,
                    this.connection.getAccessToken(),
                    note);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddNote.addNote(
                    accountInfo,
                    this.connection.getAccessToken(),
                    note);
        }
    }

    public List<String> addNotes(ToodledoAccountInfo accountInfo, Note[] notes)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddNote.addNotes(
                    accountInfo,
                    this.connection.getAccessToken(),
                    notes);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddNote.addNotes(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    task,
                    syncSubTasks,
                    syncMeta);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddTask.addTask(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    tasks,
                    syncSubTasks,
                    syncMeta);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            return callAddTask.addTasks(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    context);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditContext.editContext(
                    accountInfo,
                    this.connection.getAccessToken(),
                    context);
        }
    }

    public void editFolder(ToodledoAccountInfo accountInfo, Folder folder)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditFolder.editFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditFolder.editFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        }
    }

    public void editGoal(ToodledoAccountInfo accountInfo, Goal goal)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditGoal.editGoal(accountInfo, this.connection.getAccessToken(), goal);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditGoal.editGoal(accountInfo, this.connection.getAccessToken(), goal);
        }
    }

    public void editLocation(ToodledoAccountInfo accountInfo, Location location)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditLocation.editLocation(
                    accountInfo,
                    this.connection.getAccessToken(),
                    location);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditLocation.editLocation(
                    accountInfo,
                    this.connection.getAccessToken(),
                    location);
        }
    }

    public void editNote(ToodledoAccountInfo accountInfo, Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditNote.editNote(accountInfo, this.connection.getAccessToken(), note);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditNote.editNote(accountInfo, this.connection.getAccessToken(), note);
        }
    }

    public void editNotes(ToodledoAccountInfo accountInfo, Note[] notes)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditNote.editNotes(accountInfo, this.connection.getAccessToken(), notes);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditNote.editNotes(accountInfo, this.connection.getAccessToken(), notes);
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
                    this.connection.getAccessToken(),
                    task,
                    syncSubTasks,
                    syncMeta);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTask.editTask(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    tasks,
                    syncSubTasks,
                    syncMeta);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTask.editTasks(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    task);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTaskParent.editTaskParent(
                    accountInfo,
                    this.connection.getAccessToken(),
                    task);
        }
    }

    public void editTasksParent(ToodledoAccountInfo accountInfo, Task[] tasks)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTaskParent.editTasksParent(
                    accountInfo,
                    this.connection.getAccessToken(),
                    tasks);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTaskParent.editTasksParent(
                    accountInfo,
                    this.connection.getAccessToken(),
                    tasks);
        }
    }

    public void editTaskMeta(ToodledoAccountInfo accountInfo, Task task)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTaskMeta.editTaskMeta(
                    accountInfo,
                    this.connection.getAccessToken(),
                    task);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTaskMeta.editTaskMeta(
                    accountInfo,
                    this.connection.getAccessToken(),
                    task);
        }
    }

    public void editTasksMeta(ToodledoAccountInfo accountInfo, Task[] tasks)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTaskMeta.editTasksMeta(
                    accountInfo,
                    this.connection.getAccessToken(),
                    tasks);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callEditTaskMeta.editTasksMeta(
                    accountInfo,
                    this.connection.getAccessToken(),
                    tasks);
        }
    }

    public void deleteContext(ToodledoAccountInfo accountInfo, Context context)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteContext.deleteContext(
                    accountInfo,
                    this.connection.getAccessToken(),
                    context);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteContext.deleteContext(
                    accountInfo,
                    this.connection.getAccessToken(),
                    context);
        }
    }

    public void deleteFolder(ToodledoAccountInfo accountInfo, Folder folder)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteFolder.deleteFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteFolder.deleteFolder(
                    accountInfo,
                    this.connection.getAccessToken(),
                    folder);
        }
    }

    public void deleteGoal(ToodledoAccountInfo accountInfo, Goal goal)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteGoal.deleteGoal(
                    accountInfo,
                    this.connection.getAccessToken(),
                    goal);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteGoal.deleteGoal(
                    accountInfo,
                    this.connection.getAccessToken(),
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
                    this.connection.getAccessToken(),
                    location);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteLocation.deleteLocation(
                    accountInfo,
                    this.connection.getAccessToken(),
                    location);
        }
    }

    public void deleteNote(ToodledoAccountInfo accountInfo, Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteNote.deleteNote(
                    accountInfo,
                    this.connection.getAccessToken(),
                    note);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteNote.deleteNote(
                    accountInfo,
                    this.connection.getAccessToken(),
                    note);
        }
    }

    public void deleteNotes(ToodledoAccountInfo accountInfo, Note[] notes)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteNote.deleteNotes(
                    accountInfo,
                    this.connection.getAccessToken(),
                    notes);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteNote.deleteNotes(
                    accountInfo,
                    this.connection.getAccessToken(),
                    notes);
        }
    }

    public void deleteTask(ToodledoAccountInfo accountInfo, Task task)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteTask.deleteTask(
                    accountInfo,
                    this.connection.getAccessToken(),
                    task);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteTask.deleteTask(
                    accountInfo,
                    this.connection.getAccessToken(),
                    task);
        }
    }

    public void deleteTasks(ToodledoAccountInfo accountInfo, Task[] tasks)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteTask.deleteTasks(
                    accountInfo,
                    this.connection.getAccessToken(),
                    tasks);
        } catch (ToodledoConnectionException e) {
            this.connection.reconnect();
            callDeleteTask.deleteTasks(
                    accountInfo,
                    this.connection.getAccessToken(),
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
