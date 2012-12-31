//
// DemoMachine1.java  
// 

package net.ech.casino.slots;

/**
 * A simple demo slot machine.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class DemoMachine1 extends SlotMachine
{
	private static final int ReelCount = 3;

	// Must match the order of appearance of symbols in the graphic...
	private final static int Bar = 0;
	private final static int Bell = 1;
	private final static int Plum = 2;
	private final static int Orange = 3;
	private final static int Cherry = 4;
	private final static int Lemon = 5;

	private static final int[] ReelPattern =
	{
		Bar,Lemon,Cherry,Orange,Bell,Lemon,Plum,Cherry,Lemon,Bar,Orange,
		Plum,Bell,Lemon,Cherry,Orange,Plum,Lemon,Cherry,Bell,Orange,Plum
	};

	/**
	 * Constructor.
	 */
	public DemoMachine1 ()
	{
		PayTable payTable = new PayTable ();
		payTable.add ("3 bars", Pattern.create (1), new MultWin (1, 100));
		payTable.add ("3 bells", Pattern.create (2), new MultWin (2, 40));
		payTable.add ("3 plums", Pattern.create (4), new MultWin (3, 25));
		payTable.add ("3 oranges", Pattern.create (8), new MultWin (4, 20));
		payTable.add ("3 cherries  ", Pattern.create (16), new MultWin (5, 10));
		payTable.add ("bell,*,bell",
					  Pattern.create (new int[] { 2, ~2, 2}),
					  new MultWin (6, 8)); 
		payTable.add ("2 cherries",
					  Pattern.create (16, 2),
					  new MultWin (7, 2));
		payTable.add ("cherry,*,*  ",
					  Pattern.create (new int[] { 16, ~16, ~16 }),
					  new MultWin (8, 1));
		setPayTable (payTable);

		setMaximumBet (3);
	}

	/**
	 * @return the number of reels on this machine.
	 */
	public int getReelCount ()
	{
		return ReelCount;
	}

	/**
	 * @return the pattern for the indexed reel.
	 */
	public int[] getReelPattern (int index)
	{
		// The three reels have identical configuration...
		return ReelPattern;
	}
}
