package net.ech.casino.videopoker;

/**
 * A card pattern matcher.
 */
public class RankCardPattern
	implements CardPattern
{
	private String ranks;

	public RankCardPattern(String ranks)
	{
		this.ranks = ranks;
	}

	@Override
	public boolean matches (char rank, char suit)
	{
		return ranks.indexOf(rank) >= 0;
	}
}
