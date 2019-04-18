package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.cscore.UsbCamera;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Robot extends TimedRobot {
  private DifferentialDrive myDrive;

  private XboxController Controller;
  private UsbCamera Camera1, Camera2;

  private VictorSPX leftSpinner, rightSpinner;

  //private DoubleSolenoid Plunger, Gripper;

  private Spark Left, Right;
  private TalonSRX leftLiftMotor, rightLiftMotor, gripperFlipper;
  private Victor liftMotor;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //private Compressor c;

  public void robotInit() {

    //Usb Cameras
    Camera1 = CameraServer.getInstance().startAutomaticCapture();
    //Camera2 = CameraServer.getInstance().startAutomaticCapture();
    Camera1.setConnectionStrategy(VideoSource.ConnectionStrategy.kAutoManage);
    Camera1.setVideoMode(PixelFormat.kMJPEG, 320, 240, 8);
    Camera1.setBrightness(30);
    /*Camera2.setConnectionStrategy(VideoSource.ConnectionStrategy.kAutoManage);
    Camera2.setVideoMode(PixelFormat.kMJPEG, 320, 240, 8);
    Camera2.setBrightness(30);*/
    
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

    //Spinners
    leftSpinner = new VictorSPX(4);
    rightSpinner = new VictorSPX(5);

    //Arm Flipper
    gripperFlipper = new TalonSRX(6);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }
  public void teleopInit() {
    super.teleopInit();
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

  }

  public void testPeriodic() {
  }
}