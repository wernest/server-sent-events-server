package org.glassfish.jersey.examples.sse;

import java.util.*;

/**
 * Created by wernest on 4/21/2016.
 */
public class MessageManager {

  private static MessageManager instance;
  private Integer counter = 0;
  // Map of subscribers to object ids
  private final Map<String, List<Object>> clientsToObjectsMap = new HashMap<>();
  //Map of object types to subscribers ids
  private final Map<String, List<String>> objectsSubscriptionMap = new HashMap<>();

  MessageManager() {
  }

  public static MessageManager getInstance() {
    if (instance == null) {
      instance = new MessageManager();
    }
    return instance;
  }

  public void addList(String subscriptionId, List<Object> list) {
    if (this.clientsToObjectsMap.containsKey(subscriptionId)) {
      List<Object> oldList = this.clientsToObjectsMap.get(subscriptionId);
      list.addAll(oldList);
    }
    this.clientsToObjectsMap.put(subscriptionId, list);
  }

  public List<Object> getList(String subscriptionId) {
    if (this.clientsToObjectsMap.containsKey(subscriptionId)) {
      return this.clientsToObjectsMap.get(subscriptionId);
    } else {
      List<Object> list = new LinkedList<>();
      this.addList(subscriptionId, list);
      return list;
    }
  }

  public void removeList(String subscriptionId) {
    this.clientsToObjectsMap.remove(subscriptionId);
  }

  public void addObject(String className, Object object) {
    if (!objectsSubscriptionMap.containsKey(className)) {
      objectsSubscriptionMap.put(className, new LinkedList<>());
    }

    List<String> subscribedClients = objectsSubscriptionMap.get(className);
    // If no subscribers, no sense in keeping object in map
    if (subscribedClients.isEmpty()) {
      return;
    }

    for (String subscribedClient : subscribedClients) {
      this.clientsToObjectsMap.get(subscribedClient).add(object); // Add reference from subscriber to specific object
    }
  }

  public void addSubscription(String subscriptionId, String className) {
    if (!this.objectsSubscriptionMap.containsKey(className)) {
      this.objectsSubscriptionMap.put(className, new LinkedList<>());
    }

    if (!this.objectsSubscriptionMap.get(className).contains(subscriptionId)) {
      this.objectsSubscriptionMap.get(className).add(subscriptionId);
    }
  }

  public void removeSubscription(String subscriptionId, String className) {
    if (!this.objectsSubscriptionMap.containsKey(className)) {
      return;
    }

    if (this.objectsSubscriptionMap.get(className).contains(subscriptionId)) {
      this.objectsSubscriptionMap.get(className).remove(subscriptionId);
    }
  }

}
