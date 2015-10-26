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

import java.util.Date;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.lightstreamer.javameclient.midp.SimpleItemUpdate;

/**
 * Class LSDisplayListenerInteraction.
 */
public class LSDisplayListenerRoundTrip extends LSDisplayListener {
    private String[][] dataTable = new String[5][3];
    private Date[][] dateTable = new Date[5][3];
    private GraphicsTable table = null;
    private GraphicsTable orTable = null;
    private int rows = 5;

    /** Field $objectName$. */
    Graphics painter;

    /**
     * Constructor LSDisplayListenerInteraction.
     *
     * @param  title  ...
     * @param  errorPrompt  ...
     */
    public LSDisplayListenerRoundTrip(String title, ErrorPrompt errorPrompt) {
        super(title, errorPrompt);
    }

    /**
     * Method init.
     */
    public void init(String status) {
        super.init(status);
        dataTable = new String[5][3];
        dateTable = new Date[5][3];
    }
    
    protected void initTables(Graphics g) {
        super.initTables(g);
        
        int availHeight = getHeight() - this.offsetTop;

        boolean ok = false;
        while (!ok) {    //reduce number of displayed items and/or font size to fit small displays
            for (int i = 0; (i <= 1) && !ok; i++) {
                g.setFont(Font.getFont(fontFace, Font.STYLE_BOLD,
                                       fontSize));
                int rowH = g.getFont().getHeight() * 2;
                if ((rowH * rows) > availHeight) {
                    if (i == 0) {
                        this.fontSize = Font.SIZE_SMALL;
                    } else {
                        rows--;
                        this.fontSize = Font.SIZE_MEDIUM;
                        if (rows <= 1) {
                            //less than one line...not suitable, so we stop here
                            ok = true;
                        }
                    }
                } else {
                    ok = true;
                }
            }
        }

        //we make two overlapped table. One is 1-column table (will contain the messages)
        //the other is a 2-column table (IP and date/time) so each table will have 
        //half of his rows filled
        this.orTable = new GraphicsTable(0, this.offsetTop, getWidth(),
                                                              availHeight,
                                                              1, rows * 2);
        this.table = new GraphicsTable(0, this.offsetTop, getWidth(),
                                                            availHeight, 2,
                                                            rows * 2);
    }

    /**
     * Method paint.
     *
     * @param  g  ...
     */
    protected void paint(Graphics g) {
        super.paint(g);

        Date now = new Date();
        for (int i = 1; i <= rows; i++) {
            for (int f = 1; f <= 3; f++) {
                Date lastUpdate = dateTable[i - 1][f - 1];
                String toPrint = dataTable[i - 1][f - 1];
                if (toPrint == null) {
                    toPrint = "";
                }

                int dist = 0;
                if ((i % 2) == 0) {
                    dist = 255;
                }
                if (lastUpdate != null) {
                    if ((now.getTime() - lastUpdate.getTime()) < 500) {
                        g.setColor(0, 0, 255);
                        needsRepaint = true;
                    } else {
                        g.setColor(0, 0, 0);
                    }
                } else {
                    g.setColor(0, 0, 0);
                }

                if (f == 1) {
                    g.setFont(Font.getFont(fontFace, Font.STYLE_PLAIN,
                                           fontSize));
                    
                    this.orTable.fillCell(g, toPrint, dist, 255, 255, 1,
                                          (i * 2), false);
                } else {
                    g.setFont(Font.getFont(fontFace, Font.STYLE_BOLD,
                                           fontSize));
                    if (f == 2) {
                        toPrint = "Time: " + toPrint;
                    } else {
                        toPrint = "IP: " + toPrint;
                    }
                    this.table.fillCell(g, toPrint, dist, 255, 255, f - 1,
                                        (i * 2) - 1, false);

                }
            }

        }
    }

    /**
     * Method onUpdate.
     *
     * @param  item  ...
     * @param  update  ...
     */
    public void onUpdate(int item, SimpleItemUpdate update) {
        if (item > rows) {
            //we subscribe all items but we may have the chance to display
            //only some of them
            return;
        }
        needsRepaint = false;
        for (int i = 1; i <= 3; i++) {
            if (update.isFieldChanged(i)) {
                dataTable[item - 1][i - 1] = update.getFieldNewValue(i);
                dateTable[item - 1][i - 1] = new Date();
            }
        }

        this.repaint();
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
