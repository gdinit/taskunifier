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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.properties.events.ReloadPropertiesListener;
import com.leclercb.commons.api.properties.events.WeakReloadPropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.main.main.MainSaveFiles;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUApplyButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ConfigurationDialogPanel extends TUDialogPanel implements ConfigurationGroup, ReloadPropertiesListener {
	
	private static ConfigurationDialogPanel INSTANCE;
	
	protected static ConfigurationDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ConfigurationDialogPanel();
		
		return INSTANCE;
	}
	
	private ActionListener okListener;
	private ActionListener applyListener;
	private ActionListener cancelListener;
	
	private JTabbedPane tabbedPane;
	
	private ConfigurationPanel generalConfigurationPanel;
	private ConfigurationPanel dateConfigurationPanel;
	private ConfigurationPanel backupConfigurationPanel;
	private ConfigurationPanel proxyConfigurationPanel;
	private ConfigurationPanel searcherConfigurationPanel;
	private ConfigurationPanel themeConfigurationPanel;
	private ConfigurationPanel toolbarConfigurationPanel;
	private ConfigurationPanel listsConfigurationPanel;
	private ConfigurationPanel publicationConfigurationPanel;
	private ConfigurationPanel synchronizationConfigurationPanel;
	private ConfigurationPanel advancedConfigurationPanel;
	
	private ConfigurationDialogPanel() {
		this.initialize();
	}
	
	@Override
	public boolean setSelectedConfigurationTab(ConfigurationTab configurationTab) {
		CheckUtils.isNotNull(configurationTab);
		
		int i = 0;
		
		if (this.generalConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.dateConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.backupConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.proxyConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.searcherConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.themeConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.toolbarConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.listsConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.publicationConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.synchronizationConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.advancedConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		return false;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.configuration"));
		header.setDescription(Translations.getString("header.description.configuration"));
		header.setIcon(ImageUtils.getResourceImage("settings.png", 32, 32));
		
		this.tabbedPane = new JTabbedPane();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.add(header, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
		
		this.initializeGeneralPanel();
		this.initializeDatePanel();
		this.initializeBackupPanel();
		this.initializeProxyPanel();
		this.initializeSearcherPanel();
		this.initializeThemePanel();
		this.initializeToolbarPanel();
		this.initializeListsPanel();
		this.initializePublicationPanel();
		this.initializeSynchronizationPanel();
		this.initializeAdvancedPanel();
		
		Main.getUserSettings().addReloadPropertiesListener(
				new WeakReloadPropertiesListener(Main.getUserSettings(), this));
	}
	
	private void initializeButtonsPanel() {
		this.okListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				ConfigurationDialogPanel.this.saveAndApplyConfig();
				ConfigurationDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		this.applyListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				ConfigurationDialogPanel.this.saveAndApplyConfig();
			}
			
		};
		
		this.cancelListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				ConfigurationDialogPanel.this.cancelConfig();
				ConfigurationDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		JButton okButton = new TUOkButton(this.okListener);
		JButton cancelButton = new TUCancelButton(this.cancelListener);
		JButton applyButton = new TUApplyButton(this.applyListener);
		
		this.setButtons(okButton, okButton, cancelButton, applyButton);
	}
	
	private void initializeGeneralPanel() {
		this.generalConfigurationPanel = new GeneralConfigurationPanel(
				this,
				false,
				false);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.general"),
				ComponentFactory.createJScrollPane(
						this.generalConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeDatePanel() {
		this.dateConfigurationPanel = new DateConfigurationPanel(
				this,
				true,
				false);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.date"),
				ComponentFactory.createJScrollPane(
						this.dateConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeBackupPanel() {
		this.backupConfigurationPanel = new BackupConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.backup"),
				ComponentFactory.createJScrollPane(
						this.backupConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeProxyPanel() {
		this.proxyConfigurationPanel = new ProxyConfigurationPanel(this, true);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.proxy"),
				ComponentFactory.createJScrollPane(
						this.proxyConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeSearcherPanel() {
		this.searcherConfigurationPanel = new SearcherConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.searcher"),
				ComponentFactory.createJScrollPane(
						this.searcherConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeThemePanel() {
		this.themeConfigurationPanel = new ThemeConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.theme"),
				this.themeConfigurationPanel);
	}
	
	private void initializeToolbarPanel() {
		this.toolbarConfigurationPanel = new ToolBarConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.toolbar"),
				ComponentFactory.createJScrollPane(
						this.toolbarConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeListsPanel() {
		this.listsConfigurationPanel = new ListsConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.lists"),
				ComponentFactory.createJScrollPane(
						this.listsConfigurationPanel,
						false,
                        true));
	}
	
	private void initializePublicationPanel() {
		this.publicationConfigurationPanel = new PublicationConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.publication"),
				ComponentFactory.createJScrollPane(
						this.publicationConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeSynchronizationPanel() {
		this.synchronizationConfigurationPanel = new SynchronizationConfigurationPanel(
				this,
				false);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.synchronization"),
				ComponentFactory.createJScrollPane(
						this.synchronizationConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeAdvancedPanel() {
		this.advancedConfigurationPanel = new AdvancedConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.advanced"),
				ComponentFactory.createJScrollPane(
						this.advancedConfigurationPanel,
						false,
                        true));
	}
	
	@Override
	public void saveAndApplyConfig() {
		try {
			this.generalConfigurationPanel.saveAndApplyConfig();
			this.dateConfigurationPanel.saveAndApplyConfig();
			this.backupConfigurationPanel.saveAndApplyConfig();
			this.proxyConfigurationPanel.saveAndApplyConfig();
			this.searcherConfigurationPanel.saveAndApplyConfig();
			this.themeConfigurationPanel.saveAndApplyConfig();
			this.listsConfigurationPanel.saveAndApplyConfig();
			this.toolbarConfigurationPanel.saveAndApplyConfig();
			this.publicationConfigurationPanel.saveAndApplyConfig();
			this.synchronizationConfigurationPanel.saveAndApplyConfig();
			this.advancedConfigurationPanel.saveAndApplyConfig();
			
			MainSaveFiles.saveSettings();
			MainSaveFiles.saveUserSettings();
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					Translations.getString("error.save_settings"),
					null,
					"GUI",
					e,
					Level.SEVERE,
					null);
			
			JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
		}
	}
	
	@Override
	public void cancelConfig() {
		try {
			this.generalConfigurationPanel.cancelConfig();
			this.dateConfigurationPanel.cancelConfig();
			
			this.synchronizationConfigurationPanel.cancelConfig();
			
			this.backupConfigurationPanel.cancelConfig();
			this.proxyConfigurationPanel.cancelConfig();
			this.searcherConfigurationPanel.cancelConfig();
			this.themeConfigurationPanel.cancelConfig();
			this.listsConfigurationPanel.cancelConfig();
			this.toolbarConfigurationPanel.cancelConfig();
			this.publicationConfigurationPanel.cancelConfig();
			this.advancedConfigurationPanel.cancelConfig();
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					Translations.getString("error.save_settings"),
					null,
					"GUI",
					e,
					Level.SEVERE,
					null);
			
			JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
		}
	}
	
	@Override
	public void reloadProperties() {
		this.cancelConfig();
	}
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().getRootPane().registerKeyboardAction(
				this.okListener,
				KeyStroke.getKeyStroke(
						KeyEvent.VK_S,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		this.getDialog().getRootPane().registerKeyboardAction(
				this.cancelListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
}
