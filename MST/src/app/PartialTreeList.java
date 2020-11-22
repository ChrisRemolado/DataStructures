package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		PartialTreeList ptl = new PartialTreeList();		
		
		for (int i = 0; i < graph.vertices.length; i++) { //for each partial tree
			
			//initialize each vertex
			Vertex currVert = graph.vertices[i];
			PartialTree pt = new PartialTree(currVert);
			
			//populate minheap with all possible arcs for each vertex
			Vertex.Neighbor currNeigh = currVert.neighbors;
			MinHeap<Arc> allArcs = new MinHeap<Arc>();
			
			while (currNeigh != null) {
				Arc currArc = new Arc(currVert, currNeigh.vertex, currNeigh.weight);
				allArcs.insert(currArc);
				
				//next element
				currNeigh = currNeigh.next;
			}
			
			//order each arc by mins in partial tree
			while (!allArcs.isEmpty()) {
				Arc currArc = allArcs.getMin();
				allArcs.deleteMin();
				
				pt.getArcs().insert(currArc);
			}
			
			//add each line
			ptl.append(pt);
			System.out.println(pt.toString());
		}
		
		return ptl;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		
		//STEP 3 (STEP 9)
		ArrayList<Arc> MST = new ArrayList<Arc>();
		
		while (ptlist.size() >= 2) {
			PartialTree pt = ptlist.remove();
			MinHeap<Arc> allArcs = pt.getArcs();
			
			//System.out.println(allArcs.toString());
			
			//STEP 4
			boolean hasAppended = false;
			while (!allArcs.isEmpty() && !hasAppended) {
				Arc a = allArcs.getMin();
				allArcs.deleteMin();
				Vertex v1 = a.getv1();
				Vertex v2 = a.getv2();
				
				//System.out.println("a: " + a.toString() + " v1: " + v1 + " v2: " + v2);
				
				//STEP 5
				PartialTree ptOther = ptlist.removeTreeContaining(v1); //loops itself
				if (ptOther == null) {
					ptOther = ptlist.removeTreeContaining(v2);
				}
				
				//TEST AT LAST
				//if (MST.size() == 3) {
					//System.out.println("Passed flag");
				//}
				
				//STEP 6
				if (ptOther != null) {
					pt.merge(ptOther);
					
					//STEP 7
					MST.add(a);
					
					//STEP 8 - add combined partial tree to the end, size--
					ptlist.append(pt);
					hasAppended = true;
					
					//System.out.println("test");
					//for (int i=0; i<MST.size(); i++)
						//System.out.println(MST.get(i));
				}
			}
			
			//boolean flag = false;
			//if (MST.size() == 3) {
			//	flag = true;
			//}
		}
		
		//for (int i=0; i<MST.size(); i++)
			//System.out.println(MST.get(i));
		
		return MST;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	
    		//BETWEEN STEP 5 & 6
    		PartialTree ptToRemove = null;
    		
    		//ERROR
    		if (this.rear == null) { //!pt.isEmpty()
    			throw new NoSuchElementException("Empty tree list.");
    		}
    		
    		//BASE CASE
    		Node nodePtr = this.rear;
    		//System.out.println(nodePtr.tree.toString());
    		boolean listRemoved = false;
    		
    		
    		do {
    			//STEP 5A - Is vertex in current partial tree?
    			PartialTree tree = nodePtr.tree;
    			boolean vertInPT = false;
    			
    			//get to root vertex
    			Vertex currVert = vertex;
    			while (currVert != currVert.parent) {
    				currVert = currVert.parent;
    			}
    			//Vertex test = tree.getRoot();
    			//if root vertex is a parent of input vertex, flag = true
    			if (currVert == tree.getRoot()) {
    				vertInPT = true;
    			}
    			
    			//STEP 5B - remove node from current partial tree
    			if (vertInPT == true) {
    				ptToRemove = tree;
    				
    				Node nodePtrAfter = nodePtr.next;
    				Node nodePtrBefore = nodePtr;
    				
    				//Go all the way around to get nodePtrBefore
    				while (nodePtrBefore.next != nodePtr) {
    					nodePtrBefore = nodePtrBefore.next;
    				}

    				if (size == 1) { //1 node
    					rear = null;
    				}
    				else if (size == 2) { //2 nodes
    					if (nodePtr == rear) {
    						rear = rear.next;
    					}
    					nodePtr.next.next = nodePtr.next;
    				}
    				else { // 3 or more nodes
    					if (nodePtr == rear) {
    						rear = nodePtrBefore;
    					}
    					nodePtrBefore.next = nodePtrAfter;
    				}
    				size--;
    				listRemoved = true;
    			}
    			
    			//Iterator
    			nodePtr = nodePtr.next;    			
    		} while (nodePtr != this.rear && !listRemoved); //because CLL
    		
    		return ptToRemove;
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}

