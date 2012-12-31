//
// GameException.java  
// 

package net.ech.casino;

import java.util.*;

/**
 * A GameException represents a violation of game rules.  Such an error might
 * be caused by bad client input, incorrect game configuration, or other
 * problems.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class GameException extends CasinoException
{
	private Game game;

	/**
	 * Constructor.
	 * @param game		the related game
	 */
	public GameException (Game game)
	{
		this.game = game;
	}

	/**
	 * Constructor.
	 * @param msg		An error message.
	 * @param game		the related game
	 */
	public GameException (String msg, Game game)
	{
		super (msg);
		this.game = game;
	}

	/**
	 * Get the Game related to this error.
	 */
	public Game getGame ()
	{
		return game;
	}
}
