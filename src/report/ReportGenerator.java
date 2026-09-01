/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package report;

import model.Report;


public interface ReportGenerator {
    
    /**
     * Generates a report and returns a populated Report object.
     *
     * @return the generated Report
     */
    Report generate();
}
    