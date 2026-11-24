package com.rentalmusik.exception;

/**
 * Dilempar ketika barang sedang disewa, diajukan dalam transaksi, atau tidak berstatus tersedia
 * untuk disewa/dihapus/diterima.
 *
 * @author Kelompok 4
 */
public class BarangTidakTersediaException extends RentalException {

    /**
     * Membuat instance BarangTidakTersediaException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan ketersediaan barang
     */
    public BarangTidakTersediaException(String pesan) {
        super(pesan);
    }
}
