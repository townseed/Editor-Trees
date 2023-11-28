package editortrees;

import java.util.ArrayList;
import editortrees.EditTree.NodeInfo;

/**
 * a node in a height-balanced binary tree with rank
 * except for the NULL_NODE, one node cannot belong to two different trees
 * 
 * @author Jared Kagay
 * @author Ethan Townsend
 */
public class Node {
	
	DisplayableNodeWrapper displayableNodeWrapper; // used for graphics

	char data;
	Node left, right; // subtrees
	int rank; // in-order position of this node within its own subtree.
	Code balance; // whether the left or right subtree has a greater height

	static final Node NULL_NODE = new Node();

	public Node(char data, Node left, Node right) {

		this.data = data;
		this.left = left;
		this.right = right;
		this.rank = 0;
		this.balance = Code.SAME;

		this.displayableNodeWrapper = new DisplayableNodeWrapper(this);

	} // Node

	public Node() {
		
		this('\0', null, null);
		
	} // Node

	public Node(char data) {

		this(data, NULL_NODE, NULL_NODE);
		
	} // Node

	enum Code {
		
		LEFT, SAME, RIGHT;

		// used in the displayer and debug string
		public String toString() {
			
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
			
		} // toString
		
	} // Code
	
	public Node add(char ch, NodeInfo nodeInfo) {

		if (this == NULL_NODE) return new Node(ch); // base case

		// move all the way right to get to last index
		this.right = this.right.add(ch, nodeInfo);

		// update balance codes moving up the tree
		if (!nodeInfo.balanced) {
			
			// update balance codes
			if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
				nodeInfo.balanced = true;
			}
			else if (this.balance == Code.SAME)
				this.balance = Code.RIGHT;
			else if (this.balance == Code.RIGHT) {
				nodeInfo.balanced = true;
				nodeInfo.spins++;
				this.balance = Code.SAME;

				// needs double rotation
				if (this.right.balance == Code.LEFT) {
					nodeInfo.spins++;

					// updates balance codes
					this.right.balance = Code.SAME;
					if (this.right.left.balance == Code.RIGHT)
						this.balance = Code.LEFT;
					else if (this.right.left.balance == Code.LEFT)
						this.right.balance = Code.RIGHT;
					this.right.left.balance = Code.SAME;

					// first part of double rotation
					this.right = this.rotateRight(this.right, this.right.left);
					
				}
				else this.right.balance = Code.SAME;

				// single rotation
				return this.rotateLeft(this, this.right);
				
			} // end rotations
			
		} // end balancing

		return this;

	} // add

	public Node add(char ch, int pos, NodeInfo nodeInfo) {

		if (this == NULL_NODE) return new Node(ch);

		if (pos <= this.rank) { // the index is too large

			// move to the left
			this.left = this.left.add(ch, pos, nodeInfo);

			// update balance codes moving up the tree
			if (!nodeInfo.balanced) {
				
				// update balance codes
				if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
					nodeInfo.balanced = true;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				} else if (this.balance == Code.LEFT) {
					nodeInfo.balanced = true;
					this.rank++;
					nodeInfo.spins++;
					this.balance = Code.SAME;

					// needs double rotation
					if (this.left.balance == Code.RIGHT) {
						nodeInfo.spins++;

						// updates balance codes
						this.left.balance = Code.SAME;
						if (this.left.right.balance == Code.LEFT)
							this.balance = Code.RIGHT;
						else if (this.left.right.balance == Code.RIGHT)
							this.left.balance = Code.LEFT;
						this.left.right.balance = Code.SAME;

						// first part of double rotation
						this.left = this.rotateLeft(this.left, this.left.right);
						
					}
					else this.left.balance = Code.SAME;

					// single rotation
					return this.rotateRight(this, this.left);
					
				} // end rotations

			} // end balancing

			this.rank++; // the new node will become part of the left subtree

		} // end move right

		else { // the index is too small
			
			// move to the right
			this.right = this.right.add(ch, pos - this.rank - 1, nodeInfo);

			// update balance codes moving up the tree
			if (!nodeInfo.balanced) {
				
				// update balance codes
				if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
					nodeInfo.balanced = true;
				} else if (this.balance == Code.SAME)
					this.balance = Code.RIGHT;
				else if (this.balance == Code.RIGHT) {
					nodeInfo.balanced = true;
					nodeInfo.spins++;
					this.balance = Code.SAME;

					// needs double rotation
					if (this.right.balance == Code.LEFT) {
						nodeInfo.spins++;

						// updates balance codes
						this.right.balance = Code.SAME;
						if (this.right.left.balance == Code.RIGHT)
							this.balance = Code.LEFT;
						else if (this.right.left.balance == Code.LEFT)
							this.right.balance = Code.RIGHT;
						this.right.left.balance = Code.SAME;

						// first part of double rotation
						this.right = this.rotateRight(this.right, this.right.left);
						
					}
					else this.right.balance = Code.SAME;

					// single rotation
					return this.rotateLeft(this, this.right);
					
				} // end rotations
				
			} // end balancing
			
		} // end move right

		return this;

	} // add

	/**
	 * method for a single left rotation at the current node
	 * works in O(1) time
	 */
	private Node rotateLeft(Node parent, Node child) {
		
		// switches parent and child
		Node temp = child.left;
		child.left = parent;
		parent.right = temp;
		
		child.rank += parent.rank + 1;
		return child;
		
	} // singleLeftRotation

	/**
	 * method for a single right rotation at the current node
	 * works in O(1) time
	 */
	private Node rotateRight(Node parent, Node child) {
		
		// switches parent and child
		Node temp = child.right;
		child.right = parent;
		parent.left = temp;
		
		parent.rank = parent.rank - child.rank - 1;
		return child;
		
	} // singleRightRotation

	public Node get(int pos) {

		if (this == NULL_NODE) throw new IndexOutOfBoundsException();

		if (pos < this.rank) return this.left.get(pos); // must be in left subtree
		
		if (pos > this.rank) return this.right.get(pos - this.rank - 1); // must be in right subtree

		return this; // index is found

	} // get

	int slowHeight() {
		
		if (this == NULL_NODE) return -1;
		
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
		
	} // slowHeight

	public int slowSize() {
		
		if (this == NULL_NODE) return 0;
		
		return left.slowSize() + right.slowSize() + 1;
		
	} // slowSize
	
	public SizeAndBalanced ranksMatchLeftSubtreeSize() {
		
		if (this == NULL_NODE) return new SizeAndBalanced(0, true);

		SizeAndBalanced leftSizeAndBalanced = this.left.ranksMatchLeftSubtreeSize();
		SizeAndBalanced rightSizeAndBalanced = this.right.ranksMatchLeftSubtreeSize();
		
		int sum = leftSizeAndBalanced.size + 1 + rightSizeAndBalanced.size;
		boolean rankMatch = leftSizeAndBalanced.size == this.rank // ensures the rank is correct
				&& rightSizeAndBalanced.balanced && leftSizeAndBalanced.balanced;
		
		return (new SizeAndBalanced(sum, rankMatch));
		
	} // ranksMatchLeftSubtreeSize

	/**
	 * container class that stores the size of the left subtree to ensure the ranks are accurate
	 */
	public class SizeAndBalanced {
		
		int size;
		boolean balanced;

		public SizeAndBalanced(int size, boolean balanced) {
			
			this.size = size;
			this.balanced = balanced;
			
		} // SumAndBalanced
		
	} // SumAndBalanced

	public void toRankString(ArrayList<String> list) {

		if (this == NULL_NODE) return;

		// pre-order iteration
		list.add("" + this.data + this.rank);
		this.left.toRankString(list);
		this.right.toRankString(list);

	} // toRankString

	public void toString(StringBuilder stringBuilder) {

		if (this == NULL_NODE) return;
		
		// create pre-order string
		this.left.toString(stringBuilder);
		stringBuilder.append(this.data);
		this.right.toString(stringBuilder);

	} // toString

	public Node copyTree(Node currentNode) {

		// end recursion at null node
		if (currentNode == NULL_NODE) return NULL_NODE;

		// set left & right nodes
		this.left = new Node(currentNode.left.data).copyTree(currentNode.left);
		this.right = new Node(currentNode.right.data).copyTree(currentNode.right);

		// update rank & balance code
		this.rank = currentNode.rank;
		this.balance = currentNode.balance;

		return this;

	} // copyTree

	public int fastHeight() {

		// null node has height of -1
		if (this == NULL_NODE) return -1;

		// moves in the direction of the greatest height
		// as that is where the balance code is angled
		if (this.balance == Code.RIGHT) 
			return 1 + this.right.fastHeight();
		return 1 + this.left.fastHeight();

	} // fastHeight

	public String toDebugString(String string) {
		
		if (this == NULL_NODE) return string;
		
		return string + this.data + this.rank + this.balance.toString() + ", " 
			+ this.left.toDebugString(string) + this.right.toDebugString(string);
		
	} // toDebugString

	public HeightAndBalanced getHeightAndIsBalanced() {

		// start with a balanced null node with height -1
		if (this == NULL_NODE) return new HeightAndBalanced(-1, true);

		// retrieve values from children
		HeightAndBalanced leftHeightAndBalanced = this.left.getHeightAndIsBalanced();
		HeightAndBalanced rightHeightAndBalanced = this.right.getHeightAndIsBalanced();

		// calculate height in O(log(n)) time
		int leftHeight = leftHeightAndBalanced.height;
		int rightHeight = rightHeightAndBalanced.height;
		int height = Math.max(leftHeight, rightHeight) + 1;

		// ensures left and right subtrees are also balanced
		boolean balanced = leftHeightAndBalanced.balanced && rightHeightAndBalanced.balanced;

		// check balance codes in relation to heights
		if (leftHeight > rightHeight)
			return new HeightAndBalanced(height, balanced && this.balance == Code.LEFT);
		if (leftHeight < rightHeight)
			return new HeightAndBalanced(height, balanced && this.balance == Code.RIGHT);
		return new HeightAndBalanced(height, balanced && this.balance == Code.SAME);

	} // getHeightAndIsBalanced

	/**
	 * used for the balanceCodesAreCorrect method
	 * allows getHeightAndIsBalanced to run in O(n) time
	 */
	public class HeightAndBalanced {

		public int height;
		public boolean balanced;

		public HeightAndBalanced(int height, boolean balanced) {

			this.height = height;
			this.balanced = balanced;

		} // HeightAndBalanced

	} // end HeightAndBalanced

	/*
	 * recursive delete helper method
	 * (we had fun variable names, but I guess they were too fun :/ )
	 */
	public Node delete(int index, NodeInfo nodeInfo) {

		if (this == NULL_NODE) return this;
		
		else if (this.rank > index) { // the index is too large
			
			// move left
			this.left = this.left.delete(index, nodeInfo);
			this.rank--; // the node is being removed from the left subtree
			
			// update balance codes moving up the tree
			if (!nodeInfo.balanced) {
				
				// update balance codes
				if (this.balance == Code.LEFT)
					this.balance = Code.SAME;
				else if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
					nodeInfo.balanced = true;
				}
				
				// requires rotation
				else if (this.balance == Code.RIGHT) {
					
					nodeInfo.spins++;
					this.balance = Code.SAME;
					
					// needs double rotation
					if (this.right.balance == Code.LEFT) {
						nodeInfo.spins++;

						// updates balance codes
						this.right.balance = Code.SAME;
						if (this.right.left.balance == Code.RIGHT)
							this.balance = Code.LEFT;
						else if (this.right.left.balance == Code.LEFT)
							this.right.balance = Code.RIGHT;
						this.right.left.balance = Code.SAME;

						// first part of double rotation
						this.right = this.rotateRight(this.right, this.right.left);
						
					} 
					
					// the special case
					else if (this.right.balance == Code.SAME) {
						
						this.right.balance = Code.LEFT;
						this.balance = Code.RIGHT;
						nodeInfo.balanced = true;
						
					}
					else this.right.balance = Code.SAME;

					// single rotation
					return this.rotateLeft(this, this.right);
					
				} // end rotations
				
			} // end balancing

		} // end move left
		
		else if (this.rank < index) { // index is too small
			
			// move right
			this.right = this.right.delete(index - this.rank - 1, nodeInfo);
			
			// update balance codes moving up the tree
			if (!nodeInfo.balanced) {
				
				// update balance codes
				if (this.balance == Code.RIGHT)
					this.balance = Code.SAME;
				else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
					nodeInfo.balanced = true;
				}
				
				// needs rotation
				else if (this.balance == Code.LEFT) {
					
					nodeInfo.spins++;
					this.balance = Code.SAME;

					// needs double rotation
					if (this.left.balance == Code.RIGHT) {
						nodeInfo.spins++;

						// updates balance codes
						this.left.balance = Code.SAME;
						if (this.left.right.balance == Code.LEFT)
							this.balance = Code.RIGHT;
						else if (this.left.right.balance == Code.RIGHT)
							this.left.balance = Code.LEFT;
						this.left.right.balance = Code.SAME;
						
						// first part of double rotation
						this.left = this.rotateLeft(this.left, this.left.right);

					}
					
					// special case
					else if (this.left.balance == Code.SAME) {
						
						this.left.balance = Code.RIGHT;
						this.balance = Code.LEFT;
						nodeInfo.balanced = true;
						
					}
					else this.left.balance = Code.SAME;

					// single rotation
					return this.rotateRight(this, this.left);
					
				} // end rotations
				
			} // end balancing
			
		} // end move right
		
		else { // node is found to delete
			
			nodeInfo.deletedData = this.data;
			
			// no children or one child
			if (this.left == NULL_NODE) return this.right;
			if (this.right == NULL_NODE) return this.left;
			
			// find successor to delete and inherit data
			char data = this.data;
			this.right = this.right.delete(0, nodeInfo);
			this.data = nodeInfo.deletedData;
			nodeInfo.deletedData = data;
			
			// update balance codes moving up the tree
			if (!nodeInfo.balanced) {
				
				// update balance codes
				if (this.balance == Code.RIGHT)
					this.balance = Code.SAME;
				else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
					nodeInfo.balanced = true;
				}
				
				// requires rotation
				else if (this.balance == Code.LEFT) {
					
					// rotate
					nodeInfo.spins++;
					this.balance = Code.SAME;

					// needs double rotation
					if (this.left.balance == Code.RIGHT) {
						nodeInfo.spins++;

						// updates balance codes
						this.left.balance = Code.SAME;
						if (this.left.right.balance == Code.LEFT)
							this.balance = Code.RIGHT;
						else if (this.left.right.balance == Code.RIGHT)
							this.left.balance = Code.LEFT;
						this.left.right.balance = Code.SAME;
						
						// first part of double rotation
						this.left = this.rotateLeft(this.left, this.left.right);
						
					}
					
					// special case
					else if (this.right.balance == Code.SAME) {
						
						this.right.balance = Code.LEFT;
						this.balance = Code.RIGHT;
						nodeInfo.balanced = true;
						
					}
					else this.left.balance = Code.SAME;

					// single rotation
					return this.rotateRight(this, this.left);
					
				} // end rotations
				
			} // end balancing
			
		} // end replacement
		
		return this;
		
	} // end of the delete method

	public NodeAndHeight buildTreeFromString(String s) {
		
		if (s.equals("")) return new NodeAndHeight(NULL_NODE, -1);
		
		int length = s.length() / 2;
		
		// build left subtree
		String leftS = s.substring(0, length);
		NodeAndHeight leftNodeAndHeight = new Node().buildTreeFromString(leftS);
		this.left = leftNodeAndHeight.node;

		// build right subtree
		String rightS = s.substring(length + 1);
		NodeAndHeight rightNodeAndHeight = new Node().buildTreeFromString(rightS);
		this.right = rightNodeAndHeight.node;
		
		int leftHeight = leftNodeAndHeight.height;
		int rightHeight = rightNodeAndHeight.height;
		
		// build this node using given data
		if (leftHeight > rightHeight) this.balance = Code.LEFT;
		else if (rightHeight > leftHeight) this.balance = Code.RIGHT;
		else this.balance = Code.SAME;
		
		this.data = s.charAt(length);
		this.rank = leftS.length();
		
		return new NodeAndHeight(this, Math.max(leftHeight, rightHeight) + 1);
		
	} // buildTreeFromString
	
	/**
	 * used for the string constructor
	 * creates tree in O(n) time
	 */
	public class NodeAndHeight {
		
		Node node;
		int height;
		
		public NodeAndHeight(Node node, int height) {
			
			this.node = node;
			this.height = height;
			
		} // NodeAndHeight
		
	} // end NodeAndHeight

	public void get(int leftPos, int rightPos, StringBuilder stringBuilder) {
		
		if (this == NULL_NODE) return;
		
		// get values in left subtree
		if (leftPos < this.rank)
			left.get(leftPos, Math.min(this.rank - 1, rightPos), stringBuilder);
		
		// append this value if in range
		if (leftPos <= this.rank && this.rank <= rightPos)
			stringBuilder.append(this.data);
		
		// get values in right subtree
		if (rightPos > this.rank)
			right.get(Math.max(leftPos - rank - 1, 0), rightPos - rank - 1, stringBuilder);
		
	} // end get

	public boolean hasLeft() {
		
		return this.left != NULL_NODE;
		
	} // hasLeft

	public boolean hasRight() {
		
		return this.right != NULL_NODE;
		
	} // hasRight

	public boolean hasParent() {
		
		return false;
		
	} // hasParent

	public Node getParent() {
		
		return NULL_NODE;
		
	} // getParent
	
} // end Node