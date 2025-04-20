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

        int urutan = getLastIndex();

        try (FileWriter writer = new FileWriter("toDoListData.txt", true)) {
            writer.write("TX" + urutan + " | " + task + " | " + tanggal + " | " + waktu + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saat menulis file ke " + e);
            System.out.println("Error saat menulis file ke " + e);
        }
        JOptionPane.showMessageDialog(null, "Task added: " + task);
        System.out.println("Task added: " + task);

    }

    // Fungsi untuk menghapus to do dari daftar (Delete)
    public boolean deleteTask(String code) {
        File file = new File("toDoListData.txt");
        ArrayList<String> lines = new ArrayList<>();

        boolean isFound = false;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String taskRecord = fileScanner.nextLine();
                String[] taskParts = taskRecord.split(" \\| ");
                if (taskParts[0].equals(code)) {
                    isFound = true;
                } else {
                    String newLine = code + " | ";
                    newLine = taskParts[0] + " | " + taskParts[1] + " | " + taskParts[2] + " | " + taskParts[3];
                    lines.add(newLine);
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("disini?");
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e);
            return false;
        }

        return isFound;
    }

    public ArrayList<Object[]> getAllTasks() {
        return getAllTasks("");
    }

    public ArrayList<Object[]> getAllTasks(String search) {
        ArrayList<Object[]> isi = new ArrayList<>();
        File file = new File("toDoListData.txt");

        if (!file.exists()) {
            System.out.println("Belum ada data tersimpan.");
            return isi;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String taskRecord = fileScanner.nextLine();
                String[] taskParts = taskRecord.split(" \\| ");

                if (taskParts.length >= 4) {
                    Object[] task = new Object[] { 0, taskParts[0], taskParts[1], taskParts[2], taskParts[3] };

                    // Jika search kosong, langsung tambahkan semua
                    if (search.isEmpty()) {
                        isi.add(task);
                    } else {
                        boolean matchFound = false;
                        for (String part : taskParts) {
                            if (part.matches("(?i).*" + search + ".*")) { // (?i) = case-insensitive
                                matchFound = true;
                                break;
                            }
                        }

                        if (matchFound) {
                            isi.add(task);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan.");
        }

        return isi;
    }

    public Object[][] sortingByTanggal(ArrayList<Object[]> isi) {
        isi.sort((a, b) -> {
            String tanggalA = (String) a[3];
            String tanggalB = (String) b[3];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate dateA = null;
            LocalDate dateB = null;

            try {
                dateA = LocalDate.parse(tanggalA, formatter); // Coba dengan format kedua (dd/MM/yyyy)
            } catch (DateTimeParseException e2) {
                e2.printStackTrace(); // Jika masih gagal, print error untuk debugging
            }

            try {
                dateB = LocalDate.parse(tanggalB, formatter); // Coba dengan format kedua (dd/MM/yyyy)
            } catch (DateTimeParseException e2) {
                e2.printStackTrace(); // Jika masih gagal, print error untuk debugging
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
        File file = new File("toDoListData.txt");
        int maxIdx = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" \\| ");
                int idx = Integer.parseInt(parts[0].split("X")[1]);
                maxIdx = Math.max(maxIdx, idx);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }

        return maxIdx + 1;
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
