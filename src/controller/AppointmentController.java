/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.AppointmentDAO;
import dao.PatientDAO;
import model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;


public class AppointmentController {
    
    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;

    public AppointmentController() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
    }

   
    public String createAppointment(Appointment appointment) {
        // Step 1: register patient if new (no existing ID)
        if (appointment.getPatient().getPatientId() == null) {
            String patientId = patientDAO.saveOrUpdate(appointment.getPatient());
            if (patientId == null) {
                return null;
            }
            appointment.getPatient().setPatientId(patientId);
        }

        // Step 2: check dentist availability
        boolean available = appointmentDAO.checkAvailability(
                appointment.getDentist().getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );
        if (!available) {
            return null;
        }

        // Step 3: generate appointment number
        String appointmentNo = appointmentDAO.generateAppointmentNo();
        if (appointmentNo == null) {
            return null;
        }
        appointment.setAppointmentNo(appointmentNo);

        // Step 4: save
        return appointmentDAO.save(appointment) ? appointmentNo : null;
    }

    /**
     * Retrieves an appointment by its number.
     *
     * @param appointmentNo the number of the appointment to retrieve
     * @return the Appointment object if found, null otherwise
     */
    public Appointment getAppointmentDetails(String appointmentNo) {
        return appointmentDAO.findByNo(appointmentNo);
    }

    /**
     * Cancels an appointment.
     *
     * @param appointmentNo the number of the appointment to cancel
     * @return true if the cancellation succeeded, false otherwise
     */
    public boolean cancelAppointment(String appointmentNo) {
        Appointment appointment = appointmentDAO.findByNo(appointmentNo);
        if (appointment == null) {
            return false;
        }
        appointment.cancel();
        return appointmentDAO.update(appointment);
    }

    /**
     * Reschedules an appointment to a new date and time.
     *
     * @param appointmentNo the number of the appointment to reschedule
     * @param newDate       the new date
     * @param newTime       the new time
     * @return true if the rescheduling succeeded, false otherwise
     */
    public boolean rescheduleAppointment(String appointmentNo, LocalDate newDate, LocalTime newTime) {
        Appointment appointment = appointmentDAO.findByNo(appointmentNo);
        if (appointment == null) {
            return false;
        }

        boolean available = appointmentDAO.checkAvailability(
                appointment.getDentist().getDentistId(), newDate, newTime
        );
        if (!available) {
            return false;
        }

        appointment.reschedule(newDate, newTime);
        return appointmentDAO.update(appointment);
    }
}
