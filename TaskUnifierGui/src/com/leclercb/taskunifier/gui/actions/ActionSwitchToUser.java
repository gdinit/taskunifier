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

import java.awt.event.ActionEvent;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.processes.users.ProcessSwitchUser;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.UserUtils;

public class ActionSwitchToUser extends AbstractViewAction {
	
	private String userId;
	
	public ActionSwitchToUser(int width, int height, String userId) {
		super(
				Translations.getString(
						"action.switch_user",
						UserUtils.getInstance().getUserName(userId)),
				ImageUtils.getResourceImage("user.png", width, height));
		
		CheckUtils.isNotNull(userId);
		this.userId = userId;
		
		this.putValue(SHORT_DESCRIPTION, Translations.getString(
				"action.switch_user",
				UserUtils.getInstance().getUserName(userId)));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionSwitchToUser.switchToUser(this.userId);
	}
	
	public static boolean switchToUser(String userId) {
		TUWorkerDialog<Void> dialog = new TUWorkerDialog<Void>(
				Translations.getString("manage_users.switch_user"));
		
		ProcessSwitchUser process = new ProcessSwitchUser(userId);
		
		dialog.setWorker(new Worker<Void>(process));
		
		dialog.setVisible(true);
		
		try {
			dialog.getResult();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
