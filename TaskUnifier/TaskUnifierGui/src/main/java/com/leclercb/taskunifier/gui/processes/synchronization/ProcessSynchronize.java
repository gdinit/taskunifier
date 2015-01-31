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
package com.leclercb.taskunifier.gui.processes.synchronization;

import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerNotConnectedException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerSettingsException;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerDefaultProgressMessage;
import com.leclercb.taskunifier.gui.actions.ActionPluginConfiguration;
import com.leclercb.taskunifier.gui.actions.ActionRefresh;
import com.leclercb.taskunifier.gui.actions.ActionSave;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.BackupUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ProcessSynchronize implements Process<Void> {

    public enum Type {

        PUBLISH,
        SYNCHRONIZE;

    }

    private List<SynchronizerGuiPlugin> plugins;
    private List<Type> types;

    public ProcessSynchronize() {
        this.plugins = new ArrayList<SynchronizerGuiPlugin>();
        this.types = new ArrayList<Type>();
    }

    public void add(SynchronizerGuiPlugin plugin, Type type) {
        CheckUtils.isNotNull(plugin);
        CheckUtils.isNotNull(type);

        if (type == Type.PUBLISH && !plugin.isPublisher())
            throw new IllegalArgumentException();

        if (type == Type.SYNCHRONIZE && !plugin.isSynchronizer())
            throw new IllegalArgumentException();

        this.plugins.add(plugin);
        this.types.add(type);
    }

    @Override
    public Void execute(final Worker<?> worker) throws Exception {
        final ProgressMonitor monitor = worker.getEDTMonitor();

        GuiLogger.getLogger().log(Level.INFO, "Synchronization started");

        Synchronizing.getInstance().setSynchronizing(true);

        SynchronizerGuiPlugin plugin = null;

        try {
            ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                @Override
                public Void call() {
                    Constants.UNDO_SUPPORT.discardAllEdits();

                    return null;
                }

            });

            SynchronizerUtils.setTaskRepeatEnabled(false);
            SynchronizerUtils.setTaskRulesEnabled(false);

            for (int i = 0; i < this.plugins.size(); i++) {
                plugin = this.plugins.get(i);
                Type type = this.types.get(i);

                GuiLogger.getLogger().log(
                        Level.INFO,
                        "Synchronization ongoing ("
                                + type
                                + ") with plugin "
                                + plugin.getId());

                if (type == Type.SYNCHRONIZE) {
                    ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                        @Override
                        public Void call() {
                            ActionSave.save();

                            return null;
                        }

                    });

                    if (Main.getSettings().getBooleanProperty("backup.backup_before_sync")) {
                        ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                            @Override
                            public Void call() {
                                BackupUtils.getInstance().createNewBackup(
                                        Translations.getString("manage_backups.before_sync_backup_name"));

                                return null;
                            }

                        });
                    }
                }

                SynchronizerUtils.initializeProxy(plugin);

                monitor.addMessage(new SynchronizerDefaultProgressMessage(
                        Translations.getString(
                                "synchronizer.connecting",
                                plugin.getSynchronizerApi().getApiName()),
                        ImageUtils.getResourceImage("connection.png", 16, 16)));

                Connection connection;

                try {
                    connection = plugin.getSynchronizerApi().getConnection(
                            Main.getUserSettings());
                } catch (SynchronizerException e) {
                    ProcessSynchronize.this.handleSynchronizerException(
                            worker,
                            e,
                            plugin);

                    return null;
                }

                connection.loadParameters(Main.getUserSettings());

                final Connection finalConnection = connection;
                final SynchronizerGuiPlugin finalPlugin = plugin;

                try {
                    worker.executeInterruptibleAction(new Callable<Void>() {

                        @Override
                        public Void call() throws Exception {
                            finalConnection.connect();
                            return null;
                        }

                    });
                } catch (ExecutionException e) {
                    try {
                        throw e.getCause();
                    } catch (final SynchronizerException se) {
                        if (!worker.isCancelled()) {
                            ProcessSynchronize.this.handleSynchronizerException(
                                    worker,
                                    se,
                                    finalPlugin);
                            worker.cancel();
                        }
                    } catch (final Throwable t) {
                        if (!worker.isCancelled()) {
                            ProcessSynchronize.this.handleThrowable(worker, t);
                            worker.cancel();
                        }
                    }
                }

                if (worker.isCancelled())
                    return null;

                try {
                    ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                        @Override
                        public Void call() {
                            finalConnection.saveParameters(Main.getUserSettings());

                            return null;
                        }

                    });

                    final Synchronizer synchronizer = plugin.getSynchronizerApi().getSynchronizer(
                            Main.getUserSettings(),
                            connection);

                    synchronizer.loadParameters(Main.getUserSettings());

                    try {
                        if (type == Type.PUBLISH) {
                            synchronizer.publish(monitor);
                        } else if (type == Type.SYNCHRONIZE) {
                            SynchronizerChoice choice = Main.getUserSettings().getEnumProperty(
                                    "synchronizer.choice",
                                    SynchronizerChoice.class);

                            synchronizer.synchronize(choice, monitor);
                        }

                        ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                            @Override
                            public Void call() {
                                synchronizer.saveParameters(Main.getUserSettings());

                                return null;
                            }

                        });
                    } catch (SynchronizerException e) {
                        ProcessSynchronize.this.handleSynchronizerException(
                                worker,
                                e,
                                plugin);

                        return null;
                    }
                } finally {
                    connection.disconnect();
                }

                monitor.addMessage(new SynchronizerDefaultProgressMessage(
                        "----------"));

                if (worker.isCancelled())
                    return null;
            }

            monitor.addMessage(new SynchronizerDefaultProgressMessage(
                    Translations.getString("synchronizer.synchronization_fully_completed"),
                    ImageUtils.getResourceImage("success.png", 16, 16)));
        } catch (InterruptedException e) {
            return null;
        } catch (Throwable t) {
            this.handleThrowable(worker, t);
            return null;
        }

        return null;
    }

    @Override
    public void done(final Worker<?> worker) {
        try {
            SynchronizerUtils.removeOldCompletedTasks();

            SynchronizerUtils.setTaskRepeatEnabled(true);
            SynchronizerUtils.setTaskRulesEnabled(true);

            ActionRefresh.refresh();

            Main.getUserSettings().setObjectProperty(
                    "synchronizer.scheduler_sleep_time",
                    String.class,
                    Main.getUserSettings().getStringProperty(
                            "synchronizer.scheduler_sleep_time"),
                    true);

            Main.getUserSettings().setCalendarProperty(
                    "synchronizer.last_synchronization_date",
                    Calendar.getInstance());
        } finally {
            Synchronizing.getInstance().setSynchronizing(false);

            GuiLogger.getLogger().log(Level.INFO, "Synchronization ended");
        }
    }

    private void handleSynchronizerException(
            final Worker<?> worker,
            final SynchronizerException e,
            final SynchronizerGuiPlugin plugin) throws Exception {
        final ProgressMonitor monitor = worker.getEDTMonitor();

        if (worker.isCancelled())
            return;

        monitor.addMessage(new SynchronizerDefaultProgressMessage(
                e.getMessage(),
                ImageUtils.getResourceImage("error.png", 16, 16)));

        if (!worker.isSilent()
                || !(e instanceof SynchronizerNotConnectedException)) {
            ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                @Override
                public Void call() {
                    try {
                        PluginLogger.getLogger().log(
                                (e.isExpected() ? Level.INFO : Level.WARNING),
                                e.getMessage(),
                                e);

                        ErrorInfo info = new ErrorInfo(
                                Translations.getString("general.error"),
                                e.getMessage(),
                                null,
                                "GUI",
                                (e.isExpected() ? null : e),
                                (e.isExpected() ? Level.INFO : Level.WARNING),
                                null);

                        JXErrorPane.showDialog(
                                FrameUtils.getCurrentWindow(),
                                info);

                        if (e instanceof SynchronizerSettingsException)
                            ActionPluginConfiguration.pluginConfiguration(plugin);
                    } catch (Exception e) {
                        GuiLogger.getLogger().log(
                                Level.WARNING,
                                e.getMessage(),
                                e);
                    }

                    return null;
                }

            });
        }
    }

    private void handleThrowable(final Worker<?> worker, final Throwable t)
            throws Exception {
        final ProgressMonitor monitor = worker.getEDTMonitor();

        if (worker.isCancelled())
            return;

        monitor.addMessage(new SynchronizerDefaultProgressMessage(
                t.getMessage(),
                ImageUtils.getResourceImage("error.png", 16, 16)));

        if (!worker.isSilent()) {
            ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {

                @Override
                public Void call() {
                    try {
                        PluginLogger.getLogger().log(
                                Level.WARNING,
                                t.getMessage(),
                                t);

                        ErrorInfo info = new ErrorInfo(
                                Translations.getString("general.error"),
                                t.getMessage(),
                                null,
                                "GUI",
                                t,
                                Level.WARNING,
                                null);

                        JXErrorPane.showDialog(
                                FrameUtils.getCurrentWindow(),
                                info);
                    } catch (Exception e) {
                        GuiLogger.getLogger().log(
                                Level.WARNING,
                                e.getMessage(),
                                e);
                    }

                    return null;
                }

            });
        }
    }

}
