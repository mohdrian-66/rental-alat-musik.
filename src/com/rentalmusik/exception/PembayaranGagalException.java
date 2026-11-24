package com.rentalmusik.exception;

/**
 * Dilempar ketika proses pembayaran transaksi dibatalkan atau gagal diproses.
 *
 * @author Kelompok 4
 */
public class PembayaranGagalException extends RentalException {

    /**
     * Membuat instance PembayaranGagalException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan pembayaran
     */
    public PembayaranGagalException(String pesan) {
        super(pesan);
    }
}
