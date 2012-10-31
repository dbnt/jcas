//
// WarGame.java
//

package net.ech.casino.war;

import java.util.*;
import net.ech.casino.*;

/**
 * A WarGame handles a game of Casino War.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class WarGame extends TableGame implements Constants
{
	// Current state of the game.
	//
	private WarModel table;
	
	/**
	 * Constructor.
	 */
	public WarGame (Casino casino, WarMachine machine)
	{
		super (casino, machine);
		table = new WarModel (machine);
	}

	//=======================================================================
	// Properties	 
	//=======================================================================

	/**
	 * Get my machine.
	 */
	public WarMachine getWarMachine()
	{
		return (WarMachine) getMachine();
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
	public WarTable getTable ()
	{
		return table;
	}

	/**
	 * Get a working copy of the current table model.
	 */
	public WarModel copyModel ()
	{
		return table.copy();
	}

	/**
	 * Framework method, deprecated.
	 */
	public boolean isQuitLegal (Player player) 
	{
		return table.isClear() || table.isEndOfHand();
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
	public SurrenderPlay getSurrenderPlay (int seatIndex)
		throws GameException
	{
		return new SurrenderPlay (this, checkPlayer (seatIndex), seatIndex);
	}

	/**
	 * Get a go-to-war controller for the player currently seated in the
	 * indexed seat.
	 */
	public GoToWarPlay getGoToWarPlay (int seatIndex)
		throws GameException
	{
		return new GoToWarPlay (this, checkPlayer (seatIndex), seatIndex);
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
	// House logic.
	//=======================================================================

	//
	// Execute the dealer's new move on the given table.  Update the
	// transaction to include any winnings/refunds distributed to players.
	//
	void runDealer (WarModel model, Transaction trans)
	{
		if (isReadyToDeal (model))
		{
			model.dealInitial (getRandomizer());
			resolveAntes (model, trans);
			resolveTieBets (model, trans);
		}
		else if (isReadyToWar(model))
		{
			model.dealWar ();
			resolveRaises (model, trans);
		}
	}

	/**
	 * Return true if the table is clear and all ante bets are in.
	 */
	private boolean isReadyToDeal (WarModel model)
	{
		// Table is clear?
		if (!model.isClear())
			return false;

		// Everyone ante'd up?
		for (int i = 0; i < model.getSeatCount(); ++i)
		{
			if (getPlayer(i) != null && model.getSeat(i).getAnte() == null)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Resolve all resolveable ante bets following an initial deal.
	 */
	private void resolveAntes (WarModel model, Transaction trans)
	{
		byte dCard = model.getDealerCard (DEALER_CARD_INITIAL);

		for (int i = 0; i < model.getSeatCount(); ++i)
		{
			WarSeat seat = model.getSeat(i);
			byte pCard = seat.getPlayerCard (PLAYER_CARD_INITIAL);
			if (compareCards (pCard, dCard) > 0)
			{
				// Player wins initial deal.
				model.showTake (i, seat.getAnte().getAmount().multiply (2));
				trans.addRefund ("ante", seat.getAnte());
				trans.addWin ("ante", getPlayer(i).getAccountId(), seat.getAnte());
			}
		}
	}

	/**
	 * Resolve all tie bets following an initial deal.
	 */
	private void resolveTieBets (WarModel model, Transaction trans)
	{
		byte dCard = model.getDealerCard (DEALER_CARD_INITIAL);

		for (int i = 0; i < model.getSeatCount(); ++i)
		{
			if (model.isTieAt(i))
			{
				Bet tieBet = model.getSeat(i).getTieBet();
				if (tieBet != null && tieBet.getAmount().signum() > 0)
				{
					model.showTake (i, 
						tieBet.getAmount().multiply (TIE_PAYOUT_MULTIPLE + 1));
					trans.addRefund ("tie", tieBet);
					trans.addWin ("tie", getPlayer(i).getAccountId(),
						tieBet.getPurse(),
						tieBet.getAmount().multiply (TIE_PAYOUT_MULTIPLE));
				}
			}
		}
	}

	/**
	 * Return true if a war is necessary to finish this hand.
	 */
	private boolean isReadyToWar (WarModel model)
	{
		if (model.isClear() || model.isEndOfHand())
			return false;

		int nTies = 0;
		int nRaises = 0;

		for (int i = 0; i < model.getSeatCount(); ++i)
		{
			if (getPlayer (i) != null && model.isTieAt (i))
			{
				nTies += 1;
				if (model.getSeat(i).getRaise() != null)
				{
					nRaises += 1;
				}
			}
		}

		return nTies > 0 && nRaises == nTies;
	}

	/**
	 * Resolve all raise bets following a war.
	 */
	private void resolveRaises (WarModel model, Transaction trans)
	{
		byte dCard = model.getDealerCard (DEALER_CARD_WAR);

		for (int i = 0; i < model.getSeatCount(); ++i)
		{
			if (model.isTieAt (i) && !model.getSeat(i).isSurrendered())
			{
				WarSeat seat = model.getSeat (i);
				byte pCard = seat.getPlayerCard (PLAYER_CARD_WAR);
				int payout = 0;
				switch (compareCards (pCard, dCard))
				{
				case 0:
					payout = getWarMachine().getWarTiePayout();
					break;
				case 1:
					payout = WAR_PAYOUT_MULTIPLE;
					break;
				}

				// Calculate the take - may be null, but must be reset, to
				// clear any value left by the ante play.
				Bet ante = seat.getAnte();
				Money take = null; 
				if (payout > 0)
				{
					take = ante.getAmount().multiply(payout);
				}
				model.showTake (i, take);

				// If player won, account for the winnings.
				if (take != null)
				{
					String playerId = getPlayer (i).getAccountId();
					trans.addRefund ("ante", ante);
					trans.addRefund ("raise", ante);
					trans.addWin ("raise", playerId, seat.getRaise().getPurse(), seat.getRaise().getAmount().multiply (payout - 2));
				}
			}
		}
	}

	/**
	 * Return 1 if the player card beats the dealer card, -1 if the 
	 * dealer card beats the player card, zero if the cards tie.
	 */
	private static int compareCards (byte playerCard, byte dealerCard)
	{
		byte playerRank = Card.rankOf (playerCard);
		byte dealerRank = Card.rankOf (dealerCard);
		if (playerRank == dealerRank)
			return 0;
		return playerRank > dealerRank ? 1 : -1;
	}

	/**
	 * Commit a game transaction to the back end.
	 */
	void executeTransaction (WarModel model, Transaction trans)
		throws CasinoException
	{
		WarModel oldModel = this.table;

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
	public void removePlayerAt (int seatIndex)
	{
		super.removePlayerAt (seatIndex);
		table.clearPlayerBets (seatIndex);
	}
}
