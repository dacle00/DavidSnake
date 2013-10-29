import java.applet.*;
import java.awt.*;
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
	Color		pelletColor = Color.orange;

	int	WDTH	= 800;
	int	HGHT	= 800;

	Thread 		th = new Thread(this);
	boolean 	gameRunning = false;
	boolean		gamePaused;
	boolean		showDebug;
	int			numPlayers;
	int			numPellets;
	final int	PELLETMAXREWARD = 4;

	Map[]		maps;
	int			numMaps = 1;
	int			level;
	Map			CurrentLevel;
	int			MapGridSize = 25;
	Snake		s1;
	Snake		s2;
	ArrayList<Pellet>	pellets;
	
	final int	PADDINGTOP = 0;
	final int   PADDINGBOTTOM = 0;
	final int	PADDINGLEFT = 0;
	final int   PADDINGRIGHT = 0;
	final int	POINTSTOADVANCE = 150;
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
		m.p1x = (2*MapGridSize);
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
				if( pellets.size()<numPlayers )
					addNewPellet();
					
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
				
				//Snake on Wall
				s1.isColliding |= Collision_SnakeWall(s1); // Snake on Wall collision
				if( numPlayers==2 )
					s2.isColliding |= Collision_SnakeWall(s2); // Snake on Wall collision

				// Snake on Snake
				s1.isColliding |= Collision_SnakeSnake(s1); 
				if( numPlayers==2 )
					s2.isColliding |= Collision_SnakeSnake(s2);

				// Snake on Pellet
				if( Collision_SnakePellet(s1) ) removeExistingPellet(s1);
				if( numPlayers==2 )
					if( Collision_SnakePellet(s2) ) removeExistingPellet(s2);

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
	
	
	
	public void addNewPellet()
	{
		Coord c = CurrentLevel.getRandomEmptyCoord();
		String n = "pellet"+ ++numPellets;
		int r = new Random().nextInt(PELLETMAXREWARD-1) + 1;
		pellets.add(new Pellet(n, r, CurrentLevel.grid, c, true, pelletColor));
		CurrentLevel.setTileAt(c,  Map.tile.pellet);
	}
	
	public void removeExistingPellet(Snake s)
	{
		int grid = CurrentLevel.grid;
		Coord c = new Coord(Math.round(s.head.x/grid), Math.round(s.head.y/grid));
		Pellet p = null;
	
		//remove pellet from CurrentLevel map
		for( int i=0; i<pellets.size(); i++ )
		{
			p = pellets.get(i);
			if( p.location.equals(c) )
			{
				//mark tile as snake#.
				if( s.name==s1.name )
					CurrentLevel.setTileAt(c, Map.tile.snake1);
				else
					CurrentLevel.setTileAt(c, Map.tile.snake2);		

				//remove pellet from collection
				pellets.remove(i);
				pellets.trimToSize();

				//Score for player#
				if( showDebug ) System.out.println(s.name + " got a pellet!!");
				s.score(p.reward);
			}
		}			
		

	}
	
	public boolean Collision_SnakeWall(Snake s)
	{
		boolean collision = false;
		int grd = CurrentLevel.grid;
		Map.tile tmpTile = CurrentLevel.getTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)));
		if( tmpTile.name()=="wall" )
		{
			collision=true;
		}
		return collision;
	}

	public boolean Collision_SnakeSnake(Snake s)
	{
		boolean collision = false;
		int grd = CurrentLevel.grid;
		Map.tile tmpTile = CurrentLevel.getTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)));
		if( (tmpTile.name()=="snake1" && s.name!=s1.name) ||
			(tmpTile.name()=="snake2" && s.name!=s2.name) )
		{
			collision=true;
		}
		//TODO: bug fix: snake1 registers crash in head-on, but not snake2, due to snake2 more recently setting the tile name.
		// make it so both snakes register the crash... somehow.

		return collision;
	}

	
	public boolean Collision_SnakePellet(Snake s)
	{
		boolean collision = false;
		int grd = CurrentLevel.grid;
		Map.tile tmpTile = CurrentLevel.getTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)));
		if( tmpTile==Map.tile.pellet )
		{
			 collision=true;
		}
		return collision;
	}

	
	public void MarkSnakeTiles(Snake s)
	{
		int grd = CurrentLevel.grid;
		Map.tile t = CurrentLevel.getTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)));
		
		//define tileAt(s.head) as a Tile.Snake#
		if( s.name==s1.name )
			if( t.name()=="pellet" || t.name()=="blank" )
				//if snake1
				CurrentLevel.setTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)), Map.tile.snake1);
			else
			{
				//error, or duplicate call per snake-on-tile
			}

		//define tileAt(s.head) as a Tile.Snake#
		if( s.name==s2.name )
			if( t.name()=="pellet" || t.name()=="blank" )
				//if snake2
				CurrentLevel.setTileAt(new Coord(Math.round(s.head.x/grd), Math.round(s.head.y/grd)), Map.tile.snake2);
			else
			{
				//error, or duplicate call per snake-on-tile
			}

		//mark tileAt(s.tail_prev) as Tile.Blank
		if( s.tail_prev!=null )
			CurrentLevel.setTileAt(new Coord(Math.round(s.tail_prev.x/grd), Math.round(s.tail_prev.y/grd)), Map.tile.blank);
	
		//update tail_prev before moving snake
		s.tail_prev = s.tail;
	}
	
	
	public void processSnakeMovement(Snake s)
	{		
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
		//draw the current map
		drawMap(g);

		//draw game objects
		drawPellets(g);
		drawSnake(g, s1);
		if( numPlayers==2 ) drawSnake(g, s2);
		
		//draw Debug Information
		if( showDebug ) drawDebug(g);
	
		//draw menu(s)
		if( gamePaused ) drawPauseMenu(g);
		if( s1.score+s2.score>=POINTSTOADVANCE ) drawWinningMenu(g);

	}

	
	private void drawSnake(Graphics g, Snake s)
	{
				
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

		//draw Snake head
		if( !s.isColliding ) 
			g.setColor(s.color);
		else
			g.setColor(s.color_inv);
		//int buffer = (int)(.1*grid);
		//g.fillRoundRect(s.head.x+buffer, s.head.y+buffer, grid-(2*buffer), grid-(2*buffer), 5*grid/4, 5*grid/4);	//slightly fatter than snake
		g.fillRoundRect(s.head.x, s.head.y, grid, grid, grid/2, grid/2);											//full grid width
		
		
	}
	
	
	private void drawSegment(Graphics g, Coord c1, Coord c2, int grd)
	{

		int buffer = (int) Math.round(0.2*grd);
		int width = Math.abs(c1.getHorizontalDistanceTo(c2));
		int height = Math.abs(c1.getVerticalDistanceTo(c2));
		//TODO:  figure out the anomaly
		// the +1's and -1's below offset the snake by 1 pixel. this aligns it to the grid and makes the head appear symmetrical
		// however, it also causes a couple pixels to extend outward from top and left corners.
		if( c1.isDirectlyAbove(c2) )
			g.fillRoundRect(c1.x+buffer+1, c1.y+buffer, grd-(2*buffer)-1, height+(grd-(2*buffer)), grd/2, grd/2);
		if( c1.isDirectlyBelow(c2) )
			g.fillRoundRect(c1.x+buffer+1, c2.y+buffer, grd-(2*buffer)-1, height+(grd-(2*buffer)), grd/2, grd/2);
		if( c1.isDirectlyLeftOf(c2) )
			g.fillRoundRect(c1.x+buffer, c1.y+buffer+1, width+(grd-(2*buffer)), grd-(2*buffer)-1, grd/2, grd/2);
		if( c1.isDirectlyRightOf(c2) )
			g.fillRoundRect(c2.x+buffer, c2.y+buffer+1, width+(grd-(2*buffer)), grd-(2*buffer)-1, grd/2, grd/2);
	}


	private void drawPauseMenu(Graphics g)
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

	
	private void drawPellets(Graphics g)
	{
		int grid = CurrentLevel.grid;
		for( int i=0; i<pellets.size(); i++ )
		{
			Coord c = pellets.get(i).location;
			g.setColor(pelletColor);
			g.fillRect(c.x*grid, c.y*grid, grid, grid);
		}
	}
	
	
	private void drawMap(Graphics g)
	{
		g.setColor(wallColor);
		int grid = CurrentLevel.grid;
		int w = CurrentLevel.width;
		int h = CurrentLevel.height;
		Map.tile t = Map.tile.blank;
		for( int x=0; x<w; x++)
		{
			for( int y=0; y<h; y++)
			{
				t = CurrentLevel.getTileAt(new Coord(x, y));
				if( t==Map.tile.wall )
					g.setColor(wallColor);
				else if( t==Map.tile.snake1 && showDebug )
					g.setColor(new Color(s1.color.getRed(), s1.color.getGreen()+100, s1.color.getBlue(), s1.color.getAlpha()));
				else if( t==Map.tile.snake2 && showDebug )
					g.setColor(new Color(s2.color.getRed(), s2.color.getGreen(), s2.color.getBlue()+100, s2.color.getAlpha()));
				else if( t==Map.tile.pellet && showDebug )
					g.setColor(pelletColor);
				else
				{
					// empty tile
					g.setColor(backgroundColor);
				}
				g.fillRect((x*grid), (y*grid), grid, grid);
			}
		}
	}

	
	private void drawDebug(Graphics g)
	{
		//grid lines
		int grid = CurrentLevel.grid;
		for( int x=0; x<CurrentLevel.width; x++)
		{
			g.setColor(wallColor);
			g.drawLine(0, x*grid, WDTH, x*grid);
			g.drawLine(x*grid, 0, x*grid, HGHT);
		}
		drawDebug_snake(g, s1);
		drawDebug_snake(g, s2);
	}
	
	
	private void drawDebug_snake(Graphics g, Snake s)
	{
		int grdFraction;
		int grid = CurrentLevel.grid;
		
		//draw Snake tail
		grdFraction = 4;
		g.setColor(Color.white);
		//g.fillRoundRect(s.tail.x+(grid-grdFraction/2)-grid/2, s1.tail.y+(grid-grdFraction/2)-grid/2, grid/grdFraction, grid/grdFraction, s.width/2, s.width/2);
		g.fillRect(s.tail.x+(grid-grdFraction/2)-grid/2, s.tail.y+(grid-grdFraction/2)-grid/2, grid/grdFraction, grid/grdFraction);
	
		//draw every corner in Snake
		grdFraction = 4;
		g.setColor(Color.black);
		for(int i = 0; i<s.corners.size(); i++)
		{
			Coord c = s.corners.get(i);
			g.fillRect(c.x+(grid-grdFraction/2)-grid/2, c.y+(grid-grdFraction/2)-grid/2, grid/grdFraction, grid/grdFraction);
		}
	}
	
	
	private void drawWinningMenu(Graphics g)
	{
		{
			s1.isPaused = true;
			s2.isPaused = true;
			s1.pauseTime = s1.maxPause;
			s2.pauseTime = s2.maxPause;
			//TODO: draw end-game menu offer keys to quit and to play again.
		}
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
		CurrentLevel = new Map(maps[level]);
		
		//Clear all non-wall tiles from map
		CurrentLevel.clear();
		
		//reInitialize all snakes. Set each Snake on the board, per numPlayers
		int s = 0;
		if( s1!=null )s=s1.score;
		s1 = new Snake("Snake1", new Color(0,128,0), s, 5, (CurrentLevel.width/2)*(CurrentLevel.width/CurrentLevel.grid), CurrentLevel.grid, CurrentLevel.p1x, CurrentLevel.p1y, Snake.direction.up, 1f, CurrentLevel.grid);			

		
		if( numPlayers==2 )
		{
			if( s2!=null )s=s2.score;
			s2 = new Snake("Snake2", new Color(25,25,112), s, 5,(CurrentLevel.width/2)*(CurrentLevel.width/CurrentLevel.grid), CurrentLevel.grid, CurrentLevel.p2x, CurrentLevel.p2y, Snake.direction.up, 1f, CurrentLevel.grid);
		}
		else
			s2 = new Snake();
		
		//remove all pellets
		pellets = new ArrayList<Pellet>(numPlayers);
		
	}

	
}