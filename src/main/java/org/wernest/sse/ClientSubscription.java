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
