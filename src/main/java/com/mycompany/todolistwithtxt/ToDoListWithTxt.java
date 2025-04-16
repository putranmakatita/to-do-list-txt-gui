/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.todolistwithtxt;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

/**
 *
 * @author Putra Nurhuda Makatita
 */

public class ToDoListWithTxt {

    private static Scanner scanner = new Scanner(System.in);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Konstruktor untuk inisialisasi daftar to do
    public ToDoListWithTxt() {
        scanner = new Scanner(System.in);
    }

    // Fungsi untuk menambah to do ke daftar (Create)
    public void addTask(String task, String tanggal, String waktu) {
        if (task.equals("")) {
            JOptionPane.showMessageDialog(null, "Task tidak boleh kosong.");
            return;
        }
        if (tanggal.equals("") || tanggal.equals("dd/mm/yyyy")) {
            JOptionPane.showMessageDialog(null, "Tanggal tidak boleh kosong atau berisi placeholder.");
            return;
        }
        if (waktu.equals("") || waktu.equals("hh.mm")) {
            JOptionPane.showMessageDialog(null, "Waktu tidak boleh kosong atau berisi placeholder.");
            return;
        }

        // String code = generateCode();
        int code = getLastIndex();
        // String code = "TX1";

        try (FileWriter writer = new FileWriter("toDoListData.txt", true)) {
            writer.write("TX" + code + " | " + task + " | " + tanggal + " | " + waktu + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saat menulis file ke " + e);
            System.out.println("Error saat menulis file ke " + e);
        }
        JOptionPane.showMessageDialog(null, "Task added: " + task);
        System.out.println("Task added: " + task);

    }

    public String generateCode() {
        File file = new File("toDoListData.txt");
        int i = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }

        return "TX" + (i + 1);
    }

    // Fungsi untuk menghapus to do dari daftar (Delete)
    public void deleteTask(String task) {
        File file = new File("toDoListData.txt");
        ArrayList<String> lines = new ArrayList<>();
        Boolean isFound = false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals(task)) {
                    isFound = true;
                } else {
                    lines.add(scanner.nextLine());
                }
            }
            System.out.println("Task is not found: " + task);
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Gagal menulis ke file: " + e.getMessage());
            return;
        }

        if (isFound) {
            System.out.println("Task is deleted: " + task);
        } else {
            System.out.println("Task is not found: " + task);
        }
    }

    // Fungsi untuk mencari indeks to do dalam daftar
    public int findTaskIndex(String task) {
        File file = new File("toDoListData.txt");

        int idx = 1;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals(task)) {
                    return idx;
                }
                idx++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }

        return -1;
    }

    // Fungsi untuk menampilkan semua to do dalam daftar (Read all)
    public void displayTasks() {
        File file = new File("toDoListData.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "Belum ada data tersimpan.");
            return;
        }
        StringBuilder isi = new StringBuilder();
        int no = 1;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                isi.append(no + ". " + fileScanner.nextLine()).append("\n");
                no++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan.");
            return;
        }
        System.out.println(isi.toString());
    }

    // Fungsi untuk menampilkan semua to do dalam daftar (Read all)
    public Object[][] getAllTasks() {
        File file = new File("toDoListData.txt");

        if (!file.exists()) {
            System.out.println("Belum ada data tersimpan.");
            return new Object[0][0]; // Kembalikan array kosong
        }

        ArrayList<Object[]> isi = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String taskRecord = fileScanner.nextLine();
                String[] taskParts = taskRecord.split(" \\| "); // taskParts[1] = tanggal, taskParts[2] = waktu
                isi.add(new Object[] { 0, taskParts[0], taskParts[1], taskParts[2], taskParts[3] });
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan.");
            return new Object[0][0];
        }

        isi.sort((a, b) -> {
            String tanggalA = (String) a[3];
            String tanggalB = (String) b[3];

            // Format pertama: dua digit tahun (yy)
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yy");
            // Format kedua: empat digit tahun (yyyy)
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate dateA = null;
            LocalDate dateB = null;

            // Coba parse dengan format pertama (dd/MM/yy)
            try {
                dateA = LocalDate.parse(tanggalA, formatter1);
            } catch (DateTimeParseException e1) {
                try {
                    dateA = LocalDate.parse(tanggalA, formatter2); // Coba dengan format kedua (dd/MM/yyyy)
                } catch (DateTimeParseException e2) {
                    e2.printStackTrace(); // Jika masih gagal, print error untuk debugging
                }
            }

            // Coba parse dengan format pertama (dd/MM/yy)
            try {
                dateB = LocalDate.parse(tanggalB, formatter1);
            } catch (DateTimeParseException e1) {
                try {
                    dateB = LocalDate.parse(tanggalB, formatter2); // Coba dengan format kedua (dd/MM/yyyy)
                } catch (DateTimeParseException e2) {
                    e2.printStackTrace(); // Jika masih gagal, print error untuk debugging
                }
            }

            // Jika kedua tanggal berhasil diparse, lakukan perbandingan
            if (dateA != null && dateB != null) {
                int cmpTanggal = dateA.compareTo(dateB);
                if (cmpTanggal != 0)
                    return cmpTanggal;
            }

            // Jika tanggal sama, bandingkan waktu
            String waktuA = (String) a[4]; // contoh: "13:45"
            String waktuB = (String) b[4];

            return waktuA.compareTo(waktuB); // opsional: bisa parsing juga jika perlu
        });

        // Tambahkan nomor urut setelah diurutkan
        for (int i = 0; i < isi.size(); i++) {
            isi.get(i)[0] = i + 1;
        }

        return isi.toArray(new Object[0][]);
    }

    public int getLastIndex() {
        ArrayList<String> tasks = new ArrayList<>();
        File file = new File("toDoListData.txt");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                tasks.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }

        return tasks.size();
    }

    // Fungsi untuk sunting data (Update)
    public boolean updateTask(String code, String newTask, int col) {

        File file = new File("toDoListData.txt");
        ArrayList<String> lines = new ArrayList<>();

        boolean isFound = false;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String taskRecord = fileScanner.nextLine();
                String[] taskParts = taskRecord.split(" \\| ");
                String newLine = code + " | ";
                if (taskParts[0].equals(code)) {
                    isFound = true;
                    for (int i = 1; i < taskParts.length; i++) {
                        String akhiran = i == taskParts.length - 1 ? "" : " | ";
                        if (i == col) {
                            newLine += newTask + akhiran;
                            continue;
                        }
                        newLine += taskParts[i] + akhiran;
                    }
                } else {
                    newLine = taskParts[0] + " | " + taskParts[1] + " | " + taskParts[2] + " | " + taskParts[3];
                }
                lines.add(newLine);
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e);
            return false;
        }

        return isFound;
    }

    public static void main(String[] args) {
        ToDoListJFrame myFrame = new ToDoListJFrame();
        myFrame.jalanKan();

    }
}
