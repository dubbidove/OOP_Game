import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

// =====================================================
// ABSTRACT SCREEN
// =====================================================
abstract class GameScreen extends JFrame {
    public abstract void display();
}

// =====================================================
// ABSTRACT GAME ENTITY
// =====================================================
abstract class GameEntity {
    protected int x, y, width, height;

    public GameEntity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        // Only collide on lower body (torso+legs), so jumping overhead clears cars
        if (this instanceof Player) {
            return new Rectangle(x + 14, y + 30, 28, 55);
        }
        // Cars: tight box matching visible car body only
        return new Rectangle(x + 8, y + 10, width - 16, height - 14);
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2);
}

// =====================================================
// PLAYER (stick figure character)
// =====================================================
class Player extends GameEntity {
    private int velocityY = 0;
    private boolean jumping = false;

    private static final int GROUND_Y = 340;

    public Player() {
        super(100, GROUND_Y, 55, 90);
    }

    public void jump() {
        if (!jumping) {
            velocityY = -20;
            jumping = true;
        }
    }

    @Override
    public void update() {
        if (jumping) {
            y += velocityY;
            velocityY += 1;
            if (y >= GROUND_Y) {
                y = GROUND_Y;
                velocityY = 0;
                jumping = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);

        // Shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(5, 84, 45, 8);

        // Shirt (body)
        g.setColor(new Color(30, 100, 220));
        g.fillRoundRect(13, 32, 30, 30, 6, 6);

        // Pants
        g.setColor(new Color(40, 40, 120));
        g.fillRect(13, 58, 12, 22);
        g.fillRect(30, 58, 12, 22);

        // Shoes
        g.setColor(new Color(60, 40, 20));
        g.fillRoundRect(9, 76, 16, 8, 4, 4);
        g.fillRoundRect(30, 76, 16, 8, 4, 4);

        // Skin
        g.setColor(new Color(255, 210, 160));

        // Head
        g.fillOval(14, 2, 28, 28);

        // Static arms (no swing)
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 210, 160));
        // Left arm — held naturally at side
        int[] lax = {13, 4, 10};
        int[] lay = {35, 45, 56};
        g.drawPolyline(lax, lay, 3);
        // Right arm — held naturally at side
        int[] rax = {42, 51, 45};
        int[] ray = {35, 45, 56};
        g.drawPolyline(rax, ray, 3);

        // Face details
        g.setStroke(new BasicStroke(1));
        // Eyes
        g.setColor(new Color(50, 30, 10));
        g.fillOval(20, 10, 5, 5);
        g.fillOval(31, 10, 5, 5);
        // Smile
        g.setColor(new Color(180, 80, 80));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(21, 17, 14, 8, 200, 140);

        // Hair
        g.setColor(new Color(60, 30, 10));
        g.fillArc(14, 2, 28, 18, 0, 180);

        g.setStroke(new BasicStroke(1));
        g.dispose();
    }
}

// =====================================================
// CAR (drawn realistically)
// =====================================================
class Car extends GameEntity {
    private int speed;
    private int resetX;
    private Color bodyColor;
    private Color accentColor;
    private String carType;

    public Car(int startX, int startY, int speed, int resetX, Color bodyColor, Color accentColor, String carType) {
        super(startX, startY, 120, 55);
        this.speed = speed;
        this.resetX = resetX;
        this.bodyColor = bodyColor;
        this.accentColor = accentColor;
        this.carType = carType;
    }

    @Override
    public void update() {
        x -= speed;
        if (x < -140) {
            x = resetX;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);

        if (carType.equals("suv")) {
            drawSUV(g);
        } else if (carType.equals("sports")) {
            drawSports(g);
        } else {
            drawSedan(g);
        }

        g.dispose();
    }

    private void drawSedan(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(10, 48, 100, 10);

        g.setColor(bodyColor);
        g.fillRoundRect(0, 28, 120, 24, 8, 8);

        int[] roofX = {22, 35, 85, 98, 115, 5};
        int[] roofY = {28, 8, 8, 28, 28, 28};
        g.fillPolygon(roofX, roofY, 6);

        g.setColor(new Color(180, 220, 255, 200));
        g.fillPolygon(new int[]{37, 43, 60, 60}, new int[]{10, 11, 11, 27}, 4);
        g.fillPolygon(new int[]{63, 63, 80, 86}, new int[]{11, 27, 27, 11}, 4);
        g.setColor(accentColor.darker());
        g.setStroke(new BasicStroke(2));
        g.drawLine(61, 11, 61, 27);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(200, 230, 255, 100));
        g.fillPolygon(new int[]{23, 35, 37, 5}, new int[]{27, 9, 27, 27}, 4);
        g.fillPolygon(new int[]{85, 98, 115, 83}, new int[]{9, 27, 27, 27}, 4);

        g.setColor(bodyColor.darker());
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(60, 27, 60, 50);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(80, 80, 90));
        g.fillRoundRect(-4, 36, 12, 12, 4, 4);
        g.fillRoundRect(112, 36, 12, 12, 4, 4);

        g.setColor(new Color(255, 255, 180));
        g.fillOval(108, 30, 10, 8);
        g.setColor(new Color(220, 50, 50));
        g.fillOval(2, 30, 10, 8);

        drawWheel(g, 18, 44);
        drawWheel(g, 84, 44);
    }

    private void drawSUV(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(10, 48, 100, 10);

        g.setColor(bodyColor);
        g.fillRoundRect(0, 14, 120, 38, 6, 6);

        g.setColor(new Color(180, 220, 255, 200));
        g.fillRect(20, 17, 26, 18);
        g.fillRect(50, 17, 26, 18);
        g.fillRect(80, 17, 22, 18);

        g.setColor(new Color(50, 50, 50));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(20, 17, 26, 18);
        g.drawRect(50, 17, 26, 18);
        g.drawRect(80, 17, 22, 18);

        g.setColor(new Color(70, 70, 70));
        g.fillRect(15, 13, 90, 3);

        g.setColor(new Color(60, 60, 65));
        g.fillRoundRect(-5, 30, 15, 18, 4, 4);
        g.fillRoundRect(110, 30, 15, 18, 4, 4);

        g.setColor(new Color(255, 255, 180));
        g.fillRect(108, 22, 8, 6);
        g.setColor(new Color(220, 50, 50));
        g.fillRect(4, 22, 8, 6);

        g.setColor(bodyColor.darker());
        g.setStroke(new BasicStroke(3));
        g.drawArc(8, 34, 30, 20, 0, 180);
        g.drawArc(78, 34, 30, 20, 0, 180);
        g.setStroke(new BasicStroke(1));

        drawWheel(g, 15, 44);
        drawWheel(g, 83, 44);
    }

    private void drawSports(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(10, 48, 100, 8);

        int[] bodyX = {0, 10, 30, 90, 115, 120, 120, 0};
        int[] bodyY = {44, 32, 22, 22, 30, 36, 52, 52};
        g.setColor(bodyColor);
        g.fillPolygon(bodyX, bodyY, 8);

        g.setColor(new Color(180, 220, 255, 200));
        g.fillPolygon(new int[]{32, 40, 65, 60}, new int[]{22, 24, 24, 22}, 4);
        g.fillPolygon(new int[]{68, 72, 88, 82}, new int[]{24, 22, 22, 30}, 4);

        g.setColor(accentColor);
        g.setStroke(new BasicStroke(3));
        g.drawLine(20, 38, 105, 38);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(50, 50, 50));
        g.fillRect(4, 30, 8, 4);
        g.fillRect(3, 28, 10, 3);

        g.setColor(new Color(255, 255, 180));
        g.fillPolygon(new int[]{110, 120, 120, 112}, new int[]{32, 36, 42, 38}, 4);
        g.setColor(new Color(220, 50, 50));
        g.fillPolygon(new int[]{0, 8, 6, 0}, new int[]{36, 38, 44, 42}, 4);

        g.setColor(new Color(80, 80, 90));
        g.fillRoundRect(115, 40, 8, 8, 2, 2);

        drawWheel(g, 18, 43);
        drawWheel(g, 82, 43);
    }

    private void drawWheel(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(30, 30, 30));
        g.fillOval(cx - 14, cy - 14, 28, 28);
        g.setColor(new Color(190, 190, 200));
        g.fillOval(cx - 9, cy - 9, 18, 18);
        g.setColor(new Color(130, 130, 140));
        g.fillOval(cx - 4, cy - 4, 8, 8);
        g.setColor(new Color(160, 160, 170));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72);
            int sx = cx + (int)(4 * Math.cos(angle));
            int sy = cy + (int)(4 * Math.sin(angle));
            int ex = cx + (int)(9 * Math.cos(angle));
            int ey = cy + (int)(9 * Math.sin(angle));
            g.drawLine(sx, sy, ex, ey);
        }
        g.setStroke(new BasicStroke(1));
    }
}

// =====================================================
// GAME PANEL (custom painted)
// =====================================================
class GamePanel extends JPanel {
    private Player player;
    private Car[] cars;
    private int score;

    public GamePanel(Player player, Car[] cars) {
        this.player = player;
        this.cars = cars;
        setLayout(null);
    }

    public void setScore(int score) { this.score = score; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sky
        g2.setColor(new Color(135, 206, 250));
        g2.fillRect(0, 0, 800, 300);

        // Clouds
        drawCloud(g2, 80, 60, 70);
        drawCloud(g2, 280, 40, 90);
        drawCloud(g2, 550, 70, 60);
        drawCloud(g2, 700, 45, 80);

        // Ground/Road
        g2.setColor(new Color(80, 80, 80));
        g2.fillRect(0, 300, 800, 220);

        // Road surface
        g2.setColor(new Color(100, 100, 100));
        g2.fillRect(0, 310, 800, 210);

        // Road lines
        g2.setColor(new Color(255, 220, 0));
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{30, 20}, score * 3));
        g2.drawLine(0, 395, 800, 395);
        g2.setStroke(new BasicStroke(1));

        // Road edge lines
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, 314, 800, 314);
        g2.drawLine(0, 500, 800, 500);
        g2.setStroke(new BasicStroke(1));

        for (Car car : cars) car.draw(g2);
        player.draw(g2);
    }

    private void drawCloud(Graphics2D g2, int x, int y, int size) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillOval(x, y, size, size / 2);
        g2.fillOval(x + size / 4, y - size / 4, size / 2, size / 2);
        g2.fillOval(x + size / 2, y, size / 2, size / 3);
    }
}

// =====================================================
// FIRST UI (welcome screen)
// =====================================================
class firstUI extends GameScreen {
    private JTextField usernameField;

    public firstUI() {
        setTitle("Run The Tale");
        setSize(800, 520);
        setLayout(null);
        setResizable(false);

        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Solid blue background
                g2.setColor(new Color(30, 90, 200));
                g2.fillRect(0, 0, 800, 520);
                // Subtle lighter blue overlay at top for depth
                g2.setColor(new Color(60, 130, 240, 120));
                g2.fillRect(0, 0, 800, 260);
            }
        };
        bg.setLayout(null);
        bg.setBounds(0, 0, 800, 520);
        add(bg);

        // Title card
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBounds(200, 100, 400, 280);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        bg.add(card);

        JLabel title = new JLabel("RUN THE TALE");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(30, 80, 180));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 20, 400, 50);
        card.add(title);

        JLabel sub = new JLabel("Dodge the cars. Survive the road.");
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 100, 110));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setBounds(0, 68, 400, 22);
        card.add(sub);

        JLabel usernameLabel = new JLabel("Enter your username:");
        usernameLabel.setForeground(new Color(50, 50, 60));
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        usernameLabel.setBounds(70, 108, 260, 26);
        card.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(70, 138, 260, 38);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        card.add(usernameField);

        // Single ENTER button, centered
        JButton enterButton = new JButton("ENTER");
        enterButton.setBounds(130, 200, 140, 46);
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));
        enterButton.setBackground(new Color(40, 120, 220));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enterButton.addActionListener(e -> openGame());
        card.add(enterButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void openGame() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username!");
            return;
        }
        dispose();
        new secondUI(username).display();
    }

    @Override
    public void display() { setVisible(true); }
}

// =====================================================
// SECOND UI (game screen)
// =====================================================
class secondUI extends GameScreen {
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JButton startButton;
    private JButton jumpButton;
    private Player player;
    private Car[] cars;
    private Timer gameLoop;
    private Timer countdown;
    private int score = 0;
    private int timeLeft = 15;
    private boolean running = false;
    private String username;

    public secondUI(String username) {
        this.username = username;
        initialize();
    }

    private void initialize() {
        setTitle("Run The Tale");
        setSize(800, 520);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        player = new Player();

        cars = new Car[]{
            new Car(750,  330, 5, 1050, new Color(200, 40,  40),  Color.WHITE,          "sedan"),
            new Car(980,  330, 4, 1280, new Color(40,  100, 200), new Color(180,220,255),"suv"),
            new Car(1250, 330, 6, 1500, new Color(30,  160, 80),  new Color(200,255,200),"sports"),
            new Car(1550, 330, 3, 1800, new Color(200, 140, 20),  Color.WHITE,           "sedan"),
        };

        gamePanel = new GamePanel(player, cars);
        gamePanel.setLayout(null);

        // HUD
        scoreLabel = new JLabel("SCORE: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setBounds(15, 10, 200, 30);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(0, 0, 0, 100));
        gamePanel.add(scoreLabel);

        timerLabel = new JLabel("TIME: 15");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setBounds(640, 10, 140, 30);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(0, 0, 0, 100));
        gamePanel.add(timerLabel);

        JLabel userLabel = new JLabel("Player: " + username);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(15, 46, 200, 22);
        userLabel.setOpaque(true);
        userLabel.setBackground(new Color(0, 0, 0, 80));
        gamePanel.add(userLabel);

        // Start button
        startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBounds(290, 180, 210, 60);
        startButton.setBackground(new Color(40, 160, 60));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startGame());
        gamePanel.add(startButton);

        // Jump button (up arrow)
        jumpButton = new JButton("▲");
        jumpButton.setFont(new Font("Arial", Font.BOLD, 22));
        jumpButton.setBounds(350, 440, 90, 50);
        jumpButton.setBackground(new Color(40, 120, 220));
        jumpButton.setForeground(Color.WHITE);
        jumpButton.setFocusPainted(false);
        jumpButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        jumpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jumpButton.setFocusable(false);
        jumpButton.setVisible(false);
        jumpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (running) player.jump();
            }
        });
        gamePanel.add(jumpButton);

        // Spacebar as instant backup jump
        InputMap inputMap = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = gamePanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "jump");
        actionMap.put("jump", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (running) player.jump();
            }
        });

        setContentPane(gamePanel);
        setLocationRelativeTo(null);
    }

    private void startGame() {
        if (running) return;
        running = true;
        startButton.setVisible(false);
        jumpButton.setVisible(true);

        gameLoop = new Timer(16, e -> {
            player.update();
            for (Car car : cars) car.update();
            checkCollision();
            score++;
            scoreLabel.setText("SCORE: " + score);
            gamePanel.setScore(score);
            gamePanel.repaint();
        });
        gameLoop.start();

        countdown = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("TIME: " + timeLeft);
            if (timeLeft <= 0) winGame();
        });
        countdown.start();
    }

    private void checkCollision() {
        Rectangle playerBox = player.getBounds();
        for (Car car : cars) {
            if (playerBox.intersects(car.getBounds())) {
                loseGame();
                return;
            }
        }
    }

    private void stopGame() {
        running = false;
        if (gameLoop != null) gameLoop.stop();
        if (countdown != null) countdown.stop();
    }

    private void loseGame() {
        stopGame();
        JOptionPane.showMessageDialog(this, "GAME OVER! You got hit!");
        dispose();
        new ThirdUI(username, score, false).display();
    }

    private void winGame() {
        stopGame();
        JOptionPane.showMessageDialog(this, "YOU WIN! Well done, " + username + "!");
        dispose();
        new ThirdUI(username, score, true).display();
    }

    @Override
    public void display() { setVisible(true); }
}

// =====================================================
// THIRD UI (result screen)
// =====================================================
class ThirdUI extends GameScreen {
    public ThirdUI(String username, int score, boolean win) {
        setTitle("Result");
        setSize(700, 460);
        setLayout(null);
        setResizable(false);

        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(win ? new Color(20, 100, 60) : new Color(120, 30, 30));
                g.fillRect(0, 0, 700, 460);
                g.setColor(new Color(255, 255, 255, 18));
                g.fillOval(-60, -60, 300, 300);
                g.fillOval(500, 300, 250, 250);
            }
        };
        bg.setLayout(null);
        bg.setBounds(0, 0, 700, 460);
        add(bg);

        // Result card
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(new Color(255, 255, 255, 230));
        card.setBounds(120, 70, 460, 300);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        bg.add(card);

        JLabel resultLabel = new JLabel(win ? "YOU WIN!" : "GAME OVER");
        resultLabel.setForeground(win ? new Color(20, 130, 60) : new Color(180, 30, 30));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 38));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setBounds(0, 20, 460, 55);
        card.add(resultLabel);

        JLabel divider = new JLabel("────────────────────────");
        divider.setForeground(new Color(200, 200, 200));
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        divider.setBounds(0, 78, 460, 22);
        card.add(divider);

        JLabel usernameLabel = new JLabel("Username:   " + username);
        usernameLabel.setForeground(new Color(60, 60, 70));
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setBounds(0, 110, 460, 30);
        card.add(usernameLabel);

        JLabel scoreLabel = new JLabel("Score:   " + score);
        scoreLabel.setForeground(new Color(30, 80, 180));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBounds(0, 150, 460, 34);
        card.add(scoreLabel);

        JLabel rankLabel = new JLabel(score > 600 ? "Excellent!" : score > 300 ? "Good run!" : "Keep practicing!");
        rankLabel.setForeground(new Color(120, 90, 30));
        rankLabel.setFont(new Font("Arial", Font.ITALIC, 15));
        rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rankLabel.setBounds(0, 188, 460, 24);
        card.add(rankLabel);

        // Try Again button
        JButton againButton = new JButton("TRY AGAIN");
        againButton.setBounds(80, 240, 140, 46);
        againButton.setFont(new Font("Arial", Font.BOLD, 15));
        againButton.setBackground(new Color(40, 120, 220));
        againButton.setForeground(Color.WHITE);
        againButton.setFocusPainted(false);
        againButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        againButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        againButton.addActionListener(e -> {
            dispose();
            new secondUI(username).display();
        });
        card.add(againButton);

        // Normal EXIT button
        JButton exitButton = new JButton("EXIT");
        exitButton.setBounds(240, 240, 140, 46);
        exitButton.setFont(new Font("Arial", Font.BOLD, 15));
        exitButton.setBackground(new Color(180, 40, 40));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));
        card.add(exitButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void display() { setVisible(true); }
}

// =====================================================
// MAIN CLASS
// =====================================================
public class RunTheTale {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new firstUI().display());
    }
}