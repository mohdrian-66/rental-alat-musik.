package com.rentalmusik.model;

import com.rentalmusik.exception.BarangTidakDitemukanException;
import com.rentalmusik.exception.BarangTidakTersediaException;
import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PenyimpananException;
import com.rentalmusik.exception.TransaksiTidakValidException;
import com.rentalmusik.service.DaftarBarang;
import com.rentalmusik.service.DaftarTransaksi;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Kelas representasi akun Administrator sistem rental.
 * Mengelola katalog barang, persetujuan dan penolakan transaksi, serta konfirmasi pengembalian.
 *
 * @author Kelompok 4
 */
public class Admin extends Akun {

    private DaftarBarang daftarBarang;
    private DaftarTransaksi daftarTransaksi;

    /**
     * Membuat objek Admin baru dengan kredensial dan dependensi service.
     *
     * @param id ID akun admin
     * @param username nama pengguna admin
     * @param password kata sandi admin
     * @param nama nama tampilan admin
     * @param daftarBarang service katalog barang
     * @param daftarTransaksi service transaksi
     */
    public Admin(String id, String username, String password, String nama,
                 DaftarBarang daftarBarang, DaftarTransaksi daftarTransaksi) {
        super(id, username, password, nama);
        this.daftarBarang = daftarBarang;
        this.daftarTransaksi = daftarTransaksi;
    }

    @Override
    public String getPeran() {
        return "Admin";
    }

    /**
     * Mengatur service katalog barang.
     *
     * @param daftarBarang service barang
     */
    public void setDaftarBarang(DaftarBarang daftarBarang) {
        this.daftarBarang = daftarBarang;
    }

    /**
     * Mengatur service daftar transaksi.
     *
     * @param daftarTransaksi service transaksi
     */
    public void setDaftarTransaksi(DaftarTransaksi daftarTransaksi) {
        this.daftarTransaksi = daftarTransaksi;
    }

    /**
     * Mengembalikan service katalog barang.
     *
     * @return service daftarBarang
     */
    public DaftarBarang getDaftarBarang() {
        return daftarBarang;
    }

    /**
     * Mengembalikan service transaksi.
     *
     * @return service daftarTransaksi
     */
    public DaftarTransaksi getDaftarTransaksi() {
        return daftarTransaksi;
    }

    /**
     * Menambahkan barang baru ke dalam katalog sistem.
     *
     * @param b objek barang baru
     * @throws InputTidakValidException jika validasi nama atau data barang gagal
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void tambahBarang(Barang b) throws InputTidakValidException, PenyimpananException {
        daftarBarang.tambahBarang(b);
    }

    /**
     * Mengedit atribut barang yang sudah ada dalam katalog.
     *
     * @param id ID barang yang akan diubah
     * @param nama nama baru (atau null jika tidak diubah)
     * @param tarif tarif harian baru (atau null jika tidak diubah)
     * @param biaya biaya tambahan baru (atau null jika tidak diubah)
     * @throws BarangTidakDitemukanException jika barang tidak ditemukan
     * @throws InputTidakValidException jika data baru tidak valid
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void editBarang(String id, String nama, Double tarif, Double biaya)
            throws BarangTidakDitemukanException, InputTidakValidException, PenyimpananException {
        daftarBarang.editBarang(id, nama, tarif, biaya);
    }

    /**
     * Menghapus barang dari katalog sesuai aturan bisnis BR-16 (hanya jika barang tersedia dan tidak diajukan).
     *
     * @param id ID barang yang akan dihapus
     * @throws BarangTidakDitemukanException jika barang tidak ditemukan
     * @throws BarangTidakTersediaException jika barang sedang disewa atau diajukan dalam transaksi
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void hapusBarang(String id)
            throws BarangTidakDitemukanException, BarangTidakTersediaException, PenyimpananException {
        Barang b = daftarBarang.cariById(id);
        if (b == null) {
            throw new BarangTidakDitemukanException("[GAGAL] Barang dengan ID " + id + " tidak ditemukan.");
        }
        if (!b.isTersedia() || daftarTransaksi.adaBarangDiajukan(id)) {
            throw new BarangTidakTersediaException(
                    "[GAGAL] Barang " + b.getNama() + " tidak dapat dihapus karena sedang disewa atau diajukan dalam transaksi.");
        }
        daftarBarang.hapusBarang(id);
    }

    /**
     * Mengembalikan daftar seluruh transaksi yang berstatus MENUNGGU.
     *
     * @return daftar transaksi menunggu persetujuan
     */
    public ArrayList<Transaksi> lihatTransaksiMenunggu() {
        return daftarTransaksi.filterStatus(StatusTransaksi.MENUNGGU);
    }

    /**
     * Menerima transaksi penyewaan berstatus MENUNGGU.
     * Mengunci ketersediaan barang menjadi tidak tersedia, memulai masa sewa, dan mencetak invoice sewa.
     *
     * @param t transaksi yang akan diterima
     * @throws BarangTidakTersediaException jika ada barang yang sudah tidak tersedia
     * @throws TransaksiTidakValidException jika status transaksi tidak valid
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void terimaTransaksi(Transaksi t)
            throws BarangTidakTersediaException, TransaksiTidakValidException, PenyimpananException {
        if (t == null) {
            throw new TransaksiTidakValidException("[GAGAL] Objek transaksi kosong.");
        }

        // Periksa tiap barang masih ada dan tersedia
        for (ItemSewa item : t.getItems()) {
            Barang b = daftarBarang.cariById(item.getIdBarang());
            if (b == null || !b.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "[GAGAL] Barang " + item.getNamaBarang()
                                + " sudah tidak tersedia. Transaksi tidak dapat diterima; silakan tolak transaksi.");
            }
        }

        // Kunci ketersediaan barang
        for (ItemSewa item : t.getItems()) {
            Barang b = daftarBarang.cariById(item.getIdBarang());
            if (b != null) {
                b.setTersedia(false);
            }
        }

        t.mulaiSewa(LocalDate.now());
        daftarBarang.simpan();
        daftarTransaksi.simpan();

        new Invoice(t).cetakInvoice();
    }

    /**
     * Menolak transaksi yang berstatus MENUNGGU.
     *
     * @param t transaksi yang ditolak
     * @throws TransaksiTidakValidException jika status awal bukan MENUNGGU
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void tolakTransaksi(Transaksi t) throws TransaksiTidakValidException, PenyimpananException {
        if (t == null) {
            throw new TransaksiTidakValidException("[GAGAL] Objek transaksi kosong.");
        }
        t.ubahStatus(StatusTransaksi.DITOLAK);
        daftarTransaksi.simpan();
    }

    /**
     * Mengonfirmasi pengembalian barang untuk transaksi berstatus DISEWA.
     * Mengembalikan status barang menjadi tersedia, mencatat tanggal kembali, dan mencetak invoice akhir.
     *
     * @param t transaksi yang dikembalikan
     * @param tglKembali tanggal pengembalian barang aktual
     * @throws TransaksiTidakValidException jika tanggal kembali sebelum tanggal sewa atau status tidak valid
     * @throws PenyimpananException jika penyimpanan file gagal
     */
    public void konfirmasiKembali(Transaksi t, LocalDate tglKembali)
            throws TransaksiTidakValidException, PenyimpananException {
        if (t == null) {
            throw new TransaksiTidakValidException("[GAGAL] Objek transaksi kosong.");
        }

        t.selesaikan(tglKembali);

        // Kembalikan barang menjadi tersedia
        for (ItemSewa item : t.getItems()) {
            Barang b = daftarBarang.cariById(item.getIdBarang());
            if (b != null) {
                b.setTersedia(true);
            }
        }

        daftarBarang.simpan();
        daftarTransaksi.simpan();

        new Invoice(t).cetakInvoice();
    }

    /**
     * Mengembalikan seluruh riwayat transaksi yang tercatat dalam sistem.
     *
     * @return daftar seluruh transaksi
     */
    public ArrayList<Transaksi> lihatSemuaTransaksi() {
        return daftarTransaksi.getDaftar();
    }
}
