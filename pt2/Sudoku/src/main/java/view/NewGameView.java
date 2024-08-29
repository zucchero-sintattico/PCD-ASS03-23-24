package view;

import logic.Controller;
import java.io.IOException;

public class NewGameView extends AbstractInputView {

    public NewGameView(ScreenManager screenManager, Controller controller){
        super(screenManager, controller, "GridID", "Insert GridID", "Insert");
    }

    @Override
    protected void handleAction() {
        if(!this.inputField.getText().isEmpty()){
            try {
                this.controller.createSudoku(this.controller.getUser().name(), this.inputField.getText());
                this.screenManager.switchScreen(Screen.GRID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }else{
            MessageDialog.showErrorMessage(this, "Username Invalid", "This username isn't valid");
        }
    }
}
