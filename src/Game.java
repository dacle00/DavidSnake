import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;
import Snake.direction;


public class Game extends Applet implements Runnable, KeyListener
{

	private static final long serialVersionUID = 1L;  //dunno what this is. the KeyListener made me do it.

	//double buffering to avoid flickering images
	Image 		dbImage;
	Graphics 	dbGraphics;

	int	WDTH			= 800;
	int	HGHT			= 800;


	Thread 		th = new Thread(this);
	boolean 	gameRunning = false;
	boolean		gamePaused;
	boolean		showDebug;
	int			numPlayers;

	Map[]		maps;
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

		gameRunning = true;
		gamePaused = true;
		showDebug = false;
		numPlayers = 1;
 
		//define ALL MAPS
		maps = new Map[1];
		maps[0] = new Map();
		maps[0].p1x = WDTH/2;
		maps[0].p1y = HGHT;
		
		
		//put Snake(s) on map
		s1 = new Snake("Snake1", 5, 10, new Dimension(maps[0].p1x, maps[0].p1y), direction.left);
		if( numPlayers==2 && maps[0].p2StartLocation ) 
			s2 = new Snake("Snake1", 5, 10, new Dimension(maps[0].p2x, maps[0].p2y), direction.left);
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
		int x_prev;
		int y_prev;
		int xspeed;
		float yspeed;
		
		while (gameRunning)
		{
			if (!gamePaused)
			{
				//temp ball info
				x_prev = b.getX();
				y_prev = b.getY();
				xspeed = b.getxSpeed();
				yspeed = b.getySpeed();
	
				//============================
				//Move the Ball's Position
				//============================
				if( !ballIsPaused )
				{
					b.setX(x_prev += xspeed);
					b.setY(y_prev += yspeed);
				}
				else
				{
					if( ballPauseCountdown--<=0 )
					{
						ballPauseCountdown = MAXBALLPAUSE;
						ballIsPaused = false;
					}
				}
	
				//============================
				//Move the Players' Positions
				//============================
				p1 = CheckKeyboardInput(p1);
				p2 = CheckKeyboardInput(p2);
				
				//============================
				//check Collisions
				//============================
				CheckBorderCollisions();
				CheckPaddleCollisions(p1);
				CheckPaddleCollisions(p2);
				
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

		dbGraphics.setColor(Color.black);
		dbGraphics.fillRect(0, 0,  WDTH, HGHT);
		dbGraphics.setColor(this.getForeground());
		paint(dbGraphics);

		//finally, draw the items
		g.drawImage(dbImage, 0, 0, this);
	}
	
	//Draw each object to the screen once. The repaint() method will call this method
	public void paint(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0,  WDTH, HGHT);

		//DRAW SCORE
		g.setColor(Color.cyan);
		g.setFont(new Font("monospaced", Font.BOLD, 56));
		g.drawString(Integer.toString(p1.getScore()), (WDTH/2)-120, 46);
		g.drawString(Integer.toString(p2.getScore()), (WDTH/2)+80, 46);		
		
		//draw ball
		g.setColor(b.getColor());
		g.fillOval(b.getX()-b.getRadius(),  b.getY()-b.getRadius(),  b.getRadius()*2,  b.getRadius()*2);
		
		//draw paddle1
		g.setColor(p1.getColor());
		g.fillRect(p1.getX()-(p1.getWidth()/2), p1.getY()-(p1.getHeight()/2), p1.getWidth(), p1.getHeight());
		
		//draw paddle2
		g.setColor(p2.getColor());
		g.fillRect(p2.getX()-(p2.getWidth()/2), p2.getY()-(p2.getHeight()/2), p2.getWidth(), p2.getHeight());
		
		//draw center dashed line
		paint_centerDashedLine(g);
		
		//draw temp dot to show the actual SINGLE POINT of paddles
		g.setColor(Color.white);  
		g.fillOval(p1.getX(),  p1.getY(), 3, 3);
		g.fillOval(p2.getX(),  p2.getY(), 3, 3);
		
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
		if( p1.getScore()>=POINTSTOWIN || p2.getScore()>=POINTSTOWIN )
		{
			ballIsPaused = true;
			ballPauseCountdown = MAXBALLPAUSE;
			// draw end-game menu offer keys to quit and to play again.
		}
		
	}


	public void paint_centerDashedLine(Graphics g)
	{
		g.setColor(b.getColor());
		int winHeight	= HGHT;
		int centerX		= WDTH/2;
		int dashY		= 0; 
		int dashWidth	= p1.getWidth() /2;
		int numDashes	= 10;
		int dashPad		= 20;
		int dashHeight	= (winHeight/numDashes)-dashPad;
		for( int i=0; i<numDashes; i++)
		{   
			dashY		= (i * (winHeight/numDashes)) + dashPad/2;
			dashHeight	= (winHeight/numDashes) - dashPad;
			g.fillRect(centerX-(dashWidth/2), dashY, dashWidth, dashHeight);
		}
	}
	
	
	public void CheckBorderCollisions()
	{
		int x = b.getX();
		int y = b.getY();
		float yspeed = b.getySpeed();
		
		//Ball on Left Edge (P2 scores against P1!)
		if( x < 0 )
		{
			//catch the ball left edge, increment score
			Score("Player2");
			if(showDebug) System.out.println("P2 SCORE!!!!  Ball Hit Left at X:" + x + " + Y:" + y);
		}
		
		//Ball on Right Edge (P1 scores against P2!)
		if( x > getSize().width )
		{
			//catch the ball, right edge.
			Score("Player1");
			if(showDebug) System.out.println("P1 SCORE!!!!  Ball Hit Left at X:" + x + " + Y:" + y);
		}

		//bounce ball off of top edge.
		if( y < 0 + PADDINGTOP )  
		{
			playSound(BallHitEdge);
			b.setySpeed(Math.abs(yspeed));
			if(showDebug) System.out.println("Ball Hit Top at X:" + x + " + Y:" + y);
		}
		
		//bounce ball off of bottom edge.
		if( y > getSize().height - PADDINGBOTTOM ) 
		{
			playSound(BallHitEdge);
			b.setySpeed(-Math.abs(yspeed));
			if(showDebug) System.out.println("Ball Hit Bottom at X:" + x + " + Y:" + y);
		} 
	}
		
	
	public void CheckPaddleCollisions(Paddle p)
	{
		int x = b.getX();
		int y = b.getY();
		int xspeed = b.getxSpeed();
		int yDif = b.getY() - p.getY();
		int modifier = 1;


		//check for Paddle Collisions if ballX value is within range of Paddle1X value
		if( x>(p.getX()-(p.getWidth())) && x<(p.getX()+(p.getWidth())) )
		{
			if(showDebug) System.out.println("Collision Check Left  X:" + x + " + Y:" + y);
			if( CollisionCheck_PaddleX(p) )
			{
				
				playSound(BallHitPaddle);
				
				//modify horizontal speed based on if Paddle was moving AWAY or TOWARDS ball.
				if( p.getX()==p1.getX() )
					switch (p.getXMoving())
					{
						case -1:
							modifier = -1;
							break;
						case 1:
							modifier = 2;
							break;
						case 0:
						default:
							modifier = 1;
							break;
					}
				if( p.getX()==p2.getX() )
					switch (p.getXMoving())
					{
						case -1:
							modifier = 2;
							break;
						case 1:
							modifier = -1;
							break;
						case 0:
						default:
							modifier = 1;
							break;
					}
				
				if(showDebug) System.out.println("BALL COLLIDED WITH PADDLE1 X:" + x + " + Y:" + x);
				//bounce the ball
				if(x<WDTH/2)
					b.setxSpeed(Math.abs(xspeed)+modifier);
				else
					b.setxSpeed(-(Math.abs(xspeed)+modifier));
				
				//set vertical speed, based on where ball collided paddle
				b.setySpeed((float)(.125 * yDif));
			}
		}
	}
	
	
	public boolean CollisionCheck_PaddleX(Paddle p)
	{
		int x = b.getX();
		int y = b.getY();
		int radius = b.getRadius();
		
		//ball is on the left side of screen, at an X-coord that could intersect the P1Paddle
		Rectangle2D.Double Paddle1Bounds = new Rectangle2D.Double(p.getX()-(p.getWidth()/2),p.getY()-(p.getHeight()/2), p.getWidth(),p.getHeight()); //locationXY, sizeXY
		Ellipse2D.Double BallBounds = new Ellipse2D.Double(x,y, radius*2,radius*2);

		//check if Ball is intersecting P1Paddle.
		return(BallBounds.intersects(Paddle1Bounds));
	}
		
	
	public void Score(String player)
	{
		playSound(BallScore);

		ballIsPaused = true;
		
		if( player == "Player1" )
		{
			p1.score();
			if(showDebug) System.out.println("P1 SCORE = "+p1.getScore());
		}	
		if( player == "Player2" )
		{
			p2.score++;
			if(showDebug) System.out.println("P2 SCORE = "+p2.getScore());
		}
		
		if( p1.getScore()<POINTSTOWIN && p2.getScore()<POINTSTOWIN)
			resetAfterScore();
		else
		{
			if( p1.getScore()>=POINTSTOWIN )
				winGame(p1);
			else
				winGame(p2);
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
	
	
	public void resetAfterScore()
	{
		//reset ball position and speed
		b.setX(WDTH/2);
		b.setY(HGHT/2);
		b.setxSpeed(INITIALBALLXSPEED);
		b.setySpeed(INITIALBALLYSPEED);
		int xspeed = b.getxSpeed();
		if(showDebug) System.out.println("Ball X:" + b.getX() + " Y:" + b.getY());
		
		//reset paddle locations?  pros? cons?
		
		//choose ball direction
		int i = new Random().nextInt(99);
		//decide direction based i value
		if (i>49)
			b.setxSpeed(Math.abs(xspeed));
		else
			b.setxSpeed(-Math.abs(xspeed));
	}

	
	public void winGame(Paddle p)
	{
		
		if( p.getX()==p1.getX() )
		{
			//p1 won
			if(showDebug) System.out.println("!!!! P1 WON !!!!");
		}
		else if ( p.getX()==p2.getX() )
		{
			//p2 won
			if(showDebug) System.out.println("!!!! P1 WON !!!!");
		}
		else
		{
			//nobody won. just reset the game
			if(showDebug) System.out.println("Game is resetting.");
		}
		//call init
		init();
		//unpause game.
		gamePaused = false;
		
		//reset some other global variables??
		
		//pause ball for MAXBALLPAUSE
		ballIsPaused = true;
		ballPauseCountdown = MAXBALLPAUSE;
	}
	
	
	public Paddle CheckKeyboardInput(Paddle p)
	{
		//the variables dictating if the paddles are moving up, down, or are still
		//are set in the keyPressed() and keyReleased() methods.
		int pyspeed = p.getYSpeed();
		int pxspeed = p.getXSpeed();
		int py = p.getY();
		int px = p.getX();
		
		//CHECK VERTICAL MOVEMENT
		if( p.getYMoving()>0 && p.getY()>0+p.getHeight()/2 )
		{
			//moving UP
			p.setY( py-pyspeed );
		}
		else if ( p.getYMoving()<0 && p.getY()<HGHT-p.getHeight()/2 )
		{
			//moving DOWN
			p.setY( py+pyspeed );
		}
		else 
		{
			//not y moving, no change
		}
		
		//CHECK HORIZONTAL MOVEMENT
		if( p.getXMoving()<0 && 
			p.getX()>=(p.getInitialX()-(PADDLEALLOWEDHORIZONTALMOVEMENT/2)) )
		{
			//moving LEFT
			p.setX( px-pxspeed );
		}
		else if ( p.getXMoving()>0 && 
				  p.getX()<=(p.getInitialX()+(PADDLEALLOWEDHORIZONTALMOVEMENT/2)) )
		{
			//moving RIGHT
			p.setX( px+pxspeed );
		}
		else 
		{
			//not x moving, no change
		}
		
		return p;
	}


	public void keyTyped(KeyEvent ke) 	{	}


	public void keyPressed(KeyEvent ke)
	{		
		//System.out.println("User Pressed Key: " + KeyEvent.getKeyText(ke.getKeyCode()) + " KeyChar: "+ke.getKeyChar()+" KeyCode: "+ke.getKeyCode());

		//P1 Move UP
		if (ke.getKeyCode() == p1.getUpKey())
			p1.setYMoving(1);

		//P1 Move DOWN
		if (ke.getKeyCode() == p1.getDownKey())
			p1.setYMoving(-1);

		//P1 Move LEFT
		if (ke.getKeyCode() == p1.getLeftKey())
			p1.setXMoving(-1);

		//P1 Move RIGHT
		if (ke.getKeyCode() == p1.getRightKey())
			p1.setXMoving(1);

		//P2 Move UP
		if (ke.getKeyCode() == p2.getUpKey())
			p2.setYMoving(1);

		//P2 Move DOWN
		if (ke.getKeyCode() == p2.getDownKey())
			p2.setYMoving(-1);

		//P2 Move LEFT
		if (ke.getKeyCode() == p2.getLeftKey())
			p2.setXMoving(-1);

		//P2 Move RIGHT
		if (ke.getKeyCode() == p2.getRightKey())
			p2.setXMoving(1);
		
		//PAUSE UNPAUSE THE GAME
		if (ke.getKeyCode() == PAUSE)
			gamePaused = !gamePaused;

		//TOGGLE DEBUG INFO
		if (ke.getKeyCode() == DEBUG)
			showDebug = !showDebug;

		//RESET THE GAME
		if (gamePaused && ke.getKeyCode() == RESET)
			winGame(new Paddle());
	}
	
	
	public void keyReleased(KeyEvent ke)
	{
		//P1 stop moving up if moving up
		if (ke.getKeyCode() == p1.getUpKey() && p1.getYMoving()>0) 
			p1.setYMoving(0);
		//P1 stop moving down if moving down
		if (ke.getKeyCode() == p1.getDownKey() && p1.getYMoving()<0) 
			p1.setYMoving(0);
		//P1 stop moving left if moving left
		if (ke.getKeyCode() == p1.getLeftKey() && p1.getXMoving()<0)
			p1.setXMoving(0);
		//P1 stop moving right if moving right
		if (ke.getKeyCode() == p1.getRightKey() && p1.getXMoving()>0)
			p1.setXMoving(0);

		//P2 stop moving up if moving up 
		if (ke.getKeyCode() == p2.getUpKey() && p2.getYMoving()>0) 
			p2.setYMoving(0);
		//P2 stop moving down if moving down
		if (ke.getKeyCode() == p2.getDownKey() && p2.getYMoving()<0) 
			p2.setYMoving(0);
		//P2 stop moving left if moving left
		if (ke.getKeyCode() == p2.getLeftKey() && p2.getXMoving()<0)
			p2.setXMoving(0);
		//P2 stop moving right if moving right
		if (ke.getKeyCode() == p2.getRightKey() && p2.getXMoving()>0)
			p2.setXMoving(0);
	}
}