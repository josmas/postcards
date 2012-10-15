/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.client;

import com.jme.bounding.BoundingSphere;

import java.awt.Image;

import com.jme.math.Vector3f;

import java.util.Properties;
import java.util.ResourceBundle;

import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.postcards.common.PostcardsCellServerState;

/**
 * @author spcworld
 */

@CellFactory
public class PostcardsCellFactory implements CellFactorySPI {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/postcards/client/resources/Bundle");

    public String[] getExtensions() {
        return new String[]{"postcards"};
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
        CellServerState state = new PostcardsCellServerState();

        // Set a bounding hint based upon the width x height x depth of the
        // movie recorder
        BoundingSphere box = new BoundingSphere(1.0f, new Vector3f(1f, 0.7f, 0.2f));
        BoundingVolumeHint hint = new BoundingVolumeHint(true, box);
        state.setBoundingVolumeHint(hint);
        state.setName(BUNDLE.getString("POSTCARDS"));
        return (T) state;
    }

    public String getDisplayName() {
        return BUNDLE.getString("POSTCARDS");
    }

    public Image getPreviewImage() {
        //URL url = MovieRecorderCellFactory.class.getResource(
        //      "resources/movierecorder_preview.png");
        //return Toolkit.getDefaultToolkit().createImage(url);
        return null;
    }

}
