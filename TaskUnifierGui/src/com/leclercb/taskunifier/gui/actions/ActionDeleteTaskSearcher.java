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
package com.leclercb.taskunifier.gui.actions;

import java.awt.event.ActionEvent;

import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherFactory;
import com.leclercb.taskunifier.gui.commons.undoableedit.TaskSearcherDeleteUndoableEdit;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionDeleteTaskSearcher extends AbstractViewTaskSearcherSelectionAction {
	
	public ActionDeleteTaskSearcher(int width, int height) {
		super(
				Translations.getString("action.delete_task_searcher"),
				ImageUtils.getResourceImage("remove.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.delete_task_searcher"));
	}
	
	@Override
	public boolean shouldBeEnabled() {
		if (!super.shouldBeEnabled())
			return false;
		
		TaskSearcher searcher = ViewUtils.getSelectedOriginalTaskSearcher();
		
		boolean enabled = false;
		
		if (searcher != null) {
			boolean foundInFactory = TaskSearcherFactory.getInstance().contains(
					searcher);
			
			if (foundInFactory && searcher.getType().isEditable())
				enabled = true;
		}
		
		return enabled;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ActionDeleteTaskSearcher.deleteTaskSearcher();
	}
	
	public static void deleteTaskSearcher() {
		TaskSearcher searcher = ViewUtils.getSelectedOriginalTaskSearcher();
		
		if (searcher == null)
			return;
		
		boolean foundInFactory = TaskSearcherFactory.getInstance().contains(
				searcher);
		
		if (foundInFactory && searcher.getType().isEditable()) {
			TaskSearcherFactory.getInstance().unregister(searcher);
			Constants.UNDO_SUPPORT.postEdit(new TaskSearcherDeleteUndoableEdit(
					searcher));
		}
	}
	
}
