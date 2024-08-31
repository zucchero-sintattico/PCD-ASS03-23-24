package view;

import logic.Controller;
import java.io.IOException;

public class JoinGameView extends AbstractInputView {

    public JoinGameView(ScreenManager screenManager, Controller controller) {
        super(screenManager, controller, "Join", "Join in grid", "Join session");
    }

    @Override
    protected void handleAction() {
        if(this.inputField.getText().isEmpty()){
            MessageDialog.showErrorMessage(this, "Session Problem", "Invalid Session ID");
        }else{
            try {
                this.controller.joinSudoku(this.inputField.getText());
                this.screenManager.switchScreen(Screen.GRID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}