package net.ech.casino.videopoker.machines;

import net.ech.casino.videopoker.*;

/**
 * A machine found at VideoPokerClassic.com
 */
public class DoubleDoubleBonus
	extends VideoPokerMachine
{
	/**
	 * Constructor.
	 */
	public DoubleDoubleBonus()
	{
		super(new PayTableEntry[] {
			new PayTableEntry(
				"ROYAL FLUSH",
				new RoyalFlushPattern(),
				new Reward(250),
				new Reward(800)
			),
			new PayTableEntry(
				"FOUR ACES & 2,3,4",
				new FourAndKickerPattern("A", "234"),
				new Reward(400)
			),
			new PayTableEntry(
				"FOUR 2,3,4s & A,2,3,4",
				new FourAndKickerPattern("234", "A234"),
				new Reward(160)
			),
			new PayTableEntry(
				"FOUR ACES",
				new TuplePattern(4, "A"),
				new Reward(160)
			),
			new PayTableEntry(
				"STRAIGHT FLUSH",
				new StraightFlushPattern(),
				new Reward(50)
			),
			new PayTableEntry(
				"FOUR 2s, 3s, 4s",
				new TuplePattern(4, "234"),
				new Reward(80)
			),
			new PayTableEntry(
				"FOUR OF A KIND",
				new TuplePattern(4),
				new Reward(50)
			),
			new PayTableEntry(
				"FULL HOUSE",
				new FullHousePattern(),
				new Reward(7)
			),
			new PayTableEntry(
				"FLUSH",
				new FlushPattern(),
				new Reward(5)
			),
			new PayTableEntry(
				"STRAIGHT",
				new StraightPattern(),
				new Reward(4)
			),
			new PayTableEntry(
				"THREE OF A KIND",
				new TuplePattern(3),
				new Reward(3)
			),
			new PayTableEntry(
				"TWO PAIR",
				new TwoPairsPattern(),
				new Reward(1)
			),
			new PayTableEntry(
				"JACKS OR BETTER",
				new TuplePattern(2, "JQKA"),
				new Reward(1)
			)
		});
	}
}
