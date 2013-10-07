import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;


public class Game extends Applet implements Runnable, KeyListener
{

	private static final long serialVersionUID = 1L;  //dunno what this is. the KeyListener made me do it.

	//double buffering to avoid flickering images
	Image 		dbImage;
	Graphics 	dbGraphics;
	Color		backgroundColor = Color.green;
	Color		wallColor = Color.darkGray;

	int	WDTH	= 800;
	int	HGHT	= 800;

	Thread 		th = new Thread(this);
	boolean 	gameRunning = false;
	boolean		gamePaused;
	boolean		showDebug;
	int			numPlayers;

	Map[]		maps;
	int			MapGridSize = 10;
	Snake		s1;
	Snake		s2;
	
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
 
		//define ALL MAPS
		int numMaps = 1;
		maps = new Map[numMaps];
		Map m = new Map(WDTH, HGHT, MapGridSize);
		//m.addWall_box(m.width/4, m.height/4, (m.width/4)*3, (m.height/4)*3);
		m.p1x = WDTH/8;
		m.p1y = HGHT/2;
		maps[0] = m;
		// TODO: more maps.
		
		//put Snake(s) on map
		s1 = new Snake("Snake1", Color.red, 5, maps[0].grid, maps[0].p1x, maps[0].p1y, Snake.direction.up, 1f, maps[0].grid);
		s2 = new Snake("Snake2", Color.blue, 5, maps[0].grid, maps[0].p2x, maps[0].p2y, Snake.direction.up, 1f, maps[0].grid);
		if( numPlayers==2 && maps[0].p2StartLocation ) 
			s2 = new Snake("Snake1", Color.blue, 5, 10, maps[0].p2x, maps[0].p2y, Snake.direction.left, 1f, maps[0].grid);
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
				if( !s1.isPaused )
				{
					//check if turn, then move forward.
					checkSnakeTurning(s1);
					moveForward(s1);
				}
				else
				{
					if( s1.pauseTime--<=0 )
					{
						s1.pauseTime = s1.maxPause;
						s1.isPaused = false;
					}
				}
				
				//============================
				//PRINT DEBUG INFO
				//============================
				if(showDebug)
				{
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
			}
			catch(InterruptedException ie)
			{
			
			}
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
		int grid = maps[0].grid;
		int w = maps[0].width;
		int h = maps[0].height;
		for( int x=0; x<w; x++)
		{
			g.drawLine(0, x*grid, WDTH, x*grid);
			g.drawLine(x*grid, 0, x*grid, HGHT);
			for( int y=0; y<h; y++)
				if( maps[0].map[x][y]==Map.tile.wall )
				{
					g.fillRect((x*grid)-(grid/2), (y*grid)-(grid/2), grid, grid);
				}	
		}
		
		//draw Snake1
		g.setColor(s1.color);
		g.fillRect(s1.headX-(s1.width/2), s1.headY-(s1.width/2), s1.width, s1.width);
		
		//draw temporary dot to show the actual SINGLE POINT of paddles
		g.setColor(Color.white);  
		g.fillOval(s1.headX,  s1.headY, 3, 3);
		
		//draw temporary grid, showing where snake can turn.
		g.setColor(wallColor);
		
		//DRAW PAUSE MENU
		if( gamePaused )
		{
			int widthCenter = WDTH/2;
			int widthThird = WDTH/3;
			int heightThird = HGHT/3;
			
			g.setColor(Color.darkGray);
			g.fillRoundRect(widthThird, heightThird, widthThird, heightThird, 10, 10);
			g.setColor(Color.cyan);
			g.setFont(new Font("monospaced", Font.BOLD, 22));
			g.drawString("P A U S E D", widthCenter - 72, heightThird + 30);
			g.setFont(new Font("monospaced", Font.BOLD, 18));
			g.drawString("Play Again - [P] key", widthCenter - 132, heightThird + 100);
			g.drawString("Show Debug - [F3] key", widthCenter - 132, heightThird + 140);
			g.drawString("Unpause    - [SPACE BAR]", widthCenter - 132, heightThird + 180);
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

		//TOGGLE DEBUG INFO
		if (ke.getKeyCode() == DEBUG)
			showDebug = !showDebug;

		//RESET THE GAME
		if (gamePaused && ke.getKeyCode() == RESET)
		{
			//winGame(new Paddle());
		}
	}
	
	
	public void keyReleased(KeyEvent ke) { }

	
	public void checkSnakeTurning(Snake s)
	{
		int grid= maps[0].grid;
		//determine if snake is about to reach a grid intersection to turn.
		// then calculate distance until snake reaches allowable turn
		if( s.turning==Snake.direction.up || s.turning==Snake.direction.down )
		{
			if( s.facing==Snake.direction.right && s.distUntilTurn<s.speed )
				s.distUntilTurn = (int)(s.speed - s.distUntilTurn);
			else if( s.facing==Snake.direction.left && s.distUntilTurn<-s.speed )
				s.distUntilTurn = (int)(-s.speed- s.distUntilTurn);  //TODO:  does this work ?
		}
		if( s.turning==Snake.direction.left || s.turning==Snake.direction.right )
		{
			if( s.facing==Snake.direction.down && s.distUntilTurn<s.speed )
				s.distUntilTurn = (int)(s.speed - s.distUntilTurn);
			else if( s.facing==Snake.direction.up && s.distUntilTurn<-s.speed )
				s.distUntilTurn = (int)(-s.speed- s.distUntilTurn);  //TODO:  does this work ?
		}
	}
	
	public void moveForward(Snake s)
	{
		//if speed<distUntilTurn.. simply move snake per speed and update distUntilTurn.
		//otherwise, (speed>distUntilTurn), need to handle the turn first:
		// move as far as distUntilTurn, turn the snake, move the remaining speed-distUntilTurn.  remember to update distUntilTurn
		int x = s.headX;
		int y = s.headY;
		int d = s.distUntilTurn;
		int grid = maps[0].grid;
		float spd = s.speed;
		
		if( spd<=d )
		{
			forward(s, s.speed); 
		}
		else
		{
			//move distUntilTurn
			forward(s, d);
			
			//turn
			if( s.turning!=null && s.turning!=s.facing )
			{
				s.facing = s.turning;
				s.turning = null;
			}

			//move remaining dist of speed
			forward(s, s.speed-d);
		}
		
		
		//recalc distUntilTurn
		
		
	}
	
	
	public void forward(Snake s, float dist)
	{
		int x = s.headX;
		int y = s.headY;
		if( s.facing==Snake.direction.right )
			s.headX = (int)(x + s.speed);
		else if( s.facing==Snake.direction.left )
			s.headX = (int)(x - s.speed);
		else if( s.facing==Snake.direction.down )
			s.headY = (int)(y + s.speed);
		else if( s.facing==Snake.direction.up )
			s.headY = (int)(y - s.speed);
	}
	
	
}