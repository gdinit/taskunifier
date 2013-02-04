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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.TUDialog;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class SynchronizerPluginsDialog extends TUDialog {
	
	private static SynchronizerPluginsDialog INSTANCE;
	
	public static SynchronizerPluginsDialog getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SynchronizerPluginsDialog();
		
		return INSTANCE;
	}
	
	private PluginsPanel pluginsPanel;
	
	private SynchronizerPluginsDialog() {
		this.initialize();
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			this.pluginsPanel.reloadPlugins();
			this.setLocationRelativeTo(FrameUtils.getCurrentFrame());
		}
		
		super.setVisible(visible);
	}
	
	private void initialize() {
		this.setModal(true);
		this.setTitle(Translations.getString("general.manage_synchronizer_plugins"));
		this.setSize(650, 400);
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.manage_synchronizer_plugins"));
		header.setDescription(Translations.getString("header.description.manage_synchronizer_plugins"));
		header.setIcon(ImageUtils.getResourceImage("settings.png", 32, 32));
		this.add(header, BorderLayout.NORTH);
		
		this.pluginsPanel = new PluginsPanel(false, true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		mainPanel.add(this.pluginsPanel, BorderLayout.CENTER);
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("OK")) {
					SynchronizerPluginsDialog.this.pluginsPanel.installSelectedPlugin();
				}
				
				SynchronizerPluginsDialog.this.setVisible(false);
			}
			
		};
		
		JButton okButton = new TUOkButton(listener);
		JButton cancelButton = new TUCancelButton(listener);
		
		JPanel panel = new TUButtonsPanel(Help.getInstance().getHelpButton(
				"synchronization"), okButton, cancelButton);
		
		this.add(panel, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(okButton);
	}
	
}
