package com.snakegame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class App extends Application {

    GraphicsContext gc;
    private int speedX = 2;
    private int speedY = 0;
    private int score = 0;
    private List<Segment> snake = new ArrayList<>();
    private int foodX;
    private int foodY;
    private boolean eaten = false;
    private boolean gameOver = false;
    URL food = getClass().getResource("/sounds/food.wav");
    URL gameover = getClass().getResource("/sounds/gameover.wav");
    private static Scene scene;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(800, 500);
        gc = canvas.getGraphicsContext2D();

        snake.add(new Segment(50, 50));

        AudioClip foodsound = new AudioClip(food.toExternalForm());
        foodsound.setVolume(0.8);
        foodsound.play(0.0);

        AudioClip gameoversound = new AudioClip(gameover.toExternalForm());
        gameoversound.setVolume(0.8);
        gameoversound.play(0.0);

        renderFood();
        startGameloop(foodsound, gameoversound);

        Pane pane = new Pane(canvas);
        pane.setStyle("-fx-background-color: black");
        scene = new Scene(pane, 800, 500);
        controls(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void render() {
        gc.clearRect(0, 0, 800, 500);

        gc.setFill(Color.YELLOW);
        gc.fillRoundRect(foodX, foodY, 20, 20, 20, 20);

        gc.setFill(Color.GREEN);
        for (Segment part : snake) {
            gc.fillRoundRect(part.x, part.y, 20, 20, 8, 8);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        gc.fillText("SCORE " + score, 10, 20);
    }

    public void renderFood() {
        Random random = new Random();
        foodX = random.nextInt(780);
        foodY = random.nextInt(480);
        eaten = false;
    }

    public void updateSnake() {
        Segment head = snake.get(0);

        int newX = head.x + speedX;
        int newY = head.y + speedY;

        snake.add(0, new Segment(newX, newY));

        if (!eaten) {
            snake.remove(snake.size() - 1);
        }
    }

    public void checkCollision() {
        Segment head = snake.get(0);

        if (head.x < 0 ||
                head.x >= 780 ||
                head.y < 0 ||
                head.y >= 480) {

            gameOver = true;
        }

        for (int i = 20; i < snake.size(); i++) {
            Segment part = snake.get(i);
            if (head.x < part.x + 20 && head.x + 20 > part.x &&
                    head.y < part.y + 20 && head.y + 20 > part.y) {
                gameOver = true;
                break;
            }
        }
    }

    public void checkEaten() {
        Segment head = snake.get(0);

        if (head.x < foodX + 20 &&
                head.x + 20 > foodX &&
                head.y < foodY + 20 &&
                head.y + 20 > foodY) {

            eaten = true;
            score += 2;

            Segment tail = snake.get(snake.size() - 1);
            for (int i = 0; i < 10; i++) {
                snake.add(new Segment(tail.x, tail.y));
            }
        }
    }

    public void controls(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.RIGHT && speedX != -2) {
                speedX = 2;
                speedY = 0;
            } else if (e.getCode() == KeyCode.LEFT && speedX != 2) {
                speedX = -2;
                speedY = 0;
            } else if (e.getCode() == KeyCode.UP && speedY != 2) {
                speedY = -2;
                speedX = 0;
            } else if (e.getCode() == KeyCode.DOWN && speedY != -2) {
                speedY = 2;
                speedX = 0;
            } else if (e.getCode() == KeyCode.SPACE) {
                if (gameOver)
                    restartGame();
            }
        });
    }

    public void startGameloop(AudioClip foodsound, AudioClip gameoversound) {
        AnimationTimer gameloop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver) {
                    gc.setFill(Color.RED);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
                    gc.fillText("GAME OVER", 280, 220);

                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Arial", 18));
                    gc.fillText("Press SPACE to restart", 295, 260);
                    return;
                }
                checkCollision();
                if (gameOver) {
                    gameoversound.play();
                }
                checkEaten();
                updateSnake();
                render();
                if (eaten) {
                    foodsound.play();
                    renderFood();
                }
            }
        };
        gameloop.start();
    }

    public void restartGame() {

        gameOver = false;
        eaten = false; 
        snake.clear();

        snake.add(new Segment(50, 50));
        speedX = 2;
        speedY = 0;
        renderFood();
    }

    public static void main(String[] args) {
        launch();
    }

}

class Segment {
    int x;
    int y;

    Segment(int x, int y) {
        this.x = x;
        this.y = y;
    }
}