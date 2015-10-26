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

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.lightstreamer.javameclient.midp.SimpleItemUpdate;
import com.lightstreamer.javameclient.midp.SimpleTableListener;

/**
 * Class LSDisplayListener.
 */
public abstract class LSDisplayListener extends Canvas
        implements SimpleTableListener {
    
    /** Field $objectName$. */
    protected String strTitle = "";

    /** Field $objectName$.  */
    protected ErrorPrompt errorPrompt = null;

    /** Field $objectName$. */
    protected String status = "Waiting";

    /** Field $objectName$. */
    protected GraphicsTable table = null;

    /** Field $objectName$. */
    protected GraphicsTable statusTable = null;

    /** Field $objectName$. */
    protected int offsetTop = 0;
    private int offsetLogo = 0;

    /** Field $objectName$. */
    protected boolean needsRepaint = false;

    /** Field $objectName$. */
    protected int fontFace = Font.FACE_SYSTEM;

    /** Field $objectName$. */
    protected int fontSize = Font.SIZE_MEDIUM;

    private int np = 0;
    
    private int lastW = 0;
    private int lastH = 0;
    
    private Image logo;

    /**
     * Constructor LSDisplayListener.
     *
     * @param  title  ...
     * @param  errorPrompt  ...
     */
    public LSDisplayListener(String title, ErrorPrompt errorPrompt) {
        super();
        this.strTitle = title;
        this.errorPrompt = errorPrompt;
        try {
            logo = Image.createImage("/javamedemo/midlet/logobig.png");
            if (this.getWidth()/2 < logo.getWidth()) {
                logo = Image.createImage("/javamedemo/midlet/logo.png");
            }            
            this.offsetLogo = logo.getHeight();
            this.offsetTop = this.offsetLogo;
        } catch(IOException e) {
        }
    }

    /**
     * Method init.
     */
    public void init(String status) {
        this.status = status;
        return;
    }

    /**
     * Method paint.
     * Each time this method is called it will repaint the entire display
     * @param  g  ...
     */
    protected void paint(Graphics g) {
        //clean the display
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(0, 0, 0);
        
        //if we can put Lighstreamer's logo on top of the demo
        if (this.logo != null) { 
            g.drawImage(logo, 0, 0, Graphics.TOP | Graphics.LEFT);
            int offsetLeft = logo.getWidth();
        
            
            //we have the space to write down demo's title
            g.setFont(Font.getFont(fontFace, Font.STYLE_BOLD,
                                   Font.SIZE_MEDIUM));
            g.setColor(0, 0, 0);
            g.drawString(this.strTitle,
                         ((getWidth() - offsetLeft) / 2) + offsetLeft,
                         (offsetLogo / 2) + (g.getFont().getHeight() / 2),
                         Graphics.BASELINE | Graphics.HCENTER);
            
    
        }
        
        if (lastW != getWidth() || lastH != getHeight() || this.statusTable == null) { //check this.statusTable==null just in case getWidth and getHeight return 0
            lastW = getWidth();
            lastH = getHeight();
            
            this.offsetTop = this.offsetLogo + g.getFont().getHeight();
            
            this.initTables(g);
        }
        
        if (this.status.indexOf("Connecting") > -1) {
            //if status is "connecting" make a little animation
            np++;
            if (np == 6) {
                this.status = "Connecting";
                np = 0;
            } else {
                this.status = "." + this.status + ".";
            }
            needsRepaint = true;
        } else {
            needsRepaint = false;
        }

        //write the status
        this.statusTable.fillCell(g, this.status, 10, 255, 100, 1, 1, true);
    }

    protected void initTables(Graphics g) {
        //This table will contain the status (wait - connecting - streaming...)
        //This is a mono-cell table
        this.statusTable = new GraphicsTable(0, offsetLogo, getWidth(),
                g.getFont().getHeight(), 1, 1);
    }

    /**
     * Method onUpdate.
     *
     * @param  item  ...
     * @param  update  ...
     */
    public void onUpdate(int item, SimpleItemUpdate update) {
        //no updates waited on this base class
    }

    /**
     * Method onEndOfSnapshot.
     *
     * @param  item  ...
     */
    public void onEndOfSnapshot(int item) {
        //no updates waited on this base class
    }

    /**
     * Method onRawUpdatesLost.
     *
     * @param  item  ...
     * @param  lostUpdates  ...
     */
    public void onRawUpdatesLost(int item, int lostUpdates) {
        //no updates waited on this base class
    }

    /**
     * Method onUnsubscribe.
     */
    public void onUnsubscribe() {
        //no updates waited on this base class
    }

    /**
     * Method onControlError.
     *
     * @param  code  ...
     * @param  error  ...
     */
    public void onControlError(int code, String error) {
        //will put the error message in the status table
        //this.needsRepaint = true;
        errorPrompt.printMessage("Control Error " + code + ": " + error);
    }

    /**
     * Method onStatusChange.
     *
     * @param  newStatus  ...
     */
    public void onStatusChange(String newStatus) {
        this.status = newStatus;

        //don't set needsRepaint, repaint it now
        this.repaint();
    }

    /**
     * Method needsRepaint.
     * @return ...
     */
    public boolean needsRepaint() {
        return needsRepaint;
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
