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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import com.leclercb.commons.api.properties.events.WeakUndoableEditListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.swing.undo.UndoFireManager;
import com.leclercb.commons.gui.swing.undo.events.DiscardAllEditsListener;
import com.leclercb.commons.gui.swing.undo.events.RedoListener;
import com.leclercb.commons.gui.swing.undo.events.UndoListener;
import com.leclercb.commons.gui.swing.undo.events.WeakDiscardAllEditsListener;
import com.leclercb.commons.gui.swing.undo.events.WeakRedoListener;
import com.leclercb.commons.gui.swing.undo.events.WeakUndoListener;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionUndo extends AbstractViewAction implements UndoableEditListener, UndoListener, RedoListener, DiscardAllEditsListener {
	
	private UndoFireManager undoManager;
	private UndoableEditSupport editSupport;
	
	public ActionUndo(
			int width,
			int height,
			UndoFireManager undoManager,
			UndoableEditSupport editSupport) {
		super(
				Translations.getString("action.undo"),
				ImageUtils.getResourceImage("undo.png", width, height));
		
		CheckUtils.isNotNull(undoManager);
		CheckUtils.isNotNull(editSupport);
		
		this.undoManager = undoManager;
		this.editSupport = editSupport;
		
		this.putValue(SHORT_DESCRIPTION, Translations.getString("action.undo"));
		
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_Z,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		this.updateAction();
		
		this.undoManager.addUndoListener(new WeakUndoListener(
				this.undoManager,
				this));
		this.undoManager.addRedoListener(new WeakRedoListener(
				this.undoManager,
				this));
		this.undoManager.addDiscardAllEditsListener(new WeakDiscardAllEditsListener(
				this.undoManager,
				this));
		this.editSupport.addUndoableEditListener(new WeakUndoableEditListener(
				this.editSupport,
				this));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.undoManager.canUndo())
			this.undoManager.undo();
	}
	
	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		this.updateAction();
	}
	
	@Override
	public void undoPerformed(ActionEvent event) {
		this.updateAction();
	}
	
	@Override
	public void redoPerformed(ActionEvent event) {
		this.updateAction();
	}
	
	@Override
	public void discardAllEditsPerformed(ActionEvent event) {
		this.updateAction();
	}
	
	private void updateAction() {
		this.setEnabled(this.undoManager.canUndo());
		this.putValue(NAME, this.undoManager.getUndoPresentationName());
	}
	
}
