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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginsUtils;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginExceptionType;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.StoppableWorker;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.HttpUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class LoadPluginsFromXml implements Process<Plugin[]> {
	
	private boolean includePublishers;
	private boolean includeSynchronizers;
	private boolean includeDummyPlugin;
	
	public LoadPluginsFromXml(
			boolean includePublishers,
			boolean includeSynchronizers,
			boolean includeDummyPlugin) {
		this.setIncludePublishers(includePublishers);
		this.setIncludeSynchronizers(includeSynchronizers);
		this.setIncludeDummyPlugin(includeDummyPlugin);
	}
	
	public boolean isIncludePublishers() {
		return this.includePublishers;
	}
	
	public void setIncludePublishers(boolean includePublishers) {
		this.includePublishers = includePublishers;
	}
	
	public boolean isIncludeSynchronizers() {
		return this.includeSynchronizers;
	}
	
	public void setIncludeSynchronizers(boolean includeSynchronizers) {
		this.includeSynchronizers = includeSynchronizers;
	}
	
	public boolean isIncludeDummyPlugin() {
		return this.includeDummyPlugin;
	}
	
	public void setIncludeDummyPlugin(boolean includeDummyPlugin) {
		this.includeDummyPlugin = includeDummyPlugin;
	}
	
	@Override
	public Plugin[] execute(StoppableWorker<Plugin[]> worker) throws Exception {
		ProgressMonitor monitor = worker.getEDTMonitor();
		
		try {
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.retrieve_plugin_database")));
			
			HttpResponse response = null;
			
			response = worker.executeAtomicAction(new Callable<HttpResponse>() {
				
				@Override
				public HttpResponse call() throws Exception {
					return HttpUtils.getHttpGetResponse(new URI(
							Constants.PLUGINS_FILE));
				}
				
			}, Constants.TIMEOUT_HTTP_CALL);
			
			if (worker.isStopped())
				return null;
			
			if (!response.isSuccessfull()) {
				throw new PluginException(
						PluginExceptionType.ERROR_LOADING_PLUGIN_DB);
			}
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.analysing_plugin_database")));
			
			XStream xstream = new TUXStream(
					new PureJavaReflectionProvider(),
					new DomDriver("UTF-8"));
			xstream.setMode(XStream.NO_REFERENCES);
			xstream.alias("plugins", Plugin[].class);
			xstream.alias("plugin", Plugin.class);
			xstream.processAnnotations(Plugin.class);
			
			Plugin[] plugins = (Plugin[]) xstream.fromXML(response.getContent());
			List<Plugin> filteredPlugins = new ArrayList<Plugin>();
			
			for (Plugin plugin : plugins) {
				// Check min version
				if (plugin.getMinVersion() != null
						&& plugin.getMinVersion().length() != 0) {
					if (Constants.VERSION.compareTo(plugin.getMinVersion()) < 0)
						continue;
				}
				
				// Check max version
				if (plugin.getMaxVersion() != null
						&& plugin.getMaxVersion().length() != 0) {
					if (Constants.VERSION.compareTo(plugin.getMaxVersion()) > 0)
						continue;
				}
				
				// Check publisher & synchronizer
				if (!((this.includePublishers && plugin.isPublisher()) || (this.includeSynchronizers && plugin.isSynchronizer())))
					continue;
				
				plugin.loadHistory();
				plugin.loadLogo();
				
				filteredPlugins.add(plugin);
			}
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.plugin_database_retrieved")));
			
			if (this.includeSynchronizers && this.includeDummyPlugin)
				filteredPlugins.add(0, PluginsUtils.getDummyPlugin());
			
			return filteredPlugins.toArray(new Plugin[0]);
		} catch (Throwable t) {
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot load plugin database",
					t);
			
			throw new PluginException(
					PluginExceptionType.ERROR_LOADING_PLUGIN_DB,
					t);
		}
	}
	
}
