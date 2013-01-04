package com.leclercb.taskunifier.gui.components.tasksearcheredit.filter;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;

public class TaskFilterEditPanel extends JPanel implements TreeSelectionListener {
	
	private TaskFilterElementPanel elementPanel;
	private TaskFilterPanel filterPanel;
	
	public TaskFilterEditPanel() {
		this.initialize();
	}
	
	public TaskFilter getFilter() {
		return this.filterPanel.getFilter();
	}
	
	public void setFilter(TaskFilter filter) {
		this.filterPanel.setFilter(filter);
	}
	
	public boolean isAllowCompareModel() {
		return this.elementPanel.isAllowCompareModel();
	}
	
	public void setAllowCompareModel(boolean allowCompareModel) {
		this.elementPanel.setAllowCompareModel(allowCompareModel);
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout(5, 5));
		
		this.elementPanel = new TaskFilterElementPanel();
		
		this.filterPanel = new TaskFilterPanel();
		this.filterPanel.getTree().addTreeSelectionListener(this);
		
		this.add(this.filterPanel, BorderLayout.CENTER);
		this.add(this.elementPanel, BorderLayout.SOUTH);
	}
	
	public void close() {
		this.elementPanel.saveElement();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		this.elementPanel.saveElement();
		
		if (this.filterPanel.getTree().getSelectionCount() != 0) {
			TreeNode node = (TreeNode) this.filterPanel.getTree().getLastSelectedPathComponent();
			
			if (node instanceof TaskFilterElementTreeNode) {
				this.elementPanel.setElement(((TaskFilterElementTreeNode) node).getElement());
				return;
			}
		}
		
		this.elementPanel.setElement(null);
	}
	
}
