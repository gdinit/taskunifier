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
package com.leclercb.taskunifier.gui.components.batchaddtasks;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class BatchAddTasksDialogPanel extends TUDialogPanel {
	
	private static BatchAddTasksDialogPanel INSTANCE;
	
	protected static BatchAddTasksDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BatchAddTasksDialogPanel();
		
		return INSTANCE;
	}
	
	private BatchAddTasksPanel batchaddTasksPanel;
	
	private BatchAddTasksDialogPanel() {
		this.initialize();
	}
	
	public BatchAddTasksPanel getBatchAddTasksPanel() {
		return this.batchaddTasksPanel;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("general.batch_add_tasks"));
		header.setDescription(Translations.getString("batch_add_tasks.insert_task_titles"));
		header.setIcon(ImageUtils.getResourceImage("batch.png", 32, 32));
		
		this.batchaddTasksPanel = new BatchAddTasksPanel();
		this.batchaddTasksPanel.setBorder(BorderFactory.createEmptyBorder(
				10,
				10,
				10,
				10));
		
		this.add(header, BorderLayout.NORTH);
		this.add(this.batchaddTasksPanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("OK")) {
					BatchAddTasksDialogPanel.this.batchaddTasksPanel.batchAddTasks();
				} else {
					BatchAddTasksDialogPanel.this.batchaddTasksPanel.reset();
				}
				
				BatchAddTasksDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		JButton helpButton = Help.getInstance().getHelpButton(
				"task_batchaddtasks");
		JButton okButton = new TUOkButton(listener);
		JButton cancelButton = new TUCancelButton(listener);
		
		this.setButtons(okButton, helpButton, okButton, cancelButton);
	}
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				BatchAddTasksDialogPanel.this.batchaddTasksPanel.reset();
			}
			
		});
	}
	
}
