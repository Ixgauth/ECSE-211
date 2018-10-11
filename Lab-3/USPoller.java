package LabThree;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//

//I stole pretty much all of the code from lab one's ultrasonic poller and used it here as a poller for my sensor
//To that I added a class process usData that does what the name suggests, processes the data
//In the situation that the 
public class USPoller extends Thread
{
	private SampleProvider us;
	private USPoller cont;
	private float[] usData;
	private boolean change;
	private NavigationPtTwo navpttwo;
	private Odometer odometer;
	private NavigationPtOne navptone;
	private double currentX;
	private double currentY;
	private double currentTheta;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	//made the constructor from the first lab and added to it all of the necesary values that need to be used in the process
	//data method
	public USPoller(SampleProvider us, float[] usData, NavigationPtTwo navpttwo, Odometer odometer, NavigationPtOne navptone, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
	{
		this.us = us;
		this.cont = cont;
		this.usData = usData;
		this.navpttwo = navpttwo;
		this.odometer = odometer;
		this.navptone = navptone;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	public void processUsData(int distance)
	{
		//this is the stuff that was changed
		if(distance < 15)
		{
			//if a block is detected we check whether we have already reached 0,60
			int whatTravel = navpttwo.whatTravel;
			navpttwo.setisRunning(false);
			//then we get the values from the odometer
			currentX = odometer.getX();
			currentY = odometer.getY();
			currentTheta = odometer.getTheta();
			//then we turn to the right to go around the block
			navptone.turnTo(currentTheta + Math.PI/2);
			//then we go right around the block
			leftMotor.setSpeed(250);
			rightMotor.setSpeed(250);
			leftMotor.rotate(convertDistance(2.1, 30), true);
			rightMotor.rotate(convertDistance(2.1, 30), false);
			//then we turn back to our original heading and go forward to avoid the block
			navptone.turnTo(currentTheta);
			leftMotor.rotate(convertDistance(2.1, 40), true);
			rightMotor.rotate(convertDistance(2.1, 40), false);
			//then, if we have already been to 0, 60 we go to 60,0
			//if not we go to 0, 60 first
			if(whatTravel == 0)
			{
				navptone.travelTo(0, 60);
				navptone.travelTo(60, 0);
				navptone.travelTo(60, 0);
			}
			else
			{
				navptone.travelTo(60, 0);
				navptone.travelTo(60, 0);
			}
		}
	}
	//stolen from the second lab code given by prof
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	//stolen from the code given in the first lab
	public void run() {
		int distance;
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			processUsData(distance);						// now take action depending on value
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
	}

}

