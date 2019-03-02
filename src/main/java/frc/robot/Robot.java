package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;


public class Robot extends TimedRobot {
  DifferentialDrive myDrive;

  XboxController Controller;
  UsbCamera Camera1;
  UsbCamera Camera2;
  VideoSink Server;

  Joystick Joy1;
  boolean prevTrigger = false;

  VictorSPX leftSpinner, rightSpinner;

  DoubleSolenoid Plunger;
  DoubleSolenoid Gripper;

  Spark Left, Right;
  TalonSRX leftLiftMotor, rightLiftMotor, gripperFlipper;
  Victor liftMotor;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Compressor c;

  public void robotInit() {

    //Usb Cameras
    Camera1 = CameraServer.getInstance().startAutomaticCapture();
    Camera2 = CameraServer.getInstance().startAutomaticCapture();
    Server = CameraServer.getInstance().getServer();
    Camera1.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
    Camera2.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    //Drive System
    Left = new Spark(0);
    Right = new Spark(1);
    Left.setInverted(true);
    //Right.setInverted(true);
    myDrive = new DifferentialDrive(Left, Right);
    myDrive.setRightSideInverted(false);
    //Lift Motor Controllers
    leftLiftMotor = new TalonSRX(2);
    rightLiftMotor = new TalonSRX(3);
    liftMotor = new Victor(2);

    //Xbox Controller
    Controller = new XboxController(0);
    Joy1 = new Joystick(1);

    //Spinners
    leftSpinner = new VictorSPX(4);
    rightSpinner = new VictorSPX(5);

    //Arm Flipper
    gripperFlipper = new TalonSRX(6);

    //Pnuematics
    c = new Compressor(0);
    Plunger = new DoubleSolenoid(0, 1);
    Gripper = new DoubleSolenoid(2, 3);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }
  public void teleopInit() {
    super.teleopInit();
    c.setClosedLoopControl(true);
  }

  public void robotPeriodic() {
  }

  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
  }

  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  public void teleopPeriodic() {
    myDrive.arcadeDrive(Controller.getRawAxis(1) *-1, Controller.getRawAxis(0)*-1);

    //Usb Cameras
    if (Joy1.getTrigger() && !prevTrigger){
      Server.setSource(Camera2);
    }
    else if (!Joy1.getTrigger() && prevTrigger){
      Server.setSource(Camera1);
    }

    prevTrigger = Joy1.getTrigger();

    //Use Talon For Flipper
    if (Controller.getRawButton(8)){
      gripperFlipper.set(ControlMode.Position, 1);
    }
    else if(Controller.getRawButton(7)){
      gripperFlipper.set(ControlMode.PercentOutput, -1);
    }

    //Use Lift Motors
    leftLiftMotor.setInverted(true);

    liftMotor.set(Controller.getRawAxis(5));

    if (Controller.getRawAxis(5) < -0.7) {
      leftLiftMotor.set(ControlMode.PercentOutput, 1);
      rightLiftMotor.set(ControlMode.PercentOutput, 1);
    }
    else if (Controller.getRawAxis(5) > 0.7){
      leftLiftMotor.set(ControlMode.PercentOutput, -1);
      rightLiftMotor.set(ControlMode.PercentOutput, -1);
    }
    else {
      leftLiftMotor.set(ControlMode.PercentOutput, 0);
      rightLiftMotor.set(ControlMode.PercentOutput, 0);
    }


    //Use of the Spinners
    leftSpinner.setInverted(true);

    if (Controller.getRawAxis(3) > 0.7){
      leftSpinner.set(ControlMode.PercentOutput, 1);
      rightSpinner.set(ControlMode.PercentOutput, 1);
    }
    else if (Controller.getRawAxis(2) > 0.7){
      leftSpinner.set(ControlMode.PercentOutput, -1);
      rightSpinner.set(ControlMode.PercentOutput, -1);

    }
    else{
      leftSpinner.set(ControlMode.PercentOutput, 0);
      rightSpinner.set(ControlMode.PercentOutput, 0);
    }


    //Use of the Solenoids
    if (Controller.getRawButton(1)){
      Plunger.set(DoubleSolenoid.Value.kForward);
    }
    else{
      Plunger.set(DoubleSolenoid.Value.kReverse);
    }

  }

  public void testPeriodic() {
  }
}