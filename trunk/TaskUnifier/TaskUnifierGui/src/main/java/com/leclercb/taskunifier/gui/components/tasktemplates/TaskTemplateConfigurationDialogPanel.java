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
package com.leclercb.taskunifier.gui.components.tasktemplates;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class TaskTemplateConfigurationDialogPanel extends TUDialogPanel {
	
	private static TaskTemplateConfigurationDialogPanel INSTANCE = null;
	
	protected static TaskTemplateConfigurationDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskTemplateConfigurationDialogPanel();
		
		return INSTANCE;
	}
	
	private TaskTemplateConfigurationPanel templateConfigurationPanel;
	private ActionListener okListener;
	
	private TaskTemplateConfigurationDialogPanel() {
		this.initialize();
	}
	
	public void setSelectedTemplate(TaskTemplate template) {
		this.templateConfigurationPanel.setSelectedTemplate(template);
	}
	
	public void focusAndSelectTextInTextField() {
		this.templateConfigurationPanel.focusAndSelectTextInTextField();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.manage_task_templates"));
		header.setDescription(Translations.getString("header.description.manage_task_templates"));
		header.setIcon(ImageUtils.getResourceImage("template.png", 32, 32));
		
		this.templateConfigurationPanel = new TaskTemplateConfigurationPanel();
		
		this.add(header, BorderLayout.NORTH);
		this.add(this.templateConfigurationPanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		this.okListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				TaskTemplateConfigurationDialogPanel.this.getDialog().setVisible(
						false);
			}
			
		};
		
		JButton helpButton = Help.getInstance().getHelpButton(
				"manage_task_templates");
		JButton okButton = new TUOkButton(this.okListener);
		
		this.setButtons(okButton, helpButton, okButton);
	}
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().getRootPane().registerKeyboardAction(
				this.okListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
}
