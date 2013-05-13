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
package com.leclercb.taskunifier.gui.components.modelnote.editors.draganddrop;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.io.StringWriter;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.TransferHandler;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.commons.transfer.ModelNoteTransferable;
import com.leclercb.taskunifier.gui.components.modelnote.converters.HTML2Text;
import com.leclercb.taskunifier.gui.components.modelnote.editors.WysiwygHTMLEditorKit;

public class ModelNoteTransferHandler extends TransferHandler {
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		JEditorPane pane = (JEditorPane) c;
		
		String text = "";
		
		try {
			WysiwygHTMLEditorKit kit = (WysiwygHTMLEditorKit) pane.getEditorKit();
			StringWriter writer = new StringWriter();
			kit.write(
					writer,
					pane.getDocument(),
					pane.getSelectionStart(),
					pane.getSelectionEnd() - pane.getSelectionStart());
			text = writer.toString();
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.WARNING, e.getMessage(), e);
			text = pane.getSelectedText();
		}
		
		return new ModelNoteTransferable(
				HTML2Text.convertToBasicHtml(text),
				text);
	}
	
	@Override
	public void exportToClipboard(JComponent comp, Clipboard clip, int action)
			throws IllegalStateException {
		if (action == COPY || action == MOVE) {
			clip.setContents(this.createTransferable(comp), null);
			
			if (action == MOVE) {
				JEditorPane pane = (JEditorPane) comp;
				pane.replaceSelection("");
			}
		}
	}
	
}
