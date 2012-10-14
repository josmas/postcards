/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.client;

import com.jme.scene.Node;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;

/**
 *
 * @author spcworld
 */
public class PostcardsCellRenderer extends BasicRenderer{

    public PostcardsCellRenderer(Cell cell) {
        super(cell);
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
