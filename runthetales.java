import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// ══════════════════════════════════════════════════════════════════
//  ABSTRACTION — abstract base screen every frame inherits from
// ══════════════════════════════════════════════════════════════════
abstract class GameScreen extends JFrame {

    protected static final Logger logger =
            Logger.getLogger(GameScreen.class.getName());

    /** Every screen must implement how it shows itself. */
    public abstract void display();

    /** Shared Nimbus look-and-feel helper. */
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

// ══════════════════════════════════════════════════════════════════
//  ABSTRACTION — abstract game entity (player & cars extend this)
// ══════════════════════════════════════════════════════════════════
abstract class GameEntity {

    protected JLabel label;
    protected int    x, y, width, height;

    public GameEntity(JLabel label, int x, int y, int width, int height) {
        this.label  = label;
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        syncBounds();
    }

    protected void syncBounds() {
        label.setBounds(x, y, width, height);
    }

    /** POLYMORPHISM: each subtype overrides this with its own movement logic */
    public abstract void update();

    /** Inset hitbox so near-misses feel fair */
    public Rectangle getHitbox() {
        return new Rectangle(x + 12, y + 12, width - 24, height - 24);
    }

    public JLabel getLabel() { return label; }
}

// ══════════════════════════════════════════════════════════════════
//  INHERITANCE — Car extends GameEntity
// ══════════════════════════════════════════════════════════════════
class Car extends GameEntity {

    // ENCAPSULATION: speed is private; only changed through update()
    private int speed;
    private int spawnX;

    public Car(JLabel label, int startX, int laneY,
               int carW, int carH, int speed, int spawnX) {
        super(label, startX, laneY, carW, carH);
        this.speed  = speed;
        this.spawnX = spawnX;
        applyStyle();
    }

    private void applyStyle() {
        label.setOpaque(true);
        label.setBackground(new Color(180, 30, 30));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        label.setText("<html><center>🚗</center></html>");
        syncBounds();
    }

    /** POLYMORPHISM: Car moves left and wraps around */
    @Override
    public void update() {
        x -= speed;
        if (x + width < 0) {
            x = spawnX;
            label.putClientProperty("scored", Boolean.FALSE);
        }
        syncBounds();
    }

    public int getSpeed() { return speed; }
    public void setSpeed(int s) { speed = s; }
}

// ══════════════════════════════════════════════════════════════════
//  INHERITANCE — Player extends GameEntity
// ══════════════════════════════════════════════════════════════════
class Player extends GameEntity {

    private static final int GROUND_Y      = 330;
    private static final int JUMP_STRENGTH = -16;
    private static final int GRAVITY       =   1;

    private int     vy       = 0;
    private boolean airborne = false;
    private boolean big      = false;

    public Player(JLabel label) {
        super(label, 80, GROUND_Y, 90, 120);
        applyStyle(false);
    }

    private void applyStyle(boolean enlarged) {
        label.setOpaque(true);
        label.setBackground(new Color(60, 120, 200));
        label.setBorder(BorderFactory.createLineBorder(new Color(30, 70, 140), 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, enlarged ? 80 : 44));
        label.setText("<html><center>🧍</center></html>");
        syncBounds();
    }

    public void jump() {
        if (!airborne) {
            vy       = JUMP_STRENGTH;
            airborne = true;
        }
    }

    /** POLYMORPHISM: Player applies gravity and lands on the ground */
    @Override
    public void update() {
        if (airborne) {
            y  += vy;
            vy += GRAVITY;
            if (y >= GROUND_Y) {
                y        = GROUND_Y;
                vy       = 0;
                airborne = false;
            }
        }
        syncBounds();
    }

    public void growBig() {
        if (!big) {
            big    = true;
            width  = 170;
            height = 230;
            applyStyle(true);
        }
    }

    public boolean isBig() { return big; }
}

// ══════════════════════════════════════════════════════════════════
//  SCREEN 1 — firstUI  (login / title screen)
//  INHERITANCE: extends GameScreen
// ══════════════════════════════════════════════════════════════════
class firstUI extends GameScreen {

    // ENCAPSULATION: username private, read-only via getter
    private String username = "";

    private JPanel     panel;
    private JLabel     titleLabel, promptLabel;
    private JTextField usernameField;
    private JButton    enterButton;

    public firstUI() {
        initComponents();
    }

    public String getUsername() { return username; }

    private void initComponents() {
        setTitle("Run The Tale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel = new JPanel(null);
        panel.setBackground(new Color(0, 204, 204));
        panel.setPreferredSize(new Dimension(792, 508));

        // Title
        titleLabel = new JLabel("RUN THE TALE");
        titleLabel.setFont(new Font("Palatino Linotype", Font.BOLD | Font.ITALIC, 62));
        titleLabel.setForeground(new Color(255, 51, 51));
        titleLabel.setBounds(130, 90, 560, 100);
        panel.add(titleLabel);

        // Prompt
        promptLabel = new JLabel("Enter your username to begin:");
        promptLabel.setFont(new Font("MS Gothic", Font.BOLD, 16));
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setBounds(240, 250, 320, 30);
        panel.add(promptLabel);

        // Username field
        usernameField = new JTextField();
        usernameField.setFont(new Font("MS Gothic", Font.PLAIN, 14));
        usernameField.setBounds(240, 295, 310, 34);
        usernameField.setBorder(BorderFactory.createBevelBorder(
                javax.swing.border.BevelBorder.RAISED));
        usernameField.addActionListener(e -> onEnter());
        panel.add(usernameField);

        // Enter button
        enterButton = new JButton("ENTER");
        enterButton.setFont(new Font("MS Gothic", Font.BOLD, 14));
        enterButton.setBackground(new Color(0, 153, 153));
        enterButton.setForeground(Color.WHITE);
        enterButton.setBorder(BorderFactory.createBevelBorder(
                javax.swing.border.BevelBorder.RAISED));
        enterButton.setBounds(320, 350, 150, 42);
        enterButton.addActionListener(e -> onEnter());
        panel.add(enterButton);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void onEnter() {
        String input = usernameField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        username = input;
        dispose();
        EventQueue.invokeLater(() -> new secondUI(username).display());
    }

    @Override
    public void display() { setVisible(true); }

    public static void main(String[] args) {
        applyNimbus();
        EventQueue.invokeLater(() -> new firstUI().display());
    }
}

// ══════════════════════════════════════════════════════════════════
//  SCREEN 2 — secondUI  (main game screen)
//  INHERITANCE: extends GameScreen
// ══════════════════════════════════════════════════════════════════
class secondUI extends GameScreen {

    // ── Exact component names from your original secondUI file ──
    private JPanel     jPanel1;
    private JLabel     jLabel1;       // "Time left:" static text
    private JTextField jTextField1;   // countdown timer display
    private JLabel     jLabel2;       // decorative / background
    private JLabel     jLabel4;       // road background

    // Game object labels matching your spec:
    // jLabel3, jLabel5, jLabel7, jLabel6 = CARS
    // jLabel8                             = PLAYER
    private JLabel jLabel3, jLabel5, jLabel7, jLabel6, jLabel8;

    private JLabel  scoreLabel;
    private JButton startButton;

    private JMenuBar  jMenuBar1;
    private JMenu     jMenu1;
    private JMenuItem jMenuItem1, jMenuItem2, jMenuItem3;

    // ── Game state — ENCAPSULATION: all private ──
    private final String username;
    private int     score       = 0;
    private int     timeLeft    = 15;
    private boolean gameRunning = false;
    private boolean gameOver    = false;

    private Timer gameLoop;
    private Timer countdown;

    // ── OOP entities ──
    private Player player;
    private Car    car3, car5, car7, car6;   // label numbers match

    public secondUI(String username) {
        this.username = username;
        initComponents();
    }

    private void initComponents() {
        setTitle("Run The Tale — " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1 = new JPanel(null);
        jPanel1.setBackground(new Color(0, 204, 204));
        jPanel1.setPreferredSize(new Dimension(792, 508));

        // ── jLabel4: road / sky background layers ──
        jLabel4 = new JLabel();
        jLabel4.setBounds(0, 0, 792, 508);
        jLabel4.setOpaque(true);
        jLabel4.setBackground(new Color(50, 50, 50));
        jPanel1.add(jLabel4);

        buildRoad();   // sky + road + lane markings

        // ── jLabel2: decorative silhouette ──
        jLabel2 = new JLabel("🏃");
        jLabel2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        jLabel2.setBounds(-150, -30, 370, 400);
        jLabel2.setForeground(new Color(0, 170, 170, 60));
        jPanel1.add(jLabel2);

        // ── Cars (jLabel3, jLabel5, jLabel7, jLabel6) ──
        jLabel3 = new JLabel();
        jLabel5 = new JLabel();
        jLabel7 = new JLabel();
        jLabel6 = new JLabel();

        //             label    startX  laneY   w    h  spd  spawnX
        car3 = new Car(jLabel3,   870,  335, 100, 55,  5,   820);
        car5 = new Car(jLabel5,  1150,  375,  95, 50,  4,  1100);
        car7 = new Car(jLabel7,  1380,  350, 100, 55,  6,  1320);
        car6 = new Car(jLabel6,  1650,  365,  95, 50,  3,  1600);

        jPanel1.add(jLabel3);
        jPanel1.add(jLabel5);
        jPanel1.add(jLabel7);
        jPanel1.add(jLabel6);

        // ── Player (jLabel8) ──
        jLabel8 = new JLabel();
        player  = new Player(jLabel8);
        jPanel1.add(jLabel8);

        // ── jLabel1: "Time left:" text label ──
        jLabel1 = new JLabel("Time left: ");
        jLabel1.setFont(new Font("MS Gothic", Font.BOLD, 14));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setBounds(580, 10, 100, 25);
        jPanel1.add(jLabel1);

        // ── jTextField1: countdown display (15 → 0) ──
        jTextField1 = new JTextField("15");
        jTextField1.setFont(new Font("MS Gothic", Font.BOLD, 14));
        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(JTextField.CENTER);
        jTextField1.setBounds(680, 8, 50, 28);
        jPanel1.add(jTextField1);

        // ── Score display ──
        scoreLabel = new JLabel("SCORE: 0");
        scoreLabel.setFont(new Font("MS Gothic", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 10, 180, 25);
        jPanel1.add(scoreLabel);

        // ── Hint label ──
        JLabel hint = new JLabel("SPACE or ▲ to jump over cars!");
        hint.setFont(new Font("MS Gothic", Font.PLAIN, 12));
        hint.setForeground(Color.YELLOW);
        hint.setBounds(250, 42, 300, 20);
        jPanel1.add(hint);

        // ── START button ──
        startButton = new JButton("▶  START");
        startButton.setFont(new Font("MS Gothic", Font.BOLD, 15));
        startButton.setBounds(335, 215, 130, 46);
        startButton.addActionListener(e -> startGame());
        jPanel1.add(startButton);

        // ── On-screen jump button ──
        JButton jumpBtn = new JButton("▲");
        jumpBtn.setFont(new Font("MS Gothic", Font.BOLD, 16));
        jumpBtn.setBounds(720, 430, 55, 40);
        jumpBtn.addActionListener(e -> { if (gameRunning) player.jump(); });
        jPanel1.add(jumpBtn);

        // ── Menu bar ──
        jMenuBar1  = new JMenuBar();
        jMenu1     = new JMenu("MENU");
        jMenuItem1 = new JMenuItem("Pause");
        jMenuItem2 = new JMenuItem("Resume");
        jMenuItem3 = new JMenuItem("Exit");

        jMenuItem1.addActionListener(e -> pauseGame());
        jMenuItem2.addActionListener(e -> resumeGame());
        jMenuItem3.addActionListener(e -> System.exit(0));

        jMenu1.add(jMenuItem1);
        jMenu1.add(jMenuItem2);
        jMenu1.add(jMenuItem3);
        jMenuBar1.add(jMenu1);
        setJMenuBar(jMenuBar1);

        // ── Spacebar binding ──
        jPanel1.setFocusable(true);
        jPanel1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && gameRunning)
                    player.jump();
            }
        });

        getContentPane().add(jPanel1);
        pack();
        setLocationRelativeTo(null);
    }

    /** Layered road scene (sky → road surface → lane lines). */
    private void buildRoad() {
        // Sky
        JLabel sky = new JLabel();
        sky.setBounds(0, 0, 792, 285);
        sky.setOpaque(true);
        sky.setBackground(new Color(100, 165, 220));
        jPanel1.add(sky);

        // Road surface
        JLabel road = new JLabel();
        road.setBounds(0, 285, 792, 223);
        road.setOpaque(true);
        road.setBackground(new Color(68, 68, 68));
        jPanel1.add(road);

        // Top kerb
        JLabel kerb1 = new JLabel();
        kerb1.setBounds(0, 286, 792, 6);
        kerb1.setOpaque(true);
        kerb1.setBackground(Color.WHITE);
        jPanel1.add(kerb1);

        // Centre lane divider
        JLabel centre = new JLabel();
        centre.setBounds(0, 358, 792, 6);
        centre.setOpaque(true);
        centre.setBackground(new Color(255, 215, 0));
        jPanel1.add(centre);

        // Bottom kerb
        JLabel kerb2 = new JLabel();
        kerb2.setBounds(0, 500, 792, 6);
        kerb2.setOpaque(true);
        kerb2.setBackground(Color.WHITE);
        jPanel1.add(kerb2);
    }

    // ─── Game loop ────────────────────────────────────────────────
    private void startGame() {
        if (gameRunning) return;
        gameRunning = true;
        gameOver    = false;
        score       = 0;
        timeLeft    = 15;
        jTextField1.setText("15");
        jTextField1.setForeground(Color.BLACK);
        scoreLabel.setText("SCORE: 0");

        startButton.setVisible(false);
        jPanel1.requestFocusInWindow();

        // ~60 fps
        gameLoop = new Timer(16, e -> tick());
        gameLoop.start();

        // 1-second countdown
        countdown = new Timer(1000, e -> {
            timeLeft--;
            jTextField1.setText(String.valueOf(timeLeft));
            jTextField1.setForeground(timeLeft <= 5 ? Color.RED : Color.BLACK);
            if (timeLeft <= 0) onTimeUp();
        });
        countdown.start();
    }

    private void pauseGame() {
        if (gameLoop  != null) gameLoop.stop();
        if (countdown != null) countdown.stop();
        gameRunning = false;
    }

    private void resumeGame() {
        if (!gameOver && !gameRunning) {
            gameRunning = true;
            if (gameLoop  != null) gameLoop.start();
            if (countdown != null) countdown.start();
            jPanel1.requestFocusInWindow();
        }
    }

    private void stopAll() {
        gameRunning = false;
        gameOver    = true;
        if (gameLoop  != null) gameLoop.stop();
        if (countdown != null) countdown.stop();
    }

    /**
     * Main update — called ~60 times/second.
     * POLYMORPHISM: update() dispatched to Player and Car overrides.
     */
    private void tick() {
        player.update();   // Player subclass behaviour
        car3.update();     // Car subclass behaviour
        car5.update();
        car7.update();
        car6.update();

        checkCollisions();
        jPanel1.repaint();
    }

    private void checkCollisions() {
        Rectangle pBox = player.getHitbox();

        for (Car car : new Car[]{ car3, car5, car7, car6 }) {
            // Crash?
            if (pBox.intersects(car.getHitbox())) {
                onCrash();
                return;
            }

            // Car safely passed the player → score point
            Object scored = car.getLabel().getClientProperty("scored");
            if (car.x + car.width < player.x && !Boolean.TRUE.equals(scored)) {
                car.getLabel().putClientProperty("scored", Boolean.TRUE);
                score += 10;
                scoreLabel.setText("SCORE: " + score);
            }

            // Reset flag after car loops back
            if (car.x > player.x + player.width) {
                car.getLabel().putClientProperty("scored", Boolean.FALSE);
            }
        }
    }

    private void onCrash() {
        stopAll();
        jLabel8.setText("<html><center>💥</center></html>");
        Timer pause = new Timer(800, e -> showResult(false));
        pause.setRepeats(false);
        pause.start();
    }

    private void onTimeUp() {
        stopAll();
        showResult(true);
    }

    private void showResult(boolean won) {
        // POLYMORPHISM: growBig() only defined on Player
        if (won) player.growBig();
        int delay = won ? 900 : 0;
        Timer pause = new Timer(delay, e -> {
            dispose();
            EventQueue.invokeLater(() -> new ThirdUI(username, score, won).display());
        });
        pause.setRepeats(false);
        pause.start();
    }

    @Override
    public void display() { setVisible(true); }
}

// ══════════════════════════════════════════════════════════════════
//  SCREEN 3 — ThirdUI  (result screen)
//  INHERITANCE: extends GameScreen
// ══════════════════════════════════════════════════════════════════
class ThirdUI extends GameScreen {

    // ENCAPSULATION: result data private; passed via constructor
    private final String  username;
    private final int     score;
    private final boolean won;

    // Exact component names from your original ThirdUI file
    private JPanel     jPanel1;
    private JTextField jTextField1;   // USERNAME
    private JTextField jTextField2;   // SCORE
    private JTextField jTextField3;   // congratulations / game-over message
    private JLabel     jLabel1;       // "USERNAME" label
    private JLabel     jLabel2;       // "SCORE" label
    private JButton    jButton1;      // TRY AGAIN
    private JButton    jButton2;      // EXIT

    public ThirdUI(String username, int score, boolean won) {
        this.username = username;
        this.score    = score;
        this.won      = won;
        initComponents();
    }

    private void initComponents() {
        setTitle(won ? "🎉 You Win!" : "💥 Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1 = new JPanel(null);
        jPanel1.setBackground(new Color(0, 153, 153));
        jPanel1.setPreferredSize(new Dimension(750, 500));

        // Big emoji
        JLabel emojiLabel = new JLabel(won ? "🏆" : "💥");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setBounds(270, 20, 210, 110);
        jPanel1.add(emojiLabel);

        // jLabel1 — "USERNAME"
        jLabel1 = new JLabel("USERNAME");
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setBounds(190, 162, 90, 25);
        jPanel1.add(jLabel1);

        // jTextField1 — displays the username
        jTextField1 = new JTextField(username);
        jTextField1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(JTextField.CENTER);
        jTextField1.setBounds(300, 160, 220, 28);
        jPanel1.add(jTextField1);

        // jLabel2 — "SCORE"
        jLabel2 = new JLabel("SCORE");
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel2.setForeground(Color.WHITE);
        jLabel2.setBounds(200, 238, 90, 25);
        jPanel1.add(jLabel2);

        // jTextField2 — displays the score
        jTextField2 = new JTextField(String.valueOf(score));
        jTextField2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        jTextField2.setEditable(false);
        jTextField2.setHorizontalAlignment(JTextField.CENTER);
        jTextField2.setBounds(300, 233, 220, 36);
        jPanel1.add(jTextField2);

        // jTextField3 — congratulations or game-over message
        jTextField3 = new JTextField(
                won ? "🎉 CONGRATULATIONS! 🎉"
                    : "💥 GAME OVER! Try again!");
        jTextField3.setFont(new Font("Segoe UI", Font.BOLD, won ? 15 : 14));
        jTextField3.setEditable(false);
        jTextField3.setBackground(new Color(0, 153, 153));
        jTextField3.setForeground(won ? Color.YELLOW : Color.WHITE);
        jTextField3.setHorizontalAlignment(JTextField.CENTER);
        jTextField3.setBorder(null);
        jTextField3.setBounds(215, 310, 320, 35);
        jPanel1.add(jTextField3);

        // jButton1 — TRY AGAIN
        jButton1 = new JButton("TRY AGAIN");
        jButton1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jButton1.setBorder(BorderFactory.createBevelBorder(
                javax.swing.border.BevelBorder.RAISED));
        jButton1.setBounds(240, 440, 110, 30);
        jButton1.addActionListener(e -> {
            dispose();
            EventQueue.invokeLater(() -> new secondUI(username).display());
        });
        jPanel1.add(jButton1);

        // jButton2 — EXIT
        jButton2 = new JButton("EXIT");
        jButton2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jButton2.setBorder(BorderFactory.createBevelBorder(
                javax.swing.border.BevelBorder.RAISED));
        jButton2.setBounds(400, 440, 110, 30);
        jButton2.addActionListener(e -> System.exit(0));
        jPanel1.add(jButton2);

        getContentPane().add(jPanel1);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void display() { setVisible(true); }
}

// ══════════════════════════════════════════════════════════════════
//  ENTRY POINT
// ══════════════════════════════════════════════════════════════════
class runthetales {
    public static void main(String[] args) {
        GameScreen.applyNimbus();
        EventQueue.invokeLater(() -> new firstUI().display());
    }
}