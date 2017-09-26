package MyApp.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sorpaas on 11/23/16.
 */
public class ElevatorController implements AbstractElevatorController {
    class ElevatorObservable extends Observable {
        @Override
        protected synchronized void setChanged() {
            super.setChanged();
        }
    }

    private AtomicInteger[] curFloors;
    private AtomicInteger[] destFloors;
    private AtomicInteger[] moveds;
    private long speed;
    private int size;
    private int waiting;
    private ElevatorObservable whenStoppedObservable = new ElevatorObservable();

    private Thread timer = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(this.speed);
            } catch (InterruptedException e) {
                System.out.println("Interruptted.");
                return;
            }

            for (int i = 0; i < size; i++) {
                int curFloor = curFloors[i].get();
                int destFloor = destFloors[i].get();
                int moved = moveds[i].incrementAndGet();

                if (curFloor > destFloor) {
                    int newFloor = curFloors[i].decrementAndGet();
                    moveds[i].set(0);
                    System.out.println("Elevator " + i + " moved to " + newFloor);
                } else if (curFloor < destFloor) {
                    int newFloor = curFloors[i].incrementAndGet();
                    moveds[i].set(0);
                    System.out.println("Elevator " + i + " moved to " + newFloor);
                } else if (moved == 1) {
                    System.out.println("Elevator " + i + " arrived at " + curFloor);
                    whenStoppedObservable.setChanged();
                    whenStoppedObservable.notifyObservers(i);
                }

                if (moved == waiting) {
                    this.setDestFloor(i, 0);
                }
            }
        }
    });

    public ElevatorController(int size, long speed, int waiting) {
        this.size = size;
        this.speed = speed;
        this.curFloors = new AtomicInteger[size];
        this.destFloors = new AtomicInteger[size];
        this.moveds = new AtomicInteger[size];

        for(int i = 0; i < size; i++) {
            this.curFloors[i] = new AtomicInteger(0);
            this.destFloors[i] = new AtomicInteger(0);
            this.moveds[i] = new AtomicInteger(1);
        }

        this.timer.start();
    }

    public Observable whenStopped() {
        return whenStoppedObservable;
    }

    public int setDestFloor(int elevatorID, int dest) {
        this.destFloors[elevatorID].set(dest);
        return 1;
    }

    public int setCurFloor(int elevatorID, int dest) {
        this.curFloors[elevatorID].set(dest);
        return 1;
    }

    public int getCurFloor(int elevatorID) {
        return this.curFloors[elevatorID].get();
    }

    public int getDestFloor(int elevatorID) {
        return this.destFloors[elevatorID].get();
    }


    //May result in error...
    //It may return 0 as stop when a list reaches its dest but the queue in MainController is not empty...
    public int getStatus(int elevatorID) {
        int curFloor = this.curFloors[elevatorID].get();
        int destFloor = this.destFloors[elevatorID].get();

        if (curFloor == destFloor) {
            return 0;
        } else if (curFloor > destFloor) {
            return 2; // Down
        } else {
            return 1; // Up
        }
    }

    public List<Integer> selectSameDirection(int standbyFloor, int direction) {
        ArrayList<Integer> directeds = new ArrayList<Integer>();
        for (int i = 0; i < this.size; i++) {
            if (this.getStatus(i) == direction &&
                    ((direction == 1 && this.getCurFloor(i) < standbyFloor - 1) || (direction == 2 && this.getCurFloor(i) > standbyFloor + 1))) {
                directeds.add(i);
            }
        }
        return directeds;
    }

    public List<Integer> selectOppositeDirection(int standbyFloor, int direction) {
        ArrayList<Integer> directeds = new ArrayList<Integer>();
        List<Integer> sameDireteds = selectSameDirection(standbyFloor,direction);
        for (int i = 0; i < this.size; i++){
            if (!sameDireteds.contains(i))
                directeds.add(i);
        }
        return directeds;
    }

    public List<Integer> selectFree() {
        ArrayList<Integer> frees = new ArrayList<Integer>();
        for (int i = 0; i < this.size; i++) {
            if (this.getStatus(i) == 0) {
                frees.add(i);
            }
        }
        return frees;
    }
}
