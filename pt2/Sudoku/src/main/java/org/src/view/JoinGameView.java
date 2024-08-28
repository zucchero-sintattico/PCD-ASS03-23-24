package org.src.view;

import org.src.controller.Controller;
import java.io.IOException;
import java.util.Objects;

public class JoinGameView extends AbstractInputView {

    public JoinGameView(ScreenManager screenManager, Controller controller) {
        super(screenManager, controller, "Join", "Join in grid", "Join session");
    }

    @Override
    protected void handleAction() {
        if(Objects.equals(this.inputField.getText(), "")){
            MessageDialog.showErrorMessage(this, "Invalid Session ID", "Session Problem");
        }else{
            try {
                this.controller.joinSudoku(this.controller.getUser().getName(), this.inputField.getText());
                System.out.println("Session Id: " + this.inputField.getText());
                this.screenManager.switchScreen("grid");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}