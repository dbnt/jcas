//
// PaiGowMachine.java
//

package net.ech.casino.paigow;

import net.ech.casino.*;

/**
 * A PaiGowMachine specifies pai gow poker table parameters.
 *
 * @version 1.1
 * @author I. Mannino imannino@acm.org
 */
public class PaiGowMachine extends TableMachine implements Constants
{
	// House rake:
	private float commissionRate = 0.05f;

	/**
	 * Constructor.
	 */
	public PaiGowMachine()
	{
	}

	/**
	 * Set the house commission rate.
	 */
	public void setCommissionRate (float commissionRate)
	{
		this.commissionRate = commissionRate;
	}

	/**
	 * Get the house commission rate.
	 */
	public float getCommissionRate ()
	{
		return commissionRate;
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public PaiGowGame createPaiGowGame(Casino casino)
	{
		return new PaiGowGame(casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame(Casino casino)
	{
		return createPaiGowGame(casino);
	}
}
