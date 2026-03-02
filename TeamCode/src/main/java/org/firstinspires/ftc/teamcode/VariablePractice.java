package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
@TeleOp
public class VariablePractice extends OpMode {
    public void init() {
        int teamNumber = 27136;
        double motorSpeed = 0.75;
        boolean clawClosed = true;
        String teamName = "SharkBots";
        int motorAngle = 90;

        telemetry.addData("Team number", teamNumber);
        telemetry.addData("Motor speed", motorSpeed);
        telemetry.addData("Claw closed", clawClosed);
        telemetry.addData("Name", teamName);
        telemetry.addData("Motor angle", motorAngle);
    }


    @Override
    public void loop() {

    }
}
