//
// CasinoException.java	 
// 

package net.ech.casino;

import java.util.*;

/**
 * CasinoException is the base class for all casino-related errors.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class CasinoException
	extends Exception
{
	/**
	 * Constructor.
	 */
	public CasinoException ()
	{
	}

	/**
	 * Constructor.
	 * @param msg		An error message.
	 */
	public CasinoException (String msg)
	{
		super (msg);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public CasinoException (Throwable cause)
	{
		super (cause);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public CasinoException (String msg, Throwable cause)
	{
		super (msg, cause);
	}
}
