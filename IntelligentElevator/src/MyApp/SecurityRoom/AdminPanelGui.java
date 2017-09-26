package MyApp.SecurityRoom;

import MyApp.misc.Msg;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by SSR on 2016/12/1.
 */
public class AdminPanelGui {

    private JPanel panelMain;
    private JLabel Header;
    private JButton buttonEdit;
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JButton buttonList;
    private JPanel panelBottom;
    private JPanel panelTop;
    private JTextField editIdInput;
    private JTextField editFloorInput;
    private JTextField removeIdInput;
    private JTextField addFloorInput;
    private JTextField addIdInput;
    private JLabel displayArea;
    private String editId, editFloor, addId, addFloor, removeId;

    public AdminPanelGui(AdministrationPanel ap) {

        //initiate
        JFrame frame = new JFrame("admin Panel");
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //get user input
        editIdInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editId= editIdInput.getText();
                editIdInput.selectAll();
            }
        });

        editFloorInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editFloor= editFloorInput.getText();
                editFloorInput.selectAll();
            }
        });
        removeIdInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeId= removeIdInput.getText();
                removeIdInput.selectAll();

            }
        });
        addIdInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addId = addIdInput.getText();
                addIdInput.selectAll();
            }
        });
        addFloorInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFloor = addFloorInput.getText();
                addFloorInput.selectAll();
            }
        });

        buttonEdit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ap.sendMsg(new Msg(ap.getID(), 0, editId+" "+editFloor));
                editId="";
                editFloor="";

            }
        });

        buttonAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ap.sendMsg(new Msg(ap.getID(), 1, addId+" "+addFloor));
                addId="";
                addFloor="";
            }
        });

        buttonList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ap.sendMsg(new Msg(ap.getID(), 3, ""));
            }
        });

        buttonRemove.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ap.sendMsg(new Msg(ap.getID(), 2, removeId));
                removeId="";
            }
        });
    }

    public void display(String s){
        displayArea.setText(s);
    }

//    public static void main(String[] args){
//
//        JFrame frame = new JFrame("Admin Panel");
//        frame.setContentPane(new AdminPanelGui().panelMain);
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//
//    }
}
