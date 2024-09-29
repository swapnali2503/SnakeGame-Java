import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private final int DOT_SIZE = 10;
    private int delay = 140; // Starting delay
    private final ArrayList<Point> snake;
    private final ArrayList<Point> obstacles;
    private Point food;
    private char direction;
    private boolean running;
    private boolean paused;
    private int score;
    private Timer timer;
    private boolean showInstructions; // To toggle instructions

    public SnakeGame() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (showInstructions) {
                    // Start the game when any key is pressed
                    showInstructions = false;
                    startGame();
                    spawnFood();
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            if (direction != 'R') direction = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != 'L') direction = 'R';
                            break;
                        case KeyEvent.VK_UP:
                            if (direction != 'D') direction = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != 'U') direction = 'D';
                            break;
                        case KeyEvent.VK_R:
                            if (!running) {
                                startGame();
                                spawnFood();
                            }
                            break;
                        case KeyEvent.VK_P:
                            paused = !paused; // Toggle pause
                            break;
                    }
                }
            }
        });

        snake = new ArrayList<>();
        obstacles = new ArrayList<>();
        direction = 'R';
        running = false;
        paused = false;
        showInstructions = true; // Show instructions at the start

        timer = new Timer(delay, this);
        timer.start();
        spawnFood();
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(100, 100));
        direction = 'R';
        running = true;
        score = 0; // Reset score
        spawnObstacles(); // Initialize obstacles
    }

    private void spawnFood() {
        Random rand = new Random();
        food = new Point(rand.nextInt(WIDTH / DOT_SIZE) * DOT_SIZE,
                         rand.nextInt(HEIGHT / DOT_SIZE) * DOT_SIZE);
    }

    private void spawnObstacles() {
        obstacles.clear();
        obstacles.add(new Point(200, 200)); // Example obstacle
        obstacles.add(new Point(300, 300)); // Another obstacle
    }

    private void adjustDifficulty() {
        if (score > 0 && score % 50 == 0) {
            // Every 50 points, reduce the delay (increase speed)
            if (delay > 50) {
                delay -= 10; // Speed up
                timer.setDelay(delay); // Update timer delay
            }
        }
    }

    private void showIntro(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.BOLD, 20));
        g.drawString("Welcome to Snake Game!", WIDTH / 4, HEIGHT / 4);
        g.setFont(new Font("Helvetica", Font.PLAIN, 16));
        g.drawString("Use Arrow Keys to Move", WIDTH / 4, HEIGHT / 4 + 40);
        g.drawString("Eat the red dots (Food)", WIDTH / 4, HEIGHT / 4 + 70);
        g.drawString("Avoid the blue dots (Obstacles)", WIDTH / 4, HEIGHT / 4 + 100);
        g.drawString("Press any key to Start", WIDTH / 4, HEIGHT / 4 + 130);
        g.drawString("Press P to Pause", WIDTH / 4, HEIGHT / 4 + 160);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showInstructions) {
            showIntro(g); // Show introductory instructions
        } else if (running) {
            g.setColor(Color.red);
            g.fillRect(food.x, food.y, DOT_SIZE, DOT_SIZE);

            g.setColor(Color.green);
            for (Point point : snake) {
                g.fillRect(point.x, point.y, DOT_SIZE, DOT_SIZE);
            }

            g.setColor(Color.blue); // Color for obstacles
            for (Point obstacle : obstacles) {
                g.fillRect(obstacle.x, obstacle.y, DOT_SIZE, DOT_SIZE);
            }
        } else {
            showGameOver(g);
            
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void showGameOver(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.BOLD, 14));
        if (paused) {
            g.drawString("Paused! Press P to Resume", WIDTH / 4, HEIGHT / 2 - 20);
        } else {
            g.drawString("Game Over! Score: " + score + " Press R to Restart", WIDTH / 4, HEIGHT / 2);
            g.drawString("Avoid the blue obstacles! Eat the red food!", WIDTH / 4, HEIGHT / 2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) { // Only update if not paused
            move();
            checkCollision();
            checkFood();
            adjustDifficulty();
        }
        repaint();
    }

    private void move() {
        Point head = new Point(snake.get(0));
        switch (direction) {
            case 'U': head.y -= DOT_SIZE; break;
            case 'D': head.y += DOT_SIZE; break;
            case 'L': head.x -= DOT_SIZE; break;
            case 'R': head.x += DOT_SIZE; break;
        }
        snake.add(0, head);
        if (snake.size() > 3) {
            snake.remove(snake.size() - 1); // Remove tail
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);
        // Check for wall collision
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }
        // Check for self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }
        // Check for obstacle collision
        for (Point obstacle : obstacles) {
            if (head.equals(obstacle)) {
                running = false;
            }
        }
    }

    private void checkFood() {
        if (snake.get(0).equals(food)) {
            spawnFood();
            score += 10; // Increase score by 10
            snake.add(new Point(0, 0)); // Grow snake
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
