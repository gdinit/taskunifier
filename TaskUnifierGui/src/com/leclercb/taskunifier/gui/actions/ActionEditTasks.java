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
package com.leclercb.taskunifier.gui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.components.taskedit.BatchTaskEditDialog;
import com.leclercb.taskunifier.gui.components.taskedit.BatchTaskEditDialogPanel;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionEditTasks extends AbstractViewTaskSelectionAction {
	
	public ActionEditTasks(int width, int height) {
		super(
				Translations.getString("action.edit_tasks"),
				ImageUtils.getResourceImage("edit.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.edit_tasks"));
		
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_E,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	@Override
	public boolean shouldBeEnabled() {
		if (!super.shouldBeEnabled())
			return false;
		
		return ViewUtils.getSelectedTasks().length != 0;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ActionEditTasks.editTasks();
	}
	
	public static boolean editTasks() {
		Task[] tasks = ViewUtils.getSelectedTasks();
		return editTasks(tasks, true);
	}
	
	public static boolean editTasks(Task[] tasks, boolean select) {
		if (tasks == null || tasks.length == 0)
			return false;
		
		Task[] previousSelectedTasks = ViewUtils.getSelectedTasks();
		
		BatchTaskEditDialogPanel.getInstance().setTasks(tasks);
		new BatchTaskEditDialog().setVisible(true);
		boolean edited = !BatchTaskEditDialogPanel.getInstance().isCancelled();
		
		if (edited) {
			if (select)
				ViewUtils.setSelectedTasks(tasks);
			else
				ViewUtils.setSelectedTasks(previousSelectedTasks);
		}
		
		return edited;
	}
	
}
