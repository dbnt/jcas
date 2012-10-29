//
// Machine.java	 
// 

package net.ech.casino;

/**
 * A Machine represents the rules and parameters related to a specific
 * type of game, for example, 25-cent Jacks or Better.	 It describes the 
 * static machine, as opposed to the dynamic state of a game in progress.
 * One of its functions is to instantiate a Game, which represents
 * a game in progress.
 *
 * While many of a Machine's properties are read/write, a Machine is
 * immutable by design.	 That is, once a Machine has been used to create
 * a Game, there must be no more changes to the Machine's properties.
 * Any such changes will cause unpredictable results in the existing
 * Games.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Machine
	implements java.io.Serializable, java.lang.Cloneable
{
	private String id = "";
	private int maxBet = 1;			 // maximum bet is usually variable.
	private boolean testMode;
	private String accountingCode;

	/**
	 * Constructor.
	 */
	public Machine ()
	{
	}

	/**
	 * Constructor.
	 * @param id		an id string for this machine
	 */
	public Machine (String id)
	{
	}

	//====================================================================
	// Properties.
	//====================================================================

	/**
	 * Set the id of this machine.	The id is used by the client to uniquely
	 * identify a machine, including its type and all its parameters.
	 * @param id this machine's unique id.
	 */
	public void setId (String id)
	{
		this.id = id;
	}

	/**
	 * Get the id of this machine.	The id is used by the client to uniquely
	 * identify a machine, including its type and all its parameters.
	 * @return this machine's unique id.
	 */
	public String getId ()
	{
		return this.id;
	}

	/**
	 * Get the number of seats at this type of game.
	 * @return the number of seats.
	 */
	public int getNumberOfSeats ()
	{
		return 1;
	}

	/**
	 * Get the "table minimum" of a game of this type.	Exact interpretation
	 * of this value is game-specific.	Often, it applies only to the 
	 * initial bet.
	 *
	 * Default implementation returns one.	Variable minimum may be 
	 * implemented in subclasses.
	 *
	 * @return the minimum bet in credits.
	 */
	public int getMinimumBet ()
	{
		return 1;
	}

	/**
	 * Set the "table maximum" of a game of this type.	Exact interpretation
	 * of this value is game-specific.	Often, it applies only to the
	 * initial bet.
	 *
	 * @param maxBet	the maximum bet in credits.
	 */
	public void setMaximumBet (int maxBet)
	{
		this.maxBet = maxBet;
	}

	/**
	 * Get the "table maximum" of a game of this type.
	 * @return the maximum bet in credits.
	 */
	public int getMaximumBet ()
	{
		return this.maxBet;
	}

	/**
	 * Set the test mode flag.
	 */
	public void setTestMode (boolean testMode)
	{
		this.testMode = testMode;
	}

	/**
	 * Return the test mode flag.
	 */
	public boolean isTestMode ()
	{
		return testMode;
	}

	/**
	 * Set the accounting code, which may be used to store system-specific
	 * information for the Session.
	 */
	public void setAccountingCode (String code)
	{
		this.accountingCode = code;
	}

	/**
	 * Get the accounting code, which may be used to store system-specific
	 * information for the Session.
	 * @return a String, may be null
	 */
	public String getAccountingCode ()
	{
		return accountingCode;
	}

	//====================================================================
	// Methods.
	//====================================================================

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public abstract Game createGame (Casino casino)
		throws MachineException;

	/**
	 * All Machines are cloneable.
	 */
	public Object copy ()
	{
		try 
		{
			return (Machine) super.clone ();
		}
		catch (CloneNotSupportedException e)
		{ 
			// Should not happen, since this class is Cloneable
			throw new InternalError ();
		}
	}

	/**
	 * If the "machine" property of a game is written as a String,
	 * the machine id is output.
	 */
	public String toString ()
	{
		return id;
	}

	/**
	 * Print this machine for tracing/debugging.
	 */
	public void print (java.io.PrintWriter out)
	{
		if (id != null)
		{
			out.print (id);
			out.print ('(');
		}
		out.print (getClass ().getName ());
		if (id != null)
		{
			out.print (')');
		}
		if (testMode)
		{
			out.print (" (test)");
		}
		if (accountingCode != null)
		{
			out.print (" (");
			out.print (accountingCode);
			out.print (')');
		}
		out.println ();
		out.print ("\tmaxBet=");
		out.println (maxBet);
	}
}
