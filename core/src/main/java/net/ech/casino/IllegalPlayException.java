//
// IllegalPlayException.java  
// 

package net.ech.casino;

/**
 * An IllegalPlayException represents a user play that is invalid
 * for some reason - for example, it is out of sequence, or some 
 * parameter value is malformed.  Usually indicates an error in the
 * client.
 */
public class IllegalPlayException
	extends CasinoException
{
	/**
	 * Constructor.
	 */
	public IllegalPlayException ()
	{
	}

	/**
	 * Constructor.
	 * @param msg		an error message.
	 */
	public IllegalPlayException (String msg)
	{
		super (msg);
	}
}
