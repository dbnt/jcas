//
// NullCardPattern.java	
// 

package net.ech.casino.videopoker;

/**
 * Null Object pattern for CardPattern interface.
 */
public class NullCardPattern implements CardPattern
{
	@Override
	public boolean matches (char rank, char suit)
	{
		return false;
	}
}
