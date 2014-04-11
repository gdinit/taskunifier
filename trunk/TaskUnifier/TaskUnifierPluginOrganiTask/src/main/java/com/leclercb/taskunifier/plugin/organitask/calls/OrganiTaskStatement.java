/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.*;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskConnection;
import com.leclercb.taskunifier.plugin.organitask.calls.exc.OrganiTaskConnectionException;

import java.util.Calendar;

public class OrganiTaskStatement {

    private static CallGetToken callGetToken = new CallGetToken();
    private static CallGetAuthInfo callGetAuthInfo = new CallGetAuthInfo();

    private static CallGetContexts callGetContexts = new CallGetContexts();
    private static CallGetFolders callGetFolders = new CallGetFolders();
    private static CallGetGoals callGetGoals = new CallGetGoals();
    private static CallGetNotes callGetNotes = new CallGetNotes();
    private static CallGetTasks callGetTasks = new CallGetTasks();

    private static CallGetDeletedContexts callGetDeletedContexts = new CallGetDeletedContexts();
    private static CallGetDeletedFolders callGetDeletedFolders = new CallGetDeletedFolders();
    private static CallGetDeletedGoals callGetDeletedGoals = new CallGetDeletedGoals();
    private static CallGetDeletedNotes callGetDeletedNotes = new CallGetDeletedNotes();
    private static CallGetDeletedTasks callGetDeletedTasks = new CallGetDeletedTasks();

    private static CallAddContext callAddContext = new CallAddContext();
    private static CallAddFolder callAddFolder = new CallAddFolder();
    private static CallAddGoal callAddGoal = new CallAddGoal();
    private static CallAddNote callAddNote = new CallAddNote();
    private static CallAddTask callAddTask = new CallAddTask();

    private static CallEditContext callEditContext = new CallEditContext();
    private static CallEditFolder callEditFolder = new CallEditFolder();
    private static CallEditGoal callEditGoal = new CallEditGoal();
    private static CallEditNote callEditNote = new CallEditNote();
    private static CallEditTask callEditTask = new CallEditTask();

    private static CallDeleteContext callDeleteContext = new CallDeleteContext();
    private static CallDeleteFolder callDeleteFolder = new CallDeleteFolder();
    private static CallDeleteGoal callDeleteGoal = new CallDeleteGoal();
    private static CallDeleteNote callDeleteNote = new CallDeleteNote();
    private static CallDeleteTask callDeleteTask = new CallDeleteTask();

    public static OrganiTaskToken getToken(String code) throws SynchronizerException {
        return callGetToken.getToken(code);
    }

    public static OrganiTaskToken refreshToken(String refreshToken) throws SynchronizerException {
        return callGetToken.refreshToken(refreshToken);
    }

    private OrganiTaskConnection connection;

    public OrganiTaskStatement(OrganiTaskConnection connection) {
        CheckUtils.isNotNull(connection);
        this.connection = connection;
    }

    public OrganiTaskAuthInfo getAuthInfo() throws SynchronizerException {
        try {
            // this.checkConnection();
            return callGetAuthInfo.getAuthInfo(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetAuthInfo.getAuthInfo(this.connection.getAccessToken());
        }
    }

    public ContextBean[] getContexts(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetContexts.getContexts(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetContexts.getContexts(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public FolderBean[] getFolders(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetFolders.getFolders(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetFolders.getFolders(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public GoalBean[] getGoals(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetGoals.getGoals(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetGoals.getGoals(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public NoteBean[] getNotes(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetNotes.getNotes(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetNotes.getNotes(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public TaskBean[] getTasks(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetTasks.getTasks(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetTasks.getTasks(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public ContextBean[] getDeletedContexts() throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedContexts.getDeletedContexts(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedContexts.getDeletedContexts(this.connection.getAccessToken());
        }
    }

    public FolderBean[] getDeletedFolders() throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedFolders.getDeletedFolders(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedFolders.getDeletedFolders(this.connection.getAccessToken());
        }
    }

    public GoalBean[] getDeletedGoals() throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedGoals.getDeletedGoals(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedGoals.getDeletedGoals(this.connection.getAccessToken());
        }
    }

    public NoteBean[] getDeletedNotes() throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedNotes.getDeletedNotes(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedNotes.getDeletedNotes(this.connection.getAccessToken());
        }
    }

    public TaskBean[] getDeletedTasks() throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedTasks.getDeletedTasks(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedTasks.getDeletedTasks(this.connection.getAccessToken());
        }
    }

    public ContextBean addContext(Context context, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddContext.addContext(
                    this.connection.getAccessToken(),
                    context,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddContext.addContext(
                    this.connection.getAccessToken(),
                    context,
                    syncParent);
        }
    }

    public FolderBean addFolder(Folder folder, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddFolder.addFolder(
                    this.connection.getAccessToken(),
                    folder,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddFolder.addFolder(
                    this.connection.getAccessToken(),
                    folder,
                    syncParent);
        }
    }

    public GoalBean addGoal(Goal goal, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddGoal.addGoal(
                    this.connection.getAccessToken(),
                    goal,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddGoal.addGoal(
                    this.connection.getAccessToken(),
                    goal,
                    syncParent);
        }
    }

    public NoteBean addNote(Note note, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddNote.addNote(
                    this.connection.getAccessToken(),
                    note,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddNote.addNote(
                    this.connection.getAccessToken(),
                    note,
                    syncParent);
        }
    }

    public TaskBean addTask(Task task, boolean syncParent) throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddTask.addTask(
                    this.connection.getAccessToken(),
                    task,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddTask.addTask(
                    this.connection.getAccessToken(),
                    task,
                    syncParent);
        }
    }

    public void editContext(Context context, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditContext.editContext(
                    this.connection.getAccessToken(),
                    context,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditContext.editContext(
                    this.connection.getAccessToken(),
                    context,
                    syncParent);
        }
    }

    public void editFolder(Folder folder, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditFolder.editFolder(
                    this.connection.getAccessToken(),
                    folder,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditFolder.editFolder(
                    this.connection.getAccessToken(),
                    folder,
                    syncParent);
        }
    }

    public void editGoal(Goal goal, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditGoal.editGoal(
                    this.connection.getAccessToken(),
                    goal,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditGoal.editGoal(
                    this.connection.getAccessToken(),
                    goal,
                    syncParent);
        }
    }

    public void editNote(Note note, boolean syncParent)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditNote.editNote(
                    this.connection.getAccessToken(),
                    note,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditNote.editNote(
                    this.connection.getAccessToken(),
                    note,
                    syncParent);
        }
    }

    public void editTask(Task task, boolean syncParent) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTask.editTask(
                    this.connection.getAccessToken(),
                    task,
                    syncParent);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditTask.editTask(
                    this.connection.getAccessToken(),
                    task,
                    syncParent);
        }
    }

    public void editContextParent(Context context) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditContext.editContextParent(
                    this.connection.getAccessToken(),
                    context);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditContext.editContextParent(
                    this.connection.getAccessToken(),
                    context);
        }
    }

    public void editFolderParent(Folder folder) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditFolder.editFolderParent(
                    this.connection.getAccessToken(),
                    folder);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditFolder.editFolderParent(
                    this.connection.getAccessToken(),
                    folder);
        }
    }

    public void editGoalParent(Goal goal) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditGoal.editGoalParent(
                    this.connection.getAccessToken(),
                    goal);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditGoal.editGoalParent(
                    this.connection.getAccessToken(),
                    goal);
        }
    }

    public void editNoteParent(Note note) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditNote.editNoteParent(
                    this.connection.getAccessToken(),
                    note);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditNote.editNoteParent(
                    this.connection.getAccessToken(),
                    note);
        }
    }

    public void editTaskParent(Task task) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTask.editTaskParent(
                    this.connection.getAccessToken(),
                    task);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditTask.editTaskParent(
                    this.connection.getAccessToken(),
                    task);
        }
    }

    public void deleteContext(Context context)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteContext.deleteContext(
                    this.connection.getAccessToken(),
                    context);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteContext.deleteContext(
                    this.connection.getAccessToken(),
                    context);
        }
    }

    public void deleteFolder(Folder folder)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteFolder.deleteFolder(
                    this.connection.getAccessToken(),
                    folder);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteFolder.deleteFolder(
                    this.connection.getAccessToken(),
                    folder);
        }
    }

    public void deleteGoal(Goal goal)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteGoal.deleteGoal(
                    this.connection.getAccessToken(),
                    goal);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteGoal.deleteGoal(
                    this.connection.getAccessToken(),
                    goal);
        }
    }

    public void deleteNote(Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteNote.deleteNote(
                    this.connection.getAccessToken(),
                    note);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteNote.deleteNote(
                    this.connection.getAccessToken(),
                    note);
        }
    }

    public void deleteTask(Task task)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteTask.deleteTask(
                    this.connection.getAccessToken(),
                    task);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteTask.deleteTask(
                    this.connection.getAccessToken(),
                    task);
        }
    }

    private void checkConnection() throws SynchronizerException {
        if (!this.connection.isConnected())
            throw new SynchronizerConnectionException(
                    false,
                    OrganiTaskApi.getInstance().getApiId(),
                    null,
                    "The connection is closed");
    }

}