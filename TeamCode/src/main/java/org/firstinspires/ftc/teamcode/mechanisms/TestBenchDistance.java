package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestBenchDistance {
    private DistanceSensor distance;

    public void init(HardwareMap hwMap) {
        distance = hwMap.get(DistanceSensor.class, "Sensor_Distance");
    }

    public double getDistance () {
        return distance.getDistance(DistanceUnit.CM);
    }


}
