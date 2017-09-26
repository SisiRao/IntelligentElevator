package MyApp.Elevator;

import MyApp.AppKickstarter;
import MyApp.misc.AppThread;
import MyApp.misc.MBox;
import MyApp.misc.Msg;

/**
 * Created by LRX on 2016/12/1.
 */
public class Elevator extends AppThread {
    private int curFloor;
    private int destFloor;
    private int status; //stop: 0; 1: up; 2: down
    private MBox conMBox = null;
    private MBox timerMBox = null;

    public Elevator(String id, AppKickstarter appKickstarter){
        super(id, appKickstarter);
        conMBox = appKickstarter.getThread("Controller").getMBox();
    }

    private void updateCurFloor() {
    }

    @Override
    public void run() {
        for (;;) {
            Msg msg = mbox.receive();
            if (msg.getSender().equals("Timer")) {

            }
            if (msg.getSender().equals("Controller")) {

            }

            }
        }
}

