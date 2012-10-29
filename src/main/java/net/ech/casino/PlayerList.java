//
// PlayerList.java	
// 

package net.ech.casino;

/**
 * A PlayerList is a list of Players and associated Sessions.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public final class PlayerList implements java.io.Serializable
{
	private Player[] players;
	private Session[] sessions;

	/**
	 * Constructor.
	 * @param size		the maximum number of players
	 */
	public PlayerList (int size)
	{
		this.players = new Player [size];
	}

	/**
	 * Get the maximum number of players/sessions on this list.
	 */
	public int getSize ()
	{
		return players.length;
	}

	/**
	 * Get the current number of players on this list.
	 */
	public synchronized int getPlayerCount ()
	{
		int count = 0;

		for (int i = 0; i < players.length; ++i)
		{
			if (players[i] != null)
				++count;
		}

		return count;
	}

	/**
	 * Get the array of players at this table.
	 * @return an array of Players.	 No array entry is null.
	 */
	public synchronized Player[] getPlayers ()
	{
		Player[] result = new Player [getPlayerCount ()];

		int count = 0;
		for (int i = 0; i < players.length; ++i)
		{
			if (players[i] != null)
			{
				result[count] = players[i];
				++count;
			}
		}

		return result;
	}

	/**
	 * Get the Player at the indexed seat.
	 * @return a Player, or null if none in the indexed seat.
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public Player playerAt (int index)
	{
		return players[index];
	}

	/**
	 * Add a player to the first available position in the list.
	 * @param player	A player.
	 * @return the player that was added, or null if table is full.
	 */
	public synchronized int addPlayer (Player player)
	{
		for (int i = 0; i < players.length; ++i)
		{
			if (players[i] == null)
			{
				players[i] = player;
				return i;
			}
		}

		return -1;
	}

	/**
	 * Drop a player from this list.
	 * @param index		The index
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public synchronized void removePlayerAt (int index)
	{
		players[index] = null;
		setSessionAt (index, null);
	}

	/**
	 * Find the player at this table with the given account id.
	 * @return the Player, or null if no such player at this table.
	 */
	public synchronized int findByAccountId (String accountId)
	{
		for (int i = 0; i < players.length; ++i)
		{
			Player p = players[i];
			if (p != null && p.getAccountId ().equals (accountId))
				return i;
		}

		return -1;
	}

	/**
	 * Get the Session at the indexed seat.
	 * @return a Session, or null if none in the indexed seat.
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public synchronized Session sessionAt (int index)
	{
		if (sessions == null) return null;
		return sessions[index];
	}

	/**
	 * Set the Session at the indexed seat.
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void setSessionAt (int index, Session session)
	{
		if (sessions == null)
		{
			if (session == null)
				return;
			sessions = new Session [players.length];
		}
		sessions[index] = session;
	}
}
