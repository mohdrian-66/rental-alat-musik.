package com.rentalmusik.pembayaran;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PembayaranGagalException;

/**
 * Interface untuk strategi metode pembayaran pada sistem rental alat musik.
 *
 * @author Kelompok 4
 */
public interface Pembayaran {

    /**
     * Mengembalikan nama tampilan metode pembayaran.
     *
     * @return nama tampilan metode pembayaran
     */
    String getNama();

    /**
     * Mengembalikan kode unik metode pembayaran untuk penyimpanan file.
     *
     * @return kode pembayaran (TRANSFER, QRIS, atau TUNAI)
     */
    String getKode();

    /**
     * Mengembalikan besaran biaya administrasi untuk metode pembayaran ini.
     *
     * @return nominal biaya admin
     */
    double getBiayaAdmin();

    /**
     * Memproses alur pembayaran sebesar jumlah total tagihan.
     *
     * @param jumlah total tagihan yang harus dibayar
     * @throws PembayaranGagalException jika pembayaran dibatalkan atau gagal diproses
     */
    void prosesPembayaran(double jumlah) throws PembayaranGagalException;

    /**
     * Membuat instance objek Pembayaran yang sesuai berdasarkan kode teks.
     *
     * @param kode kode string pembayaran ("TRANSFER", "QRIS", "TUNAI")
     * @return implementasi objek {@link Pembayaran} yang sesuai
     * @throws InputTidakValidException jika kode pembayaran tidak dikenali
     */
    static Pembayaran dariKode(String kode) throws InputTidakValidException {
        if (kode == null) {
            throw new InputTidakValidException("Kode pembayaran tidak boleh kosong.");
        }
        String kodeBersih = kode.trim().toUpperCase();
        return switch (kodeBersih) {
            case "TRANSFER" -> new TransferBank();
            case "QRIS" -> new QRIS();
            case "TUNAI" -> new Tunai();
            default -> throw new InputTidakValidException("Metode pembayaran '" + kode + "' tidak valid.");
        };
    }
}
