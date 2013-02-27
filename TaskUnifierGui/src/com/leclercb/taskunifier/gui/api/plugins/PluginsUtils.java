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
package com.leclercb.taskunifier.gui.api.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginExceptionType;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.processes.plugins.ProcessLoadAndUpdatePluginsFromXml;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public final class PluginsUtils {
	
	private PluginsUtils() {
		
	}
	
	public static Plugin getDummyPlugin() {
		return DUMMY_PLUGIN;
	}
	
	private static Plugin DUMMY_PLUGIN;
	
	static {
		DUMMY_PLUGIN = new Plugin(
				PluginStatus.INSTALLED,
				DummyGuiPlugin.getInstance().getId(),
				DummyGuiPlugin.getInstance().isPublisher(),
				DummyGuiPlugin.getInstance().isSynchronizer(),
				DummyGuiPlugin.getInstance().getName(),
				DummyGuiPlugin.getInstance().getAuthor(),
				Constants.VERSION,
				Constants.VERSION,
				DummyGuiPlugin.getInstance().getVersion(),
				DummyGuiPlugin.getInstance().getSynchronizerApi().getApiWebSite(),
				DummyGuiPlugin.getInstance().getSynchronizerApi().getApiWebSite(),
				null,
				null,
				null);
		
		DUMMY_PLUGIN.setHistory("Version "
				+ DummyGuiPlugin.getInstance().getVersion());
		DUMMY_PLUGIN.setLogo(ImageUtils.getResourceImage("do_not_synchronize.png"));
	}
	
	public static SynchronizerGuiPlugin loadPlugin(File file)
			throws PluginException {
		if (!file.isFile()
				|| !FileUtils.getExtention(file.getAbsolutePath()).equals("jar"))
			return null;
		
		try {
			File tmpFile = File.createTempFile("taskunifier_plugin_", ".jar");
			
			try {
				tmpFile.deleteOnExit();
			} catch (Throwable t) {
				
			}
			
			org.apache.commons.io.FileUtils.copyFile(file, tmpFile);
			
			List<SynchronizerGuiPlugin> plugins = Main.getApiPlugins().loadJar(
					tmpFile,
					file,
					false);
			
			if (plugins.size() == 0) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw new PluginException(PluginExceptionType.NO_VALID_PLUGIN);
			}
			
			if (plugins.size() > 1) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw new PluginException(
						PluginExceptionType.MORE_THAN_ONE_PLUGIN);
			}
			
			try {
				if (!EqualsUtils.equals(
						Constants.PLUGIN_API_VERSION,
						plugins.get(0).getPluginApiVersion()))
					throw new PluginException(
							PluginExceptionType.OUTDATED_PLUGIN);
			} catch (PluginException pexc) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw pexc;
			} catch (Throwable t1) {
				try {
					file.delete();
				} catch (Throwable t2) {
					
				}
				
				throw new PluginException(PluginExceptionType.OUTDATED_PLUGIN);
			}
			
			SynchronizerGuiPlugin plugin = plugins.get(0);
			
			List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
					Main.getApiPlugins().getPlugins());
			
			// "existingPlugins" does not contain the new plugin yet
			Main.getApiPlugins().addPlugin(file, plugin);
			
			GuiLogger.getLogger().info(
					"Plugin loaded: "
							+ plugin.getName()
							+ " - "
							+ plugin.getVersion());
			
			SynchronizerGuiPlugin loadedPlugin = plugin;
			
			for (SynchronizerGuiPlugin p : existingPlugins) {
				if (EqualsUtils.equals(p.getId(), plugin.getId())) {
					SynchronizerGuiPlugin pluginToDelete = null;
					if (CompareUtils.compare(
							p.getVersion(),
							plugin.getVersion()) < 0) {
						pluginToDelete = p;
					} else {
						pluginToDelete = plugin;
						loadedPlugin = null;
					}
					
					deletePlugin(pluginToDelete);
					break;
				}
			}
			
			if (loadedPlugin != null)
				loadedPlugin.loadPlugin();
			
			return loadedPlugin;
		} catch (PluginException e) {
			throw e;
		} catch (Throwable t) {
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot install plugin",
					t);
			
			throw new PluginException(
					PluginExceptionType.ERROR_INSTALL_PLUGIN,
					t);
		}
	}
	
	public static void deletePlugin(SynchronizerGuiPlugin plugin) {
		if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
			return;
		
		plugin.deletePlugin();
		
		File file = Main.getApiPlugins().getFile(plugin);
		file.delete();
		
		Main.getApiPlugins().removePlugin(plugin);
		
		GuiLogger.getLogger().info(
				"Plugin deleted: "
						+ plugin.getName()
						+ " - "
						+ plugin.getVersion());
	}
	
	/**
	 * CAN BE EXECUTED OUTSIDE EDT
	 */
	public static void updatePlugin(Plugin plugin, ProgressMonitor monitor)
			throws Exception {
		if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
			return;
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_update",
							plugin.getName())));
		
		boolean use = false;
		if (plugin.getId().equals(
				SynchronizerUtils.getSynchronizerPlugin().getId()))
			use = true;
		
		deletePlugin(plugin, monitor);
		installPlugin(plugin, use, monitor);
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_updated",
							plugin.getName())));
	}
	
	/**
	 * CAN BE EXECUTED OUTSIDE EDT
	 */
	public static void deletePlugin(
			final Plugin plugin,
			final ProgressMonitor monitor) {
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_deletion",
							plugin.getName())));
		
		TUSwingUtilities.executeOrInvokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
						Main.getApiPlugins().getPlugins());
				for (SynchronizerGuiPlugin existingPlugin : existingPlugins) {
					if (existingPlugin.getId().equals(plugin.getId())) {
						existingPlugin.deletePlugin();
						
						File file = Main.getApiPlugins().getFile(existingPlugin);
						file.delete();
						Main.getApiPlugins().removePlugin(existingPlugin);
						
						GuiLogger.getLogger().info(
								"Plugin deleted: "
										+ existingPlugin.getName()
										+ " - "
										+ existingPlugin.getVersion());
						
						plugin.setStatus(PluginStatus.DELETED);
					}
				}
			}
			
		});
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_deleted",
							plugin.getName())));
	}
	
	public static Plugin[] loadAndUpdatePluginsFromXML(
			boolean includePublishers,
			boolean includeSynchronizers,
			boolean includeDummyPlugin,
			boolean silent) {
		ProcessLoadAndUpdatePluginsFromXml process = new ProcessLoadAndUpdatePluginsFromXml(
				includePublishers,
				includeSynchronizers,
				includeDummyPlugin);
		
		Worker<Plugin[]> worker = new Worker<Plugin[]>(process);
		
		if (silent) {
			TUWorkerDialog<Plugin[]> dialog = new TUWorkerDialog<Plugin[]>(
					Translations.getString("general.loading_plugins"));
			
			dialog.setWorker(worker);
			
			dialog.setVisible(true);
			
			return dialog.getResult();
		} else {
			try {
				worker.execute();
				return worker.get();
			} catch (Exception e) {
				return null;
			}
		}
	}
	
}
