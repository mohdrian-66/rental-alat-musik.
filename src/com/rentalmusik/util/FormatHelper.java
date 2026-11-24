package com.rentalmusik.util;

import com.rentalmusik.exception.InputTidakValidException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Kelas utilitas untuk membantu pemformatan mata uang, tanggal, perataan teks, dan garis batas.
 *
 * @author Kelompok 4
 */
public final class FormatHelper {

    /** Formatter tanggal dd-MM-yyyy dengan parsing ketat. */
    public static final DateTimeFormatter FORMATTER_TAMPIL = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Formatter tanggal ISO yyyy-MM-dd untuk penyimpanan file. */
    public static final DateTimeFormatter FORMATTER_FILE = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private FormatHelper() {
    }

    /**
     * Memformat nilai nominal rupiah tanpa desimal dengan pemisah ribuan berupa titik.
     * Contoh: Rp20.000, Rp657.500, Rp0.
     *
     * @param nilai nominal numerik yang akan diformat
     * @return string format rupiah
     */
    public static String rupiah(double nilai) {
        long nilaiBulat = Math.round(nilai);
        String formatted = String.format(Locale.US, "%,d", nilaiBulat).replace(',', '.');
        return "Rp" + formatted;
    }

    /**
     * Mengubah objek {@link LocalDate} menjadi string tanggal dengan format dd-MM-yyyy.
     * Jika parameter bernilai null, mengembalikan string tanda hubung ("-").
     *
     * @param tanggal objek tanggal yang akan diformat
     * @return representasi string tanggal atau "-" jika null
     */
    public static String tanggal(LocalDate tanggal) {
        if (tanggal == null) {
            return "-";
        }
        return tanggal.format(FORMATTER_TAMPIL);
    }

    /**
     * Mem-parsing string tanggal dengan format dd-MM-yyyy secara ketat.
     *
     * @param input string tanggal masukan pengguna
     * @return objek {@link LocalDate} hasil parsing
     * @throws InputTidakValidException jika format tidak valid atau tanggal tidak ada pada kalender
     */
    public static LocalDate parseTanggal(String input) throws InputTidakValidException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputTidakValidException("Format tanggal harus dd-MM-yyyy dan tanggal harus valid.");
        }
        try {
            return LocalDate.parse(input.trim(), FORMATTER_TAMPIL);
        } catch (DateTimeParseException e) {
            throw new InputTidakValidException("Format tanggal harus dd-MM-yyyy dan tanggal harus valid.");
        }
    }

    /**
     * Membuat baris karakter berulang sepanjang lebar yang ditentukan.
     *
     * @param c karakter yang akan diulang
     * @param lebar panjang karakter yang dihasilkan
     * @return string garis sepanjang lebar
     */
    public static String garis(char c, int lebar) {
        if (lebar <= 0) {
            return "";
        }
        return String.valueOf(c).repeat(lebar);
    }

    /**
     * Menggabungkan dua string dengan teks kanan diletakkan rata kanan pada kolom selebar total lebar.
     *
     * @param kiri teks sebelah kiri
     * @param kanan teks sebelah kanan
     * @param lebar lebar total kolom
     * @return string terformat rata kanan
     */
    public static String rataKanan(String kiri, String kanan, int lebar) {
        if (kiri == null) {
            kiri = "";
        }
        if (kanan == null) {
            kanan = "";
        }
        int spasi = lebar - kiri.length() - kanan.length();
        if (spasi > 0) {
            return kiri + " ".repeat(spasi) + kanan;
        }
        return kiri + " " + kanan;
    }
}
