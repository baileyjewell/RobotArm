
import java.lang.Math;

public class MotorControl {

    public final double MOTOR_DISTANCE = 144; // number of pixels between the two motors
    public final double MOTOR_ARM_LENGTH = 244; // NOTE: Not sure about this value
    
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
            distance = Math.sqrt((xt - L_MOTOR_X)*(xt-L_MOTOR_X)+(yt-L_MOTOR_Y)*(yt-L_MOTOR_Y));
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
    
    /** **NOTE: Not actually sure about what this calculates?**
     * Returns the angle between the motor and the joint MAYBE???????
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double[] getElbowAngle(double xt, double yt, String motor) {
        double[] a = new double[2];
        if (motor.equals("R")) {
            // cos for x, sin for y
            a[0] = Math.acos((R_MOTOR_X - xt)/calculateDistance(xt, yt, motor));
            a[1] = Math.asin((R_MOTOR_Y - yt)/calculateDistance(xt, yt, motor));
        } else if (motor.equals("L")) {
            a[0] = Math.acos((L_MOTOR_X - xt)/calculateDistance(xt, yt, motor));
            a[1] = Math.asin((L_MOTOR_Y - yt)/calculateDistance(xt, yt, motor));
        }
        return a;
    }
    
    
    /**
     * Returns the position of the joint bending outwards
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double[] getJointPositions(double xt, double yt, String motor) {
        double[] joint = new double[2];
        double[] angles = getElbowAngle(xt, yt, motor);
        double[] midpoint = calculateMidpoint(xt, yt, motor);
        double distance = midDistance(xt, yt, motor);
        
        double x3 = midpoint[0] + distance * Math.sin(angles[0]);
        double x4 = midpoint[0] - distance * Math.sin(angles[0]);
        double y3 = midpoint[0] - distance * Math.cos(angles[1]);
        double y4 = midpoint[0] + distance * Math.cos(angles[1]);
        
        if (motor.equals("R")) {
            // for the right motor makes sure elbow is bending outwards
            if (x3 >= x4) {
                joint[0] = x3;
                joint[1] = y3;
            }
            else {
                joint[0] = x4;
                joint[1] = y4;
            }
        } else if (motor.equals("L")) {
            // for the left motor makes sure elbow is bending outwards
            if (x3 < x4) {
                joint[0] = x3;
                joint[1] = y3;
            }
            else {
                joint[0] = x4;
                joint[1] = y4;
            }
        }
        return joint;
    }
    
    /**
     * Returns the angle that the motor must turn in order to position the pen at xt, yt in DEGREES
     * xt, yt = pen coords
     * motor = "L" or "R"
     */
    public double getMotorAngle(double xt, double yt, String motor) {
        double[] joint = getJointPositions(xt, yt, motor);
        if (motor.equals("R")) { // subtracts distance of second motor
            joint[0] -= MOTOR_DISTANCE;
        }
        return Math.toDegrees(Math.atan2(joint[0], joint[1]));
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // TODO: better method names
    public double getMotor1Signal(double xt, double yt){
        double a = getMotorAngle(xt, yt, "L");
        return -10.57*a + 2690;
    }
    
    // TODO: better method names
    public double getMotor2Signal(double xt, double yt){
        double a = getMotorAngle(xt, yt, "R");
        return -10.288*a + 2093.6;
    }


    public static void main(String[] arguments){
        new ScanImage();
    }

}
