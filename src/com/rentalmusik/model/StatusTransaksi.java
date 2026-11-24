package com.rentalmusik.model;

/**
 * Enumerasi status siklus hidup transaksi penyewaan alat musik.
 *
 * @author Kelompok 4
 */
public enum StatusTransaksi {

    /** Transaksi baru dibuat oleh penyewa dan menunggu persetujuan admin. */
    MENUNGGU("Menunggu"),

    /** Transaksi telah disetujui admin dan barang sedang dalam masa sewa. */
    DISEWA("Disewa"),

    /** Transaksi selesai setelah barang dikembalikan dan denda (bila ada) dilunasi. */
    SELESAI("Selesai"),

    /** Transaksi ditolak oleh admin. */
    DITOLAK("Ditolak"),

    /** Transaksi dibatalkan oleh penyewa selagi masih berstatus MENUNGGU. */
    DIBATALKAN("Dibatalkan");

    private final String namaTampil;

    /**
     * Konstruktor enumerasi StatusTransaksi.
     *
     * @param namaTampil nama tampilan ramah pengguna
     */
    StatusTransaksi(String namaTampil) {
        this.namaTampil = namaTampil;
    }

    /**
     * Mengembalikan teks nama status yang ramah pengguna.
     *
     * @return nama tampilan status
     */
    public String getNamaTampil() {
        return namaTampil;
    }
}
