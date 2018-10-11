package labTwo;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

public class NavigationPtOne extends Thread{
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	//private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	//private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double currentX = 0.0;
	private double currentY = 0.0;
	private double currentTheta = 0.0;
	
	public NavigationPtOne(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
	{
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		

	}
	
	
	public void travelTo(double x, double y)
	{
		//this method is passed an x and y value and the robot should then navigate to that desired point
			
			currentX = odometer.getX();
			currentY = odometer.getY();
			currentTheta = odometer.getTheta();
			double deltaX = x - currentX;
			double deltaY = y - currentY;
			double wantedTheta = currentTheta;
			if(deltaX > 0)
			{
				wantedTheta = Math.atan(deltaY/deltaX);
				
			}
			else if(deltaX < 0 && deltaY > 0)
			{
				wantedTheta = Math.atan(deltaY/deltaX) + Math.PI;
				
			}
			else if(deltaX < 0 && deltaY < 0)
			{
				wantedTheta = Math.atan(deltaY/deltaX) - Math.PI;
			
			}
			if(wantedTheta != currentTheta)
			{
				turnTo(wantedTheta);
			}
			
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
	
	public void run() //run method which begins the thread
	{
		
		travelTo(60, 30);
		travelTo(30,30);
		travelTo(30,60);
		travelTo(60,0);
	
	}

}
