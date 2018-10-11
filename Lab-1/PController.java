package lab1EV3WallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		//
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}

		// TODO: process a movement based on the us distance passed in (P style)
		int relativeDistance = (this.distance - 17);	//at any point closer than 17, the robot shouldn't be moving forward
														//thus we set a relative distance of 17 as a bottom value
		if(this.distance >= 17 && this.distance <= 47)	//If within the range 17-47,the right motor should have speed
		{												//proportional to the distance from the wall
			
			rightMotor.setSpeed(0 + relativeDistance*10);//equation for propotional response
			rightMotor.forward();
		}
		else if(this.distance < 17)						//if too close to wall, right wheel should not move at all
		{
			rightMotor.setSpeed(0);
			rightMotor.forward();
		}
		else if(this.distance > 47)						//in case that robot has very high reading, all readings will be treated
		{												//the same, to not send the robot spinning like a top
			rightMotor.setSpeed(300);
			rightMotor.forward();
		}
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
