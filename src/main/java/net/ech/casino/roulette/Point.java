//
// Point.java  
// 

package net.ech.casino.roulette;

import java.util.*;

/**
 * Utilities for handling roulette betting point bit masks.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Point implements Constants
{
	private static Hashtable nameToPoint = new Hashtable ();
	private static Hashtable pointToName = new Hashtable ();
	static 
	{
		name ("1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36", Red);
		name ("2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35", Black);
		name ("1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35", Odd);
		name ("2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36", Even);
		name ("1,2,3,4,5,6,7,8,9,10,11,12", Twelve1);
		name ("13,14,15,16,17,18,19,20,21,22,23,24", Twelve2);
		name ("25,26,27,28,29,30,31,32,33,34,35,36", Twelve3);
		name ("1,4,7,10,13,16,19,22,25,28,31,34", Col1);
		name ("2,5,8,11,14,17,20,23,26,29,32,35", Col2);
		name ("3,6,9,12,15,18,21,24,27,30,33,36", Col3);
		// Aliases for Low and High:
		name ("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18", "LOW");
		name ("19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36", "HIGH");
		name ("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18", Low);
		name ("19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36", High);
	}

	private static void name (String pointString, String name)
	{
		Long point = new Long (Point.parse (pointString));
		nameToPoint.put (name, point);
		pointToName.put (point, name);
	}

	/**
	 * Do not instantiate.	A point is represented by a long.
	 */
	private Point ()
	{
	}

	/**
	 * Parse from comma-separated String.
	 */
	public static long parse (String pointString)
		throws NumberFormatException
	{
		StringTokenizer tok = new StringTokenizer (pointString, ",");
		if (!tok.hasMoreTokens ())
			throw new NumberFormatException ("empty point");

		// Try for a name.
		String token = tok.nextToken ();
		Long l =
			(Long) nameToPoint.get (token.toUpperCase ().replace ('_', ' '));
		if (l != null)
			return l.longValue ();

		// Must be a comma-separated list of numbers.
		long point = 0;
		for (;;)
		{
			int spot = Integer.parseInt (token);
			if (spot == 0 && token.length () == 2)
				spot = DoubleZero;
			if (spot > HighNumber)
				throw new NumberFormatException (spot + " out of range");

			point |= 1L << (spot - DoubleZero);
			if (!tok.hasMoreTokens ())
				break;
			token = tok.nextToken ();
		}

		return point;
	}

	/**
	 * Get the number of points covered by this bet.
	 */
	public static int count (long point)
	{
		int count = 0;

		long mask = 0x1L;
		int nbits = HighNumber - DoubleZero + 1;
		for (int i = 0; i < nbits; ++i)
		{
			if ((point & mask) != 0)
				++count;
			mask <<= 1;
		}

		return count;
	}

	/**
	 * Format as a name or a comma-separated String.
	 */
	public static String toString (long point)
	{
		return toString (point, true);
	}

	/**
	 * Format as a name or a comma-separated String.
	 */
	public static String toString (long point, boolean useName)
	{
		if (useName)
		{
			String name = (String) pointToName.get (new Long (point));
			if (name != null)
				return name;
		}

		StringBuilder buf = new StringBuilder ();

		int bitCount = 0;
		long bitMask = 1L;
		for (int spot = DoubleZero; spot <= HighNumber; ++spot)
		{
			if ((point & bitMask) != 0L)
			{
				if (bitCount++ > 0)
				{
					buf.append (",");
				}
				if (spot == DoubleZero)
				{
					buf.append ("00");
				}
				else
				{
					buf.append (spot);
				}
			}
			bitMask <<= 1;
		}

		return buf.toString ();
	}
}
