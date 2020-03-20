package CodeCopy;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {

    @FXML
    AnchorPane anchor;
    private double xOffset, yOffset;

    private BufferedReader fin;
    private String finalTxt;

    @FXML
    public void initialize(){
        anchor.setBackground(new Background(new BackgroundFill(Color.AQUA, new CornerRadii(1),new Insets(0,0,0,0))));
        anchor.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                xOffset = anchor.getScene().getWindow().getX() - event.getScreenX();
                yOffset = anchor.getScene().getWindow().getY() - event.getScreenY();
            }
        });
        anchor.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                anchor.getScene().getWindow().setX(event.getScreenX() + xOffset);
                anchor.getScene().getWindow().setY(event.getScreenY() + yOffset);
            }
        });
        //get data        
        try {
            String strLine = ""; 
            String txt = "";
            fin = new BufferedReader(new FileReader("conf"));
            while ((strLine = fin.readLine()) != null) {
                txt += (strLine+"\n");
            }
            fin.close();
            finalTxt = txt;
        } catch (IOException e1) {
            //here should open a file diag
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    String txt;
                    while(true){
                        try{  
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Choose a configuration file...");
                            File fid = fileChooser.showOpenDialog(anchor.getScene().getWindow());
                            if(fid != null){
                                fin = new BufferedReader(new FileReader(fid));
                            } else {
                                throw new IOException();
                            }
                            String strLine = "";
                            txt = "";
                            while ((strLine = fin.readLine()) != null) {
                                txt += (strLine+"\n");
                            }
                            fin.close();
                            break;
                        }catch (IOException e2){
                            Alert alert = new Alert(AlertType.WARNING,"Configuration Error", ButtonType.OK);
                            alert.showAndWait();
                        }
                    }
                    finalTxt = txt;
                }
            });
            
        }    

        


        //create the content menu
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem item1 = new MenuItem("copy C file comment...");
        final MenuItem item2 = new MenuItem("copy C function comment...");
        final MenuItem item3 = new MenuItem("copy H file comment...");
        final MenuItem item4 = new MenuItem("copy H function comment...");
        final MenuItem item5 = new MenuItem("Exit");

        

        contextMenu.getItems().addAll(item1, item2, item3, item4, item5);
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String partText;
                partText = finalTxt.substring(finalTxt.indexOf("[C-head]")+9);
                partText = partText.substring(0,partText.indexOf("[end]"));

                //copy to clapboard
                copyToClipBoard(partText);
            }
        });
        item3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String partText;
                partText = finalTxt.substring(finalTxt.indexOf("[H-head]")+9);
                partText = partText.substring(0,partText.indexOf("[end]"));

                //copy to clapboard
                copyToClipBoard(partText);
            }
        });
        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String partText;
                partText = finalTxt.substring(finalTxt.indexOf("[C-func]")+9);
                partText = partText.substring(0,partText.indexOf("[end]"));

                //copy to clapboard
                copyToClipBoard(partText);
            }
        });
        item4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String partText;
                partText = finalTxt.substring(finalTxt.indexOf("[H-func]")+9);
                partText = partText.substring(0,partText.indexOf("[end]"));

                //copy to clapboard
                copyToClipBoard(partText);
            }
        });
        item5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) anchor.getScene().getWindow();
                stage.close();
            }
        });

        anchor.setOnContextMenuRequested(e->contextMenu.show(anchor,e.getScreenX(),e.getScreenY()));
    }


    private void copyToClipBoard(String txt){
        StringSelection selec= new StringSelection(txt);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selec, selec);
    }
}
