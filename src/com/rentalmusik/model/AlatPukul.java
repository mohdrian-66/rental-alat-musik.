package com.rentalmusik.model;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.util.Konstanta;

/**
 * Kelas representasi alat musik kategori Pukul / Perkusi (Drum Set, Cajon, dll).
 * Memiliki biaya tambahan flat berupa Biaya Angkut/Setup.
 *
 * @author Kelompok 4
 */
public class AlatPukul extends Barang {

    private double biayaAngkutSetup;

    /**
     * Membuat objek AlatPukul baru dengan biaya angkut/setup default.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatPukul(String id, String nama, double tarifHarian, boolean tersedia)
            throws InputTidakValidException {
        this(id, nama, tarifHarian, Konstanta.BIAYA_ANGKUT_SETUP_DEFAULT, tersedia);
    }

    /**
     * Membuat objek AlatPukul baru dengan biaya angkut/setup khusus.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param biayaAngkutSetup nominal biaya angkut/setup
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatPukul(String id, String nama, double tarifHarian, double biayaAngkutSetup, boolean tersedia)
            throws InputTidakValidException {
        super(id, nama, tarifHarian, tersedia);
        setBiayaTambahan(biayaAngkutSetup);
    }

    @Override
    public double hitungBiaya(int hari) {
        return hitungBiayaDasar(hari) + this.biayaAngkutSetup;
    }

    @Override
    public KategoriBarang getKategori() {
        return KategoriBarang.PUKUL;
    }

    @Override
    public double getBiayaTambahan() {
        return this.biayaAngkutSetup;
    }

    @Override
    public void setBiayaTambahan(double biaya) throws InputTidakValidException {
        if (biaya < 0 || biaya > Konstanta.BIAYA_TAMBAHAN_MAKS) {
            throw new InputTidakValidException("Biaya tambahan harus antara Rp0 dan Rp10.000.000.");
        }
        this.biayaAngkutSetup = biaya;
    }

    @Override
    public String getNamaBiayaTambahan() {
        return "Biaya Angkut/Setup";
    }
}
