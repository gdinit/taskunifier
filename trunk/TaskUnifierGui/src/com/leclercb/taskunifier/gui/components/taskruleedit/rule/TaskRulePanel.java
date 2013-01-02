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
package com.leclercb.taskunifier.gui.components.taskruleedit.rule;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.gui.api.rules.TaskRule;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

public class TaskRulePanel extends JPanel implements PropertyChangeSupported, PropertyChangeListener {
	
	private TaskRule rule;
	
	private JTextField ruleTitle;
	private JCheckBox ruleEnabled;
	
	public TaskRulePanel() {
		this.initialize();
	}
	
	public TaskRule getRule() {
		return this.rule;
	}
	
	public void setRule(TaskRule rule) {
		if (this.rule != null) {
			this.rule.removePropertyChangeListener(this);
		}
		
		this.rule = rule;
		
		if (this.rule != null) {
			this.rule.addPropertyChangeListener(new WeakPropertyChangeListener(
					this.rule,
					this));
			
			this.ruleTitle.setText(rule.getTitle());
			this.ruleEnabled.setSelected(rule.isEnabled());
		}
		
		this.ruleTitle.setEnabled(rule != null);
	}
	
	public JTextField getRuleTitle() {
		return this.ruleTitle;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		// Title
		this.ruleTitle = new JTextField();
		this.ruleTitle.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (TaskRulePanel.this.rule == null)
					return;
				
				TaskRulePanel.this.rule.setTitle(TaskRulePanel.this.ruleTitle.getText());
			}
			
		});
		
		builder.appendI15d("ruleedit.rule.title", true, this.ruleTitle);
		
		// Enabled
		this.ruleEnabled = new JCheckBox();
		this.ruleEnabled.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				TaskRulePanel.this.rule.setEnabled(TaskRulePanel.this.ruleEnabled.isSelected());
			}
			
		});
		
		builder.appendI15d("ruleedit.rule.enabled", true, this.ruleEnabled);
		
		// Lay out the panel
		panel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.add(panel, BorderLayout.NORTH);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BasicModel.PROP_TITLE)) {
			if (!EqualsUtils.equals(this.ruleTitle.getText(), evt.getNewValue()))
				this.ruleTitle.setText((String) evt.getNewValue());
		}
		
		if (evt.getPropertyName().equals(TaskRule.PROP_ENABLED)) {
			if (!EqualsUtils.equals(
					this.ruleEnabled.isSelected(),
					evt.getNewValue()))
				this.ruleEnabled.setSelected((Boolean) evt.getNewValue());
		}
	}
	
}
