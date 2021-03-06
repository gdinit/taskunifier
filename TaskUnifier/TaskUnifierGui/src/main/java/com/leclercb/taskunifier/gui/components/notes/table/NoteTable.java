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

import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.actions.ActionDelete;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorterElement;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionChangeSupport;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionListener;
import com.leclercb.taskunifier.gui.commons.events.NoteSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.commons.highlighters.AlternateHighlighter;
import com.leclercb.taskunifier.gui.commons.highlighters.SearchHighlighter;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.notes.NoteTableView;
import com.leclercb.taskunifier.gui.components.notes.table.draganddrop.NoteTransferHandler;
import com.leclercb.taskunifier.gui.components.notes.table.highlighters.NoteSearchHighlightPredicate;
import com.leclercb.taskunifier.gui.components.notes.table.highlighters.NoteTitleHighlighter;
import com.leclercb.taskunifier.gui.components.notes.table.highlighters.NoteTooltipHighlighter;
import com.leclercb.taskunifier.gui.components.notes.table.menu.NoteTableMenu;
import com.leclercb.taskunifier.gui.components.notes.table.sorter.NoteRowComparator;
import com.leclercb.taskunifier.gui.components.notes.table.sorter.NoteRowFilter;
import com.leclercb.taskunifier.gui.components.print.PrintUtils;
import com.leclercb.taskunifier.gui.components.print.TablePrintable;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.UndoSupport;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteTable extends JXTable implements NoteTableView, SavePropertiesListener {

    private UndoSupport undoSupport;

    private ModelSelectionChangeSupport noteSelectionChangeSupport;

    private NoteRowComparator noteRowComparator;
    private TUTableProperties<Note> tableProperties;
    private NoteTableMenu noteTableMenu;

    private SearchHighlighter searchHighlighter;

    public NoteTable(TUTableProperties<Note> noteColumnsProperties) {
        CheckUtils.isNotNull(noteColumnsProperties);

        this.noteRowComparator = new NoteRowComparator();
        this.tableProperties = noteColumnsProperties;
        this.undoSupport = Constants.UNDO_SUPPORT;
        this.noteSelectionChangeSupport = new ModelSelectionChangeSupport(this);

        this.initialize();
    }

    public TUTableProperties<Note> getTableProperties() {
        return this.tableProperties;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public int getNoteCount() {
        return this.getRowCount();
    }

    public Note getNote(int row) {
        try {
            int index = this.getRowSorter().convertRowIndexToModel(row);
            return ((NoteTableModel) this.getModel()).getNote(index);
        } catch (IndexOutOfBoundsException exc) {
            return null;
        }
    }

    @Override
    public Note[] getNotes() {
        List<Note> notes = new ArrayList<Note>();
        for (int i = 0; i < this.getModel().getRowCount(); i++) {
            Note note = this.getNote(i);

            if (note != null)
                notes.add(note);
        }

        return notes.toArray(new Note[0]);
    }

    @Override
    public Note[] getSelectedNotes() {
        int[] indexes = this.getSelectedRows();

        List<Note> notes = new ArrayList<Note>();
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != -1) {
                Note note = this.getNote(indexes[i]);

                if (note != null)
                    notes.add(note);
            }
        }

        return notes.toArray(new Note[0]);
    }

    @Override
    public void setSelectedNotes(Note[] notes) {
        NoteTableModel model = (NoteTableModel) this.getModel();

        this.getSelectionModel().setValueIsAdjusting(true);
        this.getSelectionModel().clearSelection();

        int firstRowIndex = -1;
        for (Note note : notes) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (note.equals(model.getNote(i))) {
                    int index = this.getRowSorter().convertRowIndexToView(i);

                    if (index != -1) {
                        this.getSelectionModel().addSelectionInterval(
                                index,
                                index);

                        if (firstRowIndex == -1)
                            firstRowIndex = index;
                    }
                }
            }
        }

        this.getSelectionModel().setValueIsAdjusting(false);

        if (firstRowIndex != -1)
            this.scrollRowToVisible(firstRowIndex);
    }

    @Override
    public void setSelectedNoteAndStartEdit(Note note) {
        this.setSelectedNotes(new Note[]{note});

        NoteTableColumnModel columnModel = (NoteTableColumnModel) this.getColumnModel();
        NoteTableModel model = (NoteTableModel) this.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (note.equals(model.getNote(i))) {
                int row = this.getRowSorter().convertRowIndexToView(i);
                int col = columnModel.getColumnIndex(NoteColumnList.getInstance().get(
                        NoteColumnList.TITLE));

                if (row != -1) {
                    if (this.editCellAt(row, col)) {
                        Component editor = this.getEditorComponent();
                        editor.requestFocusInWindow();

                        if (editor instanceof JTextComponent)
                            ((JTextComponent) editor).selectAll();
                    }
                }

                break;
            }
        }
    }

    @Override
    public void refreshNotes() {
        this.getSortController().setRowFilter(
                new NoteRowFilter(
                        this.noteRowComparator.getNoteSearcher().getFilter()));

        this.getRowSorter().allRowsChanged();

        try {
            if (this.getSelectedRow() != -1)
                this.scrollRowToVisible(this.getSelectedRow());
        } catch (Throwable t) {

        }

        this.firePropertyChange(PROP_NOTE_COUNT, null, this.getNoteCount());
    }

    public NoteSearcher getNoteSearcher() {
        return this.noteRowComparator.getNoteSearcher();
    }

    private void setNoteSearcher(NoteSearcher searcher) {
        CheckUtils.isNotNull(searcher);

        this.noteRowComparator.setNoteSearcher(searcher);

        this.setSortOrder(
                NoteColumnList.getInstance().get(NoteColumnList.MODEL),
                SortOrder.ASCENDING);

        this.refreshNotes();
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchHighlighter.setSearchText(searchText);
    }

    @Override
    public void printNotes(boolean selection) throws HeadlessException,
            PrinterException {
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
                        + this.getNoteSearcher().getTitle()),
                new MessageFormat(this.getNoteCount() + " notes | Page - {0}"));

        PrintUtils.printTable("view.notes.print", tablePrintable);
    }

    @Override
    public void pasteNote() {
        TransferHandler.getPasteAction().actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        this.commitChanges();
    }

    @Override
    public void commitChanges() {
        if (this.getCellEditor() != null)
            this.getCellEditor().stopCellEditing();
    }

    @Override
    public void addModelSelectionChangeListener(ModelSelectionListener listener) {
        this.noteSelectionChangeSupport.addModelSelectionChangeListener(listener);
    }

    @Override
    public void removeModelSelectionChangeListener(
            ModelSelectionListener listener) {
        this.noteSelectionChangeSupport.removeModelSelectionChangeListener(listener);
    }

    @Override
    public void noteSearcherSelectionChange(
            NoteSearcherSelectionChangeEvent event) {
        if (event.getSelectedNoteSearcher() != null)
            this.setNoteSearcher(event.getSelectedNoteSearcher());
    }

    private void initialize() {
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        NoteTableColumnModel columnModel = new NoteTableColumnModel(
                this.tableProperties,
                this.noteRowComparator);
        NoteTableModel tableModel = new NoteTableModel(this.undoSupport);

        this.setModel(tableModel);
        this.setColumnModel(columnModel);
        this.setRowHeight(20);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setShowGrid(true, false);

        this.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        this.setSortable(true);
        this.setSortsOnUpdates(true);
        this.setSortOrderCycle(SortOrder.ASCENDING);
        this.setColumnControlVisible(true);

        this.initializeSettings();
        this.initializeHeaderListener();
        this.initializeFind();
        this.initializeDeleteNote();
        this.initializeEditNote();
        this.initializeNoteTableMenu();
        this.initializeDragAndDrop();
        this.initializeUndoRedo();
        this.initializeCopyAndPaste();
        this.initializeTabKey();
        this.initializeHighlighters();

        this.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting())
                            NoteTable.this.noteSelectionChangeSupport.fireModelSelectionChange(NoteTable.this.getSelectedNotes());
                    }

                });

        this.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent evt) {
                NoteTable.this.firePropertyChange(
                        PROP_NOTE_COUNT,
                        null,
                        NoteTable.this.getNoteCount());
            }

        });
    }

    private void initializeSettings() {
        this.setHorizontalScrollEnabled(Main.getSettings().getBooleanProperty(
                this.tableProperties.getPropertyName()
                        + ".horizontal_scroll_enabled",
                false));
    }

    private void initializeHeaderListener() {
        this.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                JTable table = ((JTableHeader) evt.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();

                int colIndex = colModel.getColumnIndexAtX(evt.getX());

                if (colIndex == -1) {
                    return;
                }

                Rectangle headerRect = table.getTableHeader().getHeaderRect(
                        colIndex);
                if (headerRect.contains(evt.getX(), evt.getY())) {
                    PropertyAccessor<Note> column = (PropertyAccessor<Note>) colModel.getColumn(
                            colIndex).getIdentifier();

                    if (EqualsUtils.equals(
                            column,
                            NoteColumnList.getInstance().get(
                                    NoteColumnList.MODEL)))
                        return;

                    NoteSearcher searcher = NoteTable.this.getNoteSearcher().clone();

                    if (searcher.getSorter().getElementCount() != 0
                            && searcher.getSorter().getElement(0).getProperty() == column) {
                        NoteSorterElement element = searcher.getSorter().getElement(
                                0);

                        SortOrder order = element.getSortOrder();

                        if (order == SortOrder.ASCENDING)
                            order = SortOrder.DESCENDING;
                        else
                            order = SortOrder.ASCENDING;

                        element.setSortOrder(order);
                    } else {
                        NoteSorterElement element = new NoteSorterElement(
                                column,
                                SortOrder.ASCENDING);

                        searcher.getSorter().insertElement(element, 0);
                    }

                    NoteTable.this.setNoteSearcher(searcher);
                }
            }

        });
    }

    private void initializeFind() {
        this.getActionMap().put("find", null);
    }

    private void initializeDeleteNote() {
        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                    ActionDelete.delete();
            }

        });
    }

    private void initializeEditNote() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1
                        && event.getClickCount() == 2) {
                    int rowIndex = NoteTable.this.rowAtPoint(event.getPoint());

                    if (rowIndex == -1)
                        return;

                    rowIndex = NoteTable.this.getRowSorter().convertRowIndexToModel(
                            rowIndex);

                    int colIndex = NoteTable.this.columnAtPoint(event.getPoint());

                    PropertyAccessor<Note> column = (PropertyAccessor<Note>) NoteTable.this.getColumn(
                            colIndex).getIdentifier();

                    if (EqualsUtils.equals(
                            column,
                            NoteColumnList.getInstance().get(
                                    NoteColumnList.NOTE))) {
                        Note note = ((NoteTableModel) NoteTable.this.getModel()).getNote(rowIndex);

                        if (note == null)
                            return;

                        NoteTable.this.commitChanges();

                        NoteTable.this.setSelectedNotes(new Note[]{note});

                        if (ViewUtils.getCurrentNoteView() != null) {
                            ViewUtils.getCurrentNoteView().getModelNoteView().edit();
                        }
                    }
                }
            }

        });
    }

    private void initializeNoteTableMenu() {
        this.noteTableMenu = new NoteTableMenu();

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                // Or BUTTON3 due to a bug with OSX
                if (event.isPopupTrigger()
                        || event.getButton() == MouseEvent.BUTTON3) {
                    int rowIndex = NoteTable.this.rowAtPoint(event.getPoint());

                    if (rowIndex != -1) {
                        rowIndex = NoteTable.this.getRowSorter().convertRowIndexToModel(
                                rowIndex);

                        Note note = ((NoteTableModel) NoteTable.this.getModel()).getNote(rowIndex);

                        if (note != null) {
                            NoteTable.this.commitChanges();

                            boolean found = false;
                            Note[] selectedNotes = NoteTable.this.getSelectedNotes();
                            for (Note selectedNote : selectedNotes) {
                                if (selectedNote.equals(note)) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found)
                                NoteTable.this.setSelectedNotes(new Note[]{note});
                        } else {
                            NoteTable.this.setSelectedNotes(new Note[]{});
                        }
                    } else {
                        NoteTable.this.setSelectedNotes(new Note[]{});
                    }

                    NoteTable.this.noteTableMenu.show(
                            event.getComponent(),
                            event.getX(),
                            event.getY());
                }
            }

        });
    }

    private void initializeDragAndDrop() {
        this.setDragEnabled(true);
        this.setTransferHandler(new NoteTransferHandler());
    }

    private void initializeUndoRedo() {
        this.undoSupport.initializeMaps(this);
    }

    private void initializeCopyAndPaste() {
        ActionMap amap = this.getActionMap();
        amap.put(
                TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        amap.put(
                TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        amap.put(
                TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        InputMap imap = this.getInputMap();
        imap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X,
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                TransferHandler.getCutAction().getValue(Action.NAME));
        imap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C,
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                TransferHandler.getCopyAction().getValue(Action.NAME));
        imap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V,
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                TransferHandler.getPasteAction().getValue(Action.NAME));
    }

    private void initializeTabKey() {
        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_TAB) {
                    try {
                        NoteTable.this.editCellAt(
                                NoteTable.this.getSelectedRow(),
                                NoteTable.this.getSelectedColumn());

                        if (NoteTable.this.getEditorComponent() != null)
                            NoteTable.this.getEditorComponent().requestFocus();
                    } catch (Exception e) {

                    }
                }
            }

        });
    }

    private void initializeHighlighters() {
        this.searchHighlighter = new SearchHighlighter(
                new NoteSearchHighlightPredicate());

        this.setHighlighters(
                new AlternateHighlighter(),
                new NoteTitleHighlighter(),
                this.searchHighlighter,
                new NoteTooltipHighlighter());
    }

    @Override
    public void saveProperties() {
        Main.getSettings().setBooleanProperty(
                this.tableProperties.getPropertyName()
                        + ".horizontal_scroll_enabled",
                this.isHorizontalScrollEnabled());
    }

}
