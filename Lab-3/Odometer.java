//Odometer.java

//same odometer code as last week
package LabThree;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta, i , j, thetachange, cChange;
	private int leftMotorTachoCount, rightMotorTachoCount;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.leftMotorTachoCount = 0;
		this.rightMotorTachoCount = 0;
		lock = new Object();
		this.i = 0.0;
		this.j = 0.0;
		this.cChange = 0.0;
		this.thetachange = 0.0;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			//TODO put (some of) your odometer code here

			synchronized (lock) {
				/**
				 * Don't use the variables x, y, or theta anywhere but here!
				 * Only update the values of x, y, and theta in this block. 
				 * Do not perform complex math
				 * 
				 */
				this.i = (leftMotor.getTachoCount() - this.leftMotorTachoCount)*Math.PI/180;	//delta left motor tacho value
				this.j = (rightMotor.getTachoCount() - this.rightMotorTachoCount)*Math.PI/180;	//delta right motor tacho value
				this.cChange = (this.i*2.1 + this.j*2.1)/2;	//change in center value (equation found in notes)
				this.thetachange = ((this.i*2.1 -this.j*2.1)/10.35);	//change in theta value (equation found in notes)
				if(this.i > 0 && this.j > 0)	//if both wheels moving forward
				{
					
						this.setY(this.cChange*Math.cos(this.theta + this.thetachange/2) + this.y); //change y (equation found in notes)
					
					
						this.setX(this.cChange*Math.sin(this.theta + this.thetachange/2) + this.x); //change x (equation found in notes)
					
				}
				else if(this.i > 0 && this.j < 0) //left motor moving forward, right moving backward
				{
					this.setX(this.cChange*Math.sin(this.theta + this.thetachange/2) + this.x);//calculate change in x (equation found in notes)
					this.setY(this.cChange*Math.cos(this.theta + this.thetachange/2) + this.y);//calculate change in y (equation found in notes)
					this.setTheta(this.theta + this.thetachange); //change value of theta (equation found in notes)
					if(this.theta > Math.PI*2) //if theta is greater than 2*pi
					{
						this.theta = this.theta - Math.PI*2;//wrap around, making angle zero again
					}
					else if(this.theta < 0)//if theta is less than zero
					{
						this.theta = this.theta + Math.PI*2;//wrap around, making theta 2*pi
					}
				}
				else if(this.i < 0 && this.j > 0) // right motor moving foward, left going back
				{
					this.setX(this.cChange*Math.sin(this.theta + this.thetachange/2) + this.x);//calculate change in x (equation found in notes)
					this.setY(this.cChange*Math.cos(this.theta + this.thetachange/2) + this.y);//calculate change in y (equation found in notes)
					this.setTheta(this.theta + this.thetachange);//change value of theta (equation found in notes)
					if(this.theta > Math.PI*2) //if theta is greater than 2*pi
					{
						this.theta = this.theta - Math.PI*2;//wrap around, making angle zero again
					}
					else if(this.theta < 0)//if theta is less than zero
					{
						this.theta = this.theta + Math.PI*2;//wrap around, making theta 2*pi
					}
				}
				else //wheels going backwards, decrease x and y (or increase, depending on the angle
				{
					this.setX(this.cChange*Math.sin(this.theta + this.thetachange/2) + this.x);
					this.setY(this.cChange*Math.cos(this.theta + this.thetachange/2) + this.y);
				}
				this.setLeftMotorTachoCount(leftMotor.getTachoCount());
				this.setRightMotorTachoCount(rightMotor.getTachoCount());
				//theta = -0.7376; //TODO replace example value
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/**
	 * @return the leftMotorTachoCount
	 */
	public int getLeftMotorTachoCount() {
		return leftMotorTachoCount;
	}

	/**
	 * @param leftMotorTachoCount the leftMotorTachoCount to set
	 */
	public void setLeftMotorTachoCount(int leftMotorTachoCount) {
		synchronized (lock) {
			this.leftMotorTachoCount = leftMotorTachoCount;	
		}
	}

	/**
	 * @return the rightMotorTachoCount
	 */
	public int getRightMotorTachoCount() {
		return rightMotorTachoCount;
	}

	/**
	 * @param rightMotorTachoCount the rightMotorTachoCount to set
	 */
	public void setRightMotorTachoCount(int rightMotorTachoCount) {
		synchronized (lock) {
			this.rightMotorTachoCount = rightMotorTachoCount;	
		}
	}
}
