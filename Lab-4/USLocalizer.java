package LabFour;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static float ROTATION_SPEED = 75;

	private Odometer odo;
	private Navigation nav;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int filterLargeValues = 0;
	float previousDistance = 0;
	
	public USLocalizer(Odometer odo, Navigation nav,  SampleProvider usSensor, float[] usData, LocalizationType locType, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
	{
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.nav = nav;
	}
	
	public void doLocalization() 
	{
		double [] pos = new double [3];
		double angleA, angleB;
		double correctAngle;
		//chooses between the two types of localization
		//we have hard coded it to use the falling edge which we believe to be a better success rate
		if (locType == LocalizationType.FALLING_EDGE) 
		{
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			// rotate the robot until it sees no wall
			//it will then stop momentarily before turning clockwise
			while(this.getFilteredData() < .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.forward();
				rightMotor.backward();
			}
			Sound.buzz();
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			// keep rotating until the robot sees a wall, then latch the angle
			//stop once the wall is found 
			while(this.getFilteredData() > .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.forward();
				rightMotor.backward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			angleA = odo.getAng();
			//the angle that the first wall was found can be said to be angleA
			// switch direction and wait until it sees no wall
			//once it sees no wall the robot is pointed completely away from the wall and thus should try to find the other
			while(this.getFilteredData() < .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.backward();
				rightMotor.forward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			// keep rotating until the robot sees a wall, then latch the angle
			//stop once it sees the second wall in order to make calculations
			
			while(this.getFilteredData() > .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.backward();
				rightMotor.forward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			angleB = odo.getAng();
			//the angle that the second wall was found can be said to be angleB
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			//find the correct positioning
			//use the fact that the angle directly between the two walls should be 45 degrees
			//based on the information provided in the lab tutorial
			//the if and else statements are used to endure that no matter whether the first wall found 
			//has a larger or smaller angle than the second wall it will always point in the same direction
			//we find 45 by turning to the angle between the two walls and setting that to be 45 degrees
			if(angleA > angleB)
			{
				correctAngle = (angleA + angleB)/2;
				if(correctAngle > 360)
				{
					correctAngle = correctAngle - 360;
				}
				nav.turnTo(correctAngle, true);
				odo.setPosition(new double [] {0.0, 0.0, 45.0}, new boolean [] {true, true, true});
			}
			else
			{
				correctAngle = (angleA + angleB)/2 - 180;
				if(correctAngle > 360)
				{
					correctAngle = correctAngle - 360;
				}
				nav.turnTo(correctAngle, true);
				odo.setPosition(new double [] {0.0, 0.0, 45.0}, new boolean [] {true, true, true});
			}
			//we then travel to 0.0
			nav.turnTo(0.0,true);
			
		} else 
		{
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//
			// FILL THIS IN
			//
			//this localizer is much the same to the first one except it finds the wall and then turns while pointed
			//at the wall itself and finds the second wall and then calculates the correct angle in exactly
			//the same way as the FALLING EDGE portion of the code
			//we chose to hard code the use of falling edge for our program due to it's better sucess rate
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			while(this.getFilteredData() > .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.backward();
				rightMotor.forward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			while(this.getFilteredData() < .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.backward();
				rightMotor.forward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			angleA =  odo.getAng();
			
			while(this.getFilteredData() > .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.forward();
				rightMotor.backward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			
			while(this.getFilteredData() < .5)
			{
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.setSpeed(ROTATION_SPEED);
				
				leftMotor.forward();
				rightMotor.backward();
			}
			leftMotor.stop(true);
			rightMotor.stop(true);
			angleB = odo.getAng();
			if(angleA > angleB)
			{
				correctAngle = (angleA + angleB)/2;
				if(correctAngle > 360)
				{
					correctAngle = correctAngle - 360;
				}
				nav.turnTo(correctAngle, true);
				odo.setPosition(new double [] {0.0, 0.0, 45.0}, new boolean [] {true, true, true});
			}
			else
			{
				correctAngle = (angleA + angleB)/2 - 180;
				if(correctAngle > 360)
				{
					correctAngle = correctAngle - 360;
				}
				nav.turnTo(correctAngle, true);
				odo.setPosition(new double [] {0.0, 0.0, 45.0}, new boolean [] {true, true, true});
			}
			//we then travel to 0.0
			nav.turnTo(0.0,true);
		}
	}
	//this takes in the US data and if the data is very large(more than half a meter) to places a filter on it
	//this filter makes sure that large values occur a few times in a row before they are reported to be true
	private float getFilteredData() 
	{
		usSensor.fetchSample(usData, 0);
		float distance = usData[0];
		
		if(distance > .5 && filterLargeValues > 2)
		{
			previousDistance = distance;
			return distance;
		}
		else if(distance > .5)
		{
			filterLargeValues++;
			return previousDistance;
		}
		else
		{
			filterLargeValues =0;
			previousDistance = distance;
			return distance;
		}
		
	}

}
