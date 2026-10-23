package com.gamezone.service;

import java.time.LocalDate;
import java.util.List;

import com.gamezone.model.Return;

/**
 * Computes the monthly figures of the returns report: the total of the sales,
 * the total refunded and the net balance. The sales total is read through a
 * narrow abstraction, so this class does not depend on the sale service.
 */
public class MonthlyBalanceCalculator {

    private final MonthlySales monthlySales;

    /**
     * Creates a calculator that reads the sales total through the given abstraction.
     *
     * @param monthlySales the source of the monthly sales total
     */
    public MonthlyBalanceCalculator(MonthlySales monthlySales) {
        this.monthlySales = monthlySales;
    }

    /**
     * Calculates the total sales amount for the given month and year, using
     * each sale's final total (including discounts and extended warranty costs).
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the total sales amount for that period
     */
    public double calculateMonthlySales(int month, int year) {
        return monthlySales.calculateMonthlyTotal(month, year);
    }

    /**
     * Calculates the total refunded amount for the returns registered in the
     * given month and year.
     *
     * @param returns the returns to evaluate
     * @param month   the month to evaluate (1-12)
     * @param year    the year to evaluate
     * @return the total returns amount for that period
     */
    public double calculateMonthlyReturns(List<Return> returns, int month, int year) {
        double totalReturns = 0.0;
        for (Return returnRecord : returns) {
            LocalDate returnDate = returnRecord.getDate();
            if (returnDate.getMonthValue() == month && returnDate.getYear() == year) {
                totalReturns += returnRecord.getRefundAmount();
            }
        }
        return totalReturns;
    }

    /**
     * Generates the net monthly balance, calculated as the total sales minus the
     * total returns recorded within that period.
     *
     * @param returns the returns to evaluate
     * @param month   the month to evaluate (1-12)
     * @param year    the year to evaluate
     * @return the net balance for that period
     */
    public double generateMonthlyBalance(List<Return> returns, int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(returns, month, year);
    }
}