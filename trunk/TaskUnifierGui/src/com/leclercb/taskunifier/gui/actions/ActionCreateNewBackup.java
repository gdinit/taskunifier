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
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.BackupUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionCreateNewBackup extends AbstractViewAction {
	
	public ActionCreateNewBackup(int width, int height) {
		super(
				Translations.getString("action.create_new_backup"),
				ImageUtils.getResourceImage("save.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.create_new_backup"));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionCreateNewBackup.createNewBackup(false);
	}
	
	public static String createNewBackup(boolean silent) {
		String backupName = null;
		
		if (!silent) {
			backupName = askBackupName();
			
			if (backupName == null)
				return null;
		}
		
		String backupFolderName = BackupUtils.getInstance().createNewBackup(
				backupName);
		
		if (silent)
			return backupFolderName;
		
		if (backupFolderName != null) {
			JOptionPane.showMessageDialog(
					FrameUtils.getCurrentWindow(),
					Translations.getString("action.create_new_backup.success"),
					Translations.getString("general.information"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					Translations.getString("action.create_new_backup.failure"),
					null,
					"GUI",
					null,
					Level.WARNING,
					null);
			
			JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
		}
		
		return backupFolderName;
	}
	
	public static String askBackupName() {
		return JOptionPane.showInputDialog(
				FrameUtils.getCurrentWindow(),
				Translations.getString("manage_backups.new_backup_name"),
				Translations.getString("general.manage_backups"),
				JOptionPane.QUESTION_MESSAGE);
	}
	
}
