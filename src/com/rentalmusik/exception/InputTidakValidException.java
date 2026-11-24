package com.rentalmusik.exception;

/**
 * Dilempar ketika input pengguna melanggar aturan format, rentang nilai, atau keunikan data.
 *
 * @author Kelompok 4
 */
public class InputTidakValidException extends RentalException {

    /**
     * Membuat instance InputTidakValidException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan validasi input
     */
    public InputTidakValidException(String pesan) {
        super(pesan);
    }
}
