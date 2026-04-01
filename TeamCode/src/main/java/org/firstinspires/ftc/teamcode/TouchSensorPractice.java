package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;
@TeleOp
public class TouchSensorPractice extends OpMode {
    TestBench bench = new TestBench();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        String touchSensorPressed = "not pressed!";
        if (bench.isTouchSensorPressed()) {
            touchSensorPressed = "pressed!";
        }
        telemetry.addData("Touch Sensor State", bench.isTouchSensorPressed());
        telemetry.addData("Is touch sensor pressed",bench.isTouchSensorReleased());
        telemetry.addData("is sensor pressed",touchSensorPressed);
    }
}
