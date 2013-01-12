package net.ech.casino.videopoker;

import java.util.LinkedList;
import net.ech.casino.*;
import net.ech.math.*;

public class NotRandomizer
	extends Randomizer
{
	private LinkedList<Integer> queue = new LinkedList<Integer>();

	public NotRandomizer()
	{
		super(new RandomNumberGenerator() {
			@Override
			public void setSeed(long seed) {}
			@Override
			public int next(int bits) {
				throw new RuntimeException("should not be reached");
			}
		});
	}

	public void enqueue(Integer value)
	{
		queue.add(value);
	}

	@Override
	public int nextInt()
	{
		return queue.size() > 0 ? queue.removeFirst() : 0;
	}
}
