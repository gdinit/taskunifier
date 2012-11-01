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
package com.leclercb.taskunifier.gui.settings;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.resources.Resources;

public final class UserSettingsVersion {
	
	private UserSettingsVersion() {
		
	}
	
	public static String updateSettings() {
		String version = Main.getUserSettings().getStringProperty(
				"general.user.version");
		
		if (Main.isFirstExecution())
			version = Constants.VERSION;
		
		final String oldVersion = version;
		
		if (version == null)
			version = "2.4.0";
		
		if (version.equals("2.4.0"))
			version = updateUserSettings_2_4_0_to_3_0_0();
		
		if (version.equals("3.0.0"))
			version = updateUserSettings_3_0_0_to_3_0_1();
		
		if (version.equals("3.0.1"))
			version = updateUserSettings_3_0_1_to_3_0_2();
		
		if (version.equals("3.0.2"))
			version = updateUserSettings_3_0_2_to_3_0_3();
		
		if (version.equals("3.0.3"))
			version = updateUserSettings_3_0_3_to_3_0_4();
		
		if (version.equals("3.0.4"))
			version = updateUserSettings_3_0_4_to_3_1_0();
		
		cleanSettings();
		
		Main.getUserSettings().setStringProperty(
				"general.user.version",
				Constants.VERSION);
		
		return oldVersion;
	}
	
	private static void cleanSettings() {
		try {
			Properties defaultProperties = new Properties();
			defaultProperties.load(Resources.class.getResourceAsStream("default_user_settings.properties"));
			
			for (String key : defaultProperties.stringPropertyNames()) {
				String value = defaultProperties.getProperty(key);
				
				if (value == null || value.length() == 0)
					continue;
				
				if (Main.getUserSettings().getStringProperty(key) == null) {
					GuiLogger.getLogger().warning("Clean user settings: " + key);
					Main.getUserSettings().remove(key);
				}
			}
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot clean user settings",
					t);
		}
	}
	
	private static String updateUserSettings_2_4_0_to_3_0_0() {
		GuiLogger.getLogger().info(
				"Update user settings from version 2.4.0 to 3.0.0");
		
		Main.getUserSettings().setStringProperty(
				"synchronizer.publish_background",
				"false");
		Main.getUserSettings().setStringProperty(
				"synchronizer.publish_exit",
				"false");
		Main.getUserSettings().setStringProperty(
				"synchronizer.publish_start",
				"false");
		Main.getUserSettings().setStringProperty(
				"synchronizer.sync_reminder_field",
				"true");
		
		copyInsideUserFolder("contexts.xml", "contexts_v3.xml");
		copyInsideUserFolder("folders.xml", "folders_v3.xml");
		copyInsideUserFolder("goals.xml", "goals_v3.xml");
		copyInsideUserFolder("locations.xml", "locations_v3.xml");
		copyInsideUserFolder("notes.xml", "notes_v3.xml");
		copyInsideUserFolder("tasks.xml", "tasks_v3.xml");
		copyInsideUserFolder("task_templates.xml", "task_templates_v3.xml");
		copyInsideUserFolder("task_searchers.xml", "task_searchers_v3.xml");
		copyInsideUserFolder("note_searchers.xml", "note_searchers_v3.xml");
		
		return "3.0.0";
	}
	
	private static String updateUserSettings_3_0_0_to_3_0_1() {
		GuiLogger.getLogger().info(
				"Update user settings from version 3.0.0 to 3.0.1");
		
		return "3.0.1";
	}
	
	private static String updateUserSettings_3_0_1_to_3_0_2() {
		GuiLogger.getLogger().info(
				"Update user settings from version 3.0.1 to 3.0.2");
		
		return "3.0.2";
	}
	
	private static String updateUserSettings_3_0_2_to_3_0_3() {
		GuiLogger.getLogger().info(
				"Update user settings from version 3.0.2 to 3.0.3");
		
		return "3.0.3";
	}
	
	private static String updateUserSettings_3_0_3_to_3_0_4() {
		GuiLogger.getLogger().info(
				"Update user settings from version 3.0.3 to 3.0.4");
		
		return "3.0.4";
	}
	
	private static String updateUserSettings_3_0_4_to_3_1_0() {
		GuiLogger.getLogger().info(
				"Update user settings from version 3.0.4 to 3.1.0");
		
		return "3.1.0";
	}
	
	private static void copyInsideUserFolder(
			String fromFileName,
			String toFileName) {
		try {
			File from = new File(Main.getUserFolder()
					+ File.separator
					+ fromFileName);
			
			File to = new File(Main.getUserFolder()
					+ File.separator
					+ toFileName);
			
			if (!to.exists()) {
				FileUtils.copyFile(from, to);
			}
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while copying " + fromFileName + " to " + toFileName,
					t);
		}
	}
	
}
