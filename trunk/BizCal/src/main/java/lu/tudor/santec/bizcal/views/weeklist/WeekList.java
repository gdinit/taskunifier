package lu.tudor.santec.bizcal.views.weeklist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import lu.tudor.santec.bizcal.views.WeekListViewPanel;
import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;

public class WeekList extends JPanel {
	
	private CalendarViewConfig config;
	private WeekListViewPanel parent;
	
	private JLabel label;
	private WeekJList list;
	
	public WeekList(CalendarViewConfig config, WeekListViewPanel parent) {
		this.config = config;
		this.parent = parent;
		
		this.label = new JLabel();
		this.label.setHorizontalAlignment(SwingConstants.CENTER);
		this.label.setBorder(BorderFactory.createMatteBorder(
				0,
				0,
				1,
				0,
				Color.LIGHT_GRAY));
		
		this.list = new WeekJList(this.parent) {
			
			@Override
			public String getToolTipText(MouseEvent evt) {
				int index = this.locationToIndex(evt.getPoint());
				
				if (index == -1)
					return null;
				
				Event item = (Event) this.getModel().getElementAt(index);
				
				if (item == null)
					return null;
				
				return item.getToolTip();
			}
			
		};
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setCellRenderer(new WeekListRenderer(config));
		this.list.setModel(new DefaultListModel());
		
		this.setLayout(new BorderLayout());
		this.add(this.label, BorderLayout.NORTH);
		this.add(this.list, BorderLayout.CENTER);
		
		this.setBorder(BorderFactory.createMatteBorder(
				0,
				0,
				0,
				1,
				Color.LIGHT_GRAY));
	}
	
	public void setDate(Calendar date) {
		this.list.setDate(date);
	}
	
	public Calendar getDate() {
		return this.list.getDate();
	}
	
	public WeekJList getList() {
		return this.list;
	}
	
	public void setEvents(List events, Date date) {
		this.label.setText(this.config.getDayFormat().format(date));
		
		DefaultListModel model = (DefaultListModel) this.list.getModel();
		model.removeAllElements();
		
		for (Object object : events) {
			model.addElement(object);
		}
	}
	
}
