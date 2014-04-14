/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.ContactBean;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.plugins.AbstractSynchronizer;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskAuthInfo;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskDeletedContact;
import com.leclercb.taskunifier.plugin.organitask.calls.OrganiTaskStatement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;

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
    protected boolean isTreeModelType(ModelType type) {
        switch (type) {
            case CONTEXT:
            case FOLDER:
            case GOAL:
            case TASK:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected boolean isUpdatedModels(ModelType type)
            throws SynchronizerException {
        return true;
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
            beans.addAll(Arrays.asList(this.statement.getContexts(this.lastSync)));
            return beans;
        }

        if (type == ModelType.FOLDER) {
            beans.addAll(Arrays.asList(this.statement.getFolders(this.lastSync)));
            return beans;
        }

        if (type == ModelType.GOAL) {
            beans.addAll(Arrays.asList(this.statement.getGoals(this.lastSync)));
            return beans;
        }

        if (type == ModelType.NOTE) {
            beans.addAll(Arrays.asList(this.statement.getNotes(this.lastSync)));
            return beans;
        }

        if (type == ModelType.TASK) {
            beans.addAll(Arrays.asList(this.statement.getTasks(this.lastSync)));
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
                ids.add(this.statement.addContext((Context) model, false).getModelReferenceIds().get("organitask"));
            }
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                ids.add(this.statement.addFolder((Folder) model, false).getModelReferenceIds().get("organitask"));
            }
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                ids.add(this.statement.addGoal((Goal) model, false).getModelReferenceIds().get("organitask"));
            }
        }

        if (type == ModelType.NOTE) {
            for (Model model : models) {
                ids.add(this.statement.addNote((Note) model, false).getModelReferenceIds().get("organitask"));
            }
        }

        if (type == ModelType.TASK) {
            for (Model model : models) {
                ids.add(this.statement.addTask((Task) model, false).getModelReferenceIds().get("organitask"));
            }
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
                this.statement.editContext((Context) model, false);
            }

            for (Model model : models) {
                this.statement.editContextParent((Context) model);
            }

            return;
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                this.statement.editFolder((Folder) model, false);
            }

            for (Model model : models) {
                this.statement.editFolderParent((Folder) model);
            }

            return;
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                this.statement.editGoal((Goal) model, false);
            }

            for (Model model : models) {
                this.statement.editGoalParent((Goal) model);
            }

            return;
        }

        if (type == ModelType.NOTE) {
            for (Model model : models) {
                this.statement.editNote((Note) model, false);
            }

            for (Model model : models) {
                this.statement.editNoteParent((Note) model);
            }

            return;
        }

        if (type == ModelType.TASK) {
            for (Model model : models) {
                this.statement.editTask((Task) model, false);
            }

            for (Model model : models) {
                this.statement.editTaskParent((Task) model);
            }

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
                this.statement.deleteContext((Context) model);
            }

            return;
        }

        if (type == ModelType.FOLDER) {
            for (Model model : models) {
                this.statement.deleteFolder((Folder) model);
            }

            return;
        }

        if (type == ModelType.GOAL) {
            for (Model model : models) {
                this.statement.deleteGoal((Goal) model);
            }

            return;
        }

        if (type == ModelType.NOTE) {
            for (Model model : models) {
                this.statement.deleteNote((Note) model);
            }

            return;
        }

        if (type == ModelType.TASK) {
            for (Model model : models) {
                this.statement.deleteTask((Task) model);
            }

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
            deletedModels.addAll(Arrays.asList(this.statement.getDeletedContexts()));
        }

        if (type == ModelType.FOLDER) {
            deletedModels.addAll(Arrays.asList(this.statement.getDeletedFolders()));
        }

        if (type == ModelType.GOAL) {
            deletedModels.addAll(Arrays.asList(this.statement.getDeletedGoals()));
        }

        if (type == ModelType.NOTE) {
            deletedModels.addAll(Arrays.asList(this.statement.getDeletedNotes()));
        }

        if (type == ModelType.TASK) {
            deletedModels.addAll(Arrays.asList(this.statement.getDeletedTasks()));
        }

        return deletedModels;
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
                    List<OrganiTaskDeletedContact> organitaskDeletedContacts = new ArrayList<OrganiTaskDeletedContact>();

                    for (ModelBean deletedContact : deletedContacts) {
                        OrganiTaskDeletedContact organitaskDeletedContact = new OrganiTaskDeletedContact();
                        organitaskDeletedContact.setModelId(deletedContact.getModelId());
                        organitaskDeletedContact.setModelUpdateDate(deletedContact.getModelUpdateDate());

                        organitaskDeletedContacts.add(organitaskDeletedContact);
                    }

                    List<Contact> contacts = ContactFactory.getInstance().getList();
                    for (Contact contact : contacts) {
                        if (contact.getModelStatus() == ModelStatus.TO_DELETE
                                || contact.getModelStatus() == ModelStatus.DELETED) {
                            OrganiTaskDeletedContact organitaskDeletedContact = null;
                            for (OrganiTaskDeletedContact deletedContact : organitaskDeletedContacts) {
                                if (contact.getModelId().equals(
                                        deletedContact.getModelId())) {
                                    organitaskDeletedContact = deletedContact;
                                    break;
                                }
                            }

                            if (organitaskDeletedContact != null)
                                continue;

                            organitaskDeletedContact = new OrganiTaskDeletedContact();
                            organitaskDeletedContact.setModelId(contact.getModelId());
                            organitaskDeletedContact.setModelUpdateDate(contact.getModelUpdateDate());

                            organitaskDeletedContacts.add(organitaskDeletedContact);
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
                    OrganiTaskDeletedContact.encodeBeansToXML(
                            output,
                            organitaskDeletedContacts.toArray(new OrganiTaskDeletedContact[0]));
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
                OrganiTaskDeletedContact[] deletedContacts = OrganiTaskDeletedContact.decodeBeansFromXML(IOUtils.toInputStream(StringEscapeUtils.unescapeHtml4(note.getNote())));

                for (OrganiTaskDeletedContact deletedContact : deletedContacts) {
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

        this.setLastSync(p.getCalendarProperty("plugin.organitask.synchronizer.last_sync"));
    }

    @Override
    public void saveParameters(Properties properties) {
        PropertyMap p = new PropertyMap(properties);

        p.setCalendarProperty(
                "plugin.organitask.synchronizer.last_sync",
                this.getLastSync());
    }

}
