/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.components.synchronize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.event.listchange.ListChangeListener;
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
import com.leclercb.taskunifier.gui.actions.ActionSave;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.swing.TUStopableWorker;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.BackupUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class SynchronizerWorker extends TUStopableWorker<Void> {
	
	public enum Type {
		
		PUBLISH,
		SYNCHRONIZE;
		
	}
	
	private static int NO_LICENSE_COUNT = 0;
	
	private List<SynchronizerGuiPlugin> plugins;
	private List<Type> types;
	
	private boolean silent;
	private ListChangeListener handler;
	
	public SynchronizerWorker(boolean silent) {
		this(silent, null);
	}
	
	public SynchronizerWorker(boolean silent, ListChangeListener handler) {
		super(Constants.PROGRESS_MONITOR);
		
		this.plugins = new ArrayList<SynchronizerGuiPlugin>();
		this.types = new ArrayList<Type>();
		
		this.silent = silent;
		this.handler = handler;
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
	public synchronized void stop() {
		if (!this.isStopped()) {
			this.getMonitor().addMessage(
					new SynchronizerDefaultProgressMessage(
							Translations.getString("synchronizer.cancelled_by_user"),
							ImageUtils.getResourceImage("cancel.png", 16, 16)));
		}
		
		super.stop();
	}
	
	@Override
	protected final Void longTask() throws Exception {
		GuiLogger.getLogger().log(Level.INFO, "Synchronization started");
		
		Synchronizing.getInstance().setSynchronizing(true);
		
		if (this.handler != null)
			this.getMonitor().addListChangeListener(this.handler);
		
		boolean noLicense = false;
		SynchronizerGuiPlugin plugin = null;
		
		try {
			TUSwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					Constants.UNDO_SUPPORT.discardAllEdits();
				}
				
			});
			
			SynchronizerUtils.setTaskRepeatEnabled(false);
			
			for (int i = 0; i < this.plugins.size(); i++) {
				plugin = this.plugins.get(i);
				Type type = this.types.get(i);
				
				GuiLogger.getLogger().log(
						Level.INFO,
						"Synchronization ongoing ("
								+ type
								+ ") with plugin "
								+ plugin.getId());
				
				if (type == Type.SYNCHRONIZE
						&& Main.getSettings().getBooleanProperty(
								"backup.backup_before_sync")) {
					TUSwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							BackupUtils.getInstance().createNewBackup(
									Translations.getString("manage_backups.before_sync_backup_name"));
							ActionSave.save();
						}
						
					});
				}
				
				SynchronizerUtils.initializeProxy(plugin);
				
				if (plugin.needsLicense()) {
					boolean betaExp = false;
					if (Constants.BETA_SYNC_EXP != null) {
						Calendar betaExpDate = Calendar.getInstance();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");
						betaExpDate.setTime(dateFormat.parse(Constants.BETA_SYNC_EXP));
						betaExp = Calendar.getInstance().compareTo(betaExpDate) > 0;
					}
					
					if (!Constants.BETA || (Constants.BETA && betaExp)) {
						this.publish(new SynchronizerDefaultProgressMessage(
								Translations.getString(
										"synchronizer.checking_license",
										plugin.getSynchronizerApi().getApiName()),
								ImageUtils.getResourceImage("key.png", 16, 16)));
						
						if (!plugin.checkLicense()) {
							noLicense = true;
							
							String key = "synchronizer.no_license_count."
									+ plugin.getId();
							int noLicenseCount = Main.getSettings().getIntegerProperty(
									key,
									0);
							noLicenseCount++;
							Main.getSettings().setIntegerProperty(
									key,
									noLicenseCount);
							
							if (noLicenseCount > Constants.MAX_NO_LICENSE_SYNCS) {
								this.publish(new SynchronizerDefaultProgressMessage(
										Translations.getString("synchronizer.max_no_license_syncs_reached"),
										ImageUtils.getResourceImage(
												"error.png",
												16,
												16)));
								
								this.publish(new SynchronizerDefaultProgressMessage(
										"----------"));
								
								continue;
							} else {
								this.publish(new SynchronizerDefaultProgressMessage(
										Translations.getString(
												"synchronizer.max_no_license_syncs_left",
												Constants.MAX_NO_LICENSE_SYNCS
														- noLicenseCount),
										ImageUtils.getResourceImage(
												"key.png",
												16,
												16)));
							}
							
							int waitTime = Constants.WAIT_NO_LICENSE_TIME;
							
							waitTime += NO_LICENSE_COUNT
									* Constants.WAIT_NO_LICENSE_ADDED_TIME;
							
							this.publish(new SynchronizerDefaultProgressMessage(
									Translations.getString(
											"synchronizer.wait_no_license",
											waitTime),
									ImageUtils.getResourceImage(
											"clock.png",
											16,
											16)));
							
							Thread.sleep(waitTime * 1000);
							
							if (this.isStopped())
								return null;
						}
					}
				}
				
				this.publish(new SynchronizerDefaultProgressMessage(
						Translations.getString(
								"synchronizer.connecting",
								plugin.getSynchronizerApi().getApiName()),
						ImageUtils.getResourceImage("connection.png", 16, 16)));
				
				Connection connection = null;
				
				try {
					connection = plugin.getSynchronizerApi().getConnection(
							Main.getUserSettings());
				} catch (SynchronizerException e) {
					SynchronizerWorker.this.handleSynchronizerException(
							e,
							plugin);
					
					return null;
				}
				
				connection.loadParameters(Main.getUserSettings());
				
				final Connection finalConnection = connection;
				final SynchronizerGuiPlugin finalPlugin = plugin;
				this.executeNonAtomicAction(new Runnable() {
					
					@Override
					public void run() {
						try {
							finalConnection.connect();
						} catch (final SynchronizerException e) {
							if (SynchronizerWorker.this.isStopped())
								return;
							
							SynchronizerWorker.this.handleSynchronizerException(
									e,
									finalPlugin);
							SynchronizerWorker.this.stop();
						} catch (final Throwable t) {
							if (SynchronizerWorker.this.isStopped())
								return;
							
							SynchronizerWorker.this.handleThrowable(t);
							SynchronizerWorker.this.stop();
						}
					};
					
				});
				
				try {
					if (this.isStopped())
						return null;
					
					TUSwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							finalConnection.saveParameters(Main.getUserSettings());
						}
						
					});
					
					final Synchronizer synchronizer = plugin.getSynchronizerApi().getSynchronizer(
							Main.getUserSettings(),
							connection);
					
					synchronizer.loadParameters(Main.getUserSettings());
					
					try {
						if (type == Type.PUBLISH) {
							synchronizer.publish(this.getEDTMonitor());
						} else if (type == Type.SYNCHRONIZE) {
							SynchronizerChoice choice = Main.getUserSettings().getEnumProperty(
									"synchronizer.choice",
									SynchronizerChoice.class);
							
							synchronizer.synchronize(
									choice,
									this.getEDTMonitor());
						}
						
						TUSwingUtilities.invokeAndWait(new Runnable() {
							
							@Override
							public void run() {
								synchronizer.saveParameters(Main.getUserSettings());
							}
							
						});
					} catch (SynchronizerException e) {
						SynchronizerWorker.this.handleSynchronizerException(
								e,
								plugin);
						
						return null;
					}
				} finally {
					connection.disconnect();
				}
				
				TUSwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						Main.getUserSettings().setCalendarProperty(
								"synchronizer.last_synchronization_date",
								Calendar.getInstance());
					}
					
				});
				
				this.publish(new SynchronizerDefaultProgressMessage(
						"----------"));
			}
			
			this.publish(new SynchronizerDefaultProgressMessage(
					Translations.getString("synchronizer.synchronization_fully_completed"),
					ImageUtils.getResourceImage("success.png", 16, 16)));
			
			if (noLicense)
				NO_LICENSE_COUNT++;
		} catch (InterruptedException e) {
			return null;
		} catch (Throwable t) {
			this.handleThrowable(t);
			return null;
		}
		
		Thread.sleep(1000);
		
		TUSwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Main.getUserSettings().setStringProperty(
						"synchronizer.scheduler_sleep_time",
						Main.getUserSettings().getStringProperty(
								"synchronizer.scheduler_sleep_time"),
						true);
			}
			
		});
		
		return null;
	}
	
	@Override
	protected void done() {
		try {
			if (this.handler != null)
				this.getMonitor().removeListChangeListener(this.handler);
			
			Constants.PROGRESS_MONITOR.clear();
			
			SynchronizerUtils.removeOldCompletedTasks();
			
			SynchronizerUtils.setTaskRepeatEnabled(true);
		} finally {
			Synchronizing.getInstance().setSynchronizing(false);
			
			GuiLogger.getLogger().log(Level.INFO, "Synchronization ended");
		}
		
		super.done();
	}
	
	private void handleSynchronizerException(
			final SynchronizerException e,
			final SynchronizerGuiPlugin plugin) {
		if (this.isStopped())
			return;
		
		this.publish(new SynchronizerDefaultProgressMessage(
				e.getMessage(),
				ImageUtils.getResourceImage("error.png", 16, 16)));
		
		if (!this.silent || !(e instanceof SynchronizerNotConnectedException)) {
			TUSwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
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
								FrameUtils.getCurrentFrame(),
								info);
						
						if (e instanceof SynchronizerSettingsException)
							ActionPluginConfiguration.pluginConfiguration(plugin);
					} catch (Exception e) {
						GuiLogger.getLogger().log(
								Level.WARNING,
								e.getMessage(),
								e);
					}
				}
				
			});
		}
	}
	
	private void handleThrowable(final Throwable t) {
		if (this.isStopped())
			return;
		
		this.publish(new SynchronizerDefaultProgressMessage(
				t.getMessage(),
				ImageUtils.getResourceImage("error.png", 16, 16)));
		
		if (!this.silent) {
			TUSwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
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
								FrameUtils.getCurrentFrame(),
								info);
					} catch (Exception e) {
						GuiLogger.getLogger().log(
								Level.WARNING,
								e.getMessage(),
								e);
					}
				}
				
			});
		}
	}
	
}
