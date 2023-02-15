using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq.Expressions;
using UnityEngine;

public static class CurrencyManager
{
    static int currency;

    public static void ModCurrency(int currencyDelta)
    {
        if (isEnoughDatabaseCurrency(currencyDelta))
        {
            ModLocalCurrency(currencyDelta);
            ModDatabaseCurrency(currencyDelta);
        }
    }

    static bool isEnoughDatabaseCurrency(int currencyToSpend)
    {
        return true; //Returns true for now, currently the player has unlimited spendings
        //Remove this after function has been properly implemented

        throw new NotImplementedException("App is not connected to database yet, Player can spend as much as they want");

        //Check if the player can spend "currencyToSpend" amount through the database
        //Assumes that database holds authority over this data
    }

    public static void ModLocalCurrency(int localCurrencyDelta)
    {
        currency += localCurrencyDelta;
    }

    public static void ModDatabaseCurrency(int databaseCurrencyDelta)
    {
        throw new NotImplementedException("App is not connected to database yet, no database currency to update");
        //Relay spendings to database
    }
    public static int GetCurrency()
    {
        return currency;
    }
}
