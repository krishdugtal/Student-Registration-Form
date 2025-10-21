package com.example.registration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// A polished Swing UI with responsive layout using GridBagLayout and custom styling
public class StudentRegistrationForm extends JFrame {
    private final StudentDataStore store = new StudentDataStore();
    private final DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // form fields
    private JTextField firstNameField = new JTextField();
    private JTextField lastNameField = new JTextField();
    private JTextField dobField = new JTextField();
    private JComboBox<String> genderBox = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
    private JTextField emailField = new JTextField();
    private JTextField phoneField = new JTextField();
    private JTextArea addressArea = new JTextArea(3, 20);
    private JComboBox<String> courseBox = new JComboBox<>(new String[]{"", "Computer Science", "Mathematics", "Physics", "Biology", "Chemistry", "Economics"});
    private JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    // Allow GPA up to 10.0 and fine-grained steps (0.01) so values like 9.72 are preserved
    private JSpinner gpaSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.01));
    private JLabel photoLabel = new JLabel();
    private String photoPath = null;

    private DefaultListModel<Student> listModel = new DefaultListModel<>();
    private JList<Student> studentList = new JList<>(listModel);

    public StudentRegistrationForm() {
        super("Student Registration Form");
        initUI();
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(16,16,16,16));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(8,8,8,8);

        // Left: form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,225)), new EmptyBorder(12,12,12,12)));

        int row = 0;
        addLabelField(form, "First name", firstNameField, row++, c);
        addLabelField(form, "Last name", lastNameField, row++, c);
        addLabelField(form, "Date of birth (YYYY-MM-DD)", dobField, row++, c);
        addLabelField(form, "Gender", genderBox, row++, c);
        addLabelField(form, "Email", emailField, row++, c);
        addLabelField(form, "Phone", phoneField, row++, c);
        addLabelField(form, "Address", new JScrollPane(addressArea), row++, c);
        addLabelField(form, "Course", courseBox, row++, c);
        addLabelField(form, "Year", yearSpinner, row++, c);
        addLabelField(form, "GPA", gpaSpinner, row++, c);

        // photo chooser
        JPanel photoRow = new JPanel(new BorderLayout(8,8));
        photoRow.setBackground(Color.WHITE);
        JButton choosePhoto = new JButton("Choose Photo");
        choosePhoto.addActionListener(e -> onChoosePhoto());
        photoLabel.setPreferredSize(new Dimension(120, 120));
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(220,220,225)));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoRow.add(photoLabel, BorderLayout.WEST);
        photoRow.add(choosePhoto, BorderLayout.CENTER);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.weightx = 1; c.weighty = 0;
        form.add(photoRow, c);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);
    JButton submit = new JButton("Register");
    submit.setBackground(new Color(34,150,243));
    // Use a dark foreground so the label remains visible under different LookAndFeels
    submit.setForeground(Color.BLACK);
    submit.setOpaque(true);
    submit.setBorderPainted(false);
    submit.setFocusPainted(false);
    submit.setFont(submit.getFont().deriveFont(Font.BOLD));
    submit.addActionListener(e -> onSubmit());
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> onClear());
        actions.add(clear);
        actions.add(submit);

        c.gridx = 0; c.gridy = row+1; c.gridwidth = 2; c.weightx = 1; c.weighty = 0;
        form.add(actions, c);

        // Right: list
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(250,250,252));
        right.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,225)), new EmptyBorder(12,12,12,12)));
        studentList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) lbl.setText(((Student) value).toString());
                lbl.setBorder(new EmptyBorder(6,6,6,6));
                return lbl;
            }
        });

        right.add(new JLabel("Registered Students"), BorderLayout.NORTH);
        right.add(new JScrollPane(studentList), BorderLayout.CENTER);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightActions.setBackground(new Color(250,250,252));
        JButton saveBtn = new JButton("Save CSV");
        saveBtn.addActionListener(e -> onSave());
        JButton loadBtn = new JButton("Load CSV");
        loadBtn.addActionListener(e -> onLoad());
        rightActions.add(loadBtn);
        rightActions.add(saveBtn);
        right.add(rightActions, BorderLayout.SOUTH);

        // Layout placement
        c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0; c.gridy = 0; c.weightx = 0.65; c.weighty = 1;
        root.add(form, c);

        c.gridx = 1; c.gridy = 0; c.weightx = 0.35; c.weighty = 1;
        root.add(right, c);

        setContentPane(root);

        // small polish
        firstNameField.setFont(firstNameField.getFont().deriveFont(14f));
        lastNameField.setFont(firstNameField.getFont());
        dobField.setFont(firstNameField.getFont());
        emailField.setFont(firstNameField.getFont());
        phoneField.setFont(firstNameField.getFont());
        addressArea.setFont(firstNameField.getFont());

    // Ensure GPA spinner shows two decimals and preserves user input like 9.72
    JSpinner.NumberEditor gpaEditor = new JSpinner.NumberEditor(gpaSpinner, "0.00");
    gpaSpinner.setEditor(gpaEditor);

        // list double click to view details
        studentList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Student s = studentList.getSelectedValue();
                    if (s != null) showStudentDetails(s);
                }
            }
        });
    }

    private void addLabelField(JPanel p, String label, Component field, int row, GridBagConstraints base) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = base.insets; c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = row; c.weightx = 0.28; c.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(13f));
        p.add(lbl, c);

        c.gridx = 1; c.gridy = row; c.weightx = 0.72; c.anchor = GridBagConstraints.CENTER;
        if (field instanceof JScrollPane) {
            p.add(field, c);
        } else {
            field.setPreferredSize(new Dimension(200, 30));
            p.add(field, c);
        }
    }

    private void onChoosePhoto() {
        // Ensure any partial edits in the GPA spinner are committed before focus changes
        try {
            gpaSpinner.commitEdit();
        } catch (ParseException ex) {
            // ignore; the spinner will revert or keep previous valid value
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));
        int ok = chooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            photoPath = f.getAbsolutePath();
            try {
                BufferedImage img = ImageIO.read(f);
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
            } catch (IOException ex) {
                photoLabel.setIcon(null);
                photoLabel.setText("No preview");
            }
        }
    }

    private void onSubmit() {
        // validate
        // Commit any pending editor edits (e.g. GPA) so we read the current typed value
        try {
            gpaSpinner.commitEdit();
        } catch (ParseException ex) {
            // ignore, we'll read whatever value is currently set on the model
        }
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        if (fn.isEmpty() || ln.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First and last name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dob = null;
        if (!dobField.getText().trim().isEmpty()) {
            try { dob = LocalDate.parse(dobField.getText().trim(), dobFormat); } catch (Exception e) { JOptionPane.showMessageDialog(this, "DOB must be in YYYY-MM-DD format", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        }
        Student s = new Student(fn, ln, dob, (String)genderBox.getSelectedItem(), emailField.getText().trim(), phoneField.getText().trim(), addressArea.getText().trim(), (String)courseBox.getSelectedItem(), (Integer)yearSpinner.getValue(), ((Number)gpaSpinner.getValue()).doubleValue(), photoPath);
        store.add(s);
        listModel.addElement(s);
        onClear();
    }

    private void onClear() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderBox.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        courseBox.setSelectedIndex(0);
        yearSpinner.setValue(1);
        gpaSpinner.setValue(0.0);
        photoLabel.setIcon(null);
        photoLabel.setText("");
        photoPath = null;
    }

    private void onSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save students to CSV");
        int ok = chooser.showSaveDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try { store.saveToCsv(f); JOptionPane.showMessageDialog(this, "Saved."); } catch (IOException e) { JOptionPane.showMessageDialog(this, "Save failed: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void onLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load students from CSV");
        int ok = chooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try { store.loadFromCsv(f); refreshList(); JOptionPane.showMessageDialog(this, "Loaded."); } catch (IOException e) { JOptionPane.showMessageDialog(this, "Load failed: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void refreshList() {
        listModel.clear();
        List<Student> students = store.list();
        for (Student s : students) listModel.addElement(s);
    }

    private void showStudentDetails(Student s) {
        StringBuilder b = new StringBuilder();
        b.append("Name: ").append(s.getFirstName()).append(" ").append(s.getLastName()).append("\n");
        b.append("DOB: ").append(s.getDob() == null ? "" : s.getDob().toString()).append("\n");
        b.append("Gender: ").append(s.getGender()).append("\n");
        b.append("Email: ").append(s.getEmail()).append("\n");
        b.append("Phone: ").append(s.getPhone()).append("\n");
        b.append("Address: ").append(s.getAddress()).append("\n");
        b.append("Course: ").append(s.getCourse()).append("\n");
        b.append("Year: ").append(s.getYear()).append("\n");
        b.append("GPA: ").append(s.getGpa()).append("\n");
        if (s.getPhotoPath() != null) b.append("Photo: ").append(s.getPhotoPath()).append("\n");
        JOptionPane.showMessageDialog(this, b.toString(), "Student details", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentRegistrationForm f = new StudentRegistrationForm();
            f.setVisible(true);
        });
    }
}
