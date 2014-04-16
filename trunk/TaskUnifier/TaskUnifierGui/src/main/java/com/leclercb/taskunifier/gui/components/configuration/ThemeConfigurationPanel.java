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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import com.leclercb.commons.api.event.action.WeakActionListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class ThemeConfigurationPanel extends ConfigurationPanel implements ActionListener {
	
	private JTabbedPane tabbedPane;
	
	private int taskCustomColumnListConfigurationIndex;
	
	private ConfigurationPanel generalConfigurationPanel;
	private ConfigurationPanel taskCustomColumnListConfigurationPanel;
	private ConfigurationPanel noteColumnsConfigurationPanel;
	private ConfigurationPanel taskColumnsConfigurationPanel;
	private ConfigurationPanel noteFieldsConfigurationPanel;
	private ConfigurationPanel taskFieldsConfigurationPanel;
	private ConfigurationPanel priorityConfigurationPanel;
	private ConfigurationPanel importanceConfigurationPanel;
	
	public ThemeConfigurationPanel(ConfigurationGroup configurationGroup) {
		super(ConfigurationTab.THEME, configurationGroup);
		
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.tabbedPane = new JTabbedPane();
		this.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.initializeGeneralPanel();
		this.initializeTaskCustomColumnListPanel();
		this.initializeNoteColumnsPanel();
		this.initializeTaskColumnsPanel();
		this.initializeNoteFieldsPanel();
		this.initializeTaskFieldsPanel();
		this.initializePriorityPanel();
		this.initializeImportancePanel();
	}
	
	private void initializeGeneralPanel() {
		this.generalConfigurationPanel = new ThemeGeneralConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.general"),
				ComponentFactory.createJScrollPane(
						this.generalConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeTaskCustomColumnListPanel() {
		this.taskCustomColumnListConfigurationPanel = new TaskCustomColumnListConfigurationPanel(
				this);
		
		this.taskCustomColumnListConfigurationIndex = -1;
		this.checkTaskCustomColumnListPanel();
		
		Main.getActionSupport().addActionListener(
				new WeakActionListener(Main.getActionSupport(), this));
	}
	
	private void checkTaskCustomColumnListPanel() {
		if (Main.isProVersion()) {
			if (this.taskCustomColumnListConfigurationIndex == -1) {
				this.tabbedPane.addTab(
						Translations.getString("configuration.tab.task_custom_column_list"),
						ComponentFactory.createJScrollPane(
								this.taskCustomColumnListConfigurationPanel,
								false,
                                true));
				this.taskCustomColumnListConfigurationIndex = this.tabbedPane.getTabCount() - 1;
			}
		} else {
			if (this.taskCustomColumnListConfigurationIndex != -1) {
				this.tabbedPane.removeTabAt(this.taskCustomColumnListConfigurationIndex);
				this.taskCustomColumnListConfigurationIndex = 1;
			}
		}
	}
	
	private void initializeNoteColumnsPanel() {
		this.noteColumnsConfigurationPanel = new ThemeNoteColumnsConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.note_columns"),
				ComponentFactory.createJScrollPane(
						this.noteColumnsConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeTaskColumnsPanel() {
		this.taskColumnsConfigurationPanel = new ThemeTaskColumnsConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.task_columns"),
				ComponentFactory.createJScrollPane(
						this.taskColumnsConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeNoteFieldsPanel() {
		this.noteFieldsConfigurationPanel = new ThemeNoteFieldsConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.note_fields"),
				ComponentFactory.createJScrollPane(
						this.noteFieldsConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeTaskFieldsPanel() {
		this.taskFieldsConfigurationPanel = new ThemeTaskFieldsConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.task_fields"),
				ComponentFactory.createJScrollPane(
						this.taskFieldsConfigurationPanel,
						false));
	}
	
	private void initializePriorityPanel() {
		this.priorityConfigurationPanel = new ThemePriorityConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.priority"),
				ComponentFactory.createJScrollPane(
						this.priorityConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeImportancePanel() {
		this.importanceConfigurationPanel = new ThemeImportanceConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.importance"),
				ComponentFactory.createJScrollPane(
						this.importanceConfigurationPanel,
						false,
                        true));
	}
	
	@Override
	public boolean setSelectedConfigurationTab(ConfigurationTab configurationTab) {
		if (super.setSelectedConfigurationTab(configurationTab))
			return true;
		
		CheckUtils.isNotNull(configurationTab);
		
		int i = 0;
		
		if (this.generalConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		if (Main.isProVersion()) {
			i++;
			
			if (this.taskCustomColumnListConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
				this.tabbedPane.setSelectedIndex(i);
				return true;
			}
		}
		
		i++;
		
		if (this.noteColumnsConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.taskColumnsConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.noteFieldsConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.taskFieldsConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.priorityConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.importanceConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void saveAndApplyConfig() {
		this.generalConfigurationPanel.saveAndApplyConfig();
		this.taskCustomColumnListConfigurationPanel.saveAndApplyConfig();
		this.noteColumnsConfigurationPanel.saveAndApplyConfig();
		this.taskColumnsConfigurationPanel.saveAndApplyConfig();
		this.noteFieldsConfigurationPanel.saveAndApplyConfig();
		this.taskFieldsConfigurationPanel.saveAndApplyConfig();
		this.priorityConfigurationPanel.saveAndApplyConfig();
		this.importanceConfigurationPanel.saveAndApplyConfig();
	}
	
	@Override
	public void cancelConfig() {
		this.generalConfigurationPanel.cancelConfig();
		this.taskCustomColumnListConfigurationPanel.cancelConfig();
		this.noteColumnsConfigurationPanel.cancelConfig();
		this.taskColumnsConfigurationPanel.cancelConfig();
		this.noteFieldsConfigurationPanel.cancelConfig();
		this.taskFieldsConfigurationPanel.cancelConfig();
		this.priorityConfigurationPanel.cancelConfig();
		this.importanceConfigurationPanel.cancelConfig();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (!EqualsUtils.equals(event.getActionCommand(), "PRO_VERSION"))
			return;
		
		this.checkTaskCustomColumnListPanel();
	}
	
}
