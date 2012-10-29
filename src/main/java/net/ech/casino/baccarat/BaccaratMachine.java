//
// BaccaratMachine.java	 
// 

package net.ech.casino.baccarat;

import java.util.*;
import net.ech.casino.*;

/**
 * A BaccaratMachine specifies baccarat table parameters.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class BaccaratMachine extends TableMachine implements Constants
{
	/**
	 * Table parameters:
	 */
	private int nDecks = 6;
	private float tieOdds = 8;
	private boolean canRequestReshuffle;

	//=================================================================
	// INITIALIZATION
	//=================================================================

	/**
	 * Constructor.
	 */
	public BaccaratMachine ()
	{
	}

	/**
	 * Constructor.
	 */
	public BaccaratMachine (int nDecks, float tieOdds,
							 boolean canRequestReshuffle)
	{
		this.nDecks = nDecks;
		this.tieOdds = tieOdds;
		this.canRequestReshuffle = canRequestReshuffle;
	}

	//=================================================================
	// PROPERTIES
	//=================================================================

	/**
	 * Set the number of decks in a shoe.
	 */
	public void setNumberOfDecks (int nDecks)
	{
		this.nDecks = nDecks;
	}

	/**
	 * Get the number of decks in a shoe.
	 */
	public int getNumberOfDecks ()
	{
		return nDecks;
	}

	/**
	 * Get the number of decks in a shoe.
	 * @deprecated
	 */
	public int getNDecks ()
	{
		return nDecks;
	}

	/**
	 * Set the odds on a tie bet.
	 */
	public void setTieOdds (float tieOdds)
	{
		this.tieOdds = tieOdds;
	}

	/**
	 * Get the odds on a tie bet.
	 */
	public float getTieOdds ()
	{
		return tieOdds;
	}

	/**
	 * Set whether the player can request a reshuffle.
	 */
	public void setCanRequestReshuffle (boolean can)
	{
		canRequestReshuffle = can;
	}

	/**
	 * Tell whether the player can request a reshuffle.
	 */
	public boolean getCanRequestReshuffle ()
	{
		return canRequestReshuffle;
	}

	//=================================================================
	// METHODS
	//=================================================================

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public BaccaratGame createBaccaratGame (Casino casino)
	{
		return new BaccaratGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createBaccaratGame (casino);
	}
}
