package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TestBenchServo {
    private Servo servoPos;
    private CRServo servoRot;


    public void init (HardwareMap hwMap){
        servoPos = hwMap.get(Servo.class, "servo_Pos");
        servoRot = hwMap.get(CRServo.class,"servo_rot");
        servoPos.scaleRange(0.5,1.0); // set range from midpoint to max
        servoPos.setDirection(Servo.Direction.REVERSE);
        servoRot.setDirection(CRServo.Direction.REVERSE);
    }

    public void setServoPos(double angle) {
        servoPos.setPosition(angle);
    }

    public void setServoRot(double power) {
        servoRot.setPower(power);
    }
}
