//
// Shoe.java
//

package net.ech.casino;

/**
 * General card shoe type for multiple card games.	Implements ordered 
 * collection of cards as an array of bytes.  Default deck composition
 * utilizes Card/CardConstants' card encoding scheme, but composition
 * may be specialized.	A Shoe maintains a shufflePoint property, representing
 * the position of a "cut" marker card in the shoe as used in some casinos,
 * but does not enforce reshuffling.
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.1
 */
public class Shoe implements CardConstants, Cloneable, java.io.Serializable
{
	// Machine parameters:
	private int numberOfDecks;
	private double minCutFactor;
	private double maxCutFactor;
	private Deck theDeck;

	// The current shoe:
	private byte[] array;
	private int index;
	private int cut;

	// For testing:
	private byte[] stackedCards;

	/**
	 * Constructor.
	 * @param numberOfDecks		Number of standard decks per shoe.
	 */
	public Shoe (int numberOfDecks)
	{
		this (numberOfDecks, new Deck ());
	}

	/**
	 * Constructor.
	 * @param numberOfDecks		Number of decks per shoe.
	 * @param theDeck			The deck to use for card composition.
	 */
	public Shoe (int numberOfDecks, Deck theDeck)
	{
		this.numberOfDecks = numberOfDecks;
		this.theDeck = theDeck;
	}

	/**
	 * Set the range of where the "cut" may be placed.
	 */
	public void setCutRange (double minCutFactor, double maxCutFactor)
	{
		if (minCutFactor < 0 || maxCutFactor < 0 ||
			minCutFactor > 1 || maxCutFactor > 1)
		{
			throw new IllegalArgumentException ();
		}

		this.minCutFactor = Math.min (minCutFactor, maxCutFactor);
		this.maxCutFactor = Math.max (minCutFactor, maxCutFactor);
	}

	/**
	 * @return the number of decks (packs) in this shoe.
	 */
	public int getNumberOfDecks ()
	{
		return numberOfDecks;
	}

	/**
	 * @return the number of cards in this shoe.
	 */
	public int getSize ()
	{
		return numberOfDecks * theDeck.getSize();
	}

	/**
	 * @return whether the "cut" point in the shoe has been 
	 * reached, indicating that the shoe must be reshuffled
	 * before the next deal.
	 */
	public boolean isShufflePending ()
	{
		return array == null || index >= cut;
	}

	/**
	 * Get the number of cards dealt so far from the current shoe.
	 */
	public int getNumberOfCardsDealt ()
	{
		return index;
	}

	/**
	 * Specify the cards that are to appear at the top of the shoe after
	 * a shuffle.  This is for testing only!
	 */
	public void setStackedCards (String cardString)
	{
		int stackLength = cardString.length() / 2;
		this.stackedCards = new byte [stackLength];

		for (int i = 0; i < stackLength; ++i)
		{
			this.stackedCards[i] = Card.parse (cardString, i * 2);
		}
	}

	/**
	 * Randomize the order of the cards in the shoe.
	 */
	public void shuffle (Randomizer random)
	{
		shuffle (getSize(), random);
	}

	/**
	 * Randomize the order of the cards in the shoe, only if 
	 * isShufflePending is initially true.
	 */
	public void shuffleIfPending (Randomizer random)
	{
		if (isShufflePending())
		{
			shuffle(random);
		}
	}

	/**
	 * Randomize the order of the top n cards in the shoe.
	 */
	public void shuffle (int n, Randomizer random)
	{
		if (n < 0 || n > getSize())
		{
			throw new IllegalArgumentException (
				"shuffle " + n + " cards of " + getSize() + "?");
		}

		// Create new card array each time, so that copied shoes,
		// which are shallow copies, remain valid.
		//
		array = new byte [getSize()];

		// Fill card array with initial values.
		compose ();

		// Shuffle.
		shuffle (array, n, random);

		// Reset.
		index = 0;

		// Randomize the cut.
		cut = 0;
		if (minCutFactor > 0 || maxCutFactor > 0)
		{
			int minCut = (int) (minCutFactor * array.length + 0.5);
			minCut = Math.max (minCut, 0);
			int maxCut = (int) (maxCutFactor * array.length + 0.5);
			maxCut = Math.min (maxCut, array.length - 20);
			cut = random.roll (minCut, maxCut);
		}
	}

	/**
	 * Randomize the order of the cards in the shoe, only if 
	 * isShufflePending is initially true.
	 */
	public void shuffleIfPending (int n, Randomizer random)
	{
		if (isShufflePending())
		{
			shuffle(n, random);
		}
	}

	//
	// Fill card array with initial values.
	//
	private void compose ()
	{
		int i = 0;
		for (int d = 0; d < numberOfDecks; ++d)
		{
			theDeck.copyCards (array, i);
			i += theDeck.getSize();
		}
		if (i != getSize())
			throw new RuntimeException ("failed to fully initialize shoe");
	}

	/**
	 * Apply the shuffling algorithm.
	 */
	protected void shuffle (byte[] array, int n, Randomizer random)
	{
		// Default: use the randomizer's shuffle method.
		random.shuffle (array, n);

		if (stackedCards != null)
		{
			stackTheDeck();
		}
	}

	private void stackTheDeck ()
	{
		for (int i = 0; i < stackedCards.length; ++i)
		{
			byte card = stackedCards[i];
			if (card == NilCard)
				continue;		  // a don't-care value, presumably

			int found = find (array, i, card);
			if (found < 0)
			{
				throw new RuntimeException ("stackTheDeck: " +
					Card.toString (card) + " not found");
			}

			swap (array, i, found);
		}
	}

	private static int find (byte[] array, int startix, byte value)
	{
		for (int i = startix; i < array.length; ++i)
		{
			if (array[i] == value)
				return i;
		}
		return -1;
	}

	private static void swap (byte[] array, int ix1, int ix2)
	{
		byte temp = array[ix1];
		array[ix1] = array[ix2];
		array[ix2] = temp;
	}

	/**
	 * Draw one card.
	 * @return the card dealt
	 */
	public byte draw ()
	{
		return array[index++];
	}

	/**
	 * Make a backup copy (shallow) of this shoe.
	 */
	public Shoe copy ()
	{
		// Let the default clone() do a shallow copy, which is what
		// we want.
		try
		{
			return (Shoe) clone ();
		}
		catch (CloneNotSupportedException e)
		{
			// But it is.
			return null;
		}
	}
}
