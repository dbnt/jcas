package net.ech.casino.videopoker.machines;

import net.ech.casino.videopoker.*;

/**
 * A machine found at WizardOfOdds.com - 98.52%
 */
public class DeucesAndJokerWild
	extends VideoPokerMachine
{
	/**
	 * Constructor.
	 */
	public DeucesAndJokerWild()
	{
		super(new PayTableEntry[] {
			new PayTableEntry(
				"FIVE WILDS",
				new WildHandPattern(5),
				new Reward(2000)
			),
			new PayTableEntry(
				"NATURAL ROYAL FLUSH",
				new RoyalFlushPattern(),
				new Reward(400),
				new Reward(800)
			),
			new PayTableEntry(
				"NATURAL FOUR DEUCES",
				new WildHandPattern(4),
				new Reward(25)
			),
			new PayTableEntry(
				"WILD ROYAL FLUSH",
				new RoyalFlushPattern(true),
				new Reward(12)
			),
			new PayTableEntry(
				"FIVE OF A KIND",
				new TuplePattern(5),
				new Reward(8)
			),
			new PayTableEntry(
				"STRAIGHT FLUSH",
				new StraightFlushPattern(),
				new Reward(6)
			),
			new PayTableEntry(
				"FOUR OF A KIND",
				new TuplePattern(4),
				new Reward(3)
			),
			new PayTableEntry(
				"FULL HOUSE",
				new FullHousePattern(),
				new Reward(3)
			),
			new PayTableEntry(
				"FLUSH",
				new FlushPattern(),
				new Reward(3)
			),
			new PayTableEntry(
				"STRAIGHT",
				new StraightPattern(),
				new Reward(2)
			),
			new PayTableEntry(
				"THREE OF A KIND",
				new TuplePattern(3),
				new Reward(1)
			)
		}, 1, new RankCardPattern("2"));
	}
}
