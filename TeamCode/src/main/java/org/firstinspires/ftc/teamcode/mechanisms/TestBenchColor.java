package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestBenchColor {
    NormalizedColorSensor colorSensor;


    public enum detectedColor {
        RED,
        BLUE,
        YELLOW,
        UNKNOWN
    }

    public void init(HardwareMap hwMap) {
        colorSensor = hwMap.get(NormalizedColorSensor.class, "sensor_Color_Distance");
        colorSensor.setGain(8); // amplifies the signal that the color sensor gives out
    }

    public detectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); //returns 4 values

        float normRed,normGreen,normBlue;
        normRed = colors.red/colors.alpha;
        normGreen = colors.green/colors.alpha;
        normBlue = colors.blue/colors.alpha;

        telemetry.addData("red", normRed);   // dont need later once you calibrated so can comment out
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);

        // TODO add if statements for specific colors
        /*
        calibration values
        red, green, blue
        RED= >.35, <.3, <.3
        Yellow= >.5, >.9, <.6
        BLUE= <.2, <.5, >.5
         */

        if (normRed > 0.35 && normGreen < 0.3 && normBlue < 0.3) {
            return detectedColor.RED;
        }

        if (normRed > 0.5 && normGreen > 0.9 && normBlue < 0.6) {
            return detectedColor.YELLOW;
        }

        if (normRed < 0.2 && normGreen < 0.5 && normBlue > 0.5) {
            return detectedColor.BLUE;
        }

        else {
            return detectedColor.UNKNOWN;
        }
    }
}
