import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
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
	int			MapGridSize = 40;
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
		m.p1x = WDTH-(2*MapGridSize);
		m.p1y = HGHT-(2*MapGridSize);
		maps[0] = m;
		//
		// add more maps
		//
		
		//put Snake(s) on map
		s1 = new Snake("Snake1", Color.red, 5, (maps[0].width/2)*(maps[0].width/maps[0].grid), maps[0].grid, maps[0].p1x, maps[0].p1y, Snake.direction.up, 1f, maps[0].grid);
		s2 = new Snake("Snake2", Color.blue, 5,(maps[0].width/2)*(maps[0].width/maps[0].grid), maps[0].grid, maps[0].p2x, maps[0].p2y, Snake.direction.up, 1f, maps[0].grid);
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
					s1.moveSnake();
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
					if (s1.turning!=null)System.out.println(s1.name + s1.head.toString() + " dir:"+s1.facing.toString() + " turning:"+s1.turning.toString() + " in " + s1.distUntilTurn);
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
		
		//draw Snake1 head
		g.setColor(s1.color);
		g.fillRoundRect(s1.head.x-(s1.width/2), s1.head.y-(s1.width/2), s1.width, s1.width, s1.width/2, s1.width/2);

		//draw Snake1 tail
		g.setColor(Color.white);
		g.fillRoundRect(s1.tail.x-(s1.width/4), s1.tail.y-(s1.width/4), s1.width/2, s1.width/2, s1.width/2, s1.width/2);

		//draw every corner in Snake1
		g.setColor(Color.black);
		for(int x = 0; x<s1.corners.size(); x++)
		{
			Coord c = s1.corners.get(x);
			g.fillRect(c.x-(s1.width/4), c.y-(s1.width/4), s1.width/2, s1.width/2);
		}
		
		//draw temporary grid, showing where snake can turn.
		g.setColor(wallColor);
		
		//DRAW PAUSE MENU
		if( gamePaused )
		{
			int widthCenter = WDTH/2;
			int widthThird = WDTH/3;
			int heightThird = HGHT/3;
			
			g.setColor(Color.darkGray);
			g.fillRoundRect(widthThird, heightThird, widthThird, heightThird, WDTH/20, HGHT/20);
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


}