import java.awt.*;
import java.util.*;

public class Snake
{
	public String name;
	public Color color;
	public int length;
	public int lengthMAX;
	public int width;
	public float speed;
	public int speedMAX;
	public int distUntilTurn;
	public Coord head;
	public Coord tail;
	public Coord tail_prev;  //used for tracking when tail catches up to a corner
	public LinkedList<Coord> corners;
	public enum direction {up, down, left, right, NONE};
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
		width	= 2;
		length	= 5;
		lengthMAX	= 9999;
		speed = 0;
		speedMAX=10;
		corners = new LinkedList<Coord>();
		corners.clear();
		// corners	= new int[lengthMAX/width][lengthMAX/width];  //represents the maximum possible turns a snake could make within its allowed length
		facing	= direction.up;
		head = new Coord();
		tail = placeTail();
		isPaused = false; //todo TRUE
		pauseTime = 120;
		score = 0;
		distUntilTurn = -1;
	}
	
	//new snake, has length, but will only occupy 1 spot to start.
	public Snake(String pName, Color c, int pLength, int pLengthMax, int pWidth, int headXLoc, int headYLoc, direction dir, float spd, int spdMax)
	{
		name = pName;
		color = c;
		width = pWidth;
		length = pLength;
		lengthMAX	= pLengthMax;
		speed = spd;
		speedMAX = spdMax;
		corners = new LinkedList<Coord>();
		corners.clear();
		//corners = new int[2][25];
		facing = dir;
		head = new Coord(headXLoc, headYLoc);
		tail = placeTail();
		score = 0;
		distUntilTurn = -1;		
	}
	
	
	public Coord placeTail()
	{
		if( facing==direction.up )
			return new Coord(head.x, (head.y+(width*length)));
		if( facing==direction.down )
			return new Coord(head.x, (head.y-(width*length)));
		if( facing==direction.left )
			return new Coord((head.x+(width*length)), head.y);
		if( facing==direction.right )
			return new Coord((head.x+(width*length)), head.y);
		return(head);
	}

	
	public void moveSnake()
	{
		//check if turn is requested.
		// - if no turn requested, simply move forward the full speed( total move distance).
		// - if turn requested: compare speed(total move distance)
		//    - if distUntilTurn == 0:  turn and move full speed.
		//    - if distUntilTurn>0 AND distUntilTurn>speed:  move full speed
		//    - if distUntilTurn<speed - move distanceToTurn, turn, move remaining distance.
		//recalc distUntilTurn
		int dut = Math.abs(distUntilTurn);
		float spd = Math.abs(speed);
		Snake.direction dir_facing = facing;
		Snake.direction dir_turn = turning;
		
		if( dir_turn!=null && dir_turn!=dir_facing )
		{
			//turn is requested
			if( dut==0 )
			{
				//turn
				turnSnake();
				//move full speed
				forward(spd);
			}
			else if(dut>=spd )
			{
				//move full speed
				forward(spd);
			}
			else if( dut>spd )
			{
				//move dut
				forward(dut);
				//turn
				turnSnake();
				//move remaining distance
				forward(spd-dut);
			}
			else
			{
				//THIS SHOULD NEVER HAPPEN
				System.err.println("ERROR IN MOVEMENT FLOW");
				System.out.println("ERROR IN MOVEMENT FLOW");
			}
		}
		else
		{
			//assume no turn is requested. move full distance.
			forward(spd);
		}
		
		//recalculate dut and store to snake.
		calculateDistanceUntilTurn();
		
	}
	
	
	private void forward(float distance)
	{
		//logic for moving in a straight line, the distance specified
		int x = head.x;
		int y = head.y;
		direction tailDir = getTailDir();
	
		//move head forward
		if( facing==direction.left )
			head.x = (int)(x-distance);
		else if( facing==direction.right )
			head.x = (int)(x+distance);
		else if( facing==direction.up )
			head.y = (int)(y-distance);
		else if( facing==direction.down )
			head.y = (int)(y+distance);
		
		//determine if tail should move
		if( !tail.equals(head) && !corners.contains(tail) )
		{
			//move tail "forward"
			if( tailDir==direction.left )
				tail.x = (int)(x-distance);
			else if( tailDir==direction.right )
				tail.x = (int)(x+distance);
			else if( tailDir==direction.up )
				tail.y = (int)(y-distance);
			else if( tailDir==direction.down )
				tail.y = (int)(y+distance);
		}
		else
		{
			//tail is equal to head or to a corner, or there was an error.  tail should not move
		}
		
	}
	
	
	private void turnSnake()
	{
		//record a corner[][]
		corners.add(new Coord(head.x, head.y));
		
		//change the snake's direction
		facing = turning;
		
		//remove the snake's request to turn
		turning = null;
	}
	
	
	private void calculateDistanceUntilTurn()
	{
		int x = Math.abs(head.x);
		int y = Math.abs(head.y);
		
		if( facing==direction.right || facing==direction.left)
		{
			if( x%width==0 )
				distUntilTurn = 0;
			else if( facing==direction.right )
				distUntilTurn = width - (x%width);
			else if( facing==direction.left )
				distUntilTurn = x%width;
			else
			{
				//THIS SHOULD NEVER HAPPEN
				System.err.println("ERROR IN MOVEMENT FLOW");
				System.out.println("ERROR IN MOVEMENT FLOW");
				distUntilTurn = -1;
			}
		}
		else if( facing==direction.down || facing==direction.up )
		{
			if( y%width==0 )
				distUntilTurn = 0;
			else if( facing==direction.down )
				distUntilTurn = width - (y%width);
			else if( facing==direction.up )
				distUntilTurn = y%width;
			else
			{
				//THIS SHOULD NEVER HAPPEN
				System.err.println("ERROR IN MOVEMENT FLOW");
				System.out.println("ERROR IN MOVEMENT FLOW");
				distUntilTurn = -1;
			}
		}
	}
	

	public direction getTailDir()
	{
		//if the tail Coord and the oldest corner cord are equal:
		//    - remove the oldest coord
		//use the tail and the oldest corner to determine which direction the tail should move.
		
		Coord oldestCorner;
		direction d = direction.NONE;
		while( !corners.isEmpty()  && corners.getFirst().equals(tail) )
			corners.removeFirst();
				
		if( !corners.isEmpty() )
		{
			oldestCorner = corners.getFirst();
					
			//LEFT or RIGHT
			if( tail.y==oldestCorner.y )
				if( tail.x<oldestCorner.x )
					d = direction.right;
				else
					d = direction.left;
			else if( tail.x==oldestCorner.x )
				if( tail.y<oldestCorner.y )
					d = direction.up;
				else
					d = direction.down;
			else
			{
				//THIS SHOULD NEVER HAPPEN
				System.err.println("ERROR IN TAIL MOVEMENT FLOW");
				System.out.println("ERROR IN TAIL MOVEMENT FLOW");
			}
		}
		else
			d = direction.NONE;  //list of corners was empty

		return d;
	}
	
}





