//
// AccountingException.java	 
// 

package net.ech.casino;

/**
 * A AccountingException may be thrown by any Accounting method.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class AccountingException extends CasinoException
{
	/**
	 * Constructor.
	 */
	public AccountingException ()
	{
		super ();
	}

	/**
	 * Constructor.
	 * @param msg		An error message string.
	 */
	public AccountingException (String msg)
	{
		super (msg);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public AccountingException (Exception ex)
	{
		super (ex);
	}

	/**
	 * Constructor.
	 * @param ex		The original exception wrapped by this one.
	 */
	public AccountingException (String msg, Exception ex)
	{
		super (msg, ex);
	}
}
