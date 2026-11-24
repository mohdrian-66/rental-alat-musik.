package com.rentalmusik.model;

import com.rentalmusik.exception.TransaksiTidakValidException;
import com.rentalmusik.pembayaran.Pembayaran;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.Konstanta;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Entitas yang merepresentasikan transaksi peminjaman alat musik.
 * Mengelola siklus status, perhitungan subtotal, biaya admin, denda, dan total akhir.
 *
 * @author Kelompok 4
 */
public class Transaksi {

    private String id;
    private Penyewa penyewa;
    private ArrayList<ItemSewa> items;
    private int lamaSewa;
    private LocalDate tanggalDibuat;
    private LocalDate tanggalSewa;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private StatusTransaksi status;
    private Pembayaran pembayaran;

    /**
     * Membuat objek Transaksi baru dengan status awal MENUNGGU.
     *
     * @param id kode transaksi (misal "TRX-0001")
     * @param penyewa akun penyewa pemilik transaksi
     * @param items daftar snapshot item yang disewa
     * @param lamaSewa durasi peminjaman dalam hari
     * @param tanggalDibuat tanggal transaksi diajukan
     * @param pembayaran metode pembayaran yang digunakan
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<ItemSewa> items, int lamaSewa,
                     LocalDate tanggalDibuat, Pembayaran pembayaran) {
        this.id = id;
        this.penyewa = penyewa;
        this.items = items != null ? items : new ArrayList<>();
        this.lamaSewa = lamaSewa;
        this.tanggalDibuat = tanggalDibuat;
        this.pembayaran = pembayaran;
        this.status = StatusTransaksi.MENUNGGU;
        this.tanggalSewa = null;
        this.tanggalJatuhTempo = null;
        this.tanggalKembali = null;
    }

    /**
     * Membuat objek Transaksi dari data persisten (semua parameter terdefinisi).
     *
     * @param id kode transaksi
     * @param penyewa akun penyewa
     * @param items daftar snapshot item
     * @param lamaSewa durasi sewa
     * @param tanggalDibuat tanggal pembuatan
     * @param tanggalSewa tanggal mulai sewa
     * @param tanggalJatuhTempo tanggal batas pengembalian
     * @param tanggalKembali tanggal pengembalian aktual
     * @param status status terkini transaksi
     * @param pembayaran metode pembayaran
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<ItemSewa> items, int lamaSewa,
                     LocalDate tanggalDibuat, LocalDate tanggalSewa, LocalDate tanggalJatuhTempo,
                     LocalDate tanggalKembali, StatusTransaksi status, Pembayaran pembayaran) {
        this.id = id;
        this.penyewa = penyewa;
        this.items = items != null ? items : new ArrayList<>();
        this.lamaSewa = lamaSewa;
        this.tanggalDibuat = tanggalDibuat;
        this.tanggalSewa = tanggalSewa;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.pembayaran = pembayaran;
    }

    /**
     * Menghitung subtotal biaya sewa seluruh barang dalam transaksi.
     *
     * @return subtotal sewa
     */
    public double hitungSubtotal() {
        double subtotal = 0.0;
        for (ItemSewa item : items) {
            subtotal += item.getBiayaTotal();
        }
        return subtotal;
    }

    /**
     * Menghitung besaran biaya administrasi dari metode pembayaran transaksi.
     *
     * @return biaya administrasi
     */
    public double hitungBiayaAdmin() {
        if (pembayaran != null) {
            return pembayaran.getBiayaAdmin();
        }
        return 0.0;
    }

    /**
     * Menghitung total tagihan awal saat transaksi dibuat (subtotal + biaya admin).
     *
     * @return total tagihan
     */
    public double hitungTotal() {
        return hitungSubtotal() + hitungBiayaAdmin();
    }

    /**
     * Menghitung selisih hari keterlambatan pengembalian dari tanggal jatuh tempo.
     * Mengembalikan 0 jika belum dikembalikan atau tepat waktu.
     *
     * @return jumlah hari terlambat (minimal 0)
     */
    public int hitungHariTerlambat() {
        if (tanggalKembali == null || tanggalJatuhTempo == null) {
            return 0;
        }
        long selisih = ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalKembali);
        return (int) Math.max(0, selisih);
    }

    /**
     * Menghitung total denda keterlambatan (hariTerlambat x jumlahBarang x Rp20.000).
     *
     * @return total denda
     */
    public double hitungDenda() {
        int hariTerlambat = hitungHariTerlambat();
        return (double) hariTerlambat * items.size() * Konstanta.DENDA_PER_BARANG_PER_HARI;
    }

    /**
     * Menghitung total pembayaran akhir (total sewa + total denda).
     *
     * @return total akhir
     */
    public double hitungTotalAkhir() {
        return hitungTotal() + hitungDenda();
    }

    /**
     * Mengubah status transaksi sesuai aturan transisi siklus transaksi (BR-15).
     *
     * @param baru status transaksi tujuan
     * @throws TransaksiTidakValidException jika perubahan status melanggar aturan bisnis
     */
    public void ubahStatus(StatusTransaksi baru) throws TransaksiTidakValidException {
        if (this.status == baru) {
            return;
        }

        boolean valid = switch (this.status) {
            case MENUNGGU -> baru == StatusTransaksi.DISEWA
                    || baru == StatusTransaksi.DITOLAK
                    || baru == StatusTransaksi.DIBATALKAN;
            case DISEWA -> baru == StatusTransaksi.SELESAI;
            case SELESAI, DITOLAK, DIBATALKAN -> false;
        };

        if (!valid) {
            throw new TransaksiTidakValidException(
                    "Transisi status dari " + this.status.getNamaTampil()
                            + " ke " + baru.getNamaTampil() + " tidak valid.");
        }

        this.status = baru;
    }

    /**
     * Memulai masa sewa ketika admin menyetujui transaksi.
     * Status diubah menjadi DISEWA dan tanggal sewa serta jatuh tempo dihitung.
     *
     * @param tanggal tanggal mulai sewa
     * @throws TransaksiTidakValidException jika status awal bukan MENUNGGU
     */
    public void mulaiSewa(LocalDate tanggal) throws TransaksiTidakValidException {
        ubahStatus(StatusTransaksi.DISEWA);
        this.tanggalSewa = tanggal;
        this.tanggalJatuhTempo = tanggal.plusDays(this.lamaSewa);
    }

    /**
     * Menyelesaikan transaksi ketika pengembalian barang dikonfirmasi oleh admin.
     *
     * @param tanggal tanggal pengembalian barang aktual
     * @throws TransaksiTidakValidException jika tanggal kembali sebelum tanggal sewa atau status bukan DISEWA
     */
    public void selesaikan(LocalDate tanggal) throws TransaksiTidakValidException {
        if (this.tanggalSewa != null && tanggal.isBefore(this.tanggalSewa)) {
            throw new TransaksiTidakValidException(
                    "Tanggal kembali tidak boleh sebelum tanggal sewa ("
                            + FormatHelper.tanggal(this.tanggalSewa) + ").");
        }
        ubahStatus(StatusTransaksi.SELESAI);
        this.tanggalKembali = tanggal;
    }

    public String getId() {
        return id;
    }

    public Penyewa getPenyewa() {
        return penyewa;
    }

    public void setPenyewa(Penyewa penyewa) {
        this.penyewa = penyewa;
    }

    public ArrayList<ItemSewa> getItems() {
        return items;
    }

    public int getLamaSewa() {
        return lamaSewa;
    }

    public LocalDate getTanggalDibuat() {
        return tanggalDibuat;
    }

    public LocalDate getTanggalSewa() {
        return tanggalSewa;
    }

    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public StatusTransaksi getStatus() {
        return status;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }
}
