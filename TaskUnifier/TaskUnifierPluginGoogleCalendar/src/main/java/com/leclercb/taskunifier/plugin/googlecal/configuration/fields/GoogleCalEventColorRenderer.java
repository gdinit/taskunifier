/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.google.api.services.calendar.model.ColorDefinition;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalEventColorRenderer implements ListCellRenderer {
	
	private JLabel label;
	
	public GoogleCalEventColorRenderer() {
		this.label = new JLabel();
	}
	
	@Override
	public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		ColorDefinition color = (ColorDefinition) value;
		
		this.label.setText(PluginTranslations.getString("event_name_sample"));
		this.label.setOpaque(true);
		this.label.setBackground(Color.decode(color.getBackground()));
		this.label.setForeground(Color.decode(color.getForeground()));
		
		return this.label;
	}
	
}
