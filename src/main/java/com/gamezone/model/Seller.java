package com.gamezone.model;

/**
 * Represents an employee who attends clients and registers sales.
 * A seller is a specialization of Person with an employee code
 * and an assigned work shift.
 */
public class Seller extends Person {

    private String employeeCode;
    private String workShift;

    /**
     * Creates a new Seller with the given information.
     *
     * @param name         the seller's full name
     * @param employeeCode the seller's employee code
     * @param workShift    the seller's assigned work shift
     */
    public Seller(String name, String employeeCode, String workShift) {
        super(name, employeeCode, "N/A"); // Pasa valores por defecto o ajusta según Person
        this.employeeCode = employeeCode;
        this.workShift = workShift;
    }

    /**
     * Returns the seller's employee code.
     *
     * @return the employee code
     */
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
     * Sets the seller's employee code.
     *
     * @param employeeCode the new employee code
     */
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    /**
     * Returns the seller's assigned work shift.
     *
     * @return the work shift
     */
    public String getWorkShift() {
        return workShift;
    }

    /**
     * Sets the seller's assigned work shift.
     *
     * @param workShift the new work shift
     */
    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }
}