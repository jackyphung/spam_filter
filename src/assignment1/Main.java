package assignment1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Spam Detector!");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));

        File mainDirectory = directoryChooser.showDialog(primaryStage);

        Trainer trainer = new Trainer();

        //running training program before the scene
        trainer.parseFile(new File("resources\\train"));
        trainer.probabilitySpamCalculator();
        trainer.probabilityHamCalculator();
        trainer.probabilityOfSpamCalculator();
        trainer.parseTestFile(mainDirectory);
        trainer.accuracyCalculator();
        trainer.precisionCalculator();
        trainer.debugMessages();

        //initialize fxml
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
