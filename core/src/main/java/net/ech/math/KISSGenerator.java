//
// KISSGenerator.java
// 

package net.ech.math;

import java.util.*;

/**
 * Class KISSGenerator implements the KISS random number generator
 * developed by George Marsaglia at Florida State University through
 * research supported by NSF Grant DMS-9206972.
 * <p>
 * This generator is not synchronized.	To obtain a synchronized
 * instance, call <code>RandomNumberGenerator.synchronizedInstance</code>.
 * 
 * @author Rex Carlson mathmanrex@hotmail.com
 * @author Lee Hasiuk lhasiuk@alumni.caltech.edu
 * @author ech@codespinner.com
 */
public class KISSGenerator extends RandomNumberGenerator
{
		private int seed_w, seed_x, seed_y, seed_z;

		/** 
		 * Constructor.			Seed is initialized to a value based on the
		 * current time. 
		 */
		public KISSGenerator ()
		{
				setSeed (System.currentTimeMillis ());
		}

		/** 
		 * Constructor.
		 * @param		seed   the initial seed.
		 */
		public KISSGenerator (long seed)
		{
				setSeed (seed);
		}

		/**
		 * Set the seed of this random number generator using a single 
		 * <code>long</code> seed. 
		 * @param		seed   the initial seed.
		 */
		public void setSeed (long seed)
		{
				seed_w	=  916191069;  // DO NOT CHANGE THIS VALUE !!!
				seed_x	=  (int)(seed >>> 32);
				seed_y	=  (int)(seed | 1);				 // ensure non-zero value
				seed_z	=  521288629;  // DO NOT CHANGE THIS VALUE !!!
		}

		/**
		 * The core of this random number generator.
		 */
		protected int next (int bits)
		{
				// LC generator - not full period - loop defined by initial seed_w
				seed_w = 30903 * (seed_w & 0xFFFF) + (seed_w >>> 16);

				// LC generator - period = 2^32
				seed_x = 69069 * seed_x + 1327217885;

				//		LFSR generator - period = 2^32-1 (Zero not allowed)
				update_seed_y();

				// LC generator - not full period - loop defined by initial seed_z
				seed_z = 18000 * (seed_z & 0xFFFF) + (seed_z >>> 16);
		
				int rnd = (seed_w << 16) + seed_x + seed_y + (seed_z & 0xFFFF);
				rnd >>>= (32 - bits);
				return rnd;
		}
		
		private void update_seed_y()
		{
				seed_y ^= (seed_y << 13);
				seed_y ^= (seed_y >>> 17);
				seed_y ^= (seed_y << 5);

				// GENERATE FAULT ON ZERO VALUE
				if (seed_y == 0)
						throw new RuntimeException ();
		}
}
