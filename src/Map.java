import java.util.Random;


public class Map {

	int width;
	int height;
	int grid;
	tile[][] map;
	int p1x, p1y;
	int p2x, p2y;
	boolean p1StartLocation = false;
	boolean p2StartLocation = false;
	
	enum tile {blank, wall, snake1, snake2, p1Start, p2Start, pellet};
	
	
	public Map()
	{
		//set everything to 1
		width = 1;
		height = 1;
		grid = 1;
		p1x = 1;
		p1y = 1;
		p2x = 1;
		p2y = 1;
		p1StartLocation = true;
		p2StartLocation = false;
		//blank all tiles
		for( int x=0; x<width; x++)
			for( int y=0; y<height; y++)
				map[x][y] = tile.blank;
	}
	
	
	//default empty map with square edges;
	/*    XXXXXXXX
	 *    X      X
	 *    X      X
	 *    X      X
	 *    XXXXXXXX   */
	public Map(int wdth, int hght, int gridSize)
	{
		width = wdth/gridSize;
		height = hght/gridSize;
		grid = gridSize;
		map = new tile[width][height];

		//blank all tiles
		for( int x=0; x<width; x++)
			for( int y=0; y<height; y++)
				map[x][y] = tile.blank;

		//draw horizontal edges
		for(int x=0; x<width; x++)
		{
			map[x][0] = tile.wall;
			map[x][height-1] = tile.wall;
		}

		//draw vertical edges
		for(int x=0; x<height; x++)
		{
			map[0][x] = tile.wall;
			map[width-1][x] = tile.wall;
		}
	}

	
	public Map(Map m)
	{
		//set everything to 1
		width = m.width;
		height = m.height;
		grid = m.grid;
		p1x = m.p1x;
		p1y = m.p1y;
		p2x = m.p2x;
		p2y = m.p2y;
		p1StartLocation = m.p1StartLocation;
		p2StartLocation = m.p2StartLocation;
		map = m.map;
	}

	
	
	
	/*      
	 * 		XXXXX
	 * 		XXXXX
	 * 		XXXXX
	 */
	public void addWall_box(int x1, int y1, int x2, int y2)
	{
		//switch so x is lower.
		if(x1>x2) {	int tmp = x1;	x1 = x2; 	x2 = tmp; }
		if(y1>y2) { int tmp = y1;	y1 = y2; 	y2 = tmp; }

		for( int x=x1; x<=x2; x++)
			for( int y=y1; y<=y2; y++)
				map[x][y] = tile.wall;
	}

	/* 	XXXXXXXXXXXXX     
	 * 	XXXXXXXXXXXXX
	 * 	XX         XX
	 * 	XX         XX
	 * 	XXXXXXXXXXXXX
	 *  XXXXXXXXXXXXX   */
	public void removeWall_box(int x1, int y1, int x2, int y2)
	{
		//switch so x is lower.
		if(x1>x2) {	int tmp = x1;	x1 = x2; 	x2 = tmp; }
		if(y1>y2) { int tmp = y1;	y1 = y2; 	y2 = tmp; }

		for( int x=x1; x<=x2; x++)
			for( int y=y1; y<=y2; y++)
				map[x][y] = tile.blank;
	}

	/*
	 *    XXXXXXXXXXXXXX
	 */
	public void addWall_line(int x, int y, int length, String direction)
	{
		int dist = 0;
		
		//TODO: add check that each coord is within bounds of array
		if( direction=="right" )
			for( int j=x; dist<length; dist++)
				map[j+dist][y] = tile.wall;
		else if( direction=="left" )
			for( int j=x; dist<length; dist++)
				map[j-dist][y] = tile.wall;
		else if( direction=="down" )
			for( int j=y; dist<length; dist++)
				map[x][j+dist] = tile.wall;
		else if( direction=="down" )
			for( int j=y; dist<length; dist++)
				map[x][j-dist] = tile.wall;
	}

	// add a spot to map where Player1 Snake starts
	public void addStart(int x1, int y1)
	{
		if( !p1StartLocation)
			map[x1][y1] = tile.p1Start;
			
	}

	// add a spot to map where Player1 and Player2 Snakes start
	public void addStart(int x1, int y1, int x2, int y2)
	{
		if( x1<0 || x1>width ) x1 = 1;
		if( x2<0 || x2>width ) x2 = 1;
		if( y1<0 || y1>width ) y1 = 2;
		if( y2<0 || y2>width ) y2 = 2;
		
		if( x1!=x2 && y1!=y2 )
		{
			if( !p1StartLocation)
				map[x1][y1] = tile.p1Start;
			if( !p2StartLocation)
				map[x2][y2] = tile.p2Start;
		}
	}
	
	public tile getTileAt(Coord c)
	{
		int x=c.x, y=c.y;
		
		if( x<width-1 && y<height-1 )
			return map[x][y];
		else
			return( tile.wall);
	}
	public void setTileAt(Coord c, tile t)
	{
		map[c.x][c.y]=t;
	}
	
	
	public Coord getRandomEmptyCoord()
	{
		return getRandomEmptyCoord(System.currentTimeMillis());
	}
	public Coord getRandomEmptyCoord(long seed)
	{
		Coord c = new Coord();
		Random rnd = new Random(seed);
		c.x = rnd.nextInt(width-1);
		c.y = rnd.nextInt(height-1);
		while( getTileAt(c)!=tile.blank )
		{
			c.x = rnd.nextInt(width-1);
			c.y = rnd.nextInt(height-1);
		}
		return c;
	}


	//set all non-wall tiles to tile.blank
	public void clear()
	{
		for( int x=0; x<width; x++ )
			for( int y=0; y<height; y++ )
			{
				tile t = map[x][y];
				if( t!=tile.wall && t!=tile.p1Start && t!=tile.p2Start )
					map[x][y]=tile.blank;
			}
	}
}
