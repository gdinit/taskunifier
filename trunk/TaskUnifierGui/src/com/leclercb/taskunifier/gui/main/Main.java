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
package com.leclercb.taskunifier.gui.main;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.commons.api.event.action.ActionSupport;
import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.plugins.PluginLoader;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.properties.SortedProperties;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.SingleInstanceUtils;
import com.leclercb.commons.api.utils.exceptions.SingleInstanceException;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.commons.gui.swing.lookandfeel.types.DefaultLookAndFeelDescriptor;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.properties.ModelIdCoder;
import com.leclercb.taskunifier.gui.actions.ActionImportComFile;
import com.leclercb.taskunifier.gui.actions.ActionQuit;
import com.leclercb.taskunifier.gui.api.models.GuiContact;
import com.leclercb.taskunifier.gui.api.models.GuiContext;
import com.leclercb.taskunifier.gui.api.models.GuiFolder;
import com.leclercb.taskunifier.gui.api.models.GuiGoal;
import com.leclercb.taskunifier.gui.api.models.GuiLocation;
import com.leclercb.taskunifier.gui.api.models.GuiNote;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.api.models.beans.ComBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiContactBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiContextBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiFolderBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiGoalBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiLocationBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiNoteBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiTaskBean;
import com.leclercb.taskunifier.gui.api.plugins.PluginsUtils;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.main.MainLoadFiles;
import com.leclercb.taskunifier.gui.main.main.MainLoadLoggers;
import com.leclercb.taskunifier.gui.main.main.MainSaveFiles;
import com.leclercb.taskunifier.gui.main.main.MainSwingRunnable;
import com.leclercb.taskunifier.gui.properties.ShortcutKeyCoder;
import com.leclercb.taskunifier.gui.resources.Resources;
import com.leclercb.taskunifier.gui.settings.SettingsVersion;
import com.leclercb.taskunifier.gui.settings.UserSettingsVersion;
import com.leclercb.taskunifier.gui.swing.EventQueueProxy;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.swing.lookandfeel.JTattooLookAndFeelDescriptor;
import com.leclercb.taskunifier.gui.threads.Threads;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.CommunicatorUtils;
import com.leclercb.taskunifier.gui.utils.ProtocolUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import com.leclercb.taskunifier.gui.utils.UserUtils;

public class Main {
	
	private static String CURRENT_USER_ID;
	
	private static boolean QUITTING;
	
	private static boolean DEVELOPER_MODE;
	private static boolean FIRST_EXECUTION;
	
	private static String RESOURCES_FOLDER;
	private static String DATA_FOLDER;
	private static String USER_FOLDER;
	private static String BACKUP_FOLDER;
	private static String PLUGINS_FOLDER;
	
	private static PropertyMap INIT_SETTINGS;
	private static PropertyMap SETTINGS;
	private static PropertyMap USER_SETTINGS;
	
	private static String PREVIOUS_VERSION;
	private static boolean VERSION_UPDATED;
	private static boolean OUTDATED_PLUGINS;
	
	private static PluginLoader<SynchronizerGuiPlugin> API_PLUGINS;
	
	private static ActionSupport ACTION_SUPPORT;
	
	public static String getCurrentUserId() {
		return CURRENT_USER_ID;
	}
	
	public static boolean isQuitting() {
		return QUITTING;
	}
	
	public static void setQuitting(boolean quitting) {
		QUITTING = quitting;
	}
	
	public static boolean isDeveloperMode() {
		return DEVELOPER_MODE;
	}
	
	public static boolean isFirstExecution() {
		return FIRST_EXECUTION;
	}
	
	private static String getLockFile() {
		return DATA_FOLDER + File.separator + "taskunifier.lock";
	}
	
	public static String getInitSettingsFile() {
		return RESOURCES_FOLDER + File.separator + "taskunifier.properties";
	}
	
	public static String getSettingsFile() {
		return DATA_FOLDER + File.separator + "settings.properties";
	}
	
	public static String getUserSettingsFile() {
		return USER_FOLDER + File.separator + "settings.properties";
	}
	
	public static String getResourcesFolder() {
		return RESOURCES_FOLDER;
	}
	
	public static String getDataFolder() {
		return DATA_FOLDER;
	}
	
	public static String getUserFolder() {
		return USER_FOLDER;
	}
	
	public static String getUserFolder(String userId) {
		return getDataFolder()
				+ File.separator
				+ "users"
				+ File.separator
				+ userId;
	}
	
	public static String getBackupFolder() {
		return BACKUP_FOLDER;
	}
	
	public static String getPluginsFolder() {
		return PLUGINS_FOLDER;
	}
	
	public static PropertyMap getInitSettings() {
		return INIT_SETTINGS;
	}
	
	public static PropertyMap getSettings() {
		return SETTINGS;
	}
	
	public static PropertyMap getUserSettings() {
		return USER_SETTINGS;
	}
	
	public static String getPreviousVersion() {
		return PREVIOUS_VERSION;
	}
	
	public static boolean isVersionUpdated() {
		return VERSION_UPDATED;
	}
	
	public static boolean isOutdatedPlugins() {
		return OUTDATED_PLUGINS;
	}
	
	public static PluginLoader<SynchronizerGuiPlugin> getApiPlugins() {
		return API_PLUGINS;
	}
	
	public static ActionSupport getActionSupport() {
		return ACTION_SUPPORT;
	}
	
	public static void main(final String[] args) throws SingleInstanceException {
		try {
			initialize();
			loadDeveloperMode();
			loadResourceFolder();
			loadInitSettings();
			loadDataFolder();
			loadPluginsFolder();
			
			if (!checkSingleInstance(args)) {
				secondaryMain(args);
				throw new SingleInstanceException(
						"Another instance of TaskUnifier is running");
			}
			
			MainLoadLoggers.loadLoggers();
			loadUncaughtExceptionHandler();
			loadSettings();
			loadTimeZone();
			loadUserId();
			loadUserFolder();
			loadBackupFolder();
			PREVIOUS_VERSION = SettingsVersion.updateSettings();
			loadUserSettings();
			UserSettingsVersion.updateSettings();
			MainLoadLoggers.loadLoggerLevels();
			loadProxies();
			loadLocale();
			loadModels();
			loadLookAndFeel();
			OUTDATED_PLUGINS = loadApiPlugins();
			loadSynchronizer();
			loadShutdownHook();
			loadCustomProtocolHandlers();
			
			VERSION_UPDATED = !Constants.VERSION.equals(PREVIOUS_VERSION);
			
			Constants.initialize();
			
			ACTION_SUPPORT.fireActionPerformed(0, "AFTER_START");
		} catch (SingleInstanceException e) {
			throw e;
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		TUSwingUtilities.invokeLater(new MainSwingRunnable(args));
	}
	
	private static void secondaryMain(String[] args) {
		try {
			loadSettings();
			loadUserId();
			loadUserFolder();
			loadBackupFolder();
			loadUserSettings();
			
			ComBean bean = new ComBean();
			bean.setApplicationName(Constants.TITLE);
			bean.setArguments(args);
			
			CommunicatorUtils.send(
					bean,
					"127.0.0.1",
					SETTINGS.getIntegerProperty("general.communicator.port"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void handleArguments(String[] args) {
		if (args == null)
			return;
		
		for (String arg : args) {
			if (arg == null)
				continue;
			
			ActionImportComFile.importComFile(new File(arg));
		}
	}
	
	private static boolean checkSingleInstance(String[] args) {
		if (SystemUtils.IS_OS_MAC)
			return true;
		
		return SingleInstanceUtils.isSingleInstance(getLockFile());
	}
	
	private static void loadDeveloperMode() {
		String developerMode = System.getProperty("com.leclercb.taskunifier.developer_mode");
		DEVELOPER_MODE = "true".equals(developerMode);
		
		if (isDeveloperMode())
			GuiLogger.getLogger().severe("DEVELOPER MODE");
	}
	
	private static void initialize() throws Exception {
		DEVELOPER_MODE = false;
		FIRST_EXECUTION = false;
		
		SortedProperties defaultProperties = null;
		
		defaultProperties = new SortedProperties();
		defaultProperties.load(Resources.class.getResourceAsStream("default_settings.properties"));
		
		SETTINGS = new PropertyMap(
				new SortedProperties(defaultProperties),
				defaultProperties);
		
		SETTINGS.addCoder(new ModelIdCoder());
		SETTINGS.addCoder(new ShortcutKeyCoder());
		
		defaultProperties = new SortedProperties();
		defaultProperties.load(Resources.class.getResourceAsStream("default_user_settings.properties"));
		
		USER_SETTINGS = new PropertyMap(
				new SortedProperties(defaultProperties),
				defaultProperties);
		
		USER_SETTINGS.addCoder(new ModelIdCoder());
		USER_SETTINGS.addCoder(new ShortcutKeyCoder());
		
		PREVIOUS_VERSION = Constants.VERSION;
		VERSION_UPDATED = false;
		OUTDATED_PLUGINS = false;
		
		ACTION_SUPPORT = new ActionSupport(Main.class);
	}
	
	private static void loadResourceFolder() throws Exception {
		RESOURCES_FOLDER = System.getProperty("com.leclercb.taskunifier.resource_folder");
		
		if (RESOURCES_FOLDER == null)
			RESOURCES_FOLDER = "resources";
		
		File file = new File(RESOURCES_FOLDER);
		if (!file.exists() || !file.isDirectory())
			throw new Exception(String.format(
					"Resources folder \"%1s\" does not exist",
					RESOURCES_FOLDER));
	}
	
	private static void loadInitSettings() {
		INIT_SETTINGS = new PropertyMap(new Properties());
		
		try {
			INIT_SETTINGS.load(new FileInputStream(getInitSettingsFile()));
		} catch (FileNotFoundException e) {
			try {
				new File(getInitSettingsFile()).createNewFile();
			} catch (Throwable t) {
				
			}
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading init settings",
					e);
		}
	}
	
	private static void loadDataFolder() throws Exception {
		DATA_FOLDER = getInitSettings().getStringProperty(
				"com.leclercb.taskunifier.data_folder");
		
		if (DATA_FOLDER == null)
			DATA_FOLDER = System.getProperty("com.leclercb.taskunifier.data_folder");
		
		if (DATA_FOLDER == null) {
			if (SystemUtils.IS_OS_MAC) {
				if (EqualsUtils.equalsStringIgnoreCase(
						System.getProperty("com.leclercb.taskunifier.mac_app_store"),
						"true")) {
					DATA_FOLDER = System.getProperty("ApplicationSupportDirectory");
				}
			}
		}
		
		if (DATA_FOLDER == null)
			DATA_FOLDER = "data";
		
		if (MainLoadFiles.loadFolder(DATA_FOLDER))
			FIRST_EXECUTION = true;
		
		MainLoadFiles.loadFolder(DATA_FOLDER + File.separator + "users");
	}
	
	private static void loadUserFolder() throws Exception {
		USER_FOLDER = getUserFolder(CURRENT_USER_ID);
		MainLoadFiles.loadFolder(USER_FOLDER);
	}
	
	private static void loadBackupFolder() throws Exception {
		BACKUP_FOLDER = USER_FOLDER + File.separator + "backup";
		MainLoadFiles.loadFolder(BACKUP_FOLDER);
	}
	
	private static void loadPluginsFolder() throws Exception {
		PLUGINS_FOLDER = DATA_FOLDER + File.separator + "plugins";
		MainLoadFiles.loadFolder(PLUGINS_FOLDER);
	}
	
	private static void loadUncaughtExceptionHandler() {
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(
				new EventQueueProxy());
	}
	
	private static void loadSettings() throws Exception {
		try {
			SETTINGS.load(new FileInputStream(getSettingsFile()));
		} catch (FileNotFoundException e) {
			SETTINGS.load(Resources.class.getResourceAsStream("default_settings.properties"));
			FIRST_EXECUTION = true;
		}
	}
	
	private static void loadTimeZone() {
		String id = SETTINGS.getStringProperty("date.timezone");
		
		if (id == null)
			return;
		
		TimeZone.setDefault(TimeZone.getTimeZone(id));
	}
	
	private static void loadUserId() throws Exception {
		CURRENT_USER_ID = SETTINGS.getStringProperty("general.user.last_user_id");
		
		String[] userIds = UserUtils.getInstance().getUserIds();
		for (String userId : userIds) {
			if (EqualsUtils.equals(CURRENT_USER_ID, userId))
				return;
		}
		
		if (userIds.length == 0) {
			CURRENT_USER_ID = UserUtils.getInstance().createNewUser("Default");
			return;
		}
		
		CURRENT_USER_ID = userIds[0];
	}
	
	private static void loadUserSettings() throws Exception {
		USER_SETTINGS.clear();
		
		try {
			USER_SETTINGS.load(new FileInputStream(getUserSettingsFile()));
		} catch (Exception e) {
			USER_SETTINGS.load(Resources.class.getResourceAsStream("default_user_settings.properties"));
		}
	}
	
	private static void reloadUserSettings() throws Exception {
		for (String key : USER_SETTINGS.stringPropertyNames()) {
			String value = USER_SETTINGS.getProperty(key);
			USER_SETTINGS.setStringProperty(key, value, true);
		}
	}
	
	private static void loadProxies() {
		boolean p = SETTINGS.getBooleanProperty("proxy.use_system_proxies");
		System.setProperty("java.net.useSystemProxies", p + "");
		
		SETTINGS.addPropertyChangeListener(
				"proxy.use_system_proxies",
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						boolean p = SETTINGS.getBooleanProperty("proxy.use_system_proxies");
						System.setProperty("java.net.useSystemProxies", p + "");
					}
					
				});
	}
	
	private static void loadLocale() throws Exception {
		try {
			Translations.setLocale(SETTINGS.getLocaleProperty("general.locale"));
		} catch (Throwable t) {
			SETTINGS.remove("general.locale");
			Translations.setLocale(Translations.DEFAULT_LOCALE);
		}
		
		SETTINGS.setLocaleProperty("general.locale", Translations.getLocale());
	}
	
	private static void loadModels() throws Exception {
		ContactFactory.initializeWithClass(
				GuiContact.class,
				GuiContactBean.class);
		ContextFactory.initializeWithClass(
				GuiContext.class,
				GuiContextBean.class);
		FolderFactory.initializeWithClass(GuiFolder.class, GuiFolderBean.class);
		GoalFactory.initializeWithClass(GuiGoal.class, GuiGoalBean.class);
		LocationFactory.initializeWithClass(
				GuiLocation.class,
				GuiLocationBean.class);
		NoteFactory.initializeWithClass(GuiNote.class, GuiNoteBean.class);
		TaskFactory.initializeWithClass(GuiTask.class, GuiTaskBean.class);
		
		MainLoadFiles.loadAllData(getUserFolder());
	}
	
	private static void loadLookAndFeel() throws Exception {
		// jGoodies
		Properties jgoodies = new Properties();
		jgoodies.load(Resources.class.getResourceAsStream("jgoodies_themes.properties"));
		
		for (Object key : jgoodies.keySet()) {
			LookAndFeelUtils.addLookAndFeel(new DefaultLookAndFeelDescriptor(
					"jGoodies - " + jgoodies.getProperty(key.toString()),
					key.toString()));
		}
		
		// jTattoo
		Properties jtattoo = new Properties();
		jtattoo.load(Resources.class.getResourceAsStream("jtattoo_themes.properties"));
		
		for (Object key : jtattoo.keySet()) {
			LookAndFeelUtils.addLookAndFeel(new JTattooLookAndFeelDescriptor(
					"jTattoo - " + jtattoo.getProperty(key.toString()),
					key.toString()));
		}
	}
	
	private static boolean loadApiPlugins() {
		API_PLUGINS = new PluginLoader<SynchronizerGuiPlugin>(
				SynchronizerGuiPlugin.class);
		
		API_PLUGINS.addPlugin(null, DummyGuiPlugin.getInstance());
		
		File pluginsFolder = new File(getPluginsFolder());
		
		boolean outdatedPlugins = false;
		File[] pluginFiles = pluginsFolder.listFiles();
		
		for (File file : pluginFiles) {
			try {
				PluginsUtils.loadPlugin(file);
			} catch (PluginException e) {
				switch (e.getType()) {
					case MORE_THAN_ONE_PLUGIN:
					case NO_VALID_PLUGIN:
					case OUTDATED_PLUGIN:
						outdatedPlugins = true;
						break;
					default:
						outdatedPlugins = false;
						break;
				}
				
				GuiLogger.getLogger().warning(e.getMessage());
			} catch (Throwable t) {
				GuiLogger.getLogger().log(
						Level.WARNING,
						"Unknown plugin error",
						t);
			}
		}
		
		API_PLUGINS.addListChangeListener(new ListChangeListener() {
			
			@Override
			public void listChange(ListChangeEvent evt) {
				SynchronizerGuiPlugin plugin = (SynchronizerGuiPlugin) evt.getValue();
				
				if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
					if (EqualsUtils.equals(
							Main.getUserSettings().getStringProperty(
									"plugin.synchronizer.id"),
							plugin.getId()))
						SynchronizerUtils.setSynchronizerPlugin(DummyGuiPlugin.getInstance());
				}
			}
			
		});
		
		SynchronizerUtils.setSynchronizerPlugin(SynchronizerUtils.getSynchronizerPlugin());
		
		return outdatedPlugins;
	}
	
	private static void loadSynchronizer() throws Exception {
		SynchronizerUtils.setTaskRepeatEnabled(true);
	}
	
	private static void loadShutdownHook() {
		QUITTING = false;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				boolean quit = ActionQuit.quit(true);
				
				if (!quit) {
					Synchronizing.getInstance().addPropertyChangeListener(
							Synchronizing.PROP_SYNCHRONIZING,
							new PropertyChangeListener() {
								
								@Override
								public void propertyChange(
										PropertyChangeEvent evt) {
									ActionQuit.quit(true);
								}
							});
				}
			}
			
		});
	}
	
	private static void loadCustomProtocolHandlers() {
		ProtocolUtils.registerCustomProtocolHandlers();
	}
	
	public static void quit() {
		synchronized (Main.class) {
			if (QUITTING)
				return;
			
			QUITTING = true;
		}
		
		Threads.stopAll();
		
		ACTION_SUPPORT.fireActionPerformed(0, "BEFORE_EXIT");
		
		SETTINGS.setStringProperty("general.user.last_user_id", CURRENT_USER_ID);
		
		SETTINGS.setCalendarProperty(
				"general.last_exit_date",
				Calendar.getInstance());
		
		MainSaveFiles.saveAllData();
		
		GuiLogger.getLogger().info("Exiting " + Constants.TITLE);
		
		System.exit(0);
	}
	
	public static boolean changeUser(String userId) {
		if (EqualsUtils.equals(userId, CURRENT_USER_ID))
			return false;
		
		MainSaveFiles.saveAllData();
		
		Synchronizing.getInstance().setSynchronizing(true);
		
		boolean result = false;
		
		String oldUserId = CURRENT_USER_ID;
		
		try {
			String userName = UserUtils.getInstance().getUserName(userId);
			
			CURRENT_USER_ID = userId;
			loadUserFolder();
			
			SynchronizerUtils.resetAllSynchronizersAndDeleteModels();
			
			loadUserSettings();
			UserSettingsVersion.updateSettings();
			reloadUserSettings();
			
			SynchronizerUtils.setTaskRepeatEnabled(false);
			MainLoadFiles.loadAllData(getUserFolder());
			SynchronizerUtils.setTaskRepeatEnabled(true);
			
			UserUtils.getInstance().fireSwitchedUser();
			
			result = true;
			
			GuiLogger.getLogger().info("User switched to \"" + userName + "\"");
		} catch (Exception e) {
			CURRENT_USER_ID = oldUserId;
			USER_FOLDER = DATA_FOLDER
					+ File.separator
					+ "users"
					+ File.separator
					+ CURRENT_USER_ID;
			
			GuiLogger.getLogger().log(
					Level.SEVERE,
					String.format("Error while switching user %1s", userId),
					e);
		} finally {
			Synchronizing.getInstance().setSynchronizing(false);
		}
		
		return result;
	}
	
}
