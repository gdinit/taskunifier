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
package com.leclercb.taskunifier.gui.components.searcheredit.filter;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.api.searchers.filters.Filter;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.*;
import com.leclercb.taskunifier.gui.commons.models.*;
import com.leclercb.taskunifier.gui.commons.values.*;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultFilterElementPanel<M extends Model, F extends Filter<M, F, FE>, FE extends FilterElement<M, F, FE>> extends JPanel {

    private PropertyAccessorList<M> list;

    private FE element;

    private boolean allowCompareModel;

    private JXComboBox elementColumn;
    private JXComboBox elementCondition;
    private JXComboBox elementValueCb;
    private JTextField elementValueTf;
    private JCheckBox elementCompareModel;

    public DefaultFilterElementPanel(PropertyAccessorList<M> list) {
        CheckUtils.isNotNull(list);
        this.list = list;

        this.allowCompareModel = false;

        this.initialize();
        this.setElement(null);
    }

    public FE getElement() {
        return this.element;
    }

    public boolean isAllowCompareModel() {
        return this.allowCompareModel;
    }

    public void setAllowCompareModel(boolean allowCompareModel) {
        this.allowCompareModel = allowCompareModel;
        this.elementCompareModel.setVisible(allowCompareModel);
    }

    public void saveElement() {
        if (this.element != null) {
            Object elementValue = null;

            if (this.elementValueCb.isVisible())
                elementValue = this.elementValueCb.getSelectedItem();
            else
                elementValue = this.elementValueTf.getText();

            Object value = null;

            switch (((PropertyAccessor<M>) this.elementColumn.getSelectedItem()).getType()) {
                case MODEL:
                case ORDER:
                case TIMER:
                case VOID:
                    value = null;
                    break;
                case BOOLEAN:
                case STAR:
                    value = elementValue.toString();
                    break;
                case FILE:
                case STRING:
                case TAGS:
                    value = elementValue.toString();
                    break;
                case CALENDAR_DATE:
                case CALENDAR_DATE_TIME:
                    try {
                        if (elementValue.toString().length() == 0)
                            value = null;
                        else
                            value = Integer.parseInt(elementValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    break;
                case INTEGER:
                case MINUTES:
                case TIME:
                    try {
                        value = Integer.parseInt(elementValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    break;
                case DOUBLE:
                case PERCENTAGE:
                    try {
                        value = Double.parseDouble(elementValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0.0;
                    }
                    break;
                case CONTACT:
                case CONTEXT:
                case CONTEXTS:
                case FOLDER:
                case GOAL:
                case GOALS:
                case LOCATION:
                case LOCATIONS:
                case NOTE:
                case TASK:
                case TASK_STATUS:
                    value = elementValue;
                    break;
                case TASK_PRIORITY:
                case TASK_REPEAT_FROM:
                    value = elementValue;
                    break;
                default:
                    value = elementValue;
                    break;
            }

            this.element.checkAndSet(
                    (PropertyAccessor<M>) this.elementColumn.getSelectedItem(),
                    (Condition<?, ?>) this.elementCondition.getSelectedItem(),
                    value,
                    this.elementCompareModel.isSelected());
        }
    }

    public void setElement(FE element) {
        if (element == null)
            this.resetFields(null, null, null, false);
        else
            this.resetFields(
                    element.getProperty(),
                    element.getCondition(),
                    element.getValue(),
                    element.isCompareModel());

        this.element = element;
    }

    private void resetFields(
            PropertyAccessor<M> column,
            Condition<?, ?> condition,
            Object value,
            boolean compareModel) {
        FE currentElement = this.element;
        this.element = null;

        this.elementCompareModel.setSelected(compareModel);
        this.elementCompareModel.setEnabled(column != null);

        this.elementValueCb.setVisible(false);
        this.elementValueTf.setVisible(false);

        this.elementColumn.setEnabled(column != null);
        this.elementCondition.setEnabled(column != null);
        this.elementValueCb.setEnabled(column != null);
        this.elementValueTf.setEnabled(column != null);

        this.elementColumn.setModel(new DefaultComboBoxModel());
        this.elementCondition.setModel(new DefaultComboBoxModel());
        this.elementValueCb.setModel(new DefaultComboBoxModel());
        this.elementValueTf.setText("");

        if (column == null) {
            this.elementValueTf.setVisible(true);
            return;
        }

        DefaultComboBoxModel columnsModel = new DefaultComboBoxModel(
                this.list.getAccessors().toArray());

        for (PropertyAccessor<M> accessor : this.list.getAccessors()) {
            if (accessor.getType() == PropertyAccessorType.MODEL)
                columnsModel.removeElement(accessor);
            else if (accessor.getType() == PropertyAccessorType.ORDER)
                columnsModel.removeElement(accessor);
            else if (accessor.getType() == PropertyAccessorType.TIMER)
                columnsModel.removeElement(accessor);
            else if (accessor.getType() == PropertyAccessorType.VOID)
                columnsModel.removeElement(accessor);
        }

        this.elementColumn.setModel(columnsModel);
        this.elementColumn.setSelectedItem(column);

        this.elementValueCb.setRenderer(new DefaultListCellRenderer());

        List<Condition<?, ?>> modelConditionList = new ArrayList<Condition<?, ?>>();
        modelConditionList.addAll(Arrays.asList(ModelCondition.values()));
        modelConditionList.addAll(Arrays.asList(StringCondition.values()));
        modelConditionList.remove(StringCondition.EQUALS);
        modelConditionList.remove(StringCondition.NOT_EQUALS);

        Object[] modelConditions = modelConditionList.toArray();

        switch (column.getType()) {
            case CONTACT:
            case CONTEXT:
            case CONTEXTS:
            case FOLDER:
            case GOAL:
            case GOALS:
            case LOCATION:
            case LOCATIONS:
            case NOTE:
            case TASK:
            case TASK_STATUS:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        modelConditions));

                if (condition == null)
                    condition = (Condition<?, ?>) modelConditions[0];

                if (condition instanceof StringCondition) {
                    this.elementValueTf.setText(value == null ? "" : value.toString());
                    this.elementValueTf.setVisible(true);
                }
                break;
        }

        switch (((PropertyAccessor<M>) this.elementColumn.getSelectedItem()).getType()) {
            case MODEL:
            case ORDER:
            case TIMER:
            case VOID:
                break;
            case BOOLEAN:
            case STAR:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        new Object[]{StringCondition.EQUALS}));
                this.elementValueCb.setModel(new DefaultComboBoxModel(
                        new Object[]{true, false}));
                this.elementValueCb.setRenderer(new DefaultListRenderer(
                        StringValueBoolean.INSTANCE));
                this.elementValueCb.setSelectedIndex(value != null
                        && Boolean.parseBoolean(value.toString()) ? 0 : 1);
                this.elementValueCb.setVisible(true);
                break;
            case FILE:
            case STRING:
            case TAGS:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        StringCondition.values()));
                this.elementValueTf.setText(value == null ? "" : value.toString());
                this.elementValueTf.setVisible(true);
                break;
            case CALENDAR_DATE:
            case CALENDAR_DATE_TIME:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        DaysCondition.values()));
                this.elementValueTf.setText(value == null ? "" : value.toString());
                this.elementValueTf.setVisible(true);
                break;
            case INTEGER:
            case MINUTES:
            case TIME:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        NumberCondition.values()));
                this.elementValueTf.setText(value == null ? "0" : value.toString());
                this.elementValueTf.setVisible(true);
                break;
            case DOUBLE:
            case PERCENTAGE:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        NumberCondition.values()));
                this.elementValueTf.setText(value == null ? "0.0" : value.toString());
                this.elementValueTf.setVisible(true);
                break;
            case CONTACT:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new ContactModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case CONTEXT:
            case CONTEXTS:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new ContextModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case FOLDER:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new FolderModel(true, true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case GOAL:
            case GOALS:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new GoalModel(true, true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case LOCATION:
            case LOCATIONS:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new LocationModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case NOTE:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new NoteModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case TASK:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new TaskModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case TASK_STATUS:
                if (condition instanceof ModelCondition) {
                    this.elementValueCb.setModel(new TaskStatusModel(true));
                    this.elementValueCb.setRenderer(new DefaultListRenderer(
                            StringValueModel.INSTANCE_INDENTED,
                            IconValueModel.INSTANCE));
                    this.elementValueCb.setSelectedItem(value);
                    this.elementValueCb.setVisible(true);
                }
                break;
            case TASK_PRIORITY:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        EnumCondition.values()));
                this.elementValueCb.setModel(new DefaultComboBoxModel(
                        TaskPriority.values()));
                this.elementValueCb.setRenderer(new DefaultListRenderer(
                        StringValueTaskPriority.INSTANCE,
                        IconValueTaskPriority.INSTANCE));
                this.elementValueCb.setSelectedItem(value == null ? TaskPriority.LOW : value);
                this.elementValueCb.setVisible(true);
                break;
            case TASK_REPEAT_FROM:
                this.elementCondition.setModel(new DefaultComboBoxModel(
                        EnumCondition.values()));
                this.elementValueCb.setModel(new DefaultComboBoxModel(
                        TaskRepeatFrom.values()));
                this.elementValueCb.setRenderer(new DefaultListRenderer(
                        StringValueTaskRepeatFrom.INSTANCE));
                this.elementValueCb.setSelectedItem(value == null ? TaskRepeatFrom.DUE_DATE : value);
                this.elementValueCb.setVisible(true);
                break;
            default:
                break;
        }

        if (condition == null)
            this.elementCondition.setSelectedIndex(0);
        else
            this.elementCondition.setSelectedItem(condition);

        this.element = currentElement;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder(
                "fill:default:grow, 10dlu, fill:default:grow, 10dlu, fill:default:grow");

        builder.appendI15d("searcheredit.element.column", true);
        builder.appendI15d("searcheredit.element.condition", true);
        builder.appendI15d("searcheredit.element.value", true);

        // Column
        this.elementColumn = new JXComboBox();
        this.elementColumn.setEnabled(false);
        this.elementColumn.addItemListener(new ItemListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (DefaultFilterElementPanel.this.element == null)
                    return;

                DefaultFilterElementPanel.this.resetFields(
                        (PropertyAccessor<M>) DefaultFilterElementPanel.this.elementColumn.getSelectedItem(),
                        null,
                        null,
                        DefaultFilterElementPanel.this.element.isCompareModel());
            }

        });

        builder.append(this.elementColumn);

        // Condition
        this.elementCondition = new JXComboBox();
        this.elementCondition.setRenderer(new DefaultListRenderer(
                StringValueFilterCondition.INSTANCE));
        this.elementCondition.setEnabled(false);
        this.elementCondition.addItemListener(new ItemListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (DefaultFilterElementPanel.this.element == null)
                    return;

                PropertyAccessor<M> column = (PropertyAccessor<M>) DefaultFilterElementPanel.this.elementColumn.getSelectedItem();

                switch (column.getType()) {
                    case CONTACT:
                    case CONTEXT:
                    case CONTEXTS:
                    case FOLDER:
                    case GOAL:
                    case GOALS:
                    case LOCATION:
                    case LOCATIONS:
                    case MODEL:
                    case NOTE:
                    case TASK:
                    case TASK_STATUS:
                        break;
                    default:
                        return;
                }

                DefaultFilterElementPanel.this.resetFields(
                        (PropertyAccessor<M>) DefaultFilterElementPanel.this.elementColumn.getSelectedItem(),
                        (Condition<?, ?>) DefaultFilterElementPanel.this.elementCondition.getSelectedItem(),
                        null,
                        DefaultFilterElementPanel.this.element.isCompareModel());
            }

        });

        builder.append(this.elementCondition);

        // Value
        this.elementValueCb = new JXComboBox();
        this.elementValueCb.setEnabled(false);

        this.elementValueTf = new JTextField();
        this.elementValueTf.setEnabled(false);

        this.elementCompareModel = new JCheckBox(
                Translations.getString("searcheredit.element.compare_model"));
        this.elementCompareModel.setVisible(this.allowCompareModel);

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(this.elementValueCb);
        valuePanel.add(this.elementValueTf);
        valuePanel.add(this.elementCompareModel);

        builder.append(valuePanel);

        // Lay out the panel
        panel.add(builder.getPanel(), BorderLayout.CENTER);

        this.add(panel, BorderLayout.CENTER);
    }

}
