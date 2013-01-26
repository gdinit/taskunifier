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
package com.leclercb.taskunifier.gui.api.searchers.coders;

import java.util.Calendar;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.leclercb.commons.api.coder.AbstractXMLCoder;
import com.leclercb.commons.api.coder.exc.FactoryCoderException;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.XMLUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.CalendarCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.Condition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.DaysCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.EnumCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.ModelCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.NumberCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;

public class TaskFilterXMLCoder extends AbstractXMLCoder<TaskFilter> {
	
	private static final String NULL_STRING_VALUE = "{{NULL}}";
	
	public TaskFilterXMLCoder() {
		super("filter");
	}
	
	@Override
	protected TaskFilter decode(Node node) throws FactoryCoderException {
		try {
			NodeList nFilter = node.getChildNodes();
			TaskFilter filter = new TaskFilter();
			filter.setLink(FilterLink.valueOf(XMLUtils.getAttributeValue(
					node,
					"link")));
			
			for (int i = 0; i < nFilter.getLength(); i++) {
				if (nFilter.item(i).getNodeName().equals("element")) {
					NodeList nElement = nFilter.item(i).getChildNodes();
					TaskFilterElement element = null;
					
					PropertyAccessor<Task> column = null;
					String conditionClass = null;
					String enumName = null;
					String valueStr = null;
					boolean compareModel = false;
					
					for (int j = 0; j < nElement.getLength(); j++) {
						if (nElement.item(j).getNodeName().equals("column")) {
							try {
								String col = nElement.item(j).getTextContent();
								
								if (EqualsUtils.equals(col, "CONTEXT"))
									col = "CONTEXTS";
								else if (EqualsUtils.equals(col, "GOAL"))
									col = "GOALS";
								else if (EqualsUtils.equals(col, "LOCATION"))
									col = "LOCATIONS";
								
								column = TaskColumnList.getInstance().get(col);
							} catch (Throwable t) {
								GuiLogger.getLogger().log(
										Level.WARNING,
										"Invalid filter column",
										t);
							}
						}
						
						if (nElement.item(j).getNodeName().equals("condition")) {
							String[] values = nElement.item(j).getTextContent().split(
									"\\.");
							conditionClass = values[0];
							enumName = values[1];
						}
						
						if (nElement.item(j).getNodeName().equals("value")) {
							valueStr = nElement.item(j).getTextContent();
							
							if (valueStr.equals(NULL_STRING_VALUE))
								valueStr = null;
						}
						
						if (nElement.item(j).getNodeName().equals(
								"comparemodel")) {
							compareModel = Boolean.parseBoolean(nElement.item(j).getTextContent());
						}
					}
					
					if (column != null
							&& conditionClass.equals("CalendarCondition")) {
						CalendarCondition condition = CalendarCondition.valueOf(enumName);
						Calendar value = null;
						
						if (valueStr != null) {
							value = Calendar.getInstance();
							value.setTimeInMillis(Long.parseLong(valueStr));
						}
						
						element = new TaskFilterElement(
								column,
								condition,
								value,
								compareModel);
					} else if (column != null
							&& conditionClass.equals("DaysCondition")) {
						DaysCondition condition = DaysCondition.valueOf(enumName);
						Integer value = null;
						
						if (valueStr != null) {
							value = Integer.parseInt(valueStr);
						}
						
						element = new TaskFilterElement(
								column,
								condition,
								value,
								compareModel);
					} else if (column != null
							&& conditionClass.equals("StringCondition")) {
						StringCondition condition = StringCondition.valueOf(enumName);
						String value = null;
						
						if (valueStr != null) {
							value = valueStr;
						}
						
						element = new TaskFilterElement(
								column,
								condition,
								value,
								compareModel);
					} else if (column != null
							&& conditionClass.equals("NumberCondition")) {
						NumberCondition condition = NumberCondition.valueOf(enumName);
						Number value = null;
						
						if (valueStr != null) {
							value = Double.parseDouble(valueStr);
						}
						
						element = new TaskFilterElement(
								column,
								condition,
								value,
								compareModel);
					} else if (column != null
							&& conditionClass.equals("EnumCondition")) {
						Condition<?, ?> condition = EnumCondition.valueOf(enumName);
						Object value = null;
						
						if (valueStr != null) {
							String valueClass = valueStr.substring(
									0,
									valueStr.lastIndexOf("#"));
							String valueEnum = valueStr.substring(
									valueStr.lastIndexOf("#") + 1,
									valueStr.length());
							
							try {
								Object[] enums = Class.forName(valueClass).getEnumConstants();
								
								for (int j = 0; j < enums.length; j++) {
									Enum<?> e = (Enum<?>) enums[j];
									if (e.name().equals(valueEnum))
										value = e;
								}
							} catch (Throwable t) {
								if (EqualsUtils.equals(
										condition,
										EnumCondition.NOT_EQUALS))
									condition = StringCondition.NOT_EQUALS;
								else
									condition = StringCondition.EQUALS;
								
								value = valueEnum;
								
								if (EqualsUtils.equals(
										valueStr,
										"com.leclercb.taskunifier.api.models.enums.TaskStatus#NEXT_ACTION")) {
									value = "Next Action";
								}
							}
						}
						
						element = new TaskFilterElement(
								column,
								condition,
								value,
								compareModel);
					} else if (column != null
							&& conditionClass.equals("ModelCondition")) {
						ModelCondition condition = ModelCondition.valueOf(enumName);
						Model value = null;
						
						if (valueStr != null) {
							try {
								if (column.getType() == PropertyAccessorType.TASK)
									value = TaskFactory.getInstance().get(
											new ModelId(valueStr));
								else if (column.getType() == PropertyAccessorType.CONTEXT
										|| column.getType() == PropertyAccessorType.CONTEXTS)
									value = ContextFactory.getInstance().get(
											new ModelId(valueStr));
								else if (column.getType() == PropertyAccessorType.FOLDER)
									value = FolderFactory.getInstance().get(
											new ModelId(valueStr));
								else if (column.getType() == PropertyAccessorType.GOAL
										|| column.getType() == PropertyAccessorType.GOALS)
									value = GoalFactory.getInstance().get(
											new ModelId(valueStr));
								else if (column.getType() == PropertyAccessorType.LOCATION
										|| column.getType() == PropertyAccessorType.LOCATIONS)
									value = LocationFactory.getInstance().get(
											new ModelId(valueStr));
								else if (column.getType() == PropertyAccessorType.TASK)
									value = TaskFactory.getInstance().get(
											new ModelId(valueStr));
							} catch (Exception e) {
								value = null;
							}
						}
						
						if (valueStr == null || value != null)
							element = new TaskFilterElement(
									column,
									condition,
									value,
									compareModel);
					}
					
					if (element != null)
						filter.addElement(element);
				}
				
				if (nFilter.item(i).getNodeName().equals("filter")) {
					filter.addFilter(this.decode(nFilter.item(i)));
				}
			}
			
			return filter;
		} catch (Exception e) {
			throw new FactoryCoderException(e.getMessage(), e);
		}
	}
	
	@Override
	protected void encode(Document document, Element root, TaskFilter filter) {
		root.setAttribute("link", filter.getLink().name());
		
		for (TaskFilterElement e : filter.getElements()) {
			Element element = document.createElement("element");
			root.appendChild(element);
			
			Element column = document.createElement("column");
			column.setTextContent(e.getProperty().getId());
			element.appendChild(column);
			
			Element condition = document.createElement("condition");
			element.appendChild(condition);
			
			Element value = document.createElement("value");
			element.appendChild(value);
			
			if (e.getCondition() instanceof CalendarCondition) {
				condition.setTextContent("CalendarCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null)
					value.setTextContent(((Calendar) e.getValue()).getTimeInMillis()
							+ "");
			} else if (e.getCondition() instanceof DaysCondition) {
				condition.setTextContent("DaysCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null)
					value.setTextContent((e.getValue()) + "");
			} else if (e.getCondition() instanceof StringCondition) {
				condition.setTextContent("StringCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null)
					value.setTextContent((String) e.getValue());
			} else if (e.getCondition() instanceof NumberCondition) {
				condition.setTextContent("NumberCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null)
					value.setTextContent((e.getValue()) + "");
			} else if (e.getCondition() instanceof EnumCondition) {
				condition.setTextContent("EnumCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null)
					value.setTextContent(e.getValue().getClass().getName()
							+ "#"
							+ ((Enum<?>) e.getValue()).name());
			} else if (e.getCondition() instanceof ModelCondition) {
				condition.setTextContent("ModelCondition."
						+ e.getCondition().name());
				
				if (e.getValue() != null) {
					ModelId id = ((Model) e.getValue()).getModelId();
					value.setTextContent(id.getId());
				}
			}
			
			if (e.getValue() == null)
				value.setTextContent(NULL_STRING_VALUE);
			
			Element compareModel = document.createElement("comparemodel");
			compareModel.setTextContent(e.isCompareModel() + "");
			element.appendChild(compareModel);
		}
		
		for (TaskFilter f : filter.getFilters()) {
			Element element = document.createElement("filter");
			root.appendChild(element);
			
			this.encode(document, element, f);
		}
	}
	
}
