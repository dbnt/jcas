//
// TestCasino.java  
// 

package net.ech.casino;

import java.io.*;

public class TestCasino extends CasinoAdapter
{
    private Machine machine;
    private int reportIncrement = 1000;
    private int nrounds;
    private Money balance = new Money (1000.00);
    private double currentBet;
    private double currentWin;
    private int roundCount;
    private int winCount;
    private int winPushCount;
    private double totalBet;
    private double totalWin;
    private double totalJackpotWin;
    private int jackpotCount;
    private double jackpotAmount;
    private Jackpots jackpots = new TestJackpots ();
    private Transaction lastTransaction;

    private final static double EPSILON = 0.001;

    /**
     * Constructor.
     */
    public TestCasino (Machine machine)
    {
        this.machine = machine;
    }

    /**
     * Reset the accumulated results.
     */
    public void reset ()
    {
        roundCount = 0;
        totalBet = 0;
        totalWin = 0;
        totalJackpotWin = 0;
        winCount = 0;
        winPushCount = 0;
        jackpotCount = 0;
        jackpotAmount = 0;
    }

    /**
     * Get this casino's jackpot manager.
     * @return the jackpot manager, never null.
     */
    public Jackpots getJackpots ()
    {
        return jackpots;
    }

    /**
     * Set the number of trials to run.
     */
    public void setNumberOfRounds (int nrounds)
    {
        this.nrounds = nrounds;
    }

    public int getRoundCount ()
    {
        return roundCount;
    }

    public int getWinCount ()
    {
        return winCount;
    }

    public double getTotalBet ()
    {
        return totalBet;
    }

    public double getTotalWin ()
    {
        return totalWin;
    }

    public double getTotalJackpotWin ()
    {
        return totalJackpotWin;
    }

    public int getJackpotCount ()
    {
        return jackpotCount;
    }

    public Money getJackpotAmount ()
    {
        return new Money (jackpotAmount);
    }

    public Transaction getLastTransaction ()
    {
        return lastTransaction;
    }

    private static String d2 (double n)
    {
        return new Money (n).toString ();
    }

	public void executeTransaction (Transaction trans)
		throws CasinoException
	{
		tallyBet (trans.getWagerAmount ());
		tallyWin (trans.getReturnAmount ());
		tallyWin (trans.getWinAmount ());

		for (int i = 0; i < trans.getJackpotTransactionCount (); ++i)
		{
			JackpotTransaction jtrans = trans.getJackpotTransaction (i);
			String jackpotName = jtrans.getJackpotName ();
			Money jackpotAmount = null;
			if (jtrans.isContribution())
			{
				jackpotAmount = jackpots.addToJackpot (jackpotName, jtrans.getContributionAmount());
			}
			if (jtrans.isClaim ())
			{
				jackpotAmount = jackpots.claimJackpot (jackpotName, jtrans.getClaimFactor());
				tallyWin (jackpotAmount);
			}
			//applyJackpotAmount (trans.getGame(), jackpotName, jackpotAmount);
		}

		TestCasino.this.lastTransaction = trans;

		//if (trans.getGame ().isQuitLegal (0)) {
			tallyRound ();
		//}
	}

    /**
     * Subclass may extend to respond to each new bet.
     */
    protected void tallyBet (Money bet)
    {
        if (bet != null)
        {
            double amount = Math.abs (bet.doubleValue ());
            currentBet += amount;
            totalBet += amount;
        }
    }

    /**
     * Subclass may extend to respond to each win/push.
     */
    protected void tallyWin (Money win)
    {
        if (win != null)
        {
            double amount = Math.abs (win.doubleValue ());
            currentWin += amount;
            totalWin += amount;
        }
    }

    /**
     * Subclass may extend to respond to the end of each round.
     */
    protected void tallyRound ()
    {
        ++roundCount;
        if (currentWin > currentBet + EPSILON)
            ++winCount;
        if (currentWin > currentBet - EPSILON)
            ++winPushCount;
        currentBet = 0;
        currentWin = 0;
    }

    //==================================================================
    //
    // Jackpots.
    //
    //==================================================================

    private class TestJackpots extends JackpotsAdapter
    {
        /**
         * Look up the current amount of the identified jackpot.
         */
        public Money getJackpotAmount (String name)
        {
            return new Money (jackpotAmount);
        }

        /**
         * Increment the identified jackpot.
         */
        public Money addToJackpot (String name, double amount) 
        {
            jackpotAmount += amount;
            return new Money (jackpotAmount);
        }

        /**
         * A player has won the identified jackpot.
         */
        public Money claimJackpot (String name, double amount)
        {
            double won = jackpotAmount * amount;
            jackpotAmount -= won;
            totalJackpotWin += won;
            ++jackpotCount;
            return new Money (won);
        }
    }
}
