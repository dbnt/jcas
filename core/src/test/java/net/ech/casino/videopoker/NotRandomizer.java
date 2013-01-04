package net.ech.casino.videopoker;

import net.ech.casino.*;
import net.ech.math.*;

public class NotRandomizer
	extends Randomizer
{
	public NotRandomizer()
	{
		super(new NotRandomNumberGenerator());
	}

	public static class NotRandomNumberGenerator
		extends RandomNumberGenerator
	{
		@Override
		public void setSeed(long seed)
		{
		}

		@Override
		protected int next(int nbits)
		{
			return 0;
		}
	}
}
