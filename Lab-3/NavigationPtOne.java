package LabThree;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

//this class is used for the without block navigation
public class NavigationPtOne extends Thread{
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double currentX = 0.0;
	private double currentY = 0.0;
	private double currentTheta = 0.0;
	private static final int erroramt = 2;
	private boolean reachedDest;
	
	//constructor
	public NavigationPtOne(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
	{
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		

	}
	
	//travel to method
	public void travelTo(double x, double y)
	{
		//this method is passed an x and y value and the robot should then navigate to that desired point
			
		//takes odometer readings
			currentX = odometer.getX();
			currentY = odometer.getY();
			currentTheta = odometer.getTheta();
			// computes the changes from current position to wanted
			double deltaX = x - currentX;
			double deltaY = y - currentY;
			double wantedTheta = currentTheta;
			//computes the new theta (wantedtheta) using the equations from class (our x and y coordinates are switched)
			wantedTheta = Math.atan(deltaX/deltaY);
			if(deltaY < 0 &&  deltaX > 0)
			{
				wantedTheta = wantedTheta + Math.PI;
			}
			else if(deltaY < 0 && deltaX < 0)
			{
				wantedTheta = wantedTheta - Math.PI;
			}
			
			//in case we have not yet turned, we should turn
			if(wantedTheta != currentTheta)
			{
				turnTo(wantedTheta);
			}
			//compute the distance to travel, assuming we are pointed at the right spot
			//then travel there
			
				double distanceToTravel =Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
				distanceToTravel = Math.sqrt(distanceToTravel);
				
				leftMotor.setSpeed(FORWARD_SPEED);
				rightMotor.setSpeed(FORWARD_SPEED);
				
				leftMotor.rotate(convertDistance(2.1, distanceToTravel), true);
				rightMotor.rotate(convertDistance(2.1, distanceToTravel), false);
			
		
	}
	
	public void turnTo(double theta)
	{
		//this method is used to turn the robot to a certain angle and point it in the correct direction
		
		//get current theta
		currentTheta = odometer.getTheta();
		double amountToTurn = 0;
		//difference between current angle and wanted angle is amount to turn to have correct heading
		amountToTurn = theta - currentTheta;
		//ensures that the robot never turns using a maximal angle
		if(amountToTurn > Math.PI)
		{
			amountToTurn = amountToTurn - Math.PI*2;
		}
		else if(amountToTurn < -Math.PI)
		{
			amountToTurn = Math.PI*2 + amountToTurn;
		}
		//convert to degrees from rads then sets the robot to turn to the correct heading
		amountToTurn = amountToTurn*180/Math.PI;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		leftMotor.rotate(convertAngle(2.1, 10.35,amountToTurn), true);
		rightMotor.rotate(-convertAngle(2.1, 10.35, amountToTurn), false);
		
	}
	//convert distance and angle methods are stolen from code provided to us in lab 2 by proffessors
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public void run() //run method which begins the thread
	{
	//these are the places we are supposed to go	
		travelTo(60, 30);
		travelTo(30,30);
		travelTo(30,60);
		travelTo(60,0);
		//fix error
		travelTo(60,0);
	
	}

}

