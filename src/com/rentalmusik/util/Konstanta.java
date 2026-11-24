package com.rentalmusik.util;

/**
 * Kelas penyimpan nilai-nilai konstanta konfigurasi sistem rental alat musik.
 *
 * @author Kelompok 4
 */
public final class Konstanta {

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private Konstanta() {
    }

    /** Tarif denda per barang per hari keterlambatan (Rp20.000). */
    public static final double DENDA_PER_BARANG_PER_HARI = 20000.0;

    /** Batas minimal lama sewa dalam hari (1 hari). */
    public static final int LAMA_SEWA_MIN = 1;

    /** Batas maksimal lama sewa dalam hari (14 hari). */
    public static final int LAMA_SEWA_MAKS = 14;

    /** Batas maksimal percobaan login yang gagal berturut-turut (3 kali). */
    public static final int MAKS_PERCOBAAN_LOGIN = 3;

    /** Biaya administrasi untuk metode pembayaran Transfer Bank (Rp2.500). */
    public static final double BIAYA_ADMIN_TRANSFER = 2500.0;

    /** Biaya perawatan senar default untuk kategori Petik (Rp5.000). */
    public static final double BIAYA_PERAWATAN_SENAR_DEFAULT = 5000.0;

    /** Biaya angkut/setup default untuk kategori Pukul (Rp10.000). */
    public static final double BIAYA_ANGKUT_SETUP_DEFAULT = 10000.0;

    /** Biaya sanitasi mouthpiece default untuk kategori Tiup (Rp5.000). */
    public static final double BIAYA_SANITASI_DEFAULT = 5000.0;

    /** Batas minimal tarif sewa harian barang (Rp1.000). */
    public static final double TARIF_MIN = 1000.0;

    /** Batas maksimal tarif sewa harian barang (Rp10.000.000). */
    public static final double TARIF_MAKS = 10000000.0;

    /** Batas maksimal biaya tambahan barang (Rp10.000.000). */
    public static final double BIAYA_TAMBAHAN_MAKS = 10000000.0;

    /** Batas panjang maksimal nama barang (30 karakter). */
    public static final int PANJANG_NAMA_MAKS = 30;

    /** Nama folder penyimpanan data teks ("data"). */
    public static final String FOLDER_DATA = "data";

    /** Keterangan nomor rekening untuk pembayaran Transfer Bank. */
    public static final String REKENING_TRANSFER = "Bank Nusantara - 1234567890 a.n. Rental Alat Musik";
}
