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
package com.leclercb.taskunifier.gui.components.notes.table.draganddrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.commons.io.IOUtils;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.gui.actions.ActionAddNote;
import com.leclercb.taskunifier.gui.actions.ActionDuplicateNotes;
import com.leclercb.taskunifier.gui.commons.transfer.ModelTransferData;
import com.leclercb.taskunifier.gui.commons.transfer.ModelTransferable;
import com.leclercb.taskunifier.gui.components.notes.table.NoteTable;

public class NoteTransferHandler extends TransferHandler {
	
	public NoteTransferHandler() {
		// this.setDragImage(ImageUtils.getResourceImage("note.png", 48,
		// 48).getImage());
		// this.setDragImageOffset(new Point(-24, 0));
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		NoteTable table = (NoteTable) c;
		Note[] notes = table.getSelectedNotes();
		
		List<ModelId> ids = new ArrayList<ModelId>();
		for (Note note : notes)
			ids.add(note.getModelId());
		
		return new ModelTransferable(new ModelTransferData(
				ModelType.NOTE,
				ids.toArray(new ModelId[0])));
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		Transferable t = support.getTransferable();
		
		if (support.isDataFlavorSupported(ModelTransferable.MODEL_FLAVOR)) {
			try {
				ModelTransferData data = (ModelTransferData) t.getTransferData(ModelTransferable.MODEL_FLAVOR);
				
				if (!data.getType().equals(ModelType.NOTE))
					return false;
			} catch (Exception e) {
				GuiLogger.getLogger().log(
						Level.SEVERE,
						"Transfer data error",
						e);
				
				return false;
			}
			
			return true;
		} else {
			DataFlavor[] flavors = t.getTransferDataFlavors();
			for (DataFlavor flavor : flavors) {
				if (flavor.isFlavorTextType()
						&& flavor.getHumanPresentableName().equals("text/plain"))
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		if (!this.canImport(support)) {
			return false;
		}
		
		Transferable t = support.getTransferable();
		NoteTable table = (NoteTable) support.getComponent();
		
		if (support.isDataFlavorSupported(ModelTransferable.MODEL_FLAVOR)) {
			// Get Drag Note
			List<Note> dragNotes = new ArrayList<Note>();
			
			try {
				ModelTransferData data = (ModelTransferData) t.getTransferData(ModelTransferable.MODEL_FLAVOR);
				
				for (ModelId id : data.getIds())
					dragNotes.add(NoteFactory.getInstance().get(id));
			} catch (Exception e) {
				GuiLogger.getLogger().log(
						Level.SEVERE,
						"Transfer data error",
						e);
				
				return false;
			}
			
			ActionDuplicateNotes.duplicateNotes(dragNotes.toArray(new Note[0]));
			
			return true;
		} else {
			try {
				DataFlavor[] flavors = t.getTransferDataFlavors();
				for (DataFlavor flavor : flavors) {
					if (flavor.isFlavorTextType()
							&& flavor.getHumanPresentableName().equals(
									"text/plain")) {
						Reader reader = flavor.getReaderForText(t);
						String data = IOUtils.toString(reader);
						
						String[] lines = data.split("\n");
						
						String title = "";
						String note = "";
						
						if (lines.length >= 1)
							title = lines[0];
						
						for (int i = 1; i < lines.length; i++)
							note += lines[i] + "\n";
						
						Note model = ActionAddNote.addNote(title, false);
						model.setNote(note);
						
						table.refreshNotes();
						table.setSelectedNotes(new Note[] { model });
						
						return true;
					}
				}
			} catch (Throwable throwable) {
				GuiLogger.getLogger().log(
						Level.SEVERE,
						"Transfer data error",
						throwable);
				
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		
	}
	
}
