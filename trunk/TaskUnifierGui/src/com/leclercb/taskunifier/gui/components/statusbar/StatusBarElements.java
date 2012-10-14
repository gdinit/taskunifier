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
package com.leclercb.taskunifier.gui.components.statusbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.progress.ProgressMessageTransformer;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.components.notes.NoteTableView;
import com.leclercb.taskunifier.gui.components.synchronize.progress.SynchronizerProgressMessageTransformer;
import com.leclercb.taskunifier.gui.components.tasks.TaskTableView;
import com.leclercb.taskunifier.gui.components.views.NoteView;
import com.leclercb.taskunifier.gui.components.views.TaskView;
import com.leclercb.taskunifier.gui.components.views.ViewItem;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.main.frames.FrameView;
import com.leclercb.taskunifier.gui.threads.Threads;
import com.leclercb.taskunifier.gui.threads.communicator.progress.CommunicatorProgressMessageTransformer;
import com.leclercb.taskunifier.gui.threads.scheduledsync.ScheduledSyncThread;
import com.leclercb.taskunifier.gui.translations.Translations;

final class StatusBarElements implements ListChangeListener, PropertyChangeListener {
	
	private JLabel synchronizerStatusLabel;
	private JLabel lastSynchronizationDateLabel;
	private JLabel scheduledSyncStatusLabel;
	private JLabel rowCountLabel;
	private JLabel currentDateTime;
	
	public StatusBarElements(int frameId) {
		this.initializeSynchronizerStatus();
		this.initializeLastSynchronizationDate();
		this.initializeScheduledSyncStatus();
		this.initializeRowCount(frameId);
		this.initializeCurrentDateTime();
	}
	
	public JLabel getSynchronizerStatusLabel() {
		return this.synchronizerStatusLabel;
	}
	
	public JLabel getLastSynchronizationDateLabel() {
		return this.lastSynchronizationDateLabel;
	}
	
	public JLabel getScheduledSyncStatusLabel() {
		return this.scheduledSyncStatusLabel;
	}
	
	public JLabel getRowCountLabel() {
		return this.rowCountLabel;
	}
	
	public JLabel getCurrentDateTime() {
		return this.currentDateTime;
	}
	
	private void initializeSynchronizerStatus() {
		this.synchronizerStatusLabel = new JLabel();
		this.synchronizerStatusLabel.setText(Translations.getString("synchronizer.status")
				+ ": ");
		
		Constants.PROGRESS_MONITOR.addListChangeListener(new WeakListChangeListener(
				Constants.PROGRESS_MONITOR,
				this));
	}
	
	private void initializeLastSynchronizationDate() {
		this.lastSynchronizationDateLabel = new JLabel();
		
		this.updateLastSynchronizationDate();
		
		Main.getUserSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
	}
	
	private void updateLastSynchronizationDate() {
		String date = Translations.getString("statusbar.never");
		
		Calendar lastSyncDate = Main.getUserSettings().getCalendarProperty(
				"synchronizer.last_synchronization_date");
		
		if (lastSyncDate != null)
			date = StringValueCalendar.INSTANCE_DATE_TIME.getString(lastSyncDate);
		
		this.lastSynchronizationDateLabel.setText(Translations.getString("statusbar.last_synchronization_date")
				+ ": "
				+ date);
	}
	
	private void initializeScheduledSyncStatus() {
		this.scheduledSyncStatusLabel = new JLabel();
		
		this.updateScheduledSyncStatus();
		
		Main.getUserSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
		
		Threads.getScheduledSyncThread().addPropertyChangeListener(
				new WeakPropertyChangeListener(
						Threads.getScheduledSyncThread(),
						this));
	}
	
	private void updateScheduledSyncStatus() {
		String text = null;
		
		if (Main.getUserSettings().getBooleanProperty(
				"synchronizer.scheduler_enabled")) {
			long sleep = Threads.getScheduledSyncThread().getRemainingSleepTime();
			sleep = sleep / 1000;
			
			String time = "";
			time += (sleep / 3600) + "h ";
			time += ((sleep % 3600) / 60) + "m ";
			
			if (sleep < 60)
				time += (sleep % 60) + "s";
			
			text = Translations.getString("statusbar.next_scheduled_sync", time);
		} else {
			text = Translations.getString(
					"statusbar.next_scheduled_sync",
					Translations.getString("statusbar.never"));
		}
		
		this.scheduledSyncStatusLabel.setText(text);
	}
	
	private void initializeRowCount(int frameId) {
		this.rowCountLabel = new JLabel();
		
		this.updateRowCount();
		
		FrameUtils.getFrameView(frameId).addPropertyChangeListener(
				FrameView.PROP_SELECTED_VIEW,
				new WeakPropertyChangeListener(
						FrameUtils.getFrameView(frameId),
						this));
	}
	
	private void updateRowCount() {
		ViewType viewType = ViewUtils.getCurrentViewType();
		
		if (viewType == null) {
			this.rowCountLabel.setText("");
			return;
		}
		
		int rowCount = 0;
		
		switch (viewType) {
			case NOTES:
				rowCount = ViewUtils.getCurrentNoteView().getNoteTableView().getNoteCount();
				this.rowCountLabel.setText(rowCount
						+ " "
						+ Translations.getString("general.notes"));
				break;
			case TASKS:
				rowCount = ViewUtils.getCurrentTaskView().getTaskTableView().getTaskCount();
				this.rowCountLabel.setText(rowCount
						+ " "
						+ Translations.getString("general.tasks"));
				break;
			default:
				this.rowCountLabel.setText("");
				break;
		}
	}
	
	private void initializeCurrentDateTime() {
		this.currentDateTime = new JLabel();
		
		this.updateCurrentDateTime();
		
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				StatusBarElements.this.updateCurrentDateTime();
			}
			
		};
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 10000, 10000);
	}
	
	private final void updateCurrentDateTime() {
		this.currentDateTime.setText(StringValueCalendar.INSTANCE_DATE_TIME.getString(Calendar.getInstance()));
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		ProgressMessageTransformer t = null;
		
		t = SynchronizerProgressMessageTransformer.getInstance();
		
		if (t.acceptsEvent(event))
			this.synchronizerStatusLabel.setText(Translations.getString("synchronizer.status")
					+ ": "
					+ t.getEventValue(event, null));
		
		t = CommunicatorProgressMessageTransformer.getInstance();
		
		if (t.acceptsEvent(event))
			this.synchronizerStatusLabel.setText((String) t.getEventValue(
					event,
					null));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				"synchronizer.last_synchronization_date")) {
			this.updateLastSynchronizationDate();
		}
		
		if (event.getPropertyName().equals("synchronizer.scheduler_enabled")) {
			this.updateScheduledSyncStatus();
		}
		
		if (event.getPropertyName().equals(
				ScheduledSyncThread.PROP_REMAINING_SLEEP_TIME)) {
			this.updateScheduledSyncStatus();
		}
		
		if (event.getPropertyName().equals(NoteTableView.PROP_NOTE_COUNT)) {
			this.updateRowCount();
		}
		
		if (event.getPropertyName().equals(TaskTableView.PROP_TASK_COUNT)) {
			this.updateRowCount();
		}
		
		if (event.getPropertyName().equals(FrameView.PROP_SELECTED_VIEW)) {
			this.updateRowCount();
			
			if (event != null
					&& event.getOldValue() != null
					&& event.getOldValue() instanceof ViewItem) {
				ViewItem oldView = (ViewItem) event.getOldValue();
				
				if (oldView.getViewType() == ViewType.NOTES) {
					((NoteView) oldView.getView()).getNoteTableView().removePropertyChangeListener(
							this);
				}
				
				if (oldView.getViewType() == ViewType.TASKS) {
					((TaskView) oldView.getView()).getTaskTableView().removePropertyChangeListener(
							this);
				}
			}
			
			if (ViewList.getInstance().getCurrentView() != null
					&& ViewList.getInstance().getCurrentView().isLoaded()) {
				if (ViewUtils.getCurrentViewType() == ViewType.NOTES) {
					ViewUtils.getCurrentNoteView().getNoteTableView().addPropertyChangeListener(
							new WeakPropertyChangeListener(
									ViewUtils.getCurrentNoteView().getNoteTableView(),
									this));
				}
				
				if (ViewUtils.getCurrentViewType() == ViewType.TASKS) {
					ViewUtils.getCurrentTaskView().getTaskTableView().addPropertyChangeListener(
							new WeakPropertyChangeListener(
									ViewUtils.getCurrentTaskView().getTaskTableView(),
									this));
				}
			}
		}
	}
	
}
