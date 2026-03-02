package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
@TeleOp
public class GamepadPractice extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        // runs 50 times a second
        double speedForward = gamepad1.left_stick_y / 2.0;
        double diffXJoystick = gamepad1.left_stick_x - gamepad1.right_stick_x;
        double sumTriggers = gamepad1.left_trigger + gamepad1.right_trigger;

        telemetry.addData("Lx", gamepad1.left_stick_x);
        telemetry.addData("Ly", speedForward);
        telemetry.addData("Rx", gamepad1.right_stick_x);
        telemetry.addData("Ry", gamepad1.right_stick_y);

        telemetry.addData("a button", gamepad1.a);
        telemetry.addData("b button", gamepad1.b);
        telemetry.addData("difference in x", diffXJoystick);
        telemetry.addData("Sum of Trigger Values", sumTriggers);
    }
}
