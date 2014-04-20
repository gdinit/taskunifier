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
package com.leclercb.taskunifier.gui.api.accessor;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.main.Main;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

public abstract class DefaultPropertyAccessor<T> implements PropertyAccessor<T>, PropertyChangeListener, PropertyChangeSupported {

    private PropertyChangeSupport propertyChangeSupport;

    private String id;
    private String label;
    private PropertyAccessorType type;
    private String fieldSettingsPropertyName;
    private String propertyName;
    private boolean usable;
    private boolean used;
    private boolean editable;
    private boolean sortable;

    public DefaultPropertyAccessor(
            String id,
            String fieldSettingsPropertyName,
            PropertyAccessorType type,
            String propertyName,
            String label,
            boolean editable,
            boolean usable,
            boolean sortable) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);

        this.setId(id);
        this.setFieldSettingsPropertyName(fieldSettingsPropertyName);
        this.setType(type);
        this.setPropertyName(propertyName);
        this.setLabel(label);
        this.setEditable(editable);
        this.setUsable(usable);
        this.setSortable(sortable);

        if (this.fieldSettingsPropertyName == null) {
            this.setUsed(false);
        } else {
            this.setUsed(Main.getSettings().getBooleanProperty(
                    fieldSettingsPropertyName + ".used",
                    true));

            Main.getSettings().addPropertyChangeListener(
                    fieldSettingsPropertyName + ".used",
                    new WeakPropertyChangeListener(Main.getSettings(), this));
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    private void setId(String id) {
        CheckUtils.isNotNull(id);
        this.id = id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    private void setLabel(String label) {
        CheckUtils.isNotNull(label);
        this.label = label;
    }

    @Override
    public PropertyAccessorType getType() {
        return this.type;
    }

    private void setType(PropertyAccessorType type) {
        CheckUtils.isNotNull(type);
        this.type = type;
    }

    @Override
    public String getFieldSettingsPropertyName() {
        return this.fieldSettingsPropertyName;
    }

    private void setFieldSettingsPropertyName(String fieldSettingsPropertyName) {
        this.fieldSettingsPropertyName = fieldSettingsPropertyName;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    private void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean isUsable() {
        return this.usable;
    }

    private void setUsable(boolean usable) {
        this.usable = usable;
    }

    @Override
    public boolean isUsed() {
        return this.used;
    }

    @Override
    public void setUsed(boolean used) {
        if (used == this.isUsed())
            return;

        boolean oldUsed = this.isUsed();
        this.used = used;

        if (this.fieldSettingsPropertyName != null) {
            Main.getSettings().setBooleanProperty(
                    this.fieldSettingsPropertyName + ".used",
                    used);
        }

        this.propertyChangeSupport.firePropertyChange(PROP_USED, oldUsed, used);
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }

    private void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isSortable() {
        return this.sortable;
    }

    private void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    @Override
    public Comparator<?> getComparator() {
        return this.type.getComparator();
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return this.type.getCellRenderer();
    }

    @Override
    public TableCellEditor getCellEditor() {
        return this.type.getCellEditor();
    }

    @Override
    public String getPropertyAsString(T object) {
        CheckUtils.isNotNull(object);

        Object value = this.getProperty(object);
        return this.type.convertPropertyToString(value);
    }

    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof PropertyAccessor) {
            PropertyAccessor<?> pa = (PropertyAccessor<?>) o;

            return new EqualsBuilder().append(this.getId(), pa.getId()).isEquals();
        }

        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCode = new HashCodeBuilder();
        hashCode.append(this.getId());

        return hashCode.toHashCode();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(
                propertyName,
                listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(
                propertyName,
                listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == null)
            this.setUsed(false);
        else
            this.setUsed((Boolean) evt.getNewValue());
    }

}
