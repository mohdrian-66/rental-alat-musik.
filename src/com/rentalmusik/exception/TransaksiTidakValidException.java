package com.rentalmusik.exception;

/**
 * Dilempar ketika transaksi tidak ditemukan, transisi status tidak sah, atau parameter transaksi
 * melanggar aturan bisnis (seperti tanggal kembali sebelum tanggal sewa).
 *
 * @author Kelompok 4
 */
public class TransaksiTidakValidException extends RentalException {

    /**
     * Membuat instance TransaksiTidakValidException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan transaksi
     */
    public TransaksiTidakValidException(String pesan) {
        super(pesan);
    }
}
