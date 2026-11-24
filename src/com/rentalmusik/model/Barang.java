package com.rentalmusik.model;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.util.Konstanta;

/**
 * Kelas abstrak dasar yang merepresentasikan sebuah alat musik dalam sistem rental.
 * Menerapkan prinsip Abstraction dan Encapsulation.
 *
 * @author Kelompok 4
 */
public abstract class Barang {

    private String id;
    private String nama;
    private double tarifHarian;
    private boolean tersedia;

    /**
     * Konstruktor default untuk kelas turunan.
     */
    protected Barang() {
    }

    /**
     * Konstruktor parameter lengkap untuk inisialisasi objek Barang.
     *
     * @param id kode pengenal unik barang
     * @param nama nama alat musik
     * @param tarifHarian tarif sewa per hari
     * @param tersedia status ketersediaan barang (true jika tersedia)
     * @throws InputTidakValidException jika validasi nama atau tarif gagal
     */
    public Barang(String id, String nama, double tarifHarian, boolean tersedia)
            throws InputTidakValidException {
        this.id = id;
        setNama(nama);
        setTarifHarian(tarifHarian);
        this.tersedia = tersedia;
    }

    /**
     * Menghitung biaya dasar sewa barang sebelum biaya tambahan spesifik kategori.
     *
     * @param hari durasi peminjaman dalam hari
     * @return biaya sewa dasar (tarifHarian x hari)
     */
    protected double hitungBiayaDasar(int hari) {
        return this.tarifHarian * hari;
    }

    /**
     * Menghitung total biaya sewa untuk durasi hari tertentu secara polimorfik.
     * Masing-masing subclass menambahkan biaya tambahan khas kategorinya.
     *
     * @param hari durasi hari sewa
     * @return total biaya sewa barang untuk durasi tersebut
     */
    public abstract double hitungBiaya(int hari);

    /**
     * Mengembalikan kategori dari alat musik ini.
     *
     * @return enum {@link KategoriBarang}
     */
    public abstract KategoriBarang getKategori();

    /**
     * Mengembalikan nilai nominal biaya tambahan flat per transaksi untuk barang ini.
     *
     * @return nominal biaya tambahan
     */
    public abstract double getBiayaTambahan();

    /**
     * Mengubah nominal biaya tambahan untuk barang ini dengan validasi rentang.
     *
     * @param biaya nominal biaya tambahan baru
     * @throws InputTidakValidException jika biaya di luar rentang Rp0 - Rp10.000.000
     */
    public abstract void setBiayaTambahan(double biaya) throws InputTidakValidException;

    /**
     * Mengembalikan nama komponen biaya tambahan khas kategori barang ini.
     *
     * @return nama jenis biaya tambahan
     */
    public abstract String getNamaBiayaTambahan();

    /**
     * Mengembalikan ID barang.
     *
     * @return ID barang
     */
    public String getId() {
        return id;
    }

    /**
     * Mengatur ID barang.
     *
     * @param id ID barang
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Mengembalikan nama barang.
     *
     * @return nama alat musik
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengatur nama barang dengan validasi panjang dan karakter terlarang.
     *
     * @param nama nama baru
     * @throws InputTidakValidException jika nama kosong, melebihi 30 karakter, atau mengandung karakter ilegal
     */
    public void setNama(String nama) throws InputTidakValidException {
        if (nama == null) {
            throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
        }
        String trimmed = nama.trim();
        if (trimmed.isEmpty() || trimmed.length() > Konstanta.PANJANG_NAMA_MAKS) {
            throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
        }
        if (trimmed.contains("|") || trimmed.contains(";") || trimmed.contains("~")) {
            throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
        }
        this.nama = trimmed;
    }

    /**
     * Mengembalikan tarif sewa per hari.
     *
     * @return tarif harian
     */
    public double getTarifHarian() {
        return tarifHarian;
    }

    /**
     * Mengatur tarif harian dengan validasi rentang nilai Rp1.000 hingga Rp10.000.000.
     *
     * @param tarifHarian tarif harian baru
     * @throws InputTidakValidException jika tarif di luar rentang
     */
    public void setTarifHarian(double tarifHarian) throws InputTidakValidException {
        if (tarifHarian < Konstanta.TARIF_MIN || tarifHarian > Konstanta.TARIF_MAKS) {
            throw new InputTidakValidException("Tarif harian harus antara Rp1.000 dan Rp10.000.000.");
        }
        this.tarifHarian = tarifHarian;
    }

    /**
     * Memeriksa apakah barang berstatus tersedia untuk disewa.
     *
     * @return true jika tersedia, false jika sedang disewa
     */
    public boolean isTersedia() {
        return tersedia;
    }

    /**
     * Memeriksa apakah barang tersedia (alias method sesuai konvensi bean).
     *
     * @return true jika tersedia
     */
    public boolean getTersedia() {
        return tersedia;
    }

    /**
     * Mengatur status ketersediaan barang.
     *
     * @param tersedia true untuk tersedia, false untuk sedang disewa
     */
    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }
}
