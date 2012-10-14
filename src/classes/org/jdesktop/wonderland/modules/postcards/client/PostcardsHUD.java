package  org.jdesktop.wonderland.modules.postcards.client;

import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

/**
 *
 * @author jos
 */
public class PostcardsHUD {

    private static final Logger logger = Logger.getLogger(PostcardsHUD.class.getName());
    
    private HUD mainHUD;
    private HUDComponent sampleHud;
    
    private JButton oneButton;

    /**
     * Constructor to grab the main HUD area, and display the HUD within it.
     */
    public PostcardsHUD() {
        mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        displayHud();
    }


    private void displayHud() {
        createPanelForHUD();
        createHUDComponent();
        setHudComponentVisible(true);
    }

    /**
     * Creates a JPanel which will contain the elements to be shown in the HUD.
     * @return panelForHUD
     */
    private JPanel createPanelForHUD() {
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
}
