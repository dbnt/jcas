//
// CrapsException.java
//

package net.ech.casino.craps;

import net.ech.casino.GameException;

/**
 * A craps-related exception.  Simply a subclass of GameException
 */
public class CrapsException extends GameException
{
	/**
	 * Constructor.
	 */
	public CrapsException (CrapsGame game)
	{
		super (game);
	}

	/**
	 * Constructor.
	 * @param msg an error message
	 */
	public CrapsException (String msg, CrapsGame game)
	{
		super (msg, game);
	}
}
