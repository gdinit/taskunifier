/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare;

import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.*;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.plugins.AbstractSynchronizer;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class NetworkShareSynchronizer extends AbstractSynchronizer {

    private List<ContactBean> contactBeans;
    private List<ContextBean> contextBeans;
    private List<FolderBean> folderBeans;
    private List<GoalBean> goalBeans;
    private List<LocationBean> locationBeans;
    private List<NoteBean> noteBeans;
    private List<TaskBean> taskBeans;

    private ModelList<?> notSharedFolders;

    public NetworkShareSynchronizer(NetworkShareConnection connection) {
        super(connection, "networkshare");
    }

    @Override
    public void synchronize(SynchronizerChoice choice, ProgressMonitor monitor)
            throws SynchronizerException {
        this.notSharedFolders = PluginApi.getUserSettings().getObjectProperty(
                "plugin.networkshare.not_shared_folders",
                ModelList.class);

        if (this.notSharedFolders == null)
            this.notSharedFolders = new ModelList<Folder>(ModelType.FOLDER);

        NetworkShareConnection c = (NetworkShareConnection) this.getConnection();

        ZipFile zip = null;

        this.contactBeans = new ArrayList<ContactBean>();
        this.contextBeans = new ArrayList<ContextBean>();
        this.folderBeans = new ArrayList<FolderBean>();
        this.goalBeans = new ArrayList<GoalBean>();
        this.locationBeans = new ArrayList<LocationBean>();
        this.noteBeans = new ArrayList<NoteBean>();
        this.taskBeans = new ArrayList<TaskBean>();

        try {
            for (String f : c.getSharedFiles()) {
                zip = new ZipFile(f);

                for (Enumeration<?> e = zip.getEntries(); e.hasMoreElements(); ) {
                    ZipArchiveEntry entry = (ZipArchiveEntry) e.nextElement();

                    if (entry.getName().equals("contacts.xml"))
                        this.addIfNewer(
                                this.contactBeans,
                                ContactFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("contexts.xml"))
                        this.addIfNewer(
                                this.contextBeans,
                                ContextFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("folders.xml"))
                        this.addIfNewer(
                                this.folderBeans,
                                FolderFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("goals.xml"))
                        this.addIfNewer(
                                this.goalBeans,
                                GoalFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("locations.xml"))
                        this.addIfNewer(
                                this.locationBeans,
                                LocationFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("notes.xml"))
                        this.addIfNewer(
                                this.noteBeans,
                                NoteFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));

                    if (entry.getName().equals("tasks.xml"))
                        this.addIfNewer(
                                this.taskBeans,
                                TaskFactory.getInstance().decodeBeansFromXML(
                                        zip.getInputStream(entry)));
                }
            }
        } catch (Exception e) {
            throw new SynchronizerException(false, e.getMessage(), e);
        } finally {
            try {
                zip.close();
            } catch (Exception e) {

            }
        }

        this.synchronizeModels(choice, monitor, new ModelType[]{
                ModelType.CONTACT,
                ModelType.CONTEXT,
                ModelType.FOLDER,
                ModelType.GOAL,
                ModelType.LOCATION,
                ModelType.NOTE,
                ModelType.TASK});

        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(new FileOutputStream(
                    c.getCurrentUserSharedFile()));
            ByteArrayOutputStream output = null;

            // CONTACTS
            output = new ByteArrayOutputStream();
            ContactFactory.getInstance().encodeToXML(output);

            this.writeIntoZip(zos, "contacts.xml", new ByteArrayInputStream(
                    output.toByteArray()));

            // CONTEXTS
            output = new ByteArrayOutputStream();
            ContextFactory.getInstance().encodeToXML(output);

            this.writeIntoZip(zos, "contexts.xml", new ByteArrayInputStream(
                    output.toByteArray()));

            // FOLDERS
            output = new ByteArrayOutputStream();

            List<FolderBean> folderBeans = new ArrayList<FolderBean>();
            main:
            for (Folder folder : FolderFactory.getInstance().getList()) {
                for (Object notSharedFolder : this.notSharedFolders.getList()) {
                    if (EqualsUtils.equals(folder, notSharedFolder))
                        continue main;
                }

                folderBeans.add(folder.toBean());
            }

            FolderFactory.getInstance().encodeBeansToXML(
                    output,
                    folderBeans.toArray(new FolderBean[0]));

            this.writeIntoZip(zos, "folders.xml", new ByteArrayInputStream(
                    output.toByteArray()));

            // GOALS
            output = new ByteArrayOutputStream();
            GoalFactory.getInstance().encodeToXML(output);

            this.writeIntoZip(
                    zos,
                    "goals.xml",
                    new ByteArrayInputStream(output.toByteArray()));

            // LOCATIONS
            output = new ByteArrayOutputStream();
            LocationFactory.getInstance().encodeToXML(output);

            this.writeIntoZip(zos, "locations.xml", new ByteArrayInputStream(
                    output.toByteArray()));

            // NOTES
            output = new ByteArrayOutputStream();

            List<NoteBean> noteBeans = new ArrayList<NoteBean>();
            main:
            for (Note note : NoteFactory.getInstance().getList()) {
                for (Object notSharedFolder : this.notSharedFolders.getList()) {
                    if (EqualsUtils.equals(note.getFolder(), notSharedFolder))
                        continue main;
                }

                noteBeans.add(note.toBean());
            }

            NoteFactory.getInstance().encodeBeansToXML(
                    output,
                    noteBeans.toArray(new NoteBean[0]));

            this.writeIntoZip(
                    zos,
                    "notes.xml",
                    new ByteArrayInputStream(output.toByteArray()));

            // TASKS
            output = new ByteArrayOutputStream();

            List<TaskBean> taskBeans = new ArrayList<TaskBean>();
            main:
            for (Task task : TaskFactory.getInstance().getList()) {
                for (Object notSharedFolder : this.notSharedFolders.getList()) {
                    if (EqualsUtils.equals(task.getFolder(), notSharedFolder))
                        continue main;
                }

                taskBeans.add(task.toBean());
            }

            TaskFactory.getInstance().encodeBeansToXML(
                    output,
                    taskBeans.toArray(new TaskBean[0]));

            this.writeIntoZip(
                    zos,
                    "tasks.xml",
                    new ByteArrayInputStream(output.toByteArray()));
        } catch (Exception e) {
            throw new SynchronizerException(false, e.getMessage(), e);
        } finally {
            try {
                zos.close();
            } catch (Exception e) {

            }
        }
    }

    private <M extends ModelBean> void addIfNewer(List<M> list, M[] models) {
        for (M model : models) {
            M existingModel = null;

            for (M m : list) {
                if (EqualsUtils.equals(model.getModelId(), m.getModelId())) {
                    existingModel = m;
                    break;
                }
            }

            if (existingModel == null) {
                list.add(model);
                continue;
            }

            if (existingModel.getModelUpdateDate().compareTo(
                    model.getModelUpdateDate()) < 0) {
                list.remove(existingModel);
                list.add(model);
                continue;
            }
            ;
        }
    }

    private void writeIntoZip(
            ZipOutputStream output,
            String name,
            InputStream input) throws Exception {
        output.putNextEntry(new ZipEntry(name));

        int size = 0;
        byte[] buffer = new byte[1024];

        while ((size = input.read(buffer, 0, buffer.length)) > 0) {
            output.write(buffer, 0, size);
        }

        output.closeEntry();
        input.close();
    }

    @Override
    public void loadParameters(Properties properties) {

    }

    @Override
    public void saveParameters(Properties properties) {

    }

    @Override
    public SynchronizerPlugin getPlugin() {
        return PluginApi.getPlugin(NetworkSharePlugin.ID);
    }

    @Override
    protected boolean isTreeModelType(ModelType type) {
        switch (type) {
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
        List<ModelBean> list = new ArrayList<ModelBean>();

        switch (type) {
            case CONTACT:
                for (ModelBean bean : this.contactBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case CONTEXT:
                for (ModelBean bean : this.contextBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case FOLDER:
                for (ModelBean bean : this.folderBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case GOAL:
                for (ModelBean bean : this.goalBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case LOCATION:
                for (ModelBean bean : this.locationBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case NOTE:
                for (ModelBean bean : this.noteBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
            case TASK:
                for (ModelBean bean : this.taskBeans)
                    if (bean.getModelStatus().isEndUserStatus())
                        list.add(bean);
                break;
        }

        return list;
    }

    @Override
    protected List<ModelBean> getDeletedModels(ModelType type)
            throws SynchronizerException {
        List<ModelBean> list = new ArrayList<ModelBean>();

        switch (type) {
            case CONTACT:
                for (ModelBean bean : this.contactBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case CONTEXT:
                for (ModelBean bean : this.contextBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case FOLDER:
                for (ModelBean bean : this.folderBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case GOAL:
                for (ModelBean bean : this.goalBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case LOCATION:
                for (ModelBean bean : this.locationBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case NOTE:
                for (ModelBean bean : this.noteBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
            case TASK:
                for (ModelBean bean : this.taskBeans)
                    if (bean.getModelStatus() == ModelStatus.TO_DELETE
                            || bean.getModelStatus() == ModelStatus.DELETED)
                        list.add(bean);
                break;
        }

        return list;
    }

    @Override
    protected List<String> addModels(ModelType type, List<Model> models)
            throws SynchronizerException {
        List<String> list = new ArrayList<String>();

        for (Model model : models)
            list.add(model.getModelId().getId());

        return list;
    }

    @Override
    protected void updateModels(ModelType type, List<Model> models)
            throws SynchronizerException {

    }

    @Override
    protected void deleteModels(ModelType type, List<Model> models)
            throws SynchronizerException {

    }

}
