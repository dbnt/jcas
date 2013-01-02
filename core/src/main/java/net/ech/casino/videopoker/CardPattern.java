//
// CardPattern.java	
// 

package net.ech.casino.videopoker;

/**
 * A card pattern matcher.
 */
public interface CardPattern
{
	/**
	 * Return true if the given hand matches this pattern.
	 */
	public boolean matches (char rank, char suit);
}
