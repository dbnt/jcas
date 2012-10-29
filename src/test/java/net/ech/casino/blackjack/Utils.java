package net.ech.casino.blackjack;

import net.ech.casino.*;

public class Utils
{
    /**
     * Return a Shoe created by the provided machine but with a stacked
     * deck.
     */
    public static Shoe stackedShoe (
        BlackjackMachine machine,
        String playerCardString,
        String dealerCardString)
    {
        StringBuffer stackedCards = new StringBuffer (48);

        stackedCards.append (playerCardString.substring (0, 2));
        stackedCards.append (dealerCardString.substring (0, 2));
        stackedCards.append (playerCardString.substring (2, 4));
        stackedCards.append (dealerCardString.substring (2, 4));

        int nPlayerCards = playerCardString.length() / 2;
        for (int i = 2; i < nPlayerCards; ++i)
        {
            stackedCards.append (playerCardString.substring (i * 2, i * 2 + 2));
        }

        int nDealerCards = dealerCardString.length() / 2;
        for (int i = 2; i < nDealerCards; ++i)
        {
            stackedCards.append (dealerCardString.substring (i * 2, i * 2 + 2));
        }

        Shoe shoe = machine.createNewShoe ();
        shoe.setStackedCards (stackedCards.toString());
        return shoe;
    }
}
