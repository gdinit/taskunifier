/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import java.util.Properties;
import java.util.logging.Level;

import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.toodledo.resources.Resources;

public class ToodledoPlugin implements SynchronizerPlugin {
	
	public static final String ID = "4";
	
	private static String NAME = "Toodledo Plugin";
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
	
	public ToodledoPlugin() {
		
	}
	
	@Override
	public boolean isPublisher() {
		return false;
	}
	
	@Override
	public boolean isSynchronizer() {
		return true;
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
		return ToodledoApi.getInstance();
	}
	
}
