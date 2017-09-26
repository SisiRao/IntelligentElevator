package MyApp.Controller;

import MyApp.AppKickstarter;
import MyApp.CardDB;
import MyApp.misc.AppThread;
import MyApp.misc.MBox;
import MyApp.misc.Msg;

import javax.smartcardio.Card;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by LRX on 2016/11/23.
 * This class is the main controller. It receives message from
 * different component through MBox.
 * This class also run the algorithm for choosing the suitable elevator for users
 * and update request list based on the status of elevators.
 */
public class MainController extends AppThread {
    private CardDB db = null;
    private Hashtable<String, MBox> mboxes = null; //MBox for Kiosks
    private ElevatorController elevatorC = null;

    //A hash table is used to store the stop queue for each elevator
    private Hashtable<Integer, LinkedList<Integer>> elevatorH= null;
    //A has table is used to store all received request.
    //In the outer hash table, Integer refers to the floor number
    //In the inner hash table, Integer refers to the assigned elevator id.
    private Hashtable<Integer, Hashtable<Integer, LinkedList<Request>>> requestH = null;
    private int numElevator;
    private int numFloor;
    //control panel
    ControlPanel controlPanel = new ControlPanel();

    //elevatorController

    //------------------------------------------------------------
    // MainController
    public MainController(Properties cfgProps, String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        numElevator = Integer.parseInt(cfgProps.getProperty("nElevator"));
        numFloor = Integer.parseInt(cfgProps.getProperty("nFloor"));
        requestH = new Hashtable<>();
        elevatorH = new Hashtable<>();
        mboxes = new Hashtable<String, MBox>();
        for (int i = 0; i < numElevator; i++){
            elevatorH.put(i, new LinkedList<Integer>());
        }
        for (int i = 0; i <= numFloor; i++){
            requestH.put(i, new Hashtable<>());
        }
        elevatorC = new ElevatorController(numElevator, 2000, 60);
        try {
            db = new CardDB(cfgProps.getProperty("dbUrl"));
        } catch(Exception e){
            System.out.println("Database Connection Failure: " + e.getMessage());
        }

//        //testing only
          // initiate elevator status
//        LinkedList<Integer> tmp = new LinkedList<>(Arrays.asList(9));
//        elevatorH.put(0, tmp);
//        elevatorC.setCurFloor(0, 5);
//        elevatorC.setDestFloor(0, 9);

        elevatorC.whenStopped().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                mbox.send(new Msg("Elevator", Integer.parseInt(arg.toString()), "Updated"));
            }
        });
    } // MainController


    //This class is for registering MBox of Kiosks and elevator to the MBox list.
    public void registerMBox(String id, MBox mb){
        mboxes.put(id, mb);
    }

    //Database related methods
    private int checkID(String id){
        int floor = -1;
        try {
            db.checkCard(id);
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return floor;
    }
    private String listAll(){
        String result = "";
        try {
            result = db.getAllMapping();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return result;
    }
    private int removeID(String id){
        int result = -1;
        try {
            db.removeCard(id);
            result = 0;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return result;
    }
    private int addID(String id, int floor){
        int result = -1;
        try {
            db.addCard(id, floor);
            result = 0;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return result;
    }
    private int editID(String id, int floor){
        int result = -1;
        try {
            db.removeCard(id);
            db.addCard(id,floor);
            result = 0;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return result;
    }

    /*
    This method receive a Request object and return the assigned elevator ID
    The rules used to get the best suitable elevator (lift) is as follow:
    1. If there is a lift coming towards the requester, it will be assigned;
    2. If there are more than one lift coming, assigned the closest one;
    3. If no coming lifts, an lift in stop status will be assigned;
    4. If more than one lifts are in the stop mode, the nearest one will be assigned;
    5. If no coming or stopped lifts, all lifts are either in the opposite direction
                                    or in the same direction but higher/lower requester's current floor (curF);
      Because each elevator has a list of stop floor stored and as our estimate, there can only be two turning points for each list,
      (For example, for upward lift, its list can be: 3,4,6,5,3,7,8 (6 and 3 will be the turning points))
      for each lift, we calculate a distance from the lift to the requester and return the one with the smallest distance.
      For convenient, we use T1 as the first turning point and T2 as the second turning point.
     If opposite direction: the distance (D) = |T1 - curF of lift (L) | + |T1 - curF of requester (R) |
     If same direction:
          If (|T1 - T2| <= |T1 - curF of R|), D = |T1 - curF of L| + |T1 - curF of R|
          If (|T1 - T2| > |T1 - curF of R|), D = |T1 - curF of L| + |T1 - T2| + |T2 - curF of R|
    This algorithm only consider the waiting of the requester and may jeopardise previous requesters.
    */
    private int getElevator(Request r){
        int from = r.fromF;
        int to = r.toF;
        int id = -1;
        int direct = 0;
        //0: stop; 1: up; 2: down
        if (from < to) {
            direct = 1;
        } else {
            direct = 2;
        }
        List<Integer> result = elevatorC.selectSameDirection(from,direct);
//        System.out.print("Same Direction Result: ");
//        for(Integer i: result)
//            System.out.print(i+" ");
//        System.out.println();

        if (!result.isEmpty()){
            int closestD = numFloor + 1;
            for(Integer i: result){
                int distance = Math.abs(elevatorC.getCurFloor(i)- from);
                if (distance < closestD && distance > 0){
                    closestD = distance;
                    id = i;
                }
            }
            r.eleID = id;
            //add new stops in the queue
            updateElevatorTable(r);
            addRequest(r);
            return r.eleID;
        }

        id = -1;
        result = elevatorC.selectFree();
//        System.out.print("Free Result: ");
//        for(Integer i: result)
//            System.out.print(i+" ");
//        System.out.println();

        if (!result.isEmpty()){
            int distance = numFloor+1;
            for(Integer i: result){
                int newDis = Math.abs(elevatorC.getCurFloor(i)- from);
                if (newDis < distance){
                    distance = newDis;
                    id = i;
                }
            }
            r.eleID = id;
            //add new stops in the queue
            updateElevatorTable(r);
            addRequest(r);
            return r.eleID;
        }

        //opposite direction
        result = elevatorC.selectOppositeDirection(from,direct);
//        System.out.print("Opposite Direction Result: ");
//        for(Integer i: result)
//            System.out.print(i+" ");
//        System.out.println();
        int min = numFloor +1;
        List<Integer> minTurns = null;
        id = -1;
        for (Integer i: result){
            LinkedList<Integer> stopQ = elevatorH.get(i);
            int curF = elevatorC.getCurFloor(i);
            List<Integer> turns = getTurningPoints(curF, stopQ);
            int tmp = 0;
            if (direct == elevatorC.getStatus(i)){
                tmp = Math.abs(curF - turns.get(0));
                if (turns.get(1) != -1) {
                    tmp += Math.abs(turns.get(1) - turns.get(0));
                } else {
                    tmp += Math.abs(r.fromF - turns.get(0));
                }
            } else {
                tmp = Math.abs(curF - turns.get(0))+ Math.abs(turns.get(0)-r.fromF);
            }
            if (tmp < min){
                min = tmp;
                id = i;
                minTurns = turns;
            }
        }
        r.eleID = id;
        //after choosing the best suit elevator,
        //add two new stops in the queue.
        updateElevatorTable(r, minTurns);
        addRequest(r);
        return r.eleID;
    }
    private List<Integer> getTurningPoints(int curF, LinkedList<Integer> Q){
        if (Q.size() == 1) {
            return Arrays.asList(Q.getFirst(), new Integer(-1));
        }
        int sub = Q.getFirst() - curF;
        int i = 0;
        while(i < Q.size() - 1 && (Q.get(i+1) - Q.get(i))*sub > 0)
            i++;
        if (i >= Q.size()-1)
            return Arrays.asList(Q.getLast(), new Integer(-1));

        int firstTurn = Q.get(i);
        sub = -sub;
        while(i < Q.size() - 1 && (Q.get(i+1) - Q.get(i))*sub > 0)
            i++;
        if (i >= Q.size()-1)
            return Arrays.asList(firstTurn,Q.getLast());
        return Arrays.asList(firstTurn, Q.get(i));
    }


      //testing only check whether turning point calculate correctly
//    public void main(String[] args){
//        int [] list = {1,2,3};
//        LinkedList<Integer> stopQ = new ArrayList<>();
//        for (int i: list){
//            stopQ.add(new Integer(i));
//        }
//            printQ(0, stopQ);
//        List<Integer> turns = getTurningPoints(0, stopQ);
//        System.out.print("Turns: ");
//        for(Integer index: turns)
//            System.out.print(index+" ");
//        System.out.println();
//    }

    //need modify
    private void addRequest(Request q){
        Hashtable<Integer, LinkedList<Request>> tmp = requestH.get(q.fromF);
        if (tmp.containsKey(q.eleID)) {
            tmp.get(q.eleID).add(q);
        } else {
            LinkedList<Request> tmpList = new LinkedList<>();
            tmpList.add(q);
            tmp.put(q.eleID, tmpList);
        }
    }

    //testing only
    private void printQ(int id, LinkedList<Integer> queue){
        System.out.print("Elevator "+ id+ ": ");
        if (!queue.isEmpty()){
            for (Integer i: queue){
                System.out.print(i+" ");
            }
        }
        System.out.println();
    }

    //This update methods only used for add stops for lift which need to turn around to get the requester
    //Only two scenarios:
    //1. the lift is currently in the same direction, the two stops are added after the 2 turns
    //2. the lift is currently in the opposite direction, the two stops are added within the first and second turns.
    private void updateElevatorTable(Request r, List<Integer> turns){
        LinkedList<Integer> stopQ = elevatorH.get(r.eleID);
        printQ(r.eleID,stopQ);
        //convert downward to upward
        if (r.fromF > r.toF) {
            r.fromF = -r.fromF;
            r.toF = -r.toF;
            for (Integer i : stopQ) {
                i = -i;
            }
        }
        if (turns.get(1) == -1){
            if (r.fromF == turns.get(0)) {
                stopQ.add(r.toF);
            } else {
                stopQ.add(r.fromF);
                stopQ.add(r.toF);
            }
        } else {
            if (turns.get(0) < turns.get(1)){ // different direction
                int fromPosition = insertValue(r.fromF, stopQ.subList(stopQ.indexOf(turns.get(0)), stopQ.size()));
                insertValue(r.toF, stopQ.subList(fromPosition, stopQ.size()));
            } else { //same direction
                int fromPosition = insertValue(r.fromF, stopQ.subList(stopQ.indexOf(turns.get(1)), stopQ.size()));
                insertValue(r.toF, stopQ.subList(fromPosition, stopQ.size()));
            }
        }

        if (r.fromF < 0) {
            r.fromF = -r.fromF;
            r.toF = -r.toF;
            for (Integer i : stopQ) {
                i = -i;
            }
        }
        elevatorC.setDestFloor(r.eleID,stopQ.getFirst());
        printQ(r.eleID,stopQ);
    }

    //This update methods only used for add stops for lift which is coming towards the requester or in stop mode
    private void updateElevatorTable(Request r){
        LinkedList<Integer> stopQ = elevatorH.get(r.eleID);
        int curF = elevatorC.getCurFloor(r.eleID);
        printQ(r.eleID,stopQ);
        if (stopQ.isEmpty()){
            if (curF != r.fromF) {
                stopQ.add(r.fromF);
                stopQ.add(r.toF);
            } else {
                stopQ.add(r.toF);
            }
        } else {
            //convert downward to upward
            if (r.fromF > r.toF) {
                curF = -curF;
                r.fromF = -r.fromF;
                r.toF = -r.toF;
                for (Integer i : stopQ) {
                    i = -i;
                }
            }

            if (curF != r.fromF) {
                int position = insertValue(r.fromF, stopQ);
                insertValue(r.toF, stopQ.subList(position, stopQ.size()));
            } else {
                insertValue(r.toF, stopQ);
            }

            if (r.fromF < 0) {
                curF = -curF;
                r.fromF = -r.fromF;
                r.toF = -r.toF;
                for (Integer i : stopQ) {
                    i = -i;
                }
            }
        }
        elevatorC.setDestFloor(r.eleID,stopQ.getFirst());
        printQ(r.eleID,stopQ);
    }

    //This method is used by updateElevatorTable to insert a number in a list
    private int insertValue(int v, List<Integer> Q){
        if (Q.isEmpty()){
            Q.add(v);
            return 0;
        }
        if (Q.size() == 1){
            if (v == Q.get(0)){
                return 0;
            }
            if (v < Q.get(0)) {
                Q.add(0, v);
                return 0;
            }
            Q.add(v);
            return 1;
        }
        //more than 1 values in the queue
        int i = 0;
        for(i = 0; i < Q.size()-1 && Q.get(i) < v && Q.get(i) < Q.get(i+1); i++){}
        if (v == Q.get(i))
            return i;
        if (v < Q.get(i)){
            Q.add(i, v);
            return i;
        }
        Q.add(i+1, v);
        return i+1;
    }

    //This method is called when an elevator is reached its destination floor.
    //This method will then set a new dest floor via ElevatorController.
    private void updateFloor(int eleID){
        LinkedList<Integer> tmpList = elevatorH.get(eleID);
        int curFloor = elevatorC.getCurFloor(eleID);
        if (!tmpList.isEmpty() ) {
            int targetF = tmpList.getFirst();
            if (targetF == curFloor) {
                tmpList.removeFirst();
                if (!tmpList.isEmpty()) {
                    int newTarget = tmpList.getFirst();
                    elevatorC.setDestFloor(eleID, newTarget);
                }
            } else {
                System.out.println("Error: "+ targetF + " " + curFloor);
            }
        } else {
            if (curFloor != 0){
                System.out.println("Error: " + curFloor);
            }
        }
    }

    //This method is called when an elevator received its destination floor
    //This method will then update the status of requests in the request pool (requestH)
    //A requester only enter the lift if 1. the lift reach its floor, 2. the direction of the list is the same as the requester.
    private void updateRequestPool(int eleID){
        LinkedList<Integer> tmpList = elevatorH.get(eleID); //get elevator queue
        int curFloor = elevatorC.getCurFloor(eleID);
        int eleDirect;
        LinkedList<Request> queue = requestH.get(curFloor).get(eleID); //get queue in the curFloor waiting for eleID
        if (curFloor == tmpList.getFirst()) {//confirm reaching
            if (tmpList.getFirst() < tmpList.get(1)) {
                eleDirect = 1;
            } else {
                eleDirect = 2;
            }

            for (Iterator<Request> iterator = queue.iterator(); iterator.hasNext();) {
                Request r = iterator.next();
            if (r.status == 1 && r.toF == curFloor) {
                // Usre leaves the elevator
                iterator.remove();
                //Can add methods to calculate waiting time.
            }
            if (r.status == 0 && r.fromF == curFloor) {
                //User enter the elevator
                r.status = 1;
            }
        }
        } else {
            System.out.println("Elevator Error");
        }
    }

    //Control Panel
    private void printEleStatus(){
        String output = "Current Elevator Status \nFormat: Elevator[eleID]: curFloor stop1 stop2 ...\n";
        for(int i = 0; i < numElevator; i++){
            output +="Elevator["+i+"]: "+ elevatorC.getCurFloor(i);
            LinkedList<Integer> tmp = elevatorH.get(i);
            if (!tmp.isEmpty()){
                for (Integer stop: tmp){
                    output +=" " + stop;
                }
            }
            controlPanel.setElevator(output.replace("Current Elevator Status \nFormat: Elevator[eleID]: curFloor stop1 stop2 ...\n",""));
            System.out.print(output+"\n\n");
        }
    }

    private void printRequestStatus(){
        System.out.println("=== Current Customer Status ===");
        String output = "";
        Hashtable<Integer, LinkedList<Request>> tmpH = new Hashtable<>();
        output += "\nWaiting: (format: Floor[floorNum]: (from, to, eleID) (from, to, eleID) ...";
        for(int i = 0; i <= numFloor ; i++){
            String requests = "";
            Hashtable<Integer, LinkedList<Request>> eleH = requestH.get(i);
            for(int j = 0; j < numElevator; j++){
                LinkedList<Request> rList = eleH.get(j);
                if (rList != null && !rList.isEmpty()){
                    for(Request r: rList){
                        if (r.status == 0){
                            requests += "("+r.fromF+","+r.toF+","+r.eleID+") ";
                        } else {
                            if (tmpH.containsKey(j)){
                                tmpH.get(j).add(r);
                            } else {
                                tmpH.put(j, new LinkedList<Request>(Arrays.asList(r)));
                            }
                        }
                    }
                }
            }
            if (!requests.equals("")){
                requests = "Floor[" + i + "]: " + requests;
                System.out.print(output+requests+"\n\n");
                controlPanel.setUser(requests);
            }
        }
        output = "In Elevator: (format: Elevator[eleID]: (from, to) (from, to) ...";
        String requests = "";
        for(int i  = 0; i  < numElevator; i++){
            if (tmpH.containsKey(i)){
                requests += "Elevator[" + i + "]: ";
                for(Request r: tmpH.get(i)){
                    requests += "("+r.fromF+","+r.toF+") ";
                }
                requests += "\n";
            }
        }
        if (!requests.equals("")){
            System.out.println(output+requests);
        }

    }

    //------------------------------------------------------------
    // run
    public void run() {
        for (;;) {
            printEleStatus();
            printRequestStatus();
            Msg msg = mbox.receive();
            String sender = msg.getSender();
            if (sender.equals("Elevator")){
                System.out.println(id + " Received msg: " + msg);
                //int eleID = Integer.parseInt(id.substring(8));
                updateRequestPool(msg.getType());
                updateFloor(msg.getType());
                printEleStatus();
                printRequestStatus();
            }

            if (sender.startsWith("Kiosk")) {
                System.out.println(id + " Received msg: " + msg); //print msg
                //interpret request message
                int from = Integer.parseInt(sender.substring(5));
                String[] tmp = msg.getDetails().split(" ");
                int toFloor = -1;
                MBox senderMBox = mboxes.get(sender);
                if (tmp[0].equals("user")) {
                    toFloor = checkID(tmp[1]);
                } else {
//                    System.out.println(msg.getDetails());
                    toFloor = Integer.parseInt(tmp[1]);
                }
//                System.out.println("Test: " + from + " " + toFloor);
                if (toFloor == -1) {
                    //send error msg to kiosk
                    senderMBox.send(new Msg(id, 1, "Authorization Error"));
                } else {
                    if (from == toFloor){
                        senderMBox.send(new Msg(id, 1, "You are at your destination. Please try again!"));
                    } else
                    if (toFloor > numFloor) {
                        senderMBox.send(new Msg(id, 1, "Incorrect Floor Number! Please enter number from 0 to " + numFloor));
                    } else {
                        int elevatorID = getElevator(new Request(from, toFloor, -1, 0));
                        senderMBox.send(new Msg(id, 0, toFloor + " " + elevatorID));
                    }
                }
                printEleStatus();
                printRequestStatus();
            }

            if (msg.getSender().equals("Simulator")) {
                System.out.println(id + ": Terminating MyApp!");
                System.exit(0);
            }
        }
    } // run
    class Request{
        int fromF = -1;
        int toF = -1;
        int eleID = -1;
        int status = -1; //0: waiting; 1: in the lift; 2: reach dest (This may be used to calculate the waiting time)
        Request(int from, int to, int id, int newStatus){
            fromF = from;
            toF = to;
            eleID = id;
            status = newStatus;
        }
    }


} // MainController
