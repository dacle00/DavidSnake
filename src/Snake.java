import java.awt.*;

public class Snake
{
	public String name;
	public int length;
	public int width;
	public int speed;
	public Dimension head;
	public Dimension tail;
	public Dimension[] corners;
	public enum direction {up, down, left, right};
	public direction facing;
	public boolean paused;
	public int maxPause;
	
	
	public Snake()
	{
		name	= "Snake";
		length	= 5;
		width	= 2;
		head	= new Dimension(0,0);
		tail	= new Dimension(0,0);
		corners	= new Dimension[25];
		facing	= direction.up;
		paused = true;
		maxPause = 120;
	}
	
	//new snake, has length, but will only occupy 1 spot to start.
	public Snake(String pName, int pLength, int pWidth, Dimension headLoc, direction dir)
	{
		name = pName;
		length = pLength;
		width = pWidth;
		head = headLoc;
		corners = new Dimension[25];
		facing = dir;
		
		
	}

}
