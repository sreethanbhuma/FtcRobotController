package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestBench {

    private DcMotor motor; //usually make name more accurate
    private double ticksPerRev; //revolution
    private DigitalChannel touchSensor;

    public void init(HardwareMap hwMap) {
        //touch sensor
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);
        //motor section
        motor = hwMap.get(DcMotor.class,"motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    //motor
    public void setMotorSpeed (double speed) {
        //accept values from -1.0 to 1.0
        motor.setPower(speed);
    }

    public double getMotorRevs() {
        return motor.getCurrentPosition() / ticksPerRev; //normalizing ticks to revolution
    }

    public void setMotorZeroBehavior (DcMotor.ZeroPowerBehavior zeroMethod) {
        motor.setZeroPowerBehavior(zeroMethod);
    }
    // touch sensor
    public boolean isTouchSensorPressed() {
        return !touchSensor.getState();
    }

    public boolean isTouchSensorReleased() {
        return touchSensor.getState();
    }
}

