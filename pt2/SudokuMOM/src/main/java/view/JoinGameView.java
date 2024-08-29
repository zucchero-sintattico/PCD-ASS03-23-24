package view;

import logic.Controller;
import java.io.IOException;
import java.util.Objects;

public class JoinGameView extends AbstractInputView {

    public JoinGameView(ScreenManager screenManager, Controller controller) {
        super(screenManager, controller, "Join", "Join in grid", "Join session");
    }

    @Override
    protected void handleAction() {
        if(Objects.equals(this.inputField.getText(), "")){
            MessageDialog.showErrorMessage(this, "Session Problem", "Invalid Session ID");
        }else{
            try {
                this.controller.joinSudoku(this.controller.getUser().name(), this.inputField.getText());
                this.screenManager.switchScreen(Screen.GRID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}