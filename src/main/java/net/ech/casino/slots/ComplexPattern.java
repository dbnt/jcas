//
// ComplexPattern.java	
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * This class represents a pattern of a type similar to 1 pair, 2 pair, full house, 3/4 of a kind
 * in poker.   
 * 
 * @version 1.1
 * @author Dave Giese, dgiese@ech.net
 */
public class ComplexPattern extends Pattern
{
	private long maskArray[][];
	private int bitOffset;

	/**
	 * Constructor.	 This constructor is useful for pattern matching where the order
	 * that symbols appear on the reels is not important and where we are looking for 
	 * a certain number of different symbols (ie. a full house, pair, 3 of a kind).	 
	 * @param maskArray	 2D Array saying how many of a pattern need to match.  These
	 * should be put in descending order in the array.
	 * full house = ANY,3 
	 *				ANY,2
	 * 3 of a kind = ANY,3
	 * 4 kings =	KingPattern,4
	 * 2 pair = ANY,2 etc.
	 */
	public ComplexPattern (long maskArray[][], int bitOffset)
	{
		this.maskArray = maskArray;
		this.bitOffset = bitOffset;
	}	 
		

	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		// Sort the symbols by frequency
		long [][] symbolFrequency = new long [symbols.length][2];
		int numDifferentSymbols = 0;
		int i;
		// initialize the frequency array
		for (i=0;i<symbolFrequency.length;i++)
		{	 
			symbolFrequency[i][0] = -1;	   
			symbolFrequency[i][1] = 0; 
		}	 
		 
		for (i=0;i<symbols.length;i++)
		{  
			long symbolmask = symbols[i];
			if (bitOffset > 0)
				symbolmask %= bitOffset;
			symbolmask = 1L << symbolmask;
			for (int j=0;j<symbolFrequency.length;j++)
			{
				if (symbolFrequency[j][0] == -1)
				{	 
					symbolFrequency[j][0] = symbolmask;
					symbolFrequency[j][1]++;
					numDifferentSymbols++;
					break;
				}	 
				else if (symbolmask == symbolFrequency[j][0])
				{	 
					symbolFrequency[j][1]++;
					break;
				}	 
			}
		}	 
		
		Vector vec = new Vector (numDifferentSymbols);
		vec.addElement ((symbolFrequency[0]));
		for (i=1;i<numDifferentSymbols;i++)
		{
			for (int j=0;j<vec.size();j++)
			{
				long currentElement[] = (long []) vec.elementAt(j);
				if (symbolFrequency[i][1] > currentElement[1])
				{	 // found the place in the vector, insert
					vec.insertElementAt(symbolFrequency[i], j);
					break;	
				}
				else if (j==vec.size()-1)
				{	// last time thru the loop, add it at the end 
					vec.addElement (symbolFrequency[i]);
					break;
				}	 
			}
		}	  
			 
		for (i=0;i<maskArray.length;i++)
		{	 
			// if the number of elements does not equal mask 
			// or the rank of the symbol is less than the mask then we fail the test.
			long currentElement[] = (long []) vec.elementAt(i);
			if ( (maskArray[i][1] !=currentElement[1]) ||
				 (maskArray[i][0] > currentElement[0]) )
				return (false);
		}
		return true;
	}
}
