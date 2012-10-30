//
// Money.java  
// 

package net.ech.casino;

/**
 * A Money object is a number of dollars and cents.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class Money extends Number
	implements java.io.Serializable, java.lang.Cloneable
{
	public final static Money ZERO = new Money (0);

	/**
	 * For backward compatibilty.
	 * @deprecated	Use Money.ZERO.
	 */
	public final static Money Zero = new Money (0);

	private final static double GRACE = 0.001;

	private int cents;

	/**
	 * Transform a null Money into a non-null zero.	 Pass all material 
	 * objects through.
	 */
	public static Money materialize (Money money)
	{
		return money == null ? ZERO : money;
	}

	/**
	 * Constructor.
	 */
	public Money ()
	{
	}

	/**
	 * Constructor.
	 */
	public Money (double amount)
	{
		// Handle floating point imprecision, as in 100.249999999999999999
		cents = (int) ((amount + GRACE) * 100.0);
	}

	/**
	 * Constructor.
	 */
	public Money (int dollars)
	{
		cents = dollars * 100;
	}

	/**
	 * Constructor (parser).
	 */
	public Money (String str)
		throws NumberFormatException
	{
		int len = str.length ();
		int dollars = 0;
		int cents = 0;
		int decimal = 0;
		for (int i = 0; i < len; ++i)
		{
			char c = str.charAt (i);
			if (c >= '0' && c <= '9')
			{
				int val = c - '0';
				switch (decimal)
				{
				case 0:
					dollars = (dollars * 10) + val;
					break;
				case 1:
					cents = val * 10;
					++decimal;
					break;
				case 2:
					cents += val;
					++decimal;
					break;
				}
			}
			else if (c != '.' || decimal != 0)
			{
				throw new NumberFormatException (str);
			}
			else
				++decimal;
		}

		this.cents = (dollars * 100) + cents;
	}

	/**
	 * Return the value as an int by dropping cents.
	 */
	public int intValue ()
	{
		return cents / 100;
	}

	/**
	 * Return the value as a long by dropping cents.
	 */
	public long longValue ()
	{
		return cents / 100;
	}

	/**
	 * Return the value as a float.
	 */
	public float floatValue ()
	{
		return cents / 100f;
	}

	/**
	 * Return the value as a double.
	 */
	public double doubleValue ()
	{
		return cents / 100.0;
	}

	/**
	 * Return the value as a byte (why?)
	 */
	public byte byteValue ()
	{
		return (byte) (cents / 100);
	}

	/**
	 * Return the value as a short (why?)
	 */
	public short shortValue ()
	{
		return (short) (cents / 100);
	}

	/**
	 * Return a Money whose value is this plus that.
	 */
	public Money add (Money that)
	{
		Money sum = new Money ();
		sum.cents = cents + that.cents;
		return sum;
	}

	/**
	 * Return a Money whose value is this minus that.
	 */
	public Money subtract (Money that)
	{
		Money difference = new Money ();
		difference.cents = cents - that.cents;
		return difference;
	}

	/**
	 * Return a Money whose value is this times that.
	 */
	public Money multiply (int that)
	{
		Money product = new Money ();
		product.cents = cents * that;
		return product;
	}

	/**
	 * Return a Money whose value is this divided by that.
	 */
	public Money divide (int that)
	{
		Money quotient = new Money ();
		quotient.cents = cents / that;
		return quotient;
	}

	/**
	 * Return a Money whose value is the absolute value of
	 * this one.
	 */
	public Money abs ()
	{
		return cents < 0 ? negate () : this;
	}

	/**
	 * Return a Money whose value is the -1 * this one.
	 */
	public Money negate ()
	{
		Money neg = new Money ();
		neg.cents = -cents;
		return neg;
	}

	/**
	 * Return the signum function of this amount (-1, 0 or 1 as
	 * the value is negative, zero or positive).
	 */
	public int signum ()
	{
		return cents == 0 ? 0 : (cents < 0 ? -1 : 1);
	}

	/**
	 * Return true iff this Money value represents a whole dollar amount.
	 * @since 1.2
	 */
	public boolean isWhole ()
	{
		return cents % 100 == 0;
	}

	/**
	 * Return the difference between this and that.
	 */
	public int compareTo (Money that)
	{
		return cents - that.cents;
	}

	/**
	 * Return true if that is a Money with value equals to this one.
	 */
	public boolean equals (Object that)
	{
		if (that == null)
			return false;
		if (!(that instanceof Money))
			return false;
		return cents == ((Money) that).cents;
	}

	/**
	 * Return the greater of this and that.
	 */
	public Money max (Money that)
	{
		return cents > that.cents ? this : that;
	}

	/**
	 * Return the lesser of this and that.
	 */
	public Money min (Money that)
	{
		return cents < that.cents ? this : that;
	}

	/**
	 * Compute a hash code.
	 */
	public int hashCode ()
	{
		return cents;
	}

	/**
	 * Format as String.
	 */
	public String toString ()
	{
		StringBuilder buf = new StringBuilder ();
		int cents = this.cents;
		if (cents < 0)
		{
			buf.append ('-');
			cents *= -1;
		}
		buf.append (cents / 100);
		buf.append ('.');
		int beforeLen = buf.length ();
		cents %= 100;
		if (cents < 10)
			buf.append ('0');
		buf.append (cents);
		return buf.toString ();
	}
}
