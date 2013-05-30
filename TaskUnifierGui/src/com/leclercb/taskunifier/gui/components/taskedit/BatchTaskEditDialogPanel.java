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
package com.leclercb.taskunifier.gui.components.taskedit;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class BatchTaskEditDialogPanel extends TUDialogPanel {
	
	private static BatchTaskEditDialogPanel INSTANCE;
	
	protected static BatchTaskEditDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BatchTaskEditDialogPanel();
		
		return INSTANCE;
	}
	
	private JXHeader header;
	private BatchTaskEditPanel batchTaskEditPanel;
	private boolean cancelled;
	
	private ActionListener okListener;
	private ActionListener cancelListener;
	
	private BatchTaskEditDialogPanel() {
		this.initialize();
	}
	
	public Task[] getTasks() {
		return this.batchTaskEditPanel.getTasks();
	}
	
	public void setTasks(Task[] tasks) {
		if (tasks != null && tasks.length == 1) {
			this.header.setTitle(Translations.getString("header.title.edit_task"));
			this.header.setDescription(Translations.getString("header.description.edit_task"));
		} else {
			this.header.setTitle(Translations.getString("header.title.batch_edit_task"));
			this.header.setDescription(Translations.getString("header.description.batch_edit_task"));
		}
		
		this.batchTaskEditPanel.setTasks(tasks);
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.header = new JXHeader();
		this.header.setTitle(Translations.getString("header.title.batch_edit_task"));
		this.header.setDescription(Translations.getString("header.description.batch_edit_task"));
		this.header.setIcon(ImageUtils.getResourceImage("edit.png", 32, 32));
		
		this.batchTaskEditPanel = new BatchTaskEditPanel();
		this.batchTaskEditPanel.setBorder(BorderFactory.createEmptyBorder(
				10,
				10,
				10,
				10));
		
		this.add(this.header, BorderLayout.NORTH);
		this.add(ComponentFactory.createJScrollPane(
				this.batchTaskEditPanel,
				false), BorderLayout.CENTER);
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		this.okListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				BatchTaskEditDialogPanel.this.batchTaskEditPanel.editTasks();
				BatchTaskEditDialogPanel.this.cancelled = false;
				BatchTaskEditDialogPanel.this.setTasks(null);
				BatchTaskEditDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		this.cancelListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				boolean saveChanges = BatchTaskEditDialogPanel.this.askSaveChanges();
				BatchTaskEditDialogPanel.this.cancelled = !saveChanges;
				BatchTaskEditDialogPanel.this.setTasks(null);
				BatchTaskEditDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		JButton okButton = new TUOkButton(this.okListener);
		JButton cancelButton = new TUCancelButton(this.cancelListener);
		
		this.setButtons(okButton, okButton, cancelButton);
	}
	
	private boolean askSaveChanges() {
		if (!this.batchTaskEditPanel.isChanged())
			return false;
		
		String[] options = new String[] {
				Translations.getString("general.yes"),
				Translations.getString("general.no") };
		
		int result = JOptionPane.showOptionDialog(
				this,
				Translations.getString("general.save_changes"),
				Translations.getString("general.question"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		
		if (result == JOptionPane.YES_OPTION) {
			this.batchTaskEditPanel.editTasks();
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				BatchTaskEditDialogPanel.this.cancelled = false;
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				boolean saveChanges = BatchTaskEditDialogPanel.this.askSaveChanges();
				BatchTaskEditDialogPanel.this.cancelled = !saveChanges;
				BatchTaskEditDialogPanel.this.setTasks(null);
				BatchTaskEditDialogPanel.this.getDialog().setVisible(false);
			}
			
		});
		
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
