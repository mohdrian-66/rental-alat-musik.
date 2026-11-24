package com.rentalmusik.exception;

/**
 * Dilempar ketika barang dengan ID tertentu tidak ditemukan pada daftar barang atau keranjang.
 *
 * @author Kelompok 4
 */
public class BarangTidakDitemukanException extends RentalException {

    /**
     * Membuat instance BarangTidakDitemukanException dengan pesan detail tertentu.
     *
     * @param pesan pesan detail kesalahan
     */
    public BarangTidakDitemukanException(String pesan) {
        super(pesan);
    }
}
