//
// JackpotException.java  
// 

package net.ech.casino;

import java.util.*;

/**
 * A JackpotException represents an error in accessing the jackpots
 * database.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class JackpotException extends CasinoException
{
	/**
	 * Constructor.
	 */
	public JackpotException ()
	{
	}

	/**
	 * Constructor.
	 * @param msg		An error message.
	 */
	public JackpotException (String msg)
	{
		super (msg);
	}

	/**
	 * Constructor.
	 * @param sub		Base exception.
	 */
	public JackpotException (Exception sub)
	{
		super (sub);
	}

	/**
	 * Constructor.
	 * @param msg		An error message.
	 * @param sub		Base exception.
	 */
	public JackpotException (String msg, Exception sub)
	{
		super (msg, sub);
	}
}
