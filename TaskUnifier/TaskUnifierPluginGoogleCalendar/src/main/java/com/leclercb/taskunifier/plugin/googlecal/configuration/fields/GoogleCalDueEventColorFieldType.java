/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.calendar.model.ColorDefinition;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleCalDueEventColorFieldType extends ConfigurationFieldType.ComboBox {
	
	public GoogleCalDueEventColorFieldType() {
		super(
				getColors(),
				PluginApi.getUserSettings(),
				"plugin.googlecal.due_event_color");
		
		this.setRenderer(new GoogleCalEventColorRenderer());
		
		this.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent evt) {
				ColorDefinition color = (ColorDefinition) GoogleCalDueEventColorFieldType.this.getFieldValue();
				GoogleCalDueEventColorFieldType.this.setBackground(Color.decode(color.getBackground()));
				GoogleCalDueEventColorFieldType.this.setForeground(Color.decode(color.getForeground()));
			}
			
		});
	}
	
	@Override
	public Object getPropertyValue() {
		return getColor(PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.due_event_color",
				"2"));
	}
	
	@Override
	public void saveAndApplyConfig() {
		PluginApi.getUserSettings().setStringProperty(
				"plugin.googlecal.due_event_color",
				(String) ((ColorDefinition) this.getFieldValue()).get("id"));
	}
	
	private static ColorDefinition getColor(String id) {
		for (ColorDefinition color : getColors())
			if (color.get("id").equals(id))
				return color;
		
		return null;
	}
	
	private static ColorDefinition[] getColors() {
		List<ColorDefinition> colors = new ArrayList<ColorDefinition>();
		ColorDefinition color = null;
		
		color = new ColorDefinition();
		color.set("id", "1");
		color.setBackground("#a4bdfc");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "2");
		color.setBackground("#7ae7bf");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "3");
		color.setBackground("#dbadff");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "4");
		color.setBackground("#ff887c");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "5");
		color.setBackground("#fbd75b");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "6");
		color.setBackground("#ffb878");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "7");
		color.setBackground("#46d6db");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		color = new ColorDefinition();
		color.set("id", "8");
		color.setBackground("#e1e1e1");
		color.setForeground("#1d1d1d");
		colors.add(color);
		
		return colors.toArray(new ColorDefinition[0]);
	}
	
}
