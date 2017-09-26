import MyApp.Controller.ElevatorController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sorpaas on 12/1/16.
 */
public class ElevatorControllerTest {
    private ElevatorController elevator;

    @Before
    public void setUp() throws Exception {
        elevator = new ElevatorController(2, 10000, 10000);
    }

    @Test
    public void testSetDestFloor() throws Exception {
        elevator.setDestFloor(0, 5);
        Assert.assertEquals(elevator.getDestFloor(0), 5);
    }

    @Test
    public void testSetCurFloor() throws Exception {
        elevator.setCurFloor(1, 8);
        Assert.assertEquals(elevator.getCurFloor(1), 8);
    }

    @Test
    public void testGetStatus() throws Exception {
        elevator.setDestFloor(0, 20);
        Assert.assertEquals(elevator.getStatus(0), 1);
    }
}
