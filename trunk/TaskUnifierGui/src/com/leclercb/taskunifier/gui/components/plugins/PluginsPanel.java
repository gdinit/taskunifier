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
package com.leclercb.taskunifier.gui.components.plugins;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.leclercb.taskunifier.gui.api.plugins.Plugin;
import com.leclercb.taskunifier.gui.api.plugins.PluginsUtils;
import com.leclercb.taskunifier.gui.components.plugins.list.PluginList;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.processes.plugins.ProcessInstallOrUpdatePlugin;
import com.leclercb.taskunifier.gui.processes.plugins.ProcessInstallPluginFromFile;
import com.leclercb.taskunifier.gui.swing.TUFileDialog;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class PluginsPanel extends JPanel implements ListSelectionListener {
	
	private boolean includePublishers;
	private boolean includeSynchronizers;
	
	private PluginList list;
	private JTextArea history;
	
	private boolean pluginListLoaded;
	
	private TUButtonsPanel buttonsPanel;
	
	public PluginsPanel(boolean includePublishers, boolean includeSynchronizers) {
		this.includePublishers = includePublishers;
		this.includeSynchronizers = includeSynchronizers;
		
		this.pluginListLoaded = false;
		
		this.initialize();
	}
	
	public TUButtonsPanel getButtonsPanel() {
		return this.buttonsPanel;
	}
	
	public boolean isPluginListLoaded() {
		return this.pluginListLoaded;
	}
	
	public void reloadPlugins() {
		Plugin[] plugins = PluginsUtils.loadAndUpdatePluginsFromXML(
				this.includePublishers,
				this.includeSynchronizers,
				true,
				false);
		
		if (plugins == null)
			plugins = new Plugin[] { Plugin.getDummyPlugin() };
		else
			this.pluginListLoaded = true;
		
		this.list.setPlugins(plugins);
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 5));
		
		this.list = new PluginList();
		this.list.addListSelectionListener(this);
		
		mainPanel.add(
				ComponentFactory.createJScrollPane(this.list, true),
				BorderLayout.CENTER);
		
		this.history = new JTextArea(5, 10);
		this.history.setEditable(false);
		
		mainPanel.add(
				ComponentFactory.createJScrollPane(this.history, true),
				BorderLayout.SOUTH);
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		JButton installFromFileButton = new JButton(
				Translations.getString("manage_plugins.install_from_file"));
		installFromFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				PluginsPanel.this.installPluginFromFile();
			}
			
		});
		
		this.buttonsPanel = new TUButtonsPanel();
		
		this.add(this.buttonsPanel, BorderLayout.SOUTH);
	}
	
	public void installSelectedPlugin() {
		final Plugin plugin = this.list.getSelectedPlugin();
		
		if (plugin == null)
			return;
		
		TUWorkerDialog<Void> dialog = new TUWorkerDialog<Void>(
				Translations.getString("general.manage_plugins"));
		
		ProcessInstallOrUpdatePlugin process = new ProcessInstallOrUpdatePlugin(
				plugin,
				true);
		
		dialog.setWorker(new Worker<Void>(process));
		
		dialog.setVisible(true);
		
		PluginsPanel.this.valueChanged(null);
	}
	
	public void installPluginFromFile() {
		TUFileDialog fileDialog = new TUFileDialog(
				true,
				Translations.getString("manage_plugins.install_from_file"));
		
		fileDialog.setVisible(true);
		
		if (fileDialog.isCancelled())
			return;
		
		String file = fileDialog.getFile();
		
		TUWorkerDialog<Void> dialog = new TUWorkerDialog<Void>(
				Translations.getString("general.manage_plugins"));
		
		ProcessInstallPluginFromFile process = new ProcessInstallPluginFromFile(
				new File(file),
				true);
		
		dialog.setWorker(new Worker<Void>(process));
		
		dialog.setVisible(true);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		if (evt != null && evt.getValueIsAdjusting())
			return;
		
		Plugin plugin = this.list.getSelectedPlugin();
		
		if (plugin == null) {
			this.history.setText(null);
			return;
		}
		
		this.history.setText(plugin.getHistory());
		this.history.setCaretPosition(0);
	}
	
}
