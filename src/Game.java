import java.applet.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.net.URL;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;
import java.util.*;


public class Game extends Applet implements Runnable, KeyListener
{

	private static final long serialVersionUID = 1L;  //dunno what this is. the KeyListener made me do it.

	//double buffering to avoid flickering images
	Image 		dbImage;
	Graphics 	dbGraphics;
	Color		backgroundColor = new Color(220,255,220);	//light gray, with green tint
	Color		wallColor = new Color(64,128,64);  			// dark gray, with green tint
	Color		pelletColor = Color.white;

	int	WDTH	= 800;
	int	HGHT	= 800;

	Thread 		th = new Thread(this);
	boolean 	gameRunning = false;
	boolean		gamePaused;
	boolean		showDebug;
	int			numPlayers;

	Map[]		maps;
	int			numMaps = 1;
	int			level;
	Map			CurrentLevel;
	int			MapGridSize = 20;
	Snake		s1;
	Snake		s2;
	ArrayList<Pellet>	pellets;
	
	final int	PADDINGTOP = 0;
	final int   PADDINGBOTTOM = 0;
	final int	PADDINGLEFT = 0;
	final int   PADDINGRIGHT = 0;
	final int	POINTSTOADVANCE = 10;
	final int	MaxSnakePause = 120;
	
	//PLAYER CONTROLS
	final int	P1Lt = 65;		// [A]
	final int	P1Rt = 68;		// [D]
	final int	P1Up = 87;		// [W]
	final int	P1Dn = 83;		// [S]
	final int	P2Lt = 37;		// [LEFT]
	final int	P2Rt = 39;		// [RIGHT]
	final int	P2Up = 38;		// [UP]
	final int	P2Dn = 40;		// [DOWN]
	final int	PAUSE = 32;		// [SPACE]
	final int	PLAY1 = 122;	// [F11]
	final int	PLAY2 = 123;	// [F12]
	final int	DEBUG = 114;	// [F3]
	final int	RESET = 80;		// [P]

	
	public void init()
	{
		if( !gameRunning ) 
		{
			//perform these steps on the first-game only, not subsequent games
			addKeyListener(this);
			
			//Window Size.
			this.setSize(new Dimension(WDTH, HGHT));
		}

		gameRunning	= true;
		gamePaused	= true;
		showDebug	= false;
		numPlayers	= 1;
		level = 0;
 
		//define ALL MAPS
		maps = new Map[numMaps];
		Map m = new Map(WDTH, HGHT, MapGridSize);
		//m.addWall_box(m.width/4, m.height/4, (m.width/4)*3, (m.height/4)*3);
		m.p1x = (3*MapGridSize);
		m.p1y = HGHT-(8*MapGridSize);
		m.p2x = WDTH-(3*MapGridSize);
		m.p2y = HGHT-(8*MapGridSize);
		maps[0] = m;
		//
		// add more maps
		//
		
		//put Snake(s) on map
		reinitializeMap();
	}

	 
	public void start()
	{
		th.start();
	}

	
	public void stop()
	{
		gameRunning = false;
	}

	
	public void destroy()
	{
		gameRunning = false;
	}

	
	public void run()
	{
		
		while (gameRunning)
		{
			if (!gamePaused)
			{
				//============================
				//Move the Snake's Position
				//============================
				//TODO: turn the else clauses into a function like ProcessSnakePauseTime(Snake s)
				processSnakeMovement (s1);
				if( numPlayers==2 ) processSnakeMovement (s2);

				//============================
				//Add Pellets ?
				//============================
				// maintain at least 1 pellet per player
				//if( pellets.size()<numPlayers )
				//	pellets.add(new Pellet("pellet", 1, CurrentLevel.grid, CurrentLevel.getRandomEmptyCoord(), true, pelletColor));
					
				//TODO: pellet rules
					//remove old pellets?
					//pellets apply to a specific player?
					//pellets drift/move?
					//pellets decay? (value or size)
					//pellets grow?
					//pellets flash
					//pellets spawn for other reasons?

				//============================
				// Detect Collisions
				//============================
				//
			

				
				//Snake on Wall
				s1.isColliding |= Collision_SnakeWall(s1); // Snake on Wall collision
				if( numPlayers==2 )
					s2.isColliding |= Collision_SnakeWall(s2); // Snake on Wall collision

				// Snake on Snake
				s1.isColliding |= Collision_SnakeSnake(s1); 
				if( numPlayers==2 )
					s2.isColliding |= Collision_SnakeSnake(s2);

				//TODO:
				// Snake on Snake collision
				// Snake on Pellet collision
				// Pellet on Wall collision (if pellets move)
				// Pellet on Pellet collision (if pellets move)

				//============================
				// Apply Collisions
				//============================
				//Mark new tile=Snake if able
				MarkSnakeTiles(s1);
				MarkSnakeTiles(s2);
				
				
				
				
				
				
				
				//TODO: unless Snake has special powerup, etc, collisions result in:
				
				
				
				
				
				
				
				//  - snake death
				if( s1.isColliding || s2.isColliding )
				{
					//TODO: pause both snakes, highlight the err
					//TODO: keep rendering until Snake's ErrPause runs out, then reset all
					
					//reset CurrentLevel
					//reinitializeMap();
					
					//reset the collided snake's score
					//retain the surviving snake's score
					//reset all other attributes for both snakes (position, length, speed, etc)
					//pause both snakes
					s1.isPaused = true;
					s2.isPaused = true;
				}
					
				//  - reset of that snake's score to that from beginning of level 
				//  - (other snake(s) retain score)
				//  - reset of entire map for both snakes 
				
					
					
				//============================
				//PRINT DEBUG INFO
				//============================

				
				if(showDebug)
				{
					if (s1.turning!=null)System.out.println(s1.name + s1.head.toString() + " dir:"+s1.facing.toString() + " turning:"+s1.turning.toString() + " in " + s1.distUntilTurn);
					if (s2.turning!=null)System.out.println(s2.name + s2.head.toString() + " dir:"+s2.facing.toString() + " turning:"+s2.turning.toString() + " in " + s2.distUntilTurn);
					/*
					System.out.println("Ball    X:"+b.getX()+" Y:"+b.getY()+ " RADIUS:"+b.getRadius());
					System.out.println("Paddle1 X:"+p1.getX()+" Y:"+p1.getY()+ " WIDTH:"+p1.getWidth()+" HEIGHT:"+p1.getHeight());
					System.out.println("Paddle2 X:"+INITIALPADDLE2X+" Y:"+p2.getY()+ " WIDTH:"+p2.getWidth()+" HEIGHT:"+p2.getHeight());
					System.out.println("Scores: " + p1.getScore() + " to " + p2.getScore());
					*/
				}
			}
			else
			{
				//game is paused.  draw menu, check for input, react accordingly.  beep boop
			}
			
			//===============================
			// REPAINT! to update the screen
			//===============================
			repaint();  // clear screen then draw things again.  introduces flickering.  (solved by using doublebuffering)
			try
			{
				// This sets the Frames Per Second, not the ball speed.
				Thread.sleep(12); //delay.  20ms = 50 frames per second
				if( showDebug ) Thread.sleep(38);
			}
			catch(InterruptedException ie)
			{
				
			}
		}
	}
	
	
	public boolean Collision_SnakeWall(Snake s)
	{
		boolean collision = false;
		Map.tile tmpTile = CurrentLevel.getTileAt(s.head);
		if( tmpTile.name()=="wall" )
		{
			collision=true;
		}
		return collision;
	}

	public boolean Collision_SnakeSnake(Snake s)
	{
		boolean collision = false;
		Map.tile tmpTile = CurrentLevel.getTileAt(s.head);
		if( (tmpTile.name()=="snake1" && s.name!=s1.name) ||
			(tmpTile.name()=="snake2" && s.name!=s2.name) )
		{
			collision=true;
		}
		//TODO: bugfix: snake1 registers crash in head-on, but not snake2, due to snake2 more recently setting the tile name.
		// make it so both snakes register the crash... somehow.

		return collision;
	}

	
	public void MarkSnakeTiles(Snake s)
	{
		Map.tile t = CurrentLevel.getTileAt(s.head);
		
		//define tileAt(s.head) as a Tile.Snake#
		if( s.name==s1.name )
			if( t.name()=="pellet" || t.name()=="blank" )
				//if snake1
				CurrentLevel.setTileAt(s.head, Map.tile.snake1);
			else
			{
				//error, or duplicate call per snake-on-tile
			}

		//define tileAt(s.head) as a Tile.Snake#
		if( s.name==s2.name )
			if( t.name()=="pellet" || t.name()=="blank" )
				//if snake2
				CurrentLevel.setTileAt(s.head, Map.tile.snake2);
			else
			{
				//error, or duplicate call per snake-on-tile
			}

		//mark tileAt(s.tail_prev) as Tile.Blank
		if( s.tail_prev!=null )
			CurrentLevel.setTileAt(s.tail_prev, Map.tile.blank);
	
		//update tail_prev before moving snake
		s.tail_prev = s.tail;

	}
	
	
	public void processSnakeMovement(Snake s)
	{
		
		//move snake
		if( !s.isPaused ) s.moveSnake();
		else
			if( s.pauseTime--<=0 )
			{
				s.pauseTime = s.maxPause;
				s.isPaused = false;
			}
	}


	//override update() to handle double buffering, avoid flickering
	public void update(Graphics g)
	{
		//Initial Drawing
		if(dbImage == null)
		{
			dbImage = createImage(WDTH, HGHT);
			dbGraphics = dbImage.getGraphics();
		}

		dbGraphics.setColor(backgroundColor);
		dbGraphics.fillRect(0, 0,  WDTH, HGHT);
		dbGraphics.setColor(this.getForeground());
		paint(dbGraphics);

		//finally, draw the items
		g.drawImage(dbImage, 0, 0, this);
	}
	
	
	//Draw each object to the screen once. The repaint() method will call this method
	public void paint(Graphics g)
	{
		//g.setColor(Color.black);
		//g.fillRect(0, 0,  WDTH, HGHT);
		
		//draw the current map
		g.setColor(wallColor);
		int grid = CurrentLevel.grid;
		int w = CurrentLevel.width;
		int h = CurrentLevel.height;
		for( int x=0; x<w; x++)
		{
			g.drawLine(0, x*grid, WDTH, x*grid);
			g.drawLine(x*grid, 0, x*grid, HGHT);
			for( int y=0; y<h; y++)
				if( CurrentLevel.map[x][y]==Map.tile.wall )
				{
					g.fillRect((x*grid)-(grid/2), (y*grid)-(grid/2), grid, grid);
				}	
		}

		//draw pellet(s)
		for( int i=0; i<pellets.size(); i++ )
		{
			g.setColor(pelletColor);
			g.drawRect(50, 50, 60, 60);
		}
		
		//draw every segment of the snake
		drawSnake(g, s1);
		if( numPlayers==2 ) drawSnake(g, s2);
		
		//draw temporary grid, showing where snake can turn.
		g.setColor(wallColor);
		
		//DRAW PAUSE MENU
		if( gamePaused )
		{
			int widthCenter = WDTH/2;
			int widthThird = WDTH/3;
			int heightThird = HGHT/3;
			
			g.setColor(wallColor);
			g.fillRoundRect(widthThird, heightThird, widthThird, heightThird, WDTH/20, HGHT/20);
			g.setColor(backgroundColor);
			g.setFont(new Font("monospaced", Font.BOLD, 22));
			g.drawString("P A U S E D", widthCenter - 72, heightThird + 30);
			g.setFont(new Font("monospaced", Font.BOLD, 18));
			g.drawString("One Player - [F11] key", widthCenter - 132, heightThird + 100);
			g.drawString("Two Player - [F12] key", widthCenter - 132, heightThird + 140);
			g.drawString("Show Debug - [F3] key", widthCenter - 132, heightThird + 180);
			g.drawString("Unpause    - [SPACE BAR]", widthCenter - 132, heightThird + 220);
		}

		//DRAW WINNING MENU
		//winning conditions
		if( s1.score + s2.score >= POINTSTOADVANCE  )
		{
			s1.isPaused = true;
			s2.isPaused = true;
			s1.pauseTime = s1.maxPause;
			s2.pauseTime = s2.maxPause;
			// draw end-game menu offer keys to quit and to play again.
		}
	}

	
	private void drawSnake(Graphics g, Snake s)
	{
		
		//draw Snake1 head
		//g.setColor(s1.color);
		//g.fillRoundRect(s1.head.x-(s1.width/2), s1.head.y-(s1.width/2), s1.width, s1.width, s1.width/2, s1.width/2);

		//draw Snake1 tail
		//g.setColor(Color.white);
		//g.fillRoundRect(s1.tail.x-(s1.width/4), s1.tail.y-(s1.width/4), s1.width/2, s1.width/2, s1.width/2, s1.width/2);

		//draw every corner in Snake1
		//g.setColor(Color.black);
		//for(int i = 0; i<s1.corners.size(); i++)
		//{
		//	Coord c = s1.corners.get(i);
		//	g.fillRect(c.x-(s1.width/4), c.y-(s1.width/4), s1.width/2, s1.width/2);

		
		int grid = s.width;
		if( !s.isColliding ) 
			g.setColor(s.color);
		else
			g.setColor(s.color_inv);
		Coord pnt1 = s.head;
		Coord pnt2;
		for(int i=s.corners.size()-1; i>=0; i--)
		{
			pnt2 = s.corners.get(i); //get next point
			drawSegment(g, pnt1, pnt2, grid); // draw segment between 2 points
			pnt1 = pnt2;  // iterate points
		}
		//draw line from last pnt to tail
		drawSegment(g, pnt1, s.tail, grid);
	}
	
	
	private void drawSegment(Graphics g, Coord c1, Coord c2, int grd)
	{

		int buffer = (int)(0.2*grd);
		int width = Math.abs(c1.getHorizontalDistanceTo(c2));
		int height = Math.abs(c1.getVerticalDistanceTo(c2));
		
		if( c1.isDirectlyAbove(c2) )
			g.fillRoundRect(c1.x-(grd/2)+buffer, c1.y-(grd/2)+buffer, grd-(2*buffer), height+(grd-(2*buffer)), grd/2, grd/2);
		if( c1.isDirectlyBelow(c2) )
			g.fillRoundRect(c1.x-(grd/2)+buffer, c2.y-(grd/2)+buffer, grd-(2*buffer), height+(grd-(2*buffer)), grd/2, grd/2);
		if( c1.isDirectlyLeftOf(c2) )
			g.fillRoundRect(c1.x-(grd/2)+buffer, c1.y-(grd/2)+buffer, width+(grd-(2*buffer)), grd-(2*buffer), grd/2, grd/2);
		if( c1.isDirectlyRightOf(c2) )
			g.fillRoundRect(c2.x-(grd/2)+buffer, c2.y-(grd/2)+buffer, width+(grd-(2*buffer)), grd-(2*buffer), grd/2, grd/2);
	}
	
	
	public void playSound(String sound)
	{
		try {
		     URL defaultSound = getClass().getResource(sound);
		     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
		     Clip clip = AudioSystem.getClip();
		     clip.open(audioInputStream);
		     clip.start( );
		} catch (Exception ex) {
		     ex.printStackTrace();
		}
	}
	
	
	public void initSound(String sound)
	{
		try {
		     URL defaultSound = getClass().getResource(sound);
		     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
		     Clip clip = AudioSystem.getClip();
		     clip.open(audioInputStream);
		     clip.start();
		     clip.stop();
		} catch (Exception ex) {
		     ex.printStackTrace();
		}
	}
	

	public void keyTyped(KeyEvent ke) 	{	}


	public void keyPressed(KeyEvent ke)
	{		
		//System.out.println("User Pressed Key: " + KeyEvent.getKeyText(ke.getKeyCode()) + " KeyChar: "+ke.getKeyChar()+" KeyCode: "+ke.getKeyCode());

		//P1 Check for vertical movement change.
		Snake.direction d = s1.facing;
		if( d==Snake.direction.left || d==Snake.direction.right )
		{
			if (ke.getKeyCode() == P1Up)
				s1.turning=Snake.direction.up;
			if (ke.getKeyCode() == P1Dn)
				s1.turning=Snake.direction.down;			
		}

		//P1 Check for horizontal movement change.
		if( d==Snake.direction.up || d==Snake.direction.down)
		{
			if (ke.getKeyCode() == P1Lt)
				s1.turning=Snake.direction.left;
			if (ke.getKeyCode() == P1Rt)
				s1.turning=Snake.direction.right;			
		}

		
		//P2 Check for vertical movement change.
		d = s2.facing;
		if( d==Snake.direction.left || d==Snake.direction.right )
		{
			if (ke.getKeyCode() == P2Up)
				s2.turning=Snake.direction.up;
			if (ke.getKeyCode() == P2Dn)
				s2.turning=Snake.direction.down;			
		}

		//P2 Check for horizontal movement change.
		if( d==Snake.direction.up || d==Snake.direction.down)
		{
			if (ke.getKeyCode() == P2Lt)
				s2.turning=Snake.direction.left;
			if (ke.getKeyCode() == P2Rt)
				s2.turning=Snake.direction.right;			
		}
		
		//PAUSE UNPAUSE THE GAME
		if (ke.getKeyCode() == PAUSE)
			gamePaused = !gamePaused;

		//TOGGLE To 1-player game
		if (ke.getKeyCode() == PLAY1)
		{
				numPlayers=1;
				reinitializeMap();
		}

		//TOGGLE To 2-player game
		if (ke.getKeyCode() == PLAY2)
		{
				numPlayers=2;
				reinitializeMap();
		}


		//TOGGLE DEBUG INFO
		if (ke.getKeyCode() == DEBUG)
			showDebug = !showDebug;


	}
	
	
	public void keyReleased(KeyEvent ke) { }

	
	public void reinitializeMap() 
	{
		if( showDebug ) System.out.println("Toggled Number of Players: " + numPlayers);
	
		//set the map
		CurrentLevel = maps[level];
		
		//reInitialize all snakes. set each Snake on the board, per numPlayers
		s1 = new Snake("Snake1", new Color(0,128,0), 5, (CurrentLevel.width/2)*(CurrentLevel.width/CurrentLevel.grid), CurrentLevel.grid, CurrentLevel.p1x, CurrentLevel.p1y, Snake.direction.up, 1f, CurrentLevel.grid);
		if( numPlayers==2 ) 
			s2 = new Snake("Snake2", new Color(25,25,112), 5,(CurrentLevel.width/2)*(CurrentLevel.width/CurrentLevel.grid), CurrentLevel.grid, CurrentLevel.p2x, CurrentLevel.p2y, Snake.direction.up, 1f, CurrentLevel.grid);
		else
			s2 = new Snake();
		
		//set the pellets
		pellets = new ArrayList<Pellet>(numPlayers);
	}

}