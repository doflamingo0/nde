package ga;

public class NodeDepth {
	private int node;
	private int depth;
	
	public NodeDepth() {
		
	}
	public NodeDepth(NodeDepth that) {
		this(that.node, that.depth);
	}
	
	public NodeDepth(int node, int depth) {
		this.node = node;
		this.depth = depth;
	}
	
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "["+node+", " + depth+"]";
		
	}
	
}
