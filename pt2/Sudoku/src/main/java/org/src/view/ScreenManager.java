package org.src.view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private final Map<String, JFrame> screens;
    private JFrame currentScreen;

    public ScreenManager() {
        this.screens = new HashMap<>();
    }

    public void addScreen(String screenName, JFrame frame) {
        this.screens.put(screenName, frame);
    }

    public void switchScreen(String screenName) {
        if(this.currentScreen != null){
            this.currentScreen.setVisible(false);
        }

        this.currentScreen = screens.get(screenName);
        if (this.currentScreen != null) {
            SwingUtilities.invokeLater(() -> this.currentScreen.setVisible(true));
        }else{
            throw new IllegalArgumentException("Screen " + screenName + " not found");
        }
    }

    public JFrame getCurrentScreen() {
        return this.currentScreen;
    }

    public void dispose(){
        this.currentScreen.dispose();
    }
}
