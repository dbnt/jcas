//
// WarMachine.java	
// 

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * Class WarMachine defines the house options available in the game of
 * Casino War.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class WarMachine extends TableMachine implements Constants
{
	private int numberOfDecks = 6;
	private boolean warTieBonusEnabled = false;

	/**
	 * Construct a default War machine.
	 */
	public WarMachine ()
	{
	}

	//=================================================================
	// Dynamic properties
	//=================================================================

	/**
	 * Set the number of decks in the shoe.
	 */
	public void setNumberOfDecks (int numberOfDecks)
	{
		// Get reasonable.
		if (numberOfDecks < 1 || numberOfDecks > 100)
		{
			throw new IllegalArgumentException (
				"numberOfDecks=" + numberOfDecks);
		}

		this.numberOfDecks = numberOfDecks;
	}

	/**
	 * Enable/disable the bonus war rule.  This rule applies when the
	 * player opts to go to war and ends in a tie with the dealer.	If
	 * this option is disabled, this machine pushes the original bet and
	 * pays even money on the raise, just as for a regular war win.	 If
	 * this option is enabled, this machine pays even money on both bets.
	 * Again, this applies only to the event of a tie after a tie.
	 * The default value is false.
	 * @param warTieBonusEnabled   true to enable the bonus feature
	 */
	public void setWarTieBonusEnabled (boolean warTieBonusEnabled)
	{
		this.warTieBonusEnabled = warTieBonusEnabled;
	}

	//=================================================================
	// Game creation
	//=================================================================

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public WarGame createWarGame (Casino casino)
	{
		return new WarGame (casino, this);
	}

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createWarGame (casino);
	}

	//=================================================================
	// Services for WarGame.
	//=================================================================

	/**
	 * Create a new Shoe appropriate for a game of this type.
	 */
	public Shoe createNewShoe ()
	{
		return new Shoe (numberOfDecks);
	}

	private final static int MAX_CARDS_FOR_DEALER = 5;
	private final static int MAX_CARDS_PER_PLAYER = 2;

	private int maxCardsForTable()
	{
		return MAX_CARDS_FOR_DEALER + getNumberOfSeats() * MAX_CARDS_PER_PLAYER;
	}

	/**
	 * Validate the ante and tie bets.
	 */
	public void validateAnte (Bet ante, Bet tieBet)
		throws CasinoException
	{
		if (ante == null)
		{
			throw new CasinoException ("null ante?");
		}

		double anteValue = ante.getAmount().doubleValue();
		if (anteValue < getMinimumBet() || anteValue > getMaximumBet())
		{
			throw new CasinoException ("invalid ante: " + ante);
		}

		// Tie bet may be null.
		if (tieBet != null)
		{
			// Table minimum does not apply to tie bet.
			double tieValue = tieBet.getAmount().doubleValue();
			if (tieValue < 0 || tieValue > getMaximumBet())
			{
				throw new CasinoException ("invalid tie bet: " + tieBet);
			}
		}
	}

	/**
	 * Calculate the payout multiple in the event of a tie following
	 * a war.  This is a multiple of the ante, not of the total bet.
	 */
	public int getWarTiePayout ()
	{
		return WAR_PAYOUT_MULTIPLE + (warTieBonusEnabled ? 1 : 0);
	}
}
