//
// Game.java  
// 

package net.ech.casino;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Base class for all casino games.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Game 
{
	private String id;					// optional id
	private Casino casino;
	private Machine machine;	
	private Player[] players;

	/**
	 * Constructor.
	 * @param casino	The casino environment.
	 * @param machine	Machine description for this game.	May not be null.
	 */
	public Game (Casino casino, Machine machine)
	{
		this.casino = casino;
		this.machine = machine;
		this.players = new Player [machine.getNumberOfSeats ()];
	}

	//=======================================================================
	// PROPERTIES
	//=======================================================================

	/**
	 * Set this game's unique id
	 * @param id		an id string.
	 */
	public final void setId (String id)
	{
		this.id = id;
	}

	/**
	 * Get this game's unique id.
	 * @return the id.
	 */
	public final String getId ()
	{
		return id;
	}

	/**
	 * Get the machine associated with this game.  
	 * @return the machine
	 */
	public Machine getMachine ()
	{
		return machine;
	}

	/**
	 * Get the maximum initial bet at this game in terms of credits.
	 * (Shorthand for getMachine().getMaximumBet().)
	 * @return the maximum initial bet, in credits.
	 */
	public int getMaximumBet ()
	{
		return getMachine().getMaximumBet();
	}

	/**
	 * Get the maximum number of players at this game.
	 */
	public int getMaxPlayers ()
	{
		return players.length;
	}

	/**
	 * Get the player in seat #n.
	 * @return the Player, may be null
	 * @exception ArrayBoundsException if n is an invalid index
	 */
	public Player getPlayer (int n)
	{
		return players[n];
	}

	/**
	 * Get the casino environment.
	 * @return a Casino object, never null
	 */
	public Casino getCasino ()
	{
		return casino;
	}

	/**
	 * Get the randomizer to use for this Game.
	 */
	protected Randomizer getRandomizer ()
	{
		return casino.getRandomizer (this);
	}

	//=======================================================================
	// METHODS
	//=======================================================================

	/**
	 * Try to seat a Player at this Game.  
	 * @param playerId	A player id.
	 * @return true if the Player was seated, false if this Game is full.
	 * @exception CasinoException		if an accounting error prevented
	 *									the seating
	 */
	public synchronized boolean seatPlayer (String playerId)
		throws CasinoException
	{
		return seatPlayer (new Player (playerId, this));
	}

	/**
	 * Try to seat a Player at this Game.  
	 * @param player	A Player
	 * @return true if the Player was seated, false if this Game is full.
	 * @exception AccountingException	if an accounting error prevented
	 *									the seating
	 */
	public synchronized boolean seatPlayer (Player player)
		throws AccountingException
	{
		// If the player is already seated, ignore.
		for (int i = 0; i < players.length; ++i) {
			if (players[i] != null && players[i].getAccountId().equals(player.getAccountId())) {
				return true;
			}
		}

		// Find a seat for this player. 
		for (int i = 0; i < players.length; ++i) {
			if (players[i] == null) {
				players[i] = player;
				return true;
			}
		}

		// If no seats available, return false.
		return false;
	}

	/**
	 * Handle a Player's request to leave this Game entirely.
	 * @param player	The Player
	 * @param code		System-specific code giving reason for close
	 * @param force		If true, the Player leaves no matter what;
	 *					if false, the Player leaves only if there
	 *					are no open bets.
	 * @return true if the Player successfully left the game
	 * @exception AccountingException	if an accounting error occurred
	 */
	public synchronized boolean quitPlayer (Player player, String code,
											boolean force)
		throws AccountingException
	{
		for (int i = 0; i < players.length; ++i) {
			if (players[i] != null && players[i].getAccountId().equals(player.getAccountId())) {
				if (!force && !isQuitLegal (players[i])) 
					return false;
				// Player is leaving.
				removePlayerAt (i);
				break;
			}
		}

		return true;
	}

	/**
	 * Remove a player from the game.  Just do it.	No screwing around.
	 */
	public synchronized void removePlayerAt (int seatIndex)
	{
		players[seatIndex] = null;
	}

	/**
	 * Return true if the indicated Player can legally quit the game.
	 * It is assumed that whenever isQuitLegal returns true, 
	 * getRedemptionAmount returns zero (or null).
	 */
	public boolean isQuitLegal (Player player) 
	{
		return true;
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 * It is assumed that whenever isQuitLegal returns true, 
	 * getRedemptionAmount returns zero (or null).
	 */
	public Money getRedemptionAmount (Player player)
	{
		return Money.ZERO;
	}

	/**
	 * Format this Game as a String. 
	 */
	public String toString ()
	{
		return getClass().getName() + "(" + id + ")";
	}

	//=======================================================================
	// Services for game play implementations.
	//=======================================================================

	/**
	 * Base class, or at least a source code template, for game play
	 * methods.	 All game play methods must synchronize on the Game.
	 * A bug in the Java 1.1 runtime prevents this class from being
	 * declared with protected access.
	 */
	public class GamePlay
	{
		public final void play (Player player)
			throws CasinoException
		{
			validate ();

			saveState ();
			try
			{
				computePlay ();
				transact ();
			}
			catch (Exception e)
			{
				// Roll back.  This play never happened.
				restoreState ();

				throw (e instanceof CasinoException) ? (CasinoException) e
													 : new CasinoException (e);
			}
		}

		//
		// Fill in the blanks:
		//
		protected void validate ()
			throws GameException {}
		protected void computePlay () {}
		protected void transact ()
			throws CasinoException {}
		protected void saveState () {}
		protected void restoreState () {}
	}

	/**
	 * Apply the latest jackpot amount to this Game's properties.
	 * The Casino must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (String jackpotName, Money jackpotAmount)
	{
	}
}
