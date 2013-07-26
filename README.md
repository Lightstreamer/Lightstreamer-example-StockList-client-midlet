
# Lightstreamer StockList Demo Client for Java ME #

This project contains a full example of a Midlet Java application that employs the Lightstreamer JAVA ME client library.

## Stock-List + Round-Trip Demo ##

<table>
  <tr>
    <td style="text-align: left">
      &nbsp;<a href="http://demos.lightstreamer.com/midlet/lsmidlet_midp.jad" target="_blank"><img src="http://www.lightstreamer.com/img/demo/screen_stockrtmidlet.png"></a>&nbsp;
      
    </td>
    <td>
      &nbsp;Click here to download and install the application:<br>
      &nbsp;<a href="http://demos.lightstreamer.com/midlet/lsmidlet_midp.jad" target="_blank">http://demos.lightstreamer.com/midlet/lsmidlet_midp.jad</a>
    </td>
  </tr>
</table>

This Java midlet contains two different screens: one with three stock quotes from the [Stock-List Demos](https://github.com/Weswit/Lightstreamer-example-Stocklist-client-javascript), the other with four items from the [Round-Trip Demo](https://github.com/Weswit/Lightstreamer-example-RoundTrip-client-javascript).
This app uses the <b>Java ME Client API for Lightstreamer</b> to handle the communications with Lightstreamer Server.<br>

You will see the yellow flashing cells of the Stock-List Demos. Use the Option menu to switch to the other demo, where you can see the values of some items of the Round-Trip Demo updated in real-time. To publish new values, use the Option menu.

You can open the [Round-Trip Demo](https://github.com/Weswit/Lightstreamer-example-RoundTrip-client-javascript) on a PC browser and see the values synchronized with the mobile phone.

# Build #

If you want to skip the build and deploy processes of this demo please note that you can click the image or link above to download a MIDP 2.0 ".jad" file to your phone. As an alternative, point your mobile browser to the URL, to choose between .jad and .jar: http://demos.lightstreamer.com/midlet/ .
Once installed on your phone, run the midlet, allow the network connections, choose the access point id prompted, and wait for the connection to be established (as shown in the top green bar).<br>

Otherwise, if you want to procede with the compilation of own version of this demo please consider that this example is comprised of the following folders:
* /src<br>
  Contains the sources to build the java application.

* /res<br>
  Contains the images needed to the demo.
  
* /lib<br>
  Drop here the ls-j2me-client.jar from the Lighstreamer SDK for Java ME Clients, to be used for the build process and execution.

# Deploy #
  
By the current configuration, specified in "LightstreamerDemoMidlet.java" and used by the provided precompiled files, the demo tries to connect to the demo server currently running on Lightstreamer website.
The demo can be reconfigured and recompiled in order to connect to the local installation of Lightstreamer Server. Only the host name and the port number have to be changed in the source code.
The example requires that the [QUOTE_ADAPTER](https://github.com/Weswit/Lightstreamer-example-Stocklist-adapter-java), [ROUNDTRIP_ADAPTER]() and [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) have to be deployed in your local Lightstreamer server instance. 
The factory configuration of Lightstreamer server already provides this adapter deployed.<br>

# See Also #

## Lightstreamer Adapters needed by this demo client ##

* [Lightstreamer StockList Demo Adapter](https://github.com/Weswit/Lightstreamer-example-Stocklist-adapter-java)
* [Lightstreamer Reusable Metadata Adapter in Java](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* To be add: [Lightstreamer RoundTrip Demo Adapter]()

## Similar demo clients that may interest you ##

* [Lightstreamer StockList Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-Stocklist-client-javascript)
* [Lightstreamer StockList Demo Client for jQuery](https://github.com/Weswit/Lightstreamer-example-StockList-client-jquery)
* [Lightstreamer StockList Demo Client for Dojo](https://github.com/Weswit/Lightstreamer-example-StockList-client-dojo)
* [Lightstreamer StockList Demo Client for Java .NET](https://github.com/Weswit/Lightstreamer-example-StockList-client-dotnet)
* [Lightstreamer Round-Trip Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-RoundTrip-client-javascript)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer Java Client API v. 3.2.1 or newer.