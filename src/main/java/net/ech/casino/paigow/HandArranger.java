//
// HandArranger.java
//

package net.ech.casino.paigow;

import java.util.*;
import net.ech.casino.Card;

/**
 * This class arranges a given hand into a high and low hand as
 * dictated by the particular house way.
 * 
 * @author Istina Mannino, imannino@pacificnet.net
 */
class HandArranger implements Constants
{
	private Vector hand;

	//
	// Constructor.
	//
	public HandArranger(byte[] bhand, SevenHandInfo sh)
	{
		// Arranges a particular hand according to the chosen House Way.
		//hand = PaiGowGame.bytesToVector(bhand);
		// System.out.println("hand: " + hand);
		int[] ranks = sh.getSortedRanks();
		/*
		for (int x = 0; x < ranks.length; x++)
			System.out.println("ranks[" + x + "]: " + ranks[x]);
		*/
		int[] cardIndex = sh.getCardIndex();
		int[] suits = sh.getSortedSuits();
		int lowHandPos = 5;
		int highCardIndex = CardsInHand - 1;

		if (sh.isFiveAces()) {
			// Always split and play two Aces in the low hand.
			// System.out.println("The player has five Aces.");
			
			// In the case of five Aces, the cards indexed with 0 and 1 
			// are automatically the non-Ace cards because they are
			// the lowest ranking cards in the hand.

			// If both non-Ace cards are in the low hand move them to
			// the high hand.
			if (cardIndex[0] >= lowHandPos && cardIndex[1] >= lowHandPos) {
				switchCards(hand, cardIndex, 0, highCardIndex);
				switchCards(hand, cardIndex, 1, highCardIndex - 1);
			}
			else {
				// If one of the non-Ace cards is in the low hand check
				// the highest ranking card to see if it is already in
				// the low hand, if it is then move the next highest
				// ranking card into the low hand, else move the highest
				// ranking card into the low hand.
				int temppos;
				if (cardIndex[highCardIndex] < lowHandPos)
					temppos = highCardIndex;
				else
					temppos = highCardIndex - 1;

				if (cardIndex[0] >= lowHandPos) {
					switchCards(hand, cardIndex, 0, temppos);
				}
				else if (cardIndex[1] >= lowHandPos) {
					switchCards(hand, cardIndex, 1, temppos);
				}
			}
		}
		else if (sh.isAceLowSF()) {
			// Conservative Way: Always play the straight flush in the high 
			// hand.  If player is holding extra straight cards, play the 
			// highest two other cards in the low hand.
			System.out.println("The player has a ace low straight flush.");
			int[] notsf = new int[2];
			int nsfindex = 0;
			int sfStart = sh.getSFStartIndex();
			int sfEnd = sh.getSFEndIndex();
			int lsf = sh.getLongestSF();

			// Put any skipped (non-straight flush) cards into the 
			// notsf array.
			int[] skippedIndexes = sh.getSkippedCards();
			int scCount = sh.getSkippedCardsCount();
			for (int sc = 0; sc < scCount; sc++) {
				notsf[nsfindex] = skippedIndexes[sc];
				nsfindex++;
			}

			// Put remaining non-straight flush cards into the notsf array.
			System.out.println("nsfindex: " + nsfindex);
			System.out.println("sfStart: " + sfStart + " sfEnd: " + sfEnd);
			for (int i = 0; i < highCardIndex; i++) {
				if (nsfindex < 2 && (i < sfStart || i > sfEnd)) { 
					notsf[nsfindex] = i;
					nsfindex++;
				}
			}

			if (lsf == CardsInHand - sh.getAce()) {
				// If the longest straight flush is equal to the number of
				// cards in the hand then put the highest straight flush
				// cards into the low hand.
				System.out.println("whole hand is a straight flush.");
				int tempindex1 = highCardIndex - sh.getAce();
				int tempindex2 = highCardIndex - 1 - sh.getAce();
				switchCards(hand, cardIndex, tempindex1,
							findCardIndex(cardIndex, lowHandPos + 1));
				switchCards(hand, cardIndex, tempindex2,
							findCardIndex(cardIndex, lowHandPos));
			}
			else if (lsf == CardsInHand - 1 - sh.getAce()) {
				// If longest straight flush is 6 cards then put the
				// highest straight flush card and the card that isn't
				// part of the straight flush into the low hand.
				System.out.println("All but one card is in straight flush.");
				int tempindex1 = highCardIndex - sh.getAce();
				int tempindex2 = notsf[0];
				if (tempindex1 == tempindex2)
					tempindex2--;
				switchCards(hand, cardIndex, tempindex1, 
							findCardIndex(cardIndex, lowHandPos + 1));
				switchCards(hand, cardIndex, tempindex2,
						   findCardIndex(cardIndex, lowHandPos));
			}
			else {
				// Move the non-straight flush cards into the low hand.
				switchCards(hand, cardIndex, notsf[0],
						   findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, notsf[1],
							findCardIndex(cardIndex, lowHandPos + 1));
			}

			// Arrange the cards in the high hand so that they are 
			// displayed in ascending order.
			int flushSuit = sh.getFlushSuit();
			int validHighIndex = highCardIndex;
			for (int i = highCardIndex; i > sfEnd; i--) {
				if (suits[validHighIndex] != flushSuit)
					validHighIndex = i;
				else
					break;
			}
			switchCards(hand, cardIndex, validHighIndex,
						findCardIndex(cardIndex, 0));
			for (int j = sfStart; j < validHighIndex; j++) {
				if (j != findCardIndex(cardIndex, lowHandPos) && 
					j != findCardIndex(cardIndex, lowHandPos + 1)) {
					for (int k = j+1; k < validHighIndex; k++) {
						if (k != notsf[0] && k != notsf[1]) {
							if (ranks[j] < ranks[k] && 
								cardIndex[j] > cardIndex[k]) {
								switchCards(hand, cardIndex, j, k);
							}
						}
					}
				}
			}
		}
		else if (sh.isStraightFlush()) {
			// Regardless of the way type, royal flushes are played in the
			// same manner as straight flushes. 
		
			// Conservative Way: Always play the flush in the high hand.  
			// If player is holding extra flush cards, play the highest 
			// two other cards in the low hand.
			// System.out.println("The player has a straight flush.");
			int[] notsf = new int[2];
			int nsfindex = 0;
			int sfStart = sh.getSFStartIndex();
			int sfEnd = sh.getSFEndIndex();
			int lsf = sh.getLongestSF();

			// Puts any skipped cards into the notsf array. Skipped cards
			// are ones that are in the middle of the straight flush but
			// aren't part of it.
			int[] skippedIndexes = sh.getSkippedCards();
			int scCount = sh.getSkippedCardsCount();
			if (scCount > 0) {
				for (int sc = 0; sc < scCount; sc++) {
					notsf[nsfindex] = skippedIndexes[sc];
					nsfindex++;
				}
			}

			// Find the remaining cards that aren't part of the straight
			// flush and put them in the notsf array.
			for (int i = 0; i <= highCardIndex; i++) {
				if (nsfindex < 2 && (i < sfStart || i > sfEnd)) { 
					notsf[nsfindex] = i;
					nsfindex++;
				}
			}

			if (lsf == CardsInHand) {
				// If the longest straight flush is equal to the number of
				// cards in the hand then put the highest straight flush
				// cards into the low hand.
				switchCards(hand, cardIndex, highCardIndex,
						   findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, highCardIndex - 1,
						   findCardIndex(cardIndex, lowHandPos + 1));
			}
			else if (lsf == CardsInHand - 1) {
				// If longest straight flush is 6 cards then put the
				// highest straight flush card and the card that isn't
				// part of the straight flush into the low hand.
				switchCards(hand, cardIndex, highCardIndex,
						   findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, notsf[0],
						   findCardIndex(cardIndex, lowHandPos + 1));
			}
			else {
				// Move the non-straight flush cards into the low hand.
				switchCards(hand, cardIndex, notsf[0],
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, notsf[1],
							findCardIndex(cardIndex, lowHandPos + 1));
			}

			// Arrange the cards in the high hand so that they are displayed
			// in ascending order.
			for (int j = sfStart; j < highCardIndex; j++) {
				if (j != notsf[0] && j != notsf[1]) {
					for (int k = j+1; k <= highCardIndex; k++) {
						if (k != notsf[0] && k != notsf[1]) {
							if (ranks[j] < ranks[k] && 
								cardIndex[j] > cardIndex[k] && 
								suits[j] == suits[k]) {
								switchCards(hand, cardIndex, j, k);
							}
						}
					}
				}
			}
		}
		else if (sh.isFourOfAKind()) {
			// Conservative Way: If the four of a kind is eights or less,
			// play them together in the five card hand. If the four of a
			// kind is nines or better split and play one pair in the low
			// hand and one in the high hand. If the four of a kind is
			// nines or better and accompanied by an Ace or any pair then
			// play them together in the high hand and the Ace or pair in
			// the low hand.
			// System.out.println("The player has four-of-a-kind.");
			int quadRank = sh.getQuadRank();

			// Find the position of the four-of-a-kind cards.
			int qpos1 = 0;
			int qpos2 = 0;
			int qpos3 = 0;
			int qpos4 = 0;
			for (int i = 0; (i + 3) <= highCardIndex; i++) {
				if(ranks[i] == ranks[i + 3] && ranks[i] == quadRank) {
					qpos1 = i;
					qpos2 = i + 1;
					qpos3 = i + 2;
					qpos4 = i + 3;
				}
			}

			int pairCount = sh.getPairCount();

			// Four of a kind is eights or less
			if (quadRank <= Eight) {
				// Move quad into the first four slots.
				for (int i = 0; i < 4; i++) {
					switchCards(hand, cardIndex, qpos1 + i, 
								findCardIndex(cardIndex, i));
				}

				// Find highest and second highest card that aren't
				// part of the quad.
				int highest;
				int secondhighest;
				if (ranks[qpos4] == ranks[highCardIndex]) {
					highest = qpos4 - 4;
					secondhighest = qpos4 - 5;
				}
				else if (ranks[qpos4] == ranks[highCardIndex - 1]) {
					highest = highCardIndex;
					secondhighest = qpos4 - 4;
				}
				else {
					highest = highCardIndex;
					secondhighest = highCardIndex - 1;
				}

				// Move highest and second highest cards into the low hand.
				switchCards(hand, cardIndex, highest, 
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, secondhighest,
							findCardIndex(cardIndex, lowHandPos + 1));
				
			}
			else {
				// Four of a kind is nines or better
				if (pairCount == 4 || (quadRank != Ace &&
					ranks[highCardIndex] == Ace)){
					// Nines or better and has either an Ace or an extra pair,
					// play four of a kind in high hand and Ace or pair in
					// the low hand.

					// Move quad into the first four slots.
					for (int i = 0; i < 4; i++) {
						switchCards(hand, cardIndex, qpos1 + i, 
									findCardIndex(cardIndex, i));
					}

					if (pairCount == 4) {
						// Four of a kind and a pair

						// Find pair positions
						int ppos1 = 0;
						int ppos2 = 0;
						for (int i = 0; i < highCardIndex; i++) {
							if (pairCount == 4 && ranks[i] == ranks[i+1]
									 && ranks[i] != quadRank) {
								ppos1 = i;
								ppos2 = i + 1;
							}
						}

						// Move pair into low hand.
						switchCards(hand, cardIndex, ppos1,
									findCardIndex(cardIndex, lowHandPos));
						switchCards(hand, cardIndex, ppos2,
									findCardIndex(cardIndex, lowHandPos + 1));
					}
					else if (ranks[highCardIndex] == Ace && 
							 ranks[highCardIndex] != quadRank) {
						// Four of a kind with an Ace that isn't part of
						// the quad

						// Find position of second highest card.
						int secondhighest = 0;
						for (int i = highCardIndex - 1; i >= 0; i--) {
							if (ranks[i] != quadRank) {
								secondhighest = i;
								break;
							}
						}

						// Move two highest non-quad cards into the low hand.
						switchCards(hand, cardIndex, highCardIndex,
									findCardIndex(cardIndex, lowHandPos + 1));
						switchCards(hand, cardIndex, secondhighest,
									findCardIndex(cardIndex, lowHandPos));
					}
				}
				else {
					// No Ace or extra pair so split and play one pair
					// in the low hand and one in the high hand
					switchCards(hand, cardIndex, qpos1,
								findCardIndex(cardIndex, 0));
					switchCards(hand, cardIndex, qpos2,
								findCardIndex(cardIndex, 1));

					switchCards(hand, cardIndex, qpos3,
								findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, qpos4,
								findCardIndex(cardIndex, lowHandPos + 1));
				}
			}
		}
		else if (sh.isFullHouse()) {
			// Always split and play the pair in the low hand.	When
			// holding two pairs and three of a kind, play highest pair
			// in low hand.
			// System.out.println("The player has a full house.");
			int tripleRank = sh.getTripleRank();

			// Find the position of the pair and triple cards.
			int tpos1 = 0;
			int tpos2 = 0;
			int tpos3 = 0;
			for (int i = 0; (i + 2) <= highCardIndex; i++) {
				if((ranks[i] == ranks[i + 2]) 
				   && ranks[i] == tripleRank) {
					tpos1 = i;
					tpos2 = i + 1;
					tpos3 = i + 2;
				}
			}
			int ppos1 = 0;
			int ppos2 = 0;
			for (int i = 0; (i + 1) <= highCardIndex; i++) {
				if (ranks[i] == ranks[i + 1] && ranks[i] != tripleRank) {
					ppos1 = i;
					ppos2 = i + 1;
				}
			}

			if (cardIndex[ppos2] >= lowHandPos && 
				cardIndex[ppos1] < lowHandPos) {
				// If one of the pair cards (pos2) is in the 
				// low hand and the other (pos1) is in the high hand 
				// then determine which slot pos2 is in and put pos1
				// into the other slot.
				int temppos;
				if (cardIndex[ppos2] == lowHandPos)
					temppos = lowHandPos + 1;
				else
					temppos = lowHandPos;
				switchCards(hand, cardIndex, ppos1,
							findCardIndex(cardIndex, temppos));
			}
			else {
				// If both pair cards are in the high hand then
				// swap them with the cards in the low hand.
				switchCards(hand, cardIndex, ppos1,
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, ppos2,
							findCardIndex(cardIndex, lowHandPos + 1));
			}
			// Put the triple cards together at the front of the high hand.
			switchCards(hand, cardIndex, tpos1, findCardIndex(cardIndex, 0));
			switchCards(hand, cardIndex, tpos2, findCardIndex(cardIndex, 1));
			switchCards(hand, cardIndex, tpos3, findCardIndex(cardIndex, 2));
		}
		else if (sh.isFlush()) {
			// Conservative Way: Always play the flush in the high hand 
			// except when holding two pairs with the flush that are both 
			// tens or better, then split and play low pair in the low hand.

			// System.out.println("The player has a flush.");
			if (sh.getPairCount() == 2 && (sh.getHighPairRank() >= Ten &&
				sh.getLowPairRank() >= Ten)) {
				// Find the position of the low pair cards.
				int lppos1 = 0;
				int lppos2 = 0;
				int hppos1 = 0;
				int hppos2 = 0;
				for (int i = 0; i < highCardIndex; i++) {
					if (ranks[i] == ranks[i + 1]) { 
						if (ranks[i] == sh.getLowPairRank()) {
							lppos1 = i;
							lppos2 = i + 1;
						}
						else {
							hppos1 = i;
							hppos2 = i + 1;
						}
					}
				}

				if (cardIndex[lppos2] >= lowHandPos && 
					cardIndex[lppos1] < lowHandPos) {
					// If one of the lowest pair cards (lppos2) is in the 
					// low hand and the other (lppos1) is in the high hand 
					// then determine which slot lppos2 is in and put lppos1
					// into the other slot.
					int temppos;
					if (cardIndex[lppos2] == lowHandPos)
						temppos = lowHandPos + 1;
					else
						temppos = lowHandPos;
					switchCards(hand, cardIndex, lppos1,
								findCardIndex(cardIndex, temppos));
				}
				else {
					// If both lowest pair cards are in the high hand then
					// swap them with the cards in the low hand.
					switchCards(hand, cardIndex, lppos1, 
								findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, lppos2,
								findCardIndex(cardIndex, lowHandPos + 1));
				}

				// Put the high pair cards together at the front of the 
				// high hand.
				switchCards(hand, cardIndex, hppos1, 
							findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, hppos2, 
							findCardIndex(cardIndex, 1));
			}
			else {
				// Find the positions of the non-flush cards.
				int flushSuit = sh.getFlushSuit();
				int[] notflush = new int[2];
				int nfindex = 0;
				for (int i = 0; i <= highCardIndex; i++) {
					if (suits[i] != flushSuit) {
						notflush[nfindex] = i;
						nfindex++;
					}
				}

				// Move the non-flush cards into the low hand.
				switchCards(hand, cardIndex, notflush[0],
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, notflush[1],
							findCardIndex(cardIndex, lowHandPos + 1));
			}
		}
		else if (sh.isAceLowStraight()) {
			// Conservative Way: Always play the straight in the high hand.	 
			// If player is holding extra straight cards, play the highest 
			// two other cards in the low hand.	 There is no way for the
			// Ace low straight to have two pairs greater than ten so
			// the usual straight rules for this case don't apply.
			// System.out.println("The player has a ace low straight.");
			int[] notstraight = new int[2];
			int nsindex = 0;
			int sStart = sh.getStraightStartIndex();
			int sEnd = sh.getStraightEndIndex();
			int ls = sh.getLongestStraight();

			// Put any skipped (non-straight) cards into the 
			// notstraight array.
			int[] skippedIndexes = sh.getSkippedCards();
			int scCount = sh.getSkippedCardsCount();
			for (int sc = 0; sc < scCount; sc++) {
				notstraight[nsindex] = skippedIndexes[sc];
				nsindex++;
			}

			// Put remaining non-straight cards into the notstraight array.
			for (int i = 0; i < highCardIndex; i++) {
				if (nsindex < 2 && (i < sStart || i > sEnd)) { 
					notstraight[nsindex] = i;
					nsindex++;
				}
			}

			if (ls == CardsInHand - sh.getAce()) {
				// If the longest straight is equal to the number of
				// cards in the hand then put the highest straight 
				// cards into the low hand.
				// System.out.println("whole hand is a straight.");
				int tempindex1 = highCardIndex - sh.getAce();
				int tempindex2 = highCardIndex - 1 - sh.getAce();
				switchCards(hand, cardIndex, tempindex1,
							findCardIndex(cardIndex, lowHandPos + 1));
				switchCards(hand, cardIndex, tempindex2,
							findCardIndex(cardIndex, lowHandPos));
			}
			else if (ls == CardsInHand - 1 - sh.getAce()) {
				// System.out.println("All but one card is in the straight.");
				// If longest straight is 6 cards then put the
				// highest straight card and the card that isn't
				// part of the straight into the low hand.
				int tempindex1 = highCardIndex - sh.getAce();
				int tempindex2 = notstraight[0];
				if (tempindex1 == tempindex2)
					tempindex2--;
				switchCards(hand, cardIndex, tempindex1, 
							findCardIndex(cardIndex, lowHandPos + 1));
				switchCards(hand, cardIndex, tempindex2,
						   findCardIndex(cardIndex, lowHandPos));
			}
			else {
				// Move the non-straight cards into the low hand.
				// System.out.println("Only have a 5 card straight.");
				switchCards(hand, cardIndex, notstraight[0],
						   findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, notstraight[1],
							findCardIndex(cardIndex, lowHandPos + 1));
			}

			// Arrange the cards in the high hand so that they are 
			// displayed in ascending order.
			switchCards(hand, cardIndex, highCardIndex,
						findCardIndex(cardIndex, 0));
			for (int j = sStart; j < highCardIndex; j++) {
				if (j != findCardIndex(cardIndex, lowHandPos) && 
					j != findCardIndex(cardIndex, lowHandPos + 1)) {
					for (int k = j+1; k < highCardIndex; k++) {
						if (k != notstraight[0] && k != notstraight[1]) {
							if (ranks[j] < ranks[k] && 
								cardIndex[j] > cardIndex[k]) {
								switchCards(hand, cardIndex, j, k);
							}
						}
					}
				}
			}
		}
		else if (sh.isStraight()) {
			// Conservative Way: Always play the straight in the high hand.	 
			// If player is holding extra straight cards, play the highest 
			// two other cards in the low hand. When holding two pairs with the
			// straight, keep the straight in the high hand unless both
			// pairs are tens or better, then split and play the low
			// pair in the low hand.
			// System.out.println("The player has a straight.");
			if (sh.getPairCount() == 2 && (sh.getHighPairRank() >= Ten &&
				sh.getLowPairRank() >= Ten)) {
				// Find the positions of the pair cards.
				int lppos1 = 0;
				int lppos2 = 0;
				int hppos1 = 0;
				int hppos2 = 0;
				for (int i = 0; i < highCardIndex; i++) {
					if (ranks[i] == ranks[i + 1]) { 
						if (ranks[i] == sh.getLowPairRank()) {
							lppos1 = i;
							lppos2 = i + 1;
						}
						else {
							hppos1 = i;
							hppos2 = i + 1;
						}
					}
				}

				if (cardIndex[lppos2] >= lowHandPos && 
					cardIndex[lppos1] < lowHandPos) {
					// If one of the lowest pair cards (lppos2) is in the 
					// low hand and the other (lppos1) is in the high hand 
					// then determine which slot lppos2 is in and put lppos1
					// into the other slot. (Please Note: If lppos1 is in the 
					// low hand then the next pair card, lppos2 must also be 
					// in the low hand so they don't need to be moved.)
					int temppos;
					if (cardIndex[lppos2] == lowHandPos)
						temppos = lowHandPos + 1;
					else
						temppos = lowHandPos;
					switchCards(hand, cardIndex, lppos1,
								findCardIndex(cardIndex, temppos));
				}
				else {
					// If both lowest pair cards are in the high hand then
					// swap them with the cards in the low hand.
					switchCards(hand, cardIndex, lppos1, 
								findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, lppos2,
								findCardIndex(cardIndex, lowHandPos + 1));
				}

				// Put the high pair cards together at the front of the 
				// high hand.
				switchCards(hand, cardIndex, hppos1, 
							findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, hppos2, 
							findCardIndex(cardIndex, 1));
			}
			else {
				int[] notstraight = new int[2];
				int nsindex = 0;
				int sStart = sh.getStraightStartIndex();
				int sEnd = sh.getStraightEndIndex();
				int ls = sh.getLongestStraight();

				// Put any skipped (non-straight) cards into the 
				// notstraight array.
				int[] skippedIndexes = sh.getSkippedCards();
				int scCount = sh.getSkippedCardsCount();
				if (scCount > 0) {
					for (int sc = 0; sc < scCount; sc++) {
						notstraight[nsindex] = skippedIndexes[sc];
						nsindex++;
					}
				}

				// Put remaining non-straight cards into the notstraight array.
				for (int i = 0; i <= highCardIndex; i++) {
					if (nsindex < 2 && (i < sStart || i > sEnd)) { 
						notstraight[nsindex] = i;
						nsindex++;
					}
				}

				if (ls == CardsInHand) {
					// If the longest straight is equal to the number of
					// cards in the hand then put the highest straight 
					// cards into the low hand.
					switchCards(hand, cardIndex, highCardIndex,
							   findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, highCardIndex - 1,
							   findCardIndex(cardIndex, lowHandPos + 1));
				}
				else if (ls == CardsInHand - 1) {
					// If longest straight is 6 cards then put the
					// highest straight card and the card that isn't
					// part of the straight into the low hand.
					switchCards(hand, cardIndex, highCardIndex,
							   findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, notstraight[0],
							   findCardIndex(cardIndex, lowHandPos + 1));
				}
				else {
					// Move the non-straight cards into the low hand.
					switchCards(hand, cardIndex, notstraight[0],
							   findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, notstraight[1],
								findCardIndex(cardIndex, lowHandPos + 1));
				}

				// Arrange the cards in the high hand so that they are 
				// displayed in ascending order.
				for (int j = sStart; j < highCardIndex; j++) {
					if (j != notstraight[0] && j != notstraight[1]) {
						for (int k = j+1; k <= highCardIndex; k++) {
							if (k != notstraight[0] && k != notstraight[1]) {
								if (ranks[j] < ranks[k] && 
									cardIndex[j] > cardIndex[k]) {
									switchCards(hand, cardIndex, j, k);
								}
							}
						}
					}
				}
			}
		}
		else if (sh.isTwoThreeOfAKind()) {
			// Always play the pair of the highest three-of-a-kind in
			// the low hand.
			// System.out.println("The player has two three of a kind.");

			// Find the position of the high pair cards.
			int highTriple = sh.getHighPairRank();
			int hppos2 = 0;
			int hppos3 = 0;
			int lppos1 = 0;
			int lppos2 = 0;
			int lppos3 = 0;
			for (int i = 0; (i + 2) <= highCardIndex; i++) {
				if (ranks[i] == ranks[i + 2]) { 
					if (ranks[i] == highTriple) {
						hppos2 = i + 1;
						hppos3 = i + 2;
					}
					else {
						lppos1 = i;
						lppos2 = i + 1;
						lppos3 = i + 2;
					}
				}
			}

			if (cardIndex[hppos3] >= lowHandPos && 
				cardIndex[hppos2] < lowHandPos) {
				// If one of the highest triple cards (hppos3) is in the 
				// low hand and the other (hppos2) is in the high hand 
				// then determine which slot hppos3 is in and put hppos2
				// into the other slot.
				int temppos;
				if (cardIndex[hppos3] == lowHandPos)
					temppos = lowHandPos + 1;
				else
					temppos = lowHandPos;
				switchCards(hand, cardIndex, hppos2,
							findCardIndex(cardIndex, temppos));
			}
			else {
				// If all the highest triple cards are in the high hand then
				// swap two of them with the cards in the low hand.
				switchCards(hand, cardIndex, hppos2, 
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, hppos3,
							findCardIndex(cardIndex, lowHandPos + 1));
			}

			// Put the low triple cards together at the front of the high hand.
			switchCards(hand, cardIndex, lppos1, findCardIndex(cardIndex, 0));
			switchCards(hand, cardIndex, lppos2, findCardIndex(cardIndex, 1));
			switchCards(hand, cardIndex, lppos3, findCardIndex(cardIndex, 2));
		}
		else if (sh.isThreeOfAKind()) {
			// System.out.println("Player has three-of-a-kind.");
			// Always play three-of-a-kind in high hand except
			// if it's three aces then put one of the aces into
			// the low hand and the pair in the high hand.

			// Find the position of the three of a kind cards.
			int pos1 = 0;
			int pos2 = 0;
			int pos3 = 0;
			for (int i = 0; (i + 2) <= highCardIndex; ++i) {
				if (ranks[i] == ranks[i + 2]) {
					pos1 = i;
					pos2 = i + 1;
					pos3 = i + 2;
				}
			}

			// Find highest and second highest card that aren't
			// part of the three of a kind.
			int highest;
			int secondhighest;
			if (ranks[pos3] == ranks[highCardIndex]) {
				highest = pos3 - 3;
				secondhighest = pos3 - 4;
			}
			else if (ranks[pos3] == ranks[highCardIndex - 1]) {
				highest = highCardIndex;
				secondhighest = pos3 - 3;
			}
			else {
				highest = highCardIndex;
				secondhighest = highCardIndex - 1;
			}

			if (ranks[pos1] == Ace) {
				// If the three of a kind is Aces.
				int secondPairCard;
				if (cardIndex[pos2] >= lowHandPos) {
					// If pos2 is in the low hand then so is the pos3
					// card so leave the pos2 card in slot 5 and put
					// the highest card into slot 6.
					switchCards(hand, cardIndex, highest,
								findCardIndex(cardIndex, lowHandPos + 1));
					secondPairCard = pos3;	

				}
				else if (cardIndex[pos3] >= lowHandPos) {
					// If pos3 is in low hand then leave it in the low hand. 
					// Then swap the highest card with the one in the
					// low hand that isn't the pos3 card.
					int temppos;
					if (cardIndex[pos3] == lowHandPos)
						temppos = lowHandPos + 1;
					else 
						temppos = lowHandPos;
					switchCards(hand, cardIndex, highest,
								findCardIndex(cardIndex, temppos));
					secondPairCard = pos2;	
				}
				else {
					// In this case all of the triple cards are in the high
					// so switch the pos3 card with the one in the 5
					// card position and the highest with the one in the
					// 6 card position.
					switchCards(hand, cardIndex, pos3, 
								findCardIndex(cardIndex, lowHandPos));
					switchCards(hand, cardIndex, highest,
								findCardIndex(cardIndex, lowHandPos + 1));
					secondPairCard = pos2;	
				}
				// Put the remaining pair cards together at the front 
				// of the high hand.
				switchCards(hand, cardIndex, pos1, 
							findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, secondPairCard, 
								findCardIndex(cardIndex, 1));
			}
			else {
				if (cardIndex[pos2] >= lowHandPos) {
					// The only way pos2 can be in the low hand is if pos3
					// is also in the low hand because the sort in PGHandInfo
					// preserves the order of the triple cards. Since they are
					// both in the low hand just switch directly with the
					// highest and second highest card in the high hand.
					switchCards(hand, cardIndex, pos2, highest); 
					switchCards(hand, cardIndex, pos3, secondhighest);
				}
				else if (cardIndex[pos3] >= lowHandPos) {
					// If pos3 is in low hand then switch pos3 card with the 
					// highest card. This works even if highest is also in
					// the low hand (after the first switch pos3 is still
					// in the low hand) because the second switch swaps the
					// second highest card with whatever card is in the
					// sixth card position.
					switchCards(hand, cardIndex, pos3, highest); 
					int temppos;
					if (cardIndex[highest] == lowHandPos)
						temppos = lowHandPos + 1;
					else 
						temppos = lowHandPos;
					switchCards(hand, cardIndex, secondhighest,
								findCardIndex(cardIndex, temppos));
				}
				else {
					// In this case all of the triple cards are in the high
					// so simply switch the highest and second highest
					// cards to the low hand.
					switchCards(hand, cardIndex, secondhighest,
								findCardIndex(cardIndex, lowHandPos + 1));
					switchCards(hand, cardIndex, highest, 
								findCardIndex(cardIndex, lowHandPos));
				}

				// Put the triple cards together at the front of the high hand.
				switchCards(hand, cardIndex, pos1, findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, pos2, findCardIndex(cardIndex, 1));
				switchCards(hand, cardIndex, pos3, findCardIndex(cardIndex, 2));
			}
		}
		else if (sh.isThreePairs()) {
			// Always play the highest pair in the low hand.
			// System.out.println("The player has three pairs.");

			// Find the position of the pair cards.
			int highPair = sh.getHighPairRank();
			int lowPair = sh.getLowPairRank();
			int hppos1 = 0;
			int hppos2 = 0;
			int mppos1 = 0;
			int mppos2 = 0;
			int lppos1 = 0;
			int lppos2 = 0;
			for (int i = 0; i < highCardIndex; i++) {
				if (ranks[i] == ranks[i + 1]) {
					if (ranks[i] == highPair) {
						hppos1 = i;
						hppos2 = i + 1;
					}
					else if (ranks[i] == lowPair) {
						lppos1 = i;
						lppos2 = i + 1;
					}
					else {
						mppos1 = i;
						mppos2 = i + 1;
					}
				}
			}

			if (cardIndex[hppos2] >= lowHandPos && 
				cardIndex[hppos1] < lowHandPos) {
				// If one of the highest pair cards (hppos2) is in the 
				// low hand and the other (hppos1) is in the high hand 
				// then determine which slot hppos2 is in and put hppos1
				// into the other slot.
				int temppos;
				if (cardIndex[hppos2] == lowHandPos)
					temppos = lowHandPos + 1;
				else
					temppos = lowHandPos;
				switchCards(hand, cardIndex, hppos1,
							findCardIndex(cardIndex, temppos));
			}
			else {
				// If both highest pair cards are in the high hand then
				// swap them with the cards in the low hand.
				switchCards(hand, cardIndex, hppos1, 
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, hppos2,
							findCardIndex(cardIndex, lowHandPos + 1));
			}

			// Move remaining pair cards so that they will be displayed together
			switchCards(hand, cardIndex, mppos1, findCardIndex(cardIndex, 0)); 
			switchCards(hand, cardIndex, mppos2, findCardIndex(cardIndex, 1)); 
			switchCards(hand, cardIndex, lppos1, findCardIndex(cardIndex, 2)); 
			switchCards(hand, cardIndex, lppos2, findCardIndex(cardIndex, 3)); 
		}
		else if (sh.isTwoPair()) {
			// Conservative Way: Keep the two pairs in the high hand if
			// the pairs are both fives or less, otherwise split and
			// play the lower pair in the low hand. If both pairs are 
			// sevens or less or the high pair is tens or better and
			// the low pair is greater than six and they are accompanied 
			// by an Ace, play the Ace in the low hand. All other combinations 
			// split the pairs and play the lowest in the low hand.

			// System.out.println("Player has two pair.");
			int hpr = sh.getHighPairRank();
			int lpr = sh.getLowPairRank();

			// Find the position of the pair cards.
			int hpos1 = 0;
			int hpos2 = 0;
			int lpos1 = 0;
			int lpos2 = 0;
			for (int i = 0; i < highCardIndex; i++) {
				if (ranks[i] == ranks[i + 1]) {
					if (ranks[i] == hpr) {
						hpos1 = i;
						hpos2 = i + 1;
					}
					else {
						lpos1 = i;
						lpos2 = i + 1;
					}
				}
			}

			if ((hpr <= Five && lpr <= Five) || (((hpr <= Seven && lpr <= Seven)
				 || (hpr >= Ten && lpr > Six)) && ranks[highCardIndex] == Ace
				 && hpr != Ace)) {
				// (1) Both pairs are fives or less, (2) both pairs are seven
				// or less, (3) high pair is tens or better and low pair is
				// six or less AND for all three cases there is an Ace or a
				// Joker which is not included in the pairs.

				// Move both sets of pairs to the first four slots.
				switchCards(hand, cardIndex, hpos1,
							findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, hpos2,
							findCardIndex(cardIndex, 1));
				switchCards(hand, cardIndex, lpos1,
							findCardIndex(cardIndex, 2));
				switchCards(hand, cardIndex, lpos2,
							findCardIndex(cardIndex, 3));

				// Find highest and second highest card that aren't
				// part of the pairs.
				int highest;
				int secondhighest;
				if (hpr == ranks[highCardIndex]) {
					if (lpr != ranks[hpos2 - 3]) {
						highest = hpos2 - 2;
						secondhighest = hpos2 - 3;
					}
					else {
						highest = hpos2 - 4;
						secondhighest = hpos2 - 5;
					}
				}
				else if (hpr == ranks[highCardIndex - 1]) {
					highest = highCardIndex;
					if (lpr != ranks[hpos2 - 2])
						secondhighest = hpos2 - 2;
					else
						secondhighest = hpos2 - 4;
				}
				else {
					highest = highCardIndex;
					secondhighest = highCardIndex - 1;
				}

				// Move highest and second highest cards to low hand.
				switchCards(hand, cardIndex, highest,
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, secondhighest,
							findCardIndex(cardIndex, lowHandPos + 1));
			}
			else {
				// All other combinations.

				// Move the lower pair to the low hand.
				switchCards(hand, cardIndex, lpos1, 
							findCardIndex(cardIndex, lowHandPos));
				switchCards(hand, cardIndex, lpos2, 
							findCardIndex(cardIndex, lowHandPos + 1));

				// Arrange the high hand so that the high pair cards are
				// displayed next to each other.
				switchCards(hand, cardIndex, hpos1, 
							findCardIndex(cardIndex, 0));
				switchCards(hand, cardIndex, hpos2, 
							findCardIndex(cardIndex, 1));
			}
		}
		else if (sh.isPair()) {
			// Always play the pair in the high hand and the highest
			// ranking cards that aren't in the pair for the low hand.

			// System.out.println("Player has a pair.");
			// Find the position of the pair cards.
			int pos1 = 0;
			int pos2 = 0;
			for (int i = 0; i < highCardIndex; i++) {
				if (ranks[i] == ranks[i + 1]) {
					pos1 = i;
					pos2 = i + 1;
				}
			}

			// Find highest and second highest card that aren't
			// part of the pair.
			int highest;
			int secondhighest;
			if (ranks[pos2] == ranks[highCardIndex]) {
				highest = pos2 - 2;
				secondhighest = pos2 - 3;
			}
			else if (ranks[pos2] == ranks[highCardIndex - 1]) {
				highest = highCardIndex;
				secondhighest = pos2 - 2;
			}
			else {
				highest = highCardIndex;
				secondhighest = highCardIndex - 1;
			}

			if (cardIndex[pos1] >= lowHandPos) {
				// The only way pos1 can be in the low hand is if pos2
				// is also in the low hand because the sort in PGHandInfo
				// preserves the order of the pair cards. Since they are
				// both in the low hand just switch directly with the
				// highest and second highest card in the high hand.
				switchCards(hand, cardIndex, pos1, highest); 
				switchCards(hand, cardIndex, pos2, secondhighest);
			}
			else if (cardIndex[pos2] >= lowHandPos) {
				// If pos2 is in low hand then switch pos2 card with the 
				// highest card. This works even if highest is also in
				// the low hand (after the first switch pos2 is still
				// in the low hand) because the second switch swaps the
				// second highest card with whatever card is in the
				// sixth card position.
				switchCards(hand, cardIndex, pos2, highest); 
				int temppos;
				if (cardIndex[highest] == lowHandPos)
					temppos = lowHandPos + 1;
				else 
					temppos = lowHandPos;
				switchCards(hand, cardIndex, secondhighest,
							findCardIndex(cardIndex, temppos));
			}
			else {
				// In this case both pos1 and pos2 are in the high
				// so simply switch the highest and second highest
				// cards to the low hand.
				switchCards(hand, cardIndex, secondhighest,
							findCardIndex(cardIndex, lowHandPos + 1));
				switchCards(hand, cardIndex, highest, 
							findCardIndex(cardIndex, lowHandPos));
			}

			// Put the pair cards together at the front of the high hand.
			switchCards(hand, cardIndex, pos1, findCardIndex(cardIndex, 0));
			switchCards(hand, cardIndex, pos2, findCardIndex(cardIndex, 1));
		}
		else if (sh.isNoPair()) {
			// Always play second and third highest ranked cards in the 
			// low hand.
			// System.out.println("The player has no pair.");
			switchCards(hand, cardIndex, highCardIndex - 2, 
						findCardIndex(cardIndex, lowHandPos + 1));
			switchCards(hand, cardIndex, highCardIndex - 1, 
						findCardIndex(cardIndex, lowHandPos));
		}
	}

	// switchCards is passed the index into cardIndex that holds the
	// card's actual position in the hand vector.
	private void switchCards(Vector hand, int[] ci, int index1, int index2)
	{
		String ranktemp = (String) hand.elementAt(ci[index2]);
		int indextemp = ci[index2];
		hand.setElementAt(hand.elementAt(ci[index1]), ci[index2]);
		ci[index2] = ci[index1];
		hand.setElementAt(ranktemp, ci[index1]);
		ci[index1] = indextemp;
	}

	// Given the position of a card, this function returns the index
	// into the ranks, suits and cardIndex arrays, which is what the
	// switchCards function requires.
	private int findCardIndex(int[] ci, int cardpos)
	{
		for (int i = 0; i < CardsInHand; i++) {
			if (ci[i] == cardpos)
				return i;
		}
		return -1;
	}

	public byte[] getArrangedHand()
	{
		return null;
		/***
		byte [] bHand = new byte[hand.size()]; 

		for (int i = 0; i < hand.size(); i++) {
			String s = (String) hand.elementAt(i); 
			try {
				char c0 = s.charAt(0);
				char c1 = s.charAt(1);
				bHand[i] = Card.value (PaiGowGame.getRank(c0),
										PaiGowGameCard.getSuit(c1));
			}
			catch (StringIndexOutOfBoundsException e) {}
		}

		return bHand;
		***/
	}
}
