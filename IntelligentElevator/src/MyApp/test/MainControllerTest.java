import MyApp.AppKickstarter;
import MyApp.Controller.MainController;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sorpaas on 12/1/16.
 */
public class MainControllerTest {
    private Properties cfgProps = null;
    private final String cfgFName = "etc/configuration.cfg";
    private MainController main;

    @Before
    public void setUp() throws Exception {
        try {
            cfgProps = new Properties();
            FileInputStream in = new FileInputStream(cfgFName);
            cfgProps.load(in);
            in.close();
            main = new MainController(cfgProps, "main", new AppKickstarter()); // The entangling AppKickstarter just made unit testing a lot harder.
        } catch (FileNotFoundException e) {
            System.out.println("Failed to open config file ("+cfgFName+").");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Error reading config file ("+cfgFName+").");
            System.exit(-1);
        }
    }

    @Test
    public void testElevator() throws Exception {
        main.run();
    }
}
