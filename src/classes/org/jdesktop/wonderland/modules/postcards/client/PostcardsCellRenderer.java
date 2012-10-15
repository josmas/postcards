/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.postcards.client;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderBuffer;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.TextureRenderBuffer;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.input.*;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.utils.ScenegraphUtils;

/**
 * @author spcworld
 */
public class PostcardsCellRenderer extends BasicRenderer implements RenderUpdater {

    private static final Logger rendererLogger = Logger.getLogger(PostcardsCellRenderer.class.getName());

    public PostcardsCellRenderer(Cell cell) {
        super(cell);
    }

    private TextureRenderBuffer textureBuffer = null;
    private CaptureComponent captureComponent = null;
    private Node viewfinderNode;
    private BufferedImage captureImage = null;

    public static final float WIDTH = 1.6f; //x-extent
    public static final float HEIGHT = 0.9f; //y-extent
    private static final int IMAGE_HEIGHT = 360;
    private static final int IMAGE_WIDTH = 640;

    private Spatial stillSpatial, stillSpatialOn;


    @Override
    protected Node createSceneGraph(Entity entity) {

        /* Create the scene graph object*/
        Node root = new Node("Postcards Root");
        attachRecordingDevice(root, entity);
        root.setModelBound(new BoundingBox());
        root.updateModelBound();
        //Set the name of the buttonRoot node
        root.setName("Cell_" + cell.getCellID() + ":" + cell.getName());
        return root;

    }

    private void addCameraModel(Node device, Entity entity) throws IOException {
        //Load the cameramodel and add it to the scenegraph
        LoaderManager manager = LoaderManager.getLoaderManager();
        URL url = AssetUtils.getAssetURL("wla://movierecorder/pwl_3d_videorecorder_009.dae/pwl_3d_videorecorder_009.dae.gz.dep", this.getCell());
        DeployedModel dm = manager.getLoaderFromDeployment(url);
        Node cameraModel = dm.getModelLoader().loadDeployedModel(dm, entity);
        device.attachChild(cameraModel);

        //Get the still buttons
        stillSpatial = ScenegraphUtils.findNamedNode(cameraModel, "vrBtnStill_002-vrBtnStill");
        stillSpatialOn = ScenegraphUtils.findNamedNode(cameraModel, "vrBtnStillOn-Geometry-vrBtnStillOn");
        //locate "on" button so that it appears "pressed"
        stillSpatialOn.setLocalTranslation(0, -0.015f, 0);
        //"on" button is initially invisible
        stillSpatialOn.setVisible(false);
        //create a listener to control the appearance of the still buttons
        ButtonModel stillButtonModel = ((PostcardsCell) cell).getStillButtonModel();
        StillButtonListener buttonListener = new StillButtonListener();
        stillButtonModel.addChangeListener(buttonListener);
        stillButtonModel.addItemListener(buttonListener);

        CameraListener listener = new CameraListener();
        listener.addToEntity(entity);

    }

    private void attachRecordingDevice(Node device, Entity entity) {
        try {
            addCameraModel(device, entity);
        } catch (IOException ex) {
            rendererLogger.log(Level.SEVERE, "Failed to load camera model", ex);
        }
        entity.addEntity(createViewfinder(device));
        // entity.addEntity(createPowerButton(device));
    }

    private Entity createViewfinder(Node device) {
        WorldManager wm = ClientContextJME.getWorldManager();
        //Node for the viewfinder
        viewfinderNode = new Node("viewfinder");
        //Qhad to render the viewfinder
        Quad viewfinderQuad = new Quad("viewfinder", WIDTH, HEIGHT);
        viewfinderQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
        viewfinderQuad.updateRenderState();
        //Entity for the quad
        Entity viewfinderEntity = new Entity("viewfinder ");
        //Attach the quad to the node
        viewfinderNode.attachChild(viewfinderQuad);
        //Set the quad node position so that it is directly in front of the camera model
        //To give the appearance of an LCD panel
        viewfinderNode.setLocalTranslation(0.0f, -0.15f, -0.045f);
        //Create the texture buffer
        textureBuffer = (TextureRenderBuffer) wm.getRenderManager().createRenderBuffer(RenderBuffer.Target.TEXTURE_2D, IMAGE_WIDTH, IMAGE_HEIGHT);
        textureBuffer.setIncludeOrtho((false));
        //Disable the viewfinder
        //setViewfinderEnabled(false);
        //Create a camera node
        CameraNode cn = new CameraNode("MyCamera", null);
        //Create a node for the camera
        Node cameraSG = new Node("cameraSG");
        //Attach the camera to the node
        cameraSG.attachChild(cn);
        //Rotate the camera through 180 degrees about the Y-axis
        float angleDegrees = 180;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        Quaternion quat = new Quaternion().fromAngleAxis(angleRadians, new Vector3f(0, 1, 0));
        cameraSG.setLocalRotation(quat);
        //Translate the camera so it's in front of the model
        cameraSG.setLocalTranslation(0f, 0.5f, -0.5f);
        //Create a camera component
        CameraComponent cc = wm.getRenderManager().createCameraComponent(cameraSG, cn, IMAGE_WIDTH, IMAGE_HEIGHT, 45.0f, (float) IMAGE_WIDTH / (float) IMAGE_HEIGHT, 1f, 2000f, false);
        //Set the camera for the render buffer
        textureBuffer.setCameraComponent(cc);
        // Associated the texture buffer with the render manager, but keep it
        // off initially.
        wm.getRenderManager().addRenderBuffer(textureBuffer);
        textureBuffer.setRenderUpdater(this);
        //textureBuffer.setEnable(false);

        //Add the camera component to the quad entity
        viewfinderEntity.addComponent(CameraComponent.class, cc);

        //Create a texture state
        TextureState ts = (TextureState) wm.getRenderManager().createRendererState(RenderState.StateType.Texture);
        ts.setEnabled(true);
        //Set its texture to be the texture of the render buffer
        ts.setTexture(textureBuffer.getTexture());
        viewfinderQuad.setRenderState(ts);

        RenderComponent quadRC = wm.getRenderManager().createRenderComponent(viewfinderNode);
        viewfinderEntity.addComponent(RenderComponent.class, quadRC);

        device.attachChild(viewfinderNode);
        device.attachChild(cameraSG);

        createCaptureComponent(IMAGE_WIDTH, IMAGE_HEIGHT);

        //Create a listener to monitor the state of the power button
//        ItemListener powerButtonListener = new PowerButtonListener();
//        ((MovieRecorderCell) cell).getPowerButtonModel().addItemListener(powerButtonListener);

        return viewfinderEntity;
    }

    private void createCaptureComponent(int width, int height) {
        captureComponent = new CaptureComponent();
        captureComponent.setPreferredSize(new Dimension(width, height));
    }

    private BufferedImage createBufferedImage(ByteBuffer bb) {
        int width = textureBuffer.getWidth();
        int height = textureBuffer.getHeight();

        bb.rewind();
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 3;
                int b = bb.get(index);
                int g = bb.get(index + 1);
                int r = bb.get(index + 2);

                int pixel = ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255)) | 0xff000000;

                bi.setRGB(x, (height - y) - 1, pixel);
            }
        }
        return (bi);
    }

    void captureImage(String stillCaptureDirectory) {
        BufferedImage outputImage = createBufferedImage(textureBuffer.getTextureData());
        Calendar calendar = Calendar.getInstance();
        String imageFilename = "Wonderland.jpg";
        try {
            File outputFile = new File(stillCaptureDirectory + File.separator + imageFilename);
            ImageIO.write(outputImage, "jpg", outputFile);
        } catch (IOException e) {
            System.err.println("I/O exception in update: " + e);
            e.printStackTrace();
        }
    }

    public void update(Object arg0) {
        createBufferedImage(textureBuffer.getTextureData());
//        captureImage = createBufferedImage(textureBuffer.getTextureData());
//
//         try {
//            SwingUtilities.invokeLater(new Runnable () {
//                public void run () {
//                    captureComponent.repaint();
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new RuntimeException("Cannot capture texture buffer image");
//        }


    }

    public class CaptureComponent extends JComponent {
        public CaptureComponent() {
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            if (captureImage != null) {
                g.drawImage(captureImage, 0, 0, null);
            }
        }
    }


    class CameraListener extends EventClassListener {

        CameraListener() {
            super();
        }

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseButtonEvent3D.class};
        }

        // Note: we don't override computeEvent because we don't do any computation in this listener.
        @Override
        public void commitEvent(org.jdesktop.wonderland.client.input.Event event) {
            //rendererLogger.info("commit " + event + " for " + this);
            MouseButtonEvent3D mbe = (MouseButtonEvent3D) event;
            //ignore any mouse button that isn't the left one
            if (mbe.getButton() != MouseEvent3D.ButtonId.BUTTON1) {
                return;
            }
            TriMesh mesh = mbe.getPickDetails().getTriMesh();
            //rendererLogger.info("mesh: " + mesh);
            switch (mbe.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    mousePressed(mesh);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    mouseReleased(mesh);
                    break;
                case MouseEvent.MOUSE_CLICKED:
//                    mouseClicked(mesh);
                    break;
                default:
                    rendererLogger.warning("Unhandled event: " + mbe);
            }
        }

//        private void mouseClicked(TriMesh mesh) {
//            if (mesh == videoSpatial || mesh == videoSpatialOn) {
//                //rendererLogger.info("video button clicked");
//                if (!((MovieRecorderCell) cell).getPowerButtonModel().isSelected()) {
//                    //no power, can't record
//                    Toolkit.getDefaultToolkit().beep();
//                } else {
//                    DefaultButtonModel videoButtonModel = ((MovieRecorderCell) cell).getVideoButtonModel();
//                    if (videoButtonModel.isEnabled()) {
//                        videoButtonModel.setSelected(!videoButtonModel.isSelected());
//                    }
//                }
//            }
//            if (mesh == powerButtonBox) {
//                MovieRecorderCell mrCell = (MovieRecorderCell) cell;
//                //rendererLogger.info("clicked power button");
//                if (mrCell.isLocalRecording()) {
//                    //Can't turn off the power when recording
//                    Toolkit.getDefaultToolkit().beep();
//                } else {
//                    boolean power = mrCell.getPowerButtonModel().isSelected();
//                    mrCell.getPowerButtonModel().setSelected(!power);
//                }
//            }
//        }

        private void mousePressed(TriMesh mesh) {
            if (mesh == stillSpatial || mesh == stillSpatialOn) {
//                if (!((PostcardsCell) cell).getPowerButtonModel().isSelected()) {
//                    //no power, can't take a snapshot
//                    Toolkit.getDefaultToolkit().beep();
//                } else {
                DefaultButtonModel stillButtonModel = ((PostcardsCell) cell).getStillButtonModel();
                if (stillButtonModel.isEnabled()) {
                    stillButtonModel.setPressed(true);
                    stillButtonModel.setSelected(true);
                }
            }
//            }
        }

        private void mouseReleased(TriMesh mesh) {
            if (mesh == stillSpatial || mesh == stillSpatialOn) {
                //rendererLogger.info("still button released");
                DefaultButtonModel stillButtonModel = ((PostcardsCell) cell).getStillButtonModel();
                if (stillButtonModel.isEnabled()) {
                    stillButtonModel.setPressed(false);
                    stillButtonModel.setSelected(false);
                }
            }
        }
    }

    class StillButtonListener implements ChangeListener, ItemListener {

        public void itemStateChanged(ItemEvent event) {
            //rendererLogger.info("event: " + event);
            if (event.getStateChange() == ItemEvent.SELECTED) {
//                cameraShutter.play();
            }
        }

        public void stateChanged(ChangeEvent event) {
            //rendererLogger.info("event: " + event);
            WorldManager wm = ClientContextJME.getWorldManager();
            DefaultButtonModel stillButtonModel = ((PostcardsCell) cell).getStillButtonModel();
            if (stillButtonModel.isPressed()) {
                stillSpatial.setVisible(false);
                stillSpatialOn.setVisible(true);
            } else {
                stillSpatial.setVisible(true);
                stillSpatialOn.setVisible(false);
            }
            wm.addToUpdateList(stillSpatial);
            wm.addToUpdateList(stillSpatialOn);
        }
    }
}
