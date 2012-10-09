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
package com.leclercb.taskunifier.gui.main.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.leclercb.commons.api.logger.ApiLogger;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;

public final class MainLoadLoggers {
	
	private MainLoadLoggers() {
		
	}
	
	public static void loadLoggers() {
		String apiLogFile = Main.getDataFolder()
				+ File.separator
				+ "taskunifier_api.log";
		String guiLogFile = Main.getDataFolder()
				+ File.separator
				+ "taskunifier_gui.log";
		String pluginLogFile = Main.getDataFolder()
				+ File.separator
				+ "taskunifier_plugin.log";
		
		addHandlers(ApiLogger.getLogger(), Level.INFO, apiLogFile);
		addHandlers(GuiLogger.getLogger(), Level.INFO, guiLogFile);
		addHandlers(PluginLogger.getLogger(), Level.INFO, pluginLogFile);
	}
	
	public static void loadLoggerLevels() {
		try {
			Level apiLogLevel = Level.parse(Main.getSettings().getStringProperty(
					"logger.api.level"));
			Level guiLogLevel = Level.parse(Main.getSettings().getStringProperty(
					"logger.gui.level"));
			Level pluginLogLevel = Level.parse(Main.getSettings().getStringProperty(
					"logger.plugin.level"));
			
			ApiLogger.getLogger().setLevel(apiLogLevel);
			GuiLogger.getLogger().setLevel(apiLogLevel);
			PluginLogger.getLogger().setLevel(apiLogLevel);
			
			Handler[] handlers;
			
			handlers = ApiLogger.getLogger().getHandlers();
			for (Handler handler : handlers)
				handler.setLevel(apiLogLevel);
			
			handlers = GuiLogger.getLogger().getHandlers();
			for (Handler handler : handlers)
				handler.setLevel(guiLogLevel);
			
			handlers = PluginLogger.getLogger().getHandlers();
			for (Handler handler : handlers)
				handler.setLevel(pluginLogLevel);
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading logger levels",
					t);
		}
	}
	
	private static void addHandlers(Logger logger, Level level, String file) {
		logger.setUseParentHandlers(false);
		
		file = file.replace("%", "%%");
		
		// Console Handler
		try {
			ConsoleHandler handler = new ConsoleHandler();
			
			handler.setLevel(level);
			handler.setFormatter(new SimpleFormatter());
			
			logger.addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		// File Handler
		try {
			FileHandler handler = new FileHandler(file, 50000, 1, true);
			
			handler.setLevel(level);
			handler.setFormatter(new SimpleFormatter());
			
			logger.addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
