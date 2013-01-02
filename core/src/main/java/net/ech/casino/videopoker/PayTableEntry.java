//
// Payout.java	
// 

package net.ech.casino.videopoker;

/**
 * A row of a video poker payout table.
 */
public class PayTableEntry
{
	private HandPattern pattern;
	private Reward reward;

	/**
	 * Constructor.
	 */
	public PayTableEntry (HandPattern pattern, Reward reward)
	{
		this.pattern = pattern;
		this.reward = reward;
	}

	/**
	 * Get the pattern.
	 */
	public HandPattern getHandPattern()
	{
		return pattern;
	}

	/**
	 * Get the reward.
	 */
	public Reward getReward()
	{
		return reward;
	}
}
