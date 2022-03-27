public class Vector {
    public double x; //x
    public double y; //y

    //constructors
    public Vector() {
        this.setXY(0, 0);
    }
    public Vector(double x, double y){
        this.setXY(x,y);
    }

    //setter for x and y
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // a method to add value to x and y
    public void add(double dx, double dy){
        this.x += dx;
        this.y += dy;
    }
    // a  method to multiply x and y
    public void multiply(double m){
        this.x *= m;
        this.y *= m;
    }
    // get the distance/length
    public double getLength(){
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }
    // set the distance/length
    public void setLength(double L){
        double currentLength = this.getLength();
        //detect divided by 0
        if(currentLength == 0){
            return;
        }
        else{
            this.multiply(1/currentLength);
            this.multiply(L);
        }
    }

    //get angle method
    public double getAngle(){
        return Math.toDegrees(Math.atan2(this.y, this.x));
    }
    //set angle method
    public void setAngle(double angleDegrees){
        double L = this.getLength();
        double angleRadians = Math.toRadians(angleDegrees);
        this.x = L * Math.cos(angleRadians);
        this.y = L * Math.sin(angleRadians);
    }
}
