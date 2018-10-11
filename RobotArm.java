class RobotArm {
    
    public RobotArm() {}
    
    private ArrayList<ArrayList<Boolean>> image;
    
    // holds (x, y) in order of drawing
    private ArrayList<ArrayList<Double>> drawOrder;

    //// DRAWING METHODS ////

    /**
     * Draws a straight line from (x1, y1) to (x2, y2) 
     */
    private void drawLine(double x1, double y1, double x2, double y2) {
        if (currentX != x1 || currentY != y1) {
            moveTo(x1, y1);
        }

        // draw line between (x1, y1) and (x2, y2)

        currentX = x2;
        currentY = y2;
    }

    /**
     * Draws a circle centred at centre, with radius radius
     */
    private void drawCircle(double centre, double radius) {}

    /**
     * Lifts the pen and moves in a straight line from (x1, y1) to (x2, y2)
     */
    private void moveTo(double x1, double y1, double x2, double y2) {
        // pen up
        // move from (currentX, currentY) to (x2, y2)
        // pen down

        currentX = x2;
        currentY = y2;
 
     }

    private void drawImage() {
        double x2 = drawOrder.get(0).get(0); // next point to draw
        double y2 = drawOrder.get(0).get(1);
        moveTo(x2, y2);
        for (int i = 1; i < drawOrder.size(); i++) {
            x2 = drawOrder.get(i).get(0);
            y2 = drawOrder.get(i).get(1);
            drawLine(currentX, currentY, x2, y2);
        }
    }

    

    

    public static void main(String[] args) {
        System.out.println("RobotArm started!");
    }
}
