package net.ech.casino.videopoker;

/**
 * Logic for detecting a winning pair, triple, quad, or five of a kind.
 */
public class TuplePattern
	implements HandPattern
{
	private int cardinality;
	private String ranks;

	public TuplePattern (int cardinality)
	{
		this(cardinality, RANKS_STRING);
	}

	/**
	 * @param ranks  set of applicable ranks, expressed as a rank string
	 */
	public TuplePattern (int cardinality, String ranks)
	{
		this.cardinality = cardinality;
		this.ranks = ranks;
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		for (int r = NUMBER_OF_RANKS; --r >= 0; ) {
			if (ranks.indexOf(RANK_CHARS[r]) >= 0 &&
				handInfo.getRankCount(r) + handInfo.getWildCount() >= cardinality) {
				return true;
			}
		}

		return false;
	}
}
