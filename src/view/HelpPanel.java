/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;


public class HelpPanel extends JPanel{
    
    
    public HelpPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        JLabel header = new JLabel("  Help & Support");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        JTextArea helpText = new JTextArea();
        helpText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        helpText.setEditable(false);
        helpText.setMargin(new Insets(20, 32, 20, 32));
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setText(
            "SUNRISE DENTAL CLINIC — HELP GUIDE\n"
          + "============================================\n\n"
 
          + "NAVIGATION\n"
          + "Use the sidebar on the left to switch between modules.\n"
          + "The sidebar adapts to your role — Admin users see additional\n"
          + "options for Reports and User Management.\n\n"
 
          + "MODULES\n\n"
 
          + "1. Dashboard\n"
          + "   View today's appointment summary, quick stats, and\n"
          + "   shortcut buttons to common actions.\n\n"
 
          + "2. New Appointment\n"
          + "   Register a new appointment. Search for an existing patient\n"
          + "   by ID, or leave the ID blank to register a new one.\n"
          + "   Select a dentist, treatment, date, and time. Always click\n"
          + "   'Check Availability' before booking.\n\n"
 
          + "3. Search Appointment\n"
          + "   Look up any appointment by its number (e.g. APT-00001).\n"
          + "   From here you can cancel or reschedule the appointment.\n\n"
 
          + "4. Billing\n"
          + "   Search an appointment by number, enter the consultation\n"
          + "   fee, and generate a bill. The treatment cost is auto-filled\n"
          + "   from the appointment's treatment type. Once saved, a\n"
          + "   printable receipt is displayed.\n\n"
 
          + "5. Reports (Admin only)\n"
          + "   Generate Daily Appointment Reports for any date, or\n"
          + "   Revenue Reports for any date range. Both include summary\n"
          + "   stats, detailed tables, and an Export/Print option.\n\n"
 
          + "6. User Management (Admin only)\n"
          + "   View all system users and add new Receptionist or Admin\n"
          + "   accounts.\n\n"
 
          + "CONTACT SUPPORT\n"
          + "   Email: support@sunrisedental.lk\n"
          + "   Phone: +94 11 234 5678\n\n"
 
          + "APPLICATION VERSION: v1.0\n"
        );
 
        JScrollPane scroll = new JScrollPane(helpText);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
}
