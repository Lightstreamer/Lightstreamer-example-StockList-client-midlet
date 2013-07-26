/*
 * Copyright 2013 Weswit Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javamedemo.midlet;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.lightstreamer.javameclient.midp.ConnectionInfo;
import com.lightstreamer.javameclient.midp.ConnectionListener;
import com.lightstreamer.javameclient.midp.ConnectionPolicy;
import com.lightstreamer.javameclient.midp.ConnectionProvider;
import com.lightstreamer.javameclient.midp.LSClient;
import com.lightstreamer.javameclient.midp.SimpleTableInfo;
import com.lightstreamer.javameclient.midp.SubscribedTableKey;

/**
 * Class LightstreamerDemoMidlet.
 */
public class LightstreamerDemoMidlet extends MIDlet implements CommandListener {

    private Display display;
    private LSClient lsClient;

    private LSDisplayListener dis;
    private Command exit;
    private Command exit2;
    private Command switchDemo;
    private Command showErrors;
    private Command showForm;
    private boolean isStockList = true;
    private boolean goOn = true;

    private SubscribedTableKey stockKey = null;
    private SubscribedTableKey roundtripKey = null;

    private String dct;
    private String dcm;
    private String usm;
    private String tts;
    private boolean isSingleConnection = true;
    private boolean tryToSaveMem = true;

    private ConnectionPolicy policy = new ConnectionPolicy();

    private ConnectionInfo pushLightstreamerCom =
        new ConnectionInfo("push.lightstreamer.com");
    
    LSDisplayListenerStockList stockDisplay;
    LSDisplayListenerRoundTrip roundtripDisplay;
    ConnectionListenerDispatcher listener = new ConnectionListenerDispatcher();

    static public ErrorPrompt errorPrompt;
    SubmitForm sendForm;

    /**
     * Constructor LightstreamerDemoMidlet.
     */
    public LightstreamerDemoMidlet() {   
        super();

        //read from the jad (if possible) the connection type
        //if "polling" we will use LSClient in Smart polling mode.
        dct = getAppProperty("Demo-Connection-Type");
        //dct = "polling";
        
        //read from the jad (if possible) the connectionm mode
        //if "multiple" we will use multiple connection mode
        dcm = getAppProperty("Demo-Connection-Mode");
        //dcm = "single";
        
        //read from the jad (if possible) whenever to use or not
        //SocketConnection to open the connection to the server.
        //if false HttpConnection will be used instead
        usm = getAppProperty("Demo-Socket-Mode");
        //usm = "false";
        
        //read from the jad (if possible) the approach to take
        //with memory and CPU
        tts = getAppProperty("Try-To-Save");
        //tts = "CPU";

        //if we will use (or fall down to) smart polling mode
        //we will use a 0-interval between connections
        policy.setPollingInterval(0);

        //initiate the LSClient
        lsClient = new LSClient();
        //Always the same object instance will be passed to update events
        lsClient.useReusableItemUpdates(true);
        
        //CPU or Memory, default memory
        tryToSaveMem = (tts == null) || !tts.equals("CPU");
            //if CPU we will create two canvas immediately an keep them forever
            //if Memory each time the application is switched we will create a new Canvas to print to
        
        //The isSingleConnection flag indicates if the client will open as many connection as needed
        //or will open only one connection per-time (in this case some advanced features are disabled)
        isSingleConnection = dcm != null && dcm.equals("single") || lsClient.isUsingSingleConnection();
        lsClient.useSingleConnection(isSingleConnection);
        
        //defaults to socket connections
        boolean useSocketMode = usm == null || !usm.equals("false");
        lsClient.useSocketConnection(useSocketMode);
      
        exit = new Command("Exit", Command.STOP, 1);
        switchDemo = new Command("Switch Demo", Command.OK, 1);
        showErrors = new Command("Show Error Console", Command.OK, 3);
        showForm = new Command("Send A Message", Command.OK, 2);
        exit2 = new Command("Exit", Command.STOP, 4);
        display = Display.getDisplay(this);

        //additional display to show errors occurred
        errorPrompt = new ErrorPrompt(this);
        
        //additional display to send messages for the interaction demo
        sendForm = new SubmitForm(this,lsClient,errorPrompt);

        //when using MIDP 2 we can't use port 80 443 or 8080 unless we have certified 
        //the midlet so we use the 8888 port. 
        pushLightstreamerCom.setPort(8888);
        //configure the adapter set
        pushLightstreamerCom.setAdapter("DEMO");

        //set up a small internal buffer
        lsClient.setBufferMax(2);
        
        try {
            Class bbProvider = Class.forName("javamedemo.midlet.blackberry.BBConnectionProvider");
            //if we pass here it means that the BBConnectionProvider is in the classpath. Such class
            //is an example implementation of the ConnectionProvider interface. 
            //Such class have to be in the classpath only if we're building the BlackBerry version of the demo;
            //if used with the plain-and-simple J2ME environment it will give compilation error as it uses some BB
            //proprietary extensions to J2ME.
            //That is why that class is isolated in its own source folder.
            
            lsClient.setConnectionProvider((ConnectionProvider)bbProvider.newInstance());
            
        } catch (ClassNotFoundException e) {
            //BBConnectionProvider not available in the classpath, let the library use its default ConnectionProvider
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        
        //we could create both canvas here and then switch the visible one to save some CPU
        if (!tryToSaveMem) {
            stockDisplay = createStockListCanvas();
            roundtripDisplay = createRoundTripCanvas();
        } //else we could re-create the canvas each time a switch is called so that at each given we have at most
        //one "non-garbageable" Canvas in memory
        
        //launch a "repaint-daemon"
        new RepaintThread().start();
    }

    /**
     * Method startApp.
     * @throws MIDletStateChangeException
     */
    protected void startApp() throws MIDletStateChangeException {
        if (!isSingleConnection) {
            openConnection();
        }
        if (isStockList) {
            startStockListDemo(); 
            
        } else {
            startRoundTripDemo();
            
        }
    }
    
    /**
     * Method pauseApp.
     */
    protected void pauseApp() {
        if (lsClient != null) {
            lsClient.closeConnection();
            if (isStockList) {
                lsClient.unsubscribeTable(stockKey);
                stockKey = null;
            } else {
                lsClient.unsubscribeTable(roundtripKey);
                roundtripKey = null;
            }
        }
    }

    /**
     * Method destroyApp.
     *
     * @param  arg0  ...
     * @throws MIDletStateChangeException
     */
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        //stop all LSClient's threads 
        LSClient.closeApp();
        //stop the "repaint-daemon"
        goOn = false;
    }
    
    private void openConnection() {
        //open the new connection
        if ((dct != null) && dct.equals("polling")) {
            lsClient.openPollingConnection(pushLightstreamerCom, listener, policy);
        } else {
            lsClient.openConnection(pushLightstreamerCom, listener, policy);
        }
    }
    
    private void attachCommands(LSDisplayListener canvas, boolean attachShowForm) {
        canvas.addCommand(exit);
        canvas.addCommand(exit2);
        canvas.addCommand(switchDemo);
        canvas.addCommand(showErrors);
        if (attachShowForm) {
            canvas.addCommand(showForm); 
        }
        canvas.setCommandListener(this);
    }
    
    private LSDisplayListenerStockList createStockListCanvas() {
        //Create the display listener for the Stock list
        //preparing layout and attaching commands
        LSDisplayListenerStockList canvas = new LSDisplayListenerStockList("StockList Demo", errorPrompt);
        attachCommands(canvas,false);
        return canvas;
    }
    
    private void startStockListDemo() {
        if (!tryToSaveMem) {
            dis = stockDisplay;
        } else {
            dis = createStockListCanvas();
        }
        dis.init(lsClient.getStatus());
     
        if (roundtripKey != null) {
            if (isSingleConnection) {
                //make this code usable in single and multiple conection mode:
                //in single connection mode we have to close the connection, subscribe the table 
                //and then re-open the connection
                lsClient.closeConnection();
            }
            //if subscribed unsubscribe the roundtrip table.
            lsClient.unsubscribeTable(roundtripKey);
        }

        //prepare and subscribe the stocklist table
        String schema = "last_price time pct_change";
        String group = "item1 item2 item3";
        SimpleTableInfo table = new SimpleTableInfo(group, schema, "MERGE");
        table.setSnaspshotRequired(true);
        table.setRequestedMaxFrequency(1);
        table.setDataAdapter("QUOTE_ADAPTER");
        stockKey = lsClient.subscribeTable(table, dis);
        
        if (isSingleConnection) {
            openConnection();
        }
        
        //give "power" to our display
        display.setCurrent(dis);
        //make the first paint
        dis.repaint();
    }
    
    private LSDisplayListenerRoundTrip createRoundTripCanvas() {
        LSDisplayListenerRoundTrip canvas = new LSDisplayListenerRoundTrip("Round-Trip Demo", errorPrompt);
        attachCommands(canvas,!isSingleConnection);
        return canvas;
    }

    private void startRoundTripDemo() {
        if (!tryToSaveMem) {
            dis = roundtripDisplay;
        } else {        
            dis = createRoundTripCanvas();
        }
        dis.init(lsClient.getStatus());

        if (stockKey != null) {
            if (isSingleConnection) {
                lsClient.closeConnection();
            }
            lsClient.unsubscribeTable(stockKey);
        }

        String schema = "message timestamp IP";
        String group = "roundtrip0 roundtrip1 roundtrip2 roundtrip3 roundtrip4";
        SimpleTableInfo table = new SimpleTableInfo(group, schema, "MERGE");
        table.setSnaspshotRequired(true);
        table.setRequestedMaxFrequency(1);
        table.setDataAdapter("ROUNDTRIP_ADAPTER");
        roundtripKey = lsClient.subscribeTable(table, dis);

        if (isSingleConnection) {
            openConnection();
        }

        display.setCurrent(dis);
        dis.repaint();
    }

    /**
     * Method commandAction.
     *
     * @param  com  ...
     * @param  arg1  ...
     */
    public void commandAction(Command com, Displayable arg1) {
        if (com == exit || com == exit2) {
            notifyDestroyed();
        } else if (com == switchDemo) {
            if (isStockList) {
                startRoundTripDemo();
            } else {
                startStockListDemo();
            }
            isStockList = !isStockList;
        } else if (com == showErrors) {
            errorPrompt.show();
        } else if (com == showForm) {
            sendForm.show();
        }
    }

    /**
     * Class RepaintThread.
     */
    class RepaintThread extends Thread {

        /**
         * Method run.
         */
        public void run() {
            LSDisplayListener displayListener = null;
            while (goOn) {
                try {
                    //get the current display
                    displayListener = (LSDisplayListener) display.getCurrent();
                } catch (ClassCastException cce) {
                    //who has the control??
                    displayListener = null;
                }

                if (displayListener != null) {
                    //if one
                    if (displayListener.needsRepaint()) {
                        //that needs a repaint (ie some data change till the last repaint)
                        displayListener.repaint();
                    }
                }

                try {
                    //wait a second and poll (the LSDisplayListener) again
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }

    }
    
    class ConnectionListenerDispatcher implements ConnectionListener {

        public void onBufferFull() {
          //LSClient will hang connection until buffer is empty
        }

        public void onClientError(String error) {
            errorPrompt.printMessage(error);
        }

        public void onConnectionEnd(int cause) {
            errorPrompt.printMessage("Session closed");
        }

        public void onServerError(int code, String error) {
            errorPrompt.printMessage("Server Error " + code + ": " + error);
        }

        public void onStatusChange(String newStatus) {
            if (dis != null) {
                dis.onStatusChange(newStatus);
            }
        }
        
        
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
