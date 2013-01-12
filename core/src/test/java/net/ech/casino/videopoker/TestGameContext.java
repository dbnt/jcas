package net.ech.casino.videopoker;

import java.util.*;
import net.ech.casino.*;
import net.ech.casino.videopoker.machines.DoubleDoubleBonus;

public class TestGameContext
	implements GameContext
{
	NotRandomizer notRandomizer = new NotRandomizer();

	public <T> T get(Class<T> requestedClass)
		throws CasinoException
	{
		if (Accounting.class.equals(requestedClass)) {
			return requestedClass.cast(new Accounting() {
				public void executeTransaction (Transaction trans)
					throws CasinoException
				{
				}
			});
		}
		if (Randomizer.class.equals(requestedClass)) {
			return requestedClass.cast(notRandomizer);
		}
		if (VideoPokerConfig.class.equals(requestedClass)) {
			return requestedClass.cast(new VideoPokerConfig() {
				public VideoPokerMachine getDefaultMachine() {
					return new DoubleDoubleBonus();
				}
				public Money getDefaultCreditValue() {
					return new Money(0.25);
				}
				public int getMaximumWager() {
					return 5;
				}
				public Set<Money> getValidCreditValues() {
					return new HashSet<Money>(Arrays.asList(new Money[] { new Money(0.25), new Money(1) }));
				}
			});
		}
		throw new CasinoException();
	}
}
