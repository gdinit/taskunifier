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
package com.leclercb.taskunifier.gui.components.notesearcheredit.grouper.draganddrop;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouper;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouperElement;
import com.leclercb.taskunifier.gui.commons.transfer.NoteGrouperTransferData;
import com.leclercb.taskunifier.gui.commons.transfer.NoteGrouperTransferable;
import com.leclercb.taskunifier.gui.components.notesearcheredit.grouper.NoteGrouperTable;

public class NoteGrouperTransferHandler extends TransferHandler {
	
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(NoteGrouperTransferable.NOTE_GROUPER_FLAVOR))
			return false;
		
		return true;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		NoteGrouperTable table = (NoteGrouperTable) c;
		NoteGrouperElement[] elements = table.getSelectedNoteGrouperElements();
		
		int[] indexes = new int[elements.length];
		for (int i = 0; i < elements.length; i++)
			indexes[i] = table.getNoteGrouper().getIndexOf(elements[i]);
		
		return new NoteGrouperTransferable(new NoteGrouperTransferData(indexes));
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		if (!this.canImport(support)) {
			return false;
		}
		
		Transferable t = support.getTransferable();
		NoteGrouperTransferData data = null;
		
		try {
			data = (NoteGrouperTransferData) t.getTransferData(NoteGrouperTransferable.NOTE_GROUPER_FLAVOR);
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Transfer data error", e);
			
			return false;
		}
		
		if (support.isDrop()) {
			NoteGrouperTable table = (NoteGrouperTable) support.getComponent();
			JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
			
			// Import : If insert row
			if (dl.isInsertRow()) {
				NoteGrouper grouper = table.getNoteGrouper();
				
				List<NoteGrouperElement> dragElements = new ArrayList<NoteGrouperElement>();
				for (int i : data.getElementIndexes())
					dragElements.add(grouper.getElement(i));
				
				NoteGrouperElement dropElement = table.getNoteGrouperElement(table.rowAtPoint(dl.getDropPoint()));
				
				if (dropElement == null)
					dropElement = grouper.getElement(grouper.getElementCount() - 1);
				
				for (NoteGrouperElement element : dragElements)
					grouper.removeElement(element);
				
				int index = grouper.getIndexOf(dropElement);
				
				for (NoteGrouperElement element : dragElements)
					grouper.insertElement(element, ++index);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		
	}
	
}
