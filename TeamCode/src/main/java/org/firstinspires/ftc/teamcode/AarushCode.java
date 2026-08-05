package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.AarushHelper;
import org.firstinspires.ftc.teamcode.mechanisms.AarushHelper;

@TeleOp
public class AarushCode extends OpMode {

    // =========================================================================
    // 1. HARDWARE & HELPER INSTANCES
    // =========================================================================
    private Limelight3A limelight;
    public final AarushHelper helper = new AarushHelper();

    // =========================================================================
    // 2. TUNING & CONFIGURATION VARIABLES
    // =========================================================================
    double[] stepSizes = {0.1, 0.01, 0.001, 0.0001, 0.00001};
    double outtakePowerTune = 0.0;
    int ticksChange = 10;
    int stepIndex = 2;

    // =========================================================================
    // 3. OPMODE INITIALIZATION & START
    // =========================================================================
    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(8);
        limelight.start();
        helper.init(hardwareMap);
    }

    @Override
    public void start() {
        helper.resetTimer();
    }

    // =========================================================================
    // 4. MAIN LOOP
    // =========================================================================
    @Override
    public void loop() {
        // --- DRIVETRAIN INPUTS ---
        // Read joystick values first so turnInput is available for vision feedforward
        double driveInput  = -gamepad1.left_stick_y;
        double strafeInput =  gamepad1.left_stick_x;
        double turnInput   =  gamepad1.right_stick_x;
        helper.driveRobotOriented(driveInput, strafeInput, turnInput);

        // --- 1. VISION & TARGETING ---
        LLResult result = limelight.getLatestResult();
        boolean hasTarget = (result != null && result.isValid());
        double targetX = 0;

        if (hasTarget) {
            targetX = result.getTx();

            // Pass both targetX and turnInput to helper
            helper.update(targetX, turnInput);
            helper.calculateDistance(result.getTy());

            telemetry.addData("Target Angle (tx)", targetX);
            telemetry.addData("Distance (in)", helper.distanceInches);

        } else {
            helper.update(0, turnInput);
            telemetry.addLine("No tag detected");
        }

        // --- 2. SENSORS & SAFETY CHECKS ---
        if (helper.getLimitSwitch()) {
            helper.resetEncoder();
            telemetry.addLine("Encoder Reset");
        }

        // --- 3. INTAKE & BALL HANDLING ---
        helper.setPowerIntake(1);

        if (gamepad1.right_bumper) {
            helper.setPowerRamp(0.5);
        } else {
            helper.setPowerRamp(0);
        }

        double calculatedPower = helper.getOuttakePower(helper.distanceInches);
        helper.setPowerOuttake(calculatedPower);

        if (gamepad1.b) {
            helper.pushBall(0);
        } else {
            helper.pushBall(100);
        }

        // --- 4. READY TO SHOOT CHECKS ---
        boolean turretAligned = hasTarget && helper.isTurretAligned(targetX);
        boolean outtakeReady = helper.isOuttakeReady(calculatedPower);
        boolean isReadyToShoot = hasTarget && turretAligned && outtakeReady;

        if (isReadyToShoot) {
            gamepad1.rumble(0.4, 0.4, 100);
        }

        // --- 5. ON-THE-FLY PID TUNING (GAMEPAD 2) ---
        if (gamepad2.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad2.dpadLeftWasPressed()) {
            helper.setKP(helper.getKP() - stepSizes[stepIndex]);
        }
        if (gamepad2.dpadRightWasPressed()) {
            helper.setKP(helper.getKP() + stepSizes[stepIndex]);
        }

        if (gamepad2.dpadUpWasPressed()) {
            helper.setKD(helper.getKD() + stepSizes[stepIndex]);
        }
        if (gamepad2.dpadDownWasPressed()) {
            helper.setKD(helper.getKD() - stepSizes[stepIndex]);
        }

        // --- 6. TELEMETRY & FEEDBACK ---
        if (isReadyToShoot) {
            telemetry.addData("Launch Status", "READY TO SHOOT!");
        } else {
            telemetry.addData("Launch Status", "NOT READY TO SHOOT");
        }

        telemetry.addData("Limelight Distance", helper.distanceInches);
        telemetry.addData("Current Power", helper.getOuttakePower(helper.distanceInches));
        telemetry.addData("Current Kp", helper.getKP());
        telemetry.addData("Current Kd", helper.getKD());
        telemetry.addData("Active Step Size", stepSizes[stepIndex]);
        telemetry.addData("Current Position", helper.turret.getCurrentPosition());
        telemetry.addData("Max Position", helper.ticks);
        telemetry.addData("Limit Switch State", helper.getLimitSwitch());

        if (helper.turret.getCurrentPosition() > helper.ticks) {
            telemetry.addLine("Over Limit");
        } else if (helper.turret.getCurrentPosition() < -helper.ticks) {
            telemetry.addLine("Over Limit");
        }

        telemetry.update();

        // --- 7. MANUAL TICK & OUTTAKE ADJUSTMENTS ---
        if (gamepad2.yWasPressed()) {
            helper.ticks += ticksChange;
        } else if (gamepad2.aWasPressed()) {
            helper.ticks -= ticksChange;
        }

        if (gamepad1.dpadUpWasPressed()){
            outtakePowerTune += stepSizes[stepIndex];
        } else if (gamepad1.dpadDownWasPressed()) {
            outtakePowerTune -= stepSizes[stepIndex];
        }
         turretAligned = hasTarget && helper.isTurretAligned(targetX);
         outtakeReady = helper.isOuttakeReady(calculatedPower);
         isReadyToShoot = hasTarget && turretAligned && outtakeReady;

        if (isReadyToShoot) {
            gamepad1.rumble(0.4, 0.4, 100);
        }

        // --- 5. ON-THE-FLY PID TUNING (GAMEPAD 2) ---
        if (gamepad2.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad2.dpadLeftWasPressed()) {
            helper.setKP(helper.getKP() - stepSizes[stepIndex]);
        }
        if (gamepad2.dpadRightWasPressed()) {
            helper.setKP(helper.getKP() + stepSizes[stepIndex]);
        }

        if (gamepad2.dpadUpWasPressed()) {
            helper.setKD(helper.getKD() + stepSizes[stepIndex]);
        }
        if (gamepad2.dpadDownWasPressed()) {
            helper.setKD(helper.getKD() - stepSizes[stepIndex]);
        }

        // --- 6. TELEMETRY & FEEDBACK ---
        if (isReadyToShoot) {
            telemetry.addData("Launch Status", "READY TO SHOOT!");
        } else {
            telemetry.addData("Launch Status", "NOT READY TO SHOOT");
        }

        telemetry.addData("Limelight Distance", helper.distanceInches);
        telemetry.addData("Current Power", helper.getOuttakePower(helper.distanceInches));
        telemetry.addData("Current Kp", helper.getKP());
        telemetry.addData("Current Kd", helper.getKD());
        telemetry.addData("Active Step Size", stepSizes[stepIndex]);
        telemetry.addData("Current Position", helper.turret.getCurrentPosition());
        telemetry.addData("Max Position", helper.ticks);
        telemetry.addData("Limit Switch State", helper.getLimitSwitch());

        if (helper.turret.getCurrentPosition() > helper.ticks) {
            telemetry.addLine("Over Limit");
        } else if (helper.turret.getCurrentPosition() < -helper.ticks) {
            telemetry.addLine("Over Limit");
        }

        telemetry.update();

// --- 7. MANUAL TICK & OUTTAKE ADJUSTMENTS ---
        if (gamepad2.yWasPressed()) {
            helper.ticks += ticksChange;
        } else if (gamepad2.aWasPressed()) {
            helper.ticks -= ticksChange;
        }

        if (gamepad1.dpadUpWasPressed()){
            outtakePowerTune += stepSizes[stepIndex];
        } else if (gamepad1.dpadDownWasPressed()) {
            outtakePowerTune -= stepSizes[stepIndex];
        }

    }
}
// --- 4. READY TO SHOOT CHECKS ---
        