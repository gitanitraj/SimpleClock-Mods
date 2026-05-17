//package SimpleClock;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class SimpleClock extends JFrame {

    Calendar calendar;
    SimpleDateFormat timeFormat;
    SimpleDateFormat dayFormat;
    SimpleDateFormat dateFormat;

    JLabel timeLabel;
    JLabel dayLabel;
    JLabel dateLabel;

    boolean is24Hour = false;
    boolean useGMT = false;
    boolean isDarkTheme = true;

    // For smooth theme animation
    Color currentBg;
    Color targetBg;
    Color currentFg;
    Color targetFg;
    Timer themeAnimationTimer;

    SimpleClock() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Digital Clock");
        this.setSize(400, 260);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        // Initial formats
        timeFormat = new SimpleDateFormat("hh:mm:ss a");
        dayFormat = new SimpleDateFormat("EEEE");
        dateFormat = new SimpleDateFormat("dd MMMMM, yyyy");

        // Labels
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setOpaque(true);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        dayLabel = new JLabel();
        dayLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setOpaque(true);
        dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setOpaque(true);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Center panel with padding and vertical layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rounded border around the whole center panel
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        centerPanel.add(timeLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(dayLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(dateLabel);

        this.add(centerPanel, BorderLayout.CENTER);

        // Buttons panel
        JButton formatButton = new JButton("Switch 12/24");
        formatButton.addActionListener(e -> toggleTimeFormat());

        JButton zoneButton = new JButton("Switch GMT/Local");
        zoneButton.addActionListener(e -> toggleTimeZone());

        JButton themeButton = new JButton("Dark/Light");
        themeButton.addActionListener(e -> toggleTheme());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(formatButton);
        bottomPanel.add(zoneButton);
        bottomPanel.add(themeButton);

        this.add(bottomPanel, BorderLayout.SOUTH);

        // Initial theme
        applyThemeInstant(true); // start in dark mode

        // Prepare theme animation timer
        themeAnimationTimer = new Timer(20, e -> animateThemeStep(centerPanel, bottomPanel));
        themeAnimationTimer.setRepeats(true);

        this.setVisible(true);

        startClockThread();
    }

    private void startClockThread() {
        Thread clockThread = new Thread(() -> {
            while (true) {
                Calendar now = Calendar.getInstance(useGMT ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());

                String newTime = timeFormat.format(now.getTime());
                String newDay = dayFormat.format(now.getTime());
                String newDate = dateFormat.format(now.getTime());

                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText(newTime);
                    dayLabel.setText(newDay);
                    dateLabel.setText(newDate);
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        clockThread.setDaemon(true);
        clockThread.start();
    }

    public void toggleTimeFormat() {
        if (is24Hour) {
            timeFormat = new SimpleDateFormat("hh:mm:ss a"); // 12-hour
        } else {
            timeFormat = new SimpleDateFormat("HH:mm:ss");   // 24-hour
        }
        if (useGMT) {
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        } else {
            timeFormat.setTimeZone(TimeZone.getDefault());
        }
        is24Hour = !is24Hour;
    }

    public void toggleTimeZone() {
        useGMT = !useGMT;
        TimeZone tz = useGMT ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault();
        timeFormat.setTimeZone(tz);
        dayFormat.setTimeZone(tz);
        dateFormat.setTimeZone(tz);
    }

    public void toggleTheme() {
        isDarkTheme = !isDarkTheme;

        Color newBg = isDarkTheme ? new Color(10, 10, 10) : Color.WHITE;
        Color newFg = isDarkTheme ? Color.WHITE : Color.BLACK;

        currentBg = timeLabel.getBackground();
        currentFg = timeLabel.getForeground();
        targetBg = newBg;
        targetFg = newFg;

        if (themeAnimationTimer.isRunning()) {
            themeAnimationTimer.stop();
        }
        themeAnimationTimer.start();
    }

    private void animateThemeStep(JPanel centerPanel, JPanel bottomPanel) {
        float alpha = 0.15f; // interpolation factor per step

        Color nextBg = lerpColor(currentBg, targetBg, alpha);
        Color nextFg = lerpColor(currentFg, targetFg, alpha);

        currentBg = nextBg;
        currentFg = nextFg;

        timeLabel.setBackground(nextBg);
        dayLabel.setBackground(nextBg);
        dateLabel.setBackground(nextBg);

        timeLabel.setForeground(nextFg);
        dayLabel.setForeground(nextFg);
        dateLabel.setForeground(nextFg);

        centerPanel.setBackground(nextBg);
        bottomPanel.setBackground(nextBg);
        this.getContentPane().setBackground(nextBg);

        // Stop when close enough
        if (isClose(nextBg, targetBg) && isClose(nextFg, targetFg)) {
            applyThemeInstant(isDarkTheme);
            themeAnimationTimer.stop();
        }
    }

    private void applyThemeInstant(boolean dark) {
        Color bg = dark ? new Color(10, 10, 10) : Color.WHITE;
        Color fg = dark ? Color.WHITE : Color.BLACK;

        timeLabel.setBackground(bg);
        dayLabel.setBackground(bg);
        dateLabel.setBackground(bg);

        timeLabel.setForeground(fg);
        dayLabel.setForeground(fg);
        dateLabel.setForeground(fg);

        this.getContentPane().setBackground(bg);
    }

    private Color lerpColor(Color from, Color to, float alpha) {
        int r = (int) (from.getRed() + alpha * (to.getRed() - from.getRed()));
        int g = (int) (from.getGreen() + alpha * (to.getGreen() - from.getGreen()));
        int b = (int) (from.getBlue() + alpha * (to.getBlue() - from.getBlue()));
        return new Color(r, g, b);
    }

    private boolean isClose(Color a, Color b) {
        int dr = a.getRed() - b.getRed();
        int dg = a.getGreen() - b.getGreen();
        int db = a.getBlue() - b.getBlue();
        return (dr * dr + dg * dg + db * db) < 10;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleClock::new);
    }
}
