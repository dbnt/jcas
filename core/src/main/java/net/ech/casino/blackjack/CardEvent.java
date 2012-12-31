//
// CardEvent.java  
// 

package net.ech.casino.blackjack;

/**
 * A CardEvent signals an upturned card at a blackjack
 * table.  For implementation of card counting strategies.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CardEvent extends java.util.EventObject
{
	private byte card;

	/**
	 * Constructor.
	 * @param source	the game
	 */
	public CardEvent (BlackjackGame source)
	{
		super (source);
	}

	/**
	 * Constructor.
	 * @param source	the game
	 * @param card		the denomination of the upturned card
	 */
	public CardEvent (BlackjackGame source, byte card)
	{
		super (source);
		this.card = card;
	}

	/**
	 * @return the denomination of the upturned card
	 */
	public byte getCard ()
	{
		return card;
	}
}
