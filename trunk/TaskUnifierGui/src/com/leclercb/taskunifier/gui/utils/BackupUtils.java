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
package com.leclercb.taskunifier.gui.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.ListChangeSupport;
import com.leclercb.commons.api.event.listchange.ListChangeSupported;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.main.MainLoadFiles;
import com.leclercb.taskunifier.gui.main.main.MainSaveFiles;
import com.leclercb.taskunifier.gui.translations.Translations;

public final class BackupUtils implements ListChangeSupported {
	
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"yyyyMMdd_HHmmss");
	
	private static BackupUtils INSTANCE;
	
	public static BackupUtils getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BackupUtils();
		
		return INSTANCE;
	}
	
	private ListChangeSupport listChangeSupport;
	
	private Map<String, String> backups;
	
	private BackupUtils() {
		this.listChangeSupport = new ListChangeSupport(BackupUtils.class);
		
		this.backups = new HashMap<String, String>();
		
		this.initialize();
	}
	
	public int getIndexOf(String backupFolderName) {
		return ArrayUtils.indexOf(this.getBackups(), backupFolderName);
	}
	
	public int getBackupCount() {
		return this.backups.size();
	}
	
	public String getBackup(int index) {
		return this.getBackups()[index];
	}
	
	public String[] getBackups() {
		List<String> list = new ArrayList<String>();
		list.addAll(this.backups.keySet());
		Collections.sort(list);
		return list.toArray(new String[0]);
	}
	
	private void initialize() {
		File folder = new File(Main.getBackupFolder());
		File[] backups = folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return BackupUtils.this.checkbackupFolderName(
						pathname.getName(),
						false);
			}
		});
		
		for (File file : backups) {
			this.backups.put(
					file.getName(),
					this.getBackupNameFromSettings(file.getName()));
		}
	}
	
	private Calendar backupFolderNameToDate(String backupFolderName) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(FORMAT.parse(backupFolderName));
			return calendar;
		} catch (Exception e) {
			return null;
		}
	}
	
	private boolean checkbackupFolderName(
			String backupFolderName,
			boolean createIfNotExists) {
		if (this.backupFolderNameToDate(backupFolderName) == null)
			return false;
		
		String folder = Main.getBackupFolder(backupFolderName);
		
		File file = new File(folder);
		if (!file.exists()) {
			if (!createIfNotExists)
				return false;
			
			if (!file.mkdir()) {
				GuiLogger.getLogger().log(
						Level.WARNING,
						"Cannot create backup folder: " + backupFolderName);
				
				return false;
			}
		} else if (!file.isDirectory()) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Backup folder is not a directory: " + backupFolderName);
			
			return false;
		}
		
		return true;
	}
	
	public void autoBackup(int nbHours) {
		if (this.backups.size() == 0) {
			this.createNewBackup(Translations.getString("manage_backups.auto_backup_name"));
			return;
		}
		
		try {
			Calendar calendar = this.backupFolderNameToDate(this.getBackup(this.backups.size() - 1));
			
			Calendar past = Calendar.getInstance();
			past.add(Calendar.HOUR_OF_DAY, -nbHours);
			
			if (calendar.compareTo(past) <= 0)
				this.createNewBackup(Translations.getString("manage_backups.auto_backup_name"));
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot create auto backup",
					e);
		}
	}
	
	public String createNewBackup(String backupName) {
		String backupFolderName = FORMAT.format(Calendar.getInstance().getTime());
		
		if (!this.checkbackupFolderName(backupFolderName, true))
			return null;
		
		String folder = Main.getBackupFolder(backupFolderName);
		
		MainSaveFiles.copyAllData(folder, null);
		
		this.backups.put(backupFolderName, null);
		
		this.listChangeSupport.fireListChange(
				ListChangeEvent.VALUE_ADDED,
				0,
				backupFolderName);
		
		GuiLogger.getLogger().info("Backup created: " + backupFolderName);
		
		if (backupName != null && backupName.length() != 0)
			this.setBackupName(backupFolderName, backupName);
		
		return backupFolderName;
	}
	
	public boolean restoreBackup(String backupFolderName) {
		if (!this.checkbackupFolderName(backupFolderName, false))
			return false;
		
		SynchronizerUtils.resetAllSynchronizersAndDeleteModels();
		
		Synchronizing.getInstance().setSynchronizing(true);
		
		try {
			String folder = Main.getBackupFolder(backupFolderName);
			
			SynchronizerUtils.setTaskRepeatEnabled(false);
			SynchronizerUtils.setTaskRulesEnabled(false);
			
			MainLoadFiles.loadAllData(folder, null);
			
			SynchronizerUtils.setTaskRepeatEnabled(true);
			SynchronizerUtils.setTaskRulesEnabled(true);
		} finally {
			Synchronizing.getInstance().setSynchronizing(false);
		}
		
		return true;
	}
	
	public void removeBackup(String backupFolderName) {
		if (!this.checkbackupFolderName(backupFolderName, false))
			return;
		
		String folder = Main.getBackupFolder(backupFolderName);
		
		try {
			FileUtils.deleteDirectory(new File(folder));
			
			this.backups.remove(backupFolderName);
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot remove backup folder: " + backupFolderName,
					e);
			
			return;
		}
		
		this.listChangeSupport.fireListChange(
				ListChangeEvent.VALUE_REMOVED,
				0,
				backupFolderName);
		
		GuiLogger.getLogger().info("Backup removed: " + backupFolderName);
	}
	
	public void cleanBackups(int nbToKeep) {
		if (nbToKeep < 1)
			throw new IllegalArgumentException();
		
		while (this.backups.size() > nbToKeep) {
			String backupFolderName = this.getBackup(0);
			
			this.removeBackup(backupFolderName);
			this.backups.remove(backupFolderName);
		}
	}
	
	public String getBackupName(String backupFolderName) {
		return this.backups.get(backupFolderName);
	}
	
	private String getBackupNameFromSettings(String backupFolderName) {
		String folder = Main.getBackupFolder(backupFolderName);
		
		try {
			File file = new File(folder + File.separator + "backup.properties");
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			return properties.getProperty("backup.name");
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot get backup name from settings",
					e);
			
			return null;
		}
	}
	
	public void setBackupName(String backupFolderName, String backupName) {
		try {
			String folder = Main.getBackupFolder(backupFolderName);
			
			File file = new File(folder + File.separator + "backup.properties");
			
			if (!file.exists())
				file.createNewFile();
			
			Properties properties = new Properties();
			
			FileInputStream input = new FileInputStream(file);
			properties.load(input);
			input.close();
			
			properties.setProperty("backup.name", backupName);
			
			FileOutputStream output = new FileOutputStream(file);
			properties.store(output, Constants.TITLE + " Backup Settings");
			output.close();
			
			this.backups.put(backupFolderName, backupName);
			
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_CHANGED,
					this.getIndexOf(backupFolderName),
					backupFolderName);
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot set backup name \"" + backupName + "\"",
					e);
		}
	}
	
	@Override
	public void addListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.addListChangeListener(listener);
	}
	
	@Override
	public void removeListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.removeListChangeListener(listener);
	}
	
}
