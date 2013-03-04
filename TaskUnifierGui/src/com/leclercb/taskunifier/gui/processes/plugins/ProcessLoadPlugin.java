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
package com.leclercb.taskunifier.gui.processes.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginExceptionType;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ProcessLoadPlugin implements Process<SynchronizerGuiPlugin> {
	
	private File file;
	
	public ProcessLoadPlugin(File file) {
		this.setFile(file);
	}
	
	public File getFile() {
		return this.file;
	}
	
	public void setFile(File file) {
		CheckUtils.isNotNull(file);
		this.file = file;
	}
	
	@Override
	public SynchronizerGuiPlugin execute(final Worker<?> worker)
			throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		monitor.addMessage(new DefaultProgressMessage(
				Translations.getString("manage_plugins.progress.loading_plugin")));
		
		if (!this.file.isFile()
				|| !FileUtils.getExtention(this.file.getName()).equals("jar"))
			return null;
		
		try {
			final File tmpFile = File.createTempFile(
					"taskunifier_plugin_",
					".jar");
			
			try {
				tmpFile.deleteOnExit();
			} catch (Throwable t) {
				GuiLogger.getLogger().log(
						Level.WARNING,
						"Cannot delete file on exit",
						t);
			}
			
			org.apache.commons.io.FileUtils.copyFile(this.file, tmpFile);
			
			List<SynchronizerGuiPlugin> plugins = Main.getApiPlugins().loadJar(
					tmpFile,
					this.file,
					false);
			
			if (plugins.size() == 0) {
				try {
					this.file.delete();
				} catch (Throwable t) {
					
				}
				
				throw new PluginException(PluginExceptionType.NO_VALID_PLUGIN);
			}
			
			if (plugins.size() > 1) {
				try {
					this.file.delete();
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
			} catch (PluginException e) {
				try {
					this.file.delete();
				} catch (Throwable t) {
					GuiLogger.getLogger().log(
							Level.WARNING,
							"Cannot delete file",
							t);
				}
				
				throw e;
			} catch (Throwable t1) {
				try {
					this.file.delete();
				} catch (Throwable t2) {
					GuiLogger.getLogger().log(
							Level.WARNING,
							"Cannot delete file",
							t2);
				}
				
				throw new PluginException(PluginExceptionType.OUTDATED_PLUGIN);
			}
			
			final SynchronizerGuiPlugin plugin = plugins.get(0);
			
			// "existingPlugins" does not contain the new plugin yet
			List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
					Main.getApiPlugins().getPlugins());
			
			ProcessUtils.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					Main.getApiPlugins().addPlugin(
							ProcessLoadPlugin.this.file,
							plugin);
				}
				
			});
			
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
					
					ProcessDeleteSyncPlugin process = new ProcessDeleteSyncPlugin(
							pluginToDelete);
					process.execute(worker);
					
					break;
				}
			}
			
			final SynchronizerGuiPlugin finalLoadedPlugin = loadedPlugin;
			
			if (loadedPlugin != null) {
				ProcessUtils.invokeAndWait(new Runnable() {
					
					@Override
					public void run() {
						finalLoadedPlugin.loadPlugin();
					}
					
				});
			}
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString("manage_plugins.progress.plugin_loaded")));
			
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
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
