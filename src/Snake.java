import java.awt.*;

public class Snake {
	

	public String name;
	public int length;
	public int width;
	public int speed;
	public Dimension head;
	public Dimension tail;
	public Dimension[] corners;
	
	
	public Snake() {
		name	= "Snake";
		length	= 1;
		width	= 2;
		head	= new Dimension(0,0);
		tail	= new Dimension(0,0);
		corners	= new Dimension[25];
		
	}
	
	
	public Snake(String pName, int pLength, int pWidth, Dimension headLoc, Dimension tailLoc) {
		name = pName;
		length = pLength;
		width = pWidth;
		head = headLoc;
		tail = tailLoc;
		corners = new Dimension[25];
	}

}
