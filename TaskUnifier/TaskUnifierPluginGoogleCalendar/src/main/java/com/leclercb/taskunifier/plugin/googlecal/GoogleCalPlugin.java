/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import java.util.Properties;
import java.util.logging.Level;

import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googlecal.resources.Resources;

public class GoogleCalPlugin implements SynchronizerPlugin {
	
	public static final String ID = "2";
	
	private static String NAME = "Google Calendar Plugin";
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
	
	public GoogleCalPlugin() {
		
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
		return GoogleCalApi.getInstance();
	}
	
}
