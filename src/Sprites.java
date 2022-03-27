import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprites{
    public Vector position;
    public Vector velocity;
    public double rotation;
    public Rectangle boundary;
    public Image image;

    // constructors
    public Sprites() {
        this.position = new Vector();
        this.velocity = new Vector();
        this.rotation = 0;
        this.boundary = new Rectangle();
    }
    public Sprites(String imgFile){
        this();
        setImage(imgFile);
    }

    public void setImage(String imgFile){
        this.image = new Image(imgFile);
        this.boundary.setSize(this.image.getWidth(), this.image.getHeight());
    }
    public Rectangle getBoundary(){
        this.boundary.setPosition(this.position.x, this.position.y);
        return this.boundary;
    }

    public boolean overlap(Sprites others){
        return this.getBoundary().overlap(others.getBoundary());
    }
    //update the position
    public void update(double deltaTime){
        this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
    }

    public void render(GraphicsContext context){
        context.save();
        context.translate(this.position.x, this.position.y);
        context.rotate(this.rotation);
        context.translate(-this.image.getWidth()/2, -this.image.getHeight()/2); //translate the image
        context.drawImage(this.image, 0, 0); //take the image
        context.restore();
    }

}
