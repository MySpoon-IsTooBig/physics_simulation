import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private double y = 100;
    private double vy = 0;

    private double x = 100;
    private double vx = 0;

    private final double g = 500;

    private static final double WIDTH = 600;
    private static final double HEIGHT = 400;

    private boolean dragging = false;
    private double mouseEndX, mouseEndY;
    double mouseStartX, mouseStartY;
    double mouseX = 0;
    double mouseY = 0;
    double offsetX, offsetY;
    long lastMouseTime;


    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        Circle ball = new Circle(30, Color.RED);
        ball.setCenterX(x);
        ball.setCenterY(y);

        // Debug text
        Text debug = new Text(10, 20, "");
        debug.setStyle("-fx-font-size: 16;");

        root.getChildren().addAll(ball, debug);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Physics Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnMousePressed(e -> {
            mouseX = e.getX();
            mouseY = e.getY();

            double dx = e.getX() - x;
            double dy = e.getY() - y;
            if( dx*dx + dy*dy <= ball.getRadius() * ball.getRadius() ) {
                vy = 0;
                vx = 0;
                offsetX = dx;
                offsetY = dy;
                mouseStartX = e.getX();
                mouseStartY = e.getX();
                lastMouseTime = System.nanoTime();
                dragging = true;

            }




        });

        scene.setOnMouseDragged(e -> {
            if(dragging) {
                x = e.getX() - offsetX;
                y = e.getY() - offsetY;
            }
            mouseEndX = e.getX();
            mouseEndY = e.getY();

        });

        scene.setOnMouseReleased(e -> {
            if(dragging) {

                dragging = false;

            }






        });


        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                // ---- Physics ----
                if (!dragging) {
                    vy += g * dt;
                    y += vy * dt;
                    x += vx * dt;
                }



                // Floor collision
                if (y + ball.getRadius() > HEIGHT) {
                    y = HEIGHT - ball.getRadius();
                    vy = -vy * 0.7;
                }

                // Ceiling collision
                if (y - ball.getRadius() < 0) {
                    y = ball.getRadius();
                    vy = -vy * 0.7;
                }

                // Right wall collision
                if (x + ball.getRadius() > WIDTH) {
                    x = WIDTH - ball.getRadius();
                    vx = -vx * 0.7;
                }

                // Left wall collision
                if (x - ball.getRadius() < 0) {
                    x = ball.getRadius();
                    vx = -vx * 0.7;
                }

                // Apply position
                ball.setCenterX(x);
                ball.setCenterY(y);

                // Update debug info
                debug.setText(String.format(
                        "x  = %.2f\nvx = %.2f\ny  = %.2f\nvy = %.2f\ndt = %.4f\nmouseX = %.2f\nmouseY = %.2f",
                        x, vx, y, vy, dt, mouseX, mouseY
                ));
            }
        };

        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
