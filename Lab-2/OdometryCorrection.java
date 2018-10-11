/* 
 /* 
 * OdometryCorrection.java
 */
package labTwo;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	//private float result;
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private double savedX = 0.0;//helper variable
	private double savedY = 0.0;//helper variable
	
	private static final int testRed = 28;
	EV3ColorSensor colorSensor;

	// constructor for color sensor
	public OdometryCorrection(Odometer odometer, EV3ColorSensor colorSensor) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		
		

		// Color sensor
		float[] colorSample = {0};
		//lcd.drawInt(colorSample.length, 0, 2);
		boolean test = false;
		double senDist = 5.1;	//distance from sensor to center of mass of robot
		double helper= 0.0; // helper variable
				
			
		while (true) {
			correctionStart = System.currentTimeMillis();

			// put your correction code here
			colorSensor.getRedMode().fetchSample(colorSample, 0); //get the value for the redMode sensor
			int light_val = (int)(colorSample[0]*100);//multiply said value by 100
			
			if(light_val <= 28 && !test) // if sampled value*100 <= 28, color is black, we have hit a line
			{
				double theta = odometer.getTheta(); //get all three variables from odometer
				double xPos = odometer.getX();
				double yPos = odometer.getY();
				Sound.playNote(Sound.FLUTE,700,250);//let us know it hit a line
				if(theta < .6 || theta > 6.05)// if going in positive y direction
				{
					yPos = yPos - senDist; // correct for position of sensor
					if(savedY == 0.0)//first line hit
					{
						savedY = yPos;//update saved y
						savedX = 0.0;//reset saved x
					}
					
					else //not first line hit
					{
						helper = savedY + 30.48*Math.cos(theta); //add size of tile*cos(angle) to saved y
						odometer.setY(helper);//set y to be the correct value
						savedY = helper; //update saved y
						savedX = 0.0;//reset saved x
					}
					
				}
				else if(theta > 2.7 && theta < 3.5)// if going in negative y direction
				{
					yPos = yPos + senDist;// correct for position of sensor
					if(savedY == 0.0)//first line hit
					{
						savedY = yPos;//update saved y
						savedX = 0.0;//reset saved x
					}
					else 
					{
						helper = savedY + 30.48*Math.cos(theta);//add size of tile*cos(angle) to saved y
						odometer.setY(helper);//set y to be the correct value
						savedY = helper;//update saved y
						savedX = 0.0;//reset saved x
					}
				}
				else if(theta > .6 && theta < 2.7)// if going in positive x direction
				{
					xPos = xPos - senDist;// correct for position of sensor
					if(savedX == 0.0)//first line hit
					{
						savedX = xPos;//update saved x
						savedY = 0.0;//reset saved y
					}
					else
					{
						helper = savedX + 30.48*Math.sin(theta);//add size of tile*sin(angle) to saved x
						odometer.setX(helper);//set x to be the correct value
						savedX = helper;//update saved x
						savedY = 0.0;//reset saved y
					}
				}
				else// if going in negative x direction
				{
					xPos = xPos + senDist;// correct for position of sensor
					if(savedX == 0.0)//first line hit
					{
						savedX = xPos;//update saved x
						savedY = 0.0;//reset saved y
					}
					else
					{
						helper = savedX + 30.48*Math.sin(theta);//add size of tile*sin(angle) to saved x
						odometer.setX(helper);//set x to be the correct value
						savedX = helper;//update saved x
						savedY = 0.0;//reset saved y
					}

				}
				
				
			}
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}