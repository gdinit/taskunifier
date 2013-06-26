package lu.tudor.santec.bizcal.views.weeklist;

import java.util.Calendar;

import javax.swing.JList;

import lu.tudor.santec.bizcal.NamedCalendar;
import lu.tudor.santec.bizcal.views.WeekListViewPanel;
import lu.tudor.santec.bizcal.views.weeklist.draganddrop.EventTransferHandler;
import bizcal.common.Event;

public class WeekJList extends JList {
	
	private Calendar date;
	private WeekListViewPanel parent;
	
	public WeekJList(WeekListViewPanel parent) {
		this.parent = parent;
		
		this.setDragEnabled(true);
		this.setTransferHandler(new EventTransferHandler());
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public Calendar getDate() {
		return this.date;
	}
	
	public void movedEvent(Event event) {
		Calendar date = Calendar.getInstance();
		date.setTime(event.getStart());
		date.set(Calendar.DAY_OF_MONTH, this.date.get(Calendar.DAY_OF_MONTH));
		date.set(Calendar.MONTH, this.date.get(Calendar.MONTH));
		date.set(Calendar.YEAR, this.date.get(Calendar.YEAR));
		
		try {
			this.parent.getView().getCalendarListener().moved(
					event,
					event.get(NamedCalendar.CALENDAR_ID),
					event.getStart(),
					event.get(NamedCalendar.CALENDAR_ID),
					date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
