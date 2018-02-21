package redboxman_javafx;

import java.io.InputStream;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author McKillaGorilla
 */
public class RedBoxManRenderer extends Application {
    Canvas canvas;
    GraphicsContext gc;
    ArrayList<Point2D> imagesRedBoxManLocations;
    ArrayList<Point2D> shapesRedBoxManLocations;
    Image redBoxManImage;
    
    @Override
    public void start(Stage primaryStage) {
	// INIT THE DATA MANAGERS
	imagesRedBoxManLocations = new ArrayList<>();
	shapesRedBoxManLocations = new ArrayList<>();
	
	// LOAD THE RED BOX MAN IMAGE
        InputStream str = getClass().getResourceAsStream("/RedBoxMan.png");
	redBoxManImage = new Image(str);
	
	// MAKE THE CANVAS
	canvas = new Canvas();
	canvas.setStyle("-fx-background-color: cyan");
	gc = canvas.getGraphicsContext2D();

	// PUT THE CANVAS IN A CONTAINER
	Group root = new Group();
	root.getChildren().add(canvas);
	
	canvas.setOnMouseClicked(e->{
	    if (e.isShiftDown()) {
		shapesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else if (e.isControlDown()) {
		imagesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else {
		clear();
	    }
	});
	
	// PUT THE CONTAINER IN A SCENE
	Scene scene = new Scene(root, 800, 600);
	canvas.setWidth(scene.getWidth());
	canvas.setHeight(scene.getHeight());

	// AND START UP THE WINDOW
	primaryStage.setTitle("Red Box Man Renderer");
	primaryStage.setScene(scene);
	primaryStage.show();
    }
    
    public void clearCanvas() {
	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void clear() {
	shapesRedBoxManLocations.clear();
	imagesRedBoxManLocations.clear();
	render();
    }
    
    public void render() {
	clearCanvas();
	for (int i = 0; i < shapesRedBoxManLocations.size(); i++) {
	    renderShapeRedBoxMan(shapesRedBoxManLocations.get(i));
	}
	for (int j = 0; j < imagesRedBoxManLocations.size(); j++) {
	    renderImageRedBoxMan(imagesRedBoxManLocations.get(j));
	}
    }
    
    public void renderShapeRedBoxMan(Point2D location) {
	String headColor = "#DD0000";
	String outlineColor = "#000000";
	int headW = 115;
	int headH = 88;
    
        String eyeColor = "#FFFF00";
	int eyeW = 33;
	int eyeH = 26;
        
	// DRAW HIS RED HEAD
        gc.setFill(Paint.valueOf(headColor));
	gc.fillRect(location.getX(), location.getY(), headW, headH);
        gc.beginPath();
	gc.setStroke(Paint.valueOf(outlineColor));
	gc.setLineWidth(1);
	gc.rect(location.getX(), location.getY(), headW, headH);
	gc.stroke();
	
	// AND THEN DRAW THE REST OF HIM
        // draw eyes w/o pupils
        gc.setFill(Paint.valueOf(eyeColor));
	gc.fillRect(location.getX()+15, location.getY()+13, eyeW, eyeH);
        gc.beginPath();
	gc.setStroke(Paint.valueOf(outlineColor));
	gc.setLineWidth(1);
	gc.rect(location.getX()+15, location.getY()+13, eyeW, eyeH);
	gc.stroke();
        
        gc.fillRect(location.getX()+72, location.getY()+13, eyeW, eyeH);
        gc.rect(location.getX()+72, location.getY()+13, eyeW, eyeH);
        gc.stroke();
        
        // draw pupils
        gc.setFill(Paint.valueOf(outlineColor));
	gc.fillRect(location.getX()+30, location.getY()+22, 6, 6);
//        gc.beginPath();
//	gc.setStroke(Paint.valueOf(outlineColor));
//	gc.setLineWidth(1);
	gc.rect(location.getX()+30, location.getY()+22, 6, 6);
	gc.stroke();
        
        gc.fillRect(location.getX()+87, location.getY()+22, 6, 6);
        gc.rect(location.getX()+87, location.getY()+22, 6, 6);
        gc.stroke();
        
        // draw mouth
	gc.fillRect(location.getX()+22, location.getY()+65, 80, 8);
	gc.rect(location.getX()+22, location.getY()+65, 80, 8);
	gc.stroke();
        
        // draw upper body
        gc.fillRect(location.getX()+30, location.getY()+88, 55, 20);
	gc.rect(location.getX()+30, location.getY()+88, 55, 20);
	gc.stroke();
        
        // draw lower body
        gc.fillRect(location.getX()+34, location.getY()+108, 45, 10);
	gc.rect(location.getX()+34, location.getY()+108, 45, 10);
	gc.stroke();
        
        // draw feet
        gc.fillRect(location.getX()+30, location.getY()+118, 10, 10);
	gc.rect(location.getX()+30, location.getY()+118, 10, 10);
	gc.stroke();
        
        gc.fillRect(location.getX()+73, location.getY()+118, 10, 10);
	gc.rect(location.getX()+73, location.getY()+118, 10, 10);
	gc.stroke();
        
        
    }
    
    public void renderImageRedBoxMan(Point2D location) {
	gc.drawImage(redBoxManImage, location.getX(), location.getY());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
    
}
