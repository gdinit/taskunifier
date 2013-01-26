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
package com.leclercb.taskunifier.gui.components.configuration.fields.lists;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.comparators.PropertyAccessorComparator;
import com.leclercb.taskunifier.gui.commons.values.StringValuePropertyAccessor;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.propertyaccessoredit.EditTaskPropertyAccessorDialog;
import com.leclercb.taskunifier.gui.swing.buttons.TUAddButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUEditButton;
import com.leclercb.taskunifier.gui.swing.buttons.TURemoveButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.TaskCustomColumnList;

public class TaskCustomColumnListFieldType extends ConfigurationFieldType.Panel {
	
	private JPanel panel;
	
	private JXList list;
	
	private JButton addButton;
	private JButton editButton;
	private JButton removeButton;
	
	public TaskCustomColumnListFieldType() {
		this.initialize();
		this.setPanel(this.panel);
	}
	
	private void initialize() {
		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout(10, 10));
		
		this.initializeList();
	}
	
	private void initializeList() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(5, 5));
		
		panel.add(
				new JLabel(
						Translations.getString("general.taskcustomcolumnlist")),
				BorderLayout.NORTH);
		
		EventList<PropertyAccessor<Task>> eventList = new SortedList<PropertyAccessor<Task>>(
				TaskCustomColumnList.getInstance().getEventList(),
				PropertyAccessorComparator.INSTANCE);
		
		this.list = new JXList();
		
		EventListModel<PropertyAccessor<Task>> model = new EventListModel<PropertyAccessor<Task>>(
				eventList);
		
		this.list.setModel(model);
		this.list.setSelectionModel(new EventSelectionModel<PropertyAccessor<Task>>(
				eventList));
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setCellRenderer(new DefaultListRenderer(
				StringValuePropertyAccessor.INSTANCE));
		
		panel.add(
				ComponentFactory.createJScrollPane(this.list, true),
				BorderLayout.CENTER);
		
		this.addButton = new TUAddButton(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				EditTaskPropertyAccessorDialog.getInstance().setPropertyAccessor(
						null);
				EditTaskPropertyAccessorDialog.getInstance().setVisible(true);
			}
			
		});
		
		this.editButton = new TUEditButton(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent event) {
				if (TaskCustomColumnListFieldType.this.list.getSelectedValue() == null)
					return;
				
				PropertyAccessor<Task> item = (PropertyAccessor<Task>) TaskCustomColumnListFieldType.this.list.getSelectedValue();
				EditTaskPropertyAccessorDialog.getInstance().setPropertyAccessor(
						item);
				EditTaskPropertyAccessorDialog.getInstance().setVisible(true);
			}
			
		});
		
		this.removeButton = new TURemoveButton(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent event) {
				for (Object value : TaskCustomColumnListFieldType.this.list.getSelectedValues()) {
					TaskCustomColumnList.getInstance().removeColumn(
							(PropertyAccessor<Task>) value);
					
					if (TaskCustomColumnListFieldType.this.list.getModel().getSize() > 0)
						TaskCustomColumnListFieldType.this.list.setSelectedIndex(TaskCustomColumnListFieldType.this.list.getModel().getSize() - 1);
				}
			}
			
		});
		
		panel.add(new TUButtonsPanel(
				true,
				false,
				this.addButton,
				this.editButton,
				this.removeButton), BorderLayout.SOUTH);
		
		this.panel.add(panel, BorderLayout.WEST);
	}
	
}
