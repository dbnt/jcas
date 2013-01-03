//
// VideoPokerConfig.java
//

package net.ech.casino.videopoker;

import java.util.Set;
import net.ech.casino.Money;

/**
 * 
 */
public interface VideoPokerConfig
{
	public VideoPokerMachine getDefaultMachine();

	public Money getDefaultCreditValue();

	public int getMaximumWager();

	public Set<Money> getValidCreditValues();
}
