public class Coord {

	public int x;
	public int y;
	
	public Coord()
	{
		x=0;
		y=0;
	}
	
	public Coord(int pX, int pY)
	{
		x = pX;
		y = pY;
	};

	/////////////
	//overrides//
	/////////////
	
	public String toString()
	{
		return(x + "," + y);
	}

	
	public boolean equals(Coord c)
	{
		return( x==c.x && y==c.y );
	}
	
	
	//////////////////////////
	// OTHER COOL FUNCTIONS //
	//////////////////////////
	
	public boolean isHigherThan(Coord c)
	{
		return( y<c.y );
	}
	public boolean islowerThan(Coord c)
	{
		return( y>c.y );
	}
	public boolean isLefterThan(Coord c)
	{
		return( x<c.x );
	}
	public boolean isRighterThan(Coord c)
	{
		return( x<c.x );
	}
	
	public boolean isHorizontallyAlignedWith(Coord c)
	{
		return( y==c.y );
	}
	public boolean isVerticallyAlignedWith(Coord c)
	{
		return( x==c.x );
	}

	public boolean isDirectlyAbove(Coord c)
	{
		return( y<c.y && x==c.x );
	}
	public boolean isDirectlyBelow(Coord c)
	{
		return( y>c.y && x==c.x );
	}
	public boolean isDirectlyLeftOf(Coord c)
	{
		return( x<c.x && y==c.y );
	}
	public boolean isDirectlyRightOf(Coord c)
	{
		return( x>c.x && y==c.y );
	}

	
	public int getHorizontalDistanceTo(Coord c)
	{
		return( x-c.x );
	}
	public int getVerticalDistanceTo(Coord c)
	{
		return( y-c.y );
	}
	
}