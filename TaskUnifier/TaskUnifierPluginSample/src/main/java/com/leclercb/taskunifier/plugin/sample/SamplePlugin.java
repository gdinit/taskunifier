/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.sample;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerApi;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerPlugin;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.sample.resources.Resources;

import java.util.logging.Level;

public class SamplePlugin implements SynchronizerPlugin {
	
	public static final String ID = "999";

	public static String NAME = "Sample Plugin";
	public static String AUTHOR = "Benjamin Leclerc";
	public static String VERSION = null;

    static {
        try {
            PropertyMap properties = new PropertyMap();
            properties.load(Resources.class.getResourceAsStream("general.properties"));

            VERSION = properties.getStringProperty("version");
        } catch (Exception e) {
            PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
	
	public SamplePlugin() {
		
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
		return SampleApi.getInstance();
	}
	
}
