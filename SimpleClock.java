//package SimpleClock;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;


public class SimpleClock extends JFrame {
    
        Calendar calendar;
        SimpleDateFormat timeFormat;
        SimpleDateFormat dayFormat;
        SimpleDateFormat dateFormat;
    
        JLabel timeLabel;
        JLabel dayLabel;
        JLabel dateLabel;
        String time;
        String day;
        String date;

        SimpleClock() {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Digital Clock");
            this.setLayout(new FlowLayout());
            this.setSize(350, 220);
            this.setResizable(false);
    
            timeFormat = new SimpleDateFormat("hh:mm:ss a");
            dayFormat=new SimpleDateFormat("EEEE");
            dateFormat=new SimpleDateFormat("dd MMMMM, yyyy");
            timeLabel = new JLabel();
            timeLabel.setFont(new Font("SANS_SERIF", Font.PLAIN, 59));
            timeLabel.setBackground(Color.BLACK);
            timeLabel.setForeground(Color.WHITE);
            timeLabel.setOpaque(true);
            dayLabel=new JLabel();
            dayLabel.setFont(new Font("Ink Free",Font.BOLD,34));
    
            dateLabel=new JLabel();
            dateLabel.setFont(new Font("Ink Free",Font.BOLD,30));
    
    
            this.add(timeLabel);
            this.add(dayLabel);
            this.add(dateLabel);
            this.setVisible(true);
    
            setTimer();
        }
    
        public void setTimer() {
            Thread clockThread = new Thread(() -> {
                while (true) {
                    Calendar now = Calendar.getInstance();

                    String newTime = timeFormat.format(now.getTime());
                    String newDay  = dayFormat.format(now.getTime());
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
                    }
                }
            });

            clockThread.setDaemon(true); // closes automatically when window closes
            clockThread.start();
        }

        public static void main(String[] args) {
            new SimpleClock();
        }
    }
