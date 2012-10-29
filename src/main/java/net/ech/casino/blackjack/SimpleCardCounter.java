//
// SimpleCardCounter.java
//

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * SimpleCardCounter
 * A very simple card counting strategy.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class SimpleCardCounter implements Constants, CardListener
{
	private int count;

	/**
	 * Constructor.
	 */
	public SimpleCardCounter ()
	{
	}
	
	/**
	 * Get the current count.  A negative count indicates an Ace-10-rich
	 * deck.  A positive count indicates the opposite.
	 */
	public int getCount ()
	{
		return count;
	}

	/**
	 * Respond to a shuffle.
	 */
	public void shuffle (CardEvent e)
	{
		count = 0;
	}

	/**
	 * Respond to a shown card.
	 */
	public void cardShown (CardEvent e)
	{
		int rank = Card.rankOf (e.getCard ());
		if (rank >= 2 && rank <= 6)
			++count;
		else if (rank == Ace || rank >= 10)
			--count;
	}
}
