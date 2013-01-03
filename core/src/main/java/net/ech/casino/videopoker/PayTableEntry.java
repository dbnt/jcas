//
// Payout.java	
// 

package net.ech.casino.videopoker;

/**
 * A row of a video poker payout table.
 */
public class PayTableEntry
{
	private String label;
	private HandPattern pattern;
	private Reward reward;
	private Reward maxWagerReward;

	/**
	 * Constructor.
	 */
	public PayTableEntry (String label, HandPattern pattern, Reward reward)
	{
		this(label, pattern, reward, reward);
	}

	/**
	 * Constructor.
	 */
	public PayTableEntry (String label, HandPattern pattern, Reward reward, Reward maxWagerReward)
	{
		this.label = label;
		this.pattern = pattern;
		this.reward = reward;
		this.maxWagerReward = maxWagerReward;
	}

	/**
	 * A label, for logging.
	 */
	public String getLabel()
	{
		return label;
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
	public Reward getReward(boolean maxWager)
	{
		return maxWager ? maxWagerReward : reward;
	}
}
