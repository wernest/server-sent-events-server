package org.glassfish.jersey.examples.sse;

import java.util.*;

/**
 * Created by wernest on 4/21/2016.
 */
public class MessageManager {

  private static MessageManager instance;
  private final Map<String, List<Object>> listMap = new HashMap<>();

  MessageManager(){}

  public static MessageManager getInstance(){
    if(instance == null) {
      instance = new MessageManager();
    }
    return instance;
  }

  public void addList(String subscriptionId, List<Object> list){
    if(this.listMap.containsKey(subscriptionId)) {
      List<Object> oldList = this.listMap.get(subscriptionId);
      list.addAll(oldList);
    }
    this.listMap.put(subscriptionId, list);
  }

  public List<Object> getList(String subscriptionId){
    if(this.listMap.containsKey(subscriptionId)) {
      return this.listMap.get(subscriptionId);
    }else {
      List<Object> list = new LinkedList<>();
      this.addList(subscriptionId, list);
      return list;
    }
  }

  public void removeList(String subscriptionId){
    this.listMap.remove(subscriptionId);
  }

  public void addItem(String subscriptionId, Object object) {
    if (this.listMap.containsKey(subscriptionId)) {
      this.listMap.get(subscriptionId).add(object);
    }
  }

}
