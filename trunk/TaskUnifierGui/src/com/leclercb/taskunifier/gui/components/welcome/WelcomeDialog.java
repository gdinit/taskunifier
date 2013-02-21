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
package com.leclercb.taskunifier.gui.components.welcome;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.actions.ActionManageSynchronizerPlugins;
import com.leclercb.taskunifier.gui.components.configuration.DateConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.GeneralConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.ProxyConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.SynchronizationConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.welcome.panels.CardInterface;
import com.leclercb.taskunifier.gui.components.welcome.panels.CardPanel;
import com.leclercb.taskunifier.gui.components.welcome.panels.LicensePanel;
import com.leclercb.taskunifier.gui.components.welcome.panels.SettingsPanel;
import com.leclercb.taskunifier.gui.components.welcome.panels.WelcomePanel;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TUDialog;
import com.leclercb.taskunifier.gui.swing.TUWorker;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.HttpUtils;

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
					new ProxyConfigurationPanel(this, false)));
			
			// TODO: PRO
			if (Main.isTmpProVersion()) {
				this.addPanel(new LicensePanel("LICENSE"));
			}
			
			this.addPanel(new SettingsPanel(
					"SETTINGS_GENERAL",
					Translations.getString("configuration.tab.general"),
					new GeneralConfigurationPanel(this, false, true)));
			
			this.addPanel(new SettingsPanel(
					"SETTINGS_DATE",
					Translations.getString("configuration.tab.date"),
					new DateConfigurationPanel(this, false)));
			
			// TODO: hide when non pro
			this.addPanel(new SettingsPanel(
					"SETTINGS_SYNCHRONIZATION",
					Translations.getString("configuration.tab.synchronization"),
					new SynchronizationConfigurationPanel(this, true),
					new CardInterface() {
						
						@Override
						public boolean allowNext() {
							return true;
						}
						
						@Override
						public void display() {
							ActionManageSynchronizerPlugins.manageSynchronizerPlugins();
						}
						
					}));
		}
	}
	
	public WelcomeDialog(List<CardPanel> panels) {
		CheckUtils.isNotNull(panels);
		
		if (panels.size() == 0)
			throw new IllegalArgumentException();
		
		this.panels = new ArrayList<CardPanel>(panels);
		
		this.initialize();
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
		
		JPanel panel = new TUButtonsPanel(this.previousButton, this.nextButton);
		
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
		for (CardPanel panel : this.panels) {
			if (EqualsUtils.equals(panel.getID(), id)) {
				panel.setVisible(visible);
				break;
			}
		}
		
		this.checkButtonsState();
	}
	
	public void previous() {
		this.panels.get(this.currentPanel).saveAndApplyConfig();
		
		if (this.currentPanel != 0) {
			this.currentPanel--;
			((CardLayout) this.cardPanel.getLayout()).previous(this.cardPanel);
			
			if (!this.panels.get(this.currentPanel).isVisible()) {
				this.cardPanel.remove(this.panels.get(this.currentPanel));
				this.previous();
			}
		}
		
		this.checkButtonsState();
	}
	
	public void next() {
		this.panels.get(this.currentPanel).saveAndApplyConfig();
		
		if (this.panels.get(this.currentPanel).allowNext()) {
			if (this.currentPanel < this.panels.size() - 1) {
				this.currentPanel++;
				((CardLayout) this.cardPanel.getLayout()).next(this.cardPanel);
				
				if (!this.panels.get(this.currentPanel).isVisible()) {
					this.cardPanel.remove(this.panels.get(this.currentPanel));
					this.next();
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
		if (this.currentPanel == 0)
			this.previousButton.setEnabled(false);
		else
			this.previousButton.setEnabled(true);
		
		if (this.currentPanel == this.panels.size() - 1)
			this.nextButton.setText(Translations.getString("general.finish"));
		else
			this.nextButton.setText(Translations.getString("general.next"));
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
	
	private void testConnection() {
		TUWorkerDialog<HttpResponse> dialog = new TUWorkerDialog<HttpResponse>(
				Translations.getString("configuration.proxy.test_connection"));
		
		ProgressMonitor monitor = new ProgressMonitor();
		monitor.addListChangeListener(dialog);
		
		dialog.setWorker(new TUWorker<HttpResponse>(monitor) {
			
			@Override
			protected HttpResponse longTask() throws Exception {
				this.publish(new DefaultProgressMessage(
						Translations.getString("configuration.proxy.test_connection")));
				
				final HttpResponse response = new HttpResponse();
				
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							HttpResponse r = HttpUtils.getHttpGetResponse(new URI(
									Constants.TEST_CONNECTION));
							
							response.setCode(r.getCode());
							response.setMessage(r.getMessage());
							response.setBytes(r.getBytes());
						} catch (Throwable t) {
							
						}
					}
					
				});
				
				thread.start();
				thread.wait(1000);
				
				return new HttpResponse(
						response.getCode(),
						response.getMessage(),
						response.getBytes());
			}
			
		});
		
		dialog.setVisible(true);
	}
	
}
