//
// VideoPokerMachine.java
//

package net.ech.casino.videopoker;

/**
 * Description of a video poker machine.  A game has one of these at a time,
 * and it may not change while a hand is unresolved.
 */
public class VideoPokerMachine
	implements Constants
{
	private PayTableEntry[] payTable;
	private int numberOfJokers;
	private CardPattern wildCardPattern = new NullCardPattern();
	//private GameFactory bonusGameFactory;   // for double up

	/**
	 * Constructor.
	 */
	public VideoPokerMachine (PayTableEntry[] payTable)
	{
		this.payTable = (PayTableEntry[]) payTable.clone ();
	}

	/**
	 * Constructor.
	 */
	public VideoPokerMachine (PayTableEntry[] payTable, int numberOfJokers)
	{
		this(payTable);
		this.numberOfJokers = numberOfJokers;
	}

	/**
	 * Constructor.
	 */
	public VideoPokerMachine (PayTableEntry[] payTable, int numberOfJokers, CardPattern wildCardPattern)
	{
		this(payTable, numberOfJokers);
		this.wildCardPattern = wildCardPattern;
	}

	/**
	 * Get the size of the pay table.
	 */
	public PayTableEntry[] getPayTable()
	{
		return (PayTableEntry[]) payTable.clone ();
	}

	/**
	 * Get number of jokers in the deck.
	 */
	public int getNumberOfJokers()
	{
		return numberOfJokers;
	}

	/**
	 * Get the wild card pattern, never null.
	 */
	public CardPattern getWildCardPattern()
	{
		return wildCardPattern;
	}
}
