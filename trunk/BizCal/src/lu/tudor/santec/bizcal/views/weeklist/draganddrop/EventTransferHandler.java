package lu.tudor.santec.bizcal.views.weeklist.draganddrop;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import lu.tudor.santec.bizcal.views.weeklist.WeekJList;
import bizcal.common.Event;

public class EventTransferHandler extends TransferHandler {
	
	@Override
	public boolean canImport(TransferSupport support) {
		Transferable t = support.getTransferable();
		
		if (support.isDataFlavorSupported(EventTransferable.EVENT_FLAVOR)) {
			if (support.isDrop()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		WeekJList list = (WeekJList) c;
		Event event = (Event) list.getSelectedValue();
		
		return new EventTransferable(new EventTransferData(event));
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
		WeekJList list = (WeekJList) support.getComponent();
		
		if (support.isDataFlavorSupported(EventTransferable.EVENT_FLAVOR)) {
			// Get Drag Event
			Event dragEvent = null;
			
			try {
				EventTransferData data = (EventTransferData) t.getTransferData(EventTransferable.EVENT_FLAVOR);
				
				dragEvent = data.getEvent();
				
				if (dragEvent == null)
					return false;
			} catch (Exception e) {
				return false;
			}
			
			if (support.isDrop()) {
				list.movedEvent(dragEvent);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		
	}
	
}
