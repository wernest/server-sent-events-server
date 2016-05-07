/*
MIT License

Copyright (c) 2016 William Ernest

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.wernest.sse;

import java.util.*;

/**
 *  @author wernest
 */
public class MessageManager {

  private static MessageManager instance;

  // Map of subscribers to ClientSubscription (Thread, Objects, Subscriptions)
  private final Map<String, ClientSubscription> clientsToObjectsMap = new HashMap<>();
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

  public void addList(String subscriptionId, Thread thread, List<Object> list) {
    ClientSubscription clientSubscription;
    if (this.clientsToObjectsMap.containsKey(subscriptionId)) {
      clientSubscription = this.clientsToObjectsMap.get(subscriptionId);
      clientSubscription.addList(list);
    } else {
      clientSubscription = new ClientSubscription(thread, list);
    }
    this.clientsToObjectsMap.put(subscriptionId, clientSubscription);
  }

//  public ClientSubscription getList(String subscriptionId, Thread thread) {
//    if (this.clientsToObjectsMap.containsKey(subscriptionId)) {
//      return this.clientsToObjectsMap.get(subscriptionId);
//    } else {
//      this.addList(subscriptionId, thread, new LinkedList<Object>());
//      return this.clientsToObjectsMap.get(subscriptionId);
//    }
//  }

  public void removeList(String subscriptionId) {
    this.clientsToObjectsMap.remove(subscriptionId);
  }

  public void addObject(String className, Object object) {
    if (!objectsSubscriptionMap.containsKey(className)) {
      objectsSubscriptionMap.put(className, new LinkedList<>());
    }

    final List<String> subscribedClients = objectsSubscriptionMap.get(className);
    // If no subscribers, no sense in keeping object in map
    if (subscribedClients.isEmpty()) {
      return;
    }

    for (String subscribedClient : subscribedClients) {
      this.clientsToObjectsMap.get(subscribedClient).sendItem(object); // Add reference from subscriber to specific object
    }
  }

  public void addObjectSubscription(String subscriptionId, String className) {
    if(clientsToObjectsMap.containsKey(subscriptionId)) {
      ClientSubscription clientSubscription = clientsToObjectsMap.get(subscriptionId);
      clientSubscription.subscribeObject(className);
    }

    List<String> list;
    if(objectsSubscriptionMap.containsKey(className)) {
      list = objectsSubscriptionMap.get(className);
    }else{
      list = new LinkedList<>();
      objectsSubscriptionMap.put(className, list);
    }

    list.add(subscriptionId);
  }

  public void removeObjectSubscription(String subscriptionId, String className) {
    if(clientsToObjectsMap.containsKey(subscriptionId)) {
      ClientSubscription clientSubscription = clientsToObjectsMap.get(subscriptionId);
      clientSubscription.unSubscribeObject(className);
    }

    if(objectsSubscriptionMap.containsKey(className)) {
      List<String> list;
      list = objectsSubscriptionMap.get(className);
      list.remove(subscriptionId);
    }
  }

}
