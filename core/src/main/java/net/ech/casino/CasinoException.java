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
public class CasinoException extends Exception
{
	private Exception sub;

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
	public CasinoException (Exception ex)
	{
		super (ex.getMessage ());
		this.sub = ex;
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public CasinoException (String msg, Exception ex)
	{
		super (msg + ": " + ex.getMessage ());
		this.sub = ex;
	}

	/**
	 * Print this exception and its stack trace to the 
	 * standard error stream. 
	 */
	public void printStackTrace ()
	{ 
		printStackTrace (System.err);
	}

	/**
	 * Print this exception and its stack trace to the 
	 * specified print stream. 
	 */
	public void printStackTrace (java.io.PrintStream s)
	{ 
		if (sub != null)
		{
			s.println (this);
			sub.printStackTrace (s);
		}
		else
			super.printStackTrace (s);
	}

	/**
	 * Print this exception and its stack trace to the specified
	 * print writer.
	 */
	public void printStackTrace (java.io.PrintWriter s)
	{ 
		if (sub != null)
		{
			s.println (this);
			sub.printStackTrace (s);
		}
		else
			super.printStackTrace (s);
	}
}
