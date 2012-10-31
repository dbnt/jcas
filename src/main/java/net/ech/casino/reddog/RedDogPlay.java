//
// RedDogPlay.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * RedDogPlay: base class for RedDog play controllers.	Each subclass of
 * RedDogPlay corresponds to a different player function.
 */
public abstract class RedDogPlay implements Constants
{
	private RedDogGame game;
	private String playerId;   // for consistency checks
	private int seatIndex;

	/**
	 * Constructor.
	 */
	RedDogPlay (RedDogGame game, String playerId, int seatIndex)
	{
		this.game = game;
		this.playerId = playerId;
		this.seatIndex = seatIndex;
	}

	/**
	 * Get the game.
	 */
	public final RedDogGame getGame ()
	{
		return game;
	}

	/**
	 * Get the table.
	 */
	public final RedDogTable getTable ()
	{
		return game.getTable();
	}

	/**
	 * Get my seat.
	 */
	public final RedDogSeat getSeat ()
	{
		return game.getTable().getSeat(seatIndex);
	}

	/**
	 * Get my seat in the working table..
	 */
	public final RedDogSeat getSeat (RedDogModel model)
	{
		return model.getSeat(seatIndex);
	}

	/**
	 * Get the seat index that this play applies to.
	 * This play is invalid if the player identified in the constructor
	 * is no longer seated at this position.
	 */
	public final int getSeatIndex ()
	{
		return seatIndex;
	}

	/**
	 * Get the player id that this play applies to.
	 * This play is invalid if this player is no longer seated at the
	 * position specified in the constructor.
	 */
	public final String getPlayerId ()
	{
		return playerId;
	}

	/**
	 * Return true if this control is currently enabled.
	 */
	public abstract boolean isEnabled ();

	/**
	 * Activate the control.  This is a template method.  Synchronizes
	 * on the game and makes general validation checks before executing
	 * the control.
	 */
	public final void activate ()
		throws CasinoException
	{
		String seatedPlayerId = game.getPlayerId(seatIndex);

		if (!playerId.equals(seatedPlayerId))
		{
			throw new CasinoException ("player id mismatch!");
		}

		if (!isEnabled())
		{
			throw new IllegalPlayException (game);
		}

		// Get a working copy of the table.
		RedDogModel workingModel = game.copyModel();

		// Open a new transaction.
		Transaction workingTrans = new Transaction (game);
		
		// Execute player changes to the model; build up the transaction.
		this.execute (workingModel, workingTrans);

		// Let the house respond.
		game.runDealer (workingModel, workingTrans);
			
		// Record the transaction.
		game.executeTransaction (workingModel, workingTrans);

		// Do any post-transaction cleanup called for.
		cleanup ();
	}

	/**
	 * Subclasses fill in the guts.
	 */
	protected abstract void execute (RedDogModel table, Transaction trans)
		throws CasinoException;

	/**
	 * Subclasses provide name.
	 */
	protected abstract String getName ();

	/**
	 * Subclasses may override.	 Default is to do no cleanup.
	 * There is no opportunity to throw exceptions.	 The transaction
	 * has already taken place.
	 */
	protected void cleanup ()
	{
	}
}
