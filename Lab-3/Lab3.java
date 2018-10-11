// Lab3.java

package LabThree;

import LabThree.USPoller;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab3 {
	private static final Port usPort = LocalEV3.get().getPort("S1");
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 10.5;	//changed our width value to make it come back to home space

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// instantiating the UsSensor
		SampleProvider usDistance = usSensor.getMode("Distance");
		float[] usData = new float[usDistance.sampleSize()];
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor); //instantiating odometer
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t); //instantiating display
		NavigationPtOne navptone = new NavigationPtOne(odometer, leftMotor, rightMotor);
		NavigationPtTwo navpttwo = new NavigationPtTwo(odometer, leftMotor, rightMotor);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left  | Right >", 0, 0);
			t.drawString("        |        ", 0, 1);
			t.drawString("Without | With  ", 0, 2);
			t.drawString("block   | block    ", 0, 3);
			t.drawString("obstical|     ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			
			navptone.start();
			odometer.start();
			odometryDisplay.start();
												//no need for sensor
			
			
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			//in this option, we decide that we have a block to avoid and call the uspoller and nav pt 2 to avoid it
			odometer.start();
			navpttwo.start();
			odometryDisplay.start();
			USPoller uspoller = new USPoller(usDistance, usData, navpttwo, odometer, navptone, leftMotor, rightMotor);
			uspoller.start();
			

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
