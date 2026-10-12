package com.project.back_end.DTO;

import com.project.back_end.models.Patient;

import java.time.LocalDate;

public class PatientDTO {
    // Patient data not containing sensitive information, namely password or secrets
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String emergencyContact;
    private String insuranceProvider;

    // Constructor:
    //    - The constructor accepts all the relevant fields for the PatientDTO
    public PatientDTO(Long id, String name, String email, String phone, String address, LocalDate dateOfBirth, String emergencyContact, String insuranceProvider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.insuranceProvider = insuranceProvider;
    }

    // Constructor:
    //    - The constructor accepts a Patient and assigns corresponding fields to new constructed PatientDTO
    public PatientDTO(Patient patient) {
        this.id = patient.getId();
        this.name = patient.getName();
        this.email = patient.getEmail();
        this.phone = patient.getPhone();
        this.address = patient.getAddress();
        this.dateOfBirth = patient.getDateOfBirth();
        this.emergencyContact = patient.getEmergencyContact();
        this.insuranceProvider = patient.getInsuranceProvider();
    }

    // Getters:
    //    - Standard getter methods are provided for all fields
    //    - These methods allow access to the values of the fields in the PatientDTO object.

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

}
