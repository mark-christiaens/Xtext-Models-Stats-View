package com.sigasi.nodestats;

import java.util.ArrayList;

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

import com.google.common.base.Joiner;

public abstract class INodeStats {

	protected Class c;
	protected long instanceCount;
	protected ArrayList<String> details;

	public INodeStats(Class c) {
		this.c = c;
		this.details = new ArrayList<>();
	}

	public String getName() {
		return c.getSimpleName();
	}

	public long getInstanceCount() {
		return instanceCount;
	}

	protected String details() {
		calcDetails();
		return Joiner.on(",").join(details);
	}

	protected void calcDetails() {
	}

	protected void process(INode iNode) {
		instanceCount++;
	}

	public static class RootNodeStats extends CompositeNodeWithSemanticElementAndSyntaxErrorStats {
		public RootNodeStats(Class c) {
			super(c);
		}

		public RootNodeStats() {
			this(RootNode.class);
		}
	}

	public static class CompositeNodeStats extends INodeStats {
		int maximumLookahead;
		int accumulatedLookahead;

		public CompositeNodeStats() {
			this(CompositeNodeStats.class);
		}

		public CompositeNodeStats(Class c) {
			super(c);
			this.maximumLookahead = Integer.MIN_VALUE;
			this.accumulatedLookahead = 0;
		}

		@Override
		protected void process(INode iNode) {
			super.process(iNode);
			CompositeNode cn = (CompositeNode) iNode;
			maximumLookahead = Math.max(maximumLookahead, cn.getLookAhead());
			accumulatedLookahead += cn.getLookAhead();
		}

		@Override
		protected void calcDetails() {
			super.calcDetails();
			double averageLookahead = (1.0) * accumulatedLookahead / getInstanceCount();
			details.add("maximum lookahead: " + maximumLookahead);
			details.add("average lookahead: " + averageLookahead);
			return;
		}
	}

	public static class CompositeNodeWithSemanticElementStats extends CompositeNodeStats {
		public CompositeNodeWithSemanticElementStats() {
			this(CompositeNodeWithSemanticElementAndSyntaxError.class);
		}

		public CompositeNodeWithSemanticElementStats(Class c) {
			super(c);
		}
	}

	public static class CompositeNodeWithSemanticElementAndSyntaxErrorStats
			extends CompositeNodeWithSemanticElementStats {
		public CompositeNodeWithSemanticElementAndSyntaxErrorStats() {
			this(CompositeNodeWithSemanticElementAndSyntaxError.class);
		}

		public CompositeNodeWithSemanticElementAndSyntaxErrorStats(Class c) {
			super(c);
		}
	}

	public static class CompositeNodeWithSyntaxErrorStats extends CompositeNodeStats {
		public CompositeNodeWithSyntaxErrorStats() {
			this(CompositeNodeWithSyntaxError.class);
		}

		public CompositeNodeWithSyntaxErrorStats(Class c) {
			super(c);
		}
	}

	public static class SyntheticCompositeNodeStats extends INodeStats {
		public SyntheticCompositeNodeStats() {
			this(SyntheticCompositeNode.class);
		}

		public SyntheticCompositeNodeStats(Class c) {
			super(c);
		}
	}

	public static class LeafNodeStats extends INodeStats {
		private int maximumTotalOffset;
		private long accumulatedTotalOffset;
		private int maximumTotalLength;
		private long accumulatedTotalLength;

		public LeafNodeStats() {
			this(LeafNode.class);
		}

		public LeafNodeStats(Class c) {
			super(c);
			this.maximumTotalLength = Integer.MIN_VALUE;
			this.maximumTotalOffset = Integer.MIN_VALUE;
			this.accumulatedTotalLength = 0;
			this.accumulatedTotalOffset = 0;
		}

		@Override
		protected void process(INode iNode) {
			super.process(iNode);
			LeafNode lN = (LeafNode) iNode;
			maximumTotalOffset = Math.max(maximumTotalOffset, lN.getTotalOffset());
			accumulatedTotalOffset += lN.getTotalOffset();
			maximumTotalLength = Math.max(maximumTotalLength, lN.getTotalLength());
			accumulatedTotalLength += lN.getTotalLength();
		}

		@Override
		protected void calcDetails() {
			super.calcDetails();
			details.add("maximum total offset: " + maximumTotalOffset);
			details.add("average total offset: " + ((1.0) * accumulatedTotalOffset / getInstanceCount()));
			details.add("maximum total length: " + maximumTotalLength);
			details.add("average total length: " + ((1.0) * accumulatedTotalLength / getInstanceCount()));
		}
	}

	public static class LeafNodeWithSyntaxErrorStats extends LeafNodeStats {
		public LeafNodeWithSyntaxErrorStats() {
			this(LeafNodeWithSyntaxError.class);
		}
		
		public LeafNodeWithSyntaxErrorStats(Class c) {
			super (c);
		}
	}

	public static class HiddenLeafNodeStats extends LeafNodeStats {
		public HiddenLeafNodeStats() {
			this(HiddenLeafNodeStats.class);
		}
		
		public HiddenLeafNodeStats(Class c) {
			super(c);
		}
	}

	public static class HiddenLeafNodeWithSyntaxErrorStats extends HiddenLeafNodeStats {
		public HiddenLeafNodeWithSyntaxErrorStats() {
			this (HiddenLeafNodeWithSyntaxErrorStats.class);
		}
		
		public HiddenLeafNodeWithSyntaxErrorStats(Class c) {
			super(c);
		}
	}
}
