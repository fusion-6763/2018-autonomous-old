/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6763.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;

import com.kauailabs.navx.frc.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	Spark left = new Spark(0);
	Spark right = new Spark(2);
	Spark climber = new Spark(7);
	DifferentialDrive myRobot = new DifferentialDrive(left, right);
	Joystick stick = new Joystick(0);
	Encoder leftEncoder = new Encoder(0, 1);
	Encoder rightEncoder = new Encoder(2, 3);
	Timer timer = new Timer();
	
	AHRS navx = new AHRS(SPI.Port.kMXP);
	
	float ticksPerInch = 108.33333333F;
	String gameData;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		leftEncoder.setReverseDirection(false);
		rightEncoder.setReverseDirection(true);
		
		CameraServer.getInstance().startAutomaticCapture();
		
		navx.enableLogging(true);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		leftEncoder.reset();
		rightEncoder.reset();
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				
				if(gameData.charAt(0) == 'R') {
					//Right side of switch
					if(leftEncoder.get() < ticksPerInch * 168) {
						myRobot.tankDrive(-0.5, -0.5);
					}
					else {
						if(rightEncoder.get() < 22795) {
							myRobot.tankDrive(0.0, -0.5);
						}
						else {
								myRobot.tankDrive(0.0, 0.0);
						}
					}
				}
				else {
					//Left side of switch
					if(leftEncoder.get() < ticksPerInch * 84) {
						//Drive straight 84"
						myRobot.tankDrive(-0.5, -0.5);
					}
					else {
						if(rightEncoder.get() < (ticksPerInch * 84) + 4148) {
							//Turn 90 degrees left
							myRobot.tankDrive(0.0, -0.5);
						}
						else {
							if(leftEncoder.get() < (ticksPerInch * 84) + (ticksPerInch * 226)) {
								//Drive straight 226"
								myRobot.tankDrive(-0.5, -0.5);
							}
							else {
								if(leftEncoder.get() < 37731.33333333332) {
									//Turn 90 degrees right
									myRobot.tankDrive(-0.5, 0.0);
								}
								else {
									if(leftEncoder.get() < 48131.33333333332) {
										//Drive straight 96"
										myRobot.tankDrive(-0.5, -0.5);
									}
									else {
										if(leftEncoder.get() < 52279.33333333332) {
											//Turn 90 degrees right
											myRobot.tankDrive(-0.5, 0.0);
										}
										else {
											myRobot.tankDrive(0.0, 0.0);
										}
									}
								}
							}
						}
					}
				}
				
			break;
		}
		System.out.println(rightEncoder.get());
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		myRobot.arcadeDrive(stick.getY(), -stick.getX());
		
		if(-stick.getRawAxis(5) < 0) {
			climber.set(0);
		}
		else {
			climber.set(stick.getRawAxis(5));
		}
		System.out.println(rightEncoder.get());
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		//System.out.println(rightEncoder.get());
		System.out.println(navx.getYaw());
	}
}
