package org.glassfish.jersey.examples.sse;

import java.util.*;

/**
 * Created by wernest on 4/21/2016.
 */
public class MessageManager {

  private static MessageManager instance;
  private final Map<String, List<Object>> clientsToObjectsMap = new HashMap<>();
  private final Map<String, List<String>> subscribedObjectsMap = new HashMap<>();

  MessageManager(){}

  public static MessageManager getInstance(){
    if(instance == null) {
      instance = new MessageManager();
    }
    return instance;
  }

  public void addList(String subscriptionId, List<Object> list){
    if(this.clientsToObjectsMap.containsKey(subscriptionId)) {
      List<Object> oldList = this.clientsToObjectsMap.get(subscriptionId);
      list.addAll(oldList);
    }
    this.clientsToObjectsMap.put(subscriptionId, list);
  }

  public List<Object> getList(String subscriptionId){
    if(this.clientsToObjectsMap.containsKey(subscriptionId)) {
      return this.clientsToObjectsMap.get(subscriptionId);
    }else {
      List<Object> list = new LinkedList<>();
      this.addList(subscriptionId, list);
      return list;
    }
  }

  public void removeList(String subscriptionId){
    this.clientsToObjectsMap.remove(subscriptionId);

  }

  public void addItem(String className, Object object) {
    if(!subscribedObjectsMap.containsKey(className)) {
      subscribedObjectsMap.put(className, new LinkedList<>());
    }

    List<String> subscribedClients = subscribedObjectsMap.get(className);
    for (String subscribedClient : subscribedClients) {
      this.clientsToObjectsMap.get(subscribedClient).add(object);
    }
  }

  public void addSubscription(String subscriptionId, String name){
    if(!this.subscribedObjectsMap.containsKey(name)) {
      this.subscribedObjectsMap.put(name, new LinkedList<>());
    }

    if (!this.subscribedObjectsMap.get(name).contains(subscriptionId)){
      this.subscribedObjectsMap.get(name).add(subscriptionId);
    }
  }

  public void removeSubscription(String subscriptionId, String name) {

  }
  }
