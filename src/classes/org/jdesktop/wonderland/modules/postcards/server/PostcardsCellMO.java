/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.server;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.postcards.common.PostcardsCellClientState;
import org.jdesktop.wonderland.modules.postcards.common.PostcardsCellServerState;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 *
 * @author spcworld
 */
@ExperimentalAPI
public class PostcardsCellMO extends CellMO{

    public PostcardsCellMO() {
        super();
    }

    @Override
    public CellServerState getServerState(CellServerState state) {
        if(state == null) {
            state = new PostcardsCellServerState();
        }
        return super.getServerState(state);
    }

    @Override
    protected String getClientCellClassName(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.postcards.client.PostcardsCell";
    }


    @Override
    protected CellClientState getClientState(CellClientState cellClientState, WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (cellClientState == null) {
            cellClientState = new PostcardsCellClientState();
        }
        return super.getClientState(cellClientState, clientID, capabilities);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
