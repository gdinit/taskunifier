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
package com.leclercb.taskunifier.gui.processes.users;

import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.main.MainLoadFiles;
import com.leclercb.taskunifier.gui.main.main.MainSaveFiles;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.settings.UserSettingsVersion;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import com.leclercb.taskunifier.gui.utils.UserUtils;

public class ProcessSwitchUser implements Process<Void> {
	
	private String userId;
	
	public ProcessSwitchUser(String userId) {
		this.setUserId(userId);
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public void setUserId(String userId) {
		CheckUtils.isNotNull(userId);
		this.userId = userId;
	}
	
	@Override
	public Void execute(final Worker<?> worker) throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		monitor.addMessage(new DefaultProgressMessage(
				Translations.getString("manage_users.start_switch_user")));
		
		if (EqualsUtils.equals(this.userId, Main.getCurrentUserId()))
			return null;
		
		ProcessUtils.invokeAndWait(new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
				MainSaveFiles.saveAllData();
				return null;
			}
			
		});
		
		Synchronizing.getInstance().setSynchronizing(true);
		
		SynchronizerUtils.setTaskRepeatEnabled(false);
		SynchronizerUtils.setTaskRulesEnabled(false);
		
		try {
			ProcessUtils.invokeAndWait(new Callable<Void>() {
				
				@Override
				public Void call() throws Exception {
					String oldUserId = Main.getCurrentUserId();
					
					String userName = null;
					
					try {
						userName = UserUtils.getInstance().getUserName(
								ProcessSwitchUser.this.userId);
						
						Main.setCurrentUserId(ProcessSwitchUser.this.userId);
						Main.loadUserFolder();
					} catch (Exception e) {
						Main.setCurrentUserId(oldUserId);
						throw e;
					}
					
					try {
						SynchronizerUtils.resetAllSynchronizersAndDeleteModels();
						
						Main.loadUserSettings();
						UserSettingsVersion.updateSettings();
						Main.reloadUserSettings();
					} catch (Exception e) {
						Main.setCurrentUserId(oldUserId);
						Main.loadUserSettings();
						throw e;
					}
					
					try {
						MainLoadFiles.loadAllData(Main.getUserFolder());
						UserUtils.getInstance().fireSwitchedUser();
						Main.reloadProVersion();
						
						GuiLogger.getLogger().info(
								"User switched to \"" + userName + "\"");
					} catch (Exception e) {
						Main.setCurrentUserId(oldUserId);
						Main.loadUserSettings();
						MainLoadFiles.loadAllData(Main.getUserFolder());
						
						GuiLogger.getLogger().log(
								Level.SEVERE,
								String.format(
										"Error while switching user %1s",
										ProcessSwitchUser.this.userId),
								e);
					}
					
					return null;
				}
				
			});
		} finally {
			SynchronizerUtils.setTaskRepeatEnabled(true);
			SynchronizerUtils.setTaskRulesEnabled(true);
			
			Synchronizing.getInstance().setSynchronizing(false);
		}
		
		monitor.addMessage(new DefaultProgressMessage(
				Translations.getString("manage_users.end_switch_user")));
		
		return null;
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
}
