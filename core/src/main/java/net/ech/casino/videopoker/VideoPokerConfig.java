//
// VideoPokerConfig.java
//

package net.ech.casino.videopoker;

import java.util.HashSet;
import java.util.Set;
import net.ech.casino.Money;

/**
 * 
 */
public class VideoPokerConfig
	implements Constants
{
	private int maximumWager;

	public VideoPokerMachine getDefaultMachine()
	{
		return null;
	}

	public Money getDefaultCreditValue()
	{
		return new Money(1);
	}

	public int getMaximumWager()
	{
		return maximumWager;
	}

	public Set<Money> getValidCreditValues()
	{
		return new HashSet<Money>();
	}
}
