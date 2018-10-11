package lab1EV3WallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int largeEnough; //create filter variables
	private int closeEnough;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
		largeEnough = 0;	//initialize filter variables
		closeEnough = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		if(distance < 42 && distance > 22) //distance is within range acceptable, set wheels to have equal speed
		{
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();			
			rightMotor.forward();
			if(closeEnough < 5)			//in case that this in occurring after large reading, don't reset the largeEnough
				closeEnough++;			//filter unless there have been a few small readings in a row
			else 
				largeEnough = 0;		//if there have been, reset the large filter
		}
		else if(distance > 42 && largeEnough > 40)	//distance is very large and filter is satisfied, turn left
		{
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();			
			rightMotor.forward();
			closeEnough = 0;		//reset filter to ensure that erroneous small values are ignored
		}
		else if(distance > 42) 		//distance is large but filter not satisfied do not change motor speed
		{
			/*leftMotor.setSpeed(150);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();			
			rightMotor.forward();*/
			largeEnough++;			//increase filter
			closeEnough = 0;		//reset filter to ensure that erroneous small values are ignored
		}
		else if(distance < 27)		//distance too close to the wall, turn right
		{
			rightMotor.setSpeed(50);
			leftMotor.setSpeed(250);
			rightMotor.forward();
			leftMotor.forward();
			if(closeEnough < 5)		//in case that this in occurring after large reading, don't reset the largeEnough
				closeEnough++;		//filter unless there have been a few small readings in a row
			else 
				largeEnough = 0;	//if there have been, reset the large filter
		}
		else if(distance < 10)		//distance far too close to the wall, stop the right wheel and turn without 
		{							//forward movement of robot
			leftMotor.setSpeed(250);
			rightMotor.stop();
			leftMotor.forward();
			if(closeEnough < 5)		//in case that this in occurring after large reading, don't reset the largeEnough
				closeEnough++;		//filter unless there have been a few small readings in a row
			else 
				largeEnough = 0;	//if there have been, reset the large filter
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
