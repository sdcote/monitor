/*
 * $Id: ListEnumeration.java,v 1.1 2005/03/29 14:10:36 cotes Exp $
 * 
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.list;

import java.util.Enumeration;


/**
 * Class ListEnumeration
 *
 * @version $Revision: 1.1 $
 */
public class ListEnumeration implements Enumeration {

  private static final ListNode UNINITIALIZED = new ListNode();
  private LinkedList list;
  private ListNode node;




  /**
   * Constructor ListEnumeration
   *
   * @param linkedlist
   */
  protected ListEnumeration( LinkedList linkedlist ) {
    node = UNINITIALIZED;
    list = linkedlist;
  }




  /**
   * Method currentNode
   *
   * @return
   */
  public ListNode currentNode() {
    return node;
  }




  /**
   * Method firstNode
   *
   * @return
   */
  public ListNode firstNode() {
    return list.first;
  }




  /**
   * Method lastNode
   *
   * @return
   */
  public ListNode lastNode() {
    return list.last;
  }




  /**
   * Method nextNode
   *
   * @return
   */
  public ListNode nextNode() {
    if ( node == null ) {
      return null;
    }

    ListNode listnode;
    synchronized( list ) {
      for ( node = ( node != UNINITIALIZED ) ? node.next : list.first; ( node != null ) && ( node.list == null ); node = node.next );

      listnode = node;
    }

    return listnode;
  }




  /**
   * Return the next object value in this list.
   *
   * <p>This is the user object stored in this list, not the ListNode 
   * reference.</p>
   *
   * @return the next object in the list.
   */
  public Object nextElement() {
    ListNode listnode = nextNode();
    return ( listnode != null ) ? listnode.object : null;
  }




  /**
   * Returns whether or not there is another element in the list.
   *
   * @return
   */
  public boolean hasMoreElements() {
    if ( node == null ) {
      return false;
    }

    boolean flag;

    synchronized( list ) {
      ListNode listnode;
      for ( listnode = ( node != UNINITIALIZED ) ? node.next : list.first; ( listnode != null ) && ( listnode.list == null ); listnode = listnode.next );

      flag = listnode != null;
    }

    return flag;
  }

}
