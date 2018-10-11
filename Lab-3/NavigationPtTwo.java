package LabThree;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
//this is used for the situation in which we do have a block obstacle in the way
//very similar to the first but it can be inturrupted by USPoller
public class NavigationPtTwo extends Thread{
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	public boolean isRunning; // will be turned to false if a block is found in the way
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double currentX = 0.0;
	private double currentY = 0.0;
	private double currentTheta = 0.0;
	public int whatTravel= 0;
	
	public NavigationPtTwo(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
	{
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		

	}
	
	
	public void travelTo(double x, double y)
	{
		//this method is passed an x and y value and the robot should then navigate to that desired point
			
		//this method is the same as nav pt one except that at the end there is a while loop that can be stopped by USPoller
		//in the event that the block is found to be in the way of the robot
			currentX = odometer.getX();
			currentY = odometer.getY();
			currentTheta = odometer.getTheta();
			double deltaX = x - currentX;
			double deltaY = y - currentY;
			double wantedTheta = currentTheta;
			wantedTheta = Math.atan(deltaX/deltaY);
			if(deltaY < 0 &&  deltaX > 0)
			{
				wantedTheta = wantedTheta + Math.PI;
			}
			else if(deltaY < 0 && deltaX < 0)
			{
				wantedTheta = wantedTheta - Math.PI;
			}
			if(wantedTheta != currentTheta)
			{
				turnTo(wantedTheta);
			}
			
			double distanceToTravel =Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
			distanceToTravel = Math.sqrt(distanceToTravel);
			//this is the change, there is an added while loop
			while(isRunning = true)
			{
				leftMotor.setSpeed(FORWARD_SPEED);
				rightMotor.setSpeed(FORWARD_SPEED);
			
				leftMotor.rotate(convertDistance(2.1, distanceToTravel), true);
				rightMotor.rotate(convertDistance(2.1, distanceToTravel), false);
				break;
			}
		
	}
	
	public void turnTo(double theta)
	{
		//this method is used to turn the robot to a certain angle and point it in the correct direction
		//same as nav pt one
		currentTheta = odometer.getTheta();
		double amountToTurn = 0;
		amountToTurn = theta - currentTheta;
		if(amountToTurn > Math.PI)
		{
			amountToTurn = amountToTurn - Math.PI*2;
		}
		else if(amountToTurn < -Math.PI)
		{
			amountToTurn = Math.PI*2 + amountToTurn;
		}
		amountToTurn = amountToTurn*180/Math.PI;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		leftMotor.rotate(convertAngle(2.1, 10.35,amountToTurn), true);
		rightMotor.rotate(-convertAngle(2.1, 10.35, amountToTurn), false);
		
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	//will change the variable isRunning from another class
	public void setisRunning (boolean change)
	{
		isRunning = change;
	}
	
	public void run() //run method which begins the thread
	{
		//different goal destinations
		travelTo(0,60);
		whatTravel++;
		//this if statement is used to ensure that the second destination is only attempted if the block has not yet been detected
		//if it has, skips the travel to method
		if(isRunning == true)
		{
			travelTo(60,0);
		}
	
	}

}

