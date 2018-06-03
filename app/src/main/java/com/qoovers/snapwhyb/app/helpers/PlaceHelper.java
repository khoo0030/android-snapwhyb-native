package com.qoovers.snapwhyb.app.helpers;

public class PlaceHelper
{
    public static String getAddress(String fullAddress) {
        String address = "";

        if (!fullAddress.isEmpty()) {
            address = fullAddress.split(",")[0].trim();
        }

        return address;
    }

    public static String getCountry(String fullAddress) {
        String country = "";

        if (!fullAddress.isEmpty()) {
            String[] valueArray = fullAddress.split(",");

            int count = valueArray.length;

            int i = 1;
            for (String value : valueArray)
            {
                if (i == count) {
                    country = value.trim().split(" ")[0].trim();
                }

                i++;
            }
        }

        return country;
    }
}
