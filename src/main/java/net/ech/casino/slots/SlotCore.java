//
// SlotCore.java  
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * The SlotCore interface describes the internal state of the slot machine
 * It may be inspected and manipulated by pay table elements. 
 * The concrete SlotCore is created by the SlotsGame.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public interface SlotCore
{
	/**
	 * Get the machine configuration.
	 */
	public SlotMachine getMachine ();

	/**
	 * Get the current bet amount.
	 */
	public int getBet ();

	/**
	 * Get the value of the "charged" indicator.
	 */
	public boolean isCharged ();

	/**
	 * Add a win.
	 */
	public void addWin (int winLevel, int amountCredits);

	/**
	 * Charge up the game.
	 */
	public void addCharge ();

	/**
	 * Claim the jackpot.
	 */
	public void addJackpot (int winLevel);
}

