package com.gamezone.service;

/**
 * Narrow abstraction for the total of the sales of a month, so the services
 * that report a balance do not depend on the whole sale service.
 */
public interface MonthlySales {

    /**
     * Calculates the sum of the totals of the sales registered in the given month and year.
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the sum of the totals of the sales of that period
     */
    double calculateMonthlyTotal(int month, int year);
}