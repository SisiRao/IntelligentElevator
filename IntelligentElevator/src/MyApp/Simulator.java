package MyApp;

import MyApp.Timer.Timer;
import MyApp.misc.AppThread;
import MyApp.misc.MBox;
import MyApp.misc.Msg;

import java.util.Random;

/**
 * Created by LRX on 2016/11/23.
 */
public class Simulator extends AppThread {
    private AppKickstarter ak = null;
    //------------------------------------------------------------
    // PlayerA
    public Simulator(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        ak = appKickstarter;
    } // PlayerA


    //------------------------------------------------------------
    // run
    public void run() {
        ak.getThread("Kiosk2").getMBox().send(new Msg(id, 123, "floor 3"));

        int max = 50;
        int min = 0;

        while (true)
        {
            Random rn = new Random();
            int number = rn.nextInt(max - min + 1) + min;
            int timerID = Timer.setTimer(id, number*2);
            rn = new Random();
            int dest = rn.nextInt(max - min + 1) + min;
            int start = rn.nextInt(max - min + 1) + min;
            System.out.println("***********************Msg: start: " + start + "dest: "+ dest);
            while(true){
                Msg msg = mbox.receive();
                if (msg.getSender().equals("Timer")) {
                    ak.getThread("Kiosk"+ start).getMBox().send(new Msg(id, 123, "floor "+ dest));
                    System.out.println("Msg: Id: " + id + " start: " + start + "dest: "+ dest);
                }
            }

        }


//        for (int i = 0; i < 10; i++) {
//            int timerID = Timer.setTimer(id, 1000);
//            Msg msg = mbox.receive();
//            if (msg.getSender().equals("Timer")) {
//                System.out.println(id + ": Wake Up ("+i+")!  " + msg);
//
//                if (i % 3 == 0) {
//                    int kioskID = 5;
//                    String message = "user 005";
//                    //ak.getThread("Kiosk"+ kioskID).getMBox().send(new Msg(id, 123, message));
//                    kioskID = 9;
//                    message = "floor 10";
//                    //ak.getThread("Kiosk"+ kioskID).getMBox().send(new Msg(id, 123, message));
//                } else {
//                    if (i % 5 == 0) {
//
//                    }
//                }
//            } else {
//                System.out.println(id + ": Received msg: " + msg);
//            }
//        }

    } // run
}
