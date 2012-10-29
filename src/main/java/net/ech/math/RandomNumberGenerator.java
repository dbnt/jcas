//
// RandomNumberGenerator.java
//

package net.ech.math;

/**
 * A base class for a family of random number generators.  Compatible 
 * with java.util.Random, which seems to be intended as a base class
 * but cannot be properly subtyped due to flaws in its implementation.
 * <p>
 * The pseudo-random number sequence is determined by the inner state
 * of the RandomNumberGenerator instance.  This state may be reset by
 * passing a <code>long</code> value to <code>setSeed</code>.  Note
 * however that if two RandomNumberGenerator instances receive the
 * same seed, they will generate identical "random" sequences.
 * <p>
 * The members of java.util.Random related to Gaussian random numbers are 
 * excluded here.  If Gaussian random numbers were needed, a wrapper class
 * that implements the nextGaussian method in terms of a RandomNumberGenerator
 * could be defined.
 * <p>
 * A RandomNumberGenerator is serializable.
 * <p>
 * A RandomNumberGenerator is cloneable.
 *
 * @author Frank Yellin
 * @author ech@codespinner.com
 * @version 1.0
 * @see java.util.Random
 */
public abstract class RandomNumberGenerator
	implements java.io.Serializable, Cloneable
{
	/**
	 * This constructor does NOTHING!  Each subclass is responsible
	 * for providing constructors that initialize the state of the
	 * generator.
	 */
	public RandomNumberGenerator ()
	{
	}

	/**
	 * Set the seed of this random number generator.
	 *
	 * @param	seed   the initial seed.
	 */
	public abstract void setSeed (long seed);

	private static final int BITS_PER_BYTE = 8;
	private static final int BYTES_PER_INT = 4;

	/**
	 * Generates a user specified number of random bytes.
	 */
	public final void nextBytes(byte[] bytes) {
		int numRequested = bytes.length;

		int numGot = 0, rnd = 0;

		while (true) {
			for (int i = 0; i < BYTES_PER_INT; i++) {
				if (numGot == numRequested)
					return;

				rnd = (i==0 ? next(BITS_PER_BYTE * BYTES_PER_INT)
							: rnd >> BITS_PER_BYTE);
				bytes[numGot++] = (byte)rnd;
			}
		}
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed <code>int</code>
	 * value from this random number generator's sequence.
	 *
	 * @return	the next pseudorandom, uniformly distributed <code>int</code>
	 *			value from this random number generator's sequence.
	 */
	public final int nextInt()
	{
		return next(32);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed <code>long</code>
	 * value from this random number generator's sequence.
	 *
	 * @return	the next pseudorandom, uniformly distributed <code>long</code>
	 *			value from this random number generator's sequence.
	 */
	public final long nextLong()
	{
		// it's okay that the bottom word remains signed.
		return ((long)(next(32)) << 32) + next(32);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed <code>float</code>
	 * value between <code>0.0</code> and <code>1.0</code> from this random
	 * number generator's sequence.
	 *
	 * @return	the next pseudorandom, uniformly distributed <code>float</code>
	 *			value between <code>0.0</code> and <code>1.0</code> from this
	 *			random number generator's sequence.
	 */
	public final float nextFloat()
	{
		int i = next(24);
		return i / ((float)(1 << 24));
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed 
	 * <code>double</code> value between <code>0.0</code> and
	 * <code>1.0</code> from this random number generator's sequence.
	 *
	 * @return	the next pseudorandom, uniformly distributed 
	 *			<code>double</code> value between <code>0.0</code> and
	 *			<code>1.0</code> from this random number generator's sequence.
	 */
	public final double nextDouble()
	{
		long l = ((long)(next(26)) << 27) + next(27);
		return l / (double)(1L << 53);
	}

	/**
	 * This template method is the core of the random number generator. 
	 * Its function is to generate the next pseudo-random number of the
	 * sequence and to return its low 'bits' bits, leaving all higher
	 * bits zero.  'bits' must be in the range 1..32.
	 *
	 * @param	bits	the number of random bits, 1..32.
	 * @return	the next pseudorandom value from this random number generator's sequence.
	 */
	protected abstract int next (int bits);

	/**
	 * Return a synchronized (thread-safe) random number generator
	 * backed by the specified random number generator.	 To guarantee
	 * serial access, it is critical that <strong>all</strong> access
	 * to the backing set is accomplished through the returned set.<p>
	 *
	 * @param  random the instance to be "wrapped" in a synchronized instance.
	 * @return a synchronized view of the specified instance.
	 */
	public static RandomNumberGenerator
		synchronizedInstance (final RandomNumberGenerator random)
	{
		return new RandomNumberGenerator ()
		{
			public synchronized void setSeed (long seed)
			{
				random.setSeed (seed);
			}

			protected synchronized int next (int bits)
			{
				return random.next (bits);
			}
		};
	}

	/**
	 * Clone this random number generator.	Unless either the original
	 * or the clone is reseeded, the two will produce identical sequences.
	 */
	public RandomNumberGenerator copy ()
	{
		try
		{
			return (RandomNumberGenerator) clone ();
		}
		catch (CloneNotSupportedException e)
		{
			// But it is.
			throw new Error ();
		}
	}
}	  
