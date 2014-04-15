/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.settings;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.TaskStatusFactory;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.resources.Resources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Level;

public final class SettingsVersion {

    private SettingsVersion() {

    }

    public static String updateSettings() {
        String version = Main.getSettings().getStringProperty("general.version");

        if (Main.isFirstExecution())
            version = Constants.getVersion();

        final String oldVersion = version;

        if (version == null)
            version = "2.0.0";

        if (version.compareTo("2.4.0") <= 0)
            version = updateSettings_2_4_0_to_3_0_0();

        if (version.compareTo("4.1.0") <= 0)
            version = updateSettings_4_1_0_to_4_1_1();

        cleanSettings();

        Main.getSettings().setStringProperty(
                "general.version",
                Constants.getVersion());

        return oldVersion;
    }

    private static void cleanSettings() {
        try {
            Properties defaultProperties = new Properties();
            defaultProperties.load(Resources.class.getResourceAsStream("default_settings.properties"));

            for (String key : defaultProperties.stringPropertyNames()) {
                String value = defaultProperties.getProperty(key);

                if (value == null || value.length() == 0)
                    continue;

                if (Main.getSettings().getStringProperty(key) == null) {
                    GuiLogger.getLogger().warning("Clean settings: " + key);
                    Main.getSettings().remove(key);
                }
            }
        } catch (Throwable t) {
            GuiLogger.getLogger().log(Level.WARNING, "Cannot clean settings", t);
        }
    }

    private static String updateSettings_2_4_0_to_3_0_0() {
        GuiLogger.getLogger().info(
                "Update settings from version 2.4.0 to 3.0.0");

        Main.getSettings().replaceKey(
                "general.backup.auto_backup_every",
                "backup.auto_backup_every");
        Main.getSettings().replaceKey(
                "general.backup.backup_before_sync",
                "backup.backup_before_sync");
        Main.getSettings().replaceKey(
                "general.backup.keep_backups",
                "backup.keep_backups");

        Main.getSettings().setStringProperty(
                "general.toolbar",
                "ADD_NOTE;ADD_TASK;ADD_SUBTASK;ADD_TEMPLATE_TASK_MENU;DELETE;SEPARATOR;SYNCHRONIZE_AND_PUBLISH;SCHEDULED_SYNC;SEPARATOR;CONFIGURATION");

        Main.getSettings().replaceKey(
                "notecolumn.folder.order",
                "note.column.folder.order");
        Main.getSettings().replaceKey(
                "notecolumn.folder.visible",
                "note.column.folder.visible");
        Main.getSettings().replaceKey(
                "notecolumn.folder.width",
                "note.column.folder.width");
        Main.getSettings().replaceKey(
                "notecolumn.model.order",
                "note.column.model.order");
        Main.getSettings().replaceKey(
                "notecolumn.model.visible",
                "note.column.model.visible");
        Main.getSettings().replaceKey(
                "notecolumn.model.width",
                "note.column.model.width");
        Main.getSettings().replaceKey(
                "notecolumn.model_creation_date.order",
                "note.column.model_creation_date.order");
        Main.getSettings().replaceKey(
                "notecolumn.model_creation_date.visible",
                "note.column.model_creation_date.visible");
        Main.getSettings().replaceKey(
                "notecolumn.model_creation_date.width",
                "note.column.model_creation_date.width");
        Main.getSettings().replaceKey(
                "notecolumn.model_update_date.order",
                "note.column.model_update_date.order");
        Main.getSettings().replaceKey(
                "notecolumn.model_update_date.visible",
                "note.column.model_update_date.visible");
        Main.getSettings().replaceKey(
                "notecolumn.model_update_date.width",
                "note.column.model_update_date.width");
        Main.getSettings().replaceKey(
                "notecolumn.note.order",
                "note.column.note.order");
        Main.getSettings().replaceKey(
                "notecolumn.note.visible",
                "note.column.note.visible");
        Main.getSettings().replaceKey(
                "notecolumn.note.width",
                "note.column.note.width");
        Main.getSettings().replaceKey(
                "notecolumn.title.order",
                "note.column.title.order");
        Main.getSettings().replaceKey(
                "notecolumn.title.visible",
                "note.column.title.visible");
        Main.getSettings().replaceKey(
                "notecolumn.title.width",
                "note.column.title.width");

        Main.getSettings().replaceKey(
                "taskcolumn.contacts.order",
                "task.column.contacts.order");
        Main.getSettings().replaceKey(
                "taskcolumn.contacts.visible",
                "task.column.contacts.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.contacts.width",
                "task.column.contacts.width");
        Main.getSettings().replaceKey(
                "taskcolumn.completed.order",
                "task.column.completed.order");
        Main.getSettings().replaceKey(
                "taskcolumn.completed.visible",
                "task.column.completed.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.completed.width",
                "task.column.completed.width");
        Main.getSettings().replaceKey(
                "taskcolumn.completed_on.order",
                "task.column.completed_on.order");
        Main.getSettings().replaceKey(
                "taskcolumn.completed_on.visible",
                "task.column.completed_on.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.completed_on.width",
                "task.column.completed_on.width");
        Main.getSettings().replaceKey(
                "taskcolumn.context.order",
                "task.column.context.order");
        Main.getSettings().replaceKey(
                "taskcolumn.context.visible",
                "task.column.context.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.context.width",
                "task.column.context.width");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date.order",
                "task.column.due_date.order");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date.visible",
                "task.column.due_date.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date.width",
                "task.column.due_date.width");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date_reminder.order",
                "task.column.due_date_reminder.order");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date_reminder.visible",
                "task.column.due_date_reminder.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.due_date_reminder.width",
                "task.column.due_date_reminder.width");
        Main.getSettings().replaceKey(
                "taskcolumn.files.order",
                "task.column.files.order");
        Main.getSettings().replaceKey(
                "taskcolumn.files.visible",
                "task.column.files.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.files.width",
                "task.column.files.width");
        Main.getSettings().replaceKey(
                "taskcolumn.folder.order",
                "task.column.folder.order");
        Main.getSettings().replaceKey(
                "taskcolumn.folder.visible",
                "task.column.folder.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.folder.width",
                "task.column.folder.width");
        Main.getSettings().replaceKey(
                "taskcolumn.goal.order",
                "task.column.goal.order");
        Main.getSettings().replaceKey(
                "taskcolumn.goal.visible",
                "task.column.goal.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.goal.width",
                "task.column.goal.width");
        Main.getSettings().replaceKey(
                "taskcolumn.importance.order",
                "task.column.importance.order");
        Main.getSettings().replaceKey(
                "taskcolumn.importance.visible",
                "task.column.importance.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.importance.width",
                "task.column.importance.width");
        Main.getSettings().replaceKey(
                "taskcolumn.location.order",
                "task.column.location.order");
        Main.getSettings().replaceKey(
                "taskcolumn.location.visible",
                "task.column.location.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.location.width",
                "task.column.location.width");
        Main.getSettings().replaceKey(
                "taskcolumn.length.order",
                "task.column.length.order");
        Main.getSettings().replaceKey(
                "taskcolumn.length.visible",
                "task.column.length.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.length.width",
                "task.column.length.width");
        Main.getSettings().replaceKey(
                "taskcolumn.model.order",
                "task.column.model.order");
        Main.getSettings().replaceKey(
                "taskcolumn.model.visible",
                "task.column.model.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.model.width",
                "task.column.model.width");
        Main.getSettings().replaceKey(
                "taskcolumn.model_edit.order",
                "task.column.model_edit.order");
        Main.getSettings().replaceKey(
                "taskcolumn.model_edit.visible",
                "task.column.model_edit.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.model_edit.width",
                "task.column.model_edit.width");
        Main.getSettings().replaceKey(
                "taskcolumn.model_creation_date.order",
                "task.column.model_creation_date.order");
        Main.getSettings().replaceKey(
                "taskcolumn.model_creation_date.visible",
                "task.column.model_creation_date.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.model_creation_date.width",
                "task.column.model_creation_date.width");
        Main.getSettings().replaceKey(
                "taskcolumn.model_update_date.order",
                "task.column.model_update_date.order");
        Main.getSettings().replaceKey(
                "taskcolumn.model_update_date.visible",
                "task.column.model_update_date.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.model_update_date.width",
                "task.column.model_update_date.width");
        Main.getSettings().replaceKey(
                "taskcolumn.note.order",
                "task.column.note.order");
        Main.getSettings().replaceKey(
                "taskcolumn.note.visible",
                "task.column.note.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.note.width",
                "task.column.note.width");
        Main.getSettings().replaceKey(
                "taskcolumn.order.order",
                "task.column.order.order");
        Main.getSettings().replaceKey(
                "taskcolumn.order.visible",
                "task.column.order.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.order.width",
                "task.column.order.width");
        Main.getSettings().replaceKey(
                "taskcolumn.parent.order",
                "task.column.parent.order");
        Main.getSettings().replaceKey(
                "taskcolumn.parent.visible",
                "task.column.parent.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.parent.width",
                "task.column.parent.width");
        Main.getSettings().replaceKey(
                "taskcolumn.priority.order",
                "task.column.priority.order");
        Main.getSettings().replaceKey(
                "taskcolumn.priority.visible",
                "task.column.priority.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.priority.width",
                "task.column.priority.width");
        Main.getSettings().replaceKey(
                "taskcolumn.progress.order",
                "task.column.progress.order");
        Main.getSettings().replaceKey(
                "taskcolumn.progress.visible",
                "task.column.progress.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.progress.width",
                "task.column.progress.width");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat.order",
                "task.column.repeat.order");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat.visible",
                "task.column.repeat.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat.width",
                "task.column.repeat.width");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat_from.order",
                "task.column.repeat_from.order");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat_from.visible",
                "task.column.repeat_from.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.repeat_from.width",
                "task.column.repeat_from.width");
        Main.getSettings().replaceKey(
                "taskcolumn.show_children.order",
                "task.column.show_children.order");
        Main.getSettings().replaceKey(
                "taskcolumn.show_children.visible",
                "task.column.show_children.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.show_children.width",
                "task.column.show_children.width");
        Main.getSettings().replaceKey(
                "taskcolumn.star.order",
                "task.column.star.order");
        Main.getSettings().replaceKey(
                "taskcolumn.star.visible",
                "task.column.star.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.star.width",
                "task.column.star.width");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date.order",
                "task.column.start_date.order");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date.visible",
                "task.column.start_date.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date.width",
                "task.column.start_date.width");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date_reminder.order",
                "task.column.start_date_reminder.order");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date_reminder.visible",
                "task.column.start_date_reminder.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.start_date_reminder.width",
                "task.column.start_date_reminder.width");
        Main.getSettings().replaceKey(
                "taskcolumn.status.order",
                "task.column.status.order");
        Main.getSettings().replaceKey(
                "taskcolumn.status.visible",
                "task.column.status.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.status.width",
                "task.column.status.width");
        Main.getSettings().replaceKey(
                "taskcolumn.tags.order",
                "task.column.tags.order");
        Main.getSettings().replaceKey(
                "taskcolumn.tags.visible",
                "task.column.tags.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.tags.width",
                "task.column.tags.width");
        Main.getSettings().replaceKey(
                "taskcolumn.tasks.order",
                "task.column.tasks.order");
        Main.getSettings().replaceKey(
                "taskcolumn.tasks.visible",
                "task.column.tasks.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.tasks.width",
                "task.column.tasks.width");
        Main.getSettings().replaceKey(
                "taskcolumn.timer.order",
                "task.column.timer.order");
        Main.getSettings().replaceKey(
                "taskcolumn.timer.visible",
                "task.column.timer.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.timer.width",
                "task.column.timer.width");
        Main.getSettings().replaceKey(
                "taskcolumn.title.order",
                "task.column.title.order");
        Main.getSettings().replaceKey(
                "taskcolumn.title.visible",
                "task.column.title.visible");
        Main.getSettings().replaceKey(
                "taskcolumn.title.width",
                "task.column.title.width");

        Main.getSettings().replaceKey(
                "taskcontactscolumn.link.order",
                "taskcontacts.column.link.order");
        Main.getSettings().replaceKey(
                "taskcontactscolumn.link.visible",
                "taskcontacts.column.link.visible");
        Main.getSettings().replaceKey(
                "taskcontactscolumn.link.width",
                "taskcontacts.column.link.width");
        Main.getSettings().replaceKey(
                "taskcontactscolumn.contact.order",
                "taskcontacts.column.contact.order");
        Main.getSettings().replaceKey(
                "taskcontactscolumn.contact.visible",
                "taskcontacts.column.contact.visible");
        Main.getSettings().replaceKey(
                "taskcontactscolumn.contact.width",
                "taskcontacts.column.contact.width");

        Main.getSettings().replaceKey(
                "taskfilescolumn.link.order",
                "taskfiles.column.link.order");
        Main.getSettings().replaceKey(
                "taskfilescolumn.link.visible",
                "taskfiles.column.link.visible");
        Main.getSettings().replaceKey(
                "taskfilescolumn.link.width",
                "taskfiles.column.link.width");
        Main.getSettings().replaceKey(
                "taskfilescolumn.file.order",
                "taskfiles.column.file.order");
        Main.getSettings().replaceKey(
                "taskfilescolumn.file.visible",
                "taskfiles.column.file.visible");
        Main.getSettings().replaceKey(
                "taskfilescolumn.file.width",
                "taskfiles.column.file.width");
        Main.getSettings().replaceKey(
                "taskfilescolumn.open.order",
                "taskfiles.column.open.order");
        Main.getSettings().replaceKey(
                "taskfilescolumn.open.visible",
                "taskfiles.column.open.visible");
        Main.getSettings().replaceKey(
                "taskfilescolumn.open.width",
                "taskfiles.column.open.width");

        Main.getSettings().replaceKey(
                "tasktaskscolumn.edit.order",
                "tasktasks.column.edit.order");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.edit.visible",
                "tasktasks.column.edit.visible");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.edit.width",
                "tasktasks.column.edit.width");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.link.order",
                "tasktasks.column.link.order");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.link.visible",
                "tasktasks.column.link.visible");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.link.width",
                "tasktasks.column.link.width");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.select.order",
                "tasktasks.column.select.order");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.select.visible",
                "tasktasks.column.select.visible");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.select.width",
                "tasktasks.column.select.width");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.task.order",
                "tasktasks.column.task.order");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.task.visible",
                "tasktasks.column.task.visible");
        Main.getSettings().replaceKey(
                "tasktaskscolumn.task.width",
                "tasktasks.column.task.width");

        Main.getSettings().replaceKey(
                "window.extended_state",
                "window.main.extended_state");
        Main.getSettings().replaceKey("window.height", "window.main.height");
        Main.getSettings().replaceKey(
                "window.location_x",
                "window.main.location_x");
        Main.getSettings().replaceKey(
                "window.location_y",
                "window.main.location_y");
        Main.getSettings().replaceKey("window.width", "window.main.width");

        return "3.0.0";
    }

    private static String updateSettings_4_1_0_to_4_1_1() {
        Main.getActionSupport().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if ("AFTER_START".equals(actionEvent.getActionCommand())) {
                    String taskStatuses = Main.getSettings().getStringProperty("taskstatuses");

                    String[] items = taskStatuses.split(";");
                    for (String item : items) {
                        TaskStatusFactory.getInstance().create(item.trim());
                    }
                }
            }

        });

        return "4.1.1";
    }

}
