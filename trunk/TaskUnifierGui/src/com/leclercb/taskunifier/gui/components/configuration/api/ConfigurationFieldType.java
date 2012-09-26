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
package com.leclercb.taskunifier.gui.components.configuration.api;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXColorSelectionButton;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.swing.TUFileField;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public interface ConfigurationFieldType<ComponentType extends JComponent, ValueType> {
	
	public static class Panel implements ConfigurationFieldType<JPanel, Void> {
		
		private JPanel panel;
		
		public Panel() {
			this(null);
		}
		
		public Panel(JPanel panel) {
			this.panel = panel;
		}
		
		public void setPanel(JPanel panel) {
			this.panel = panel;
		}
		
		@Override
		public void initializeFieldComponent() {
			
		}
		
		@Override
		public JPanel getFieldComponent() {
			return this.panel;
		}
		
		@Override
		public Void getFieldValue() {
			return null;
		}
		
		@Override
		public Void getPropertyValue() {
			return null;
		}
		
		@Override
		public void saveAndApplyConfig() {
			
		}
		
	}
	
	public static class Separator extends JSeparator implements ConfigurationFieldType<JSeparator, Void> {
		
		public Separator() {
			
		}
		
		@Override
		public void initializeFieldComponent() {
			
		}
		
		@Override
		public JSeparator getFieldComponent() {
			return this;
		}
		
		@Override
		public Void getFieldValue() {
			return null;
		}
		
		@Override
		public Void getPropertyValue() {
			return null;
		}
		
		@Override
		public void saveAndApplyConfig() {
			
		}
		
	}
	
	public static class Label extends JTextArea implements ConfigurationFieldType<JTextArea, Void> {
		
		public Label(String label) {
			super(label);
			this.setOpaque(false);
			this.setLineWrap(true);
			this.setWrapStyleWord(true);
		}
		
		@Override
		public void initializeFieldComponent() {
			
		}
		
		@Override
		public JTextArea getFieldComponent() {
			return this;
		}
		
		@Override
		public Void getFieldValue() {
			return null;
		}
		
		@Override
		public Void getPropertyValue() {
			return null;
		}
		
		@Override
		public void saveAndApplyConfig() {
			
		}
		
	}
	
	public static class Button extends JButton implements ConfigurationFieldType<JButton, Void> {
		
		public Button(Action action) {
			super(action);
		}
		
		public Button(String label, ActionListener listener) {
			super(label);
			
			if (listener != null)
				this.addActionListener(listener);
		}
		
		public Button(String label, Icon icon, ActionListener listener) {
			super(label, icon);
			
			if (listener != null)
				this.addActionListener(listener);
		}
		
		@Override
		public void initializeFieldComponent() {
			
		}
		
		@Override
		public JButton getFieldComponent() {
			return this;
		}
		
		@Override
		public Void getFieldValue() {
			return null;
		}
		
		@Override
		public Void getPropertyValue() {
			return null;
		}
		
		@Override
		public void saveAndApplyConfig() {
			
		}
		
	}
	
	public static class RadioButton extends JPanel implements ConfigurationFieldType<JPanel, String>, PropertyChangeListener {
		
		private boolean first;
		private ButtonGroup group;
		private PropertyMap settings;
		private String propertyName;
		
		public RadioButton(
				PropertyMap settings,
				String propertyName,
				String[] labels,
				String[] values) {
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
			
			CheckUtils.isNotNull(labels);
			CheckUtils.isNotNull(values);
			
			if (labels.length != values.length)
				throw new IllegalArgumentException();
			
			this.setLayout(new GridLayout(0, 1));
			
			group = new ButtonGroup();
			
			for (int i = 0; i < labels.length; i++) {
				JRadioButton radioButton = new JRadioButton(labels[i]);
				radioButton.setActionCommand(values[i]);
				
				this.add(radioButton);
				group.add(radioButton);
			}
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setSelectedButton(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JPanel getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return group.getSelection().getActionCommand();
		}
		
		@Override
		public String getPropertyValue() {
			return this.settings.getStringProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setStringProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		private void setSelectedButton(String value) {
			List<AbstractButton> buttons = Collections.list(group.getElements());
			for (AbstractButton button : buttons) {
				if (EqualsUtils.equals(button.getActionCommand(), value)) {
					group.setSelected(button.getModel(), true);
					break;
				}
			}
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setSelectedButton(getPropertyValue());
		}
		
	}
	
	public static class CheckBox extends JCheckBox implements ConfigurationFieldType<JCheckBox, Boolean>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public CheckBox(PropertyMap settings, String propertyName, String label) {
			super(label);
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			Boolean selected = getPropertyValue();
			
			if (selected == null)
				selected = false;
			
			this.setSelected(selected);
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JCheckBox getFieldComponent() {
			return this;
		}
		
		@Override
		public Boolean getFieldValue() {
			return this.isSelected();
		}
		
		@Override
		public Boolean getPropertyValue() {
			return this.settings.getBooleanProperty(propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setBooleanProperty(propertyName, this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			Boolean selected = getPropertyValue();
			
			if (selected == null)
				selected = false;
			
			this.setSelected(selected);
		}
		
	}
	
	public static abstract class Spinner extends JSpinner implements ConfigurationFieldType<JSpinner, Object>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public Spinner(PropertyMap settings, String propertyName) {
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			try {
				this.setValue(Spinner.this.getPropertyValue());
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JSpinner getFieldComponent() {
			return this;
		}
		
		@Override
		public Object getFieldValue() {
			return this.getValue();
		}
		
		@Override
		public abstract Object getPropertyValue();
		
		@Override
		public abstract void saveAndApplyConfig();
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			try {
				this.setValue(Spinner.this.getPropertyValue());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
	}
	
	public static class StarCheckBox extends CheckBox {
		
		public StarCheckBox(
				PropertyMap settings,
				String propertyName,
				String label,
				boolean afterRestart) {
			super(settings, propertyName, label);
			
			this.setIcon(ImageUtils.getResourceImage(
					"checkbox_star.png",
					18,
					18));
			this.setSelectedIcon(ImageUtils.getResourceImage(
					"checkbox_star_selected.png",
					18,
					18));
		}
		
	}
	
	public static abstract class ComboBox extends JComboBox implements ConfigurationFieldType<JComboBox, Object>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public ComboBox(Object[] items) {
			this(items, null, null);
		}
		
		public ComboBox(ComboBoxModel model) {
			this(model, null, null);
		}
		
		public ComboBox(
				Object[] items,
				PropertyMap settings,
				String propertyName) {
			this(new DefaultComboBoxModel(items), settings, propertyName);
		}
		
		public ComboBox(
				ComboBoxModel model,
				PropertyMap settings,
				String propertyName) {
			super(model);
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setSelectedItem(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				if (this.propertyName != null) {
					this.settings.addPropertyChangeListener(
							propertyName,
							new WeakPropertyChangeListener(this.settings, this));
				}
			}
		}
		
		@Override
		public JComboBox getFieldComponent() {
			return this;
		}
		
		@Override
		public Object getFieldValue() {
			return this.getSelectedItem();
		}
		
		@Override
		public abstract Object getPropertyValue();
		
		@Override
		public abstract void saveAndApplyConfig();
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setSelectedItem(ComboBox.this.getPropertyValue());
		}
		
	}
	
	public static class TextArea extends JTextArea implements ConfigurationFieldType<JTextArea, String>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public TextArea(PropertyMap settings, String propertyName) {
			super(5, 20);
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
			
			this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setText(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JTextArea getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return this.getText();
		}
		
		@Override
		public String getPropertyValue() {
			return this.settings.getStringProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setStringProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setText(TextArea.this.getPropertyValue());
		}
		
	}
	
	public static class TextField extends JTextField implements ConfigurationFieldType<JTextField, String>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public TextField(PropertyMap settings, String propertyName) {
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setText(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JTextField getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return this.getText();
		}
		
		@Override
		public String getPropertyValue() {
			return this.settings.getStringProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setStringProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setText(TextField.this.getPropertyValue());
		}
		
	}
	
	public static abstract class FormattedTextField extends JFormattedTextField implements ConfigurationFieldType<JFormattedTextField, String>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public FormattedTextField(
				AbstractFormatter formatter,
				PropertyMap settings,
				String propertyName) {
			super(formatter);
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setText(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JFormattedTextField getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return this.getText();
		}
		
		@Override
		public abstract String getPropertyValue();
		
		@Override
		public abstract void saveAndApplyConfig();
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setText(FormattedTextField.this.getPropertyValue());
		}
		
	}
	
	public static class PasswordField extends JPasswordField implements ConfigurationFieldType<JPasswordField, String>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public PasswordField(PropertyMap settings, String propertyName) {
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setText(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JPasswordField getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return new String(this.getPassword());
		}
		
		@Override
		public String getPropertyValue() {
			return this.settings.getStringProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setStringProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setText(PasswordField.this.getPropertyValue());
		}
		
	}
	
	public static class ColorChooser implements ConfigurationFieldType<JXColorSelectionButton, Color>, PropertyChangeListener {
		
		private JXColorSelectionButton component;
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public ColorChooser(PropertyMap settings, String propertyName) {
			this.component = new JXColorSelectionButton();
			this.component.setPreferredSize(new Dimension(24, 24));
			this.component.setBorder(BorderFactory.createEmptyBorder());
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
			
		}
		
		@Override
		public void initializeFieldComponent() {
			this.component.setBackground(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public JXColorSelectionButton getFieldComponent() {
			return this.component;
		}
		
		@Override
		public Color getFieldValue() {
			return this.component.getBackground();
		}
		
		@Override
		public Color getPropertyValue() {
			return this.settings.getColorProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setColorProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.component.setBackground(ColorChooser.this.getPropertyValue());
		}
		
	}
	
	public static class FileChooser extends TUFileField implements ConfigurationFieldType<TUFileField, String>, PropertyChangeListener {
		
		private boolean first;
		private PropertyMap settings;
		private String propertyName;
		
		public FileChooser(
				PropertyMap settings,
				String propertyName,
				String label,
				boolean open,
				int fileSelectionMode,
				FileFilter fileFilter,
				String appendFileExtention) {
			super(
					label,
					open,
					settings.getStringProperty(propertyName),
					fileSelectionMode,
					fileFilter,
					appendFileExtention);
			
			this.first = true;
			this.settings = settings;
			this.propertyName = propertyName;
		}
		
		@Override
		public void initializeFieldComponent() {
			this.setFile(this.getPropertyValue());
			
			if (this.first) {
				this.first = false;
				
				this.settings.addPropertyChangeListener(
						propertyName,
						new WeakPropertyChangeListener(this.settings, this));
			}
		}
		
		@Override
		public TUFileField getFieldComponent() {
			return this;
		}
		
		@Override
		public String getFieldValue() {
			return this.getFile();
		}
		
		@Override
		public String getPropertyValue() {
			return this.settings.getStringProperty(this.propertyName);
		}
		
		@Override
		public void saveAndApplyConfig() {
			this.settings.setStringProperty(
					this.propertyName,
					this.getFieldValue());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			this.setFile(FileChooser.this.getPropertyValue());
		}
		
	}
	
	public abstract void initializeFieldComponent();
	
	public abstract ComponentType getFieldComponent();
	
	public abstract ValueType getFieldValue();
	
	public abstract ValueType getPropertyValue();
	
	public abstract void saveAndApplyConfig();
	
}
