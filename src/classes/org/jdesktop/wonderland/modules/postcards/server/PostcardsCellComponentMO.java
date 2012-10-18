package org.jdesktop.wonderland.modules.postcards.server;

import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;

/**
 * Created with IntelliJ IDEA.
 * User: Bob
 * Date: 17/10/12
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostcardsCellComponentMO extends CellComponentMO {

    public PostcardsCellComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.postcards.PostcardsComponent";
    }
}
