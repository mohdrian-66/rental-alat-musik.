package com.rentalmusik.model;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.util.Konstanta;

/**
 * Kelas representasi alat musik kategori Petik (Gitar, Bass, Ukulele, dll).
 * Memiliki biaya tambahan flat berupa Biaya Perawatan Senar.
 *
 * @author Kelompok 4
 */
public class AlatPetik extends Barang {

    private double biayaPerawatanSenar;

    /**
     * Membuat objek AlatPetik baru dengan biaya perawatan senar default.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatPetik(String id, String nama, double tarifHarian, boolean tersedia)
            throws InputTidakValidException {
        this(id, nama, tarifHarian, Konstanta.BIAYA_PERAWATAN_SENAR_DEFAULT, tersedia);
    }

    /**
     * Membuat objek AlatPetik baru dengan biaya perawatan senar khusus.
     *
     * @param id kode barang
     * @param nama nama alat musik
     * @param tarifHarian tarif harian
     * @param biayaPerawatanSenar nominal biaya perawatan senar
     * @param tersedia status ketersediaan
     * @throws InputTidakValidException jika validasi nilai gagal
     */
    public AlatPetik(String id, String nama, double tarifHarian, double biayaPerawatanSenar, boolean tersedia)
            throws InputTidakValidException {
        super(id, nama, tarifHarian, tersedia);
        setBiayaTambahan(biayaPerawatanSenar);
    }

    @Override
    public double hitungBiaya(int hari) {
        return hitungBiayaDasar(hari) + this.biayaPerawatanSenar;
    }

    @Override
    public KategoriBarang getKategori() {
        return KategoriBarang.PETIK;
    }

    @Override
    public double getBiayaTambahan() {
        return this.biayaPerawatanSenar;
    }

    @Override
    public void setBiayaTambahan(double biaya) throws InputTidakValidException {
        if (biaya < 0 || biaya > Konstanta.BIAYA_TAMBAHAN_MAKS) {
            throw new InputTidakValidException("Biaya tambahan harus antara Rp0 dan Rp10.000.000.");
        }
        this.biayaPerawatanSenar = biaya;
    }

    @Override
    public String getNamaBiayaTambahan() {
        return "Biaya Perawatan Senar";
    }
}
