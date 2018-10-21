
import java.lang.Math;

public class MotorControl {

    public final double MOTOR_DISTANCE = 136; // number of pixels between the two motors
    public final double MOTOR_ARM_LENGTH = 241; // NOTE: Not sure about this value
    
    // left motor is at origin
    public final double L_MOTOR_X = 0;
    public final double L_MOTOR_Y = 0;
    
    // right motor is (144, 0)
    public final double R_MOTOR_X = MOTOR_DISTANCE;
    public final double R_MOTOR_Y = 0;
    
    
    /**
     * Calculates distance between motor and point
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double calculateDistance(double xt, double yt, String motor) {
        double distance = 0;
        if (motor.equals("R")) { // right motor
            distance = Math.sqrt(Math.pow(xt - R_MOTOR_X, 2) + Math.pow(yt - R_MOTOR_Y, 2));
        } else if (motor.equals("L")) { // left motor
            distance = Math.sqrt(Math.pow(xt - L_MOTOR_X, 2) + Math.pow(yt-L_MOTOR_Y, 2));
        }
        if (distance > 2*MOTOR_ARM_LENGTH){
            throw new IllegalArgumentException("Distance to tool can't be greater than twice the arm length");
        }
        return distance;
    }
    
    /**
     * Returns array of points x, y (size 2) of midpoint.
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double[] calculateMidpoint(double xt, double yt, String motor) {
        double[] coords = new double[2];
        if (motor.equals("R")) {
            coords[0] = (xt + R_MOTOR_X)/2;
            coords[1] = (yt + R_MOTOR_Y)/2;
        } else if (motor.equals("L")) {
            coords[0] = (xt + L_MOTOR_X)/2;
            coords[1] = (yt + L_MOTOR_Y)/2;
        } 
        return coords;
    }
    
    /**
     * Calculates the distance between the midpoint and joint
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double midDistance(double xt, double yt, String motor) {
        return Math.sqrt(Math.pow(MOTOR_ARM_LENGTH, 2) - Math.pow(calculateDistance(xt, yt, motor)/2, 2));
    }
    
    /**
     * Returns the internal angle of the elbow
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double getElbowAngle(double xt, double yt, String motor) {
        double elbowAngle = 0;
        if (motor.equals("R")) {
            // cos for x, sin for y
            elbowAngle = Math.acos((R_MOTOR_X - xt)/calculateDistance(xt, yt, motor));
        } else if (motor.equals("L")) {
            elbowAngle = Math.acos((xt - L_MOTOR_X)/calculateDistance(xt, yt, motor)); // angle of dist and delta x
        }
        return elbowAngle;
    }
    

    /** ** NOT WORKING ** RETURNS ANGLE NOT POSITIONS
     * Returns the position of the joint bending outwards
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double getJointPositions(double xt, double yt, String motor) {
        double[] joint = new double[2];
        double angle = getElbowAngle(xt, yt, motor);
        double[] midpoint = calculateMidpoint(xt, yt, motor);
        double mdistance = midDistance(xt, yt, motor);


        double x3 = midpoint[0] + mdistance * Math.sin(angle);
        double x4 = midpoint[0] - mdistance * Math.sin(angle);
        double y3 = midpoint[1] - mdistance * Math.cos(angle);
        double y4 = midpoint[1] + mdistance * Math.cos(angle);

        if (motor.equals("R")) { // subtracts distance of second motor
            x3 -= MOTOR_DISTANCE;
            x4 -= MOTOR_DISTANCE;
        }

        double anglePos3 = Math.atan2(y3, x3);      // Make sure angle is positive
        double anglePos4 = Math.atan2(y4, x4);
        if (anglePos3 < 0){
            anglePos3 = 2*Math.PI - anglePos3;
        }
        if (anglePos4 < 0){
            anglePos4 = 2*Math.PI - anglePos4;
        }


        if (motor.equals("R")) {
            // for the right motor makes sure elbow is bending outwards
            if (anglePos3 < anglePos4) {
                joint[0] = x3;
                joint[1] = y3;
                return anglePos3;
            }
            else {
                joint[0] = x4;
                joint[1] = y4;
                return anglePos4;
            }
        } else if (motor.equals("L")) {
            // for the left motor makes sure elbow is bending outwards
            if (anglePos3 > anglePos4) {
                joint[0] = x3;
                joint[1] = y3;
                return anglePos3;
            }
            else {
                joint[0] = x4;
                joint[1] = y4;
                return anglePos4;
            }
        }

        return Double.NaN;
    }

    
    /**
     * Returns the angle that the motor must turn in order to position the pen at xt, yt in DEGREES
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
//    public double getMotorAngle(double xt, double yt, String motor) {
////        double[] joint = getJointPositions(xt, yt, motor);
////        if (motor.equals("R")) { // subtracts distance of second motor
////            joint[0] -= MOTOR_DISTANCE;
////        }
////        System.out.println(motor+" JOINTS");
////        System.out.println("y: "+joint[1]+" x: "+joint[0]);
////        double angle = Math.toDegrees(Math.atan2(joint[1], joint[0]));
////        System.out.println(motor + " " + angle);
////        return angle;
//    }
    
    
    
    
    // TODO: better method names
    public double getMotor1Signal(double xt, double yt){
        System.out.printf("Tool at: x: %f, y: %f\n", xt, yt );
        double a = getJointPositions(xt, yt, "L");
        // -11.429 + 3057.19
        double signal = -18.18*Math.toDegrees(a) + 3663;
        System.out.println("[L motor]    angle = " + Math.toDegrees(a) + "  signal = " + signal);
        return Math.abs(signal);
    }
    
    // TODO: better method names
    public double getMotor2Signal(double xt, double yt){
        double a = getJointPositions(xt, yt, "R");
        // -10.288 + 2093.6
        double signal = -10.536*Math.toDegrees(a) + 2268;
        System.out.println("[R motor]     angle = " + Math.toDegrees(a) + "  signal = " + signal);
        return Math.abs(signal);
    }


    public static void main(String[] arguments){
        new ScanImage();
    }

}
