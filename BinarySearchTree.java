package implementation;

import java.util.Comparator;
import java.util.TreeSet;

//import trees.BinarySearchTree.Node;

public class BinarySearchTree<K, V> {

	private class Node {
		private K key;
		private V value;
		private Node left, right;

		private Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	private Node root;
	private int treeSize, maxEntries;
	private Comparator<K> comparator;

	public BinarySearchTree(Comparator<K> comparator, int maxEntries) {
		if (comparator == null || maxEntries < 1) {
			throw new IllegalArgumentException("Invalid");
		}
		this.comparator = comparator;
		this.maxEntries = maxEntries;
		treeSize = 0;
		root = null;
	}

	public BinarySearchTree<K, V> add(K key, V value)
			throws TreeIsFullException {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Parameter(s) are null");
		}
		if (treeSize == maxEntries) {
			throw new TreeIsFullException("Tree is Full");
		}
		if (root == null) {
			root = new Node(key, value);
			treeSize++;
			return this;
		} else if (addAux(key, value, root)) {
			treeSize++;
			return this;
		} else {
			return this;
		}
	}

	private boolean addAux(K key, V value, Node rootAux) {
		int comparison = comparator.compare(key, rootAux.key);

		if (comparison == 0) {
			rootAux.value = value;
			return false;
		} else if (comparison < 0) {
			if (rootAux.left == null) {
				rootAux.left = new Node(key, value);
				return true;
			} else {
				return addAux(key, value, rootAux.left);
			}
		} else {
			if (rootAux.right == null) {
				rootAux.right = new Node(key, value);
				return true;
			} else {
				return addAux(key, value, rootAux.right);
			}
		}
	}

	public String toString() {
		return toStringAux(root);
	}

	private String toStringAux(Node rootAux) {
		if (isEmpty()) {
			return "EMPTY TREE";
		} else if (rootAux == null) {
			return "";
		} else {
			return (rootAux == null) ? "EMPTY TREE"
					: toStringAux(rootAux.left) + "{" + rootAux.key + ":"
							+ rootAux.value + "}" + toStringAux(rootAux.right);
		}
	}

	/* Provided */
	public boolean isEmpty() {
		return root == null;
	}

	/* Provided */
	public int size() {
		return treeSize;
	}

	/* Provided */
	public boolean isFull() {
		return treeSize == maxEntries;
	}

	public KeyValuePair<K, V> getMinimumKeyValue() throws TreeIsEmptyException {
		return minAux(root);
	}

	private KeyValuePair<K, V> minAux(Node rootAux)
			throws TreeIsEmptyException {
		if (rootAux == null) {
			throw new TreeIsEmptyException("Empty");
		} else {
			if (rootAux.left != null) {
				return minAux(rootAux.left);
			} else {
				return new KeyValuePair<K, V>(rootAux.key, rootAux.value);
			}
		}
	}

	public KeyValuePair<K, V> getMaximumKeyValue() throws TreeIsEmptyException {
		return maxAux(root);
	}

	private KeyValuePair<K, V> maxAux(Node rootAux)
			throws TreeIsEmptyException {
		if (rootAux == null) {
			throw new TreeIsEmptyException("Empty");
		} else {
			if (rootAux.right != null) {
				return maxAux(rootAux.right);
			} else {
				return new KeyValuePair<K, V>(rootAux.key, rootAux.value);
			}
		}
	}

	public KeyValuePair<K, V> find(K key) {
		return searchAux(key, root);

	}

	public boolean findAux(K key, Node rootAux) {
		if (rootAux == null) {
			return false;
		} else {
			int comparison = comparator.compare(key, rootAux.key);
			if (comparison == 0) {
				return true;
			} else if (comparison < 0) {
				return findAux(key, rootAux.left);
			} else {
				return findAux(key, rootAux.right);
			}
		}
	}

	public BinarySearchTree<K, V> delete(K key) throws TreeIsEmptyException {
		if (isEmpty())
			throw new TreeIsEmptyException("Invalid");
		if (key == null || search(key) == null) {
			throw new IllegalArgumentException("Invalid");
		}
		if (root.key.equals(key)) {
			/* If the root is the only thing in list remove the root */
			if (root.left == null && root.right == null) {
				root = null;
				treeSize--;
				return this;
			}
			/* Only happens if there is no left subtree */
			if (root.left == null) {
				root = root.right;
				treeSize--;
				return this;
			}
			/* Only happens if there is no right subtree */
			if (root.right == null) {
				root = root.left;
				treeSize--;
				return this;
			}
			if (root.left.right == null) {
				Node right = root.right;
				root = root.left;
				root.right = right;
				treeSize--;
				return this;
			}
		}
		Node parentCurr = findParent(key);
		Node childCurr = findNode(key);
		/* If it has no children */
		if (childCurr.left == null && childCurr.right == null) {
			if (parentCurr.left == childCurr) {
				parentCurr.left = null;
				treeSize--;
				return this;
			} else {
				parentCurr.right = null;
				treeSize--;
				return this;
			}
		}
		/* If it has one child */
		if (childCurr.left == null || childCurr.right == null) {
			Node next;
			if (childCurr.left == null) {
				next = childCurr.right;
			} else {
				next = childCurr.left;
			}
			if (parentCurr.left == childCurr) {
				parentCurr.left = next;
				treeSize--;
				return this;
			} else {
				parentCurr.right = next;
				treeSize--;
				return this;
			}
		}
		Node remove = findNode(key);
		KeyValuePair<K, V> tree = maxAux(remove.left);
		Node childTwo = findNode(tree.getKey());
		Node parentTwo = findParent(tree.getKey());
		remove.key = tree.getKey();
		remove.value = tree.getValue();
		delete(childTwo.key);
		return this;
	}

	private Node findParent(K key) {
		return findParentAux(key, root);
	}

	private Node findParentAux(K key, Node rootAux) {
		if (rootAux == null) {
			return null;
		}
		if (rootAux.left != null && rootAux.left.key.equals(key)) {
			return rootAux;
		} else if (rootAux.right != null && rootAux.right.key.equals(key)) {
			return rootAux;
		}
		if (comparator.compare(rootAux.key, key) < 0) {
			return findParentAux(key, rootAux.right);
		}
		if (comparator.compare(rootAux.key, key) > 0) {
			return findParentAux(key, rootAux.left);
		}
		return null;
	}

	private Node findNode(K key) {
		return findNodeAux(key, root);

	}

	private Node findNodeAux(K key, Node rootAux) {
		if (rootAux == null) {
			return null;
		} else {
			int comparison = comparator.compare(key, rootAux.key);
			if (comparison == 0) {
				return rootAux;
			} else if (comparison < 0) {
				return findNodeAux(key, rootAux.left);
			} else {
				return findNodeAux(key, rootAux.right);
			}
		}
	}

	public void processInorder(Callback<K, V> callback) {
		if (callback == null)
			throw new IllegalArgumentException("Invalid");
		processInOrderAux(callback, root);
	}

	private void processInOrderAux(Callback<K, V> callback, Node rootAux) {
		if (rootAux == null) {
			return;
		} else {
			processInOrderAux(callback, rootAux.left);
			callback.process(rootAux.key, rootAux.value);
			processInOrderAux(callback, rootAux.right);
		}
	}

	public BinarySearchTree<K, V> subTree(K lowerLimit, K upperLimit) {
		if (lowerLimit == null || upperLimit == null
				|| comparator.compare(lowerLimit, upperLimit) > 0) {
			throw new IllegalArgumentException("Illegal");
		}
		BinarySearchTree<K, V> tree = new BinarySearchTree<K, V>(comparator,
				maxEntries);
		subtreeAux(root, lowerLimit, upperLimit, tree);
		return tree;

	}

	private void subtreeAux(Node rootAux, K lowerLimit, K upperLimit,
			BinarySearchTree<K, V> tree) {
		if (rootAux == null) {
			return;
		} else if (comparator.compare(rootAux.key, lowerLimit) < 0) {
			subtreeAux(rootAux.right, lowerLimit, upperLimit, tree);
			return;
		} else if (comparator.compare(rootAux.key, upperLimit) > 0) {
			subtreeAux(rootAux.left, lowerLimit, upperLimit, tree);
			return;
		} else {
			try {
				tree.add(rootAux.key, rootAux.value);
			} catch (TreeIsFullException e) {
				e.printStackTrace();
			}
			subtreeAux(rootAux.right, lowerLimit, upperLimit, tree);
			subtreeAux(rootAux.left, lowerLimit, upperLimit, tree);
		}
	}

	public TreeSet<V> getLeavesValues() {
		TreeSet<V> leaves = new TreeSet<V>();
		leavesAux(root, leaves);
		return leaves;
	}

	private void leavesAux(Node rootAux, TreeSet<V> set) {
		if (rootAux == null) {
			return;
		} else if (rootAux.left == null && rootAux.right == null) {
			set.add(rootAux.value);
		} else {
			leavesAux(rootAux.left, set);
			leavesAux(rootAux.right, set);
		}
	}

	private KeyValuePair<K, V> search(K key) {
		return searchAux(key, root);
	}

	private KeyValuePair<K, V> searchAux(K key, Node rootAux) {
		if (rootAux == null) {
			return null;
		} else {
			int comparison = comparator.compare(key, rootAux.key);
			if (comparison == 0) {
				KeyValuePair<K, V> result = new KeyValuePair<K, V>(key,
						rootAux.value);
				return result;
			} else if (comparison < 0) {
				return searchAux(key, rootAux.left);
			} else {
				return searchAux(key, rootAux.right);
			}
		}
	}
}
