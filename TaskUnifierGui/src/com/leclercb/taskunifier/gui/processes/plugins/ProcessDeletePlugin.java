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
import java.util.concurrent.Callable;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginStatus;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ProcessDeletePlugin implements Process<Void> {
	
	private Plugin plugin;
	
	public ProcessDeletePlugin(Plugin plugin) {
		this.setPlugin(plugin);
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public void setPlugin(Plugin plugin) {
		CheckUtils.isNotNull(plugin);
		this.plugin = plugin;
	}
	
	@Override
	public Void execute(final Worker<?> worker) throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		monitor.addMessage(new DefaultProgressMessage(Translations.getString(
				"manage_plugins.progress.start_plugin_deletion",
				this.plugin.getName())));
		
		ProcessUtils.invokeAndWait(new Callable<Void>() {
			
			@Override
			public Void call() {
				List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
						Main.getApiPlugins().getPlugins());
				
				for (SynchronizerGuiPlugin existingPlugin : existingPlugins) {
					if (existingPlugin.getId().equals(
							ProcessDeletePlugin.this.plugin.getId())) {
						existingPlugin.deletePlugin();
						
						File file = Main.getApiPlugins().getFile(existingPlugin);
						file.delete();
						
						Main.getApiPlugins().removePlugin(existingPlugin);
						
						GuiLogger.getLogger().info(
								"Plugin deleted: "
										+ existingPlugin.getName()
										+ " - "
										+ existingPlugin.getVersion());
						
						ProcessDeletePlugin.this.plugin.setStatus(PluginStatus.DELETED);
					}
				}
				
				return null;
			}
			
		});
		
		monitor.addMessage(new DefaultProgressMessage(Translations.getString(
				"manage_plugins.progress.plugin_deleted",
				this.plugin.getName())));
		
		return null;
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
