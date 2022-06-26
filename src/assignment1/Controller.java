package assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;

public class Controller {
    @FXML
    private TableView tabView;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField accuracy;
    @FXML
    private TextField precision;

    @FXML
    private void initialize(){
        DecimalFormat df = new DecimalFormat("0.00000");

        tabView.setItems(Trainer.fileData);
        //creating image and setting it to imageView
        Image image = new Image(getClass().getClassLoader().getResource("images/antispam.png").toString());
        imageView.setImage(image);
        accuracy.setText(df.format(Trainer.accuracy));
        precision.setText(df.format(Trainer.precision));
    }


}
