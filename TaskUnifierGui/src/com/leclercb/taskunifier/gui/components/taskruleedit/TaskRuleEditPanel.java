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
package com.leclercb.taskunifier.gui.components.taskruleedit;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import com.leclercb.taskunifier.gui.api.rules.TaskRule;
import com.leclercb.taskunifier.gui.components.taskruleedit.rule.TaskRulePanel;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterElementPanel;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterElementTreeNode;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterPanel;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskRuleEditPanel extends JPanel implements TreeSelectionListener {
	
	private TaskRulePanel rulePanel;
	private TaskFilterElementPanel elementPanel;
	private TaskFilterPanel filterPanel;
	
	public TaskRuleEditPanel() {
		this.initialize();
	}
	
	public void setRule(TaskRule rule) {
		this.rulePanel.setRule(rule);
		this.filterPanel.setFilter(rule.getFilter());
	}
	
	public JTextField getRuleTitle() {
		return this.rulePanel.getRuleTitle();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.rulePanel = new TaskRulePanel();
		this.elementPanel = new TaskFilterElementPanel();
		
		this.filterPanel = new TaskFilterPanel();
		this.filterPanel.getTree().addTreeSelectionListener(this);
		
		this.rulePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.elementPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.add(this.filterPanel, BorderLayout.CENTER);
		filterPanel.add(this.elementPanel, BorderLayout.SOUTH);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab(
				Translations.getString("ruleedit.tab.general"),
				this.rulePanel);
		
		tabbedPane.addTab(
				Translations.getString("ruleedit.tab.filter"),
				filterPanel);
		
		this.add(tabbedPane, BorderLayout.CENTER);
	}
	
	public void close() {
		this.elementPanel.saveElement();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		this.elementPanel.saveElement();
		
		if (this.filterPanel.getTree().getSelectionCount() != 0) {
			TreeNode node = (TreeNode) this.filterPanel.getTree().getLastSelectedPathComponent();
			
			if (node instanceof TaskFilterElementTreeNode) {
				this.elementPanel.setElement(((TaskFilterElementTreeNode) node).getElement());
				return;
			}
		}
		
		this.elementPanel.setElement(null);
	}
	
}
