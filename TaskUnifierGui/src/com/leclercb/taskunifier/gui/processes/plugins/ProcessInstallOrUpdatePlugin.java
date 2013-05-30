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

import java.util.concurrent.Callable;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginStatus;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ProcessInstallOrUpdatePlugin implements Process<SynchronizerGuiPlugin> {
	
	private Plugin plugin;
	private boolean use;
	
	public ProcessInstallOrUpdatePlugin(Plugin plugin, boolean use) {
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
	public SynchronizerGuiPlugin execute(final Worker<?> worker)
			throws Exception {
		SynchronizerGuiPlugin plugin = null;
		
		if (this.plugin.getStatus() == PluginStatus.TO_INSTALL) {
			ProcessInstallPlugin process = new ProcessInstallPlugin(
					this.plugin,
					this.use);
			plugin = process.execute(worker);
		} else if (this.plugin.getStatus() == PluginStatus.TO_UPDATE) {
			ProcessUpdatePlugin process = new ProcessUpdatePlugin(this.plugin);
			plugin = process.execute(worker)[0];
		} else {
			plugin = SynchronizerUtils.getPlugin(this.plugin.getId());
		}
		
		final SynchronizerGuiPlugin finalPlugin = plugin;
		
		if (plugin != null && this.use) {
			ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {
				
				@Override
				public Void call() {
					SynchronizerUtils.setSynchronizerPlugin(finalPlugin);
					SynchronizerUtils.addPublisherPlugin(finalPlugin);
					
					return null;
				}
				
			});
		}
		
		return plugin;
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
