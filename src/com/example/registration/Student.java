package com.example.registration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Student {
    private final String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String course;
    private int year;
    private double gpa;
    private String photoPath;

    private static final DateTimeFormatter CSV_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public Student() {
        this.id = UUID.randomUUID().toString();
    }

    public Student(String firstName, String lastName, LocalDate dob, String gender, String email, String phone,
                   String address, String course, int year, double gpa, String photoPath) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.course = course;
        this.year = year;
        this.gpa = gpa;
        this.photoPath = photoPath;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String toCsv() {
        return escape(id) + "," + escape(firstName) + "," + escape(lastName) + "," +
                (dob == null ? "" : dob.format(CSV_DATE)) + "," + escape(gender) + "," + escape(email) + "," +
                escape(phone) + "," + escape(address) + "," + escape(course) + "," + year + "," + gpa + "," + escape(photoPath);
    }

    public static Student fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 12) return null;
        Student s = new Student();
        // Note: id comes from file, but we'll preserve original id by reflection-like assignment is avoided; instead ignore file id
        s.firstName = unescape(parts[1]);
        s.lastName = unescape(parts[2]);
        try { s.dob = parts[3].isEmpty() ? null : LocalDate.parse(parts[3], CSV_DATE); } catch (Exception ignored) {}
        s.gender = unescape(parts[4]);
        s.email = unescape(parts[5]);
        s.phone = unescape(parts[6]);
        s.address = unescape(parts[7]);
        s.course = unescape(parts[8]);
        try { s.year = Integer.parseInt(parts[9]); } catch (Exception e) { s.year = 1; }
        try { s.gpa = Double.parseDouble(parts[10]); } catch (Exception e) { s.gpa = 0.0; }
        s.photoPath = unescape(parts[11]);
        return s;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").replace(",", ";");
    }

    private static String unescape(String s) {
        if (s == null) return null;
        return s.replace(";", ",");
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + (course == null ? "" : course) + ")";
    }
}
