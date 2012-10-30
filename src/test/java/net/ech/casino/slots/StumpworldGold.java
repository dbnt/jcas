//
// StumpworldGold.java  
// 

package net.ech.casino.slots;

import net.ech.casino.*;

/**
 * A simple demo slot machine.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class StumpworldGold extends SlotMachine
{
    public final static double CONTRIB = 0.025;

    private static final int ReelCount = 3;

    // Must match the order of appearance of symbols in the graphic...
    private final static int Scott = 0;
    private final static int Zeus = 1;
    private final static int Mask1 = 2;
    private final static int Mask2 = 3;
    private final static int Spiral = 4;

    private static final int[] ReelPattern =
    {
        0, 1, 2, 0, 3, 4
    };

    /**
     * Constructor.
     */
    public StumpworldGold ()
    {
        setMaximumBet (3);

        PayTable payTable = new PayTable ();
        payTable.add ("3 spirals", 
                      Pattern.create (16), 
                      new ComboReward (new MultWin (1, 100),
                                       new MaxBetBonus (new JackpotReward (5))));
        payTable.add ("3 mask2s",  
                      Pattern.create (8),
                      new MultWin (2, 50));
        payTable.add ("3 mask1s",  
                      Pattern.create (4),
                      new MultWin (3, 10));
        payTable.add ("zeus,scott,scott",
                      Pattern.create (new int[] { 2, 1, 1}),
                      new MultWin (4, 2));
        payTable.add ("scott,scott,mask",
                      Pattern.create (new int[] { 1, 1, 4}),
                      new MultWin (5, 2));
        payTable.add ("any 2 zeus",
                      Pattern.create (2, 2),
                      new MultWin (6, 2));
        setPayTable (payTable);

        try
        {
            setJackpotParameters ("stumpworld", CONTRIB, CONTRIB, Money.ZERO);
        }
        catch (MachineException e)
        {
        }
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
