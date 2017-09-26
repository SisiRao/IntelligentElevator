package MyApp.Kiosk;

import MyApp.misc.*;
import MyApp.AppKickstarter;

import javax.swing.*;


//======================================================================
// Kiosk
public class Kiosk extends AppThread {
    private MBox conMBox = null;
    private boolean status = true; // true: can receive user request ; false: busy
    private KioskGui kioskGui;
    //------------------------------------------------------------
    // Kiosk
    public Kiosk(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        conMBox = appKickstarter.getThread("Controller").getMBox();

        kioskGui = new KioskGui(this);

    } // Kiosk


    //------------------------------------------------------------
    // run
    public void run() {
        for (;;) {
            //when kiosk is idle, it wait to receive a request from user
            Msg msg = mbox.receive();
            if (status && msg.getSender().equals("Simulator")) {
                status = false;
                display(id + " received user input " + msg); //print msg
                sendMsg(new Msg(id, 123, msg.getDetails())); //send user input to controller
            }

            //when kiosk is handling request, it wait to receive response from controller
            if (!status && msg.getSender().equals("Controller")) {
                //verification result(0/1) (space) elevatorNo (space)floorNo;
                if (msg.getType() == 1) {
                    display("error: " + msg.getDetails()); //display error
                } else {
                    String[] response = msg.getDetails().split(" ");
                    display(id + ": Your destination floor: " + response[0] + ". Please go to Elevator " + response[1] + "."); //display
                }
                status = true;
            }
        }
    } // run

    //send
    public void sendMsg(Msg m){
        status = false;
        conMBox.send(m);
    }

    //display
    private void display (String msg){
        kioskGui.display(msg);
    }

} //Kiosk
