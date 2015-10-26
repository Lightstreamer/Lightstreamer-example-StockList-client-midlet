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
 * Class LSDisplayListenerStockList.
 */
public class LSDisplayListenerStockList extends LSDisplayListener {
    private String[][] dataTable = new String[3][3];
    private Date[][] dateTable = new Date[3][3];

    /** Field $objectName$. */
    Graphics painter;

    /**
     * Constructor LSDisplayListenerStockList.
     *
     * @param  title  ...
     * @param  errorPrompt  ...
     */
    public LSDisplayListenerStockList(String title, ErrorPrompt errorPrompt) {
        super(title, errorPrompt);
    }

    /**
     * Method init.
     */
    public void init(String status) {
        super.init(status);
        dataTable = new String[3][3];
        dateTable = new Date[3][3];
    }

    protected void initTables(Graphics g) {
        super.initTables(g);
        this.table = new GraphicsTable(0, this.offsetTop,
                              getWidth(),
                              getHeight() - this.offsetTop, 4, 4);
    }
    
    /**
     * Method paint.
     *
     * @param  g  ...
     */
    protected void paint(Graphics g) {
        super.paint(g);

        //Paint table headers (first row)
        g.setColor(255, 255, 255);
        g.setFont(Font.getFont(fontFace, Font.STYLE_BOLD, fontSize));
        this.table.fillCell(g, "Name", 255, 0, 0, 1, 1, true);
        this.table.fillCell(g, "Last", "price", 255, 0, 0, 2, 1, true);
        this.table.fillCell(g, "Time", "(GMT+1)", 255, 0, 0, 3, 1, true);
        this.table.fillCell(g, "Change", 255, 0, 0, 4, 1, true);

        //Paint table headers (first column)
        g.setFont(Font.getFont(fontFace, Font.STYLE_PLAIN, fontSize));
        this.table.fillCell(g, "Stock 1", 255, 0, 0, 1, 2, true);
        this.table.fillCell(g, "Stock 2", 255, 0, 0, 1, 3, true);
        this.table.fillCell(g, "Stock 3", 255, 0, 0, 1, 4, true);

        Date now = new Date();
        for (int i = 1; i <= 3; i++) {    //item per item
            for (int f = 1; f <= 3; f++) {    //field per field
                String toPrint = dataTable[i - 1][f - 1];
                Date lastUpdate = dateTable[i - 1][f - 1];
                if (toPrint == null) {
                    toPrint = "";
                }

                //format data
                if (f == 3) {
                    g.setFont(Font.getFont(fontFace, Font.STYLE_BOLD,
                                           fontSize));
                    if (toPrint.indexOf("-") == 0) {
                        g.setColor(255, 0, 0);
                    } else {
                        toPrint = "+" + toPrint;
                        g.setColor(0, 255, 0);
                    }
                } else {
                    g.setFont(Font.getFont(fontFace, Font.STYLE_PLAIN,
                                           fontSize));
                    g.setColor(0, 0, 0);
                }
                if (f != 2) {
                    toPrint = toTwoDecimals(toPrint);
                }

                if ((lastUpdate != null)
                        && (now.getTime() - lastUpdate.getTime()) < 500) {
                    //this cell must be illuminated
                    this.table.fillCell(g, toPrint, 255, 255, 100, 1 + f,
                                        1 + i, true);
                    //i need a future repaint to turn off the cell
                    needsRepaint = true;
                } else {
                    this.table.fillCell(g, toPrint, 255, 255, 255, 1 + f,
                                        1 + i, true);
                }
            }

        }

    }

    /**
     * Method toTwoDecimals.
     *
     * @param  orig  ...
     * @return ...
     */
    public String toTwoDecimals(String orig) {
        int dotIndex = orig.indexOf(".");
        if (dotIndex > -1) {
            dotIndex -= (orig.length() - 3);
            while (dotIndex > 0) {
                dotIndex--;
                orig += "0";
            }
            if (dotIndex < 0) {
                orig = orig.substring(0, orig.length() + dotIndex);
            }
        } else {
            orig += ".00";
        }
        return orig;
    }

    /**
     * Method onEndOfSnapshot.
     */
    public void onEndOfSnapshot() {

    }

    /**
     * Method onUpdate.
     *
     * @param  item  ...
     * @param  update  ...
     */
    public void onUpdate(int item, SimpleItemUpdate update) {
        needsRepaint = false;
        for (int i = 1; i <= 3; i++) {
            if (update.isFieldChanged(i)) {
                //save the update value in a matrix
                dataTable[item - 1][i - 1] = update.getFieldNewValue(i);
                //and a relative timestamp (will be used to handle cell illumination)
                dateTable[item - 1][i - 1] = new Date();
            }
        }

        //don't set needsRepaint, repaint it now
        this.repaint();
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
