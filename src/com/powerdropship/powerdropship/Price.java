package com.powerdropship.powerdropship;


import com.powerdropship.powerdropship.model.Job;


public class Price {

    //Applies the Price Multiplier, then calls priceTiers
    public double priceChange(Job job, double price) {
        double newPrice = price * job.getPriceMultiplier();
        return priceTiers(job, newPrice);
    }

    //Tests to see if price * price multiplier is less than MAP price,
    // if yes, then use MAP price * MAP price multiplier.
    // Then calls priceTiers.
    public double firstConsiderMAP(Job job, double price, double mapPrice) {
        double newPrice = price * job.getPriceMultiplier();
        if (newPrice < mapPrice) {
            newPrice = mapPrice * job.getPriceMultiplierMAP();
            //System.out.println(newPrice + " < " + mapPrice);
        }
        return priceTiers(job, newPrice);
    }

    //Applies the price tiers.
    public double priceTiers(Job job, double newPrice) {

        if (newPrice == 0) {
            newPrice = 1000000.00;

            //rounds to 2 decimals
            return newPrice;

        } else if (newPrice > job.getPriceTier1Start() & newPrice < job.getPriceTier1End()) {
            double finalPrice = newPrice * job.getPriceTier1Multiplier();
            return finalPrice;
        } else if (newPrice > job.getPriceTier2Start() & newPrice < job.getPriceTier2End()) {
            double finalPrice = newPrice * job.getPriceTier2Multiplier();
            return finalPrice;
        } else if (newPrice > job.getPriceTier3Start() & newPrice < job.getPriceTier3End()) {
            double finalPrice = newPrice * job.getPriceTier3Multiplier();
            return finalPrice;
        } else if (newPrice > job.getPriceTier4Start() & newPrice < job.getPriceTier4End()) {
            double finalPrice = newPrice * job.getPriceTier4Multiplier();
            return finalPrice;
        }
        return (double) Math.round(newPrice * 100) / 100;
    }
}
