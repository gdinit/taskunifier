package lu.tudor.santec.bizcal.views.weeklist.draganddrop;

import java.io.Serializable;

import bizcal.common.Event;

public class EventTransferData implements Serializable {
	
	private Event event;
	
	public EventTransferData(Event event) {
		this.event = event;
	}
	
	public Event getEvent() {
		return this.event;
	}
	
}
