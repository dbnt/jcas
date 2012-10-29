//
// MachineException.java  
// 

package net.ech.casino;

import java.util.*;

/**
 * A MachineException represents a casino game configuration error.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class MachineException extends CasinoException
{
	/**
	 * Constructor.
	 */
	public MachineException ()
	{
	}

	/**
	 * Constructor.
	 * @param msg		An error message.
	 */
	public MachineException (String msg)
	{
		super (msg);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public MachineException (Exception ex)
	{
		super (ex);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public MachineException (String msg, Exception ex)
	{
		super (msg, ex);
	}
}
