//
// WarPlay.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * WarPlay: base class for Casino War controllers.	A controller controls
 * a game of Casino War as one of its players.	Each subclass of WarPlay
 * corresponds to a different player function.
 */
public abstract class WarPlay implements Constants
{
	private WarGame game;
	private String playerId;   // for consistency checks
	private int seatIndex;

	/**
	 * Constructor.
	 */
	WarPlay (WarGame game, String playerId, int seatIndex)
	{
		this.game = game;
		this.playerId = playerId;
		this.seatIndex = seatIndex;
	}

	/**
	 * Get the game.
	 */
	public final WarGame getGame ()
	{
		return game;
	}

	/**
	 * Get the machine.
	 */
	public final WarMachine getMachine ()
	{
		return game.getWarMachine();
	}

	/**
	 * Get the table.
	 */
	public final WarTable getTable ()
	{
		return game.getTable();
	}

	/**
	 * Get my seat.
	 */
	public final WarSeat getSeat ()
	{
		return game.getTable().getSeat(seatIndex);
	}

	/**
	 * Get my seat in the working table..
	 */
	public final WarSeat getSeat (WarModel model)
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
		// All plays are synchronized!
		synchronized (game)
		{
			String seatedPlayerId = game.getPlayer(seatIndex).getAccountId();

			if (!playerId.equals(seatedPlayerId))
			{
				throw new CasinoException ("player id mismatch!");
			}

			if (!isEnabled())
			{
				throw new IllegalPlayException (game);
			}

			// Get a working copy of the table.
			WarModel workingModel = game.copyModel();

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
	}

	/**
	 * Subclasses fill in the guts.
	 */
	protected abstract void execute (WarModel table, Transaction trans)
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
