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
package com.leclercb.taskunifier.gui.components.notes.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable.PrintMode;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionChangeEvent;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionChangeSupport;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionChangeSupported;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionListener;
import com.leclercb.taskunifier.gui.commons.events.NoteSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.notes.NoteTableView;
import com.leclercb.taskunifier.gui.components.print.PrintUtils;
import com.leclercb.taskunifier.gui.components.print.TablePrintable;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.NoteGrouperUtils;

public class NoteGroupTable extends JXPanel implements NoteTableView, ModelSelectionChangeSupported, ModelSelectionListener, PropertyChangeSupported, PropertyChangeListener {
	
	private int level;
	
	private TUTableProperties<Note> tableProperties;
	private List<NoteTableView> tables;
	private NoteSearcher searcher;
	private List<NoteSearcher> searchers;
	private ModelSelectionChangeSupport modelSelectionChangeSupport;
	private boolean isSelectionAdjusting;
	
	public NoteGroupTable(TUTableProperties<Note> tableProperties) {
		this(tableProperties, 0);
	}
	
	private NoteGroupTable(TUTableProperties<Note> tableProperties, int level) {
		this.level = level;
		
		this.tableProperties = tableProperties;
		this.tables = new ArrayList<NoteTableView>();
		this.searcher = null;
		this.searchers = null;
		this.modelSelectionChangeSupport = new ModelSelectionChangeSupport(this);
		this.isSelectionAdjusting = false;
		
		this.initialize();
	}
	
	private void initialize() {
		if (this.level == 0) {
			this.setOpaque(true);
			
			this.setBackgroundPainter(new Painter<JPanel>() {
				
				private SourceListStandardColorScheme scheme = new SourceListStandardColorScheme();
				
				@Override
				public void paint(
						Graphics2D g,
						JPanel obj,
						int width,
						int height) {
					Graphics2D g2d = (Graphics2D) g.create();
					
					Paint p = new GradientPaint(
							0,
							0,
							this.scheme.getActiveBackgroundColor(),
							width,
							0,
							Color.WHITE);
					
					g2d.setPaint(p);
					g2d.fillRect(0, 0, width, height);
					g2d.dispose();
				}
				
			});
		} else {
			this.setOpaque(false);
		}
		
		this.updateTables();
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}
	
	@Override
	public Note[] getSelectedNotes() {
		List<Note> notes = new ArrayList<Note>();
		
		for (NoteTableView table : this.tables) {
			notes.addAll(Arrays.asList(table.getSelectedNotes()));
		}
		
		return notes.toArray(new Note[0]);
	}
	
	@Override
	public void setSelectedNotes(Note[] notes) {
		for (NoteTableView table : this.tables) {
			table.setSelectedNotes(notes);
		}
	}
	
	@Override
	public void addModelSelectionChangeListener(ModelSelectionListener listener) {
		this.modelSelectionChangeSupport.addModelSelectionChangeListener(listener);
	}
	
	@Override
	public void removeModelSelectionChangeListener(
			ModelSelectionListener listener) {
		this.modelSelectionChangeSupport.removeModelSelectionChangeListener(listener);
	}
	
	@Override
	public void noteSearcherSelectionChange(
			NoteSearcherSelectionChangeEvent event) {
		this.searcher = event.getSelectedNoteSearcher();
		this.searchers = NoteGrouperUtils.getFilters(this.searcher);
		this.updateTables();
	}
	
	@Override
	public Note[] getNotes() {
		List<Note> notes = new ArrayList<Note>();
		
		for (NoteTableView table : this.tables) {
			notes.addAll(Arrays.asList(table.getNotes()));
		}
		
		return notes.toArray(new Note[0]);
	}
	
	@Override
	public int getNoteCount() {
		int count = 0;
		
		for (NoteTableView table : this.tables) {
			count += table.getNoteCount();
		}
		
		return count;
	}
	
	@Override
	public void setSelectedNoteAndStartEdit(Note note) {
		for (NoteTableView table : this.tables) {
			table.setSelectedNoteAndStartEdit(note);
		}
	}
	
	@Override
	public void refreshNotes() {
		for (NoteTableView table : this.tables) {
			table.refreshNotes();
		}
	}
	
	@Override
	public void setSearchText(String searchText) {
		for (NoteTableView table : this.tables) {
			table.setSearchText(searchText);
		}
	}
	
	@Override
	public void printNotes(boolean selection) throws Exception {
		Note[] notes = null;
		
		if (selection)
			notes = this.getSelectedNotes();
		else
			notes = this.getNotes();
		
		TablePrintable tablePrintable = new TablePrintable(
				new NotePrintTable(new TUTableProperties<Note>(
						NoteColumnList.getInstance(),
						this.tableProperties.getPropertyName() + ".print",
						false), notes),
				PrintMode.NORMAL,
				0.7,
				new MessageFormat(Constants.TITLE
						+ " - "
						+ this.searcher.getTitle()),
				new MessageFormat(this.getNoteCount() + " notes | Page - {0}"));
		
		PrintUtils.printTable("view.notes.print", tablePrintable);
	}
	
	@Override
	public void pasteNote() {
		this.tables.get(0).pasteNote();
	}
	
	@Override
	public void commitChanges() {
		for (NoteTableView table : this.tables) {
			table.commitChanges();
		}
	}
	
	public void updateTables() {
		for (NoteTableView table : this.tables) {
			table.removeModelSelectionChangeListener(this);
			table.removePropertyChangeListener(this);
		}
		
		this.tables.clear();
		
		this.removeAll();
		this.setLayout(new BorderLayout());
		this.validate();
		this.repaint();
		
		if (this.searchers == null
				|| this.searchers.size() == 0
				|| !this.doesContainDisplayedNotes(this.searcher)) {
			if (this.level == 0) {
				NoteTable table = new NoteTable(this.tableProperties);
				
				if (this.searcher != null)
					table.noteSearcherSelectionChange(new NoteSearcherSelectionChangeEvent(
							this,
							this.searcher));
				
				this.add(
						ComponentFactory.createJScrollPane(table, false),
						BorderLayout.CENTER);
				
				table.addModelSelectionChangeListener(this);
				table.addPropertyChangeListener(this);
				this.tables.add(table);
			}
			
			this.validate();
			this.repaint();
			
			this.firePropertyChange(PROP_NOTE_COUNT, null, this.getNoteCount());
			
			return;
		}
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new MigLayout("insets 0 0 0 0"));
		mainPanel.setOpaque(false);
		
		for (NoteSearcher searcher : this.searchers) {
			if (this.doesContainDisplayedNotes(searcher)) {
				JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				titlePanel.setOpaque(false);
				
				String spacer = "";
				for (int i = 0; i < this.level; i++) {
					spacer += "     ";
				}
				
				final JXCollapsiblePane collapsiblePane = new JXCollapsiblePane(
						Direction.UP);
				collapsiblePane.setLayout(new BorderLayout());
				collapsiblePane.setOpaque(false);
				collapsiblePane.setAnimated(false);
				
				if (collapsiblePane.getContentPane() instanceof JPanel)
					((JPanel) collapsiblePane.getContentPane()).setOpaque(false);
				
				final JButton button = new JButton();
				button.setBorderPainted(false);
				button.setContentAreaFilled(false);
				button.setIcon(ImageUtils.getResourceImage(
						"collapse.png",
						12,
						12));
				button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent event) {
						collapsiblePane.setCollapsed(!collapsiblePane.isCollapsed());
						
						if (collapsiblePane.isCollapsed())
							button.setIcon(ImageUtils.getResourceImage(
									"expand.png",
									12,
									12));
						else
							button.setIcon(ImageUtils.getResourceImage(
									"collapse.png",
									12,
									12));
					}
					
				});
				
				titlePanel.add(new JLabel(spacer));
				titlePanel.add(button);
				titlePanel.add(new JLabel(searcher.getTitle()));
				
				mainPanel.add(titlePanel, "grow, wrap");
				mainPanel.add(collapsiblePane, "wrap");
				
				if (searcher.getGrouper().getElementCount() == 0) {
					JPanel tablePanel = new JPanel(new BorderLayout());
					tablePanel.setOpaque(false);
					NoteTable table = new NoteTable(this.tableProperties);
					table.noteSearcherSelectionChange(new NoteSearcherSelectionChangeEvent(
							this,
							searcher));
					tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
					tablePanel.add(table, BorderLayout.CENTER);
					collapsiblePane.add(tablePanel, BorderLayout.CENTER);
					
					table.addModelSelectionChangeListener(this);
					table.addPropertyChangeListener(this);
					this.tables.add(table);
				} else {
					NoteGroupTable table = new NoteGroupTable(
							this.tableProperties,
							this.level + 1);
					table.noteSearcherSelectionChange(new NoteSearcherSelectionChangeEvent(
							this,
							searcher));
					collapsiblePane.add(table, BorderLayout.CENTER);
					
					table.addModelSelectionChangeListener(this);
					table.addPropertyChangeListener(this);
					this.tables.add(table);
				}
			}
		}
		
		if (this.level == 0) {
			JScrollPane scrollPane = ComponentFactory.createJScrollPane(
					mainPanel,
					false);
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false);
			
			this.add(scrollPane, BorderLayout.CENTER);
		} else {
			this.add(mainPanel, BorderLayout.CENTER);
		}
		
		this.validate();
		this.repaint();
		
		this.firePropertyChange(PROP_NOTE_COUNT, null, this.getNoteCount());
	}
	
	private boolean doesContainDisplayedNotes(NoteSearcher searcher) {
		List<Note> notes = NoteFactory.getInstance().getList();
		
		for (Note note : notes) {
			if (searcher.getFilter().include(note, null))
				return true;
		}
		
		return false;
	}
	
	@Override
	public void modelSelectionChange(ModelSelectionChangeEvent event) {
		if (this.isSelectionAdjusting)
			return;
		
		this.isSelectionAdjusting = true;
		
		for (NoteTableView table : this.tables) {
			if (table != event.getSource())
				table.setSelectedNotes(new Note[0]);
		}
		
		this.isSelectionAdjusting = false;
		
		this.modelSelectionChangeSupport.fireModelSelectionChange(event.getSelectedModels());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(NoteTableView.PROP_NOTE_COUNT)) {
			this.firePropertyChange(PROP_NOTE_COUNT, null, this.getNoteCount());
		}
	}
	
}
