//
// GameContext.java
//

package net.ech.casino.videopoker;

import net.ech.casino.CasinoException;

/**
 * A source of services and other miscellaneous stuff.
 */
public interface GameContext
{
	public <T> T get(Class<T> requestedClass)
		throws CasinoException;
}
