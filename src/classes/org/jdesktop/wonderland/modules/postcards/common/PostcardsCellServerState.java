/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.common;

import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spcworld
 */
@XmlRootElement(name = "postcards-cell")

@XmlAccessorType(XmlAccessType.FIELD)
@ServerState
public class PostcardsCellServerState extends CellServerState {


    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.postcards.PostcardsCellMO";
    }

}
