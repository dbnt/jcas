package net.ech.casino.videopoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Video poker game servlet.
 */
public class Grader
	implements Constants
{
	private VideoPokerMachine machine;

	public Grader(VideoPokerMachine machine)
	{
		this.machine = machine;
	}

	public static class Grade
	{
		public int index = -1;
		public String label;
		public Reward reward;

		Grade(int index, String label, Reward reward) {
			this.index = index;
			this.label = label;
			this.reward = reward;
		}
	}

	public Grade grade(final String hand, final boolean maxWager)
	{
		HandInfo handInfo = new HandInfo (hand, machine.getWildCardPattern());
		PayTableEntry[] payTable = machine.getPayTable();
		List<Integer> matchIndexes = new ArrayList<Integer>();
		for (int i = 0; i < payTable.length; ++i) {
			if (payTable[i].getHandPattern().matches(handInfo)) {
				matchIndexes.add(i);
			}
		}
		Collections.sort(matchIndexes, new Comparator<Integer>() {
			@Override
			public int compare(Integer i1, Integer i2) {
				return getReward(i2.intValue(), maxWager).compareTo(getReward(i1.intValue(), maxWager));
			}
		});
		if (matchIndexes.size() > 0) {
			int matchIndex = matchIndexes.get(0).intValue();
			return new Grade(matchIndex, payTable[matchIndex].getLabel(), getReward(matchIndex, maxWager));
		}
		return null;
	}

	private Reward getReward(int grade, boolean maxWager)
	{
		return machine.getPayTable()[grade].getReward(maxWager);
	}
}
