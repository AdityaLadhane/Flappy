import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image gameOverImg; // New Game Over background image

    // Bird class
    int birdX = boardWidth / 8;
    int birdY = boardWidth / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4; // Move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; // Move bird up/down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    int highScore = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        gameOverImg = new ImageIcon(getClass().getResource("./Gameover.jpg")).getImage(); // Load Game Over image

        // Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // Game loop timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameOver) {
            // Draw Game Over background
            g.drawImage(gameOverImg, 0, 0, boardWidth, boardHeight, null);

            // Draw Game Over text
            g.setColor(Color.red); // Custom text color
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 36)); // Custom font
            g.drawString("Game Over!", boardWidth / 4, boardHeight / 2 - 50);
            g.setColor(Color.white); // Custom color for other text
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Score: " + (int) score, boardWidth / 4, boardHeight / 2);
            g.drawString("High Score: " + highScore, boardWidth / 4, boardHeight / 2 + 30);
            g.drawString("Press Enter to Restart", boardWidth / 8, boardHeight / 2 + 100);
        } else {
            // Draw normal background
            g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

            // Draw bird
            g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

            // Draw pipes
            for (Pipe pipe : pipes) {
                g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
            }

            // Draw score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        if (!gameOver) {
            velocityY += gravity;
            bird.y += velocityY;
            bird.y = Math.max(bird.y, 0);

            for (Pipe pipe : pipes) {
                pipe.x += velocityX;

                if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                    score += 0.5;
                    pipe.passed = true;
                }

                if (collision(bird, pipe)) {
                    gameOver = true;
                    highScore = Math.max(highScore, (int) score);
                }
            }

            if (bird.y > boardHeight) {
                gameOver = true;
                highScore = Math.max(highScore, (int) score);
            }
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && gameOver) {
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            gameOver = false;
            score = 0;
            gameLoop.start();
            placePipeTimer.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
