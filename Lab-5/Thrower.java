package LabFive;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import LabFive.Navigation;
import LabFive.Odometer;
import lejos.hardware.Button;


//instantiate the odometer, navigator and motors
public class Thrower 
{
	private Odometer odo;
	private Navigation nav;
	private EV3LargeRegulatedMotor leftMotor, rightMotor,Thrower;
	public int buttonChoice;
	
	
	public Thrower(Odometer odo, Navigation nav, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3LargeRegulatedMotor Thrower)
	{
		this.odo = odo;
		this.nav = nav;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.Thrower = Thrower;
	}
	
	//throw ball method used to throw the ball
	//two options, one for the distance after turning (as it is slightly longer than straight on)
	
	//the program gets the tacho count for the throwing arm and until the arm is straight above the robot (turned 140 degrees)
	//the motor moves quickly to give the ball speed then stops suddenly to launch the ball
	//once the ball has been launched, the motor returns to its initial position (minus 2 degrees for safety of the robot)
	//and awaits a button press of left right or down before performing the same operation again three more times
	public void throwBall(double distance)
	{
		if (distance == 0)
		{
			for(int i = 0; i < 4; i ++)
			{
				while(Thrower.getTachoCount() > -140)
				{
					Thrower.setAcceleration(5000);
					Thrower.setSpeed(500);
					Thrower.backward();
				}
				Thrower.stop();
				while(Thrower.getTachoCount() < -2)
				{
					Thrower.setAcceleration(500);
					Thrower.setSpeed(100);
					Thrower.forward();
				}
				do{
				Thrower.stop();
				buttonChoice = Button.waitForAnyPress();
				} while (buttonChoice != Button.ID_LEFT
						&& buttonChoice != Button.ID_RIGHT && 
						buttonChoice != Button.ID_DOWN);
			}
		}
		else
		{
			for(int i = 0; i < 4; i ++)
			{
				while(Thrower.getTachoCount() > -140)
				{
					Thrower.setAcceleration(4500);
					Thrower.setSpeed(450);
					Thrower.backward();
				}
				Thrower.stop();
				while(Thrower.getTachoCount() < -2)
				{
					Thrower.setAcceleration(500);
					Thrower.setSpeed(100);
					Thrower.forward();
				}
				do{
				Thrower.stop();
				buttonChoice = Button.waitForAnyPress();
				} while (buttonChoice != Button.ID_LEFT
						&& buttonChoice != Button.ID_RIGHT && 
						buttonChoice != Button.ID_DOWN);
			}
		}
		Thrower.stop();
	}
}
