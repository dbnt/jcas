//
// Payout.java	
// 

package net.ech.casino.videopoker;

/**
 * A row of a video poker payout table.
 */
public class PayTableEntry
	implements Comparable<PayTableEntry>
{
	private String label;
	private Pattern pattern;
	private Reward reward;

	/**
	 * Constructor.
	 */
	public PayTableEntry (String label, Pattern pattern, Reward reward)
	{
		this.label = label;
		this.pattern = pattern;
		this.reward = reward;
	}

	/**
	 * Get this payout row's label.
	 */
	public String getLabel ()
	{
		return label;
	}

	/**
	 * Get the pattern.
	 */
	public Pattern getPattern()
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
