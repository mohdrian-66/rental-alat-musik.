package com.rentalmusik.util;

import com.rentalmusik.exception.InputTidakValidException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Kelas utilitas untuk membaca dan memvalidasi input dari konsol melalui objek {@link Scanner} tunggal.
 *
 * @author Kelompok 4
 */
public final class InputHelper {

    /** Objek Scanner tunggal yang membaca dari System.in. */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private InputHelper() {
    }

    /**
     * Membaca satu baris teks mentah dari input pengguna. Menangani akhir stream (EOF).
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return string baris teks yang dimasukkan
     */
    public static String bacaBarisMentah(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        try {
            return SCANNER.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\n[INFO] Input ditutup. Program berhenti.");
            System.exit(0);
            return "";
        }
    }

    /**
     * Membaca string teks dan langsung melakukan pemangkasan spasi (trim).
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return string setelah di-trim
     */
    public static String bacaString(String prompt) {
        return bacaBarisMentah(prompt).trim();
    }

    /**
     * Membaca input password tanpa melakukan pemangkasan spasi (tanpa trim).
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return string password asli
     */
    public static String bacaPassword(String prompt) {
        return bacaBarisMentah(prompt);
    }

    /**
     * Membaca input bilangan bulat (integer).
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return nilai integer hasil pembacaan
     * @throws InputTidakValidException jika input bukan angka bulat
     */
    public static int bacaInt(String prompt) throws InputTidakValidException {
        String baris = bacaString(prompt);
        try {
            return Integer.parseInt(baris);
        } catch (NumberFormatException e) {
            throw new InputTidakValidException("Input harus berupa angka bulat.");
        }
    }

    /**
     * Membaca input bilangan bulat opsional (ENTER kosong diperbolehkan).
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return nilai Integer hasil pembacaan, atau null jika pengguna hanya menekan ENTER
     * @throws InputTidakValidException jika pengguna memasukkan teks bukan angka bulat
     */
    public static Integer bacaIntOpsional(String prompt) throws InputTidakValidException {
        String baris = bacaString(prompt);
        if (baris.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(baris);
        } catch (NumberFormatException e) {
            throw new InputTidakValidException("Input harus berupa angka bulat.");
        }
    }

    /**
     * Membaca bilangan bulat dalam rentang tertentu [min, maks].
     *
     * @param prompt teks petunjuk
     * @param min batas minimal (inklusif)
     * @param maks batas maksimal (inklusif)
     * @return nilai integer dalam rentang yang diminta
     * @throws InputTidakValidException jika bukan angka atau berada di luar rentang
     */
    public static int bacaIntRentang(String prompt, int min, int maks) throws InputTidakValidException {
        int nilai = bacaInt(prompt);
        if (nilai < min || nilai > maks) {
            throw new InputTidakValidException("Nilai harus antara " + min + " dan " + maks + ".");
        }
        return nilai;
    }

    /**
     * Membaca bilangan bulat dalam rentang tertentu [min, maks] dengan pesan kesalahan khusus.
     *
     * @param prompt teks petunjuk
     * @param min batas minimal (inklusif)
     * @param maks batas maksimal (inklusif)
     * @param pesanKesalahanRentang pesan yang ditampilkan jika di luar rentang
     * @return nilai integer dalam rentang yang diminta
     * @throws InputTidakValidException jika bukan angka atau berada di luar rentang
     */
    public static int bacaIntRentang(String prompt, int min, int maks, String pesanKesalahanRentang)
            throws InputTidakValidException {
        int nilai = bacaInt(prompt);
        if (nilai < min || nilai > maks) {
            throw new InputTidakValidException(pesanKesalahanRentang);
        }
        return nilai;
    }

    /**
     * Membaca tanggal dengan format dd-MM-yyyy.
     *
     * @param prompt teks petunjuk untuk pengguna
     * @return objek {@link LocalDate} hasil parsing tanggal
     * @throws InputTidakValidException jika format tidak valid atau tanggal di luar kalender
     */
    public static LocalDate bacaTanggal(String prompt) throws InputTidakValidException {
        String baris = bacaString(prompt);
        return FormatHelper.parseTanggal(baris);
    }

    /**
     * Membaca jawaban konfirmasi pengguna (y/Y untuk ya, n/N untuk tidak).
     *
     * @param prompt teks petunjuk
     * @return true jika pengguna memilih 'y' atau 'Y', false jika memilih 'n' atau 'N'
     * @throws InputTidakValidException jika input selain y, Y, n, N
     */
    public static boolean bacaYaTidak(String prompt) throws InputTidakValidException {
        String baris = bacaString(prompt);
        if ("y".equalsIgnoreCase(baris)) {
            return true;
        } else if ("n".equalsIgnoreCase(baris)) {
            return false;
        } else {
            throw new InputTidakValidException("Jawaban harus y atau n.");
        }
    }

    /**
     * Menunggu penekanan tombol ENTER dari pengguna tanpa memproses isinya.
     *
     * @param prompt teks instruksi yang dicetak sebelum menunggu ENTER
     */
    public static void bacaEnter(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        try {
            SCANNER.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\n[INFO] Input ditutup. Program berhenti.");
            System.exit(0);
        }
    }
}
