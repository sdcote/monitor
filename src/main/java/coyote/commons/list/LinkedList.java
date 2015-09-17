/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.list;

/**
 * A synchronized linked list.
 *
 * <p>This class can be over a 1000 time faster than equivalent Sun class when 
 * deleting nodes as this class allows you to remove the node through the node 
 * entry where the Sun classes searches the entire list for an object reference 
 * before it deletes the node that contains the reference.</p>
 * 
 * <p>This class exposes the ListNodes themselves allowing for a lower level of
 * manipulation. Having a reference to the node allows it to be removed in a 
 * constant time - rather than having to search for it first. This ends up 
 * being critical when removing elements from a cache-type application.</p>
 *
 * <p>The performance of this linked list as compared with the equivalent Sun
 * class is roughly 17 list operations per millisecond slower. The performance
 * difference is most probably due to the synchronization of the list.</p>
 *
 * <p>Overall performance of add/remove operations on the two classes show the
 * unsynchronized Sun classes allowing about 330 operations per millisecond and
 * this class allowing 312. All performance metrics are based upon the cached
 * class references and do NOT include the class-loading or list initialization
 * times. This is because initialization of the Sun classes seem to take quite
 * a bit longer (200 plus milliseconds) when first called.</p>
 * 
 * <p>Other benchmarking tests show when the last element of a list needs to be 
 * removed from the list, this class has a consistant time of .001 milliseconds 
 * regardless of list size, where the Sun classes vary depending upon the size 
 * of the list. When removing all the last elements from a list of 10,000 
 * entries, the Sun LinkedList average 1.3359 milliseconds per removal where 
 * this class averaged 0.001 milliseconds per removal. When the list sized was 
 * increased to 50,000 the average time for the Sun classes was 7.085 ms and 
 * this class remained at 0.001 ms.</p>
 * 
 * <p><strong>NOTE:</strong> This performance difference can be eliminated by 
 * using the node reference to remove the node. This is because the entire list 
 * does not have to be searched to remove the node as in the equivilant Sun 
 * classes.</p>
 * 
 * <p>Use this class over the Sun class when a synchronized list with blocking 
 * operations is desired or when you expect to be removing elements from the 
 * often. In the latter case, work with the ListNodes directly for fast element 
 * removal.</p>
 *
 * @version $Revision: 1.6 $
 */
public final class LinkedList {

  /** The reference to the first node in the list */
  protected ListNode first;

  /** The reference to the last node in the list */
  protected ListNode last;

  /** Track the current size so we don't have to count all the entries when asked */
  protected int size = 0;




  /**
   * Create an empty linked list.
   */
  public LinkedList() {}




  /**
   * @return True if the list is empty, false otherwise.
   */
  public synchronized boolean isEmpty() {
    return first == null;
  }




  /**
   * Add a node to the end of the list.
   *
   * @param node the node to add.
   */
  public synchronized void addNode( ListNode node ) {
    node.remove();

    node.list = this;
    node.prev = last;

    if ( first == null ) {
      first = node;
    } else {
      last.next = node;
    }

    last = node;

    size++;
    notify();
  }




  /**
   * Remove the given ListNode from the list.
   *
   * @param node The node to remove.
   */
  public synchronized void removeNode( ListNode node ) {
    if ( node.prev == null ) {
      first = node.next;
    } else {
      node.prev.next = node.next;
    }

    if ( node.next == null ) {
      last = node.prev;
    } else {
      node.next.prev = node.prev;
    }

    node.list = null;

    size--;
  }




  /**
   * Replace one ListNode with another.
   *
   * @param oldnode The node to replace.
   * @param newnode The new node to place in the list.
   */
  public synchronized void replaceNode( ListNode oldnode, ListNode newnode ) {
    newnode.remove();

    newnode.list = this;

    if ( oldnode.prev == null ) {
      first = newnode;
    } else {
      oldnode.prev.next = newnode;
    }

    if ( oldnode.next == null ) {
      last = newnode;
    } else {
      oldnode.next.prev = newnode;
    }

    newnode.prev = oldnode.prev;
    newnode.next = oldnode.next;
  }




  /**
   * Add a node immediately after another node.
   *
   * @param prevnode the node after which to perform the insert.
   * @param node the node to insert.
   */
  public synchronized void addNodeAfter( ListNode prevnode, ListNode node ) {
    node.remove();

    node.list = this;
    node.next = prevnode.next;
    node.prev = prevnode;

    if ( prevnode.next == null ) {
      last = node;
    } else {
      prevnode.next.prev = node;
    }

    prevnode.next = node;

    size++;
    notify();
  }




  /**
   * Insert a node before another node.
   *
   * @param nextnode The node before which we are to insert the node
   * @param node The node to insert in the list
   */
  public synchronized void insertNode( ListNode nextnode, ListNode node ) {
    // remove the node from its currently set list, if any
    node.remove();

    // assign this list to the node
    node.list = this;

    // set the previous node reference in the node we
    node.prev = nextnode.prev;
    node.next = nextnode;

    if ( nextnode.prev == null ) {
      first = node;
    } else {
      nextnode.prev.next = node;
    }

    nextnode.prev = node;

    size++;
    notify();
  }




  /**
   * Return the first node in the list.
   *
   * @return The first node in the list, null if the list is empty.
   */
  public synchronized ListNode getFirstNode() {
    return first;
  }




  /**
   * Returns the first element in this list.
   * 
   * @return the first element in this list.
   */
  public synchronized Object getFirst() {
    return first.getObject();
  }




  /**
   * Return the last node in the list.
   *
   * @return The last node in the list, null if the list is empty.
   */
  public synchronized ListNode getLastNode() {
    return last;
  }




  /**
   * Returns the last element in this list.
   * 
   * @return the last element in this list.
   */
  public synchronized Object getLastst() {
    return last.getObject();
  }




  /**
   * @return the number of nodes in the list
   */
  public synchronized int size() {
    return size;
  }




  /**
   * Clear out the list, removing all the references to the other nodes.
   */
  public synchronized void clear() {
    // Go through each node in the list and remove it, otherwise the GC will
    // not remove the nodes since they maintain references to their neighbors
    // and to the list itself.
    ListEnumeration en = new ListEnumeration( this );
    for ( ListNode listnode = null; ( listnode = en.nextNode() ) != null; listnode.remove() );

    first = null;
    last = null;
    size = 0;
    notifyAll();
  }




  /**
   * Return an enumeration of all the nodes in the list.
   *
   * @return The enumerator from beginning to end.
   */
  public synchronized ListEnumeration elements() {
    return new ListEnumeration( this );
  }




  /**
   * Return and remove the first ListNode from the list
   *
   * @return The first ListNode in the list, or null if the list is empty
   */
  public synchronized ListNode popNode() {
    ListNode retval = getFirstNode();
    if ( retval != null ) {
      retval.remove();

      size--;
    }

    return retval;
  }




  /**
   * Add a ListNode to the beginning of the list.
   *
   * @param node The ListNode to add
   */
  public synchronized void pushNode( ListNode node ) {
    if ( node != null ) {
      addNode( node );
    }
  }




  /**
   * Add this object to the end of the list
   *
   * @param obj The object reference to place in the last position of the list.
   *        Null values are allowed.
   */
  public synchronized void add( Object obj ) {
    ListNode node = new ListNode( obj );

    node.list = this;
    node.prev = last;

    if ( first == null ) {
      first = node;
    } else {
      last.next = node;
    }

    last = node;

    size++;
    notify();
  }




  /**
   * Add this object to the front of the list.
   *
   * @param obj The object reference to place in the first position of the 
   *        list. Null values are allowed.
   */
  public synchronized void addFirst( Object obj ) {
    ListNode node = new ListNode( obj );

    node.list = this;

    if ( first == null ) {
      first = node;
    } else {
      first.prev = node;
      node.next = first;
    }

    first = node;

    size++;
    notify();
  }




  /**
   * Remove the first list node and return the object reference stored there.
   *
   * @return The first object reference in the list, or null if the list is
   *         empty or null if the first object reference has a null value.
   */
  public synchronized Object removeFirst() {
    if ( first != null ) {
      ListNode node = first;
      node.remove();

      return node.object;
    }

    return null;
  }




  /**
   * Performs a blocking retrieval operation on the first list element.
   * 
   * <p>Block only for time-out if there are no entries to retrieve.</p>
   *
   * @param millis the time to wait for an object in milliseconds.
   *
   * @return The next object in the list, or null if timed-out.
   *
   * @throws InterruptedException
   */
  public synchronized Object removeFirst( long millis ) throws InterruptedException {
    if ( size == 0 ) {
      this.wait( millis );
    }

    if ( size == 0 ) {
      return null;
    }

    return removeFirst();
  }




  /**
   * Remove the last list node and return the object reference stored there.
   *
   * @return The last object reference in the list or null if the list is empty
   *         or the null if the last object reference has a null value.
   */
  public synchronized Object removeLast() {
    if ( last != null ) {
      ListNode node = last;
      node.remove();

      return node.object;
    }

    return null;
  }




  /**
   * Performs a blocking retrieval operation on the last list element.
   * 
   * <p>Block only for time-out if there are no entries to retrieve.</p>
   *
   * @param millis the time to wait for an object in milliseconds.
   *
   * @return The next object in the list, or null if timed-out.
   *
   * @throws InterruptedException
   */
  public synchronized Object removeLast( long millis ) throws InterruptedException {
    if ( size == 0 ) {
      this.wait( millis );
    }

    if ( size == 0 ) {
      return null;
    }

    return removeLast();
  }




  /**
   * Return and remove the first object from the top of the list.
   * 
   * <p>Simply a synonym for <code>removeFirst()</code>.</p>
   *
   * @return The first object in the list, or null if the list is empty or the
   *         first object is null.
   */
  public synchronized Object pop() {
    return removeFirst();
  }




  /**
   * Add an object to the beginning of the list.
   *
   * <p>Simply a synonym for <code>addFirst(Object)</code>.</p>
   *
   * @param object
   */
  public synchronized void push( Object obj ) {
    addFirst( obj );
  }

}
