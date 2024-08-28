package org.src.view;

import org.src.controller.Controller;
import org.src.model.UserImpl;

public class Login extends AbstractInputView {

    public Login(ScreenManager screenManager, Controller controller){
        super(screenManager, controller, "Login", "Insert username", "Create User");
    }

    @Override
    protected void handleAction() {
        if(!this.inputField.getText().isEmpty()){
            this.controller.setUser(new UserImpl(this.inputField.getText()));
            this.screenManager.switchScreen("menu");
        }else{
            MessageDialog.showErrorMessage(this, "Username Invalid", "This username isn't valid");
        }
    }

}