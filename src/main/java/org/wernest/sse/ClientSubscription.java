package org.wernest.sse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wernest on 5/6/2016.
 */
public class ClientSubscription {

  private List<Object> sendList;
  private final List<String> subscribedObjectsList = new LinkedList<>();
  private final Thread thread;

  ClientSubscription(Thread thread, List<Object> list){
    this.thread = thread;
    this.sendList = list;
  }

  /**
   * Send an item to this client
   * @param object to send
   */
  public void sendItem(Object object){
    if(!this.sendList.contains(object)){
      this.sendList.add(object);
    }
    runThread();
  }

  /**
   * Subscribe to an object by class name.
   * @param className of object
   */
  public void subscribeObject(String className) {
    if (!this.subscribedObjectsList.contains(className)) {
      this.subscribedObjectsList.add(className);
    }
  }

  /**
   * Unsubscribe from an object by class name.
   * @param className
   */
  public void unSubscribeObject(String className){
    if(this.subscribedObjectsList.contains(className)){
      this.subscribedObjectsList.remove(className);
    }
  }

  /**
   * If the client gets disconnected and reconnects, with a new SSE thread
   * we add the new list to the old object.
   * @param newList
   */
  public void addList(List<Object> newList){
    newList.addAll(this.sendList);
    this.sendList = newList;
    runThread();
  }

  private void runThread(){
    if(!this.thread.isAlive()) {
      this.thread.run();
    }
  }

}
