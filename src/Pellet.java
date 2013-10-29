import java.awt.*;

public class Pellet
{

	
	String	name;
	int 	reward;
	int		radius;
	Coord	location;
	boolean	visible;
	Color	color;
	Image	img;

	
	public Pellet()
	{
		name = "pellet";
		reward = 1;
		radius = 1;
		location = new Coord(0,0);
		visible = false;
		color = Color.white;
		img = null;
	}

	
	public Pellet(String pName, int pReward, int pSize, Coord pLocation, boolean pVisible, Color pColor)
	{
		name = pName;
		reward = pReward;
		radius = pSize;
		location = pLocation;
		visible = pVisible;
		color = pColor;
	}

	
	public Pellet(String pName, int pReward, int pSize, Coord pLocation, boolean pVisible, Image pImage)
	{
		name = pName;
		reward = pReward;
		radius = pSize;
		location = pLocation;
		visible = pVisible;
		img = pImage;
	}
	
	
}
