package MyApp.SecurityRoom;

import MyApp.AppKickstarter;
import MyApp.misc.AppThread;
import MyApp.misc.MBox;
import MyApp.misc.Msg;

/**
 * Created by SSR on 2016/12/1.
 */

public class AdministrationPanel extends AppThread {
    private MBox conMBox = null;
    //Admin Panel
    private AdminPanelGui adGui;
    //------------------------------------------------------------
     public AdministrationPanel(String id, AppKickstarter appKickstarter) {
         super(id,appKickstarter);
         conMBox = appKickstarter.getThread("Controller").getMBox();
         adGui = new AdminPanelGui(this);

     } // Admin Panel

    @Override
    public void run() {
        for (;;) {
            //when kiosk is idle, it wait to receive a request from user
            Msg msg = mbox.receive();
            if(msg.getSender().equals("Controller"))
            {
                String content = msg.getDetails();
                content.replaceAll(" ","\n");
                display(content); //print msg
            }
            }

    }

    //send
    public void sendMsg(Msg m){
        conMBox.send(m);
    }
    //display
    private void display (String msg){
        adGui.display(msg);
    }

//    public static void main(String[] args){
//        AdministrationPanel adp = new AdministrationPanel("admin", new AppKickstarter());
//    }
}
