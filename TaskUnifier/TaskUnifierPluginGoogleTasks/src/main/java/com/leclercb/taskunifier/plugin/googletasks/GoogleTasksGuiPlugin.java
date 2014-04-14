/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googletasks.help.PluginHelp;
import com.leclercb.taskunifier.plugin.googletasks.resources.Resources;

import javax.help.HelpSet;
import java.net.URL;
import java.util.logging.Level;

public class GoogleTasksGuiPlugin extends GoogleTasksPlugin implements SynchronizerGuiPlugin {
	
	private HelpSet helpSet;
	
	public GoogleTasksGuiPlugin() {
		GoogleTasksApi.getInstance().setApplicationName("TaskUnifier");

        try {
            PropertyMap properties = new PropertyMap();
            properties.load(Resources.class.getResourceAsStream("private.properties"));

            GoogleTasksApi.getInstance().setClientId(properties.getStringProperty("googletasks.client_id"));
            GoogleTasksApi.getInstance().setClientSecret(properties.getStringProperty("googletasks.client_secret"));
        } catch (Exception e) {
            PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
	}
	
	@Override
	public String getAccountLabel() {
		return PluginApi.getUserSettings().getStringProperty(
				"plugin.googletasks.email");
	}
	
	@Override
	public void loadPlugin() {
		try {
			URL url = PluginHelp.class.getResource("help.xml");
			this.helpSet = new HelpSet(null, url);
			PluginApi.addHelpSet(this.helpSet);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void installPlugin() {
		GoogleTasksApi.getInstance().resetSynchronizerParameters(
				PluginApi.getUserSettings());
	}
	
	@Override
	public void deletePlugin() {
		if (this.helpSet != null)
			PluginApi.removeHelpSet(this.helpSet);
	}
	
	@Override
	public int getPluginApiVersion() {
		return 41; // See: Constants.PLUGIN_API_VERSION
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		return new GoogleTasksConfigurationPanel(configuration, welcome);
	}
	
}
