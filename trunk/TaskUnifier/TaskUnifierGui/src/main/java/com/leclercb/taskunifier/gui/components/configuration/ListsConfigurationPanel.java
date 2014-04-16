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

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class ListsConfigurationPanel extends ConfigurationPanel {
	
	private JTabbedPane tabbedPane;

	private ConfigurationPanel taskPostponeListConfigurationPanel;
	private ConfigurationPanel taskSnoozeListConfigurationPanel;
	
	public ListsConfigurationPanel(ConfigurationGroup configurationGroup) {
		super(ConfigurationTab.LISTS, configurationGroup);
		
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.tabbedPane = new JTabbedPane();
		this.add(this.tabbedPane, BorderLayout.CENTER);

		this.initializeTaskPostponeListPanel();
		this.initializeTaskSnoozeListPanel();
	}
	
	private void initializeTaskPostponeListPanel() {
		this.taskPostponeListConfigurationPanel = new TaskPostponeListConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.task_postpone_list"),
				ComponentFactory.createJScrollPane(
						this.taskPostponeListConfigurationPanel,
						false,
                        true));
	}
	
	private void initializeTaskSnoozeListPanel() {
		this.taskSnoozeListConfigurationPanel = new TaskSnoozeListConfigurationPanel(
				this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.task_snooze_list"),
				ComponentFactory.createJScrollPane(
						this.taskSnoozeListConfigurationPanel,
						false,
                        true));
	}
	
	@Override
	public boolean setSelectedConfigurationTab(ConfigurationTab configurationTab) {
		if (super.setSelectedConfigurationTab(configurationTab))
			return true;
		
		CheckUtils.isNotNull(configurationTab);
		
		int i = 0;
		
		if (this.taskPostponeListConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		i++;
		
		if (this.taskSnoozeListConfigurationPanel.setSelectedConfigurationTab(configurationTab)) {
			this.tabbedPane.setSelectedIndex(i);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void saveAndApplyConfig() {
		this.taskPostponeListConfigurationPanel.saveAndApplyConfig();
		this.taskSnoozeListConfigurationPanel.saveAndApplyConfig();
	}
	
	@Override
	public void cancelConfig() {
		this.taskPostponeListConfigurationPanel.cancelConfig();
		this.taskSnoozeListConfigurationPanel.cancelConfig();
	}
	
}
