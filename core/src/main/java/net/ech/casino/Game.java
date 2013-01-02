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
	private String[] playerIds;

	/**
	 * Constructor.
	 * @param casino	The casino environment.
	 * @param machine	Machine description for this game.	May not be null.
	 */
	public Game (Casino casino, Machine machine)
	{
		this.casino = casino;
		this.machine = machine;
		this.playerIds = new String [machine.getNumberOfSeats ()];
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
		return playerIds.length;
	}

	/**
	 * Get the player in seat #n.
	 * @return the player ID, may be null
	 * @exception ArrayBoundsException if n is an invalid index
	 */
	public String getPlayerId (int n)
	{
		return playerIds[n];
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
	 * Try to seat a player at this Game.  
	 * @param playerId	A player ID
	 * @return true if the player was seated, false if this Game is full.
	 * @exception AccountingException	if an accounting error prevented
	 *									the seating
	 */
	public boolean seatPlayer (String playerId)
		throws AccountingException
	{
		// If the player is already seated, ignore.
		for (int i = 0; i < playerIds.length; ++i) {
			if (playerIds[i] != null && playerIds[i].equals(playerId)) {
				return true;
			}
		}

		// Find a seat for this player. 
		for (int i = 0; i < playerIds.length; ++i) {
			if (playerIds[i] == null) {
				playerIds[i] = playerId;
				return true;
			}
		}

		// If no seats available, return false.
		return false;
	}

	/**
	 * Handle a player's request to leave this Game entirely.
	 * @param playerId	The player ID
	 * @param code		System-specific code giving reason for close
	 * @param force		If true, the player leaves no matter what;
	 *					if false, the player leaves only if there
	 *					are no open bets.
	 * @return true if the Player successfully left the game
	 * @exception AccountingException	if an accounting error occurred
	 */
	public boolean quitPlayer (String playerId, String code, boolean force)
		throws AccountingException
	{
		for (int i = 0; i < playerIds.length; ++i) {
			if (playerIds[i] != null && playerIds[i].equals(playerId)) {
				if (!force && !isQuitLegal (i)) 
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
	public void removePlayerAt (int seatIndex)
	{
		playerIds[seatIndex] = null;
	}

	/**
	 * Return true if the indicated player can legally quit the game.
	 */
	public boolean isQuitLegal (int seatIndex) 
	{
		return true;
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
	 * methods.
	 * A bug in the Java 1.1 runtime prevents this class from being
	 * declared with protected access.
	 */
	public class GamePlay
	{
		public final void play ()
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
			throws CasinoException {}
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
