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
package com.leclercb.taskunifier.gui.components.modelnote.editors;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.actions.ActionCopy;
import com.leclercb.taskunifier.gui.actions.ActionCut;
import com.leclercb.taskunifier.gui.actions.ActionPaste;
import com.leclercb.taskunifier.gui.commons.listeners.PopupTriggerMouseListener;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.components.modelnote.HTMLEditorInterface;
import com.leclercb.taskunifier.gui.components.modelnote.actions.ActionCopyPlainText;
import com.leclercb.taskunifier.gui.components.modelnote.converters.HTML2Text;
import com.leclercb.taskunifier.gui.components.modelnote.converters.Text2HTML;
import com.leclercb.taskunifier.gui.components.modelnote.editors.draganddrop.ModelNoteTransferHandler;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TULinkDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;
import com.leclercb.taskunifier.gui.utils.ProtocolUtils;
import com.leclercb.taskunifier.gui.utils.UndoSupport;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jdesktop.swingx.JXEditorPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Calendar;

public class WysiwygHTMLEditorPane extends JPanel implements HTMLEditorInterface, PropertyChangeSupported, PropertyChangeListener {
	
	private UndoSupport undoSupport;
	
	private String propertyName;
	
	private JToolBar toolBar;
	private JXEditorPane htmlNote;
	private boolean flagSetText;
	
	private JComboBox fontSizeComboBox;
	private JComboBox fontFamilyComboBox;
	
	public WysiwygHTMLEditorPane(
			String text,
			boolean canEdit,
			String propertyName) {
		this.propertyName = propertyName;
		
		this.initialize(text, canEdit);
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.toolBar.setEnabled(enabled);
		this.htmlNote.setEnabled(enabled);
		
		super.setEnabled(enabled);
	}
	
	@Override
	public String getText() {
		return HTML2Text.convertToBasicHtml(this.htmlNote.getText());
	}
	
	@Override
	public void setText(String text) {
		this.setText(text, true, false);
	}
	
	@Override
	public void setText(String text, boolean canEdit, boolean discardAllEdits) {
		if (WysiwygHTMLEditorPane.this.flagSetText)
			return;
		
		this.toolBar.setEnabled(canEdit);
		this.htmlNote.setEditable(canEdit);
		this.htmlNote.setEnabled(canEdit);
		
		this.flagSetText = true;
		this.htmlNote.setText(Text2HTML.convert(text));
		this.flagSetText = false;
		
		if (discardAllEdits) {
			this.undoSupport.discardAllEdits();
		}
	}
	
	@Override
	public boolean edit() {
		if (!this.htmlNote.isEditable())
			return false;
		
		this.htmlNote.requestFocus();
		return true;
	}
	
	private void initialize(final String text, final boolean canEdit) {
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		
		this.undoSupport = new UndoSupport();
		
		this.htmlNote = new JXEditorPane();
		this.addContextMenu(this.htmlNote);
		this.htmlNote.setEditable(true);
		this.htmlNote.setContentType("text/html");
		this.htmlNote.setEditorKit(new WysiwygHTMLEditorKit());
		this.htmlNote.setFont(UIManager.getFont("Label.font"));
		this.htmlNote.setTransferHandler(new ModelNoteTransferHandler());
		this.htmlNote.getDocument().addUndoableEditListener(this.undoSupport);
		this.undoSupport.initializeMaps(this.htmlNote);
		
		this.htmlNote.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!WysiwygHTMLEditorPane.this.flagSetText)
					WysiwygHTMLEditorPane.this.firePropertyChange(
							PROP_TEXT,
							null,
							WysiwygHTMLEditorPane.this.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!WysiwygHTMLEditorPane.this.flagSetText)
					WysiwygHTMLEditorPane.this.firePropertyChange(
							PROP_TEXT,
							null,
							WysiwygHTMLEditorPane.this.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!WysiwygHTMLEditorPane.this.flagSetText)
					WysiwygHTMLEditorPane.this.firePropertyChange(
							PROP_TEXT,
							null,
							WysiwygHTMLEditorPane.this.getText());
			}
			
		});
		
		this.htmlNote.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent evt) {
				if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					String url = ProtocolUtils.urlToString(evt.getURL());
					DesktopUtils.browse(url);
				}
			}
			
		});
		
		this.toolBar = new JToolBar(SwingConstants.HORIZONTAL);
		this.toolBar.setOpaque(false);
		this.toolBar.setFloatable(false);
		
		this.toolBar.add(this.undoSupport.getUndoAction());
		this.toolBar.add(this.undoSupport.getRedoAction());
		
		this.toolBar.addSeparator();
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_b.png",
				Translations.getString("modelnote.action.b"),
				this.getAction("font-bold")));
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_i.png",
				Translations.getString("modelnote.action.i"),
				this.getAction("font-italic")));
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_ul.png",
				Translations.getString("modelnote.action.ul"),
				this.getAction("InsertUnorderedList")));
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_li.png",
				Translations.getString("modelnote.action.li"),
				this.getAction("InsertUnorderedListItem")));
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_ol.png",
				Translations.getString("modelnote.action.ol"),
				this.getAction("InsertOrderedList")));
		
		this.toolBar.add(new WysiwygInsertHTMLAction(
				this.htmlNote,
				"html_li.png",
				Translations.getString("modelnote.action.li"),
				this.getAction("InsertOrderedListItem")));
		
		this.toolBar.add(new WysiwygInsertHTMLTextAction(
				this.htmlNote,
				"html_a.png",
				Translations.getString("modelnote.action.a"),
				"",
				HTML.Tag.A) {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (this.editor.isEditable()) {
					TULinkDialog dialog = new TULinkDialog(
							true,
							Translations.getString("general.link"));
					dialog.setFile("http://");
					dialog.setVisible(true);
					
					if (dialog.isCancelled()) {
						WysiwygHTMLEditorPane.this.htmlNote.requestFocus();
						return;
					}
					
					String url = dialog.getFile();
					String label = dialog.getLabel();
					
					try {
						File file = new File(url);
						if (file.exists())
							url = file.toURI().toURL().toExternalForm();
					} catch (Throwable t) {
						
					}
					
					if (label == null || label.length() == 0) {
						label = dialog.getFile();
					}
					
					this.setHtml("<a href=\""
							+ StringEscapeUtils.escapeHtml4(url)
							+ "\">"
							+ StringEscapeUtils.escapeHtml4(label)
							+ "</a>");
					super.actionPerformed(event);
				}
			}
			
		});
		
		this.toolBar.add(new WysiwygInsertTextAction(
				this.htmlNote,
				"calendar.png",
				Translations.getString("modelnote.action.date"),
				StringValueCalendar.INSTANCE_DATE_TIME.getString(Calendar.getInstance())) {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				this.setText(StringValueCalendar.INSTANCE_DATE_TIME.getString(Calendar.getInstance()));
				super.actionPerformed(event);
			}
			
		});
		
		if (this.propertyName != null) {
			this.toolBar.addSeparator();
			
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.setOpaque(false);
			
			panel.add(this.createFontSizeComboBox(this.htmlNote));
			panel.add(this.createFontFamilyComboBox(this.htmlNote));
			
			this.toolBar.add(panel);
		}
		
		this.add(this.toolBar, BorderLayout.NORTH);
		this.add(
				ComponentFactory.createJScrollPane(this.htmlNote, false),
				BorderLayout.CENTER);
		
		this.setText(text, canEdit, true);
	}
	
	private void addContextMenu(JComponent component) {
		JPopupMenu menu = new JPopupMenu();
		
		menu.add(new ActionCopy(16, 16));
        menu.add(new ActionCopyPlainText(this.htmlNote, 16, 16));
        menu.add(new ActionCut(16, 16));
		menu.add(new ActionPaste(16, 16));
		
		component.addMouseListener(new PopupTriggerMouseListener(
				menu,
				component));
	}
	
	private JComponent createFontSizeComboBox(final JTextComponent component) {
		int fontSize = Main.getSettings().getIntegerProperty(
				this.propertyName + ".html.font_size",
				this.htmlNote.getFont().getSize());
		
		this.fontSizeComboBox = new JComboBox(new Integer[] {
				8,
				9,
				10,
				11,
				12,
				13,
				14,
				15,
				16,
				17,
				18,
				19,
				20 });
		this.fontSizeComboBox.setSelectedItem(fontSize);
		
		this.fontSizeComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent evt) {
				Integer fontSize = (Integer) WysiwygHTMLEditorPane.this.fontSizeComboBox.getSelectedItem();
				Main.getSettings().setIntegerProperty(
						WysiwygHTMLEditorPane.this.propertyName
								+ ".html.font_size",
						fontSize);
			}
			
		});
		
		this.fontSizeComboBox.setPrototypeDisplayValue("0000");
		this.fontSizeComboBox.setToolTipText(Translations.getString("modelnote.action.font_size"));
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		return this.fontSizeComboBox;
	}
	
	private JComponent createFontFamilyComboBox(final JTextComponent component) {
		String fontFamily = Main.getSettings().getStringProperty(
				this.propertyName + ".html.font_family",
				this.htmlNote.getFont().getFamily());
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = ge.getAvailableFontFamilyNames();
		
		this.fontFamilyComboBox = new JComboBox(fonts);
		this.fontFamilyComboBox.setSelectedItem(fontFamily);
		
		this.fontFamilyComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent evt) {
				String fontFamily = (String) WysiwygHTMLEditorPane.this.fontFamilyComboBox.getSelectedItem();
				Main.getSettings().setStringProperty(
						WysiwygHTMLEditorPane.this.propertyName
								+ ".html.font_family",
						fontFamily);
			}
			
		});
		
		this.fontFamilyComboBox.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(
					JList list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {
				String fontFamily = (String) value;
				
				JLabel label = (JLabel) super.getListCellRendererComponent(
						list,
						value,
						index,
						isSelected,
						cellHasFocus);
				
				label.setFont(new Font(fontFamily, Font.PLAIN, 14));
				label.setText(fontFamily);
				
				return label;
			}
			
		});
		
		this.fontFamilyComboBox.setToolTipText(Translations.getString("modelnote.action.font_family"));
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		return this.fontFamilyComboBox;
	}
	
	public Action getAction(String name) {
		for (Action action : this.htmlNote.getActions()) {
			if (EqualsUtils.equals(name, action.getValue(Action.NAME)))
				return action;
		}
		
		return null;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int fontSize = Main.getSettings().getIntegerProperty(
				this.propertyName + ".html.font_size",
				this.htmlNote.getFont().getSize());
		
		String fontFamily = Main.getSettings().getStringProperty(
				this.propertyName + ".html.font_family",
				this.htmlNote.getFont().getFamily());
		
		if (this.fontSizeComboBox != null)
			this.fontSizeComboBox.setSelectedItem(fontSize);
		
		if (this.fontFamilyComboBox != null)
			this.fontFamilyComboBox.setSelectedItem(fontFamily);
		
		this.htmlNote.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
	}
	
}
