/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskAuthInfo;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskStatement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Contact;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.ContactBean;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.models.beans.LocationBean;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.plugins.AbstractSynchronizer;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoDeletedContact;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoApiException;

public class OrganiTaskSynchronizer extends AbstractSynchronizer {

    private OrganiTaskStatement statement;
    private OrganiTaskAuthInfo authInfo;

    private List<Task> addedTasks;

    private Calendar lastSync;

    OrganiTaskSynchronizer(OrganiTaskConnection connection) {
        super(connection, "organitask");

        this.statement = connection.getStatement();
    }

    @Override
    public SynchronizerPlugin getPlugin() {
        return PluginApi.getPlugin(OrganiTaskPlugin.ID);
    }

    public Calendar getLastSync() {
        return lastSync;
    }

    public void setLastSync(Calendar lastSync) {
        this.lastSync = lastSync;
    }

    @Override
    public void synchronize(SynchronizerChoice choice, ProgressMonitor monitor)
            throws SynchronizerException {
        this.addedTasks = null;
        this.authInfo = ((OrganiTaskConnection) this.getConnection()).getAuthInfo();

        this.synchronizeModels(choice, monitor, new ModelType[]{
                ModelType.CONTEXT,
                ModelType.FOLDER,
                ModelType.GOAL,
                ModelType.LOCATION,
                ModelType.NOTE,
                ModelType.CONTACT,
                ModelType.NOTE,
                ModelType.TASK});

        this.createContactsNote();
        this.createDeletedContactsNote();

        this.authInfo = this.statement.getAuthInfo();
        this.lastSync = Calendar.getInstance();
    }

    @Override
    protected boolean isUpdatedModels(ModelType type)
            throws SynchronizerException {
        if (type == ModelType.CONTACT) {
            return true;
        }

        if (type == ModelType.CONTEXT) {
            if (this.lastContextEdit == null
                    || this.accountInfo.getLastContextEdit().compareTo(
                    this.lastContextEdit) > 0) {
                return true;
            }

            return false;
        }

        if (type == ModelType.FOLDER) {
            if (this.lastFolderEdit == null
                    || this.accountInfo.getLastFolderEdit().compareTo(
                    this.lastFolderEdit) > 0) {
                return true;
            }

            return false;
        }

        if (type == ModelType.GOAL) {
            if (this.lastGoalEdit == null
                    || this.accountInfo.getLastGoalEdit().compareTo(
                    this.lastGoalEdit) > 0) {
                return true;
            }

            return false;
        }

        if (type == ModelType.LOCATION) {
            if (this.lastLocationEdit == null
                    || this.accountInfo.getLastLocationEdit().compareTo(
                    this.lastLocationEdit) > 0) {
                return true;
            }

            return false;
        }

        if (type == ModelType.NOTE) {
            if (this.lastNoteEdit == null
                    || this.accountInfo.getLastNotebookEdit().compareTo(
                    this.lastNoteEdit) > 0) {
                return true;
            }

            return false;
        }

        if (type == ModelType.TASK) {
            if (this.lastTaskEdit == null
                    || this.accountInfo.getLastTaskEdit().compareTo(
                    this.lastTaskEdit) > 0) {
                return true;
            }

            return false;
        }

        return false;
    }

    @Override
    protected List<ModelBean> getUpdatedModels(ModelType type)
            throws SynchronizerException {
        List<ModelBean> beans = new ArrayList<ModelBean>();

        if (type == ModelType.CONTACT) {
            Note note = null;
            for (Note n : NoteFactory.getInstance().getList())
                if (n.getModelStatus().isEndUserStatus())
                    if (n.getTitle().equals("TaskUnifier: Contacts"))
                        note = n;

            try {
                if (note != null) {
                    beans.addAll(Arrays.asList(ContactFactory.getInstance().decodeBeansFromXML(
                            IOUtils.toInputStream(StringEscapeUtils.unescapeHtml4(note.getNote())))));
                }
            } catch (Exception e) {
                PluginLogger.getLogger().log(
                        Level.WARNING,
                        "Cannot decode contacts from note",
                        e);
            }

            return beans;
        }

        if (type == ModelType.CONTEXT) {
            if (this.lastContextEdit == null
                    || this.accountInfo.getLastContextEdit().compareTo(
                    this.lastContextEdit) > 0) {
                beans.addAll(Arrays.asList(this.statement.getContexts(this.accountInfo)));
            }

            return beans;
        }

        if (type == ModelType.FOLDER) {
            if (this.lastFolderEdit == null
                    || this.accountInfo.getLastFolderEdit().compareTo(
                    this.lastFolderEdit) > 0) {
                beans.addAll(Arrays.asList(this.statement.getFolders(this.accountInfo)));
            }

            return beans;
        }

        if (type == ModelType.GOAL) {
            if (this.lastGoalEdit == null
                    || this.accountInfo.getLastGoalEdit().compareTo(
                    this.lastGoalEdit) > 0) {
                beans.addAll(Arrays.asList(this.statement.getGoals(this.accountInfo)));
            }

            return beans;
        }

        if (type == ModelType.LOCATION) {
            if (this.lastLocationEdit == null
                    || this.accountInfo.getLastLocationEdit().compareTo(
                    this.lastLocationEdit) > 0) {
                beans.addAll(Arrays.asList(this.statement.getLocations(this.accountInfo)));
            }

            return beans;
        }

        if (type == ModelType.NOTE) {
            if (this.lastNoteEdit == null
                    || this.accountInfo.getLastNotebookEdit().compareTo(
                    this.lastNoteEdit) > 0) {
                if (this.lastNoteEdit == null) {
                    beans.addAll(Arrays.asList(this.statement.getNotes(this.accountInfo)));
                } else {
                    beans.addAll(Arrays.asList(this.statement.getNotesModifiedAfter(
                            this.accountInfo,
                            this.lastNoteEdit)));
                }
            }

            return beans;
        }

        if (type == ModelType.TASK) {
            if (this.lastTaskEdit == null
                    || this.accountInfo.getLastTaskEdit().compareTo(
                    this.lastTaskEdit) > 0) {
                if (this.lastTaskEdit == null) {
                    beans.addAll(Arrays.asList(this.statement.getTasks(this.accountInfo)));
                } else {
                    beans.addAll(Arrays.asList(this.statement.getTasksModifiedAfter(
                            this.accountInfo,
                            this.lastTaskEdit)));
                }
            }

            return beans;
        }

        return null;
    }

    @Override
    protected List<String> addModels(ModelType type, List<Model> models)
            throws SynchronizerException {
        List<String> ids = new ArrayList<String>();

        if (type == ModelType.CONTACT) {
            for (Model model : models) {
                ids.add(model.getModelId().getId());
            }
        }

        if (type == ModelType.CONTEXT) {
            for (Model model : models) {
                ids.add(this.statement.addContext(
                        this.accountInfo,
                        (Context) model));
            }
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                ids.add(this.statement.addFolder(
                        this.accountInfo,
                        (Folder) model));
            }
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                ids.add(this.statement.addGoal(this.accountInfo, (Goal) model));
            }
        }

        if (type == ModelType.LOCATION) {
            for (Model model : models) {
                ids.add(this.statement.addLocation(
                        this.accountInfo,
                        (Location) model));
            }
        }

        if (type == ModelType.NOTE) {
            List<Note> notes = new ArrayList<Note>();

            for (Model model : models)
                notes.add((Note) model);

            ids.addAll(this.statement.addNotes(
                    this.accountInfo,
                    notes.toArray(new Note[0])));
        }

        if (type == ModelType.TASK) {
            this.addedTasks = new ArrayList<Task>();

            for (Model model : models)
                this.addedTasks.add((Task) model);

            ids.addAll(this.statement.addTasks(
                    this.accountInfo,
                    this.addedTasks.toArray(new Task[0]),
                    false,
                    false));
        }

        return ids;
    }

    @Override
    protected void updateModels(ModelType type, List<Model> models)
            throws SynchronizerException {
        if (type == ModelType.CONTACT) {
            return;
        }

        if (type == ModelType.CONTEXT) {
            for (Model model : models) {
                try {
                    this.statement.editContext(
                            this.accountInfo,
                            (Context) model);
                } catch (ToodledoApiException e) {
                    this.handleUpdateModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                try {
                    this.statement.editFolder(this.accountInfo, (Folder) model);
                } catch (ToodledoApiException e) {
                    this.handleUpdateModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                try {
                    this.statement.editGoal(this.accountInfo, (Goal) model);
                } catch (ToodledoApiException e) {
                    this.handleUpdateModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.LOCATION) {
            for (Model model : models) {
                try {
                    this.statement.editLocation(
                            this.accountInfo,
                            (Location) model);
                } catch (ToodledoApiException e) {
                    this.handleUpdateModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.NOTE) {
            List<Note> notes = new ArrayList<Note>();

            for (Model model : models)
                notes.add((Note) model);

            this.statement.editNotes(
                    this.accountInfo,
                    notes.toArray(new Note[0]));

            return;
        }

        if (type == ModelType.TASK) {
            List<Task> tasks = new ArrayList<Task>();

            for (Model model : models)
                tasks.add((Task) model);

            this.statement.editTasks(
                    this.accountInfo,
                    tasks.toArray(new Task[0]),
                    false,
                    false);

            tasks.addAll(this.addedTasks);

            this.statement.editTasksParent(
                    this.accountInfo,
                    tasks.toArray(new Task[0]));

            this.statement.editTasksMeta(
                    this.accountInfo,
                    tasks.toArray(new Task[0]));

            return;
        }
    }

    @Override
    protected void deleteModels(ModelType type, List<Model> models)
            throws SynchronizerException {
        if (type == ModelType.CONTACT) {
            return;
        }

        if (type == ModelType.CONTEXT) {
            for (Model model : models) {
                try {
                    this.statement.deleteContext(
                            this.accountInfo,
                            (Context) model);
                } catch (ToodledoApiException e) {
                    this.handleDeleteModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                try {
                    this.statement.deleteFolder(
                            this.accountInfo,
                            (Folder) model);
                } catch (ToodledoApiException e) {
                    this.handleDeleteModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                try {
                    this.statement.deleteGoal(this.accountInfo, (Goal) model);
                } catch (ToodledoApiException e) {
                    this.handleDeleteModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.LOCATION) {
            for (Model model : models) {
                try {
                    this.statement.deleteLocation(
                            this.accountInfo,
                            (Location) model);
                } catch (ToodledoApiException e) {
                    this.handleDeleteModelException(e, model);
                }
            }

            return;
        }

        if (type == ModelType.NOTE) {
            List<Note> notes = new ArrayList<Note>();

            for (Model model : models)
                notes.add((Note) model);

            this.statement.deleteNotes(
                    this.accountInfo,
                    notes.toArray(new Note[0]));

            return;
        }

        if (type == ModelType.TASK) {
            List<Task> tasks = new ArrayList<Task>();

            for (Model model : models)
                tasks.add((Task) model);

            this.statement.deleteTasks(
                    this.accountInfo,
                    tasks.toArray(new Task[0]));

            return;
        }
    }

    @Override
    protected List<ModelBean> getDeletedModels(ModelType type)
            throws SynchronizerException {
        List<ModelBean> deletedModels = new ArrayList<ModelBean>();

        if (type == ModelType.CONTACT) {
            return this.getDeletedContacts();
        }

        if (type == ModelType.CONTEXT) {
            if (this.lastContextEdit == null
                    || this.accountInfo.getLastContextEdit().compareTo(
                    this.lastContextEdit) > 0) {
                ContextBean[] beans = this.statement.getContexts(this.accountInfo);
                List<Context> models = ContextFactory.getInstance().getList();

                main:
                for (Context model : models) {
                    if (!model.getModelStatus().isEndUserStatus())
                        continue;

                    for (ContextBean bean : beans)
                        if (EqualsUtils.equalsString(
                                model.getModelReferenceId("toodledo"),
                                bean.getModelReferenceIds().get("toodledo")))
                            continue main;

                    deletedModels.add(model.toBean());
                }
            }
        }

        if (type == ModelType.FOLDER) {
            if (this.lastFolderEdit == null
                    || this.accountInfo.getLastFolderEdit().compareTo(
                    this.lastFolderEdit) > 0) {
                FolderBean[] beans = this.statement.getFolders(this.accountInfo);
                List<Folder> models = FolderFactory.getInstance().getList();

                main:
                for (Folder model : models) {
                    if (!model.getModelStatus().isEndUserStatus())
                        continue;

                    for (FolderBean bean : beans)
                        if (EqualsUtils.equalsString(
                                model.getModelReferenceId("toodledo"),
                                bean.getModelReferenceIds().get("toodledo")))
                            continue main;

                    deletedModels.add(model.toBean());
                }
            }
        }

        if (type == ModelType.GOAL) {
            if (this.lastGoalEdit == null
                    || this.accountInfo.getLastGoalEdit().compareTo(
                    this.lastGoalEdit) > 0) {
                GoalBean[] beans = this.statement.getGoals(this.accountInfo);
                List<Goal> models = GoalFactory.getInstance().getList();

                main:
                for (Goal model : models) {
                    if (!model.getModelStatus().isEndUserStatus())
                        continue;

                    for (GoalBean bean : beans)
                        if (EqualsUtils.equalsString(
                                model.getModelReferenceId("toodledo"),
                                bean.getModelReferenceIds().get("toodledo")))
                            continue main;

                    deletedModels.add(model.toBean());
                }
            }
        }

        if (type == ModelType.LOCATION) {
            if (this.lastLocationEdit == null
                    || this.accountInfo.getLastLocationEdit().compareTo(
                    this.lastLocationEdit) > 0) {
                LocationBean[] beans = this.statement.getLocations(this.accountInfo);
                List<Location> models = LocationFactory.getInstance().getList();

                main:
                for (Location model : models) {
                    if (!model.getModelStatus().isEndUserStatus())
                        continue;

                    for (LocationBean bean : beans)
                        if (EqualsUtils.equalsString(
                                model.getModelReferenceId("toodledo"),
                                bean.getModelReferenceIds().get("toodledo")))
                            continue main;

                    deletedModels.add(model.toBean());
                }
            }
        }

        if (type == ModelType.NOTE) {
            if (this.lastNoteDelete == null
                    || this.accountInfo.getLastNotebookDelete().compareTo(
                    this.lastNoteDelete) > 0) {
                deletedModels.addAll(Arrays.asList(this.statement.getDeletedNotes(
                        this.accountInfo,
                        this.getLastNoteDelete())));
            }
        }

        if (type == ModelType.TASK) {
            if (this.lastTaskDelete == null
                    || this.accountInfo.getLastTaskDelete().compareTo(
                    this.lastTaskDelete) > 0) {
                deletedModels.addAll(Arrays.asList(this.statement.getDeletedTasks(
                        this.accountInfo,
                        this.getLastTaskDelete())));
            }
        }

        return deletedModels;
    }

    private void handleUpdateModelException(
            ToodledoApiException exc,
            Model model) throws SynchronizerException {
        if (exc.getError() != null) {
            if (exc.getError().getCode() != 5)
                throw exc;

            switch (exc.getError().getType()) {
                case CONTEXT:
                case FOLDER:
                case GOAL:
                case LOCATION:
                    return;
            }
        }

        throw exc;
    }

    private void handleDeleteModelException(
            ToodledoApiException exc,
            Model model) throws ToodledoApiException {
        if (exc.getError() != null) {
            if (exc.getError().getCode() != 4)
                throw exc;

            switch (exc.getError().getType()) {
                case CONTEXT:
                case FOLDER:
                case GOAL:
                case LOCATION:
                    return;
            }
        }

        throw exc;
    }

    private void createContactsNote() throws SynchronizerException {
        try {
            PluginApi.invokeAndWait(new Callable<Void>() {

                @Override
                public Void call() {
                    Note note = null;

                    for (Note n : NoteFactory.getInstance().getList())
                        if (n.getModelStatus().isEndUserStatus())
                            if (n.getTitle().equals("TaskUnifier: Contacts"))
                                note = n;

                    if (note == null) {
                        note = NoteFactory.getInstance().create(
                                "TaskUnifier: Contacts");
                    }

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ContactFactory.getInstance().encodeToXML(output);
                    String xml = "<!-- DO NOT EDIT THIS NOTE -->\n"
                            + output.toString();
                    note.setNote(StringEscapeUtils.escapeHtml4(xml));

                    return null;
                }

            });
        } catch (Exception e) {
            throw new SynchronizerException(false, e.getMessage(), e);
        }
    }

    private void createDeletedContactsNote() throws SynchronizerException {
        try {
            PluginApi.invokeAndWait(new Callable<Void>() {

                @Override
                public Void call() {
                    List<ModelBean> deletedContacts = OrganiTaskSynchronizer.this.getDeletedContacts();
                    List<ToodledoDeletedContact> toodledoDeletedContacts = new ArrayList<ToodledoDeletedContact>();

                    for (ModelBean deletedContact : deletedContacts) {
                        ToodledoDeletedContact toodledoDeletedContact = new ToodledoDeletedContact();
                        toodledoDeletedContact.setModelId(deletedContact.getModelId());
                        toodledoDeletedContact.setModelUpdateDate(deletedContact.getModelUpdateDate());

                        toodledoDeletedContacts.add(toodledoDeletedContact);
                    }

                    List<Contact> contacts = ContactFactory.getInstance().getList();
                    for (Contact contact : contacts) {
                        if (contact.getModelStatus() == ModelStatus.TO_DELETE
                                || contact.getModelStatus() == ModelStatus.DELETED) {
                            ToodledoDeletedContact toodledoDeletedContact = null;
                            for (ToodledoDeletedContact deletedContact : toodledoDeletedContacts) {
                                if (contact.getModelId().equals(
                                        deletedContact.getModelId())) {
                                    toodledoDeletedContact = deletedContact;
                                    break;
                                }
                            }

                            if (toodledoDeletedContact != null)
                                continue;

                            toodledoDeletedContact = new ToodledoDeletedContact();
                            toodledoDeletedContact.setModelId(contact.getModelId());
                            toodledoDeletedContact.setModelUpdateDate(contact.getModelUpdateDate());

                            toodledoDeletedContacts.add(toodledoDeletedContact);
                        }
                    }

                    Note note = null;

                    for (Note n : NoteFactory.getInstance().getList())
                        if (n.getModelStatus().isEndUserStatus())
                            if (n.getTitle().equals(
                                    "TaskUnifier: Deleted Contacts"))
                                note = n;

                    if (note == null) {
                        note = NoteFactory.getInstance().create(
                                "TaskUnifier: Deleted Contacts");
                    }

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ToodledoDeletedContact.encodeBeansToXML(
                            output,
                            toodledoDeletedContacts.toArray(new ToodledoDeletedContact[0]));
                    String xml = "<!-- DO NOT EDIT THIS NOTE -->\n"
                            + output.toString();
                    note.setNote(StringEscapeUtils.escapeHtml4(xml));

                    ContactFactory.getInstance().cleanFactory();

                    return null;
                }

            });
        } catch (Exception e) {
            throw new SynchronizerException(false, e.getMessage(), e);
        }
    }

    private List<ModelBean> getDeletedContacts() {
        List<ModelBean> contacts = new ArrayList<ModelBean>();

        Note note = null;
        for (Note n : NoteFactory.getInstance().getList())
            if (n.getModelStatus().isEndUserStatus())
                if (n.getTitle().equals("TaskUnifier: Deleted Contacts"))
                    note = n;

        try {
            if (note != null) {
                ToodledoDeletedContact[] deletedContacts = ToodledoDeletedContact.decodeBeansFromXML(IOUtils.toInputStream(StringEscapeUtils.unescapeHtml4(note.getNote())));

                for (ToodledoDeletedContact deletedContact : deletedContacts) {
                    ContactBean bean = new ContactBean();
                    bean.setModelId(deletedContact.getModelId());
                    bean.setModelStatus(ModelStatus.DELETED);
                    bean.setModelUpdateDate(deletedContact.getModelUpdateDate());
                    contacts.add(bean);
                }
            }
        } catch (Exception e) {
            PluginLogger.getLogger().log(
                    Level.WARNING,
                    "Cannot decode deleted contacts from note",
                    e);
        }

        return contacts;
    }

    @Override
    public void loadParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        this.setLastContextEdit(p.getCalendarProperty("toodledo.synchronizer.last_context_edit"));
        this.setLastFolderEdit(p.getCalendarProperty("toodledo.synchronizer.last_folder_edit"));
        this.setLastGoalEdit(p.getCalendarProperty("toodledo.synchronizer.last_goal_edit"));
        this.setLastLocationEdit(p.getCalendarProperty("toodledo.synchronizer.last_location_edit"));
        this.setLastNoteEdit(p.getCalendarProperty("toodledo.synchronizer.last_note_edit"));
        this.setLastNoteDelete(p.getCalendarProperty("toodledo.synchronizer.last_note_delete"));
        this.setLastTaskEdit(p.getCalendarProperty("toodledo.synchronizer.last_task_edit"));
        this.setLastTaskDelete(p.getCalendarProperty("toodledo.synchronizer.last_task_delete"));
    }

    @Override
    public void saveParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        p.setCalendarProperty(
                "toodledo.synchronizer.last_context_edit",
                this.getLastContextEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_folder_edit",
                this.getLastFolderEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_goal_edit",
                this.getLastGoalEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_location_edit",
                this.getLastLocationEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_note_edit",
                this.getLastNoteEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_note_delete",
                this.getLastNoteDelete());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_task_edit",
                this.getLastTaskEdit());
        p.setCalendarProperty(
                "toodledo.synchronizer.last_task_delete",
                this.getLastTaskDelete());
    }

}
