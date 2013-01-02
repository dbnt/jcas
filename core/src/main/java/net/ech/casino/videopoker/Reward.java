//
// Reward.java	
// 

package net.ech.casino.videopoker;

/**
 * A row of a video poker payout table.
 */
public class Reward
	implements Comparable<Reward>
{
	private int multiple;

	/**
	 * Constructor.
	 */
	public Reward (int multiple)
	{
		this.multiple = multiple;
	}

	public void grant(VideoPokerState state)
	{
		state.setWinCredits(state.getWagerCredits() * multiple);
	}

	public int compareTo(Reward that)
	{
		return this.multiple - that.multiple;
	}
}
