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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

/**
 * Class ErrorPrompt. 
 */
public class ErrorPrompt implements CommandListener {

    /** Field $objectName$.  */
    static int MAX_MEX = 20;

    /** Field $objectName$.  */
    boolean respectMax = true;

    /** Field $objectName$.  */
    Command close;

    /** Field $objectName$.  */
    Command clear;

    /** Field $objectName$.  */
    Command changeLimit;

    /** Field $objectName$.  */
    Displayable prevDis = null;

    /** Field $objectName$.  */
    Display display = null;

    /** Field $objectName$.  */
    MIDlet midlet;

    /** Field $objectName$.  */
    Form errorForm = null;

    /** Field $objectName$.  */
    boolean empty = true;

    /** Field $objectName$.  */
    int logLineNumber = 1;

    /**
     * Constructor ErrorPrompt. 
     *
     * @param  parent  ...
     */
    public ErrorPrompt(MIDlet parent) {
        close = new Command("Back", Command.STOP, 2);
        clear = new Command("Clear", Command.OK, 1);
        changeLimit = new Command("Disable/Enable log limit", Command.OK, 1);

        errorForm = new Form("Error Form");

        errorForm.addCommand(close);
        errorForm.addCommand(clear);
        errorForm.addCommand(changeLimit);

        errorForm.setCommandListener(this);
        midlet = parent;
        reset();
    }

    /**
     * Method printMessage. 
     *
     * @param  message  ...
     */
    public void printMessage(String message) {
        if (empty) {
            empty = false;
            errorForm.delete(0);
        } else if (respectMax && (errorForm.size() >= MAX_MEX)) {
            errorForm.delete(0);
        }
        errorForm.append(logLineNumber + "## " + message + "\n");
        logLineNumber++;
        //      show();
    }

    /**
     * Method show. 
     */
    public void show() {
        display = Display.getDisplay(midlet);
        prevDis = display.getCurrent();
        display.setCurrent(errorForm);
    }

    /**
     * Method hide. 
     */
    public void hide() {
        display = Display.getDisplay(midlet);
        display.setCurrent(prevDis);
    }

    /**
     * Method reset. 
     */
    public void reset() {
        empty = true;
        while (errorForm.size() > 0) {
            errorForm.delete(0);
        }
        errorForm.append("EMPTY");
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
        } else if (arg0 == clear) {
            reset();
        } else if (arg0 == changeLimit) {
            respectMax = !respectMax;
        }

    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
