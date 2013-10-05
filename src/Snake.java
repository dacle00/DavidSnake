import java.awt.*;

public class Snake {
	

	public String name;
	public int length;
	public int width;
	public int speed;
	public Dimension head;
	public Dimension tail;
	public Dimension[] corners;
	private enum direction {up, down, left, right};
	public direction facing;
	
	
	public Snake() {
		name	= "Snake";
		length	= 1;
		width	= 2;
		head	= new Dimension(0,0);
		tail	= new Dimension(0,0);
		corners	= new Dimension[25];
		facing	= direction.up;
	}
	
	
	public Snake(String pName, int pLength, int pWidth, Dimension headLoc, Dimension tailLoc, direction dir) {
		name = pName;
		length = pLength;
		width = pWidth;
		head = headLoc;
		tail = tailLoc;
		corners = new Dimension[25];
		facing = dir;
	}

}
