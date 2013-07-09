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
package com.leclercb.taskunifier.gui.components.notesearcheredit.grouper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouper;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouperElement;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.pro.ProPanel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class NoteGrouperPanel extends JPanel {
	
	private NoteGrouper grouper;
	private NoteGrouperTable table;
	
	private JButton addButton;
	private JButton removeButton;
	
	public NoteGrouperPanel(NoteGrouper grouper) {
		this.grouper = grouper;
		
		this.initialize();
	}
	
	public NoteGrouper getGrouper() {
		return this.grouper;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		if (Main.isProVersion()) {
			this.table = new NoteGrouperTable(this.grouper);
			this.table.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						
						@Override
						public void valueChanged(ListSelectionEvent event) {
							if (event.getValueIsAdjusting())
								return;
							
							if (NoteGrouperPanel.this.table.getSelectedRow() == -1) {
								NoteGrouperPanel.this.removeButton.setEnabled(false);
							} else {
								NoteGrouperPanel.this.removeButton.setEnabled(true);
							}
						}
						
					});
			
			panel.add(this.table.getTableHeader(), BorderLayout.NORTH);
			
			panel.add(this.table, BorderLayout.CENTER);
			
			this.add(panel, BorderLayout.CENTER);
			
			this.initializeButtons();
		} else {
			panel.add(new ProPanel(true), BorderLayout.CENTER);
			
			this.add(panel, BorderLayout.CENTER);
		}
	}
	
	private void initializeButtons() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("ADD")) {
					NoteGrouperElement element = new NoteGrouperElement(
							NoteColumnList.getInstance().get(
									NoteColumnList.TITLE), SortOrder.ASCENDING);
					
					NoteGrouperPanel.this.grouper.addElement(element);
				} else {
					if (NoteGrouperPanel.this.table.getCellEditor() != null)
						NoteGrouperPanel.this.table.getCellEditor().stopCellEditing();
					
					int[] selectedRows = NoteGrouperPanel.this.table.getSelectedRows();
					
					List<NoteGrouperElement> elements = new ArrayList<NoteGrouperElement>();
					for (int selectedRow : selectedRows) {
						NoteGrouperElement element = NoteGrouperPanel.this.table.getNoteGrouperElement(selectedRow);
						if (element != null)
							elements.add(element);
					}
					
					for (NoteGrouperElement element : elements) {
						NoteGrouperPanel.this.grouper.removeElement(element);
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
