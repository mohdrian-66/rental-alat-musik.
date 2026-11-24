package com.rentalmusik.model;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.util.Konstanta;

/**
 * Kelas representasi alat musik kategori Tiup (Saksofon, Seruling, Harmonika, dll).
 * Memiliki biaya tambahan flat berupa Biaya Sanitasi Mouthpiece.
 *
 * @author Kelompok 4
 */
public class AlatTiup extends Barang {

    private double biayaSanitasi;

    /**
     * Membuat objek AlatTiup baru dengan biaya sanitasi default.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatTiup(String id, String nama, double tarifHarian, boolean tersedia)
            throws InputTidakValidException {
        this(id, nama, tarifHarian, Konstanta.BIAYA_SANITASI_DEFAULT, tersedia);
    }

    /**
     * Membuat objek AlatTiup baru dengan biaya sanitasi khusus.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param biayaSanitasi nominal biaya sanitasi mouthpiece
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatTiup(String id, String nama, double tarifHarian, double biayaSanitasi, boolean tersedia)
            throws InputTidakValidException {
        super(id, nama, tarifHarian, tersedia);
        setBiayaTambahan(biayaSanitasi);
    }

    @Override
    public double hitungBiaya(int hari) {
        return hitungBiayaDasar(hari) + this.biayaSanitasi;
    }

    @Override
    public KategoriBarang getKategori() {
        return KategoriBarang.TIUP;
    }

    @Override
    public double getBiayaTambahan() {
        return this.biayaSanitasi;
    }

    @Override
    public void setBiayaTambahan(double biaya) throws InputTidakValidException {
        if (biaya < 0 || biaya > Konstanta.BIAYA_TAMBAHAN_MAKS) {
            throw new InputTidakValidException("Biaya tambahan harus antara Rp0 dan Rp10.000.000.");
        }
        this.biayaSanitasi = biaya;
    }

    @Override
    public String getNamaBiayaTambahan() {
        return "Biaya Sanitasi";
    }
}
