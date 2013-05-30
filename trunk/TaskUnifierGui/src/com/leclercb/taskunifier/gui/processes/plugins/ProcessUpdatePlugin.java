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
package com.leclercb.taskunifier.gui.processes.plugins;

import java.util.ArrayList;
import java.util.List;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ProcessUpdatePlugin implements Process<SynchronizerGuiPlugin[]> {
	
	private Plugin[] plugins;
	
	public ProcessUpdatePlugin(Plugin... plugins) {
		this.setPlugins(plugins);
	}
	
	public Plugin[] getPlugins() {
		return this.plugins;
	}
	
	public void setPlugins(Plugin[] plugins) {
		CheckUtils.isNotNull(plugins);
		this.plugins = plugins;
	}
	
	@Override
	public SynchronizerGuiPlugin[] execute(final Worker<?> worker)
			throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		List<SynchronizerGuiPlugin> updatedPlugins = new ArrayList<SynchronizerGuiPlugin>();
		
		for (Plugin plugin : this.plugins) {
			if (plugin == null)
				continue;
			
			if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
				continue;
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_update",
							plugin.getName())));
			
			boolean use = false;
			if (plugin.getId().equals(
					SynchronizerUtils.getSynchronizerPlugin().getId()))
				use = true;
			
			ProcessDeletePlugin processDeletePlugin = new ProcessDeletePlugin(
					plugin);
			processDeletePlugin.execute(worker);
			
			ProcessInstallPlugin processInstallPlugin = new ProcessInstallPlugin(
					plugin,
					use);
			SynchronizerGuiPlugin p = processInstallPlugin.execute(worker);
			
			updatedPlugins.add(p);
			
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_updated",
							plugin.getName())));
		}
		
		return updatedPlugins.toArray(new SynchronizerGuiPlugin[0]);
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
