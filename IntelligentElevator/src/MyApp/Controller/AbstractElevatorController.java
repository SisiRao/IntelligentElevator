package MyApp.Controller;

/**
 * Created by LRX, Sisi on 2016/11/15.
 *
 * Each elevator has the following information:
 * 1. curFloor : int
 * 2. destFloor: int
 * 3. status: int (0: stop; 1: up; 2: down)
 */
interface AbstractElevatorController {
    // Return 1: successful; 0: fail
    // Always succeed.
    int setDestFloor(int elevatorID, int dest);

    // Return 1: successful; 0: fail
    // Always succeed.
    int setCurFloor(int elevatorID, int dest);

    int getCurFloor(int elevatorID);
    int getDestFloor(int elevatorID);
    int getStatus(int elevatorID);
}
