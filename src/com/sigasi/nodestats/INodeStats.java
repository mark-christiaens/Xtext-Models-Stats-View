package com.sigasi.nodestats;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSemanticElement;
import org.eclipse.xtext.nodemodel.impl.RootNode;

public abstract class INodeStats {

	private Class<? extends INode> c;
	private long instanceCount;

	public INodeStats(Class<? extends INode> c) {
		this.c = c;
	}

	public String getName() {
		return c.getName();
	}

	public long getInstanceCount() {
		return instanceCount;
	}

	protected String details() {
		return "";
	}

	protected void process(INode iNode) {
		instanceCount++;
	}

	public static class RootNodeStats extends INodeStats {
		public RootNodeStats() {
			super(RootNode.class);
		}
	}

	public static class CompositeNodeStats extends INodeStats {
		public CompositeNodeStats() {
			super(CompositeNode.class);
		}
	}

	public static class CompositeNodeWithSemanticElementStats extends INodeStats {
		public CompositeNodeWithSemanticElementStats() {
			super(CompositeNodeWithSemanticElement.class);
		}

	}

}
