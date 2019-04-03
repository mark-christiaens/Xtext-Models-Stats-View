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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.BidiTreeIterable;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSemanticElement;
import org.eclipse.xtext.nodemodel.impl.RootNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RefreshJob extends Job {

	private final class RefreshWork implements IUnitOfWork<Map<Class, INodeStats>, XtextResource> {
		private final IProgressMonitor monitor;

		private RefreshWork(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public Map<Class, INodeStats> exec(XtextResource resource) throws Exception {
			HashMap<Class, INodeStats> stats = Maps.newHashMap();

			if (resource != null) {
				ICompositeNode rootNode = resource.getParseResult().getRootNode();

				BidiTreeIterable<INode> iterable = rootNode.getAsTreeIterable();

				ImmutableMap<Class, Function<? super Class, ? extends INodeStats>> nodeClassToStatsProvider = ImmutableMap
						.<Class, Function<? super Class, ? extends INodeStats>>builder()
						.put(RootNode.class, c -> new INodeStats.RootNodeStats())
						.put(CompositeNode.class, c -> new INodeStats.CompositeNodeStats())
						.put(CompositeNodeWithSemanticElement.class, c -> new INodeStats.CompositeNodeWithSemanticElementStats())
						.build();
				

				for (INode iNode : iterable) {
					Function<? super Class, ? extends INodeStats> constructor = nodeClassToStatsProvider
							.get(iNode.getClass());
					if (constructor == null) {
						throw new IllegalStateException(
								"Found INode of type which is not yet processed: " + iNode.getClass().getName());
					}

					stats.computeIfAbsent(iNode.getClass(), constructor).process(iNode);
				}
			}

			return stats;
		}
	}

	private StatsPage statsPage;

	public RefreshJob(StatsPage emfStatsView) {
		super("Refreshing EMF stats");
		this.statsPage = emfStatsView;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			Map<Class, INodeStats> stats = refreshINodeStats(monitor);

			if (!monitor.isCanceled()) {
				statsPage.setStats(stats);
			}

			return Status.OK_STATUS;
		} catch (Throwable t) {
			return new Status(IStatus.ERROR, "com.sigasi.emfstats", "Error refreshing EMF stats", t);
		}
	}

	protected Map<Class, INodeStats> refreshINodeStats(final IProgressMonitor monitor) {
		Map<Class, INodeStats> stats = statsPage.getXtextDocument().readOnly(new RefreshWork(monitor));
		return stats;
	}
}
