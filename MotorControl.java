

public class MotorControl {

    public double getMotor1Angle(float a){
        return -10.57*a + 2690;
    }

    public double getMotor2Angle(float a){
        return -10.288*a + 2093.6;
    }

    public void movePen(){

        float angle = 10;

        double m1 = getMotor1Angle(angle);
        double m2 = getMotor2Angle(angle);

    }

    public static void main(String[] arguments){
        new ScanImage();
    }

}
