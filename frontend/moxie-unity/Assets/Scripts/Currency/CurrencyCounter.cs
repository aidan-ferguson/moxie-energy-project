using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class CurrencyCounter
{
    static int currency;

    public static void ModCurrency(int currencyDelta)
    {
        currency += currencyDelta;
    }

    public static int GetCurrency()
    {
        return currency;
    }
}
