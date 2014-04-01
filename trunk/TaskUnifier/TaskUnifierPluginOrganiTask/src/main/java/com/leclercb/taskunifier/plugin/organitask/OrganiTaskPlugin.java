/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.organitask.resources.Resources;

import java.util.logging.Level;

public class OrganiTaskPlugin implements SynchronizerPlugin {
	
	public static final String ID = "6";
	
	private static String NAME = "OrganiTask Plugin";
	private static String AUTHOR = "Benjamin Leclerc";
	private static String VERSION = null;
	
	static {
		try {
            PropertyMap properties = new PropertyMap();
			properties.load(Resources.class.getResourceAsStream("general.properties"));

			VERSION = properties.getStringProperty("version");
		} catch (Exception e) {
			PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public OrganiTaskPlugin() {
		
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
		return OrganiTaskApi.getInstance();
	}
	
}
