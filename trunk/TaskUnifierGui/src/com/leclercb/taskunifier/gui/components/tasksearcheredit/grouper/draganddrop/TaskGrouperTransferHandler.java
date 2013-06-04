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
package com.leclercb.taskunifier.gui.components.tasksearcheredit.grouper.draganddrop;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouper;
import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouperElement;
import com.leclercb.taskunifier.gui.commons.transfer.TaskGrouperTransferData;
import com.leclercb.taskunifier.gui.commons.transfer.TaskGrouperTransferable;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.grouper.TaskGrouperTable;

public class TaskGrouperTransferHandler extends TransferHandler {
	
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(TaskGrouperTransferable.TASK_GROUPER_FLAVOR))
			return false;
		
		return true;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		TaskGrouperTable table = (TaskGrouperTable) c;
		TaskGrouperElement[] elements = table.getSelectedTaskGrouperElements();
		
		int[] indexes = new int[elements.length];
		for (int i = 0; i < elements.length; i++)
			indexes[i] = table.getTaskGrouper().getIndexOf(elements[i]);
		
		return new TaskGrouperTransferable(new TaskGrouperTransferData(indexes));
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
		TaskGrouperTransferData data = null;
		
		try {
			data = (TaskGrouperTransferData) t.getTransferData(TaskGrouperTransferable.TASK_GROUPER_FLAVOR);
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Transfer data error", e);
			
			return false;
		}
		
		if (support.isDrop()) {
			TaskGrouperTable table = (TaskGrouperTable) support.getComponent();
			JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
			
			// Import : If insert row
			if (dl.isInsertRow()) {
				TaskGrouper grouper = table.getTaskGrouper();
				
				List<TaskGrouperElement> dragElements = new ArrayList<TaskGrouperElement>();
				for (int i : data.getElementIndexes())
					dragElements.add(grouper.getElement(i));
				
				TaskGrouperElement dropElement = table.getTaskGrouperElement(table.rowAtPoint(dl.getDropPoint()));
				
				if (dropElement == null)
					dropElement = grouper.getElement(grouper.getElementCount() - 1);
				
				for (TaskGrouperElement element : dragElements)
					grouper.removeElement(element);
				
				int index = grouper.getIndexOf(dropElement);
				
				for (TaskGrouperElement element : dragElements)
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
