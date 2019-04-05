/*******************************************************************************
 * Copyright (c) 2011 Sigasi N.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sigasi N.V.: Mark Christiaens - initial API and implementation
 *******************************************************************************/

package com.sigasi.nodestats;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.Page;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;

public class StatsPage extends Page {

	private static final Logger logger = Logger.getLogger(StatsPage.class);

	private Composite control;
	private RefreshJob refreshJob;
	private IXtextDocument xtextDocument;
	private IXtextModelListener modelListener;

	private TableViewer tableViewer;
	private Table table;
	
	/**
	 * Create the PageBookView Page.
	 */
	public StatsPage(XtextEditor xtextEditor) {
		refreshJob = new RefreshJob(this);
		xtextDocument = xtextEditor.getDocument();
		configureModelListener();
		scheduleRefresh();
	}

	public void scheduleRefresh() {
		refreshJob.cancel();
		refreshJob.schedule();
	}

	protected void configureModelListener() {
		modelListener = new IXtextModelListener() {
			public void modelChanged(XtextResource resource) {
				try {
					scheduleRefresh();
				} catch (Throwable t) {
					logger.error("Error refreshing EMF statistics", t);
				}
			}

		};
		xtextDocument.addModelListener(modelListener);
	}

	/**
	 * Create contents of the PageBookView Page.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		control = container;
		container.setLayout(new GridLayout(1, false));
		
		{
			this.tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			{
				final TableColumn tblclmnClassName = new TableColumn(table, SWT.LEFT);
				tblclmnClassName.setWidth(400);
				tblclmnClassName.setText("INode class");
			}
			{
				final TableColumn tblclmnInstanceCount = new TableColumn(table, SWT.LEFT);
				tblclmnInstanceCount.setWidth(200);
				tblclmnInstanceCount.setText("Stats");
			}
			tableViewer.setLabelProvider(new TableLabelProvider());
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setComparator(new StatsComparator()); 
		}
		
	}
	
	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	public IXtextDocument getXtextDocument() {
		return xtextDocument;
	}

	public void setStats(final Map<Class, INodeStats> stats) {
		Display display = Display.getDefault();

		final Object[] array = stats.values().toArray();
		
		display.asyncExec(new Runnable() {
			public void run() {
				tableViewer.setInput(array); 
			}
		});
	}

}
