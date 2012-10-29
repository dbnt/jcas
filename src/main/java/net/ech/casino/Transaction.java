//
// Transaction.java	 
// 

package net.ech.casino;

/**
 * A Transaction encapsulates the data associated with a single
 * game play, from an accounting standpoint.  This includes 
 * changes to the player's balance as well as changes to the 
 * state of the Game.
 * <br><br>
 * A Transaction may entail one or more jackpot transactions.  Jackpot
 * Transaction are represented by {@link net.ech.casino.JackpotTransaction}.
 * JackpotTransactions are distinguished by their jackpotName property.
 * There may be at most one JackpotTransaction per jackpotName in a single
 * Transaction.
 *
 * @see net.ech.casino.Session
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Transaction implements java.io.Serializable
{
	private String code;
	private Money wagerAmount;
	private Money returnAmount;
	private Money winAmount;
	private JackpotTransaction jackTrans;

	/**
	 * Default constructor.
	 */
	public Transaction ()
	{
	}

	/**
	 * Constructor.
	 * @param code		a game-specific code describing type of transaction
	 */
	public Transaction (String code)
	{
		this.code = code;
	}

	/**
	 * Set the transaction code.
	 * @param code		a game-specific code describing type of transaction
	 */
	public void setCode (String code)
	{
		this.code = code;
	}

	/**
	 * Get the transaction code.
	 * @return a game-specific code describing type of transaction, may be
	 * null
	 */
	public String getCode ()
	{
		return this.code;
	}

	/**
	 * Set the amount wagered in this transaction.
	 * @param wagerAmount		the amount wagered, as a Money value
	 */
	public void setWagerAmount (Money wagerAmount)
	{
		this.wagerAmount = wagerAmount;
	}

	/**
	 * Set the amount wagered in this transaction.
	 * @param wagerAmount		the amount wagered, as a floating point value
	 */
	public void setWagerAmount (double wagerAmount)
	{
		this.wagerAmount = wagerAmount > 0 ? new Money (wagerAmount) : null;
	}

	/**
	 * Get the amount wagered in this transaction.
	 * @return the amount wagered, as a Money value
	 */
	public Money getWagerAmount ()
	{
		return wagerAmount;
	}

	/**
	 * Set the amount returned to the player's balance as a redemption (push).
	 * @param returnAmount		the amount added, as a Money value
	 */
	public void setReturnAmount (Money returnAmount)
	{
		this.returnAmount = returnAmount;
	}

	/**
	 * Set the amount returned to the player's balance as a redemption (push).
	 * @param returnAmount		the amount added, as a floating point value
	 */
	public void setReturnAmount (double returnAmount)
	{
		this.returnAmount = returnAmount > 0 ? new Money (returnAmount) : null;
	}

	/**
	 * Get the amount returned to the player's balance as a redemption (push)
	 * @return the amount returned, as a Money value
	 */
	public Money getReturnAmount ()
	{
		return returnAmount;
	}

	/**
	 * Set the amount added to the player's balance as winnings.
	 * @param winAmount the amount won, as a Money value
	 */
	public void setWinAmount (Money winAmount)
	{
		this.winAmount = winAmount;
	}

	/**
	 * Set the amount added to the player's balance as winnings.
	 * @param winAmount the amount won, as a floating point value
	 */
	public void setWinAmount (double winAmount)
	{
		this.winAmount = winAmount > 0 ? new Money (winAmount) : null;
	}

	/**
	 * Get the amount added to the player's balance as winnings
	 * @return the amount won, as a Money value
	 */
	public Money getWinAmount ()
	{
		return winAmount;
	}

	/**
	 * Add a sub-transaction that updates the value of a shared jackpot
	 * to this Transaction.
	 * @param jackTrans	   the jackpot sub-transaction
	 */
	public void addJackpotTransaction (JackpotTransaction jackTrans)
	{
		// This implementation can handle only one jackpot transaction
		// per transaction, currently.
		//
		if (this.jackTrans != null)
			throw new IllegalArgumentException ("too many jackpot transactions");
		this.jackTrans = jackTrans;
	}

	/**
	 * Get the number of jackpot transactions associated with this 
	 * Transaction.
	 */
	public int getJackpotTransactionCount ()
	{
		return jackTrans == null ? 0 : 1;
	}

	/**
	 * Get the indexed jackpot transaction associated with this
	 * Transaction.	 
	 * @return a jackpot transaction, or null if none or index out of range
	 */
	public JackpotTransaction getJackpotTransaction (int index)
	{
		return index == 0 ? jackTrans : null;
	}

	//=======================================================================
	// Next generation interface
	//=======================================================================

	public void addWager (String betName, String playerId, Bet bet)
	{
		addWager (betName, playerId, bet.getPurse(), bet.getAmount());
	}

	public void addWager (String betName, String playerId, String purseId,
		Money amount)
	{
		wagerAmount = wagerAmount == null ? amount : amount.add (wagerAmount);
	}

	public void addRefund (String betName, Bet bet)
	{
		addRefund (betName, bet.getPurse(), bet.getAmount());
	}

	public void addRefund (String betName, String purseId, Money amount)
	{
		returnAmount = returnAmount == null ? amount : amount.add (returnAmount);
	}

	//
	// FIXME: player id should not be required here.
	// purse id should be sufficient!  what if the player
	// quits mid-hand?
	//
	public void addWin (String betName, String playerId, Bet bet)
	{
		addWin (betName, playerId, bet.getPurse(), bet.getAmount());
	}

	//
	// FIXME: player id should not be required here.
	// purse id should be sufficient!  what if the player
	// quits mid-hand?
	//
	public void addWin (String betName, String playerId, 
		String purseId, Money amount)
	{
		winAmount = winAmount == null ? amount : amount.add (winAmount);
	}
}
