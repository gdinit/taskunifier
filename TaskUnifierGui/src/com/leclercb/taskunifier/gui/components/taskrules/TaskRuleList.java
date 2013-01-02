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
package com.leclercb.taskunifier.gui.components.taskrules;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.rules.TaskRule;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleFactory;
import com.leclercb.taskunifier.gui.commons.comparators.BasicModelComparator;
import com.leclercb.taskunifier.gui.commons.highlighters.AlternateHighlighter;
import com.leclercb.taskunifier.gui.commons.models.TaskRuleModel;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskRuleTitle;
import com.leclercb.taskunifier.gui.swing.buttons.TUAddButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TURemoveButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ComponentUtils;

abstract class TaskRuleList extends JPanel {
	
	private JTextField titleField;
	
	private JXSearchField searchField;
	
	private JXList ruleList;
	private TaskRuleRowFilter rowFilter;
	
	private JButton addButton;
	private JButton removeButton;
	
	public TaskRuleList(JTextField titleField) {
		CheckUtils.isNotNull(titleField);
		this.titleField = titleField;
		
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout(0, 3));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		TaskRuleModel model = new TaskRuleModel(false);
		
		this.ruleList = new JXList();
		this.ruleList.setModel(model);
		this.ruleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.ruleList.setCellRenderer(new DefaultListRenderer(
				StringValueTaskRuleTitle.INSTANCE));
		
		this.ruleList.setAutoCreateRowSorter(true);
		this.ruleList.setComparator(BasicModelComparator.INSTANCE_NULL_LAST);
		this.ruleList.setSortOrder(SortOrder.ASCENDING);
		this.ruleList.setSortsOnUpdates(true);
		
		this.rowFilter = new TaskRuleRowFilter();
		this.ruleList.setRowFilter(this.rowFilter);
		
		this.ruleList.setHighlighters(new AlternateHighlighter());
		
		this.ruleList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting())
					return;
				
				if (TaskRuleList.this.ruleList.getSelectedValue() == null) {
					TaskRuleList.this.ruleSelected(null);
					TaskRuleList.this.removeButton.setEnabled(false);
				} else {
					TaskRuleList.this.ruleSelected((TaskRule) TaskRuleList.this.ruleList.getSelectedValue());
					TaskRuleList.this.removeButton.setEnabled(true);
				}
			}
			
		});
		
		this.add(
				ComponentFactory.createJScrollPane(this.ruleList, true),
				BorderLayout.CENTER);
		
		this.searchField = new JXSearchField(
				Translations.getString("general.search"));
		this.searchField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TaskRuleList.this.rowFilter.setTitle(e.getActionCommand());
			}
			
		});
		
		this.rowFilter.addPropertyChangeListener(
				TaskRuleRowFilter.PROP_TITLE,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						TaskRuleList.this.searchField.setText((String) evt.getNewValue());
						TaskRuleList.this.ruleList.setRowFilter((TaskRuleRowFilter) evt.getSource());
					}
					
				});
		
		this.add(this.searchField, BorderLayout.NORTH);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("ADD")) {
					TaskRuleList.this.rowFilter.setTitle(null);
					TaskRule rule = TaskRuleFactory.getInstance().create(
							Translations.getString("rule.default.title"));
					TaskRuleList.this.setSelectedRule(rule);
					ComponentUtils.focusAndSelectTextInTextField(TaskRuleList.this.titleField);
				} else if (event.getActionCommand().equals("REMOVE")) {
					TaskRule rule = TaskRuleList.this.getSelectedRule();
					TaskRuleFactory.getInstance().markToDelete(rule);
				}
			}
			
		};
		
		this.addButton = new TUAddButton(listener);
		
		this.removeButton = new TURemoveButton(listener);
		this.removeButton.setEnabled(false);
		
		JPanel panel = new TUButtonsPanel(this.addButton, this.removeButton);
		
		this.add(panel, BorderLayout.SOUTH);
	}
	
	public TaskRule getSelectedRule() {
		return (TaskRule) this.ruleList.getSelectedValue();
	}
	
	public void setSelectedRule(TaskRule rule) {
		this.ruleList.setSelectedValue(rule, true);
	}
	
	public abstract void ruleSelected(TaskRule rule);
	
}
