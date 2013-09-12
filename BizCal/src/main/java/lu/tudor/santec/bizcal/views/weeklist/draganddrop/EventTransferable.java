package lu.tudor.santec.bizcal.views.weeklist.draganddrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EventTransferable implements Transferable {
	
	public static final DataFlavor EVENT_FLAVOR = new DataFlavor(
			EventTransferData.class,
			"EVENT_FLAVOR");
	
	public static final DataFlavor[] FLAVORS = { EVENT_FLAVOR };
	
	private static final List<DataFlavor> FLAVOR_LIST = Arrays.asList(FLAVORS);
	
	private EventTransferData data;
	
	public EventTransferable(EventTransferData data) {
		this.data = data;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return FLAVOR_LIST.contains(flavor);
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return this.data;
	}
	
}
