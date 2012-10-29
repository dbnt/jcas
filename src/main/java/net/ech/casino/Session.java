//
// Session.java	 
// 

package net.ech.casino;

import java.util.*;

/**
 * A Session represents a Player's activity at a Game from
 * an accounting point of view.	 The Session implementation is
 * the bridge between this casino framework and the application
 * code.
 *
 * @see net.ech.casino.Player
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Session implements java.io.Serializable
{
	private String id;
	private Player player;

	/**
	 * Constructor.
	 * @param player	The Player for whom this Session is established.
	 */
	public Session (Player player)
	{
		this.player = player;
	}

	/**
	 * Set the session id.
	 * The meaning of the id is specific to the application.
	 */
	public void setId (String id)
	{
		this.id = id;
	}

	/**
	 * Get the session id.
	 * The meaning of the id is specific to the application.
	 */
	public String getId ()
	{
		return id;
	}

	/**
	 * Get the account id associated with this Session.
	 */
	public final String getAccountId ()
	{
		return player.getAccountId ();
	}

	/**
	 * Get the player associated with this Session.
	 */
	public Player getPlayer ()
	{
		return player;
	}

	/**
	 * Get the Game associated with this Session.
	 */
	public Game getGame ()
	{
		return player.getGame ();
	}

	/**
	 * Format as String.
	 */
	public String toString ()
	{
		return id;
	}

	//=======================================================================
	// Methods
	//=======================================================================

	/**
	 * Open/reopen this session.
	 */
	public void open ()
		throws AccountingException
	{
	}

	/**
	 * Close this session.
	 * @param code				System-specific code giving reason for close
	 * @param redemption		Amount to credit the account, may be null
	 */
	public abstract void close (String code, Money redemption)
		throws AccountingException;

	//=======================================================================
	// Game callbacks.
	//=======================================================================

	/**
	 * A game transaction has occurred in the Java casino.	Execute the
	 * transaction in the accounting back end.	
	 * @see net.ech.casino.Transaction
	 * @see net.ech.casino.JackpotTransaction
	 * @exception InsufficientFundsException if the player's balance does not
	 *									cover the bet amount
	 * @exception AccountingException	there was an error accessing the
	 *									accounting back end
	 */
	public abstract void executeTransaction (Transaction trans)
		throws AccountingException;

	/**
	 * Apply the latest jackpot amount to the Game's properties.
	 * executeTransaction must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (String jackpotName, Money jackpotAmount)
	{
		player.getGame ().applyJackpotAmount (jackpotName, jackpotAmount);
	}
}
