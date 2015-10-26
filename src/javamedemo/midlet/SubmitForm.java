/*
 * Copyright (c) Lightstreamer Srl
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

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.lightstreamer.javameclient.midp.LSClient;
import com.lightstreamer.javameclient.midp.MessageInfo;
import com.lightstreamer.javameclient.midp.SendMessageListener;

/**
 * Class ErrorPrompt. 
 */
public class SubmitForm implements CommandListener {

    Command close;
    Command send;

    Displayable prevDis = null;
    Display display = null;
    MIDlet midlet;

    Form sendForm = null;
    TextField text = null;
    ChoiceGroup field = null;
    
    LSClient client = null;
    ErrorPrompt errorPrompt;
    SendMessageListener messageListener;

    /**
     * Constructor ErrorPrompt. 
     *
     * @param  parent  ...
     */
    public SubmitForm(MIDlet parent, LSClient client, ErrorPrompt errorPrompt) {
        this.errorPrompt = errorPrompt; 
        this.client = client;
        
        messageListener = new SentListener();
        
        close = new Command("Back", Command.STOP, 2);
        send = new Command("Send", Command.OK, 1);
        
        sendForm = new Form("RoundTrip Form");
        
        text = new TextField("Write message","",100,TextField.ANY);
        sendForm.append(text);

        field = new ChoiceGroup("Select Item",Choice.EXCLUSIVE,new String[]{"1","2","3","4","5"},null);
        sendForm.append(field);
        
        sendForm.addCommand(close);
        sendForm.addCommand(send);
        

        sendForm.setCommandListener(this);
        midlet = parent;
        reset();
    }


    /**
     * Method show. 
     */
    public void show() {
        display = Display.getDisplay(midlet);
        prevDis = display.getCurrent();
        display.setCurrent(sendForm);
    }

    /**
     * Method hide. 
     */
    public void hide() {
        reset();
        display = Display.getDisplay(midlet);
        display.setCurrent(prevDis);
    }

    /**
     * Method reset. 
     */
    public void reset() {
        this.text.setString("");
        this.field.setSelectedIndex(0, true);
    }
    
    /**
     * Method send. 
     */
    public void send() {
        int selIndex = field.getSelectedIndex();
        if (selIndex < 0 || selIndex > 4) {
            return;
        }
        
        String message = this.text.getString();
        if (message == null || message.length() <= 0) {
            return;
        }
        
        this.client.sendMessage(new MessageInfo("RT|"+selIndex+"|"+message,"roundtrip",0),messageListener);
        
        hide();
    }

    /**
     * Method commandAction. 
     *
     * @param  arg0  ...
     * @param  arg1  ...
     */
    public void commandAction(Command arg0, Displayable arg1) {
        if (arg0 == close) {
            hide();
        } else if (arg0 == send) {
            send();
        } 

    }
    
    private class SentListener implements SendMessageListener {

        public void onAbort(MessageInfo originalMessage, int prog) {
            errorPrompt.printMessage("Message " + prog + " aborted. Original message: " + originalMessage.getMessage());
        }

        public void onError(int code, String error,
                MessageInfo originalMessage, int prog) {
            errorPrompt.printMessage("Message error " + code + ": " + error + ". Original message: " + originalMessage.getMessage());
        }

        public void onProcessed(MessageInfo originalMessage, int prog) {
        }
        
    }
    
    

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
