package MyApp.Kiosk;

import MyApp.misc.Msg;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by SSR on 2016/11/24.
 */
public class KioskGui {


    public JButton buttonConfirm;
    public JPanel panelMain;
    public JLabel Header;
    public JPanel bottomPanel;
    public JPanel leftPanel;
    public JLabel tapCard;
    public JPanel rightPanel;

    //number pad
    String numberString = "";
    private JButton b7;
    private JButton b8;
    private JButton b9;
    private JButton b4;
    private JButton b5;
    private JButton b6;
    private JButton b1;
    private JButton b2;
    private JButton b3;
    private JButton b0;
    private JButton confirmButton;
    private JButton back;
    private JTextField jtf;
    private JLabel displayArea;
    private JButton clearButton;

    //number pad

    public KioskGui(Kiosk kiosk) {


        //number pad
        Header.setText(kiosk.getID() + ": Welcome! Please tap your RFID Card or input your destination floor.");
        ButtonListener listener = new ButtonListener();
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);
        b4.addActionListener(listener);
        b5.addActionListener(listener);
        b6.addActionListener(listener);
        b7.addActionListener(listener);
        b8.addActionListener(listener);
        b9.addActionListener(listener);
        b0.addActionListener(listener);
        clearButton.addActionListener(listener);

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //user userID OR floor number
                kiosk.sendMsg(new Msg(kiosk.getID(), 123, "floor "+numberString));
            }
        });

        //main?
        JFrame frame = new JFrame("Kiosk Panel");
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.setText("Display Result...");
                numberString = "";
                jtf.setText(numberString);

            }
        });
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == b1) {
                numberString += "1";
                jtf.setText(numberString);
            } else if (e.getSource() == b2) {
                numberString += "2";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b3) {
                numberString += "3";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b4) {
                numberString += "4";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b5) {
                numberString += "5";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b6) {
                numberString += "6";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b7) {
                numberString += "7";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b8) {
                numberString += "8";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b9) {
                numberString += "9";
                jtf.setText(numberString);
            }
            else if (e.getSource() == b0) {
                numberString += "0";
                jtf.setText(numberString);
            }
            else if (e.getSource() == clearButton) {
                numberString = "";
                jtf.setText(numberString);
            }

            // finish all the else ifs
        }

    }


    public void display(String s){
        System.out.println("Display: "+ s);
        displayArea.setText(s);
    }

//    public static void main(String[] args){
//        JFrame frame = new JFrame("Kiosk Panel");
//        frame.setContentPane(new KioskGui().panelMain);
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//
//    }

}
