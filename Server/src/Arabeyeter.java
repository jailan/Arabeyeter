import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.awt.AWTException; 
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Robot; 
import java.awt.Toolkit;
import java.awt.event.*; 

import javax.swing.JButton;
import javax.swing.JFrame;
public class Arabeyeter extends JFrame
implements KeyListener, MouseListener{
	static int count = 0;
	static int diff = 600;
	static int mode = 0;
	long lastStamp = 0;
	JFrame window = new JFrame();
	Robot robot = null;
	static ArrayList<Integer> lastPositionsX = new ArrayList<Integer>();
	static ArrayList<Integer> lastPositionsY = new ArrayList<Integer>();
	
	public Arabeyeter(){
		window.setSize(300,100);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.addMouseListener(this);
		window.getContentPane().setLayout(null);
		int height = window.getContentPane().getHeight();
		int width = window.getContentPane().getWidth();
		int comp_width = width/3;
		final JButton leftClick = new JButton("Left Click");
		final JButton rightClick = new JButton("Right Click");
		leftClick.addMouseListener(new MouseAdapter() {
		    public void mouseEntered(MouseEvent e) {
		        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				lastStamp = calendar.getTimeInMillis();
				//System.out.println(lastStamp);
		    }
		    public void mouseExited(MouseEvent e) {
		       Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       long now = calendar.getTimeInMillis(); 
		       if((now - lastStamp) > 1000){
		    	   mode = 0;
		    	   
//		    	   rightClick.setBackground(Color.gray);
//		    	   System.out.println(lastStamp);
//		    	   leftClick.setBackground(Color.green);
		    	   leftClick.setText(">Left Click<"); 
		    	   rightClick.setText("Right Click");
		    	   leftClick.requestFocus();
//		    	   System.out.println(now+">>>>>"+mode+">>>"+(now - lastStamp));
		       }  	   
		    }
		});
		rightClick.addMouseListener(new MouseAdapter() {
		    public void mouseEntered(MouseEvent e) {
		    	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				lastStamp = calendar.getTimeInMillis();
		    }
		    public void mouseExited(MouseEvent e) {
		    	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			       long now = calendar.getTimeInMillis(); 
			       if((now - lastStamp) > 1000){
			    	   mode = 1;
			    	   leftClick.setText("Left Click"); 
			    	   rightClick.setText(">Right Click<"); 
			    	   rightClick.requestFocus();
//			    	   System.out.println(now+">>>>>"+mode+">>>"+(now - lastStamp));
			       }  	
		    }
		});
		JButton keyboard = new JButton("Keyboard");	
		leftClick.setBounds(0,0,100,80);
		rightClick.setBounds(100,0,100,80);
		keyboard.setBounds(200,0,100,80);
		window.getContentPane().add(leftClick);
		window.getContentPane().add(rightClick);
		window.getContentPane().add(keyboard);
		try{
			robot = new Robot();
		}catch(AWTException e){
			
		}
		loop();
	}
	public void loop(){
		int port = 5010;
		int dataport = -1;
		int rev = 1;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Toolkit.getDefaultToolkit().beep();

		System.out.println("Server, listening on port " + port + ", datagram port " + dataport);
		Server mylink = null;
		try {
			mylink = new Server(port, dataport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double C[];

		C = new double[2];
		boolean f = false;
		window.setLocation(screenSize.width - (window.getWidth()+10), screenSize.height - (window.getHeight()+10));
		window.setVisible(true);
		System.out.println("Server, waiting for connection...");
		try {
			mylink.Connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			//count++;
			//Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			//long milli = calendar.getTimeInMillis() ;
			//System.out.println(milli + " >>>>>>> "+count);
			try {
				mylink.RecvDoubles(C, 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int x = (int)C[0];
			int y = (int)C[1];
			if(x <= 0 || y <= 0)
				continue;
			lastPositionsX.add(x);
			lastPositionsY.add(y);
			if(lastPositionsX.size() > 5000){
				initArrays();
			}
			int size = lastPositionsX.size();
			if(size > diff){
				int diffX = lastPositionsX.get(size - 1) - lastPositionsX.get(size - diff);
				int diffY = lastPositionsY.get(size - 1) - lastPositionsY.get(size - diff);
				diffX = Math.abs(diffX);
				diffY = Math.abs(diffY);
				if( diffX < 20 && diffY < 20){
					initArrays();
					Toolkit.getDefaultToolkit().beep();
					if(mode == 0){
						robot.mousePress(InputEvent.BUTTON1_MASK);
				        robot.mouseRelease(InputEvent.BUTTON1_MASK);
				        
					}else if(mode == 1){
						robot.mousePress(InputEvent.BUTTON3_MASK);
			            robot.mouseRelease(InputEvent.BUTTON3_MASK);
					}
						
				//	System.out.println("CLICKKKKKKKKKKKKKKKKKKKK  "+ x + "   " + y);
					continue;
				}
			}
		    robot.mouseMove(x+55,y+45); //600
			//System.out.println(C[0] +  " ,  "+ C[1]+" "+count);
			if(f)
				break;	
		}
	}
	public static void initArrays(){
		lastPositionsX = new ArrayList<Integer>();
		lastPositionsY = new ArrayList<Integer>();
	}
	public static void main( String args[] ) throws IOException
	   {
		Arabeyeter myArabeyeter = new Arabeyeter();
		
	//	}
		System.out.println("Countttt: "+ count);
		System.out.println("Server, closing connection...");
		//mylink.Close();	
		System.out.println("Server, done...");
		
	   }
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
