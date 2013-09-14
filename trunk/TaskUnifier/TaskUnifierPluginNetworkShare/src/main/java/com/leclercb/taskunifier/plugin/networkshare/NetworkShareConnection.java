/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.leclercb.taskunifier.api.synchronizer.Connection;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerSettingsException;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.networkshare.translations.PluginTranslations;

public class NetworkShareConnection implements Connection {
	
	private boolean connected;
	private String sharedFolder;
	private String currentUserSharedFile;
	private String[] sharedFiles;
	
	NetworkShareConnection() {
		
	}
	
	public String getSharedFolder() {
		return this.sharedFolder;
	}
	
	public String getCurrentUserSharedFile() {
		return this.currentUserSharedFile;
	}
	
	public String[] getSharedFiles() {
		return this.sharedFiles;
	}
	
	@Override
	public boolean isConnected() {
		return this.connected;
	}
	
	@Override
	public void connect() throws SynchronizerException {
		if (this.connected)
			return;
		
		try {
			String sharedFolder = PluginApi.getUserSettings().getProperty(
					"plugin.networkshare.shared_folder");
			
			if (sharedFolder == null || sharedFolder.length() == 0)
				throw new SynchronizerSettingsException(
						true,
						NetworkShareApi.getInstance().getApiId(),
						null,
						PluginTranslations.getString("error.empty_shared_folder"));
			
			File sharedFolderFile = new File(sharedFolder);
			
			if (!sharedFolderFile.exists() || !sharedFolderFile.isDirectory())
				throw new SynchronizerSettingsException(
						true,
						NetworkShareApi.getInstance().getApiId(),
						null,
						PluginTranslations.getString("error.invalid_shared_folder"));
			
			if (!sharedFolderFile.canWrite())
				throw new SynchronizerSettingsException(
						true,
						NetworkShareApi.getInstance().getApiId(),
						null,
						PluginTranslations.getString("error.cannot_write_shared_folder"));
			
			String sharedFile = sharedFolder
					+ File.separator
					+ "tu_ns_"
					+ PluginApi.getCurrentUserId()
					+ ".zip";
			
			File sharedFileFile = new File(sharedFile);
			
			if (!sharedFileFile.exists()) {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
						sharedFileFile));
				
				zos.putNextEntry(new ZipEntry("version.txt"));
				zos.write(Constants.getVersion().getBytes());
				zos.closeEntry();
				
				zos.close();
			}
			
			if (!sharedFileFile.exists() || !sharedFileFile.isFile())
				throw new SynchronizerSettingsException(
						true,
						NetworkShareApi.getInstance().getApiId(),
						null,
						PluginTranslations.getString("error.invalid_shared_folder"));
			
			if (!sharedFileFile.canWrite())
				throw new SynchronizerSettingsException(
						true,
						NetworkShareApi.getInstance().getApiId(),
						null,
						PluginTranslations.getString("error.cannot_write_shared_folder"));
			
			File[] sharedFiles = sharedFolderFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					if (!file.isFile())
						return false;
					
					if (!file.canRead())
						return false;
					
					if (!file.getName().startsWith("tu_ns_"))
						return false;
					
					if (!file.getName().endsWith(".zip"))
						return false;
					
					return true;
				}
				
			});
			
			this.sharedFiles = new String[sharedFiles.length];
			
			for (int i = 0; i < sharedFiles.length; i++) {
				this.sharedFiles[i] = sharedFiles[i].getAbsolutePath();
			}
			
			this.sharedFolder = sharedFolder;
			this.currentUserSharedFile = sharedFile;
			this.connected = true;
		} catch (SynchronizerException e) {
			throw e;
		} catch (Exception e) {
			throw new SynchronizerException(false, e.getMessage(), e);
		}
	}
	
	@Override
	public void disconnect() {
		this.sharedFolder = null;
		this.currentUserSharedFile = null;
		this.sharedFiles = null;
		this.connected = false;
	}
	
	@Override
	public void loadParameters(Properties properties) {
		
	}
	
	@Override
	public void saveParameters(Properties properties) {
		
	}
	
}
