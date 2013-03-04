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
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginStatus;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ProcessInstallPlugin implements Process<Void> {
	
	private Plugin plugin;
	private boolean use;
	
	public ProcessInstallPlugin(Plugin plugin, boolean use) {
		this.setPlugin(plugin);
		this.setUse(use);
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public void setPlugin(Plugin plugin) {
		CheckUtils.isNotNull(plugin);
		this.plugin = plugin;
	}
	
	public boolean isUse() {
		return this.use;
	}
	
	public void setUse(boolean use) {
		this.use = use;
	}
	
	@Override
	public Void execute(final Worker<?> worker) throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		if (this.plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
			return null;
		
		try {
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_installation",
							this.plugin.getName())));
			
			final File tempFile = File.createTempFile(
					"taskunifier_plugin_",
					".jar");
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.downloading_plugin",
							this.plugin.getName())));
			
			worker.executeInterruptibleAction(new Callable<Void>() {
				
				@Override
				public Void call() throws Exception {
					if (!Main.getSettings().getBooleanProperty(
							"proxy.use_system_proxies")
							&& Main.getSettings().getBooleanProperty(
									"proxy.enabled")) {
						FileUtils.copyURLToFile(
								new URL(
										ProcessInstallPlugin.this.plugin.getDownloadUrl()),
								tempFile,
								Main.getSettings().getStringProperty(
										"proxy.host"),
								Main.getSettings().getIntegerProperty(
										"proxy.port"),
								Main.getSettings().getStringProperty(
										"proxy.login"),
								Main.getSettings().getStringProperty(
										"proxy.password"));
					} else {
						FileUtils.copyURLToFile(
								new URL(
										ProcessInstallPlugin.this.plugin.getDownloadUrl()),
								tempFile);
					}
					
					return null;
				}
				
			},
					Constants.TIMEOUT_HTTP_CALL);
			
			if (worker.isCancelled())
				return null;
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.installing_plugin",
							this.plugin.getName())));
			
			final File file = new File(Main.getPluginsFolder()
					+ File.separator
					+ UUID.randomUUID().toString()
					+ ".jar");
			
			try {
				org.apache.commons.io.FileUtils.copyFile(tempFile, file);
				
				ProcessLoadPlugin process = new ProcessLoadPlugin(file);
				final SynchronizerGuiPlugin loadedPlugin = process.execute(worker);
				
				if (loadedPlugin != null) {
					GuiLogger.getLogger().info(
							"Plugin installed: "
									+ loadedPlugin.getName()
									+ " - "
									+ loadedPlugin.getVersion());
					
					ProcessUtils.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							if (ProcessInstallPlugin.this.use) {
								SynchronizerUtils.setSynchronizerPlugin(loadedPlugin);
								SynchronizerUtils.addPublisherPlugin(loadedPlugin);
							}
							
							if (loadedPlugin != null)
								loadedPlugin.installPlugin();
						}
						
					});
				}
				
				this.plugin.setStatus(PluginStatus.INSTALLED);
			} catch (Exception e) {
				file.delete();
				throw e;
			}
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_installed",
							this.plugin.getName())));
			
			return null;
		} catch (Exception e) {
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot install plugin",
					e);
			
			throw e;
		}
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
