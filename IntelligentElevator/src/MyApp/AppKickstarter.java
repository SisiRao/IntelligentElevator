package MyApp;

import java.lang.Thread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.Hashtable;

import MyApp.Controller.MainController;
import MyApp.Kiosk.*;
import MyApp.Timer.*;
import MyApp.misc.*;

//======================================================================
// AppKickstarter
public class AppKickstarter {
	private Properties cfgProps = null;
	private final String cfgFName = "etc/configuration.cfg";
    private Logger log = null;
    private Hashtable<String, AppThread> appThreads = null;

    //------------------------------------------------------------
    // main
    public static void main(String args[]) {
//    	// Example usage for ElevatorController.
//		ElevatorController elevators = new ElevatorController(2, 1000);
//		elevators.setDestFloor(0, 5);
//		elevators.setDestFloor(1, 7);

		AppKickstarter appKickstarter = new AppKickstarter();
		appKickstarter.startApp();
    } // main


    //------------------------------------------------------------
    // AppKickstarter
    public AppKickstarter() {
		// read system config from property file
		try {
			cfgProps = new Properties();
			FileInputStream in = new FileInputStream(cfgFName);
			cfgProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed to open config file ("+cfgFName+").");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Error reading config file ("+cfgFName+").");
			System.exit(-1);
		}

	// get and configure logger
	ConsoleHandler conHd = new ConsoleHandler();
	conHd.setFormatter(new LogFormatter());
	log = Logger.getLogger(AppKickstarter.class.getName());
	log.setUseParentHandlers(false);
	log.addHandler(conHd);
	log.setLevel(Level.INFO);
	appThreads = new Hashtable<String, AppThread>();
    } // AppKickstarter


    //------------------------------------------------------------
    // startApp
    public void startApp() {
		int numFloor = Integer.parseInt(cfgProps.getProperty("nFloor"));
		int numElevator = Integer.parseInt(cfgProps.getProperty("nElevator"));

		// create threads
		Timer timer = new Timer("Timer", this);
		MainController con = new MainController(cfgProps, "Controller", this);
		Kiosk[] kList= new Kiosk[numFloor+1];
		for (int i = 0; i <= numFloor; i++) {
			String name = "Kiosk" + i;
			kList[i] = new Kiosk(name, this);
			con.registerMBox(name, kList[i].getMBox());
		}
		Simulator sim = new Simulator("Simulator", this);
		// start threads
		new Thread(timer).start();
		new Thread(con).start();
		for (int i = 0; i <= numFloor; i++) {
			new Thread(kList[i]).start();
		}
		new Thread(sim).start();
    } // startApp


    //------------------------------------------------------------
    // getLogger
    public Logger getLogger() {
	return log;
    } // getLogger


    //------------------------------------------------------------
    // regThread
    public void regThread(AppThread appThread) {
	appThreads.put(appThread.getID(), appThread);
    } // regThread


    //------------------------------------------------------------
    // getThread
    public AppThread getThread(String id) {
	return appThreads.get(id);
    } // getThread


    //------------------------------------------------------------
    // getProperty
    public String getProperty(String property) {
	return cfgProps.getProperty(property);
    } // getThread
} // AppKickstarter
