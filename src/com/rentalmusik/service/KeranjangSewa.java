package com.rentalmusik.service;

import com.rentalmusik.exception.BarangTidakDitemukanException;
import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.model.Barang;
import java.util.ArrayList;

/**
 * Service pengelola keranjang belanja sewa penyewa di memori.
 * Data keranjang tidak disimpan ke file (hilang saat aplikasi ditutup).
 *
 * @author Kelompok 4
 */
public class KeranjangSewa {

    private final ArrayList<Barang> barang;

    /**
     * Membuat objek KeranjangSewa kosong.
     */
    public KeranjangSewa() {
        this.barang = new ArrayList<>();
    }

    /**
     * Menambahkan barang ke keranjang sewa dengan pencegahan duplikasi item.
     *
     * @param b objek barang yang ditambahkan
     * @throws InputTidakValidException jika barang sudah ada di keranjang
     */
    public void tambahBarang(Barang b) throws InputTidakValidException {
        if (b == null) {
            return;
        }
        for (Barang item : barang) {
            if (item.getId().equalsIgnoreCase(b.getId())) {
                throw new InputTidakValidException("[GAGAL] Barang " + b.getNama() + " sudah ada di keranjang.");
            }
        }
        this.barang.add(b);
    }

    /**
     * Menghapus barang tertentu dari keranjang berdasarkan ID barang.
     *
     * @param id ID barang yang akan dihapus
     * @throws BarangTidakDitemukanException jika barang tidak ada di keranjang
     */
    public void hapusBarang(String id) throws BarangTidakDitemukanException {
        if (id == null) {
            throw new BarangTidakDitemukanException("[GAGAL] ID barang tidak valid.");
        }
        String idBersih = id.trim().toUpperCase();
        Barang target = null;
        for (Barang item : barang) {
            if (item.getId().equalsIgnoreCase(idBersih)) {
                target = item;
                break;
            }
        }
        if (target == null) {
            throw new BarangTidakDitemukanException("[GAGAL] Barang " + id + " tidak ada di keranjang.");
        }
        this.barang.remove(target);
    }

    /**
     * Mengosongkan seluruh isi keranjang sewa.
     */
    public void kosongkan() {
        this.barang.clear();
    }

    /**
     * Mengembalikan daftar barang yang saat ini berada di keranjang.
     *
     * @return daftar barang di keranjang
     */
    public ArrayList<Barang> getBarang() {
        return this.barang;
    }

    /**
     * Memeriksa apakah keranjang sewa dalam keadaan kosong.
     *
     * @return true jika keranjang kosong
     */
    public boolean isKosong() {
        return this.barang.isEmpty();
    }

    /**
     * Mengembalikan jumlah item dalam keranjang.
     *
     * @return total item di keranjang
     */
    public int getJumlah() {
        return this.barang.size();
    }
}
