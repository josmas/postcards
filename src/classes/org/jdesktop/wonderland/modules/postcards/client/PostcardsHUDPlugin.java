package org.jdesktop.wonderland.modules.postcards.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 *
 * @author jos
 */
@Plugin
public class PostcardsHUDPlugin extends BaseClientPlugin {
    
    private JMenuItem postcardsHUDMI = null;
    private PostcardsHUD postcardsHUD;
    private boolean postcardsHUDEnabled = false;

    /**
     * Creates a new Menu Item for the HUD that will allow to show/hide it.
     * @param loginInfo
     */
    @Override
    public void initialize(ServerSessionManager loginInfo) {
        postcardsHUDMI = new JCheckBoxMenuItem("Postcards HUD");
        postcardsHUDMI.setSelected(false);
        postcardsHUDMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                postcardsHUDEnabled = !postcardsHUDEnabled;
                postcardsHUDMI.setSelected(postcardsHUDEnabled);
                if (postcardsHUD == null ) {
                    postcardsHUD = new PostcardsHUD();
                }
                else
                    postcardsHUD.setHudComponentVisible(postcardsHUDEnabled);
            }
        });

        super.initialize(loginInfo);
        postcardsHUD.getPostcardCell();
    }

    /**
     * Adds the Menu Item created in initialize to the Window Menu in the
     * Wonderland Client
     */
    @Override
    public void activate() {
        JmeClientMain.getFrame().addToWindowMenu(postcardsHUDMI);
    }

     /**
     * Removes the Menu Item created in initialize to the Window Menu in the
     * Wonderland Client
     */
    @Override
    public void deactivate() {
        JmeClientMain.getFrame().removeFromWindowMenu(postcardsHUDMI);
    }

}
