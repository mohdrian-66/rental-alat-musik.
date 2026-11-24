package com.rentalmusik.pembayaran;

import com.rentalmusik.exception.PembayaranGagalException;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.InputHelper;
import com.rentalmusik.util.Konstanta;

/**
 * Implementasi metode pembayaran melalui Transfer Bank.
 * Dikenakan biaya administrasi flat sebesar Rp2.500.
 *
 * @author Kelompok 4
 */
public class TransferBank implements Pembayaran {

    @Override
    public String getNama() {
        return "Transfer Bank";
    }

    @Override
    public String getKode() {
        return "TRANSFER";
    }

    @Override
    public double getBiayaAdmin() {
        return Konstanta.BIAYA_ADMIN_TRANSFER;
    }

    @Override
    public void prosesPembayaran(double jumlah) throws PembayaranGagalException {
        System.out.println("--- PEMBAYARAN: TRANSFER BANK ---");
        System.out.println("Jumlah yang harus ditransfer : " + FormatHelper.rupiah(jumlah));
        System.out.println("(sudah termasuk biaya admin " + FormatHelper.rupiah(getBiayaAdmin()) + ")");
        System.out.println("Rekening tujuan              : " + Konstanta.REKENING_TRANSFER);
        InputHelper.bacaEnter("Tekan ENTER setelah transfer dilakukan...");
        System.out.println("[OK] Pembayaran Transfer Bank sebesar " + FormatHelper.rupiah(jumlah) + " diterima.");
    }
}
