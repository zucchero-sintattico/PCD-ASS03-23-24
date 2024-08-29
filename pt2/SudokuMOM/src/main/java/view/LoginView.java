package view;

import logic.Controller;
import logic.user.UserImpl;

public class LoginView extends AbstractInputView {

    public LoginView(ScreenManager screenManager, Controller controller){
        super(screenManager, controller, "Login", "Insert username", "Create User");
    }

    @Override
    protected void handleAction() {
        if(!this.inputField.getText().isEmpty()){
            this.controller.setUser(new UserImpl(this.inputField.getText()));
            this.screenManager.switchScreen(Screen.MENU);
        }else{
            MessageDialog.showErrorMessage(this, "Username Invalid", "This username isn't valid");
        }
    }

}