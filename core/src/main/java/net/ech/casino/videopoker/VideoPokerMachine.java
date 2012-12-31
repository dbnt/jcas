//
// VideoPokerMachine.java
//

package net.ech.casino.videopoker;

import java.util.*;
import net.ech.casino.*;

/**
 * Description of a video poker machine.
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.1
 */
public class VideoPokerMachine extends CreditsMachine
	implements Constants
{
	// Machine parameters:
	private int cardsInDeck = CardsInStandardDeck;
	private boolean deucesWild = false;
	private int doubleUpLimit = 0;
	private int chargeablePayLine = NoGrade;		 // Payline that , when hit, will charge the machine
	private int chargedPayLine = NoGrade;			 // Payline that , when hit, pays more because we are charged
	private int maxCharge=0;
	private Payout[] payouts;
	private Winner[] winners;
	
	/**
	 * Constructor.
	 */
	public VideoPokerMachine (Payout[] payouts)
		throws MachineException
	{
		setMaximumBet (5);

		Winner chargeableWinner = null;

		this.payouts = (Payout[]) payouts.clone ();
		this.winners = new Winner [payouts.length];

		for (int i = 0; i < payouts.length; ++i)
		{
			String label = payouts[i].getLabel ();
			Winner winner = Winner.lookup (label);
			if (winner == null)
				throw new MachineException (label);
			if (winner == chargeableWinner)
				chargeablePayLine = i;
			winners[i] = winner;
			
			// Look for a charged payout and remember which payout is chargeable
			label.toUpperCase();
			String chargeString = getChargeString(label);
			if (chargeString != null)
			{
				String chargeable = label.substring(chargeString.length()+1);
				chargeable.trim();
				chargeableWinner = Winner.lookup(chargeable);
				chargedPayLine = i;
			}	 
		}
	}
	
	/**
	 * Constructor.
	 */
	public VideoPokerMachine (String id, Payout[] payouts)
		throws MachineException
	{
		this (payouts);
		setId (id);
	}
	
	/**
	 * Set the number of cards in a deck.
	 */
	public void setCardsInDeck (int cardsInDeck)
	{
		this.cardsInDeck = cardsInDeck;
	}

	/**
	 * Get the number of cards in a deck.
	 */
	public int getCardsInDeck ()
	{
		return cardsInDeck;
	}

	/**
	 * @return the indexed row of the payout table.
	 */
	public Payout getPayout (int grade)
	{
		try
		{
			return payouts[grade];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	/**
	 * @return the number of rows in the payout table.
	 */
	public int getPayoutCount ()
	{
		return payouts.length;
	}

	/**
	 * Set deuces wild on/off.
	 */
	public void setDeucesWild (boolean deucesWild)
	{
		this.deucesWild = deucesWild;
	}

	/**
	 * Is this a deuces wild machine?
	 */
	public boolean isDeucesWild ()
	{
		return deucesWild;
	}

	/**
	* See if the given line is the charged payline.
	*/
	public boolean isChargedPayLine (int payline)
	{
		return payline > 0 && payline == chargedPayLine;
	}
 
	/**
	* See if the given line is the chargeable payline.
	*/
	public boolean isChargeablePayLine (int payline)
	{
		return payline > 0 && payline == chargeablePayLine;
	}

	/**
	 * Set the maximum number of hands to be in the charged state,
	 * if this machine has a charged state.
	 */
	public void setMaxCharge (int maxCharge)
	{
		this.maxCharge = maxCharge;
	}

	/**
	 * Get the maximum number of hands to be in the charged state.
	 * @return the number of hands, or zero if this machine cannot be charged.
	 */
	public int getMaxCharge ()
	{
		return this.maxCharge;
	}
	
	private static String [] chargedStrings = { "CHARGED", "SHOCKWAVE" };
	
	/**
	 * @param See if string is charged string
	 * @return key word that made the string a charged string, null if not found
	 */	   
	public static String getChargeString (String str)
	{
		for (int i=0; i<chargedStrings.length;i++)
		{	 
			if (str.startsWith(chargedStrings[i]))
				return new String (chargedStrings[i]);
		}
		return null;
	}	 
	
	/**
	 * @return payline that pays more when hit in charged state
	 */
	public int getChargedPayLine ()
	{
		return chargedPayLine;
	}	 
	
	/**
	 * Set the double-up limit.	 Default is zero.
	 */
	public void setDoubleUpLimit (int doubleUpLimit)
	{
		this.doubleUpLimit = doubleUpLimit;
	}

	/**
	 * Get the double-up limit.
	 * @return the number of times per hand the player may double-up.
	 */
	public int getDoubleUpLimit ()
	{
		return doubleUpLimit;
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public VideoPokerGame createVideoPokerGame (Casino casino)
	{
		return new VideoPokerGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createVideoPokerGame (casino);
	}

	//
	// Game callbacks...
	//

	/**
	 * Fabricate a hand of the desired grade.
	 */
	public void fabricate (byte[] cards, int grade)
	{
		String label = payouts[grade].getLabel ();
		Winner winner = Winner.lookup (label);
		byte[] rep = winner.getRepresentativeHand ();
		System.arraycopy (rep, 0, cards, 0, rep.length);
	}

	/**
	 * Grade the current hand.
	 * @return the index of the corresponding payout row, or -1 if no win.
	 */
	public int grade (byte[] hand, VideoPokerGame game)
	{
		HandInfo handInfo = new HandInfo (hand, deucesWild);

		for (int i = 0; i < winners.length; ++i)
		{
			if (winners[i].matches (handInfo))
			{
				// Handle Shockwave / Flush Attack...
				if (i == chargeablePayLine && game.isCharged ())
					return chargedPayLine;
				return i;
			}
		}

		return NoGrade;
	}
}
