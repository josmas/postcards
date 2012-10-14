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
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderBuffer;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.TextureRenderBuffer;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;

/**
 *
 * @author spcworld
 */
public class PostcardsCellRenderer extends BasicRenderer implements RenderUpdater
{

    public PostcardsCellRenderer(Cell cell) {
        super(cell);
    }
    private TextureRenderBuffer textureBuffer = null;
    
 private int IMAGE_HEIGHT = 100;  
   private int IMAGE_WIDTH = 100;
 

    @Override
    protected Node createSceneGraph(Entity entity) {

        /* Create the scene graph object*/
        Node root = new Node("Movie Recorder Root");
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

    }

    private void attachRecordingDevice(Node device, Entity entity) {
        try {
            addCameraModel(device, entity);
        } catch (IOException ex) {
 //           rendererLogger.log(Level.SEVERE, "Failed to load camera model", ex);
        }
        entity.addEntity(createViewfinder(device));
       // entity.addEntity(createPowerButton(device));
    }

    private Entity createViewfinder(Node device) {
        WorldManager wm = ClientContextJME.getWorldManager();
//        //Node for the viewfinder
//        viewfinderNode = new Node("viewfinder");
//        //Qhad to render the viewfinder
//        Quad viewfinderQuad = new Quad("viewfinder", WIDTH, HEIGHT);
//        viewfinderQuad.setLightCombineMode(LightCombineMode.Off);
//        viewfinderQuad.updateRenderState();
//        //Entity for the quad
//        Entity viewfinderEntity = new Entity("viewfinder ");
//        //Attach the quad to the node
//        viewfinderNode.attachChild(viewfinderQuad);
//        //Set the quad node position so that it is directly in front of the camera model
//        //To give the appearance of an LCD panel
//        viewfinderNode.setLocalTranslation(0.0f, -0.15f, -0.045f);
//        //Create the texture buffer
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
}
