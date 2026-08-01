package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AarushHelper {

    // =========================================================================
    // 1. OUTTAKE DISTANCE REGRESSION EQUATION
    // =========================================================================
    public double a = 0.000135566;
    public double b = -0.010399;
    public double c = 0.650286;

    public double getOuttakePower(double currentDistance) {
        double power = (a * Math.pow(currentDistance, 2)) + (b * currentDistance) + c;
        return Range.clip(power, 0.0, 1.0); // Keeps power safely between 0.0 and 1.0
    }

    // =========================================================================
    // 2. LIMELIGHT DISTANCE CALCULATION
    // =========================================================================
    // PHYSICAL MEASUREMENTS (Adjust these for your robot & field)
    public double goalHeightInches = 40.0;     // h2: Height from floor to target center
    public double cameraHeightInches = 12.5;   // h1: Height from floor to Limelight lens center
    public double cameraMountAngleDeg = 25.8;  // a1: Upward angle tilt of camera (degrees)

    // CALCULATED DISTANCE VARIABLE
    public double distanceInches = 0.0;

    /**
     * Calculates distance using Limelight's vertical angle offset (ty).
     * @param ty Vertical angle offset from limelight.getLatestResult().getTy()
     * @return Distance to target in inches
     */
    public double calculateDistance(double ty) {
        double totalAngleRadians = Math.toRadians(cameraMountAngleDeg + ty);

        if (Math.tan(totalAngleRadians) != 0) {
            distanceInches = (goalHeightInches - cameraHeightInches) / Math.tan(totalAngleRadians);
        } else {
            distanceInches = 0.0;
        }

        return distanceInches;
    }

    // =========================================================================
    // 3. HARDWARE DECLARATIONS
    // =========================================================================

    // Drivetrain Motors
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    // Turret & Ball Handling Hardware
    public DcMotorEx turret;
    public DcMotor intakeMotor;
    public DcMotor rampMotor;
    public DcMotorEx outtakeMotor;
    public Servo flicker;

    // Sensors
    private DigitalChannel limitMagnet;

    // =========================================================================
    // 4. CONTROL & PID VARIABLES
    // =========================================================================
    private double kP = 0.02;             // Smooth gain (prevents jitter)
    private double kD = 0.0003;
    private final double goalX = 0.0;
    private double lastError = 0.0;
    private double angleTolerance = 1.0;  // Tight tolerance for long-range accuracy
    private final double MAX_POWER = 1.0;
    private double power = 0;

    public int ticks = 600;
    private final ElapsedTime timer = new ElapsedTime();

    // =========================================================================
    // 5. INITIALIZATION
    // =========================================================================
    public void init(HardwareMap hwMap) {

        // --- Initialize Drivetrain Motors ---
        frontLeft  = hwMap.get(DcMotor.class, "frontLeft");
        frontRight = hwMap.get(DcMotor.class, "frontRight");
        backLeft   = hwMap.get(DcMotor.class, "backLeft");
        backRight  = hwMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- Initialize Turret & Ball Handling Hardware ---
        turret       = hwMap.get(DcMotorEx.class, "rotateMotor");
        intakeMotor  = hwMap.get(DcMotor.class, "par");
        rampMotor    = hwMap.get(DcMotor.class, "perp");
        outtakeMotor = hwMap.get(DcMotorEx.class, "motorOutake");
        flicker      = hwMap.get(Servo.class, "servoPush");

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rampMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- Initialize Magnetic Limit Switch ---
        limitMagnet = hwMap.get(DigitalChannel.class, "magneticLimitSwitch");
        limitMagnet.setMode(DigitalChannel.Mode.INPUT);
    }

    // =========================================================================
    // 6. DRIVETRAIN METHODS
    // =========================================================================
    /**
     * Drives the robot relative to its own front direction (Robot-Oriented Mecanum Drive)
     */
    public void driveRobotOriented(double drive, double strafe, double turn) {
        double frontLeftPower  = drive + strafe + turn;
        double backLeftPower   = drive - strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backRightPower  = drive + strafe - turn;

        // Normalize motor powers so none exceed 1.0
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        if (maxPower > 1.0) {
            frontLeftPower  /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower   /= maxPower;
            backRightPower  /= maxPower;
        }

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    // =========================================================================
    // 7. BALL HANDLING MECHANISM METHODS
    // =========================================================================
    public void setPowerIntake(double powerIntake) {
        intakeMotor.setPower(powerIntake);
    }

    public void setPowerRamp(double powerRamp) {
        rampMotor.setPower(powerRamp);
    }

    public void setPowerOuttake(double powerOuttake) {
        outtakeMotor.setPower(powerOuttake);
    }

    public void pushBall(double angle) {
        flicker.setPosition(angle);
    }

    // --- READY TO SHOOT METHODS ---
    public boolean isOuttakeReady(double targetPower) {
        if (targetPower <= 0.05) return false;
        double actualVelocity = Math.abs(outtakeMotor.getVelocity());
        double expectedMinVelocity = (targetPower * 2500.0) * 0.90;
        return actualVelocity >= expectedMinVelocity;
    }

    public boolean isTurretAligned(double currentError) {
        return Math.abs(currentError) < angleTolerance;
    }

    // =========================================================================
    // 8. MAGNETIC LIMIT SWITCH METHODS
    // =========================================================================
    public boolean getLimitSwitch() {
        return !limitMagnet.getState();
    }

    public void resetEncoder() {
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // =========================================================================
    // 9. TURRET PID CONTROL & TARGETING WITH FEEDFORWARD
    // =========================================================================
    public void setKP(double newKP) { kP = newKP; }
    public double getKP() { return kP; }

    public void setKD(double newKD) { kD = newKD; }
    public double getKD() { return kD; }

    public void resetTimer() { timer.reset(); }

    public void update(double targetAngle, double driverTurn) {
        double deltaTime = timer.seconds();
        timer.reset();

        double error = goalX - targetAngle;
        double pTerm = error * kP;
        double dTerm = 0;

        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        // 1. If inside tolerance, cut power completely (no drift/jitter)
        if (Math.abs(error) < angleTolerance) {
            power = 0;
        } else {
            // 2. Only add minPower if error > 1.5 deg to avoid micro-bouncing near center
            double minPower = 0;
            if (Math.abs(error) > 1.5) {
                minPower = 0.06 * Math.signum(error);
            }

            // 3. Feedforward to counter driver spins
            double feedforward = driverTurn * 0.4;

            power = Range.clip(pTerm + dTerm + minPower + feedforward, -MAX_POWER, MAX_POWER);
        }

        // Encoder soft safety limits
        if (turret.getCurrentPosition() > ticks && power > 0) {
            power = 0;
        } else if (turret.getCurrentPosition() < -ticks && power < 0) {
            power = 0;
        }

        turret.setPower(power);
        lastError = error;
    }
}