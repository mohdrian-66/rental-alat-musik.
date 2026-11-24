package com.rentalmusik.exception;

/**
 * Dilempar ketika terjadi kesalahan I/O saat membaca atau menulis file data penyimpanan.
 *
 * @author Kelompok 4
 */
public class PenyimpananException extends RentalException {

    /**
     * Membuat instance PenyimpananException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan penyimpanan
     */
    public PenyimpananException(String pesan) {
        super(pesan);
    }

    /**
     * Membuat instance PenyimpananException dengan pesan detail dan penyebab terjadinya kesalahan.
     *
     * @param pesan pesan kesalahan penyimpanan
     * @param penyebab Throwable yang menyebabkan exception ini
     */
    public PenyimpananException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
