import java.awt.*;

public class Snake
{
	public String name;
	public Color color;
	public int length;
	public int width;
	public float speed;
	public int speedMAX;
	public int distUntilTurn;
	public int headX, headY;
	public int tailX, tailY;
	public int[][] corners;
	public enum direction {up, down, left, right};
	public direction facing;
	public direction turning;
	public boolean isPaused = false;
	public int maxPause = 120;
	public int pauseTime;
	public int score;
	
	
	public Snake()
	{
		name	= "Snake";
		color	= Color.green;
		length	= 5;
		width	= 2;
		headX	= 0;
		headY	= 0;
		tailX	= 0;
		tailY	= 0;
		corners	= new int[2][25];
		facing	= direction.up;
		isPaused = false; //todo TRUE
		pauseTime = 120;
		score = 0;
		speed = 0;
		distUntilTurn = -1;
		speedMAX=10;
	}
	
	//new snake, has length, but will only occupy 1 spot to start.
	public Snake(String pName, Color c, int pLength, int pWidth, int headXLoc, int headYLoc, direction dir, float spd, int spdMax)
	{
		name = pName;
		color = c;
		length = pLength;
		width = pWidth;
		headX = headXLoc;
		headY = headYLoc;
		corners = new int[2][25];
		facing = dir;
		score = 0;
		speed = spd;
		speedMAX = spdMax;
		distUntilTurn = -1;		
	}

}
