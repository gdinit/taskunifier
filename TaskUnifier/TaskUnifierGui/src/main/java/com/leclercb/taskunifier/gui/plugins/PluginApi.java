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
package com.leclercb.taskunifier.gui.plugins;

import java.awt.Frame;
import java.awt.Window;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.help.HelpSet;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.main.frames.FrameView;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public final class PluginApi {
	
	private PluginApi() {
		
	}
	
	public static String getCurrentUserId() {
		return Main.getCurrentUserId();
	}
	
	public static boolean isFirstExecution() {
		return Main.isFirstExecution();
	}
	
	public static String getInitSettingsFile() {
		return Main.getInitSettingsFile();
	}
	
	public static String getSettingsFile() {
		return Main.getSettingsFile();
	}
	
	public static String getUserSettingsFile() {
		return Main.getUserSettingsFile();
	}
	
	public static String getResourcesFolder() {
		return Main.getResourcesFolder();
	}
	
	public static String getDataFolder() {
		return Main.getDataFolder();
	}
	
	public static String getUserFolder() {
		return Main.getUserFolder();
	}
	
	public static String getPluginsFolder() {
		return Main.getPluginsFolder();
	}
	
	public static PropertyMap getInitSettings() {
		return Main.getInitSettings();
	}
	
	public static PropertyMap getSettings() {
		return Main.getSettings();
	}
	
	public static PropertyMap getUserSettings() {
		return Main.getUserSettings();
	}
	
	public static String getPreviousVersion() {
		return Main.getPreviousVersion();
	}
	
	public static boolean isVersionUpdated() {
		return Main.isVersionUpdated();
	}
	
	public static void invokeLater(Runnable runnable) {
		ProcessUtils.invokeLater(runnable);
	}
	
	public static void invokeAndWait(Runnable runnable) {
		ProcessUtils.executeOrInvokeAndWait(runnable);
	}
	
	public static <T> T invokeAndWait(Callable<T> callable)
			throws InterruptedException, ExecutionException {
		return ProcessUtils.executeOrInvokeAndWait(callable);
	}
	
	public static Frame getCurrentFrame() {
		return FrameUtils.getCurrentFrame();
	}
	
	public static Window getCurrentWindow() {
		return FrameUtils.getCurrentWindow();
	}
	
	public static FrameView getCurrentFrameView() {
		return FrameUtils.getCurrentFrameView();
	}
	
	public static SynchronizerGuiPlugin getPlugin(String pluginId) {
		return SynchronizerUtils.getPlugin(pluginId);
	}
	
	public static void initializeProxy(SynchronizerGuiPlugin plugin) {
		SynchronizerUtils.initializeProxy(plugin);
	}
	
	public static Locale getLocale() {
		return Translations.getLocale();
	}
	
	public static String getTranslation(String key) {
		return Translations.getString(key);
	}
	
	public static String getTranslation(String key, Object... args) {
		return Translations.getString(key, args);
	}
	
	public static void resetAllSynchronizersAndDeleteModels() {
		SynchronizerUtils.resetAllSynchronizersAndDeleteModels();
	}
	
	public static void addHelpSet(HelpSet helpSet) {
		Help.getInstance().getHelpSet().add(helpSet);
	}
	
	public static void removeHelpSet(HelpSet helpSet) {
		Help.getInstance().getHelpSet().remove(helpSet);
	}
	
}
