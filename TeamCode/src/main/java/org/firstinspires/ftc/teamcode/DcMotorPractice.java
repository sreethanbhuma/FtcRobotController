package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;
@TeleOp
public class DcMotorPractice extends OpMode {
    TestBench bench1 = new TestBench();
    @Override
    public void init() {
        bench1.init(hardwareMap);
    }

    @Override
    public void loop() {
        //method 2 better for drive train
        double motorSpeed = gamepad1.left_stick_y;
        bench1.setMotorSpeed(motorSpeed);
        //method 1
        if (bench1.isTouchSensorPressed()) {
            bench1.setMotorSpeed(0.5);
        }
        else {
            bench1.setMotorSpeed(0.0); // stops the motor
        }

        if (gamepad1.a) {
            bench1.setMotorZeroBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else if (gamepad1.b) {
            bench1.setMotorZeroBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        bench1.setMotorSpeed(0.5);
        telemetry.addData("motor revs", bench1.getMotorRevs());
    }
}
