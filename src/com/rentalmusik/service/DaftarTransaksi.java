package com.rentalmusik.service;

import com.rentalmusik.exception.PenyimpananException;
import com.rentalmusik.model.ItemSewa;
import com.rentalmusik.model.Penyewa;
import com.rentalmusik.model.StatusTransaksi;
import com.rentalmusik.model.Transaksi;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Service pengelola data seluruh transaksi penyewaan alat musik.
 * Menjaga urutan transaksi berdasarkan ID dan mengelola counter nomor urut transaksi otomatis.
 *
 * @author Kelompok 4
 */
public class DaftarTransaksi {

    private final ArrayList<Transaksi> daftar;
    private int nomorBerikutnya;
    private final PenyimpananFile penyimpananFile;

    /**
     * Membuat objek DaftarTransaksi, memuat data transaksi dari file, dan menginisialisasi counter.
     *
     * @param penyimpananFile komponen akses penyimpanan file
     * @param penyewaById peta akun penyewa untuk menghubungkan transaksi ke penyewa terkait
     * @throws PenyimpananException jika pembacaan data transaksi atau counter gagal
     */
    public DaftarTransaksi(PenyimpananFile penyimpananFile, HashMap<String, Penyewa> penyewaById)
            throws PenyimpananException {
        this.penyimpananFile = penyimpananFile;
        this.daftar = penyimpananFile.muatTransaksi(penyewaById);
        this.daftar.sort(Comparator.comparing(Transaksi::getId));

        int counterDimuat = penyimpananFile.muatCounter();
        if (counterDimuat <= 0) {
            this.nomorBerikutnya = hitungNomorBerikutnyaDariDaftar();
            this.penyimpananFile.simpanCounter(this.nomorBerikutnya);
        } else {
            this.nomorBerikutnya = counterDimuat;
        }
    }

    private int hitungNomorBerikutnyaDariDaftar() {
        int maks = 0;
        for (Transaksi t : daftar) {
            String id = t.getId().toUpperCase();
            if (id.startsWith("TRX-")) {
                try {
                    int nomor = Integer.parseInt(id.substring("TRX-".length()));
                    if (nomor > maks) {
                        maks = nomor;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return maks + 1;
    }

    /**
     * Mengembalikan nomor transaksi calon berikutnya (misal TRX-0001) tanpa menaikkan counter.
     *
     * @return kode calon transaksi berikutnya
     */
    public String lihatNomorBerikutnya() {
        return String.format("TRX-%04d", nomorBerikutnya);
    }

    /**
     * Menambahkan transaksi baru, menaikkan nilai counter, dan menyimpan perubahan ke file.
     *
     * @param t transaksi baru yang ditambahkan
     * @throws PenyimpananException jika penyimpanan file transaksi atau counter gagal
     */
    public void tambah(Transaksi t) throws PenyimpananException {
        if (t == null) {
            return;
        }
        this.daftar.add(t);
        this.daftar.sort(Comparator.comparing(Transaksi::getId));
        this.nomorBerikutnya++;
        simpan();
    }

    /**
     * Mencari transaksi berdasarkan ID (case-insensitive).
     *
     * @param id kode transaksi
     * @return objek {@link Transaksi} jika ditemukan, null jika tidak ada
     */
    public Transaksi cariById(String id) {
        if (id == null) {
            return null;
        }
        String idBersih = id.trim().toUpperCase();
        for (Transaksi t : daftar) {
            if (t.getId().equalsIgnoreCase(idBersih)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Menyaring transaksi yang memiliki status tertentu.
     *
     * @param s status transaksi yang dicari
     * @return daftar transaksi yang sesuai status
     */
    public ArrayList<Transaksi> filterStatus(StatusTransaksi s) {
        ArrayList<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : daftar) {
            if (t.getStatus() == s) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    /**
     * Menyaring transaksi milik penyewa tertentu.
     *
     * @param p akun penyewa
     * @return daftar transaksi milik penyewa tersebut
     */
    public ArrayList<Transaksi> filterPenyewa(Penyewa p) {
        ArrayList<Transaksi> hasil = new ArrayList<>();
        if (p == null) {
            return hasil;
        }
        for (Transaksi t : daftar) {
            if (t.getPenyewa() != null && t.getPenyewa().getId().equalsIgnoreCase(p.getId())) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    /**
     * Memeriksa apakah suatu barang sedang diajukan dalam transaksi berstatus MENUNGGU.
     * Digunakan saat validasi penghapusan barang (BR-16).
     *
     * @param idBarang ID barang yang diperiksa
     * @return true jika barang ada di transaksi MENUNGGU
     */
    public boolean adaBarangDiajukan(String idBarang) {
        if (idBarang == null) {
            return false;
        }
        String idBersih = idBarang.trim().toUpperCase();
        for (Transaksi t : daftar) {
            if (t.getStatus() == StatusTransaksi.MENUNGGU) {
                for (ItemSewa it : t.getItems()) {
                    if (it.getIdBarang().equalsIgnoreCase(idBersih)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Mengembalikan seluruh koleksi transaksi.
     *
     * @return daftar seluruh transaksi
     */
    public ArrayList<Transaksi> getDaftar() {
        return new ArrayList<>(this.daftar);
    }

    /**
     * Menyimpan seluruh transaksi dan counter ke media file persisten.
     *
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public void simpan() throws PenyimpananException {
        this.penyimpananFile.simpanTransaksi(this.daftar);
        this.penyimpananFile.simpanCounter(this.nomorBerikutnya);
    }
}
