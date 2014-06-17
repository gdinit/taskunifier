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
    private static CallGetTaskStatuses callGetTaskStatuses = new CallGetTaskStatuses();

    private static CallGetDeletedContexts callGetDeletedContexts = new CallGetDeletedContexts();
    private static CallGetDeletedFolders callGetDeletedFolders = new CallGetDeletedFolders();
    private static CallGetDeletedGoals callGetDeletedGoals = new CallGetDeletedGoals();
    private static CallGetDeletedNotes callGetDeletedNotes = new CallGetDeletedNotes();
    private static CallGetDeletedTasks callGetDeletedTasks = new CallGetDeletedTasks();
    private static CallGetDeletedTaskStatuses callGetDeletedTaskStatuses = new CallGetDeletedTaskStatuses();

    private static CallAddContext callAddContext = new CallAddContext();
    private static CallAddFolder callAddFolder = new CallAddFolder();
    private static CallAddGoal callAddGoal = new CallAddGoal();
    private static CallAddNote callAddNote = new CallAddNote();
    private static CallAddTask callAddTask = new CallAddTask();
    private static CallAddTaskStatus callAddTaskStatus = new CallAddTaskStatus();

    private static CallEditContext callEditContext = new CallEditContext();
    private static CallEditFolder callEditFolder = new CallEditFolder();
    private static CallEditGoal callEditGoal = new CallEditGoal();
    private static CallEditNote callEditNote = new CallEditNote();
    private static CallEditTask callEditTask = new CallEditTask();
    private static CallEditTaskStatus callEditTaskStatus = new CallEditTaskStatus();

    private static CallDeleteContext callDeleteContext = new CallDeleteContext();
    private static CallDeleteFolder callDeleteFolder = new CallDeleteFolder();
    private static CallDeleteGoal callDeleteGoal = new CallDeleteGoal();
    private static CallDeleteNote callDeleteNote = new CallDeleteNote();
    private static CallDeleteTask callDeleteTask = new CallDeleteTask();
    private static CallDeleteTaskStatus callDeleteTaskStatus = new CallDeleteTaskStatus();

    private static CallSync callSync = new CallSync();

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

    public TaskStatusBean[] getTaskStatuses(Calendar updatedAfter)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetTaskStatuses.getTaskStatuses(this.connection.getAccessToken(), updatedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetTaskStatuses.getTaskStatuses(this.connection.getAccessToken(), updatedAfter);
        }
    }

    public ModelBean[] getDeletedContexts(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedContexts.getDeletedContexts(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedContexts.getDeletedContexts(this.connection.getAccessToken(), deletedAfter);
        }
    }

    public ModelBean[] getDeletedFolders(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedFolders.getDeletedFolders(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedFolders.getDeletedFolders(this.connection.getAccessToken(), deletedAfter);
        }
    }

    public ModelBean[] getDeletedGoals(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedGoals.getDeletedGoals(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedGoals.getDeletedGoals(this.connection.getAccessToken(), deletedAfter);
        }
    }

    public ModelBean[] getDeletedNotes(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedNotes.getDeletedNotes(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedNotes.getDeletedNotes(this.connection.getAccessToken(), deletedAfter);
        }
    }

    public ModelBean[] getDeletedTasks(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedTasks.getDeletedTasks(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedTasks.getDeletedTasks(this.connection.getAccessToken(), deletedAfter);
        }
    }

    public ModelBean[] getDeletedTaskStatuses(Calendar deletedAfter) throws SynchronizerException {
        try {
            this.checkConnection();
            return callGetDeletedTaskStatuses.getDeletedTaskStatuses(this.connection.getAccessToken(), deletedAfter);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callGetDeletedTaskStatuses.getDeletedTaskStatuses(this.connection.getAccessToken(), deletedAfter);
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

    public NoteBean addNote(Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddNote.addNote(
                    this.connection.getAccessToken(),
                    note);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddNote.addNote(
                    this.connection.getAccessToken(),
                    note);
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

    public TaskStatusBean addTaskStatus(TaskStatus taskStatus) throws SynchronizerException {
        try {
            this.checkConnection();
            return callAddTaskStatus.addTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            return callAddTaskStatus.addTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
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

    public void editNote(Note note)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callEditNote.editNote(
                    this.connection.getAccessToken(),
                    note);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditNote.editNote(
                    this.connection.getAccessToken(),
                    note);
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

    public void editTaskStatus(TaskStatus taskStatus) throws SynchronizerException {
        try {
            this.checkConnection();
            callEditTaskStatus.editTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callEditTaskStatus.editTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
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

    public void deleteTaskStatus(TaskStatus taskStatus)
            throws SynchronizerException {
        try {
            this.checkConnection();
            callDeleteTaskStatus.deleteTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callDeleteTaskStatus.deleteTaskStatus(
                    this.connection.getAccessToken(),
                    taskStatus);
        }
    }

    public void syncStart() throws SynchronizerException {
        try {
            this.checkConnection();
            callSync.syncStart(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callSync.syncStart(this.connection.getAccessToken());
        }
    }

    public void syncEnd() throws SynchronizerException {
        try {
            this.checkConnection();
            callSync.syncEnd(this.connection.getAccessToken());
        } catch (OrganiTaskConnectionException e) {
            this.connection.reconnect();
            callSync.syncEnd(this.connection.getAccessToken());
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
