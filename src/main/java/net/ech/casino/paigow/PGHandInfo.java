//
// PGHandInfo.java	
// 
package net.ech.casino.paigow;

/**
 * A PGHandInfo object is a complete pai gow poker hand.
 * 
 * @author Istina Mannino, imannino@pacificnet.net
 */
public class PGHandInfo implements Constants
{
	private int cardsInHand;
	private int wildCount;
	private int lowRank = -1;
	private int highRank = -1;
	private int highPairRank = -1;
	private int[] tupleCounts = new int [4];
	private boolean flushFlag = false;
	private boolean straightFlag = false;
	private boolean straightFlushFlag = false;
	private boolean aceLowStraightFlag = false;
	private boolean aceLowSFFlag = false;
	private int[] skippedCards;
	private int skippedCount = 0;
	private int longestStraight = 1;
	private int longestStraightFlush = 1;
	private int straightStart = 0; 
	private int straightEnd = 0;
	private int sfStart = 0; 
	private int sfEnd = 0; 
	private int[] ranks;
	private int[] suits;
	private int[] cardIndex;
	private int[] suitCounts; 
	private int majoritySuit = 0;
	private int jokerIndex = -1;
	private int jokerPos = -1;
	private int jokerRank = -1;
	private int jokerSuit = -1;
	private boolean isAce = false;

	//
	// Constructor.	 This pre-computes the results of all the queries.
	//
	public PGHandInfo(byte[] byteHand, int cih)
	{
		// byte[] byteHand = stringsToBytes(stringHand);
		cardsInHand = cih;
		skippedCards = new int[cardsInHand];
		int rankCount = 0;
		ranks = new int[cardsInHand];
		suits = new int[cardsInHand];
		cardIndex = new int[cardsInHand];
		suitCounts = new int[NumberOfSuits]; 

		//
		// Rank the cards, sorting out the wild cards.
		//

		//int firstSuit = -1;
		for (int i = 0; i < cardsInHand; ++i) {
			int rank = cardValueToRank(byteHand[i]);
			int suit = cardValueToSuit(byteHand[i]);
			if (!isWild(rank)) {
				cardIndex[rankCount] = i;
				ranks[rankCount] = rank;
				suits[rankCount] = suit;
				++rankCount;

				suitCounts[suit]++;
			}
			else
				jokerPos = i;

		}

		for (int i = 1; i < suitCounts.length; i++) {
			if (suitCounts[majoritySuit] < suitCounts[i])
				majoritySuit = i;
		}

		//
		// Sort the ranks from lowest to highest.
		// Note: must preserve the order of pair, triple and quad cards.
		//
		for (int i = 1; i < rankCount; ++i) {
			for (int j = 1; j < rankCount; ++j) {
				if (ranks[j - 1] > ranks[j]) {
					// swap j-1 with j
					int ranktemp = ranks[j - 1];
					int suittemp = suits[j - 1];
					int citemp = cardIndex[j - 1];
					ranks[j - 1] = ranks[j];
					suits[j - 1] = suits[j];
					cardIndex[j - 1] = cardIndex[j]; 
					ranks[j] = ranktemp;
					suits[j] = suittemp;
					cardIndex[j] = citemp;
				}
			}
		}

		//
		// Remember joker and number of high and low ranks.
		//
		wildCount = cardsInHand - rankCount;
		if (rankCount > 0) {
			lowRank = ranks[0];
			highRank = ranks[rankCount - 1];
			if (highRank == Ace)
				isAce = true;
		}

		//
		// Check for straight flush

		int currentSFSize = 1;
		int currentSFStart = 0;
		int sfCount = 0;
		int currentSkippedCount = 0;
		int[] currentSkippedCards = new int[cardsInHand];
		int currentJokerRank = -1;
		int currentJokerIndex = -1; 

		// Check for Ace low straight flush.
		if (wildCount == 1 || ranks[cardsInHand - 1 - wildCount] == Ace) {
			int wantedRank = Deuce;
			int wantedSuit = majoritySuit;
			if (ranks[cardsInHand - 1 - wildCount] != Ace) {
				currentJokerRank = Ace;
				currentJokerIndex = cardsInHand - 1;
			}
			// Insert a sentinel, in case we don't have an ace.
			if (wildCount == 1)
				ranks[cardsInHand - 1] = Joker;
			for (int j = 0; j < cardsInHand; ) {
				if (ranks[j] == wantedRank && suits[j] == wantedSuit) {
					// the card we need to continue the straight flush
					wantedSuit = suits[j];
					j++;
					wantedRank++;
				}
				else if (ranks[j] == wantedRank || ranks[j] == wantedRank-1) {
					// possible member of a tuple, but not the suit we want
					currentSkippedCards[currentSkippedCount] = j;
					currentSkippedCount++;
					j++;
				}
				else if (wildCount == 1 && currentJokerRank == -1 &&
						 wantedRank < Six) {
					// if the joker's available, assign it a role to play
					currentJokerRank = wantedRank;
					currentJokerIndex = j;
					wantedRank++;
				}
				else {
					// This will always happen because the last card is
					// either an ace or joker.
					if (wantedRank >= Six) {
						// Found an ace-low straight!
						jokerIndex = currentJokerIndex;
						jokerSuit = wantedSuit;
						sfStart = 0;
						sfEnd = j - 1;

						boolean validAce = false;
						int validAceIndex = -1;
						System.out.println("wantedSuit: " + wantedSuit);
						for (int i = j; i < cardsInHand; i++) {
							if (ranks[i] == Ace && suits[i] == wantedSuit) {
								validAce = true;	
								validAceIndex = i;
								System.out.println("validAce is true and validAceIndex is " + validAceIndex);
								System.out.println("suits[" + i + "]: " +
												   suits[i]);
							}
						}
						if (!validAce)
							break;

						if (wildCount == 1) {
							if (currentJokerRank != -1) { 
								insertJoker(currentJokerRank, jokerSuit,
											jokerIndex);
								if (currentJokerRank == Ace) 
									isAce = true;
								else
									sfEnd++;
							}
							else {
								insertJoker(Ace, jokerSuit, cardsInHand - 1);
								isAce = true;
							}
						}
						longestStraightFlush = wantedRank;
						aceLowSFFlag = true;
						skippedCount = currentSkippedCount;
						if (skippedCount == 0) {
							for (int i = j; i < cardsInHand; i++) {
								if (i != validAceIndex) {
									currentSkippedCards[skippedCount] = i;
									skippedCount++;
								}
							}
						}
						System.out.println("skippedCount: " + skippedCount);
						for (int sc = 0; sc < skippedCount; sc++) {
							if (jokerIndex != -1 && 
								currentSkippedCards[sc] >= jokerIndex) {
								currentSkippedCards[sc]++;
							}
						}
						System.arraycopy(currentSkippedCards, 0, skippedCards,
										 0, currentSkippedCards.length); 
						/*
						System.out.println("skippedCards[0]: " + 
										   skippedCards[0]
										   + " skippedCards[1]: " +
										   skippedCards[1]);
						System.out.println("ranks[skippedCards[0]]: " + 
										   ranks[skippedCards[0]]
										   + " ranks[skippedCards[1]]: " +
										   ranks[skippedCards[1]]);
						System.out.println("suits[skippedCards[0]]: " + 
										   suits[skippedCards[0]]
										   + " suits[skippedCards[1]]: " +
										   suits[skippedCards[1]]);
						*/
					}
					break;
				}
			}
			// System.out.println("aceLowSFFlag: " + aceLowSFFlag);
		}

		// If there is no ace low straight flush then look for a standard
		// straight flush.
		if (!aceLowSFFlag) {
			currentSFSize = 1;
			currentSFStart = 0;
			sfCount = 0;
			currentSkippedCount = 0;
			currentSkippedCards = new int[cardsInHand];
			jokerRank = -1;
			jokerIndex = -1;
			jokerSuit = -1;
			currentJokerRank = -1;
			currentJokerIndex = -1; 

			for (int j = 0; j < (cardsInHand - wildCount); j++) {
				if (j + 1 < cardsInHand - wildCount && 
					 (ranks[j] == ranks[j+1] - 1 || ranks[j] == ranks[j+1])) {
					if (suits[j] != majoritySuit || 
						suits[j+1] != majoritySuit) {
						// Need to skip card but still may have a straight 
						// flush.
						currentSkippedCards[currentSkippedCount] = j;
						currentSkippedCount++;
					}
					else {
						// If suits equal then continuing a straight flush
						currentSFSize++;
					}
				}
				else if (wildCount == 1 && currentJokerIndex == -1 &&
						 ranks[j] < Ace && ranks[j] == ranks[j+1] - 2 &&
						 suits[j] == majoritySuit && 
						 suits[j+1] == majoritySuit) {
					// Continuing the current straight flush with a joker.
					currentJokerRank = ranks[j] + 1;
					currentJokerIndex = j + 1;
					currentSFSize += 2;
				}
				else {
					if (wildCount == 1 && currentJokerRank == -1) {
						if (ranks[j] != Ace) {
							currentJokerRank = ranks[j] + 1;
							currentJokerIndex = j + 1;
						}
						else { 
							currentJokerRank = ranks[currentSFStart] - 1;
							currentJokerIndex = currentSFStart;
						}
						currentSFSize++;
					}
					if (currentSFSize >= longestStraightFlush) {
						// the biggest straight flush so far just ended
						if (currentSFSize > longestStraightFlush)
							sfCount = 0;
						jokerRank = currentJokerRank;
						jokerIndex = currentJokerIndex;
						longestStraightFlush = currentSFSize;
						sfStart = currentSFStart;
						sfCount++;
						skippedCount = currentSkippedCount;
						System.arraycopy(currentSkippedCards, 0, 
										 skippedCards, 0, 
										 currentSkippedCards.length); 
					}
					currentSFSize = 1;
					currentSkippedCount = 0;
					currentJokerRank = -1;
					currentJokerIndex = -1;
					if (j + 1 < cardsInHand - wildCount) {
						// straight ending because the next card is bad
						// so prepare to recognize a new straight
						currentSFStart = j + 1;
					}
				}
			}
			sfEnd = sfStart + longestStraightFlush + skippedCount - 1;

			if (longestStraightFlush >= 5) {
				straightFlushFlag = true;
				if (jokerRank != -1) {
					// Insert the joker if it is being used to complete 
					// a straight.
					insertJoker(jokerRank, majoritySuit, jokerIndex);
				}
			}
			else
				jokerRank = -1;
		}

		//
		// Check for straight if there isn't a straight flush.
		//
		if (!straightFlushFlag && !aceLowSFFlag) {
			// Check for flush.
			if (suitCounts[majoritySuit] >= 5) {
				flushFlag = true;
			}
			else if (suitCounts[majoritySuit] == 4 && wildCount == 1) {
				// Flush cards with a Joker
				int possibleJoker = Ace;
				int jokerIndex = cardsInHand - 1;
				flushFlag = true;
				for (int i = cardsInHand - wildCount - 1; i >= 0; i--) {
					if (suits[i] == majoritySuit) {
						if (possibleJoker == ranks[i]) {
							possibleJoker--;
							jokerIndex--;
						}
					}
					else if (possibleJoker <= ranks[i]) {
						jokerIndex--;
					}
				}
				insertJoker(possibleJoker, majoritySuit, jokerIndex); 
			}

			if (!flushFlag) {
				int currentStraightSize = 1;
				int currentStart = 0;
				int straightCount = 0;
				currentSkippedCount = 0;
				currentSkippedCards = new int[cardsInHand];
				jokerRank = -1;
				jokerIndex = -1;
				jokerSuit = -1;
				currentJokerRank = -1;
				currentJokerIndex = -1; 

				// Check for Ace low straight.
				if (wildCount == 1 || 
					ranks[cardsInHand - 1 - wildCount] == Ace) {
					int wantedCard = Deuce;
					if (ranks[cardsInHand - 1 - wildCount] != Ace) {
						currentJokerRank = Ace;
						currentJokerIndex = cardsInHand - 1;
					}
					// Insert a sentinel, in case we don't have an ace.
					if (wildCount == 1)
						ranks[cardsInHand - 1] = Joker;
					for (int j = 0; j < cardsInHand; ) {
						if (ranks[j] == wantedCard) {
							j++;
							wantedCard++;
						}
						else if (j > 0 && ranks[j-1] == ranks[j]) {
							currentSkippedCards[currentSkippedCount] = j - 1;
							currentSkippedCount++;
							j++;
						}
						else if (wildCount == 1 && currentJokerRank == -1) {
							currentJokerRank = wantedCard;
							currentJokerIndex = j;
							wantedCard++;
						}
						else {
							// This will always happen because the last card is
							// either an ace or joker.
							if (wantedCard >= Six) {
								// Found an ace-low straight!
								jokerIndex = currentJokerIndex;
								straightStart = 0;
								straightEnd = j - 1;
								if (wildCount == 1) {
									if (currentJokerRank != -1) { 
										insertJoker(currentJokerRank, 
													getLegalSuit(currentJokerRank),
													jokerIndex);
										if (currentJokerRank == Ace) 
											isAce = true;
										else
											straightEnd++;
									}
									else {
										insertJoker(Ace, getLegalSuit(Ace), 
													cardsInHand - 1);
										isAce = true;
									}
								}
								longestStraight = wantedCard;
								aceLowStraightFlag = true;
								skippedCount = currentSkippedCount;
								for (int sc = 0; sc < skippedCount; sc++) {
									if (jokerIndex != -1 && 
										currentSkippedCards[sc] >= jokerIndex) {
										currentSkippedCards[sc]++;
									}
								}
								System.arraycopy(currentSkippedCards, 0, 
												 skippedCards, 0, 
												 currentSkippedCards.length); 
							}
							break;
						}
					}
				}

				// If there is no ace-low straight then look for a 
				// standard straight.
				if (!aceLowStraightFlag) {
					currentStraightSize = 1;
					currentStart = 0;
					straightCount = 0;
					currentSkippedCount = 0;
					currentSkippedCards = new int[cardsInHand];
					jokerRank = -1;
					jokerIndex = -1;
					jokerSuit = -1;
					currentJokerRank = -1;
					currentJokerIndex = -1; 

					for (int j = 0; j < (cardsInHand - wildCount); j++) {
						if (j + 1 < cardsInHand - wildCount && 
							 ranks[j] == ranks[j+1] - 1) {
							// Continuing the current straight
							currentStraightSize++;
						}
						else if (j + 1 < cardsInHand - wildCount && 
								 ranks[j] == ranks[j+1]) {
							currentSkippedCards[currentSkippedCount] = j;
							currentSkippedCount++;
						}
						else if (wildCount == 1 && currentJokerIndex == -1 &&
								 ranks[j] < Ace && ranks[j] == ranks[j+1] - 2) {
							// Continuing the current straight with a joker.
							currentJokerRank = ranks[j] + 1;
							currentJokerIndex = j + 1;
							currentStraightSize += 2;
						}
						else {
							if (wildCount == 1 && currentJokerRank == -1) {
								if (ranks[j] != Ace) {
									currentJokerRank = ranks[j] + 1;
									currentJokerIndex = j + 1;
								}
								else { 
									currentJokerRank = ranks[currentStart] - 1;
									currentJokerIndex = currentStart;
								}
								currentStraightSize++;
							}
							if (currentStraightSize >= longestStraight) {
								// the biggest straight so far just ended
								if (currentStraightSize > longestStraight)
									straightCount = 0;
								jokerRank = currentJokerRank;
								jokerIndex = currentJokerIndex;
								longestStraight = currentStraightSize;
								straightStart = currentStart;
								straightCount++;
								skippedCount = currentSkippedCount;
								System.arraycopy(currentSkippedCards, 0, 
												 skippedCards, 0, 
												 currentSkippedCards.length); 
							}
							currentStraightSize = 1;
							currentSkippedCount = 0;
							currentJokerRank = -1;
							currentJokerIndex = -1;
							if (j + 1 < cardsInHand - wildCount) {
								// straight ending because the next card is bad
								// so prepare to recognize a new straight
								currentStart = j + 1;
							}
						}
					}
					straightEnd = straightStart + longestStraight + 
								  skippedCount - 1;

					if (longestStraight >= 5) {
						straightFlag = true;
						if (jokerRank != -1) {
							// Insert the joker if it is being used to complete 
							// a straight.
							insertJoker(jokerRank, getLegalSuit(jokerRank), 
										jokerIndex);
						}
					}
					else
						jokerRank = -1;
				}
			}

		}


		if (wildCount == 1 && jokerRank == -1) {
			insertJoker(Ace, getLegalSuit(Ace), cardsInHand - 1); 
		}

		//
		// Count pairs, triples, quads.
		// Remember the rank of the high pair.
		//
		for (int sep = 3; sep > 0; --sep) {
			for (int i = 0; (i + sep) < rankCount; ++i) {
				if (ranks[i] == ranks[i + sep]) {
					tupleCounts[sep - 1] += 1;
					highPairRank = ranks[i];
				}
			}
		}

		if (jokerRank == Ace) {
			int end = cardsInHand - 1 - wildCount;
			int aceCount = 0;
			for (int k = end; k >= 0 && ranks[k] == 12; k--) 
				aceCount++;
			if (aceCount > 0) {
				for (int m = aceCount - 1; m >= 0; m--)
					tupleCounts[m]++;
				highPairRank = 12;
			}
			else
				highRank = 12;
		}
	}

	// Given a certain possible rank substitution for the Joker,
	// this function checks to see if the rank and majoritySuit
	// card is already in the hand.	 If so then it chooses a
	// suit to go with the rank that isn't already in the hand.
	private int getLegalSuit(int jrank)
	{
		boolean usedSuits[] = {false, false, false, false};
		for (int i = 0; i < cardsInHand; i++) {
			if (ranks[i] == jrank) {
			   for (int j = 0; j < suits.length; j++) {
				   if (suits[i] == j)
					   usedSuits[j] = true;
			   }
			}
		}

		for (int k = 0; k < usedSuits.length; k++) {
			if (usedSuits[k] != true)
				return k;
		}

		return 0;
	}

	// Put the card value the Joker is masquerading as in the correct
	// position in the rank, suit and cardIndex arrays. This means
	// that PaiGowApplet never sees a joker (except to display the
	// card), it only knows of the value of the card that the joker
	// is being used as.
	private void insertJoker(int jokerrank, int jokersuit, int subindex)
	{
		for (int i = cardsInHand - 1; i > subindex; i--) {
			ranks[i] = ranks[i - 1];
			suits[i] = suits[i - 1];
			cardIndex[i] = cardIndex[i - 1];
		}
		suits[subindex] = jokersuit;
		cardIndex[subindex] = jokerPos;
		ranks[subindex] = jokerrank;
		jokerRank = jokerrank;
	}

	/*
	private byte [] stringsToBytes(Vector sHand)
	{
		byte [] bHand = new byte[sHand.size()]; 

		for (int i = 0; i < sHand.size(); i++) {
			String s = (String) sHand.elementAt(i); 
			try {
				char c0 = s.charAt(0);
				char c1 = s.charAt(1);
				bHand[i] = card(getRank(c0), getSuit(c1));
			}
			catch (StringIndexOutOfBoundsException e) {}
		}

		return bHand;
	}

	private int getRank(char c)
	{
		int rank = 0;

		if (c >= '2' && c <= '9')
			rank = c - '0';
		else if (c == 'T')
			rank = 10;
		else if (c == 'J')
			rank = 11;
		else if (c == 'Q')
			rank = 12;
		else if (c == 'K')
			rank = 13;
		else if (c == 'A')
			rank = 14;
		else if (c == 'j')
			rank = 2;

		rank -= 2;
		return rank;
	}

	private int getSuit(char c)
	{
		int suit = 0;

		if (c == 'D')
			suit = Diamonds;
		else if (c == 'C')
			suit = Clubs;
		else if (c == 'H')
			suit = Hearts;
		else if (c == 'S')
			suit = Spades;
		else
			suit = JokerSuit;

		return suit;
	}

	private static byte card(int rank, int suit)
	{
		return (byte) (rank | (suit << 4));
	}
	*/

	private static boolean isWild(int rank)
	{
		return rank == Joker;
	}

	//
	// The queries.
	//

	public int[] getSortedRanks()
	{
		return ranks;
	}

	public int[] getSortedSuits()
	{
		return suits;
	}

	public int[] getCardIndex()
	{
		return cardIndex;
	}

	public byte[] getSortedBytes()
	{
		byte[] temp = new byte[ranks.length];
		for (int i = 0; i < temp.length; i++)
			temp[i] = (byte) ranks[i];
		return temp;
	}

	public int getWildCount()
	{
		return wildCount;
	}

	public int getAce()
	{
		if (isAce)
			return 1;
		return 0;
	}

	public int getLowRank()
	{
		return lowRank;
	}

	public int getHighRank()
	{
		if (jokerRank > highRank)
			return jokerRank;
		return highRank;
	}

	public int getSFStartIndex()
	{
		return sfStart;
	}

	public int getSFEndIndex()
	{
		return sfEnd;
	}

	public int getStraightStartIndex()
	{
		return straightStart;
	}

	public int getStraightEndIndex()
	{
		return straightEnd;
	}

	public int getStraightStartRank()
	{
		return ranks[straightStart];
	}

	public int getSFStartRank()
	{
		return ranks[sfStart];
	}

	public int getLongestSF()
	{
		return longestStraightFlush;
	}

	public int getLongestStraight()
	{
		return longestStraight;
	}

	public int getSkippedCardsCount()
	{
		return skippedCount;
	}

	public int[] getSkippedCards()
	{
		return skippedCards;
	}

	public int getHighPairRank()
	{
		return highPairRank;
	}

	public int getLowPairRank()
	{
		for (int i = 0; (i + 1) < cardsInHand; i++) {
			if (ranks[i] == ranks[i + 1] && ranks[i] != highPairRank) {
				return ranks[i];
			}
		}

		return -1;
	}

	public int getPairCount()
	{
		return tupleCounts[0];
	}

	public int getTripleCount()
	{
		return tupleCounts[1];
	}

	public int getQuadCount()
	{
		return tupleCounts[2];
	}

	public int getQuintCount()
	{
		return tupleCounts[3];
	}

	public int getTripleRank()
	{
		for (int i = 0; (i + 2) <= cardsInHand - 1; i++) {
			if (ranks[i] == ranks[i + 2] || (ranks[i] == Ace && 
				ranks[i + 2] == Joker)) {
				return ranks[i];
			}
			else if (ranks[i] == Joker && ranks[i + 2] == Ace) {
				return Ace;
			}
		}

		return -1;
	}

	public int getQuadRank()
	{
		for (int i = 0; (i + 3) <= cardsInHand - 1; i++) {
			if(ranks[i] == ranks[i + 3])
				return ranks[i];
		}

		return -1;
	}

	public int getSuitCount(int suitNum)
	{
		return suitCounts[suitNum];
	}

	public int getFlushSuit()
	{
		return majoritySuit;
	}

	public boolean isNoPair()
	{
		return getPairCount() == 0 && !straightFlag && !flushFlag && 
			   !straightFlushFlag;
	}

	public boolean isPair()
	{
		return getPairCount() == 1;
	}

	public boolean isTwoPair()
	{
		return getPairCount() == 2 && getTripleCount() == 0; 
	}

	public boolean isThreeOfAKind()
	{
	   return getTripleCount() == 1 && getQuadCount() == 0;
	}

	public boolean isAceLowStraight()
	{
		return aceLowStraightFlag;
	}

	public boolean isStraight()
	{
		return straightFlag;
	}

	public boolean isFlush()
	{
		return flushFlag;
	}

	public boolean isFullHouse()
	{
		return getTripleCount() == 1 && getPairCount() >= 3 && 
			   getQuadCount() == 0;
	}

	public boolean isFourOfAKind()
	{
		return getQuadCount() == 1;
	}

	public boolean isStraightFlush()
	{
		return straightFlushFlag;
	}

	public boolean isRoyalFlush()
	{
		return straightFlushFlag && getSFStartRank() == Ten; 
	}

	public boolean isAceLowSF()
	{
		return aceLowSFFlag;
	}

	public boolean isFiveAces()
	{
		return getQuintCount() == 1;
	}

	public static int cardValueToRank(byte cardValue)
	{
		if (cardValue == Joker)
			return Joker;
		return cardValue & 0xF;
	}

	public static int cardValueToSuit(byte cardValue)
	{
		if (cardValue == Joker)
			return JokerSuit;
		return cardValue >> 4;
	}

	/*
	public int calculateScore(int pattern)
	{
		int[] descend = getDescendingOrder(getSortedRanks());
		// The temp array is bigger than the rank array because the
		// zeroth slot holds the pattern that represents the type of
		// hand (e.g. two pair == 2).
		int[] temp = new int[descend.length + 1];
		System.arraycopy(descend, 0, temp, 1, descend.length);
		temp[0] = pattern;

		int exponent = 5; 
		for (int i = 0; i < temp.length; i++) {
			temp[i] <<= ((exponent - i) * 4);
		}

		int total = 0;
		for (int s = 0; s < temp.length; s++) {
			total |= temp[s];
		}

		return total;
	}
	*/

	private int[] getDescendingOrder(int[] ascendOrder)
	{
		int[] temp = new int[ascendOrder.length];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = ascendOrder[(temp.length - 1) - i];
		}

		return temp;
	}
}

