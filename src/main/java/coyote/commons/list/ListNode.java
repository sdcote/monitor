/*
 * $Id: ListNode.java,v 1.1 2005/03/29 14:10:35 cotes Exp $
 * 
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.list;

/**
 * Class ListNode
 *
 * @version $Revision: 1.1 $
 */
public class ListNode {

  /** Field list */
  LinkedList list;

  /** Field prev */
  ListNode prev;

  /** Field next */
  ListNode next;

  /** Field object */
  Object object;




  /**
   * Constructor ListNode
   */
  public ListNode() {}




  /**
   * Constructor ListNode
   *
   * @param obj
   */
  public ListNode( Object obj ) {
    object = obj;
  }




  /**
   * Method getObject
   *
   * @return
   */
  public Object getObject() {
    return object;
  }




  /**
   * Method getList
   *
   * @return
   */
  public LinkedList getList() {
    return list;
  }




  /**
   * Method getPrevious
   *
   * @return
   */
  public ListNode getPrevious() {
    return prev;
  }




  /**
   * Method getNext
   *
   * @return
   */
  public ListNode getNext() {
    return next;
  }




  /**
   * Method setNext
   *
   * @param listnode
   */
  public void setNext( ListNode listnode ) {
    list.addNodeAfter( this, listnode );
  }




  /**
   * Method setPrevious
   *
   * @param listnode
   */
  public void setPrevious( ListNode listnode ) {
    list.insertNode( this, listnode );
  }




  /**
   * Method remove
   *
   * @return
   */
  public boolean remove() {
    if ( list != null ) {
      list.removeNode( this );

      return true;
    } else {
      return false;
    }
  }
}