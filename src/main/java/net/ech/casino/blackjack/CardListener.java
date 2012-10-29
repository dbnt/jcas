//
// CardListener.java  
// 

package net.ech.casino.blackjack;

/**
 * A CardListener listens to the visible cards at a blackjack
 * table.  For implementation of card counting strategies.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface CardListener extends java.util.EventListener
{
	public void shuffle (CardEvent e);
	public void cardShown (CardEvent e);
}
