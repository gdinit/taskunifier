package com.leclercb.taskunifier.gui.components.tasksearcheredit.filter;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;

public class TaskFilterEditPanel extends JPanel implements TreeSelectionListener {
	
	private TaskFilterLinkPanel filterLinkPanel;
	private TaskFilterElementPanel elementPanel;
	private TaskFilterPanel filterPanel;
	
	private JPanel southPanel;
	
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
		
		this.southPanel = new JPanel(new CardLayout());
		
		this.elementPanel = new TaskFilterElementPanel();
		this.southPanel.add(this.elementPanel, "ELEMENT");
		
		this.filterLinkPanel = new TaskFilterLinkPanel();
		this.southPanel.add(this.filterLinkPanel, "LINK");
		
		this.filterPanel = new TaskFilterPanel();
		this.filterPanel.getTree().addTreeSelectionListener(this);
		
		this.add(this.filterPanel, BorderLayout.CENTER);
		this.add(this.southPanel, BorderLayout.SOUTH);
		
		((CardLayout) this.southPanel.getLayout()).show(
				this.southPanel,
				"ELEMENT");
	}
	
	public void saveChanges() {
		this.elementPanel.saveElement();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		this.elementPanel.saveElement();
		
		if (this.filterPanel.getTree().getSelectionCount() != 0) {
			TreeNode node = (TreeNode) this.filterPanel.getTree().getLastSelectedPathComponent();
			
			if (node instanceof TaskFilterElementTreeNode) {
				((CardLayout) this.southPanel.getLayout()).show(
						this.southPanel,
						"ELEMENT");
				this.elementPanel.setElement(((TaskFilterElementTreeNode) node).getElement());
				return;
			}
			
			if (node instanceof TaskFilterTreeNode) {
				((CardLayout) this.southPanel.getLayout()).show(
						this.southPanel,
						"LINK");
				this.filterLinkPanel.setFilter(((TaskFilterTreeNode) node).getFilter());
				return;
			}
		}
		
		this.elementPanel.setElement(null);
	}
	
}
