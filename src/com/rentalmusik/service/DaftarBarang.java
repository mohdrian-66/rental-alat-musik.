package com.rentalmusik.service;

import com.rentalmusik.exception.BarangTidakDitemukanException;
import com.rentalmusik.exception.BarangTidakTersediaException;
import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PenyimpananException;
import com.rentalmusik.model.Barang;
import com.rentalmusik.model.KategoriBarang;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Service pengelola data master seluruh alat musik.
 * Menggunakan ArrayList sebagai penyimpanan utama dan HashMap sebagai indeks pencarian cepat berdasarkan ID.
 *
 * @author Kelompok 4
 */
public class DaftarBarang {

    private final ArrayList<Barang> barang;
    private final HashMap<String, Barang> indeksId;
    private final PenyimpananFile penyimpananFile;

    /**
     * Membuat objek DaftarBarang dan memuat data awal dari PenyimpananFile.
     *
     * @param penyimpananFile komponen penyimpanan file
     * @throws PenyimpananException jika pemuatan data awal gagal
     */
    public DaftarBarang(PenyimpananFile penyimpananFile) throws PenyimpananException {
        this.penyimpananFile = penyimpananFile;
        this.barang = new ArrayList<>();
        this.indeksId = new HashMap<>();

        ArrayList<Barang> dimuat = penyimpananFile.muatBarang();
        for (Barang b : dimuat) {
            this.barang.add(b);
            this.indeksId.put(b.getId().toUpperCase(), b);
        }
        urutkan();
    }

    private void urutkan() {
        this.barang.sort(Comparator.comparing(Barang::getId));
    }

    /**
     * Menambahkan barang baru ke dalam sistem dan menyimpannya ke file.
     *
     * @param b objek barang baru
     * @throws InputTidakValidException jika nama barang sudah ada
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void tambahBarang(Barang b) throws InputTidakValidException, PenyimpananException {
        if (b == null) {
            throw new InputTidakValidException("Data barang tidak boleh kosong.");
        }
        for (Barang existing : barang) {
            if (existing.getNama().equalsIgnoreCase(b.getNama())) {
                throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
            }
        }
        this.barang.add(b);
        this.indeksId.put(b.getId().toUpperCase(), b);
        urutkan();
        simpan();
    }

    /**
     * Memperbarui atribut nama, tarif, atau biaya tambahan barang yang ada.
     *
     * @param id ID barang yang diedit
     * @param nama nama baru (null atau kosong jika tidak diubah)
     * @param tarif tarif harian baru (null jika tidak diubah)
     * @param biaya biaya tambahan baru (null jika tidak diubah)
     * @throws BarangTidakDitemukanException jika barang dengan ID tersebut tidak ada
     * @throws InputTidakValidException jika data baru tidak valid atau nama sudah digunakan
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void editBarang(String id, String nama, Double tarif, Double biaya)
            throws BarangTidakDitemukanException, InputTidakValidException, PenyimpananException {
        Barang b = cariById(id);
        if (b == null) {
            throw new BarangTidakDitemukanException("Barang dengan ID " + id + " tidak ditemukan.");
        }

        if (nama != null && !nama.trim().isEmpty()) {
            String namaBaru = nama.trim();
            for (Barang existing : barang) {
                if (!existing.getId().equalsIgnoreCase(b.getId()) && existing.getNama().equalsIgnoreCase(namaBaru)) {
                    throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
                }
            }
            b.setNama(namaBaru);
        }

        if (tarif != null) {
            b.setTarifHarian(tarif);
        }

        if (biaya != null) {
            b.setBiayaTambahan(biaya);
        }

        simpan();
    }

    /**
     * Menghapus barang dari sistem.
     *
     * @param id ID barang yang akan dihapus
     * @throws BarangTidakDitemukanException jika ID tidak ditemukan
     * @throws BarangTidakTersediaException jika barang sedang disewa
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void hapusBarang(String id)
            throws BarangTidakDitemukanException, BarangTidakTersediaException, PenyimpananException {
        Barang b = cariById(id);
        if (b == null) {
            throw new BarangTidakDitemukanException("Barang dengan ID " + id + " tidak ditemukan.");
        }
        if (!b.isTersedia()) {
            throw new BarangTidakTersediaException(
                    "[GAGAL] Barang " + b.getNama() + " tidak dapat dihapus karena sedang disewa atau diajukan dalam transaksi.");
        }
        this.barang.remove(b);
        this.indeksId.remove(b.getId().toUpperCase());
        simpan();
    }

    /**
     * Mencari barang berdasarkan ID dengan pencarian tidak peka huruf besar/kecil (case-insensitive).
     *
     * @param id kode ID barang
     * @return objek {@link Barang} jika ditemukan, atau null jika tidak ada
     */
    public Barang cariById(String id) {
        if (id == null) {
            return null;
        }
        return this.indeksId.get(id.trim().toUpperCase());
    }

    /**
     * Mencari barang berdasarkan potongan nama (contains, case-insensitive).
     *
     * @param kata kata kunci pencarian
     * @return daftar barang yang cocok
     */
    public ArrayList<Barang> cariByNama(String kata) {
        ArrayList<Barang> hasil = new ArrayList<>();
        if (kata == null || kata.trim().isEmpty()) {
            return hasil;
        }
        String lower = kata.trim().toLowerCase();
        for (Barang b : barang) {
            if (b.getNama().toLowerCase().contains(lower)) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Menyaring dan mengembalikan seluruh barang yang saat ini berstatus tersedia.
     *
     * @return daftar barang yang tersedia
     */
    public ArrayList<Barang> filterTersedia() {
        ArrayList<Barang> hasil = new ArrayList<>();
        for (Barang b : barang) {
            if (b.isTersedia()) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Menyaring barang berdasarkan kategori tertentu.
     *
     * @param k kategori barang yang dicari
     * @return daftar barang dalam kategori tersebut
     */
    public ArrayList<Barang> filterKategori(KategoriBarang k) {
        ArrayList<Barang> hasil = new ArrayList<>();
        for (Barang b : barang) {
            if (b.getKategori() == k) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Mengubah status ketersediaan barang dan menyimpannya ke file.
     *
     * @param id ID barang
     * @param tersedia nilai ketersediaan baru
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void ubahKetersediaan(String id, boolean tersedia) throws PenyimpananException {
        Barang b = cariById(id);
        if (b != null) {
            b.setTersedia(tersedia);
            simpan();
        }
    }

    /**
     * Menghasilkan ID unik baru secara otomatis untuk kategori tertentu (misal PTK-005).
     *
     * @param k kategori barang
     * @return ID barang baru berformat 3 digit nomor urut
     */
    public String buatIdBaru(KategoriBarang k) {
        String prefix = k.getPrefixId() + "-";
        int maks = 0;
        for (Barang b : barang) {
            String id = b.getId().toUpperCase();
            if (id.startsWith(prefix)) {
                String nomorStr = id.substring(prefix.length());
                try {
                    int nomor = Integer.parseInt(nomorStr);
                    if (nomor > maks) {
                        maks = nomor;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return prefix + String.format("%03d", maks + 1);
    }

    /**
     * Mengembalikan seluruh koleksi barang dalam sistem.
     *
     * @return daftar seluruh barang
     */
    public ArrayList<Barang> getSemuaBarang() {
        return new ArrayList<>(this.barang);
    }

    /**
     * Menyimpan seluruh daftar barang ke media penyimpanan file.
     *
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public void simpan() throws PenyimpananException {
        this.penyimpananFile.simpanBarang(this.barang);
    }
}
