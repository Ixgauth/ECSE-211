// Lab5.java

package LabFive;

import LabFive.Odometer;
import LabFive.Navigation;
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

public class Lab5 {
	
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// throwing motor is connected to output B
	// back wheels are set to port C
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor Thrower = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor backMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));


	public static void main(String[] args) {
		int buttonChoice;

		// instantiate the odometer, navigator and thrower that will be used in the throwing mechanism
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		Navigation nav = new Navigation(odo);
		Thrower toss = new Thrower(odo, nav, leftMotor, rightMotor,Thrower);


		do {
			// clear the display
			t.clear();

			// ask the user which direction it should turn before throwing
			t.drawString("< Turn  | Turn >", 0, 0);
			t.drawString("  Left  | Right   ", 0, 1);
			t.drawString(" 		  |   ", 0, 2);
			t.drawString(" Press  | down ", 0, 3);
			t.drawString(" for    | straight", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT && 
				buttonChoice != Button.ID_DOWN);
		//three choices, left for left, right for right, down for straight
		//the robot should turn to the correct heading if necessary
		//for any option chosen, the code should pass the thrower method the distance to throw, 1 for having made a turn
		//0 if the ball should be thrown straight, thus less distance
		//in addition, we use the back motors to rotate into place and hold the robot in place when shooting the balls
		//the motors rotate 180 degrees after any movement to ensure that if the robot is to be turned, the wheels don't cause
		//unnecessary friction
		if (buttonChoice == Button.ID_LEFT) 
		{					
			nav.turnTo(25.435, true);
			backMotor.setSpeed(50);
			backMotor.rotate(180);
			toss.throwBall(1);
		} 
		
		else if (buttonChoice == Button.ID_RIGHT)
		{
			nav.turnTo(360-25.435, true);
			backMotor.setSpeed(50);
			backMotor.rotate(180);
			toss.throwBall(1);
		}
		
		else if (buttonChoice == Button.ID_DOWN)
		{
			backMotor.setSpeed(50);
			backMotor.rotate(-180);
			toss.throwBall(0);
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
