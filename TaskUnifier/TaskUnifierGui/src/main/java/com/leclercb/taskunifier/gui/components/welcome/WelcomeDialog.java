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
package com.leclercb.taskunifier.gui.components.welcome;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.actions.ActionManageSynchronizerPlugins;
import com.leclercb.taskunifier.gui.components.configuration.DateConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.GeneralConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.ProxyConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.SynchronizationConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.components.welcome.panels.CardInterface;
import com.leclercb.taskunifier.gui.components.welcome.panels.CardPanel;
import com.leclercb.taskunifier.gui.components.welcome.panels.SettingsPanel;
import com.leclercb.taskunifier.gui.components.welcome.panels.WelcomePanel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TUDialog;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ConnectionUtils;

public class WelcomeDialog extends TUDialog implements ConfigurationGroup {
	
	private List<CardPanel> panels;
	
	private JPanel cardPanel;
	private int currentPanel;
	
	private JButton previousButton;
	private JButton nextButton;
	
	public WelcomeDialog() {
		this(null, null);
	}
	
	public WelcomeDialog(String[] messages, TUButtonsPanel messageButtons) {
		this.panels = new ArrayList<CardPanel>();
		
		this.initialize();
		
		if (!Main.isFirstExecution()) {
			this.addPanel(new WelcomePanel("MESSAGES", messages, messageButtons));
		} else {
			this.addPanel(new WelcomePanel("MESSAGES", messages, messageButtons));
			
			this.addPanel(new SettingsPanel(
					"SETTINGS_PROXY",
					Translations.getString("configuration.tab.proxy"),
					new ProxyConfigurationPanel(this, false)) {
				
				@Override
				public void display() {
					try {
						HttpResponse r = ConnectionUtils.testConnection(
								true,
								true);
						
						if (r != null && r.isSuccessfull())
							WelcomeDialog.this.setPanelVisible(
									"SETTINGS_PROXY",
									false);
					} catch (Exception e) {
						
					}
				}
				
			});
			
			this.addPanel(new SettingsPanel(
					"SETTINGS_GENERAL",
					Translations.getString("configuration.tab.general"),
					new GeneralConfigurationPanel(this, false, true)));
			
			this.addPanel(new SettingsPanel(
					"SETTINGS_DATE",
					Translations.getString("configuration.tab.date"),
					new DateConfigurationPanel(this, false, true)));
			
			if (Main.isProVersion()) {
				this.addPanel(new SettingsPanel(
						"SETTINGS_SYNCHRONIZATION",
						Translations.getString("configuration.tab.synchronization"),
						new SynchronizationConfigurationPanel(this, true),
						new CardInterface() {
							
							@Override
							public boolean next() {
								return true;
							}
							
							@Override
							public void display() {
								ActionManageSynchronizerPlugins.manageSynchronizerPlugins(true);
							}
							
						}));
			}
		}
	}
	
	public WelcomeDialog(List<CardPanel> panels) {
		this.panels = new ArrayList<CardPanel>();
		
		this.initialize();
		
		CheckUtils.isNotNull(panels);
		
		if (panels.size() == 0)
			throw new IllegalArgumentException();
		
		for (CardPanel panel : panels) {
			this.addPanel(panel);
		}
	}
	
	private void initialize() {
		this.setModal(true);
		this.setTitle(Translations.getString("general.welcome"));
		this.setSize(900, 500);
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		if (this.getOwner() != null)
			this.setLocationRelativeTo(this.getOwner());
		else
			this.setLocationRelativeTo(null);
		
		this.currentPanel = 0;
		
		this.cardPanel = new JPanel();
		this.cardPanel.setLayout(new CardLayout());
		this.add(this.cardPanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		this.previousButton = new JButton(
				Translations.getString("general.previous"));
		
		this.nextButton = new JButton(Translations.getString("general.next"));
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("PREVIOUS")) {
					WelcomeDialog.this.previous();
				}
				
				if (event.getActionCommand().equals("NEXT")) {
					WelcomeDialog.this.next();
				}
			}
			
		};
		
		this.previousButton.setActionCommand("PREVIOUS");
		this.previousButton.addActionListener(listener);
		
		this.nextButton.setActionCommand("NEXT");
		this.nextButton.addActionListener(listener);
		
		this.checkButtonsState();
		
		JPanel panel = new TUButtonsPanel(
				false,
				false,
				this.previousButton,
				this.nextButton);
		
		this.add(panel, BorderLayout.SOUTH);
	}
	
	public void addPanel(CardPanel panel) {
		this.panels.add(panel);
		
		if (panel.displayInScrollPane())
			this.cardPanel.add(
					ComponentFactory.createJScrollPane(panel, false),
					panel.getID());
		else
			this.cardPanel.add(panel, panel.getID());
		
		this.checkButtonsState();
	}
	
	public void setPanelVisible(String id, boolean visible) {
		int i = 0;
		for (CardPanel panel : this.panels) {
			if (EqualsUtils.equals(panel.getID(), id)) {
				panel.setVisible(visible);
				
				if (this.currentPanel == i)
					this.next();
				
				break;
			}
			
			i++;
		}
		
		this.checkButtonsState();
	}
	
	public void previous() {
		this.panels.get(this.currentPanel).saveAndApplyConfig();
		
		if (this.currentPanel != 0) {
			this.currentPanel--;
			((CardLayout) this.cardPanel.getLayout()).previous(this.cardPanel);
			
			if (!this.panels.get(this.currentPanel).isVisible()) {
				this.previous();
				return;
			}
		}
		
		this.checkButtonsState();
	}
	
	public void next() {
		this.panels.get(this.currentPanel).saveAndApplyConfig();
		
		if (this.panels.get(this.currentPanel).next()) {
			if (this.currentPanel < this.panels.size() - 1) {
				this.currentPanel++;
				((CardLayout) this.cardPanel.getLayout()).next(this.cardPanel);
				
				if (!this.panels.get(this.currentPanel).isVisible()) {
					this.next();
					return;
				} else {
					this.panels.get(this.currentPanel).display();
				}
				
				this.checkButtonsState();
			} else {
				this.setVisible(false);
				this.dispose();
			}
		}
	}
	
	private void checkButtonsState() {
		this.previousButton.setEnabled(false);
		
		for (int i = this.currentPanel; i > 0; i--)
			if (this.panels.get(i).isVisible())
				this.previousButton.setEnabled(true);
		
		this.nextButton.setText(Translations.getString("general.finish"));
		
		for (int i = this.currentPanel + 1; i < this.panels.size(); i++)
			if (this.panels.get(i).isVisible())
				this.nextButton.setText(Translations.getString("general.next"));
	}
	
	@Override
	public boolean setSelectedConfigurationTab(ConfigurationTab configurationTab) {
		return false;
	}
	
	@Override
	public void saveAndApplyConfig() {
		for (CardPanel panel : this.panels)
			panel.saveAndApplyConfig();
	}
	
	@Override
	public void cancelConfig() {
		for (CardPanel panel : this.panels)
			panel.cancelConfig();
	}
	
}
