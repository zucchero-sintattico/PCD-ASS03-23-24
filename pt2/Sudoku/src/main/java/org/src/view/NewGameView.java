package org.src.view;

import org.src.controller.Controller;
import java.io.IOException;

public class NewGameView extends AbstractInputView {

    public NewGameView(ScreenManager screenManager, Controller controller){
        super(screenManager, controller, "GridID", "Insert GridID", "Insert");
    }

    @Override
    protected void handleAction() {
        if(!this.inputField.getText().isEmpty()){
            try {
                this.controller.createSudoku(this.controller.getUser().getName(), this.inputField.getText());
                this.screenManager.switchScreen("grid");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }else{
            MessageDialog.showErrorMessage(this, "Username Invalid", "This username isn't valid");
        }
    }
}
