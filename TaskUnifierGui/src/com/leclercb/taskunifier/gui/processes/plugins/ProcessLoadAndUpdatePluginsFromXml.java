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

import java.util.List;

import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginStatus;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.processes.Worker;

public class ProcessLoadAndUpdatePluginsFromXml extends ProcessLoadPluginsFromXml {
	
	public ProcessLoadAndUpdatePluginsFromXml(
			boolean includePublishers,
			boolean includeSynchronizers,
			boolean includeDummyPlugin) {
		super(includePublishers, includeSynchronizers, includeDummyPlugin);
	}
	
	@Override
	public Plugin[] execute(final Worker<?> worker) throws Exception {
		Plugin[] plugins = super.execute(worker);
		
		if (plugins == null) {
			if (!this.isIncludeDummyPlugin())
				return new Plugin[0];
			
			plugins = new Plugin[] { Plugin.getDummyPlugin() };
		}
		
		refreshPluginStatuses(plugins);
		
		return plugins;
	}
	
	public static void refreshPluginStatuses(Plugin[] plugins) {
		if (plugins == null)
			return;
		
		List<SynchronizerGuiPlugin> loadedPlugins = Main.getApiPlugins().getPlugins();
		for (SynchronizerGuiPlugin p : loadedPlugins) {
			if (p == null)
				continue;
			
			for (int i = 0; i < plugins.length; i++) {
				if (p.getId().equals(plugins[i].getId())) {
					if (p.getVersion().compareTo(plugins[i].getVersion()) < 0)
						plugins[i].setStatus(PluginStatus.TO_UPDATE);
					else
						plugins[i].setStatus(PluginStatus.INSTALLED);
				}
			}
		}
	}
	
}
