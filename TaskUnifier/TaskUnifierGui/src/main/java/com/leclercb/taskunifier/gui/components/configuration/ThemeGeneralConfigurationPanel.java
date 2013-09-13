/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.components.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.fields.theme.FontNameFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.theme.FontSizeFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.theme.LookAndFeelFieldType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ThemeGeneralConfigurationPanel extends DefaultConfigurationPanel {
	
	public ThemeGeneralConfigurationPanel(ConfigurationGroup configuration) {
		super(
				ConfigurationTab.THEME_GENERAL,
				configuration,
				"configuration_theme_general");
		
		this.initialize();
		this.pack();
	}
	
	private void initialize() {
		this.addField(new ConfigurationField(
				"LOOK_AND_FEEL",
				Translations.getString("configuration.theme.look_and_feel"),
				new LookAndFeelFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"SHOW_BADGES_PERFORMANCE",
				null,
				new ConfigurationFieldType.Label(
						Translations.getString("configuration.theme.show_badges_performance"))));
		
		this.addField(new ConfigurationField(
				"SHOW_NOTE_BADGES",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"notesearcher.show_badges",
						Translations.getString("configuration.theme.show_note_badges"))));
		
		this.addField(new ConfigurationField(
				"SHOW_TASK_BADGES",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"tasksearcher.show_badges",
						Translations.getString("configuration.theme.show_task_badges"))));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_2",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"TASKS_SPLIT",
				Translations.getString("configuration.theme.tasks_split"),
				true,
				new ConfigurationFieldType.RadioButton(
						Main.getSettings(),
						"view.tasks.window.split",
						new String[] {
								Translations.getString("configuration.theme.notes_split.vertical"),
								Translations.getString("configuration.theme.notes_split.horizontal") },
						new String[] { "0", "1" })));
		
		this.addField(new ConfigurationField(
				"NOTES_SPLIT",
				Translations.getString("configuration.theme.notes_split"),
				true,
				new ConfigurationFieldType.RadioButton(
						Main.getSettings(),
						"view.notes.window.split",
						new String[] {
								Translations.getString("configuration.theme.notes_split.vertical"),
								Translations.getString("configuration.theme.notes_split.horizontal") },
						new String[] { "0", "1" })));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_3",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"COLORS_IMPORTANCE_ENABLED",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"theme.color.importance.enabled",
						Translations.getString("configuration.theme.colors_by_importance_enabled"))));
		
		this.addField(new ConfigurationField(
				"COLOR_TASK_PROGRESS",
				Translations.getString("configuration.theme.color_task_progress"),
				new ConfigurationFieldType.ColorChooser(
						Main.getSettings(),
						"theme.color.progress")));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_4",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"FONT_NAME",
				Translations.getString("configuration.theme.font.name"),
				true,
				new FontNameFieldType()));
		
		this.addField(new ConfigurationField(
				"FONT_SIZE",
				Translations.getString("configuration.theme.font.size"),
				true,
				new FontSizeFieldType()));
		
		this.addField(new ConfigurationField(
				"FONT_RESET",
				null,
				new ConfigurationFieldType.Button(
						Translations.getString("configuration.theme.font.reset"),
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent event) {
								Main.getSettings().setStringProperty(
										"theme.font.name",
										null);
								Main.getSettings().setIntegerProperty(
										"theme.font.size",
										12);
							}
							
						})));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_5",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"COLORS_ENABLED",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"theme.color.enabled",
						Translations.getString("configuration.theme.colors_enabled"))));
		
		this.addField(new ConfigurationField(
				"COLOR_EVEN",
				Translations.getString("configuration.theme.color_even"),
				new ConfigurationFieldType.ColorChooser(
						Main.getSettings(),
						"theme.color.even")));
		
		this.addField(new ConfigurationField(
				"COLOR_ODD",
				Translations.getString("configuration.theme.color_odd"),
				new ConfigurationFieldType.ColorChooser(
						Main.getSettings(),
						"theme.color.odd")));
		
		this.addField(new ConfigurationField(
				"COLOR_DUE_TODAY",
				Translations.getString("configuration.theme.color_due_today"),
				new ConfigurationFieldType.ColorChooser(
						Main.getSettings(),
						"theme.color.due_today")));
		
		this.addField(new ConfigurationField(
				"COLOR_OVERDUE",
				Translations.getString("configuration.theme.color_overdue"),
				new ConfigurationFieldType.ColorChooser(
						Main.getSettings(),
						"theme.color.overdue")));
	}
	
}
