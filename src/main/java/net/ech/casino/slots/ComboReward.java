//
// ComboReward.java	 
// 

package net.ech.casino.slots;

/**
 * A ComboReward is two Rewards in one.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class ComboReward extends Reward
{
	private Reward r1;
	private Reward r2;

	/**
	 * Constructor.	 Takes the two Rewards to combine into one.
	 */
	public ComboReward (Reward r1, Reward r2)
	{
		this.r1 = r1;
		this.r2 = r2;
	}

	/**
	 * Execute the effect of hitting this payline on the given game.
	 */
	public void reward (SlotCore core)
	{
		r1.reward (core);
		r2.reward (core);
	}

	/**
	 * For analysis report.
	 */
	public String toString ()
	{
		return r1.toString() + "+" + r2.toString();
	}
}
