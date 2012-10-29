//
// Analyzer.java
//

package net.ech.casino.keno;

import java.math.BigDecimal;

/**
 * Keno analyzer produces payout figures for a given set of keno
 * machine parameters.
 */
public class Analyzer
{
	public static void main(String argv[])
	{
		 try
		 {
			 String className = null;
			 int size = 80;
			 int bet = 1;
			 int nrolled = 20;
			 int maxWin = -1;
			 boolean odds = false;
			 String payTableName = null;

			 for (int i = 0; i < argv.length; ++i)
			 {
				  String arg = argv[i];
				  if (arg.startsWith ("-"))
				  {
					  if (arg.equalsIgnoreCase ("-class"))
					  {
						   ++i;
						   className = argv[i];
					  }
					  else if (arg.equalsIgnoreCase ("-size"))
					  {
						   ++i;
						   size = Integer.parseInt (argv[i]);
					  }
					  else if (arg.equalsIgnoreCase ("-nrolled"))
					  {
						   ++i;
						   nrolled = Integer.parseInt (argv[i]);
					  }
					  else if (arg.equalsIgnoreCase ("-maxwin"))
					  {
						   ++i;
						   maxWin = Integer.parseInt (argv[i]);
					  }
					  else if (arg.equalsIgnoreCase ("-bet"))
					  {
						   ++i;
						   bet = Integer.parseInt (argv[i]);
					  }
					  else if (arg.equalsIgnoreCase ("-odds"))
					  {
						   odds = true;
					  }
					  else
					  {
						   usage ();
					  }
				  }
				  else
				  {
					  payTableName = arg;
				  }
			 }

			 if (className == null && payTableName == null)
			 {
				  usage ();
			 }

			 KenoMachine machine;
			 if (className != null)
			 {
				  machine = (KenoMachine) Class.forName (className).newInstance ();
			 }
			 else
			 {
				  machine = new KenoMachine ();
				  machine.setId ("test");
				  machine.setDollarsPerCredit (1.0);
				  machine.setSize (size);
				  machine.setNumberRolled(nrolled);
				  machine.loadPayTable (payTableName);
			 }
			 if (maxWin > 0)
				  machine.setMaximumWin (maxWin);

			 Analyzer ka = new Analyzer (machine, bet);
			 if (!odds)
				  ka.printPayout ();
			 else
				  ka.printOdds ();
		 }
		 catch (Exception e)
		 {
			 System.err.println (e);
			 System.exit (1);
		 }
	}

	private static void usage ()
	{
		 System.err.println ("usage: java net.ech.casino.keno.Analyzer [-options] paytablename");
		 System.err.println ("options:");
		 System.err.println ("\t-bet #\tnumber of credits bet");
		 System.err.println ("\t-class XXX\tname of machine class");
		 System.err.println ("\t-maxwin #\tmaximum win in credits");
		 System.err.println ("\t-nrolled #\tnumber of numbers generated (don't use with -class)");
		 System.err.println ("\t-odds\tprint odds for each row, not payout");
		 System.err.println ("\t-size #\tnumber of boxes to choose from (don't use with -class)");
		 System.exit (1);
	}

	private KenoMachine machine;
	private int bet;

	/**
	 * Constructor.
	 */
	public Analyzer (KenoMachine machine, int bet)
	{
		 this.machine = machine;
		 this.bet = bet;
	}

	/**
	 * Loops through the pay table and determines the
	 * expected payback percentage per bet.
	 */
	public void printPayout ()
	{
		 int min = machine.getMinPicked ();
		 int max = machine.getMaxPicked ();
		 int size = machine.getSize ();
		 int nrolled = machine.getNumberRolled ();
		 int maxWin = machine.getMaximumWin ();

		 System.out.println ("Keno, size=" + size + ", nrolled=" + nrolled +
							   ", min=" + min +
							   ", max=" + max +
							   ", bet=" + bet);

		for (int npicks = min; npicks <= max; ++npicks)
		 {
			 double paysum = 0;
			 for (int nhits = 0; nhits <= npicks; nhits++)
			 {
				double prob = probability(npicks, nhits, nrolled, size);
				  int win = machine.getPayMultiple (npicks, nhits) * bet;;
				  if (win > maxWin)
					  win = maxWin;
				 paysum += prob * win;
			 }
			 paysum /= bet;
			 System.out.println(npicks + ": " + toString (paysum, 4));
		 }
	}

	/**
	 * Prints a table showing the odds of hitting any single
	 * payout in the pay table.
	 */
	public void printOdds ()
	{
		 int min = machine.getMinPicked ();
		 int max = machine.getMaxPicked ();
		 int size = machine.getSize ();
		 int nrolled = machine.getNumberRolled ();

		 System.out.println ("Keno, size=" + size + ", nrolled=" + nrolled +
							   ", min=" + min +
							   ", max=" + max);

		for (int npicks = min; npicks <= max; ++npicks)
		 {
			 System.out.print (npicks);
			 for (int nhits = 0; nhits <= npicks; ++nhits)
			 {
				double prob = probability(npicks, nhits, nrolled, size);
				  System.out.print (" " + nhits + ":" + toString (prob, 4));
			 }
			 System.out.println ();
		 }
	}

	/**
	 * Given that the user has picked "a" squares out of "m"
	 * and the dealer has picked "c" squares, returns the
	 * probability that the dealer picked exactly "b" of the
	 * user's squares.
	 */
	private static double probability(int a, int b, int c, int m)
	{
		double result = combination(a, b);
		 for (int i = 0; i < b; i++)
			 result *= (double) (c - i) / (double) (m - i);
		 for (int i = b; i < a; i++)
			 result *= (double) (m - c + b - i) / (double) (m - i);
		 if (result < 0 || result > 1.0) {
			 throw new RuntimeException("ERROR! result of probability(" + a + "," + b
								  + "," + c + "," + m + ") is " + result);
		 }
		return result;
	}

	/**
	 * Number of ways of choosing b items from a.
	 */
	private static double combination(int a, int b)
	{
		double result = 1;
		 for (int i = a - b + 1; i <= a; i++)
			 result *= i;
		 for (int j = 2; j <= b; j++)
			 result /= j;
		 return result;
	}

	/**
	 * Print a double-precision float to a number of significant
	 * digits.
	 */
	private static String toString (double value, int sig)
	{
		 if (value <= 0.0 || value >= 10.0)
			 return Double.toString (value);

		 double v = value;
		 int desiredScale = 0;
		 while (v < 1.0)
		 {
			 v *= 10;
			 ++desiredScale;
		 }
		 desiredScale += sig - 1;

		 BigDecimal bd = new BigDecimal (value);
		 if (bd.scale () > desiredScale)
			 bd = bd.setScale (desiredScale, BigDecimal.ROUND_HALF_UP);

		 return bd.toString ();
	}
}
