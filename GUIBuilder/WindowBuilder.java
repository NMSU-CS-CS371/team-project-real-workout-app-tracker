import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WindowBuilder extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Awesome Window");

        Pane pane = new Pane();
        pane.setPrefSize(1118, 569);
        pane.setStyle("-fx-background-color: #1e1e1e;");

        Label element2 = new Label("");
        element2.setLayoutX(76.421875);
        element2.setLayoutY(27);
        element2.setPrefWidth(287);
        element2.setPrefHeight(159);
        element2.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lato.ttf"), 14.00));
        element2.setStyle("-fx-text-fill: #D9D9D9;");
        pane.getChildren().add(element2);

        Scene scene = new Scene(pane, 1118, 569);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}