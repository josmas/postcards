/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.client;

import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.jme.SceneWorker;
import org.jdesktop.wonderland.common.cell.CellID;

public class PostcardsCell extends Cell {

    private PostcardsCellRenderer renderer;
    public PostcardsCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
    }

        void captureImage() {
        SceneWorker.addWorker(new WorkCommit() {

            public void commit() {
                ((PostcardsCellRenderer) renderer).captureImage("");
            }
        });
    }
}
