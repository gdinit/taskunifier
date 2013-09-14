/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import java.util.Properties;
import java.util.logging.Level;

import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googletasks.resources.Resources;

public class GoogleTasksPlugin implements SynchronizerPlugin {
	
	public static final String ID = "3";
	
	private static String NAME = "Google Tasks Plugin";
	private static String AUTHOR = "Benjamin Leclerc";
	private static String VERSION = null;
	
	static {
		try {
			Properties properties = new Properties();
			properties.load(Resources.class.getResourceAsStream("general.properties"));

			VERSION = (String) properties.get("version");
		} catch (Exception e) {
			PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public GoogleTasksPlugin() {
		
	}
	
	@Override
	public boolean isPublisher() {
		return true;
	}
	
	@Override
	public boolean isSynchronizer() {
		return false;
	}
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getAuthor() {
		return AUTHOR;
	}
	
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	@Override
	public SynchronizerApi getSynchronizerApi() {
		return GoogleTasksApi.getInstance();
	}
	
}
