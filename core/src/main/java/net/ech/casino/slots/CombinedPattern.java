//
// CombinedPattern.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A Pattern that is simple several other patterns grouped together.
 * 
 * @version 1.1
 * @author Dave Giese, dgiese@ech.net
 */
public class CombinedPattern extends Pattern
{
	private Pattern[] patternArray;

	/**
	 * Constructor.	 This constructor takes ...
	*/
	public CombinedPattern (Pattern[] patterns)
	{
		this.patternArray = patterns;
	}	 
		
	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		boolean matches;
		for (int i=0;i<patternArray.length;i++)
		{
			matches = patternArray[i].matches (symbols, game);
			if (matches)
				return matches;
		}
		return (false);
	}	 
}
