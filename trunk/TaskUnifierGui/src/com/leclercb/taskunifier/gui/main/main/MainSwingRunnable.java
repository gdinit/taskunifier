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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelDecorator;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelDescriptor;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.taskunifier.gui.actions.ActionCheckPluginVersion;
import com.leclercb.taskunifier.gui.actions.ActionCheckVersion;
import com.leclercb.taskunifier.gui.actions.ActionNewWindow;
import com.leclercb.taskunifier.gui.actions.ActionPublish;
import com.leclercb.taskunifier.gui.actions.ActionResetGeneralSearchers;
import com.leclercb.taskunifier.gui.actions.ActionSynchronize;
import com.leclercb.taskunifier.gui.actions.ActionSynchronizeAndPublish;
import com.leclercb.taskunifier.gui.components.tips.TipsDialog;
import com.leclercb.taskunifier.gui.components.welcome.LanguageDialog;
import com.leclercb.taskunifier.gui.components.welcome.WelcomeDialog;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.threads.Threads;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.BackupUtils;

public class MainSwingRunnable implements Runnable {
	
	private String[] args;
	
	public MainSwingRunnable(String[] args) {
		this.args = args;
	}
	
	@Override
	public void run() {
		this.loadLookAndFeel();
		
		try {
			this.showWelcomeWindow();
			
			if (Main.isFirstExecution())
				ActionResetGeneralSearchers.resetGeneralSearchers();
			
			MainSplashScreen.getInstance().update("Auto backup...");
			this.autoBackup();
			MainSplashScreen.getInstance().update("Cleaning backups...");
			this.cleanBackups();
			
			MainSplashScreen.getInstance().update(
					"Initializing application adapter...");
			MacApplication.initializeApplicationAdapter();
			
			MainSplashScreen.getInstance().update("Loading main window...");
			ActionNewWindow.newWindow();
			
			MainSplashScreen.getInstance().close();
			
			ActionCheckVersion.checkVersion(true);
			ActionCheckPluginVersion.checkAllPluginVersion(true);
			
			if (!Main.isFirstExecution())
				TipsDialog.getInstance().showTipsDialog(true);
			
			Main.handleArguments(this.args);
			
			if (Main.isFirstExecution()) {
				ActionSynchronizeAndPublish.synchronizeAndPublish(false);
			} else {
				boolean syncStart = Main.getUserSettings().getBooleanProperty(
						"synchronizer.sync_start");
				boolean publishStart = Main.getUserSettings().getBooleanProperty(
						"synchronizer.publish_start");
				
				if (syncStart && publishStart)
					ActionSynchronizeAndPublish.synchronizeAndPublish(false);
				else if (syncStart)
					ActionSynchronize.synchronize(false);
				else if (publishStart)
					ActionPublish.publish(false);
			}
			
			Threads.startAll();
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading gui",
					t);
			
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					"Error while loading gui",
					null,
					"GUI",
					t,
					Level.SEVERE,
					null);
			
			JXErrorPane.showDialog(null, info);
			
			Main.setQuitting(true);
			System.exit(1);
		}
	}
	
	private void loadLookAndFeel() {
		LookAndFeelDecorator.setErrorFeedbackEnabled(false);
		
		String lookAndFeel = Main.getSettings().getStringProperty(
				"theme.lookandfeel");
		
		try {
			LookAndFeelDescriptor laf = null;
			
			if (lookAndFeel != null) {
				laf = LookAndFeelUtils.getLookAndFeel(lookAndFeel);
				if (laf != null)
					laf.setLookAndFeel();
			}
			
			if (laf == null) {
				laf = LookAndFeelUtils.getLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
				if (SystemUtils.IS_OS_WINDOWS)
					laf = LookAndFeelUtils.getLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel$Default");
				
				if (SystemUtils.IS_OS_LINUX)
					laf = LookAndFeelUtils.getLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel$Blue");
				
				if (laf != null) {
					laf.setLookAndFeel();
					
					Main.getSettings().setStringProperty(
							"theme.lookandfeel",
							laf.getIdentifier());
				}
			}
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Error while setting look and feel: \""
							+ lookAndFeel
							+ "\"",
					t);
			
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					t.getMessage(),
					null,
					"GUI",
					t,
					Level.WARNING,
					null);
			
			JXErrorPane.showDialog(null, info);
		}
	}
	
	@SuppressWarnings("unused")
	private void showWelcomeWindow() {
		if (Main.isFirstExecution()) {
			MainSplashScreen.getInstance().close();
			new LanguageDialog().setVisible(true);
		}
		
		JButton quitButton = new JButton(Translations.getString("action.quit"));
		
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.setQuitting(true);
				System.exit(0);
			}
			
		});
		
		List<String> messages = new ArrayList<String>();
		TUButtonsPanel messageButtons = new TUButtonsPanel(quitButton);
		
		if ((Main.isFirstExecution() || Main.isVersionUpdated())
				&& Constants.BETA) {
			messages.add(Translations.getString(
					"welcome.message.beta",
					Constants.VERSION));
		}
		
		if (!Main.isFirstExecution()
				&& Main.isVersionUpdated()
				&& Main.getPreviousVersion() != null
				&& Main.getPreviousVersion().equals("3.1.6")) {
			messages.add("Due to a bug in version 3.1.6, the contexts, goals and locations of the tasks weren't correctly saved. "
					+ "It is recommended to restore from a backup (File -> Restore backup) or to reload the data from Toodledo (Settings -> Synchronization -> Remove all and synchronize).");
		}
		
		if (Main.isFirstExecution()
				|| (Main.isVersionUpdated()
						&& Main.getPreviousVersion() != null && Main.getPreviousVersion().compareTo(
						"3.0.0") < 0)) {
			if (Constants.BETA)
				messages.add(Translations.getString(
						"welcome.message.license_upgrade_required_beta",
						Constants.VERSION,
						"01/06/2012",
						Constants.BETA_SYNC_EXP));
			else
				messages.add(Translations.getString(
						"welcome.message.license_upgrade_required",
						Constants.VERSION,
						"01/06/2012"));
		}
		
		if (Main.isFirstExecution() || messages.size() > 0) {
			MainSplashScreen.getInstance().close();
			new WelcomeDialog(messages.toArray(new String[0]), messageButtons).setVisible(true);
		}
	}
	
	private void autoBackup() {
		int nbHours = Main.getSettings().getIntegerProperty(
				"backup.auto_backup_every");
		BackupUtils.getInstance().autoBackup(nbHours);
	}
	
	private void cleanBackups() {
		int nbToKeep = Main.getSettings().getIntegerProperty(
				"backup.keep_backups");
		BackupUtils.getInstance().cleanBackups(nbToKeep);
	}
	
}
