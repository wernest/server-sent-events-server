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

SSE Example using Jersey
===================================

The Jersey example for SSE doesn't show you a solid way of handling multiple clients and persistent connections. 
This implementation handles a few cases I found myself in need of. 

Specifically I needed the following features 
..* All REST communication to go through the SSE channel. 
..* All REST events immediately return a response, while output will come via the SSE channel. 
..* Mapping subscribers to subscriptions for objects. 
..* Basically.... I needed webservices without webservices.

You can read the details here
[Server-Sent Events](https://jersey.java.net/documentation/latest/sse.html).



Example
--------
To subscribe as a client you would send a get request to `sse/updates`. This will return a 
subscriber ID value to open a channel to communicate with. At that point, send a get request to 
`/sse/updates/{subscriberId}` and you will get a SSE event. 

The example contains a jQuery page that will instantiate a new `SampleObject` and send it to the 
server. All others who are subscribed to that class will receive it. 

Running the Example
-------------------

Consuming of Jersey events is included in the code for a web browser, and another project called 
sse-client will demonstrate how to consume the events using a Java client. 

Run the example as follows:

>     mvn clean compile exec:java