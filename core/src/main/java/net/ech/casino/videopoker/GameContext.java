//
// GameContext.java
//

package net.ech.casino.videopoker;

import net.ech.casino.CasinoException;

/**
 * A source of services and other miscellaneous stuff.
 */
public class GameContext
{
	public <T> T get(Class<T> requestedClass);
}
