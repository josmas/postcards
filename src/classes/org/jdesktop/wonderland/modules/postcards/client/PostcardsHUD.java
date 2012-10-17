package  org.jdesktop.wonderland.modules.postcards.client;

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.postcards.common.PostcardsCellServerState;

/**
 *
 * @author jos
 */
public class PostcardsHUD {

    private static final Logger logger = Logger.getLogger(PostcardsHUD.class.getName());
    
    private HUD mainHUD;
    private HUDComponent sampleHud;
    private PostcardsCell postcardCell;
    private PostcardsPanel postcardsPanel;
    
    private JButton oneButton;

    /**
     * Constructor to grab the main HUD area, and display the HUD within it.
     */
    public PostcardsHUD() {
        mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        displayHud();
        try {
            // create postcard cell
            CellID cellID = CellUtils.createCell(new PostcardsCellServerState());
            CellCache cache = ClientContextJME.getCellCache(LoginManager.getPrimary().getPrimarySession());
             postcardCell = (PostcardsCell) cache.getCell(cellID);
             postcardCell.setHud(this);
        } catch (CellCreationException ex) {
            Logger.getLogger(PostcardsHUD.class.getName()).log(Level.SEVERE, "could not create cell", ex);
        }

    }


    private void displayHud() {
        postcardsPanel= createPanelForHUD();
        createHUDComponent();
        setHudComponentVisible(true);
    }

    /**
     * Creates a JPanel which will contain the elements to be shown in the HUD.
     * @return panelForHUD
     */
    private PostcardsPanel createPanelForHUD() {

        return new PostcardsPanel();
    }

    /**
     * Creates the HUD Component, if it does not exist yet, and adds it to the
     * CENTER of the main HUD area (entire screen above the 3D scene).
     */
    private void createHUDComponent() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (sampleHud == null) {
                    JPanel panelForHUD = createPanelForHUD();
                    sampleHud = mainHUD.createComponent(panelForHUD);
                    sampleHud.setDecoratable(true);
                    sampleHud.setName("Postcards HUD");
                    sampleHud.setPreferredLocation(Layout.CENTER);
                    mainHUD.addComponent(sampleHud);
                }
            }
        });

    }

    /**
     * Changes the visibility of the HUD according to the boolean passed.
     * @param show
     */
    public void setHudComponentVisible(final boolean show) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                sampleHud.setVisible(show);
            }
        });

    }

    void setCaptureImage(BufferedImage outputImage) {
        postcardsPanel.setCaptureImage(outputImage);
        
    }
}
