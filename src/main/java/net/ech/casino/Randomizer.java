//
// Randomizer.java	
// 

package net.ech.casino;

import net.ech.math.*;

/**
 * Class Randomizer wraps a standard Java random number 
 * generator and extends its services to include methods
 * useful in a casino game setting.
 * 
 * @see java.util.Random
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 * @author Improved modulus operation in method roll contributed by
 * Rex R. Carlson, mathmanrex@hotmail.com
 */
public class Randomizer
{
	// the internal generator
	private RandomNumberGenerator generator;

	/**
	 * Constructor.	 Set the internal generator to an instance of
	 * java.util.Random, seeded by the system clock.
	 */
	public Randomizer ()
	{
		this (new LinearCongruentialGenerator());
	}

	/**
	 * Constructor.	 Set the internal generator to the given
	 * instance.
	 * @param generator a random number generator
	 */
	public Randomizer (RandomNumberGenerator generator)
	{
		setGenerator (generator);
	}

	/**
	 * Set the internal random number generator.
	 * @param generator a random number generator
	 */
	public void setGenerator (RandomNumberGenerator generator)
	{
		this.generator = generator;
	}

	/**
	 * Generate the next 32-bit integer.
	 * @return a random integer
	 */
	public int nextInt ()
	{
		return generator.nextInt ();
	}

	/**
	 * Generate the next 64-bit integer.
	 * @return a random integer
	 */
	public long nextLong()
	{
		return generator.nextLong ();
	}

	/**
	 * Generate a new random number in the range (lo..hi), inclusize.
	 * @param lo		the lower limit of the range
	 * @param hi		the upper limit of the range
	 * @return a random integer
	 * @exception IllegalArgumentException if lo >= hi
	 * @exception IllegalArgumentException if range exceeds 2^16-1
	 */
	public final int roll (int lo, int hi)
	{
		int rand = nextInt ();
		int range = hi - lo + 1;
		if (range < 1)
			throw new IllegalArgumentException (lo + ">=" + hi);
		short sRange = (short) range;
		if (sRange != range)
			throw new IllegalArgumentException ("roll range limit exceeded");

		// Improved modulus operation is less sensitive to the modularity
		// of the number produced by the internal generator.
		short off = (short)
			(((rand >>> 16) * range + (((rand & 0xFFFF) * range) >>> 16)) >>> 16); 

		return lo + off;
	}

	/**
	 * Shuffle an array of integers. 
	 * @param cards		the array of integers to shuffle
	 * @exception IllegalArgumentException if length of array exceeds 2^16-1
	 */
	public final void shuffle (int[] cards)
	{
		shuffle (cards, cards.length);
	}

	/**
	 * Shuffle the first n values of an array of integers.
	 * Select the values of the first n elements randomly 
	 * from the entire array. 
	 * @param cards		the array of integers to shuffle
	 * @param limit		the number of integers to shuffle
	 * @exception IllegalArgumentException if length of array exceeds 2^16-1
	 */
	public final void shuffle (int[] cards, int limit)
	{
		int top = cards.length - 1;
		limit = Math.min (limit, top);

		for (int i = 0; i < limit; ++i)
		{
			int index = roll (i, top);
			int temp = cards[index];
			cards[index] = cards[i];
			cards[i] = temp;
		}
	}

	/**
	 * Shuffle an array of bytes. 
	 * @param cards		the array of bytes to shuffle
	 * @exception IllegalArgumentException if length of array exceeds 2^16-1
	 */
	public final void shuffle (byte[] cards)
	{
		shuffle (cards, cards.length);
	}

	/**
	 * Shuffle the first n values of an array of bytes.
	 * Select the values of the first n elements randomly 
	 * from the entire array. 
	 * @param cards		the array of bytes to shuffle
	 * @param limit		the number of integers to shuffle
	 * @exception IllegalArgumentException if length of array exceeds 2^16-1
	 */
	public final void shuffle (byte[] cards, int limit)
	{
		int top = cards.length - 1;
		limit = Math.min (limit, top);

		for (int i = 0; i < limit; ++i)
		{
			int index = roll (i, top);
			byte temp = cards[index];
			cards[index] = cards[i];
			cards[i] = temp;
		}
	}
}
