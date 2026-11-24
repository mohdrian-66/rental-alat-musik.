package com.rentalmusik.pembayaran;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PembayaranGagalException;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.InputHelper;

/**
 * Implementasi metode pembayaran Tunai di tempat.
 * Meminta nominal uang tunai yang diserahkan dan menghitung kembalian secara interaktif.
 *
 * @author Kelompok 4
 */
public class Tunai implements Pembayaran {

    @Override
    public String getNama() {
        return "Tunai";
    }

    @Override
    public String getKode() {
        return "TUNAI";
    }

    @Override
    public double getBiayaAdmin() {
        return 0.0;
    }

    @Override
    public void prosesPembayaran(double jumlah) throws PembayaranGagalException {
        System.out.println("--- PEMBAYARAN: TUNAI ---");
        System.out.println("Total tagihan : " + FormatHelper.rupiah(jumlah));

        while (true) {
            int diterima;
            try {
                diterima = InputHelper.bacaInt("Masukkan uang diterima (0 untuk batal): Rp");
            } catch (InputTidakValidException e) {
                System.out.println("[GAGAL] Input harus berupa angka bulat.");
                continue;
            }

            if (diterima == 0) {
                throw new PembayaranGagalException("Pembayaran tunai dibatalkan.");
            }

            if (diterima < jumlah) {
                double selisih = jumlah - diterima;
                System.out.println("[GAGAL] Uang kurang " + FormatHelper.rupiah(selisih) + ". Coba lagi.");
            } else {
                double kembalian = diterima - jumlah;
                System.out.println("[OK] Pembayaran tunai diterima. Kembalian: " + FormatHelper.rupiah(kembalian) + ".");
                break;
            }
        }
    }
}
