package MyApp.Controller;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class ControlPanel {
	private JFrame frame;
	private int n;                //total number of elevators
	private ArrayList<JTextArea> eIDList;
	private ArrayList<JTextArea> eFloorList; //Floor info of each Elevator
	private ArrayList<JTextArea> eStatusList; // UP,down or stop
	private int col;         //numbers of elevators in a row

	public ControlPanel() {
		frame = new JFrame();
		eIDList = new ArrayList<JTextArea>();
		eFloorList = new ArrayList<JTextArea>();
		eStatusList = new ArrayList<JTextArea>();
		
		//test
		n = 22;
		col = 10;
	}
	//Change the content of elevators
//	public void setFloor(){
//		eFloorList.get(index).replaceRange(String str,int start,int end);
//	}
//	public void setStatus(){
//		
//	}
	private void addEID(int i) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = i%col*2;
		c.gridy = (i/col)*2;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 3;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		frame.add(eIDList.get(i), c);
	}
	private void addEFloor(int i) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = i%col*2;
		c.gridy = (i/col)*2+1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		frame.add(eFloorList.get(i), c);
	}
	private void addEStatus(int i) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = i%col*2+1;
		c.gridy = (i/col)*2+1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		frame.add(eStatusList.get(i), c);
	}
	public void run() {
		frame.setSize(1000, 500);
		frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for (int i = 0; i < (n / col + 1) * col; i++) {
			String temp = "";
			String temp2 = "";
			String temp3 = "";
			if (i < n){
				temp = "Elevator " + (i + 1);
				temp2 = "G/F";
				temp3 = "STOP";
			}
			JTextArea eID = new JTextArea(temp);
			JTextArea eFloor = new JTextArea(temp2);
			JTextArea eStatus = new JTextArea(temp3);
			eID.setEditable(false); 
			eID.setLineWrap(true); 
			eID.setOpaque(false);
			eID.setBorder(BorderFactory.createEmptyBorder());
			eFloor.setEditable(false); 
			eFloor.setLineWrap(true); 
			eFloor.setOpaque(false);
			eFloor.setBorder(BorderFactory.createEmptyBorder());
			eStatus.setEditable(false); 
			eStatus.setLineWrap(true); 
			eStatus.setOpaque(false);
			eStatus.setBorder(BorderFactory.createEmptyBorder());
			eIDList.add(eID);
			eFloorList.add(eFloor);
			eStatusList.add(eStatus);
			addEID(i);
			addEFloor(i);
			addEStatus(i);
		}
		frame.setVisible(true);
	}

	public void setElevator(String msg)
	{
	}

	public void setUser(String msg)
	{

	}

	public static void main(String[] args) {
		ControlPanel gui = new ControlPanel();
		gui.run();
	}
}
