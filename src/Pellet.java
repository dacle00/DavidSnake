import java.awt.*;

public class Pellet
{

	
	String	name;
	int 	reward;
	int		size;
	Coord	location;
	boolean	visible;
	Color	color;
	Image	img;

	
	public Pellet()
	{
		name = "pellet";
		reward = 1;
		size = 1;
		location = new Coord(0,0);
		visible = false;
		color = Color.white;
		img = null;
	}

	
	public Pellet(String pName, int pReward, int pSize, Coord pLocation, boolean pVisible, Color pColor)
	{
		name = pName;
		reward = pReward;
		size = pSize;
		location = pLocation;
		visible = pVisible;
		color = pColor;
	}

	
	public Pellet(String pName, int pReward, int pSize, Coord pLocation, boolean pVisible, Image pImage)
	{
		name = pName;
		reward = pReward;
		size = pSize;
		location = pLocation;
		visible = pVisible;
		img = pImage;
	}
	
	
	public void growSize(int growValue)
	{
		size +=growValue;
	}
	public void decaySize(int decayValue)
	{
		size -= decayValue;
	}

	public void growReward(int growValue)
	{
		reward +=growValue;
	}
	public void decayReward(int decayValue)
	{
		reward -= decayValue;
	}



	
}
