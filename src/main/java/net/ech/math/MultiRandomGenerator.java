//
// MultiRandomGenerator.java  
// 

package net.ech.math;

/**
 * Create an array of synchronized random number generators and use them
 * in rotation to improve throughput.
 * <p>
 * This class is not synchronized.	Do not create a synchronized instance
 * with RandomNumberGenerator.synchronizedInstance, because each generator
 * in the array is synchronized, and the whole point of this class is to
 * increase throughput!
 * 
 * @version 1.0
 * @author ech@codespinner.com
 */
public class MultiRandomGenerator extends RandomNumberGenerator
{
	private final static int DEFAULT_CARDINALITY = 12;

	private RandomNumberGenerator[] fleet;
	private int current;

	/**
	 * Constructor.
	 */
	public MultiRandomGenerator ()
	{
		this (DEFAULT_CARDINALITY);
	}

	/**
	 * Constructor.
	 */
	public MultiRandomGenerator (int n)
	{
		init (n, new LinearCongruentialGenerator ());
	}

	/**
	 * Constructor.
	 */
	public MultiRandomGenerator (RandomNumberGenerator exemplar)
	{
		init (DEFAULT_CARDINALITY, exemplar);
	}

	/**
	 * Workhorse constructor.
	 */
	public MultiRandomGenerator (int n, RandomNumberGenerator exemplar)
	{
		init (n, exemplar);
	}

	/**
	 * Constructor.
	 */
	public MultiRandomGenerator (Class rngClass)
	{
		this (DEFAULT_CARDINALITY, rngClass);
	}

	/**
	 * Constructor.
	 */
	public MultiRandomGenerator (int n, Class rngClass)
	{
		try
		{
			init (n, (RandomNumberGenerator) rngClass.newInstance ());
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException (rngClass.getName ());
		}
	}

	//
	// Shared initialization routine.
	//
	private void init (int n, RandomNumberGenerator exemplar)
	{
		fleet = new RandomNumberGenerator [n];
	
		for (int i = 0; i < n; ++i)
		{
			fleet[i] = exemplar.copy ();
		}

		setSeed (System.currentTimeMillis ());
	}

	/**
	 * Set the seed of this random number generator.
	 * @param	seed   the initial seed.
	 */
	public void setSeed (long seed)
	{
		RandomNumberGenerator seeder = new LinearCongruentialGenerator (seed);
		for (int i = 0; i < fleet.length; ++i)
		{
			fleet[i].setSeed (seeder.nextLong ());
		}
	}

	/**
	 * Generate the next pseudo-random number of the sequence.
	 * @param	bits	the number of random bits, 1..32.
	 * @return	the next pseudorandom value 
	 */
	protected int next (int bits)
	{
		// Make write access to current as brief as possible.  Without 
		// synchronization, multiple activations of this method will 
		// sometimes fail to get the next generator in sequence, but
		// strict sequencing is not as important as fast throughput.
		//
		RandomNumberGenerator rng = fleet[Math.abs (current) % fleet.length];
		++current;
		return rng.next (bits);
	}
}
