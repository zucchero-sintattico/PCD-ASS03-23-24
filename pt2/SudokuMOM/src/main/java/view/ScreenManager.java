package view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private final Map<Screen, JFrame> screens;
    private JFrame currentScreen;

    public ScreenManager() {
        this.screens = new HashMap<>();
    }

    public void addScreen(Screen screen, JFrame frame) {
        this.screens.put(screen, frame);
    }

    public void switchScreen(Screen screen) {
        SwingUtilities.invokeLater(() -> {
            if(this.currentScreen != null){
                this.currentScreen.setVisible(false);
            }
            this.currentScreen = this.screens.get(screen);
            if (this.currentScreen != null) {
                this.currentScreen.setVisible(true);
            }else{
                throw new IllegalArgumentException("Screen " + screen + " not found");
            }
        });
    }

}