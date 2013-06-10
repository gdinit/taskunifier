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
package com.leclercb.taskunifier.gui.components.tasksearcheredit.grouper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouper;
import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouperElement;
import com.leclercb.taskunifier.gui.components.pro.ProPanel;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class TaskGrouperPanel extends JPanel {
	
	private TaskGrouper grouper;
	private TaskGrouperTable table;
	
	private JButton addButton;
	private JButton removeButton;
	
	public TaskGrouperPanel(TaskGrouper grouper) {
		this.grouper = grouper;
		
		this.initialize();
	}
	
	public TaskGrouper getGrouper() {
		return this.grouper;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new BorderLayout());
		
		// TODO: PRO
		if (Main.isProVersion()) {
			panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			
			this.table = new TaskGrouperTable(this.grouper);
			this.table.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						
						@Override
						public void valueChanged(ListSelectionEvent event) {
							if (event.getValueIsAdjusting())
								return;
							
							if (TaskGrouperPanel.this.table.getSelectedRow() == -1) {
								TaskGrouperPanel.this.removeButton.setEnabled(false);
							} else {
								TaskGrouperPanel.this.removeButton.setEnabled(true);
							}
						}
						
					});
			
			panel.add(this.table.getTableHeader(), BorderLayout.NORTH);
			
			panel.add(this.table, BorderLayout.CENTER);
			
			this.add(panel, BorderLayout.CENTER);
			
			this.initializeButtons();
		} else {
			panel.add(new ProPanel(), BorderLayout.CENTER);
			
			this.add(panel, BorderLayout.CENTER);
		}
	}
	
	private void initializeButtons() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("ADD")) {
					TaskGrouperElement element = new TaskGrouperElement(
							TaskColumnList.getInstance().get(
									TaskColumnList.TITLE));
					
					TaskGrouperPanel.this.grouper.addElement(element);
				} else {
					if (TaskGrouperPanel.this.table.getCellEditor() != null)
						TaskGrouperPanel.this.table.getCellEditor().stopCellEditing();
					
					int[] selectedRows = TaskGrouperPanel.this.table.getSelectedRows();
					
					List<TaskGrouperElement> elements = new ArrayList<TaskGrouperElement>();
					for (int selectedRow : selectedRows) {
						TaskGrouperElement element = TaskGrouperPanel.this.table.getTaskGrouperElement(selectedRow);
						if (element != null)
							elements.add(element);
					}
					
					for (TaskGrouperElement element : elements) {
						TaskGrouperPanel.this.grouper.removeElement(element);
					}
				}
			}
			
		};
		
		this.addButton = new JButton(ImageUtils.getResourceImage(
				"add.png",
				16,
				16));
		this.addButton.setActionCommand("ADD");
		this.addButton.addActionListener(listener);
		
		this.removeButton = new JButton(ImageUtils.getResourceImage(
				"remove.png",
				16,
				16));
		this.removeButton.setActionCommand("REMOVE");
		this.removeButton.addActionListener(listener);
		this.removeButton.setEnabled(false);
		
		JPanel buttonsPanel = new TUButtonsPanel(
				this.addButton,
				this.removeButton);
		
		this.add(buttonsPanel, BorderLayout.SOUTH);
	}
	
}
