public class Rectangle {
    //(x,y) stands for the top-left corner of rectangle
    double x;
    double y;
    double width;
    double height;

    //constructors
    public Rectangle(){
        this.setPosition(0,0);
        this.setSize(1,1);
    }
    public Rectangle(double x, double y, double width, double height) {
        this.setPosition(x,y);
        this.setSize(width,height);
    }
    //setter for setting position
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    //setter for setting size
    public void setSize(double width, double height){
        this.width = width;
        this.height = height;
    }
    // There are 4 cases that there is no overlap
    public boolean overlap(Rectangle others){
        boolean notOverlap =
                (this.x + this.width < others.x) ||
                (others.x + others.width < this.x) ||
                (this.y + this.height < others.y) ||
                (others.y + others.height < this.y);

        return !notOverlap;
    }
}
