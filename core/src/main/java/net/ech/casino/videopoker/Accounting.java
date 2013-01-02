package net.ech.casino.videopoker;

import net.ech.casino.CasinoException;
import net.ech.casino.Transaction;

public interface Accounting 
{
	/**
	 * A game transaction has occurred in the Java casino.	Execute the
	 * transaction in the accounting back end.	
	 * @see net.ech.casino.Transaction
	 * @see net.ech.casino.JackpotTransaction
	 * @exception InsufficientFundsException if the player's balance does not
	 *									cover the bet amount
	 * @exception AccountingException	there was an error accessing the
	 *									accounting back end
	 */
	public void executeTransaction (Transaction trans)
		throws CasinoException;
}
