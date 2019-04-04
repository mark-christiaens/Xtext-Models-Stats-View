package com.sigasi.nodestats;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSemanticElement;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSemanticElementAndSyntaxError;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSyntaxError;
import org.eclipse.xtext.nodemodel.impl.HiddenLeafNode;
import org.eclipse.xtext.nodemodel.impl.HiddenLeafNodeWithSyntaxError;
import org.eclipse.xtext.nodemodel.impl.LeafNode;
import org.eclipse.xtext.nodemodel.impl.LeafNodeWithSyntaxError;
import org.eclipse.xtext.nodemodel.impl.RootNode;
import org.eclipse.xtext.nodemodel.impl.SyntheticCompositeNode;

public abstract class INodeStats<T extends INode> {

	private Class<T> c;
	private long instanceCount;

	public INodeStats(Class<T> c) {
		this.c = c;
	}

	public String getName() {
		return c.getSimpleName();
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

	public static class RootNodeStats extends INodeStats<RootNode> {
		public RootNodeStats() {
			super(RootNode.class);
		}
	}

	public static class CompositeNodeStats extends INodeStats<CompositeNode> {
		public CompositeNodeStats() {
			super(CompositeNode.class);
		}
	}

	public static class CompositeNodeWithSemanticElementStats extends INodeStats<CompositeNodeWithSemanticElement> {
		public CompositeNodeWithSemanticElementStats() {
			super(CompositeNodeWithSemanticElement.class);
		}
	}

	public static class CompositeNodeWithSemanticElementAndSyntaxErrorStats
			extends INodeStats<CompositeNodeWithSemanticElementAndSyntaxError> {
		public CompositeNodeWithSemanticElementAndSyntaxErrorStats() {
			super(CompositeNodeWithSemanticElementAndSyntaxError.class);
		}
	}

	public static class CompositeNodeWithSyntaxErrorStats extends INodeStats<CompositeNodeWithSyntaxError> {
		public CompositeNodeWithSyntaxErrorStats() {
			super(CompositeNodeWithSyntaxError.class);
		}
	}

	public static class SyntheticCompositeNodeStats extends INodeStats<SyntheticCompositeNode> {
		public SyntheticCompositeNodeStats() {
			super(SyntheticCompositeNode.class);
		}
	}

	public static class LeafNodeStats extends INodeStats<LeafNode> {
		public LeafNodeStats() {
			super(LeafNode.class);
		}
	}

	public static class LeafNodeWithSyntaxErrorStats extends INodeStats<LeafNodeWithSyntaxError> {
		public LeafNodeWithSyntaxErrorStats() {
			super(LeafNodeWithSyntaxError.class);
		}
	}

	public static class HiddenLeafNodeStats extends INodeStats<HiddenLeafNode> {
		public HiddenLeafNodeStats() {
			super(HiddenLeafNode.class);
		}
	}

	public static class HiddenLeafNodeWithSyntaxErrorStats extends INodeStats<HiddenLeafNodeWithSyntaxError> {
		public HiddenLeafNodeWithSyntaxErrorStats() {
			super(HiddenLeafNodeWithSyntaxError.class);
		}
	}
}
