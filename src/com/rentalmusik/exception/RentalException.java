package com.rentalmusik.exception;

/**
 * Kelas dasar checked exception untuk seluruh exception pada sistem rental alat musik.
 *
 * @author Kelompok 4
 */
public class RentalException extends Exception {

    /**
     * Membuat instance RentalException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan
     */
    public RentalException(String pesan) {
        super(pesan);
    }

    /**
     * Membuat instance RentalException dengan pesan detail dan penyebab tertentu.
     *
     * @param pesan pesan kesalahan
     * @param penyebab penyebab exception
     */
    public RentalException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
