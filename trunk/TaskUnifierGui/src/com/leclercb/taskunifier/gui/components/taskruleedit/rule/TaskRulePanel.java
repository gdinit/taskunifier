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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.gui.api.rules.TaskRule;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleActions;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskRuleAction;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

public class TaskRulePanel extends JPanel implements PropertyChangeListener {
	
	private TaskRule rule;
	
	private JTextField ruleTitle;
	private JCheckBox ruleEnabled;
	private JXComboBox ruleAction;
	private JButton ruleActionConfiguration;
	
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
			
			Class<?> action = null;
			
			if (this.rule.getAction() != null)
				action = this.rule.getAction().getClass();
			
			this.ruleAction.setSelectedItem(action);
		} else {
			this.ruleTitle.setText(null);
			this.ruleEnabled.setSelected(false);
			this.ruleAction.setSelectedItem(null);
		}
		
		this.ruleTitle.setEnabled(rule != null);
		this.ruleEnabled.setEnabled(rule != null);
		this.ruleAction.setEnabled(rule != null);
		this.ruleActionConfiguration.setEnabled(rule != null
				&& rule.getAction() != null);
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
				if (TaskRulePanel.this.rule == null)
					return;
				
				TaskRulePanel.this.rule.setEnabled(TaskRulePanel.this.ruleEnabled.isSelected());
			}
			
		});
		
		builder.appendI15d("ruleedit.rule.enabled", true, this.ruleEnabled);
		
		// Action
		JPanel ruleActionPanel = new JPanel(new BorderLayout(5, 5));
		
		this.ruleAction = new JXComboBox(ArrayUtils.add(
				TaskRuleActions.getInstance().getActions().toArray(),
				0,
				null));
		this.ruleAction.setRenderer(new DefaultListRenderer(
				StringValueTaskRuleAction.INSTANCE));
		this.ruleAction.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (TaskRulePanel.this.rule == null)
					return;
				
				try {
					Class<?> action = (Class<?>) TaskRulePanel.this.ruleAction.getSelectedItem();
					
					if (action == null) {
						TaskRulePanel.this.rule.setAction(null);
						TaskRulePanel.this.ruleActionConfiguration.setEnabled(false);
						return;
					}
					
					TaskRuleAction a = (TaskRuleAction) action.newInstance();
					TaskRulePanel.this.rule.setAction(a);
					
					TaskRulePanel.this.ruleActionConfiguration.setEnabled(true);
				} catch (Exception exc) {
					ErrorInfo info = new ErrorInfo(
							Translations.getString("general.error"),
							exc.getMessage(),
							null,
							"GUI",
							null,
							Level.INFO,
							null);
					
					JXErrorPane.showDialog(FrameUtils.getCurrentFrame(), info);
					
					GuiLogger.getLogger().log(
							Level.WARNING,
							"Cannot change rule action",
							exc);
				}
			}
			
		});
		
		this.ruleActionConfiguration = new JButton(
				Translations.getString("general.configure"));
		this.ruleActionConfiguration.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TaskRulePanel.this.rule.getAction() != null)
					TaskRulePanel.this.rule.getAction().configure();
			}
			
		});
		
		ruleActionPanel.add(this.ruleAction, BorderLayout.CENTER);
		ruleActionPanel.add(this.ruleActionConfiguration, BorderLayout.EAST);
		
		builder.appendI15d("ruleedit.rule.action", true, ruleActionPanel);
		
		// Lay out the panel
		panel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.add(panel, BorderLayout.NORTH);
		
		this.ruleTitle.setEnabled(false);
		this.ruleEnabled.setEnabled(false);
		this.ruleAction.setEnabled(false);
		this.ruleActionConfiguration.setEnabled(false);
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
		
		if (evt.getPropertyName().equals(TaskRule.PROP_ACTION)) {
			Class<?> action = null;
			
			if (this.rule.getAction() != null)
				action = this.rule.getAction().getClass();
			
			if (!EqualsUtils.equals(this.ruleAction.getSelectedItem(), action))
				this.ruleAction.setSelectedItem(action);
		}
	}
	
}
