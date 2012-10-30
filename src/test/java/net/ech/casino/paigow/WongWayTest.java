//
// WongWayTest.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test program for class WongWay.
 *
 * @see net.ech.casino.paigow.WongWay
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class WongWayTest implements Constants
{
	WongWay bankersWay = new WongWay (true);
	WongWay playersWay = new WongWay (false);

	@Test
	public void testStrategy3() throws Exception
	{
        testWongWay("8CAD6HTH9C9D8H",       "9?9?8?8?6?A?T?");
        testWongWay("ADACKDKCQDQC4H",       "K?K?Q?Q?4?A?A?");
        testWongWay("JDJC2D2C7H3Cjo",       "J?J?3?2?2?jo7?");
        testWongWay("7D7C6D6C9H8S2S",       "9?8?7?7?2?6?6?");
	}

	@Test
	public void testStrategy5() throws Exception
	{
        testWongWay("AHACADKHQC7D3H",       "A?A?Q?7?3?A?K?");
        testWongWay("KHKDKC9H7D3S2C",       "K?K?7?3?2?K?9?");
        testWongWay("KHKDKSQH9H6S5D",       "K?K?K?6?5?Q?9?");
        testWongWay("QHQDQS5C4C3S2D",       "Q?Q?Q?3?2?5?4?");
	}

	@Test
	public void testStrategy8() throws Exception
	{
        testWongWay("3S3DJDTH9C8S7S",       "J?T?9?8?7?3?3?");
        testWongWay("8D8CJD7C6S5H4S",       "8?7?6?5?4?J?8?");
        testWongWay("6H6CAC5H4S3S2D",       "5?4?3?2?A?6?6?");
        testWongWay("JHJCKDQDTD8S9S",       "Q?J?T?9?8?K?J?");
        testWongWay("JHJCACKSQHTH4H",       "Q?J?J?T?4?A?K?");
        testWongWay("AHADKDQSJSTS3D",       "A?K?Q?J?T?A?3?");
        testWongWay("KSKHADQDJSTH5C",       "A?K?Q?J?T?K?5?", "K?K?J?T?5?A?Q?");
	}

	@Test
	public void testStrategy9() throws Exception
	{
        testWongWay("QCQSASKSTD6S3S",       "Q?Q?T?6?3?A?K?");
        testWongWay("9C9SASKSQS8H3S",       "Q?9?9?8?3?A?K?");
        testWongWay("7C7SASKSQHJS6S",       "ASKSJS7S6SQ?7?");
        testWongWay("QCQSKSJSTS4S2C",       "KSQSJSTS4SQ?2?");
	}

	@Test
	public void testStrategy9FaultCase() throws Exception
	{
        testWongWay("6C6SKSASTS4S2C",       "??????????????");       // just don't throw
        testWongWay("ACTH4H2HAH9H9C",       "??????????????");
        testWongWay("ACTH4H2Hjo9H9C",       "??????????????");
	}

	@Test
	public void testStrategy11() throws Exception
	{
        testWongWay("8S8CjoKS7S5S2S",       "joKS7S5S2S8?8?");
        testWongWay("joASKSQS6C3S2S",       "??KSQS3S2S??6?");   // ace and joker are equiv
        testWongWay("joTSKSQS6C3S2S",       "KSQSTS3S2Sjo6?");
        testWongWay("joASKS7H6H5S2S",       "joA?6?5?2?K?7?");
        testWongWay("joASKCQS7S3S2C",       "joASQS7S3SK?2?");
        testWongWay("joASQSTC7S3S2C",       "joASQS7S3ST?2?");
        testWongWay("joASQSTS6S3C2C",       "joA?6?3?2?Q?T?");
        testWongWay("joASQS9S6S3C2C",       "joASQS9S6S3?2?");
        testWongWay("joASJS9S6S3C2C",       "joASJS9S6S3?2?");
	}

	@Test
	public void testStrategy13() throws Exception
	{
        testWongWay("TC9H8H7C6C5C4C",       "TC7C6C5C4C9?8?");
        testWongWay("JCTH9H8H7C6C5C",       "9?8?7?6?5?J?T?");
        testWongWay("joAS9C7C5C4C3S",       "jo9C7C5C4CA?3?");
        testWongWay("joASJC7C5C4C3S",       "joJC7C5C4CA?3?", "7?jo5?4?3?A?J?");
        testWongWay("QS8C6C5C4C3C2S",       "8C6C5C4C3CQ?2?");
        testWongWay("ACQS9C5C4C3C2S",       "5?4?3?2?A?Q?9?");
	}

	@Test
	public void testStrategy14() throws Exception
	{
        testWongWay("8C8HAH5C4C3C2C",       "5?4?3?2?A?8?8?");
        testWongWay("ASACKC5C4C3C2S",       "5?4?3?2?A?A?K?");
        testWongWay("6S6C7C5C4C3S2C",       "7C6C5C4C2C6?3?");
        testWongWay("QSQCACKCJSTS2C",       "Q?Q?J?T?2?A?K?");
	}

	@Test
	public void testStrategy15() throws Exception
	{
        testWongWay("3H3D2S2C6H5D4C",       "6?5?4?3?2?3?2?");
        testWongWay("6C6H5D5S4H3C2D",       "6?6?4?3?2?5?5?");
	}

	@Test
	public void testStrategy16() throws Exception
	{
        testWongWay("4D4S3S3CKS8S7S",       "7?4?4?3?3?K?8?");
        testWongWay("4H4S2S2C9S6S5S",       "9S6S5S4S2S4?2?");
        testWongWay("ASAH9S7S4S2S2C",       "AS9S7S4S2SA?2?");
	}

	@Test
	public void testStrategyStraightFlush() throws Exception
	{
        testWongWay("2H5HQS4HKDAH3H",       "5H4H3H2HAHK?Q?");
	}

	@Test
	public void testNoFaultInRegressionCase() throws Exception
	{
        testWongWay("TC9S9C8C7CAHJC",       "JCTC9C8C7CAH9S");
        testWongWay("8C8S9C5H5D9HJC",       "J?8?8?5?5?9?9?");
        testWongWay("8C8S9C5H5D9H7C",       "8?8?7?5?5?9?9?");
        testWongWay("5D9H8C7C8S9C5H",       "8?8?7?5?5?9?9?");
        testWongWay("9H9D8C8D7C5S5D",       "8?8?7?5?5?9?9?");
	}

	@Test
	public void testPracticeHands() throws Exception
	{
        testWongWay("ADACASTS6C5H4D",       "A?A?6C5H4DA?T?");
        testWongWay("THTD7H7DKCJC2C",       "K?J?T?T?2?7?7?");
        testWongWay("KHKC3S3C8C5D2H",       "K?K?8?5?2?3?3?");
        testWongWay("ADKC9D8D6D5D3H",       "AD9D8D6D5DKC3H");
        testWongWay("9D9C2D2CjoJS4S",       "9?9?4?2?2?joJ?");
        testWongWay("ACASKCTD9C5C3C",       "ACKC9C5C3CA?T?");
        testWongWay("joTC8S7D6H5C4S",       "8?7?6?5?4?joT?");
        testWongWay("joACADKS9S7S2S",       "joKS9S7S2SA?A?");
        testWongWay("7D7CQDTC9S8C6H",       "T?9?8?7?6?Q?7?");
        testWongWay("ACAH5C5HKSJS3S",       "A?A?K?J?3?5?5?");
        testWongWay("joAH8H8C7C7SQS",       "Q?8?8?7?7?joA?");
        testWongWay("joJHTH9D7D5S2S",       "J?T?9?jo7?5?2?");
        testWongWay("ACASQCQS8H8DTD",       "Q?Q?T?8?8?A?A?");
        testWongWay("9C9H9S9D7D7CKH",       "K?9?9?9?9?7?7?");
        testWongWay("ACKSQHTD8C6S2H",       "A?T?8?6?2?K?Q?");
        testWongWay("joASKSTS7S3C2D",       "joA?7?3?2?K?T?");
        testWongWay("8S8C8D5S5DQD3H",       "Q?8?8?8?3?5?5?");    // page 128
        testWongWay("joQH7S5D4C3H2D",       "5?4?3?2?joQ?7?");
        testWongWay("8S8C7C7HKCQH3D",       "K?Q?8?8?3?7?7?", "8?8?7?7?3?K?Q?");
        testWongWay("5S5C4C4Djo3S2D",       "5?5?4?4?2?jo3?");
        testWongWay("joAC8D8S3S3D2D",       "8?8?3?3?2?joA?");
        testWongWay("KSKD6D6HAHQDTS",       "A?K?K?Q?T?6?6?");
        testWongWay("joAS2S2HQH5D4D",       "joA?Q?5?4?2?2?");
        testWongWay("joASAC8H8SJSTH",       "joA?A?J?T?8?8?");    // page 129
        testWongWay("ACKD9D8D7D4S3D",       "KD9D8D7D3DA?4?");
        testWongWay("9D9H5D5HKS7S6S",       "K?9?9?7?6?5?5?");
        testWongWay("ASKSJD9D7D4H3H",       "A?9?7?4?3?K?J?");
        testWongWay("joASQSJS7S4H2S",       "??QSJS7S2S??4?");
        testWongWay("QDQHjo8H6C3H2H",       "joQH8H3H2HQ?6?");
        testWongWay("3H3S2D2SQS9H5C",       "5?3?3?2?2?Q?9?");
        testWongWay("9H9D3C3SASKH5C",       "9?9?5?3?3?A?K?");
        testWongWay("joKH8H7S5S4D3D",       "7?jo5?4?3?K?8?");   // page 130
        testWongWay("JHJD5D5SKSTS8C",       "K?J?J?T?8?5?5?");
        testWongWay("5H5S2S2DJD9H6C",       "J?9?6?5?5?2?2?");
        testWongWay("QDQCTDTHjo6D4D",       "joQ?Q?6?4?T?T?");
        testWongWay("joADKC7D6D5S3D",       "7?6?5?jo3?A?K?"); 
        // testWongWay("joKD9S8H7S3C2C",       "jo9?7?3?2?K?8?");   NYI - strategy 1
        testWongWay("7H7C6C6SKH8D2D",       "K?8?7?7?2?6?6?");
        testWongWay("KHQDJSTH9H5D3S",       "K?Q?J?T?9?5?3?");
	}

    private void testWongWay(String cardString, String expectedString)
    {
		testWongWay(cardString, expectedString, expectedString);
	}

    private void testWongWay(String cardString, String expectedPlayerSetting, String expectedBankerSetting)
    {
		testWongWay(playersWay, cardString, expectedPlayerSetting);
		testWongWay(playersWay, reverseCardString(cardString), expectedPlayerSetting);
		testWongWay(bankersWay, cardString, expectedBankerSetting);
		testWongWay(bankersWay, reverseCardString(cardString), expectedBankerSetting);
	}

	private void testWongWay(WongWay ww, String cardString, String expected)
	{
		assertEquals(14, cardString.length());
        byte[] cards = parseCards(cardString);
		assertEquals(7, cards.length);
		ww.set (cards);
		String result = Card.toString (cards);
		assertTrue (
			cardString + ": expected " + expected + ", got " + result,
			equalsIgnoreQ (result, expected));
    }

    private static byte[] parseCards(String cardString)
    {
        byte[] cards = new byte [CardsInHand];

        for (int cx = 0; cx < CardsInHand; ++cx)
        {
            cards[cx] = Card.parse (cardString, cx * 2);
        }

        return cards;
    }

    private static String reverseCardString(String cardString)
    {
		StringBuilder buf = new StringBuilder();
        while (cardString.length() > 0)
        {
			buf.append(cardString.substring(cardString.length() - 2));
			cardString = cardString.substring(0, cardString.length() - 2);
        }
		return buf.toString();
    }

    private static boolean equalsIgnoreQ (String s1, String s2)
    {
        for (int i = 0; i < s1.length (); ++i)
        {
            char c1 = s1.charAt (i);
            char c2 = s2.charAt (i);
            if (c1 != c2 && c2 != '?')
                return false;
        }

        return true;
    }
}
