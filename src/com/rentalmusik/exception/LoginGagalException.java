package com.rentalmusik.exception;

/**
 * Dilempar ketika proses login pengguna gagal karena kredensial tidak cocok atau peran salah.
 *
 * @author Kelompok 4
 */
public class LoginGagalException extends RentalException {

    /**
     * Membuat instance LoginGagalException dengan pesan detail tertentu.
     *
     * @param pesan pesan kesalahan login
     */
    public LoginGagalException(String pesan) {
        super(pesan);
    }
}
