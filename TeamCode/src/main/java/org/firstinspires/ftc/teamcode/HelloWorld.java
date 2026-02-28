package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;  // code from First Inspires
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
@Autonomous  //important
public class HelloWorld extends OpMode {

    @Override  // op mode class we extended from has initializing method, but we are overriding that
    public void init() {
        telemetry.addData("Hello", "Sreethan"); // adds data to screen of driver hub
    }


    @Override
    public void loop() {

    }

    /*
    1. change telemetry data from Hello World to Hello:Sreethan
    2.Change from teleop to auto
     */
}