//
// Setting.java
//

package net.ech.casino.paigow;

/**
 * A setting identifies one of the 21 ways to set a pair 
 * of pai gow hands.
 */
public class Setting implements Constants
{
	private final static Setting[] allSettings = 
	{
		new Setting (0, 1), new Setting (0, 2), new Setting (0, 3),
		new Setting (0, 4), new Setting (0, 5), new Setting (0, 6),
		new Setting (1, 2), new Setting (1, 3), new Setting (1, 4),
		new Setting (1, 5), new Setting (1, 6),
		new Setting (2, 3), new Setting (2, 4), new Setting (2, 5),
		new Setting (2, 6),
		new Setting (3, 4), new Setting (3, 5), new Setting (3, 6),
		new Setting (4, 5), new Setting (4, 6),
		new Setting (5, 6),
	};

	/**
	 * Get the full enumeration of 21 Settings as an array.
	 */
	public static Setting[] getAllSettings ()
	{
		return (Setting[]) allSettings.clone ();
	}

	// Indexes of the two cards in the 2nd highest hand.
	private byte index1;
	private byte index2;

	/**
	 * Copy constructor.
	 */
	public Setting (Setting that)
	{
		index1 = that.index1;
		index2 = that.index2;
	}

	/**
	 * Private constructor.
	 */
	private Setting (int index1, int index2)
	{
		this.index1 = (byte) index1;
		this.index2 = (byte) index2;
	}

	/**
	 * Set the given hand according to this Setting.
	 */
	public byte[] set (byte[] hand)
	{
		byte[] result = new byte [CardsInHand];

		int hix = 0;
		for (int i = 0; i < (CardsInHand - 2); ++i)
		{
			while (hix == index1 || hix == index2)
				++hix;
			result[i] = hand[hix];
			++hix;
		}
		result[CardsInHand - 2] = hand[index1];
		result[CardsInHand - 1] = hand[index2];

		return result;
	}
}
