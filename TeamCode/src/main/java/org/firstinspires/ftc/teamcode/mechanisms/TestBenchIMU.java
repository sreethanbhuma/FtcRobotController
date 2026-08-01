package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TestBenchIMU {
    // you need to know the orientation of the logo and usb port based on the front of your robot
    private IMU imu;
    private DcMotor motor;

    public void init(HardwareMap hwMap) {
        imu = hwMap.get(IMU.class, "imu");
        Object RevHubOrientationOnRobot;
        motor = hwMap.get(DcMotor.class, "motor"),

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);

        imu.initialize(new IMU.Parameters(RevOrientation));

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double getHeading(AngleUnit angleUnit) {  //used to determine orientation on the field
        return  imu.getRobotYawPitchRollAngles().getYaw(angleUnit);
    }

    public void setMotor(double power) {
       motor.setPower(power);
    }
}
