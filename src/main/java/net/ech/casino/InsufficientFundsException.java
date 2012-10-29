//
// InsufficientFundsException.java	
// 

package net.ech.casino;

/**
 * A InsufficientFundsException may be thrown by an Accounting method
 * that debits a user account.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class InsufficientFundsException extends AccountingException
{
	/**
	 * Constructor.
	 */
	public InsufficientFundsException ()
	{
		super ();
	}

	/**
	 * Constructor.
	 * @param msg		An error message string.
	 */
	public InsufficientFundsException (String msg)
	{
		super (msg);
	}
}
