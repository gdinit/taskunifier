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
package com.leclercb.taskunifier.gui.components.toolbar;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.apache.commons.lang3.StringUtils;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.actions.ActionAddNote;
import com.leclercb.taskunifier.gui.actions.ActionAddSubTask;
import com.leclercb.taskunifier.gui.actions.ActionAddTask;
import com.leclercb.taskunifier.gui.actions.ActionAddTemplateTask;
import com.leclercb.taskunifier.gui.actions.ActionAddTemplateTaskMenu;
import com.leclercb.taskunifier.gui.actions.ActionChangeView;
import com.leclercb.taskunifier.gui.actions.ActionConfiguration;
import com.leclercb.taskunifier.gui.actions.ActionDelete;
import com.leclercb.taskunifier.gui.actions.ActionList;
import com.leclercb.taskunifier.gui.actions.ActionManageUsers;
import com.leclercb.taskunifier.gui.actions.ActionScheduledSync;
import com.leclercb.taskunifier.gui.actions.ActionSwitchToUserMenu;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizeAndPublish;
import com.leclercb.taskunifier.gui.components.configuration.ConfigurationTab;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class DefaultToolBar extends JToolBar implements SavePropertiesListener, PropertyChangeListener {
	
	private JLabel accountLabel;
	
	public DefaultToolBar() {
		this.initialize();
	}
	
	private void initialize() {
		this.setFloatable(false);
		this.setRollover(true);
		
		this.initializeActions();
		
		this.add(Box.createHorizontalGlue());
		
		final JPopupMenu toolbarMenu = new JPopupMenu();
		toolbarMenu.add(new ActionConfiguration(
				16,
				16,
				ConfigurationTab.TOOLBAR));
		
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.isPopupTrigger()
						|| event.getButton() == MouseEvent.BUTTON3) {
					toolbarMenu.show(
							DefaultToolBar.this,
							event.getX(),
							event.getY());
				}
			}
			
		});
		
		final ActionSwitchToUserMenu userMenu = new ActionSwitchToUserMenu(
				16,
				16);
		
		this.accountLabel = new JLabel();
		this.accountLabel.setText(this.getAccountLabelText());
		
		this.accountLabel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.isPopupTrigger()
						|| event.getButton() == MouseEvent.BUTTON3) {
					userMenu.showPopupMenu(DefaultToolBar.this.accountLabel);
				}
				
				if (event.getButton() == MouseEvent.BUTTON1
						&& event.getClickCount() == 2) {
					ActionManageUsers.manageUsers();
				}
			}
			
		});
		
		Main.getUserSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getUserSettings(), this));
		
		Main.getUserSettings().addPropertyChangeListener(
				"general.user.name",
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
		
		Main.getUserSettings().addPropertyChangeListener(
				"plugin.synchronizer.id",
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
		
		this.add(this.accountLabel);
		
		this.add(Box.createHorizontalStrut(10));
	}
	
	private String getAccountLabelText() {
		String user = Main.getUserSettings().getStringProperty(
				"general.user.name");
		String account = SynchronizerUtils.getSynchronizerPlugin().getAccountLabel();
		
		if (user == null)
			user = "";
		
		if (account == null)
			account = "";
		
		if (user.length() == 0 && account.length() == 0)
			return "";
		
		if (user.length() == 0)
			return account;
		
		if (account.length() == 0)
			return user;
		
		return user + " - " + account;
	}
	
	private void initializeActions() {
		try {
			boolean added = false;
			String value = Main.getSettings().getStringProperty(
					"general.toolbar");
			
			if (value == null)
				throw new Exception();
			
			String[] actions = StringUtils.split(value, ';');
			for (String action : actions) {
				action = action.trim();
				
				try {
					ActionList l = ActionList.valueOf(action);
					
					if (!l.isFitToolBar())
						continue;
					
					if (l == ActionList.SEPARATOR) {
						this.addSeparator(new Dimension(20, 20));
						continue;
					}
					
					Action a = l.newInstance(24, 24);
					
					if (a == null)
						continue;
					
					this.add(a);
					added = true;
				} catch (Throwable t) {
					GuiLogger.getLogger().warning(
							"Cannot add action \"" + action + "\" to toolbar");
				}
			}
			
			if (!added)
				throw new Exception();
		} catch (Throwable t) {
			this.add(new ActionChangeView(24, 24));
			this.addSeparator(new Dimension(20, 20));
			this.add(new ActionAddNote(24, 24));
			this.add(new ActionAddTask(24, 24));
			this.add(new ActionAddSubTask(24, 24));
			this.add(new ActionAddTemplateTaskMenu(
					24,
					24,
					ActionAddTemplateTask.ADD_TASK_LISTENER));
			this.add(new ActionDelete(24, 24));
			this.addSeparator(new Dimension(20, 20));
			this.add(new ActionSynchronizeAndPublish(24, 24, false));
			this.add(new ActionScheduledSync(24, 24));
			this.addSeparator(new Dimension(20, 20));
			this.add(new ActionConfiguration(24, 24));
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.accountLabel.setText(DefaultToolBar.this.getAccountLabelText());
	}
	
	@Override
	public void saveProperties() {
		this.accountLabel.setText(DefaultToolBar.this.getAccountLabelText());
	}
	
}
