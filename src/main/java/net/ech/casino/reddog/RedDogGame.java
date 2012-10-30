//
// RedDogGame.java
//

package net.ech.casino.reddog;

import java.util.*;
import net.ech.casino.*;

/**
 * A RedDogGame handles a game of Red Dog.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class RedDogGame extends TableGame implements Constants
{
	// Current state of the game.
	//
	private RedDogModel table;
	
	/**
	 * Constructor.
	 */
	public RedDogGame (Casino casino, RedDogMachine machine)
	{
		super (casino, machine);
		table = new RedDogModel (machine);
	}

	//=======================================================================
	// Properties	 
	//=======================================================================

	/**
	 * Get my machine.
	 */
	public RedDogMachine getRedDogMachine()
	{
		return (RedDogMachine) getMachine();
	}

	/**
	 * Set the shoe (for testing).
	 */
	public void setShoe (Shoe shoe)
	{
		table.setShoe (shoe);
	}

	/**
	 * Get a read-only reference to the current table model.
	 */
	public RedDogTable getTable ()
	{
		return table;
	}

	/**
	 * Get a working copy of the current table model.
	 */
	public RedDogModel copyModel ()
	{
		return table.copy();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isQuitLegal (Player player) 
	{
		return table.getState() != TABLE_WORKING;
	}

	//=======================================================================
	// Play controllers	   
	//=======================================================================

	/**
	 * Get an ante controller for the player currently seated in the
	 * indexed seat.
	 */
	public AntePlay getAntePlay (int seatIndex)
		throws GameException
	{
		return new AntePlay (this, checkPlayer (seatIndex), seatIndex);
	}

	/**
	 * Get a surrender controller for the player currently seated in the
	 * indexed seat.
	 */
	public ContinuePlay getContinuePlay (int seatIndex)
		throws GameException
	{
		return new ContinuePlay (this, checkPlayer (seatIndex), seatIndex);
	}

	/**
	 * Get a quit controller for the player currently seated in the indexed
	 * seat.
	 */
	public QuitPlay getQuitPlay (int seatIndex)
		throws GameException
	{
		return new QuitPlay (this, checkPlayer (seatIndex), seatIndex);
	}

	/**
	 * Return id of player at indexed seat, or throw exception if null.
	 */
	private String checkPlayer (int seatIndex)
		throws GameException
	{
		Player player = getPlayer (seatIndex);
		if (player == null)
		{
			throw new GameException ("No player in seat " + seatIndex, this);
		}
		return player.getAccountId();
	}

	//=======================================================================
	// Bet validation.
	//=======================================================================

	/**
	 * Validate an ante bet.
	 */
	public void validateAnteBet (Bet anteBet)
		throws CasinoException
	{
		if (anteBet == null)
		{
			throw new GameException ("null ante?", this);
		}

		double anteValue = anteBet.getAmount().doubleValue();
		if (anteValue < getMinimumBet() || anteValue > getMaximumBet())
		{
			throw new GameException ("ante outside table limits: " + anteBet,
									 this);
		}
	}

	/**
	 * Validate a raise.
	 */
	public void validateRaise (int seatIndex, Money raise)
		throws CasinoException
	{
		if (raise == null || raise.signum() == 0)
			return;			// always acceptable.

		if (raise.signum() < 0)
		{
			throw new GameException ("negative raise?", this);
		}

		// May raise only when there is a spread.
		if (table.getSpread() <= 0)
		{
			throw new GameException ("raise illegal if no spread", this);
		}

		// Get the ante.
		RedDogSeat seat = table.getSeat (seatIndex);
		Bet anteBet = seat.getAnteBet ();
		if (anteBet == null)
		{
			throw new GameException ("no ante?", this);
		}

		// Raise may not exceed ante. 
		if (raise.compareTo (anteBet.getAmount()) > 0)
		{
			throw new GameException ("raise may not exceed ante", this);
		}
	}

	//=======================================================================
	// House logic.
	//=======================================================================

	/**
	 * Execute the dealer's new move on the given table.  Update the
	 * transaction to include any winnings/refunds distributed to players.
	 */
	void runDealer (RedDogModel model, Transaction trans)
	{
		if (allIn (model))
		{
			if (model.getState() != TABLE_WORKING)
			{
				// Have all players anted?
				model.setState (TABLE_WORKING);
				model.dealInitial (getRandomizer());
				resolveInitialDeal (model, trans);
			}
			else
			{
				// Have all players hit continue?
				model.dealThirdCard ();
				if (model.isCatch())
				{
					// Caught.	Pay out based on spread.
					payWithSpread (model, trans);
				}

				endHand (model);
			}
		}
	}

	/**
	 * Return true if there is at least one player and all players have
	 * responded.
	 */
	private boolean allIn (RedDogModel model)
	{
		int blockedCount = 0;

		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			switch (model.getSeat(i).getState())
			{
			case SEAT_READY:
				return false;
			case SEAT_BLOCKED:
				blockedCount += 1;
			}
		}

		return blockedCount > 0;
	}

	/**
	 * Respond to initial deal.
	 */
	private void resolveInitialDeal (RedDogModel model, Transaction trans)
	{
		switch (model.getSpread())
		{
		case -1:
			// In case of a two-way tie, deal a third card immediately.
			model.dealThirdCard ();

			if (model.is3WayTie())
			{
				// Pay everyone.
				payForTie (model, trans);
			}
			else
			{
				push (model, trans);
			}
			endHand (model);
			break;
		case 0:
			push (model, trans);
			endHand (model);
			break;
		default:
			setupToContinue (model);
			break;
		}
	}

	/**
	 * Push all antes.
	 */
	private void push (RedDogModel model, Transaction trans)
	{
		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			Bet ante = model.getSeat(i).getAnteBet();
			if (!Bet.isEmpty (ante))
			{
				trans.addRefund ("ante", ante);
				model.showTake (i, ante.getAmount());
			}
		}
	}

	/**
	 * Pay everyone for a triple tie.
	 */
	private void payForTie (RedDogModel model, Transaction trans)
	{
		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			Bet ante = model.getSeat(i).getAnteBet();
			if (!Bet.isEmpty (ante))
			{
				trans.addRefund ("ante", ante);

				// FIXME: player id should not be required here.
				// purse id should be sufficient!  what if the player
				// quits mid-hand?

				trans.addWin ("ante", getPlayer(i).getAccountId(), ante.getPurse(),
							  ante.getAmount().multiply (PAY_FOR_TIE));

				model.showTake (i, trans.getReturnAmount().add (
										trans.getWinAmount()));
			}
		}
	}

	/**
	 * Pay everyone based on total bet.
	 */
	private void payWithSpread (RedDogModel model, Transaction trans)
	{
		int payMultiple = 
			getRedDogMachine().getPayMultiple (model.getSpread());

		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			Bet ante = model.getSeat(i).getAnteBet();
			if (!Bet.isEmpty (ante))
			{
				// Return their ante.
				trans.addRefund ("ante", ante);

				// Pay the ante either 1:1 or the multiple, depending on
				// machine setting.
				//
				trans.addWin ("ante", getPlayer(i).getAccountId(), ante.getPurse(),
					getRedDogMachine().isMultipleAppliedToAnte()
						? ante.getAmount().multiply (payMultiple)
						: ante.getAmount());

				Bet raise = model.getSeat(i).getRaiseBet();
				if (!Bet.isEmpty (raise))
				{
					// Return their raise, and pay the multiple on it.
					trans.addRefund ("raise", raise);
					trans.addWin ("raise", getPlayer(i).getAccountId(), raise.getPurse(),
								  raise.getAmount().multiply (payMultiple));
				}

				model.showTake (i, trans.getReturnAmount().add (
										trans.getWinAmount()));
			}
		}
	}

	/**
	 * Get ready to continue to secondary deal.
	 */
	private void setupToContinue (RedDogModel model)
	{
		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			if (model.getSeat(i).getState() == SEAT_BLOCKED)
			{
				model.resetSeat (i, true);
			}
		}
	}

	/**
	 * Get ready for next hand.
	 */
	private void endHand (RedDogModel model)
	{
		model.setState (TABLE_END_OF_HAND);

		for (int i = 0; i < model.getNumberOfSeats(); ++i)
		{
			model.resetSeat (i, getPlayer(i) != null);
		}
	}

	/**
	 * Commit a game transaction to the back end.
	 */
	void executeTransaction (RedDogModel model, Transaction trans)
		throws CasinoException
	{
		RedDogModel oldModel = this.table;

		try
		{
			// Pending model becomes the real model, temporarily.
			this.table = model;

			// Take it to the accounting system.
			getCasino().executeTransaction (trans);
		}
		catch (Exception e)
		{
			// Restore old model.  Nothing ever happened.
			this.table = oldModel;

			throw (e instanceof CasinoException ? ((CasinoException) e) : new CasinoException (e));
		}
	}

	//=======================================================================
	// Quit logic.	Needs work.
	//=======================================================================

	/**
	 * Remove a player from the game.
	 */
	public synchronized void removePlayerAt (int seatIndex)
	{
		super.removePlayerAt (seatIndex);
	}
}
