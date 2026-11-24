package com.rentalmusik.model;

import com.rentalmusik.util.Konstanta;

/**
 * Enumerasi kategori alat musik yang tersedia di sistem rental.
 *
 * @author Kelompok 4
 */
public enum KategoriBarang {

    /** Kategori alat musik petik (contoh: Gitar, Bass, Ukulele). */
    PETIK("Petik", "PTK", "Biaya Perawatan Senar", Konstanta.BIAYA_PERAWATAN_SENAR_DEFAULT),

    /** Kategori alat musik pukul/perkusi (contoh: Drum Set, Cajon, Marakas). */
    PUKUL("Pukul", "PKL", "Biaya Angkut/Setup", Konstanta.BIAYA_ANGKUT_SETUP_DEFAULT),

    /** Kategori alat musik tiup (contoh: Saksofon, Seruling, Harmonika). */
    TIUP("Tiup", "TPU", "Biaya Sanitasi", Konstanta.BIAYA_SANITASI_DEFAULT);

    private final String namaTampil;
    private final String prefixId;
    private final String namaBiayaTambahan;
    private final double biayaTambahanDefault;

    /**
     * Konstruktor enumerasi KategoriBarang.
     *
     * @param namaTampil nama kategori untuk tampilan antarmuka
     * @param prefixId awalan tiga huruf untuk pembuatan ID barang
     * @param namaBiayaTambahan nama jenis biaya tambahan flat untuk kategori ini
     * @param biayaTambahanDefault nilai nominal default biaya tambahan
     */
    KategoriBarang(String namaTampil, String prefixId, String namaBiayaTambahan, double biayaTambahanDefault) {
        this.namaTampil = namaTampil;
        this.prefixId = prefixId;
        this.namaBiayaTambahan = namaBiayaTambahan;
        this.biayaTambahanDefault = biayaTambahanDefault;
    }

    /**
     * Mengembalikan nama tampilan kategori.
     *
     * @return nama tampilan (misal "Petik")
     */
    public String getNamaTampil() {
        return namaTampil;
    }

    /**
     * Mengembalikan awalan ID barang sesuai kategori (PTK, PKL, TPU).
     *
     * @return prefix ID barang
     */
    public String getPrefixId() {
        return prefixId;
    }

    /**
     * Mengembalikan nama komponen biaya tambahan untuk kategori ini.
     *
     * @return nama biaya tambahan
     */
    public String getNamaBiayaTambahan() {
        return namaBiayaTambahan;
    }

    /**
     * Mengembalikan nominal default biaya tambahan untuk kategori ini.
     *
     * @return nominal default biaya tambahan
     */
    public double getBiayaTambahanDefault() {
        return biayaTambahanDefault;
    }
}
