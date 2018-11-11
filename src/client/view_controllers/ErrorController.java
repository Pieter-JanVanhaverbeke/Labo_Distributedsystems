package client.view_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Created by ruben on 8/11/18.
 */
public class ErrorController {
    private Image errorImage = new Image("client/scenes/pictures/error.png");
    private Image warningImage = new Image("client/scenes/pictures/warning.png");

    @FXML
    public ImageView imageView;

    @FXML
    public Label title;

    @FXML
    public Label message;

    @FXML
    public HBox optionButtons;

    public void setError(boolean error){
        if(error)
            imageView = new ImageView(errorImage);
        else
            imageView = new ImageView(warningImage);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public void setMessage(String message){
        this.message.setText(message);
    }

    public void setOptionButtons(List<Button> buttons){
        optionButtons.getChildren().addAll(buttons);
    }



}
