//
// Player.java	
// 

package net.ech.casino;

import java.util.Locale;

/**
 * A Player represents a game participant.	It is similar to a Session
 * but differs in that:
 * <ul>
 * <li>the Player is responsible for initiating game play, while the 
 *	   Session only monitors the results of game play;</li>
 * <li>the Player class may be specialized for a type of Game, encapsulating
 *	   the logic of playing that game, while the Session must be 
 *	   game-independent and specialized for a Casino implementation.</li>
 * </ul>
 *
 * A Player has the following properties:
 * <ol>
 * <li>An account id.  (read-only)</li>
 * <li>An address string that identifies the location of the Player.  May
 *	   be an internet address, or a nickname.</li>
 * <li>An accounting code, through which the Casino may store additional
 *	   information about this player (such as preferred player, plays for
 *	   funny money, ...)</li>
 * <li>A Game that the Player intends to play.	Note: the Player is not
 *	   seated at the Game until there is a call to Player.sitDown (or
 *	   Game.seatPlayer).  This property is read-only, so a new Player
 *	   object must be created for every new Game the actual player
 *	   wishes to play.</li>
 * <li>A session.</li>
 * </ol>
 *
 * @see net.ech.casino.Session
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Player implements java.io.Serializable
{
	private String accountId;			// key to user info
	private String clientAddress;		// player's location
	private String accountingCode;		// information for accounting system
	private Game game;					// game player is playing

	/**
	 * Constructor.
	 * @param accountId	  Identifies the player's account
	 * @param game		The game that the player intends to play
	 */
	public Player (String accountId, Game game)
	{
		if (game == null)
			throw new IllegalArgumentException ("null game");
		this.accountId = accountId;
		this.game = game;
	}

	//=======================================================================
	// PROPERTIES
	//=======================================================================

	/**
	 * Return the player's account id.
	 * @return the account id
	 */
	public String getAccountId ()
	{
		return accountId;
	}

	/**
	 * Set extra information for the accounting system.
	 */
	public void setAccountingCode (String accountingCode)
	{
		this.accountingCode = accountingCode;
	}

	/**
	 * Get extra information for the accounting system.
	 */
	public String getAccountingCode ()
	{
		return accountingCode;
	}

	/**
	 * Set the player's address string.
	 */
	public void setClientAddress (String clientAddress)
	{
		this.clientAddress = clientAddress;
	}

	/**
	 * Get the player's address string.
	 */
	public String getClientAddress ()
	{
		return clientAddress;
	}

	/**
	 * Get the game this Player is playing.
	 * @return the game, or null if the player is not currently playing.
	 */
	public Game getGame ()
	{
		return game;
	}

	/**
	 * Get this Player's current Session.
	 * @return the Session
	 * @exception GameException if there is no current Session.
	 */
	public Session getSession ()
		throws GameException
	{
		return game.getSessionFor (this);
	}

	//=======================================================================
	// METHODS
	//=======================================================================

	/**
	 * Get seated.
	 * @return true if this Player was seated; false if the Game was full.
	 * @exception AccountingException	if an accounting error prevented
	 *									the seating
	 */
	public boolean sitDown ()
		throws AccountingException
	{
		return game.seatPlayer (this);
	}

	/**
	 * Leave the game temporarily.
	 * @param code		System-specific code giving reason 
	 * @param force		If true, the Player leaves the game no matter what;
	 *					if false, the Player leaves the game only if there
	 *					are no open bets.
	 * @return true if the Player successfully left the game
	 * @exception AccountingException	if an accounting error occurred
	 */
	public boolean standUp (String code, boolean force)
		throws AccountingException
	{
		return game.standPlayer (this, code, force);
	}

	/**
	 * Quit the game.
	 * @param code		System-specific code giving reason for quit
	 * @param force		If true, the Player leaves the game no matter what;
	 *					if false, the Player leaves the game only if there
	 *					are no open bets.
	 * @return true if the Player successfully left the game
	 * @exception AccountingException	if an accounting error occurred
	 */
	public boolean quit (String code, boolean force)
		throws AccountingException
	{
		return game.quitPlayer (this, code, force);
	}

	/**
	 * Create a String representation of this Player.
	 */
	public String toString ()
	{
		return "Player[" + getAccountId () + "]";
	}
}
