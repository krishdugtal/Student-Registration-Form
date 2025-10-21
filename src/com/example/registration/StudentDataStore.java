package com.example.registration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentDataStore {
    private final List<Student> students = new ArrayList<>();

    public synchronized void add(Student s) {
        students.add(s);
    }

    public synchronized List<Student> list() {
        return Collections.unmodifiableList(new ArrayList<>(students));
    }

    public synchronized void clear() { students.clear(); }

    public synchronized void saveToCsv(File f) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
            for (Student s : students) {
                w.write(s.toCsv());
                w.newLine();
            }
        }
    }

    public synchronized void loadFromCsv(File f) throws IOException {
        if (!f.exists()) return;
        students.clear();
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                Student s = Student.fromCsv(line);
                if (s != null) students.add(s);
            }
        }
    }
}
