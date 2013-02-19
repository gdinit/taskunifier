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

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionQuit extends AbstractViewAction {
	
	public ActionQuit(int width, int height) {
		super(
				Translations.getString("action.quit"),
				ImageUtils.getResourceImage("exit.png", width, height));
		
		this.putValue(SHORT_DESCRIPTION, Translations.getString("action.quit"));
		
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_Q,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionQuit.quit();
	}
	
	public static boolean quit() {
		return quit(false);
	}
	
	public static boolean quit(boolean force) {
		if (Main.isQuitting())
			return true;
		
		if (!force) {
			boolean syncExit = Main.getUserSettings().getBooleanProperty(
					"synchronizer.sync_exit");
			boolean publishExit = Main.getUserSettings().getBooleanProperty(
					"synchronizer.publish_exit");
			
			if (syncExit && publishExit)
				ActionSynchronizeAndPublish.synchronizeAndPublish(false);
			else if (syncExit)
				ActionSynchronize.synchronize(false);
			else if (publishExit)
				ActionPublish.publish(false);
		}
		
		if (Synchronizing.getInstance().isSynchronizing()) {
			if (!force)
				Synchronizing.getInstance().showSynchronizingMessage();
			
			return false;
		}
		
		ViewUtils.commitAll();
		
		Main.quit();
		
		return true;
	}
	
	public static boolean quitAndApply() {
		JOptionPane.showMessageDialog(
				FrameUtils.getCurrentWindow(),
				Translations.getString("general.quit_apply_changes"),
				Translations.getString("general.information"),
				JOptionPane.INFORMATION_MESSAGE);
		
		return quit(false);
	}
	
}
