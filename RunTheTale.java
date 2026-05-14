package oop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.logging.Level;

// ─────────────────────────────────────────────────────────────────
//  ABSTRACTION: abstract base for every screen/frame in the game
// ─────────────────────────────────────────────────────────────────
abstract class GameScreen extends JFrame {

    protected static final Logger logger =
            Logger.getLogger(GameScreen.class.getName());

    /** Every screen must know how to show itself cleanly. */
    public abstract void display();

    /** Shared look-and-feel initialiser (static utility). */
    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  SCREEN 1 – Login / Title screen  (NewJFrame)
// ─────────────────────────────────────────────────────────────────
class NewJFrame extends GameScreen {

    private JPanel  jPanel1;
    private JLabel  jLabel1, jLabel2, jLabel3, jLabel4, jLabel5;
    private JTextField jTextField1;
    private JButton jButton1;

    /** ENCAPSULATION: username is private; accessed only via getter */
    private String username = "";

    public String getUsername() { return username; }

    public NewJFrame() { initComponents(); }

    private void initComponents() {
        setTitle("Run The Tale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(665, 360));
        setResizable(false);

        jPanel1 = new JPanel(null);   // use null layout matching original AbsoluteLayout
        jPanel1.setBackground(new Color(0, 204, 204));
        jPanel1.setPreferredSize(new Dimension(675, 403));

        // Romeo silhouette placeholder (teal rectangle when image missing)
        jLabel1 = new JLabel();
        jLabel1.setBounds(-120, -50, 490, 500);
        jLabel1.setOpaque(true);
        jLabel1.setBackground(new Color(0, 180, 180));
        jPanel1.add(jLabel1);

        // Title labels  "Run  T  ale"
        jLabel3 = new JLabel("Run      he ");
        jLabel3.setFont(new Font("Palatino Linotype", Font.ITALIC | Font.BOLD, 68));
        jLabel3.setForeground(new Color(255, 51, 51));
        jLabel3.setBounds(250, 60, 310, 90);
        jPanel1.add(jLabel3);

        jLabel5 = new JLabel("T");
        jLabel5.setFont(new Font("Palatino Linotype", Font.ITALIC | Font.BOLD, 130));
        jLabel5.setForeground(new Color(255, 51, 51));
        jLabel5.setBounds(390, 70, 180, 160);
        jPanel1.add(jLabel5);

        jLabel4 = new JLabel("ale");
        jLabel4.setFont(new Font("Palatino Linotype", Font.ITALIC | Font.BOLD, 78));
        jLabel4.setForeground(new Color(255, 51, 51));
        jLabel4.setBounds(450, 110, 270, 90);
        jPanel1.add(jLabel4);

        // Username label
        jLabel2 = new JLabel("        USERNAME");
        jLabel2.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jLabel2.setForeground(Color.WHITE);
        jLabel2.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.setBounds(470, 230, 140, 30);
        jPanel1.add(jLabel2);

        // Username text field
        jTextField1 = new JTextField();
        jTextField1.setFont(new Font("MS PGothic", Font.BOLD, 12));
        jTextField1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField1.setBounds(400, 190, 270, 30);
        jPanel1.add(jTextField1);

        // Enter button
        jButton1 = new JButton("ENTER");
        jButton1.setBackground(new Color(0, 204, 204));
        jButton1.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jButton1.setForeground(Color.WHITE);
        jButton1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setBounds(480, 310, 120, 40);
        jButton1.addActionListener(e -> onEnter());
        jPanel1.add(jButton1);

        // Allow pressing Enter key in text field too
        jTextField1.addActionListener(e -> onEnter());

        getContentPane().add(jPanel1);
        pack();
        setLocationRelativeTo(null);
    }

    private void onEnter() {
        String input = jTextField1.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        username = input;
        dispose();
        // Launch the game screen
        EventQueue.invokeLater(() -> new secondUI(username).display());
    }

    @Override
    public void display() {
        EventQueue.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        applyNimbus();
        EventQueue.invokeLater(() -> new NewJFrame().display());
    }
}

// ─────────────────────────────────────────────────────────────────
//  GAME ENTITY  –  base class for moving / interactive objects
//  INHERITANCE: Car and Player extend this
// ─────────────────────────────────────────────────────────────────
abstract class GameEntity {
    protected JLabel label;
    protected int    x, y, width, height;

    public GameEntity(JLabel label, int x, int y, int width, int height) {
        this.label  = label;
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        updateLabelBounds();
    }

    protected void updateLabelBounds() {
        label.setBounds(x, y, width, height);
    }

    /** ABSTRACTION: every entity must be able to update itself */
    public abstract void update();

    public Rectangle getBounds() {
        return new Rectangle(x + 10, y + 10, width - 20, height - 20);  // shrink hitbox slightly
    }

    public JLabel getLabel() { return label; }
}

// ─────────────────────────────────────────────────────────────────
//  Car entity  (INHERITANCE from GameEntity)
// ─────────────────────────────────────────────────────────────────
class Car extends GameEntity {

    private int speed;
    private int resetX;   // where the car resets after passing left edge
    private int laneY;

    /** POLYMORPHISM: each car has its own speed and lane */
    public Car(JLabel label, int startX, int laneY, int speed, int resetX) {
        super(label, startX, laneY, 90, 50);
        this.speed  = speed;
        this.resetX = resetX;
        this.laneY  = laneY;
        styleLabel();
    }

    private void styleLabel() {
        label.setOpaque(true);
        label.setBackground(new Color(180, 30, 30));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setText("<html><center>🚗</center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        updateLabelBounds();
    }

    @Override
    public void update() {
        x -= speed;
        if (x + width < 0) {
            x = resetX;  // loop back from right
        }
        updateLabelBounds();
    }

    public void increaseSpeed(int delta) { speed += delta; }
}

// ─────────────────────────────────────────────────────────────────
//  Player entity  (INHERITANCE from GameEntity)
// ─────────────────────────────────────────────────────────────────
class Player extends GameEntity {

    private static final int GROUND_Y      = 240;  // normal standing Y
    private static final int JUMP_VELOCITY = -15;
    private static final int GRAVITY       = 1;

    private int  velocityY   = 0;
    private boolean jumping  = false;
    private boolean big      = false;  // grows on win

    public Player(JLabel label) {
        super(label, 530, GROUND_Y, 80, 110);
        styleLabel();
    }

    private void styleLabel() {
        label.setOpaque(true);
        label.setBackground(new Color(70, 130, 180));
        label.setBorder(BorderFactory.createLineBorder(new Color(30, 80, 130), 2));
        label.setText("<html><center>🧍</center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        updateLabelBounds();
    }

    public void jump() {
        if (!jumping) {
            velocityY = JUMP_VELOCITY;
            jumping   = true;
        }
    }

    @Override
    public void update() {
        if (jumping) {
            y         += velocityY;
            velocityY += GRAVITY;
            if (y >= GROUND_Y) {
                y         = GROUND_Y;
                jumping   = false;
                velocityY = 0;
            }
        }
        updateLabelBounds();
    }

    public void growBig() {
        if (!big) {
            big    = true;
            width  = 160;
            height = 220;
            label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 96));
            updateLabelBounds();
        }
    }

    public boolean isOnGround() { return !jumping; }
}

// ─────────────────────────────────────────────────────────────────
//  SCREEN 2 – Main game screen  (secondUI)
//  INHERITANCE: extends GameScreen
// ─────────────────────────────────────────────────────────────────
class secondUI extends GameScreen {

    // ── UI components ──
    private JPanel   jPanel1;
    private JLabel   jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7;
    private JButton  jButton1Up, jButton2Down, jButton3Start;
    private JLabel   timerLabel;
    private JMenuBar jMenuBar1;

    // ── Game state (ENCAPSULATION: all private) ──
    private final String  username;
    private       int     score        = 0;
    private       int     timeLeft     = 20;
    private       boolean gameRunning  = false;
    private       boolean gameOver     = false;

    private Timer gameLoop;
    private Timer countdown;

    // ── OOP entities ──
    private Player player;
    private Car    car1, car2, car3;

    // ── Difficulty ramp ──
    private int  carsAvoided    = 0;
    private int  winThreshold   = 5;   // avoid this many to win

    public secondUI(String username) {
        this.username = username;
        initComponents();
    }

    // ─── UI construction ───────────────────────────────────────────
    private void initComponents() {
        setTitle("Run The Tale – " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1 = new JPanel(null);
        jPanel1.setBackground(new Color(0, 153, 153));
        jPanel1.setPreferredSize(new Dimension(623, 370));

        // ─ Road background ─
        jLabel3 = new JLabel();
        jLabel3.setBounds(0, 0, 623, 370);
        jLabel3.setOpaque(true);
        jLabel3.setBackground(new Color(60, 60, 60));
        // Road markings painted with a custom component
        jLabel3.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 3));
        jPanel1.add(jLabel3);

        // ─ Road stripe decoration ─
        JLabel roadStripe = new JLabel();
        roadStripe.setBounds(0, 210, 623, 8);
        roadStripe.setOpaque(true);
        roadStripe.setBackground(new Color(255, 220, 50));
        jPanel1.add(roadStripe);

        JLabel roadStripe2 = new JLabel();
        roadStripe2.setBounds(0, 300, 623, 8);
        roadStripe2.setOpaque(true);
        roadStripe2.setBackground(new Color(255, 220, 50));
        jPanel1.add(roadStripe2);

        // ─ Sky ─
        JLabel sky = new JLabel();
        sky.setBounds(0, 0, 623, 180);
        sky.setOpaque(true);
        sky.setBackground(new Color(135, 206, 235));
        jPanel1.add(sky);

        // ─ Cars (jLabel4, jLabel5, jLabel6) ─
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();

        car1 = new Car(jLabel4, 650,  220, 4, 800);
        car2 = new Car(jLabel5, 900,  255, 5, 950);
        car3 = new Car(jLabel6, 1150, 235, 3, 1100);

        jPanel1.add(jLabel4);
        jPanel1.add(jLabel5);
        jPanel1.add(jLabel6);

        // ─ Player (jLabel7) ─
        jLabel7 = new JLabel();
        player = new Player(jLabel7);
        jPanel1.add(jLabel7);

        // ─ Timer label ─
        timerLabel = new JLabel("TIME LEFT: 0:20");
        timerLabel.setFont(new Font("MS PGothic", Font.BOLD, 14));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(470, 10, 140, 25);
        jPanel1.add(timerLabel);

        // ─ Score label ─
        jLabel1 = new JLabel("SCORE: 0");
        jLabel1.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setBounds(20, 10, 160, 25);
        jPanel1.add(jLabel1);

        // ─ Player label behind it (decorative cutout area) ─
        jLabel2 = new JLabel();
        jLabel2.setBounds(-200, -60, 320, 520);
        jPanel1.add(jLabel2);

        // ─ START button ─
        jButton3Start = new JButton("START");
        jButton3Start.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jButton3Start.setBounds(260, 150, 100, 40);
        jButton3Start.addActionListener(e -> startGame());
        jPanel1.add(jButton3Start);

        // ─ Jump UP button ─
        jButton1Up = new JButton("▲ JUMP");
        jButton1Up.setBounds(540, 220, 70, 40);
        jButton1Up.setEnabled(false);
        jButton1Up.addActionListener(e -> player.jump());
        jPanel1.add(jButton1Up);

        // ─ (Down button kept for layout parity; no-op) ─
        jButton2Down = new JButton("▼");
        jButton2Down.setBounds(540, 270, 70, 40);
        jButton2Down.setEnabled(false);
        jPanel1.add(jButton2Down);

        // ─ Menu bar ─
        jMenuBar1 = new JMenuBar();
        jMenuBar1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        JMenu jMenu1 = new JMenu("MENU");

        JMenuItem miSettings = new JMenuItem("Settings");
        JMenuItem miResume   = new JMenuItem("Resume");
        JMenuItem miExit     = new JMenuItem("Exit");

        miSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "No settings yet."));
        miResume.addActionListener(e -> { if (!gameRunning && !gameOver) startGame(); });
        miExit.addActionListener(e -> System.exit(0));

        jMenu1.add(miSettings);
        jMenu1.add(miResume);
        jMenu1.add(miExit);
        jMenuBar1.add(jMenu1);
        setJMenuBar(jMenuBar1);

        // Key binding for spacebar jump
        jPanel1.setFocusable(true);
        jPanel1.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && gameRunning) player.jump();
            }
        });

        getContentPane().add(jPanel1);
        pack();
        setLocationRelativeTo(null);
    }

    // ─── Game loop ─────────────────────────────────────────────────
    private void startGame() {
        if (gameRunning) return;
        gameRunning   = true;
        gameOver      = false;
        score         = 0;
        timeLeft      = 20;
        carsAvoided   = 0;

        jButton3Start.setEnabled(false);
        jButton1Up.setEnabled(true);
        jPanel1.requestFocusInWindow();

        // 16 ms ≈ 60 fps
        gameLoop = new Timer(16, e -> tick());
        gameLoop.start();

        countdown = new Timer(1000, e -> {
            timeLeft--;
            int mins = timeLeft / 60;
            int secs = timeLeft % 60;
            timerLabel.setText(String.format("TIME LEFT: %d:%02d", mins, secs));
            if (timeLeft <= 0) onTimeUp();
        });
        countdown.start();
    }

    /** Main update tick – POLYMORPHISM: update() called on all GameEntity subtypes */
    private void tick() {
        if (!gameRunning) return;

        player.update();
        car1.update();
        car2.update();
        car3.update();

        checkCollisions();
        jPanel1.repaint();
    }

    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();

        for (Car car : new Car[]{car1, car2, car3}) {
            Rectangle carRect = car.getBounds();
            if (playerRect.intersects(carRect)) {
                onCrash();
                return;
            }

            // Car passed the player → score point
            if (car.x + car.width < player.x && !car.label.getClientProperty("scored").equals(Boolean.TRUE)) {
                car.label.putClientProperty("scored", Boolean.TRUE);
                score += 10;
                carsAvoided++;
                jLabel1.setText("SCORE: " + score);
            }
            // Reset scored flag when car loops back
            if (car.x > player.x) {
                car.label.putClientProperty("scored", Boolean.FALSE);
            }
        }
    }

    private void onCrash() {
        stopGame();
        player.label.setText("<html><center>💥</center></html>");
        Timer pause = new Timer(800, e -> showResult(false));
        pause.setRepeats(false);
        pause.start();
    }

    private void onTimeUp() {
        stopGame();
        // Win condition: survived the full timer
        showResult(true);
    }

    private void stopGame() {
        gameRunning = false;
        gameOver    = true;
        if (gameLoop  != null) gameLoop.stop();
        if (countdown != null) countdown.stop();
        jButton1Up.setEnabled(false);
    }

    /** POLYMORPHISM: calls growBig() defined in Player */
    private void showResult(boolean won) {
        if (won) player.growBig();
        dispose();
        EventQueue.invokeLater(() -> new thirdUI(username, score, won).display());
    }

    @Override
    public void display() {
        EventQueue.invokeLater(() -> setVisible(true));
    }
}

// ─────────────────────────────────────────────────────────────────
//  SCREEN 3 – Result screen  (thirdUI)
//  INHERITANCE: extends GameScreen
// ─────────────────────────────────────────────────────────────────
class thirdUI extends GameScreen {

    private final String  username;
    private final int     score;
    private final boolean won;

    private JPanel     jPanel1;
    private JLabel     jLabel1, jLabel2;
    private JTextField jTextField1, jTextField2, jTextField3;
    private JButton    jButton1, jButton2;

    public thirdUI(String username, int score, boolean won) {
        this.username = username;
        this.score    = score;
        this.won      = won;
        initComponents();
    }

    private void initComponents() {
        setTitle(won ? "You Win! 🎉" : "Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(0, 153, 153));
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jPanel1.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        // ── Big status emoji ──
        JLabel emoji = new JLabel(won ? "🏆" : "💥");
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel1.add(emoji);
        jPanel1.add(Box.createVerticalStrut(10));

        // ── jTextField3: congratulations or game over message ──
        jTextField3 = new JTextField(won
                ? "🎉 CONGRATULATIONS! 🎉"
                : "💥 GAME OVER! Better luck next time.");
        jTextField3.setEditable(false);
        jTextField3.setBackground(new Color(0, 153, 153));
        jTextField3.setForeground(Color.WHITE);
        jTextField3.setFont(new Font("MS PGothic", Font.BOLD, won ? 16 : 14));
        jTextField3.setBorder(null);
        jTextField3.setHorizontalAlignment(JTextField.CENTER);
        jTextField3.setAlignmentX(Component.CENTER_ALIGNMENT);
        jTextField3.setMaximumSize(new Dimension(400, 35));
        jPanel1.add(jTextField3);
        jPanel1.add(Box.createVerticalStrut(20));

        // ── jLabel2 / jTextField2 : USERNAME row ──
        jLabel2 = new JLabel("USERNAME");
        jLabel2.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jLabel2.setForeground(Color.WHITE);
        jLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel1.add(jLabel2);

        jTextField2 = new JTextField(username);
        jTextField2.setEditable(false);
        jTextField2.setFont(new Font("MS PGothic", Font.PLAIN, 13));
        jTextField2.setHorizontalAlignment(JTextField.CENTER);
        jTextField2.setMaximumSize(new Dimension(200, 28));
        jTextField2.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel1.add(jTextField2);
        jPanel1.add(Box.createVerticalStrut(10));

        // ── jLabel1 / jTextField1 : SCORE row ──
        jLabel1 = new JLabel("     SCORE");
        jLabel1.setFont(new Font("MS PGothic", Font.BOLD, 14));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setBackground(Color.BLACK);
        jLabel1.setOpaque(true);
        jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel1.add(jLabel1);

        jTextField1 = new JTextField(String.valueOf(score));
        jTextField1.setEditable(false);
        jTextField1.setFont(new Font("MS PGothic", Font.BOLD, 18));
        jTextField1.setHorizontalAlignment(JTextField.CENTER);
        jTextField1.setMaximumSize(new Dimension(200, 36));
        jTextField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel1.add(jTextField1);
        jPanel1.add(Box.createVerticalStrut(20));

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(new Color(0, 153, 153));

        jButton1 = new JButton("TRY AGAIN!");
        jButton1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(e -> {
            dispose();
            EventQueue.invokeLater(() -> new secondUI(username).display());
        });

        jButton2 = new JButton("EXIT");
        jButton2.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(e -> System.exit(0));

        btnPanel.add(jButton1);
        btnPanel.add(jButton2);
        jPanel1.add(btnPanel);

        getContentPane().add(jPanel1);
        pack();
        setSize(400, 380);
        setLocationRelativeTo(null);
    }

    @Override
    public void display() {
        EventQueue.invokeLater(() -> setVisible(true));
    }
}

// ─────────────────────────────────────────────────────────────────
//  ENTRY POINT
// ─────────────────────────────────────────────────────────────────
class RunTheTale {
    public static void main(String[] args) {
        GameScreen.applyNimbus();
        EventQueue.invokeLater(() -> new NewJFrame().display());
    }
}
