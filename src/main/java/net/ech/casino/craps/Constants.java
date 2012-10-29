//
// Constants.java  
// 

package net.ech.casino.craps;

/**
 * Constants shared by craps classes.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface Constants
{
	/**
	 * First element of the bet enumeration.
	 */
	public final static int PASS_LINE = 0;
	public final static int PASS_ODDS = 1;

	/**
	 * Not a valid bet id.	Exists only to distinguish the don't pass bar 12
	 * betting area on the left from the one nearer the center.
	 */
	public final static int DONT_PASS_LEFT = 2;

	public final static int DONT_PASS = 3;
	public final static int COME = 4;
	public final static int COME_ODDS_4 = 5;
	// COME_ODDS_5 -> COME_ODDS_10 = 6(5), 7(6), 8(8), 9(9), 10(10)

	public final static int DONT_COME = 11;
	public final static int DONT_COME_ODDS_4 = 12;
	// DONT_COME_ODDS_5 -> DONT_COME_ODDS_10 = 13(5), 14(6), 15(8), 16(9), 17(10)

	public final static int PLACE_4 = 18;
	// PLACE_5 -> PLACE_10 = 19(5), 20(6), 21(8), 22(9), 23(10)

	public final static int BIG_6 = 24;
	public final static int BIG_8 = 25;
	public final static int FIELD = 26;
	public final static int ANY_SEVEN = 27;
	public final static int HARD_4 = 28;
	public final static int HARD_6 = 29;
	public final static int HARD_8 = 30;
	public final static int HARD_10 = 31;
	public final static int ROLL_2 = 32;
	public final static int ROLL_3 = 33;
	public final static int ROLL_11 = 34;
	public final static int ROLL_12 = 35;
	public final static int ANY_CRAPS = 36;
	public final static int COME_4 = 37;
	// COME_5 -> COME_10 = 38(5), 39(6), 40(8), 41(9), 42(10)
	public final static int DONT_COME_4 = 43;
	// DONT_COME_5 -> DONT_COME_10 = 44(5), 45(6), 46(8), 47(9), 48(10)
	public final static int DONT_PASS_ODDS = 49;
	public final static int HORN = 50;
	public final static int NUM_BETS = 51;

	public final static String[] BetNames =
	{
		"PASS LINE",
		"PASS ODDS",
		null,
		"DONT PASS",
		"COME",
		"COME ODDS 4",		  // 5
		"COME ODDS 5",		  // 6
		"COME ODDS 6",		  // 7
		"COME ODDS 8",		  // 8
		"COME ODDS 9",		  // 9
		"COME ODDS 10",		   // 10
		"DONT COME",
		"DONT COME ODDS 4", // 12
		"DONT COME ODDS 5", // 13
		"DONT COME ODDS 6", // 14
		"DONT COME ODDS 8", // 15
		"DONT COME ODDS 9", // 16
		"DONT COME ODDS 10", // 17
		"PLACE 4", // 18
		"PLACE 5", // 19
		"PLACE 6", // 20
		"PLACE 8", // 21
		"PLACE 9", // 22
		"PLACE 10", // 23
		"BIG 6",
		"BIG 8",
		"FIELD",
		"ANY SEVEN",
		"HARD 4",
		"HARD 6",
		"HARD 8",
		"HARD 10",
		"ROLL 2",
		"ROLL 3",
		"ROLL 11",
		"ROLL 12",
		"ANY CRAPS",
		"COME 4",	 // 37
		"COME 5",	 // 38
		"COME 6",	 // 39
		"COME 8",	 // 40
		"COME 9",	 // 41
		"COME 10",	 // 42
		"DONT COME 4",	  // 43
		"DONT COME 5",	  // 44
		"DONT COME 6",	  // 45
		"DONT COME 8",	  // 46
		"DONT COME 9",	  // 47
		"DONT COME 10",	  // 48
		"DONT PASS ODDS",
		"HORN"
	};

	public final static int points[] = {4, 5, 6, 8, 9, 10};
	public final static int hardways[] = {4, 6, 8, 10};
	public final static int rolls[] = {2, 3, 11, 12};
	public final static int craps[] = {2, 3, 12};
	public final static int winners[] = {7, 11};
	public final static int field[] = {2, 3, 4, 9, 10, 11, 12};
	public final static int checks[] = {1, 5, 25, 100, 500};
	public final static int pointBets[] = {COME_ODDS_4, DONT_COME_ODDS_4,
											PLACE_4, COME_4, DONT_COME_4};
}
