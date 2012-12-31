//
// CreditsGame.java
//

package net.ech.casino;

/**
 * Base class for a type of game that runs on credits rather
 * than dollar-value chips.	 Examples: slots, video poker, video
 * keno.  These types of game also can have a progressive 
 * jackpot that builds with every play.	 This class exists for
 * the sake of common properties and shared implementation.
 *
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class CreditsGame extends Game
{
	private double dollarsPerCredit = 1.0;
	private int bet;
	private int win;
	private Money jackpotAmount;

	/**
	 * Constructor.	 
	 */
	public CreditsGame (Casino casino, CreditsMachine machine)
	{
		super (casino, machine);
		this.dollarsPerCredit = machine.getDollarsPerCredit ();
	}

	//====================================================================
	// Properties
	//====================================================================

	/**
	 * Get dollars per credit as set by the CreditsMachine.
	 */
	public double getDollarsPerCredit ()
	{
		return dollarsPerCredit;
	}

	/**
	 * Set the latest bet amount.
	 */
	public void setBet (int bet)
	{
		this.bet = bet;
	}

	/** 
	 * Get the latest bet amount.
	 * @return number of credits bet, or zero if there have been no bets
	 */
	public int getBet ()
	{
		return bet;
	}

	/**
	 * Get the latest bet amount.
	 * @return a dollar amount, non-null.
	 */
	public Money getBetMoney ()
	{
		return creditsToMoney (bet);
	}

	/**
	 * Set the latest win amount.
	 */
	public void setWin (int win)
	{
		this.win = win;
	}

	/** 
	 * Get the latest win amount.
	 * @return number of credits returned
	 */
	public int getWin ()
	{
		return win;
	}

	/**
	 * Get the latest win amount.
	 * @return a dollar amount, non-null.
	 */
	public Money getWinMoney ()
	{
		return creditsToMoney (win);
	}

	/**
	 * Set cached jackpot amount.
	 */
	public void setJackpotAmount (Money jackpotAmount)
	{
		this.jackpotAmount = jackpotAmount;
	}

	/**
	 * Get cached jackpot amount.  Do not go to the source.
	 */
	public Money getJackpotAmount ()
	{
		return jackpotAmount;
	}

	//====================================================================
	// Methods
	//====================================================================

	/**
	 * Convert credits to currency.
	 */
	public Money creditsToMoney (double credits)
	{
		return new Money (credits * dollarsPerCredit);
	}

	/**
	 * Convert currency to credits.
	 */
	public int moneyToCredits (Money money)
	{
		return (int) (money.doubleValue () / dollarsPerCredit);
	}

	//====================================================================
	// Protected methods
	//====================================================================

	/**
	 * Extend Game.GamePlay to implement save/restore state methods for
	 * the convenience of subclasses.
	 * A bug in the Java 1.1 runtime prevents this class from being
	 * declared with protected access.
	 */
	public class CreditsGamePlay extends GamePlay
	{
		private int oldBet;
		private int oldWin;

		protected void saveState ()
		{
			oldBet = bet;
			oldWin = win;
		}

		protected void restoreState ()
		{
			bet = oldBet;
			win = oldWin;
		}
	}
}
