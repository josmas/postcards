/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.modules.postcards.client;

import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.jme.SceneWorker;
import org.jdesktop.wonderland.common.cell.CellID;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ResourceBundle;

public class PostcardsCell extends Cell {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/postcards/client/resources/Bundle");

    private PostcardsCellRenderer renderer;
    private DefaultButtonModel stillButtonModel;

    public PostcardsCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        stillButtonModel = new DefaultButtonModel();
        stillButtonModel.setSelected(false);
        stillButtonModel.setEnabled(true);
        stillButtonModel.addItemListener(new StillButtonChangeListener());
    }

        void captureImage() {
        SceneWorker.addWorker(new WorkCommit() {

            public void commit() {
                ((PostcardsCellRenderer) renderer).captureImage(getDefaultStillCaptureDirectory());
            }
        });
    }

    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        if (rendererType == RendererType.RENDERER_JME) {
            this.renderer = new PostcardsCellRenderer(this);
            return this.renderer;
        }
        else {
            return super.createCellRenderer(rendererType);
        }
    }

    public DefaultButtonModel getStillButtonModel() {
            return stillButtonModel;
    }

    class StillButtonChangeListener implements ItemListener {

        public void itemStateChanged(ItemEvent event) {
            //The state of the still button model has changed
            //Take some action. Rendering issues are dealth with by other listeners in
            //the renderer and the control panel
            if (event.getStateChange() == ItemEvent.SELECTED) {
                //cellLogger.info("should take a still");
                captureImage();

            }
        }
    }

    private String getDefaultStillCaptureDirectory() {
        String home = System.getProperty("user.home");
        //
        //Are we on a PC?
        File myDocuments = new File(home + File.separator
                + BUNDLE.getString("MY_DOCUMENTS") + File.separator
                + BUNDLE.getString("MY_PICTURES"));
        if (myDocuments.exists()) {
            return myDocuments.toString();
        }
        //
        //Or a Mac?
        File pictures =
                new File(home + File.separator + BUNDLE.getString("PICTURES"));
        if (pictures.exists()) {
            return pictures.toString();
        }
        //
        //Or Gnome?
        File documents =
                new File(home + File.separator + BUNDLE.getString("DOCUMENTS"));
        if (documents.exists()) {
            return documents.toString();
        }
        //
        //Otherwise
        return home;
    }

}
