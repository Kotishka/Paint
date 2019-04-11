import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * CSPaint is a small JavaFX Application that allows users to paint with other
 * small functions
 * 
 * @author Yaroslava Boyko
 * @version 1.0
 */

/**
 * Sources: https://code.makery.ch/blog/javafx-dialogs-official/
 * https://docs.oracle.com/javase/8/javafx/api/index.html
 * https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
 * http://java-buddy.blogspot.com/2013/04/free-draw-on-javafx-canvas.html
 */
public class CSPaint extends Application {

    // initialization of main layout
    private VBox root = new VBox();
    private HBox main = new HBox(15);
    private Canvas canvas = new Canvas(650, 450);
    private VBox sideBar = new VBox(15);
    private HBox incDec = new HBox();
    private HBox statistics = new HBox(15);
    private StackPane whitePane = new StackPane();

    // initialization of final classes
    private final ToggleGroup group = new ToggleGroup();
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final Separator separator = new Separator();
    private final Separator separator1 = new Separator();

    // initialization of javafx ui based on logical order

    // Radio Buttons / First Section
    private Label chooseDrawingTool = new Label("Choose your drawing tool:");
    private RadioButton draw = new RadioButton("Draw");
    private RadioButton erase = new RadioButton("Erase");
    private RadioButton circle = new RadioButton("Circle");
    private RadioButton gradient = new RadioButton("Suprise!");

    // Increase and Decrease Section
    private Label strokeLabel = new Label("Current Line Width: " + gc.getLineWidth());
    private double currentLineWidth = gc.getLineWidth();
    private Label currentDiameterLabel = new Label("Current Circle Diameter: 30");
    private int currentDiameter = 30;
    private Button increase = new Button("Increase");
    private Button decrease = new Button("Decrease");

    // Change Color Section
    private Label colorLabel = new Label("Enter a color + ENTER:");
    private Color currentColor = Color.BLACK;
    private TextField colorEnter = new TextField();
    private Alert errorAlert = new Alert(AlertType.ERROR);

    // Clear Canvas Section
    private Button clearCanvas = new Button("Clear Canvas");

    // Statistics Section
    private int numberofShapes = 0;
    private Label mousePosition = new Label("Current Mouse Position: (0.0, 0.0)");
    private Label shapesLabel = new Label("Number of Shapes: 0");

    // Suprise Fun Section
    private Color colorMix1 = Color.RED;
    private Color colorMix2 = Color.BLUE;
    private Boolean firstColor = true;

    /**
     * Main method that launches the Application
     * 
     * @param args a String[] array
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // add mouse events to canvas
        canvas.setOnMousePressed(e -> mousePressed(e));
        canvas.setOnMouseDragged(e -> mouseDragged(e));
        canvas.addEventHandler(MouseEvent.ANY, e -> mouseAny(e));

        // canvas to whitePane for the white look.
        whitePane.getChildren().addAll(canvas);
        whitePane.setStyle("-fx-background-color:white");

        // add to main, a hbox the sideBar and whitePane
        main.getChildren().addAll(sideBar, whitePane);

        // Add the statistics to its own hbox
        statistics.getChildren().addAll(mousePosition, shapesLabel);

        // Add main and statistics into root, the VBox so the statistics are at the
        // bottom
        root.getChildren().addAll(main, statistics);

        // add width Region, based on the canvas size not the pixel location
        whitePane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // set sidebar with prefwidth so it doesn't overflow and padding for ui
        sideBar.setPrefWidth(180);
        sideBar.setPadding(new Insets(5, 0, 0, 10));

        // Add button toggle to have one at a time
        draw.setToggleGroup(group);
        erase.setToggleGroup(group);
        circle.setToggleGroup(group);
        gradient.setToggleGroup(group);

        // this is for the special surpirse! Changes labels
        draw.setOnAction(e -> changeLabel());
        erase.setOnAction(e -> changeLabel());
        circle.setOnAction(e -> changeLabel());
        gradient.setOnAction(e -> changeLabel());

        /*
         * Increase and Decrease are under the size of the stroke/circle Added events
         * based on the click of a button and width for ui
         */
        increase.setPrefWidth(75);
        increase.setOnAction(e -> increaseLength());
        decrease.setPrefWidth(75);
        decrease.setOnAction(e -> decreaseLength());

        // add children to hbox so it stays together
        incDec.getChildren().addAll(increase, decrease);

        // add the Color Enter text width and eventListener
        colorEnter.setMaxWidth(150);
        colorEnter.setOnKeyPressed(e -> enterKeyPressed(e));

        // clear canvas to blank and added width to button for ui reasons
        clearCanvas.setPrefWidth(150);
        clearCanvas.setOnAction(e -> clearCanvas());

        // add children to sidebar vbox to display
        sideBar.getChildren().addAll(chooseDrawingTool, draw, erase, circle, gradient, separator, strokeLabel,
                currentDiameterLabel, incDec, separator1, colorLabel, colorEnter, clearCanvas);

        // seperators for ui reasons
        separator.setMaxWidth(180);
        separator1.setMaxWidth(180);

        // start primaryStage
        primaryStage.setScene(new Scene(root, 830, 465));
        primaryStage.setTitle("CSPaint");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // First Getters and Setters for basic variables

    private Color getColor() {
        return this.currentColor;
    }

    private void setColor(Color c) {
        this.currentColor = c;
    }

    /*
     * the difference between color and colorMix is that color is for lines and
     * circles and colorMix is for the gradient surprise.
     */

    private Color getColorMix1() {
        return this.colorMix1;
    }

    private void setColorMix1(Color c) {
        this.colorMix1 = c;
    }

    private Color getColorMix2() {
        return this.colorMix2;
    }

    private void setColorMix2(Color c) {
        this.colorMix2 = c;
    }

    // number of shapes is for the statistics at the bottom.

    private int getNumberOfShapes() {
        return this.numberofShapes;
    }

    private void setNumberOfShapes(int shapes) {
        this.numberofShapes = shapes;
    }

    /* Events start here */

    private void mousePressed(MouseEvent e) {
        if (draw.isSelected()) {
            gc.setStroke(getColor());
            gc.getLineWidth();
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        }

        if (erase.isSelected()) {
            setColor(currentColor.WHITE);
            gc.setStroke(getColor());
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        }

        if (circle.isSelected()) {
            gc.setFill(getColor());

            // Subtracted currentDiameter/2 from x and y so the mouse is sot of in the
            // middle of the circle
            gc.fillOval(e.getX() - currentDiameter / 2, e.getY() - currentDiameter / 2, currentDiameter,
                    currentDiameter);
            numberofShapes++;

            // Reset label to have accurate number of shapes
            shapesLabel.setText("Number of Shapes: " + getNumberOfShapes());
        }

        if (gradient.isSelected()) {
            Color rgColor1 = getColorMix1();
            Color rgColor2 = getColorMix2();

            /*
             * Constructor: RadialGradient(double focusAngle, double focusDistance, double
             * centerX, double centerY, double radius, boolean proportional, CycleMethod
             * cycleMethod, Stop... stops) Can add more stop's for extra color fun
             */
            RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REFLECT,
                    new Stop(0.0, rgColor1), new Stop(1.0, rgColor2));

            gc.setStroke(rg);
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        }
    }

    private void mouseDragged(MouseEvent e) {
        if (draw.isSelected() || erase.isSelected() || gradient.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        }
    }

    // mouseAny event to capture statistics and to change mouse cursor!
    private void mouseAny(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        // if x and y are above 0, set mouse Position text to current location
        if (x >= 0 && y >= 0) {
            mousePosition.setText(
                    "Current Mouse Position: (" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")");
        } else {
            mousePosition.setText("Current Mouse Position: (0, 0)");
        }

        // based on the radiobutton chosen, select cursor on CANVAS only
        if (draw.isSelected()) {
            canvas.setCursor(Cursor.CROSSHAIR);
        } else if (erase.isSelected()) {
            canvas.setCursor(Cursor.CROSSHAIR);
        } else if (circle.isSelected()) {
            canvas.setCursor(Cursor.OPEN_HAND);
        } else if (gradient.isSelected()) {
            canvas.setCursor(Cursor.WAIT);
        } else {
            canvas.setCursor(Cursor.NONE);
        }
    }

    // key pressing events
    private void enterKeyPressed(KeyEvent e) {
        // if the key is an ENTER then procede
        if (e.getCode().equals(KeyCode.ENTER)) {
            // try catch block to make sure color is entered correctly
            try {
                setColor(currentColor.valueOf(colorEnter.getText()));
                /*
                 * valueOf will return an IllegalArgumentException if the color does not exist
                 * in the color java api
                 */
            } catch (IllegalArgumentException argE) {
                // error alert will sent out if the IllegalArgumentException was caught
                errorAlert.setTitle("Incorrect Color Dialog");
                errorAlert.setHeaderText("Incorrect Color");
                errorAlert.setContentText("Please select a valid color.");
                errorAlert.showAndWait();
            }
            // clear text block to make ui experience better
            colorEnter.clear();
        }

        // same thing here except with SHIFT and 2 colors
        if (e.getCode().equals(KeyCode.SHIFT)) {

            // boolean that switches off and tells us if this is the first color
            if (firstColor) {
                try {
                    setColorMix1(colorMix1.valueOf(colorEnter.getText()));
                    firstColor = false;
                } catch (IllegalArgumentException argE) {
                    errorAlert.setTitle("Incorrect Color Dialog");
                    errorAlert.setHeaderText("Incorrect Color");
                    errorAlert.setContentText("Please select a valid color.");
                    errorAlert.showAndWait();
                }
            } else {
                try {
                    setColorMix2(colorMix2.valueOf(colorEnter.getText()));
                    firstColor = true;
                } catch (IllegalArgumentException argE) {
                    errorAlert.setTitle("Incorrect Color Dialog");
                    errorAlert.setHeaderText("Incorrect Color");
                    errorAlert.setContentText("Please select a valid color.");
                    errorAlert.showAndWait();
                }
            }
            colorEnter.clear();
        }
    }

    // increases leangth to line width by 1 OR circle diameter by 5
    private void increaseLength() {
        if (draw.isSelected() || erase.isSelected() || gradient.isSelected()) {
            currentLineWidth++;
            gc.setLineWidth(currentLineWidth);
            strokeLabel.setText("Current Line Width: " + gc.getLineWidth());
        }
        if (circle.isSelected()) {
            currentDiameter += 5;
            currentDiameterLabel.setText("Current Circle Diameter: " + currentDiameter);
        }
    }

    // increases leangth to line width by 1 OR circle diameter by 5 if not less than
    // 1 and 5 respectively
    private void decreaseLength() {
        if (draw.isSelected() || erase.isSelected() || gradient.isSelected()) {
            if (gc.getLineWidth() < 1) {
                return;
            } else {
                currentLineWidth--;
                gc.setLineWidth(currentLineWidth);
            }
            strokeLabel.setText("Current Line Width: " + gc.getLineWidth());
        }
        if (circle.isSelected()) {
            if (currentDiameter <= 5) {
                return;
            } else {
                currentDiameter = currentDiameter - 5;
                currentDiameterLabel.setText("Current Circle Diameter: " + currentDiameter);
            }
        }
    }

    // changing the label based on if the surprise toggle is selected
    private void changeLabel() {
        if (gradient.isSelected()) {
            colorLabel.setText("Enter a color + ENTER:" + "\nEnter two colors + SHFT \nfor a surprise!");
        }
        if (!gradient.isSelected()) {
            colorLabel.setText("Enter a color + ENTER:");
        }
    }

    private void clearCanvas() {
        // clears canvas based on x,y,width,height
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // sets shapes to 0 again because now they are missing!
        setNumberOfShapes(0);
        shapesLabel.setText("Number of Shapes: " + getNumberOfShapes());
    }
}