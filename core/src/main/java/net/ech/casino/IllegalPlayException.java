//
// IllegalPlayException.java  
// 

package net.ech.casino;

import java.util.*;

/**
 * An IllegalPlayException represents a user command that has come
 * out of sequence.	 Usually indicates error in the client.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class IllegalPlayException extends GameException
{
	/**
	 * Constructor.
	 * @param game			the related game
	 */
	public IllegalPlayException (Game game)
	{
		super (game);
	}

	/**
	 * Constructor.
	 * @param msg		an error message.
	 * @param game		the related game
	 */
	public IllegalPlayException (String msg, Game game)
	{
		super (msg, game);
	}
}
