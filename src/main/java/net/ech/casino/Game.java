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
public abstract class Game implements java.io.Serializable
{
	private String id;					// optional id
	private String gameLabel;			// optional description.
	private String machineId;			// key to static rules/parameters
	private PlayerList players;			// list of players/sessions
	private transient Casino casino;	// callback
	private transient Machine machine;	// cached

	/** Serialization version is fixed! */
	static final long serialVersionUID = -5375216699014535110L;

	/**
	 * Constructor.
	 * @param casino	The casino environment.
	 * @param machine	Machine description for this game.	May not be null.
	 */
	public Game (Casino casino, Machine machine)
	{
		this.casino = casino;
		this.machineId = machine.getId ();
		this.players = new PlayerList (machine.getNumberOfSeats ());
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
	 * Set a non-unique, descriptive label for this game.
	 */
	public final void setGameLabel( String gameLabel)
	{
	  this.gameLabel = gameLabel;
	}

	/**
	 * Get a non-unique, descriptive label for this game.
	 */
	public final String getGameLabel()
	{
	  return this.gameLabel;
	}

	/**
	 * Get the machine associated with this game.  
	 * @return the machine
	 */
	public Machine getMachine ()
	{
		// Avoid synchronizing on the game every time.
		if (machine == null)
		{
			synchronized (this)
			{
				if (machine == null)
				{
					machine = casino.getMachine (machineId);
				}
			}
		}
		return machine;
	}

	/**
	 * Get the machine id associated with this game.  
	 * @return the machine's unique id string
	 */
	public String getMachineId ()
	{
		return machineId;
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
	 * Get the number of players at this game.
	 */
	public int getPlayerCount ()
	{
		return players.getPlayerCount ();
	}

	/**
	 * Get the array of players at this game.
	 * No array entry is null.
	 * @return an array of Players.
	 */
	public Player[] getPlayers ()
	{
		return players.getPlayers ();
	}

	/**
	 * Get the id of the player in the indexed seat.
	 * @return the player id, or null if the seat is empty.
	 */
	public String getPlayer (int seatIndex)
	{
		return players.playerAt (seatIndex).getAccountId ();
	}

	/**
	 * Get the player in seat #n.
	 * @return the Player, may be null
	 * @exception ArrayBoundsException if n is an invalid index
	 */
	public Player playerAt (int n)
	{
		return players.playerAt (n);
	}

	/**
	 * Set the casino environment.	(For restoring the casino
	 * handle of a deserialized game.)
	 * @param casino	The casino.
	 */
	public void setCasino (Casino casino)
	{
		this.casino = casino;
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
		// Find a seat for this player. 
		// If no seats available, return false.
		//
		int seatIndex = players.findByAccountId (player.getAccountId ());
		if (seatIndex < 0)
		{
			seatIndex = players.addPlayer (player);
			if (seatIndex < 0)
				return false;
		}

		// A seat has been reserved.  If there is no Session for the
		// Player already, ask the Casino to create one.
		//
		Session session = players.sessionAt (seatIndex);
		if (session == null)
		{
			session = casino.createSession (player);
			players.setSessionAt (seatIndex, session);
		}
		session.open ();

		// Success.
		return true;
	}

	/**
	 * Handle a Player's request to sit out of this Game temporarily.
	 * @param player	The Player
	 * @param code		System-specific code giving reason for request
	 * @param force		If true, the Player leaves no matter what;
	 *					if false, the Player leaves only if there
	 *					are no open bets.
	 * @return true if the Player successfully sat out
	 * @exception AccountingException	if an accounting error occurred
	 */
	public synchronized boolean standPlayer (Player player, String code,
											 boolean force)
		throws AccountingException
	{
		int seatIndex = players.findByAccountId (player.getAccountId ());
		if (seatIndex < 0)
			return true;		// Not seated.

		boolean quitLegal = isQuitLegal (player);
		if (!force && !quitLegal)
			return false;

		// Player does not leave, but Session closes.
		// There is no redemption, because stand-up is temporary.
		Session session = players.sessionAt (seatIndex);
		if (session != null)
		{
			session.close (code, null);
			players.setSessionAt (seatIndex, null);
		}

		return true;
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
		int seatIndex = players.findByAccountId (player.getAccountId ());
		if (seatIndex < 0)
			return true;		// Not seated.

		if (!force && !isQuitLegal (player)) 
			return false;

		// Player is leaving.
		Session session = players.sessionAt (seatIndex);
		removePlayerAt (seatIndex);

		// Account for it.
		if (session != null)
		{
			session.close (code, getRedemptionAmount (player));
			players.setSessionAt (seatIndex, null);
		}

		return true;
	}

	/**
	 * Remove a player from the game.  Just do it.	No screwing around.
	 */
	public synchronized void removePlayerAt (int seatIndex)
	{
		players.removePlayerAt (seatIndex);
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
			Session session = getSessionFor (player);

			saveState ();
			try
			{
				computePlay ();
				transact (session);
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
		protected void transact (Session session)
			throws CasinoException {}
		protected void saveState () {}
		protected void restoreState () {}
	}

	/**
	 * Find the session for this Player.
	 */
	protected Session getSessionFor (Player player)
		throws GameException
	{
		String id = player.getAccountId ();
		int seatIndex = players.findByAccountId (player.getAccountId ());
		if (seatIndex < 0)
			throw new GameException ("Player " + id + " is not seated.", this);
		Session session = players.sessionAt (seatIndex);
		if (session == null)
			throw new GameException ("Player " + id + ": session expired.", this);
		return session;
	}

	/**
	 * Apply the latest jackpot amount to this Game's properties.
	 * The Session must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (String jackpotName, Money jackpotAmount)
	{
	}
}
