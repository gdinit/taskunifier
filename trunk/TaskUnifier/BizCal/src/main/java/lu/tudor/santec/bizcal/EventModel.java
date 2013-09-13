/*******************************************************************************
 * Copyright (c) 2007 by CRP Henri TUDOR - SANTEC LUXEMBOURG 
 * check http://www.santec.tudor.lu for more information
 *  
 * Contributor(s):
 * Johannes Hermen  johannes.hermen(at)tudor.lu                            
 * Martin Heinemann martin.heinemann(at)tudor.lu  
 *  
 * This library is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License (version 2.1)
 * as published by the Free Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but     
 * WITHOUT ANY WARRANTY; without even the implied warranty of               
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        
 * Lesser General Public License for more details.                          
 * 
 * You should have received a copy of the GNU Lesser General Public         
 * License along with this library; if not, write to the Free Software      
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *******************************************************************************/
/**
 * @author Martin Heinemann martin.heinemann@tudor.lu
 *
 *
 *
 * @version
 * <br>$Log: EventModel.java,v $
 * <br>Revision 1.4  2008/04/08 13:17:53  heine_
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.3  2008/01/21 14:14:17  heine_
 * <br>code cleanup and java doc
 * <br>
 * <br>Revision 1.12  2007/06/26 13:10:51  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.11  2007/06/26 08:44:15  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.10  2007/06/20 12:08:17  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.9  2007/06/15 07:00:38  hermen
 * <br>changed translatrix keys
 * <br>
 * <br>Revision 1.8  2007/06/08 12:21:10  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.7  2007/06/06 11:23:01  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.6  2007/06/05 08:33:39  heinemann
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.5  2007/05/30 11:41:18  hermen
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.4  2007/05/30 11:26:14  hermen
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.3  2007/05/30 07:17:46  hermen
 * <br>*** empty log message ***
 * <br>
 * <br>Revision 1.2  2007/05/25 13:44:00  heinemann
 * <br>pres-weekend checkin
 * <br>
 * <br>Revision 1.1  2007/05/22 09:14:10  heinemann
 * <br>*** empty log message ***
 * <br>
 *
 */
package lu.tudor.santec.bizcal;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import lu.tudor.santec.bizcal.resources.BizCalTranslations;
import lu.tudor.santec.bizcal.util.ObservableEventList;
import bizcal.common.Calendar;
import bizcal.common.CalendarModel;
import bizcal.common.Event;
import bizcal.swing.CalendarView;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.TimeOfDay;
import bizcal.util.Tuple;

/**
 * @author martin.heinemann@tudor.lu 21.05.2007 15:18:10
 * 
 * 
 * @version <br>
 *          $Log: EventModel.java,v $
 *          Revision 1.4 2008/04/08 13:17:53 heine_
 *          *** empty log message ***
 * 
 *          Revision 1.3 2008/01/21 14:14:17 heine_
 *          code cleanup and java doc
 * 
 *          Revision 1.12 2007/06/26 13:10:51 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.11 2007/06/26 08:44:15 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.10 2007/06/20 12:08:17 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.9 2007/06/15 07:00:38 hermen
 *          changed translatrix keys
 * 
 *          Revision 1.8 2007/06/08 12:21:10 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.7 2007/06/06 11:23:01 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.6 2007/06/05 08:33:39 heinemann
 *          *** empty log message ***
 * 
 *          Revision 1.5 2007/05/30 11:41:18 hermen
 *          *** empty log message ***
 * 
 *          Revision 1.4 2007/05/30 11:26:14 hermen
 *          *** empty log message ***
 * 
 *          Revision 1.3 2007/05/30 07:17:46 hermen
 *          *** empty log message ***
 * 
 *          Revision 1.2 2007/05/25 13:44:00 heinemann
 *          pres-weekend checkin
 * 
 *          Revision 1.1 2007/05/22 09:14:10 heinemann
 *          *** empty log message ***
 * 
 * 
 */
public class EventModel extends CalendarModel.BaseImpl implements Observer {
	
	public static final int TYPE_DAY = 1;
	public static final int TYPE_WEEK = 2;
	public static final int TYPE_MONTH = 3;
	
	private ObservableEventList events = null;
	
	private DateInterval interval;
	
	private Calendar cal;
	
	// Default
	private int days = 1;
	
	private CalendarView calendarView;
	private int type;
	private Tuple hourInterval;
	private int weekStart = java.util.Calendar.getInstance().getFirstDayOfWeek();
	private int weekEnd = (this.weekStart + 6) % 7;
	
	/**
	 * 
	 * @param days
	 *            number of days to display
	 * @param type
	 *            type of the view that is using the model
	 */
	public EventModel(int type) {
		/* ================================================== */
		this(null, type, null);
		/* ================================================== */
	}
	
	public EventModel(int type, Tuple hourInterval) {
		/* ================================================== */
		this(null, type, hourInterval);
		/* ================================================== */
	}
	
	public EventModel(List eventList, int type) {
		/* ================================================== */
		this(eventList, type, null);
		/* ================================================== */
	}
	
	/**
	 * @param days
	 *            number of days to display
	 * @param eventList
	 *            the data list on which the model should work
	 * @param type
	 *            type of view
	 * @param hourInterval
	 *            subset of hours to display
	 */
	public EventModel(List eventList, int type, Tuple hourInterval) {
		/* ================================================== */
		// this.days = days;
		this.type = type;
		this.events = (ObservableEventList) eventList;
		this.hourInterval = hourInterval;
		// check if we have a null list
		if (eventList == null) {
			/* ------------------------------------------------------- */
			this.events = new ObservableEventList();
			/* ------------------------------------------------------- */
		}
		this.events.addObserver(this);
		// -------------------------------------------------------------
		// create the initial date
		// -------------------------------------------------------------
		// Date start = new Date();
		// start = DateUtil.getStartOfWeek(new Date());
		
		// -------------------------------------------------------------
		// create a new Calendar for this view
		// -------------------------------------------------------------
		this.cal = new Calendar();
		this.cal.setId(this.hashCode());
		
		// set start date, mostly the current date
		this.setDate(DateUtil.getStartOfWeek(new Date()));
		/* ================================================== */
	}
	
	@Override
	public List<Event> getEvents(Object calId) throws Exception {
		return this.events;
	}
	
	@Override
	public List getSelectedCalendars() throws Exception {
		return Collections.nCopies(1, this.cal);
	}
	
	@Override
	public DateInterval getInterval() {
		return this.interval;
	}
	
	/**
	 * Sets the interval of weekdays to display
	 * 
	 * @param startDay
	 * @param endDay
	 */
	public void setWeekdayStartEnd(int startDay, int endDay) {
		/* ================================================== */
		this.weekStart = startDay;
		this.weekEnd = endDay;
		
		this.days = DateUtil.getDiffDay(this.weekStart, this.weekEnd);
		
		this.setDate(DateUtil.getStartOfWeek(new Date()));
		/* ================================================== */
	}
	
	public void setDate(Date date) {
		try {
			// Date start = (Date)date.clone();
			// start.setHours(7);
			// Date end = (Date)date.clone();
			// end.setHours(20);
			// interval = new DateInterval(start, end);
			// Date start = DateUtil.round2Week(new Date());
			Date start = null;
			Date end = null;
			switch (this.type) {
				case TYPE_DAY:
					start = DateUtil.round2Day(date);
					end = DateUtil.getDiffDay(start, this.days);
					this.cal.setSummary(BizCalTranslations.getString("bizcal.DAY_VIEW"));
					break;
				case TYPE_WEEK:
					// start = DateUtil.getStartOfWeek(date);
					start = DateUtil.setDayOfWeek(date, this.weekStart);
					end = DateUtil.getDiffDay(start, this.days);
					this.cal.setSummary(BizCalTranslations.getString("bizcal.WEEK_VIEW"));
					break;
				case TYPE_MONTH:
					start = DateUtil.round2Month(date);
					end = DateUtil.getDiffDay(start, 31);
					this.cal.setSummary(BizCalTranslations.getString("bizcal.MONTH_VIEW"));
					break;
				default:
					break;
			}
			if (this.hourInterval != null) {
				this.setViewStart(new TimeOfDay(DateUtil.round2Hour(
						start,
						(Integer) this.hourInterval.elementAt(0))));
				this.setViewEnd(new TimeOfDay(DateUtil.round2Hour(
						end,
						(Integer) this.hourInterval.elementAt(1))));
			}
			this.interval = new DateInterval(start, end);
			
			this.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addEvent(Event event) {
		/* ================================================== */
		if (event != null)
			this.events.add(event);
		/* ================================================== */
	}
	
	/**
	 * Add calendarview for refreshing
	 * 
	 * @param cView
	 */
	public void addCalendarView(CalendarView calendarView) {
		/* ================================================== */
		this.calendarView = calendarView;
		
		this.events.notifyObservers();
		/* ================================================== */
	}
	
	@Override
	public void update(Observable o, Object arg) {
		/* ====================================================== */
		try {
			this.calendarView.refresh();
		} catch (Exception e) {}
		/* ====================================================== */
	}
	
	/**
	 * set the list in changed state
	 */
	public void triggerUpdate() {
		/* ================================================== */
		this.events.trigger();
		/* ================================================== */
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return this.type;
	}
	
}
