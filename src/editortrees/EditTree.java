package editortrees;

import java.util.ArrayList;

/**
 * a height-balanced binary tree with rank that could be the basis for a text
 * editor
 * 
 * @author Jared Kagay
 * @author Ethan Townsend
 */
public class EditTree {

	private DisplayableBinaryTree display; // used for graphics

	private int rotations;
	private int size;
	Node root;

	/**
	 * constructs an empty tree
	 * works in O(1) time
	 */
	public EditTree() {

		this.root = Node.NULL_NODE;
		this.size = 0;
		this.rotations = 0;

	} // EditTree

	/**
	 * constructs a single-node tree whose element is the given character
	 * works in O(1) time
	 * 
	 * @param ch to add
	 */
	public EditTree(char ch) {

		this.root = new Node(ch);
		this.size = 1;
		this.rotations = 0;

	} // EditTree

	/**
	 * makes this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * works in O(N) time
	 * 
	 * @param e the tree to copy
	 */
	public EditTree(EditTree e) {

		this.root = new Node(e.root.data).copyTree(e.root);
		this.size = e.size();
		this.rotations = 0;

	} // EditTree

	/**
	 * creates an EditTree whose toString is s. 
	 * works in O(N) time
	 * 
	 * @param s the string
	 */
	public EditTree(String s) {

		this.root = new Node().buildTreeFromString(s).node;
		this.size = s.length();
		this.rotations = 0;

	} // EditTree

	/**
	 * @return the number of nodes in this tree in O(1) time
	 */
	public int size() {

		return this.size;

	} // size

	/**
	 * adds a node with the given character to the end of the tree
	 * works in O(log(N)) time
	 * 
	 * @param ch character to add to the end of this tree.
	 */
	public void add(char ch) {

		NodeInfo nodeInfo = new NodeInfo();
		this.root = this.root.add(ch, nodeInfo);
		this.rotations += nodeInfo.spins;
		this.size++;

	}

	/**
	 * adds a new node to the tree with the given character at the specified index in O(log(N)) time
	 * 
	 * @param ch  character to add
	 * @param pos character added in this in-order position Valid positions range
	 *            from 0 to the size of the tree, inclusive
	 * @throws IndexOutOfBoundsException if pos is negative or too large
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {

		// must check for valid index
		if (pos > this.size || pos < 0)
			throw new IndexOutOfBoundsException();

		NodeInfo nodeInfo = new NodeInfo();
		this.root = this.root.add(ch, pos, nodeInfo);
		this.rotations += nodeInfo.spins;
		size++;

	} // add

	/**
	 * gets the element of the tree at that index
	 * 
	 * @param pos position in the tree
	 * @return the character at that position in O(log(N)) time
	 * @throws IndexOutOfBoundsException if pos is negative or too big
	 */
	public char get(int pos) throws IndexOutOfBoundsException {

		if (pos < 0 || pos >= this.size)
			throw new IndexOutOfBoundsException();

		return this.root.get(pos).data;

	}

	/**
	 * @return height of the tree in O(n) time
	 */
	public int slowHeight() {

		return this.root.slowHeight();

	} // slowHeight

	/**
	 * @return size of the tree in O(n) time
	 */
	public int slowSize() {

		return this.root.slowSize();

	} // slowSize

	/**
	 * returns true iff for every node in the tree the node's rank equals the size
	 * of the left subtree
	 * 
	 * @return true iff each node's rank correctly equals its left subtree's size in O(n) time
	 */
	public boolean ranksMatchLeftSubtreeSize() {

		return this.root.ranksMatchLeftSubtreeSize().balanced;

	} // ranksMatchLeftSubtreeSize

	/**
	 * this one asks for info from each node the output isn't just the elements, but
	 * the elements AND ranks for the tree with root b and children a and c, it
	 * should return the string: [b1, a0, c0]
	 * 
	 * @return The string of elements and ranks given in an PRE-ORDER traversal of
	 *         the tree.
	 */
	public String toRankString() {

		ArrayList<String> list = new ArrayList<String>();
		this.root.toRankString(list);
		return list.toString();

	} // toRankString

	/**
	 * @return the string produced by an in-order traversal of this tree in O(N) time
	 */
	@Override
	public String toString() {

		StringBuilder bob = new StringBuilder(); //bob the StringBuilder 
		this.root.toString(bob); //(Can fix it? yes he can)
		return bob.toString();

	} // toString

	/**
	 * relies on correct balance codes to find the height of the tree
	 * 
	 * @return the height of this tree in O(log n) time
	 */
	public int fastHeight() {

		return this.root.fastHeight();

	} // fastHeight

	/**
	 * similar to toRankString(), but adding in balance codes too.
	 * 
	 * for the tree with root b and a left child a, it should return the string:
	 * [b1/, a0=]
	 * 
	 * @return the string of elements and ranks given in an pre-order traversal of
	 *         the tree in O(log(N)) time
	 */
	public String toDebugString() {

		String str = this.root.toDebugString("");
		if (str.length() < 1)
			return "[" + str + "]"; // empty tree
		return "[" + str.substring(0, str.length() - 2) + "]";

	} // toDebugString

	/**
	 * a double rotation counts as two separate rotations
	 *
	 * @return number of rotations since this tree was created in O(1) time
	 */
	public int totalRotationCount() {

		return this.rotations;

	} // totalRotationCount

	/**
	 * returns true iff for every node in the tree, the node's balance code is
	 * correct based on its childrens' heights.
	 * 
	 * @return true iff each node's balance code is correct in O(n) time
	 */
	public boolean balanceCodesAreCorrect() {

		return this.root.getHeightAndIsBalanced().balanced;

	} // balanceCodesAreCorrect

	/**
	 * deletes the node at the given position in an in-order traversal of the tree
	 * 
	 * @param pos position of character to delete from this tree
	 * @return the character that is deleted in O(log(N)) time
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {

		if (pos < 0 || pos >= this.size)
			throw new IndexOutOfBoundsException();

		NodeInfo nodeInfo = new NodeInfo();
		this.root = this.root.delete(pos, nodeInfo);

		this.size--;
		this.rotations += nodeInfo.spins;
		return nodeInfo.deletedData;

	} // delete

	/**
	 * this method operates in O(length), where length is the parameter provided
	 * 
	 * the way to do this is to recurse/iterate only over the nodes of the tree (and
	 * possibly their children) that contribute to the output string
	 * 
	 * @param pos    location of the beginning of the string to retrieve
	 * @param length length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException unless both pos and pos+length-1 are
	 *                                   legitimate indexes within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {

		if (pos < 0 || pos + length > this.size)
			throw new IndexOutOfBoundsException();

		StringBuilder stringBuilder = new StringBuilder();
		this.root.get(pos, pos + length - 1, stringBuilder);
		return stringBuilder.toString();

	} // get

	/**
	 * it will initialize the display field the first time it is called.
	 */
	public void show() {

		if (this.display == null)
			this.display = new DisplayableBinaryTree(this, 960, 1080, true);
		else
			this.display.show(true);

	} // show

	/**
	 * closes the tree window, still keeps all the data, and you can still re-show
	 * the tree with the show() method
	 */
	public void close() {

		if (this.display != null)
			this.display.close();

	} // close

	/**
	 * node method container class, used in the add and delete methods 
	 * (once was called jelenFanClub and contained some bible references)
	 */
	class NodeInfo {

		int spins;
		boolean balanced; // continue re-balancing until this is true
		char deletedData; // used for the return in delete

		public NodeInfo() {

			this.balanced = false;
			this.spins = 0;

		} // NodeInfo

	} // end NodeInfo

} // end EditTree