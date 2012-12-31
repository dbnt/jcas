//
// LinearCongruentialGenerator.java
//

package net.ech.math;

/**
 * An instance of this class is used to generate a stream of 
 * pseudorandom numbers. The class uses a 48-bit seed, which is 
 * modified using a linear congruential formula. (See Donald Knuth, 
 * <i>The Art of Computer Programming, Volume 2</i>, Section 3.2.1.) 
 * <p>
 * This generator is not synchronized.	To obtain a synchronized
 * instance, call <code>RandomNumberGenerator.synchronizedInstance</code>.
 *
 * @author	Frank Yellin
 * @author	ech@codespinner.com
 */
public class LinearCongruentialGenerator extends RandomNumberGenerator
{
	private long seed;

	private final static long MULTIPLIER = 0x5DEECE66DL;
	private final static long ADDEND = 0xBL;
	private final static long MASK = (1L << 48) - 1;

	/** 
	 * Creates a new random number generator. Its seed is initialized to 
	 * a value based on the current time. 
	 */
	public LinearCongruentialGenerator ()
	{
		setSeed (System.currentTimeMillis ());
	}

	/** 
	 * Creates a new random number generator using a single 
	 * <code>long</code> seed. 
	 *
	 * @param	seed   the initial seed.
	 * @see		java.util.Random#setSeed(long)
	 * @since	JDK1.0
	 */
	public LinearCongruentialGenerator (long seed)
	{
		setSeed (seed);
	}

	/**
	 * Sets the seed of this random number generator using a single 
	 * <code>long</code> seed. 
	 *
	 * @param	seed   the initial seed.
	 */
	public void setSeed (long seed)
	{
		this.seed = (seed ^ MULTIPLIER) & MASK;
	}

	/**
	 * Generates the next pseudorandom number. Subclass should
	 * override this, as this is used by all other methods.
	 *
	 * @param	bits random bits
	 * @return	the next pseudorandom value from this generator's sequence.
	 */
	protected int next (int bits)
	{
		long nextseed = (seed * MULTIPLIER + ADDEND) & MASK;
		seed = nextseed;
		return (int)(nextseed >>> (48 - bits));
	}
}	  
