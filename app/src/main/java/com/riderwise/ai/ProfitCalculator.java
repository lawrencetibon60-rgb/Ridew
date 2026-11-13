package com.riderwise.ai;

public class ProfitCalculator {
    public static double estimatedFuelCost(double distanceKm, double fuelPricePerLiter, double kmPerLiter) {
        if (distanceKm <= 0 || fuelPricePerLiter <= 0 || kmPerLiter <= 0) return Double.NaN;
        return (distanceKm / kmPerLiter) * fuelPricePerLiter;
    }
    public static double netFare(double fare, double fuelCost) {
        return fare - fuelCost;
    }
}
