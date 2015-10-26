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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Class GraphicsTable.
 * Crea una tabella divisa in celle in uno spazio determinato del monitor
 * dando la possibilità di ridisegnare una cella sola
 */
public class GraphicsTable {

    private int rowHeight = 0;
    private int colWidth = 0;
    private int cols = 0;
    private int rows = 0;
    private int top = 0;
    private int left = 0;

    /**
     * Constructor GraphicsTable.
     *
     * @param  left  table distance from the left margin
     * @param  top  table distance from the top margin
     * @param  width  table width
     * @param  height  table Height
     * @param  cols  columns number
     * @param  rows  rows number
     */
    public GraphicsTable(int left, int top, int width, int height, int cols,
                         int rows) {
        this.init(left, top, width, height, cols, rows);
    }
    
    public void init(int left, int top, int width, int height, int cols,
                         int rows) {
        if ((height == 0) || (rows == 0) || (width == 0) || (cols == 0)) {
            throw new IllegalArgumentException(
                "Height, width, rows and cols must be greater than 0");
        }
        this.rowHeight = height / rows;
        this.colWidth = width / cols;
        this.cols = cols;
        this.rows = rows;
        this.top = top;
        this.left = left;
    }

    /**
     * Method fillCell. Fill a cell with a text
     *
     *
     * @param  painter  ...
     * @param  text  Text to write in the cell
     * @param  r  red part of the RGB color to use as cell background
     * @param  g  green part of the RGB color to use as cell background
     * @param  b  blue part of the RGB color to use as cell background
     * @param  x  column number to write in
     * @param  y  row number to write in
     */
    public void fillCell(Graphics painter, String text, int r, int g, int b,
                         int x, int y, boolean centered) {
        if (painter.getFont().stringWidth(text) <= this.colWidth) {
            //text fit in a single row  
            fillCell(painter, text, "", r, g, b, x, y, centered);
        } else {
            //text is too long to fit the row in one line so it is divided in two and
            //shown in 2 lines on the same row
            fillCell(painter, text.substring(0, text.length() / 2),
                     text.substring(text.length() / 2, text.length() - 1), r,
                     g, b, x, y, centered);
        }
    }

    /**
     * Method fillCell. Fill a cell with a text divided in two lines
     *
     *
     *
     * @param  painter  ...
     * @param  firstLine  Text to write in the cell (first line)
     * @param  secondLine  Text to write in the cell (second line)
     * @param  r  red part of the RGB color to use as cell background
     * @param  g  green part of the RGB color to use as cell background
     * @param  b  blue part of the RGB color to use as cell background
     * @param  x  column number to write in
     * @param  y  row number to write in
     */
    public void fillCell(Graphics painter, String firstLine, String secondLine,
                         int r, int g, int b, int x, int y, boolean centered) {
        if (((r < 0) || (r > 255)) || ((g < 0) || (g > 255))
                || ((b < 0) || (b > 255))) {
            throw new IllegalArgumentException(
                "The RGB colors must be between 0 and 255");
        }

        if ((y > rows) || (x > cols)) {
            throw new IllegalArgumentException("Cell out of table");
        }
        //calculate x start position
        int newX = ((x - 1) * this.colWidth) + left;
        //calculate y start position
        int newY = ((y - 1) * this.rowHeight) + top;

        //save the actual color to set it back later
        int oldColor = painter.getColor();

        //set the new color
        painter.setColor(r, g, b);
        //fill the cell background
        painter.fillRect(newX, newY, this.colWidth, this.rowHeight);
        //set back the old color
        painter.setColor(oldColor);
        
        int stringX = centered ? newX + (this.colWidth / 2) : newX;
        int xPlace = centered ? Graphics.HCENTER : Graphics.LEFT;

        //save the actual font size to set it back later
        int oldSize = painter.getFont().getSize();

        if (((painter.getFont().getHeight() * 2) > this.rowHeight)
                || secondLine.equals("")) {
            //there is only 1 line of text to write or 2 lines of text are too tall to fit one row. 
            //In the second case some characters will not be visible on display
            if (secondLine.equals("")) {
                //put the text line at middle height in the cell
                painter.drawString(
                        firstLine, stringX,
                        (newY + this.rowHeight)
                        - ((this.rowHeight - painter.getFont().getHeight()) / 2)
                            - 2, Graphics.BASELINE | xPlace);
                

                //end
                return;
            }    //else 2 lines are too tall, 1 line (probably) too long, we will (probably) lose some characters

            boolean oldInOneLine = (painter.getFont().getHeight()
                                    > this.rowHeight);

            //try to reduce character height 
            painter.setFont(Font.getFont(painter.getFont().getFace(),
                                         painter.getFont().getStyle(),
                                         Font.SIZE_SMALL));
            if ((painter.getFont().getHeight() * 2) > this.rowHeight) {
                //reduced the font we are not able to write the text in 2 lines
                if (oldInOneLine) {
                    //before the font size change one line was short enough to
                    //fit the cell, so i can turn back the font to his original
                    //size
                    painter.setFont(Font.getFont(painter.getFont().getFace(),
                                                 painter.getFont().getStyle(),
                                                 oldSize));

                }    //else even one line was too tall to fit the cell, mantain the small font

                //write the text on the cell ignoring the second line
                painter.drawString(
                        firstLine, stringX,
                        (newY + (this.rowHeight))
                        - ((this.rowHeight - painter.getFont().getHeight()) / 2)
                            - 2, Graphics.BASELINE | xPlace);
                
                //restore the original font size
                painter.setFont(Font.getFont(painter.getFont().getFace(),
                                             painter.getFont().getStyle(),
                                             oldSize));

                //end
                return;
            }    // else with the new font size we are able to write our two text lines

        }    //else no problems, i can write the 2 text lines with no overflows (except if one of the line is too long)

        //write the lines
        painter.drawString(firstLine, stringX,
                           newY + (this.rowHeight / 2),
                           Graphics.BASELINE | xPlace);
        painter.drawString(secondLine, stringX,
                           newY + (this.rowHeight / 2),
                           Graphics.TOP | xPlace);

        if (oldSize != painter.getFont().getSize()) {
            //restore the original font size
            painter.setFont(Font.getFont(painter.getFont().getFace(),
                                         painter.getFont().getStyle(),
                                         oldSize));

        }
    }
}


/*--- Formatted in Lightstreamer Java Convention Style on 2007-02-12 ---*/
