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
package com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterElementTreeNode;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterTreeNode;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionAddFilter extends AbstractAction implements TreeSelectionListener {
	
	private TaskFilterActions filterActions;
	
	public ActionAddFilter(TaskFilterActions filterActions) {
		super(
				Translations.getString("searcheredit.add_filter"),
				ImageUtils.getResourceImage("add.png", 16, 16));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("searcheredit.add_filter"));
		
		CheckUtils.isNotNull(filterActions);
		this.filterActions = filterActions;
		
		this.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		this.filterActions.actionAddFilter();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreeNode node = (TreeNode) event.getPath().getLastPathComponent();
		
		if (node == null)
			return;
		
		if (node instanceof TaskFilterTreeNode) {
			this.setEnabled(true);
		} else if (node instanceof TaskFilterElementTreeNode) {
			this.setEnabled(false);
		} else {
			this.setEnabled(false);
		}
	}
	
}
