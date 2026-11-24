package com.rentalmusik.model;

/**
 * Snapshot data barang sewa pada saat transaksi dibuat (immutable).
 * Perubahan data master barang setelahnya tidak akan mengubah rincian ItemSewa ini.
 *
 * @author Kelompok 4
 */
public final class ItemSewa {

    private final String idBarang;
    private final String namaBarang;
    private final KategoriBarang kategori;
    private final double tarifHarian;
    private final double biayaTambahan;
    private final double biayaTotal;

    /**
     * Membuat snapshot ItemSewa dari objek Barang dan durasi lama sewa.
     * Biaya total dihitung secara polimorfik melalui {@link Barang#hitungBiaya(int)}.
     *
     * @param barang referensi objek Barang yang disewa
     * @param lamaSewa durasi peminjaman dalam hari
     */
    public ItemSewa(Barang barang, int lamaSewa) {
        this.idBarang = barang.getId();
        this.namaBarang = barang.getNama();
        this.kategori = barang.getKategori();
        this.tarifHarian = barang.getTarifHarian();
        this.biayaTambahan = barang.getBiayaTambahan();
        this.biayaTotal = barang.hitungBiaya(lamaSewa);
    }

    /**
     * Membuat snapshot ItemSewa dengan seluruh nilai yang ditentukan secara eksplisit
     * (digunakan saat merekonstruksi data dari file).
     *
     * @param idBarang ID barang
     * @param namaBarang nama barang
     * @param kategori kategori barang
     * @param tarifHarian tarif harian barang
     * @param biayaTambahan biaya tambahan barang
     * @param biayaTotal total biaya sewa item ini
     */
    public ItemSewa(String idBarang, String namaBarang, KategoriBarang kategori,
                    double tarifHarian, double biayaTambahan, double biayaTotal) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.tarifHarian = tarifHarian;
        this.biayaTambahan = biayaTambahan;
        this.biayaTotal = biayaTotal;
    }

    /**
     * Mengembalikan ID barang.
     *
     * @return ID barang
     */
    public String getIdBarang() {
        return idBarang;
    }

    /**
     * Mengembalikan nama barang.
     *
     * @return nama barang
     */
    public String getNamaBarang() {
        return namaBarang;
    }

    /**
     * Mengembalikan kategori barang.
     *
     * @return {@link KategoriBarang}
     */
    public KategoriBarang getKategori() {
        return kategori;
    }

    /**
     * Mengembalikan tarif harian barang.
     *
     * @return tarif harian
     */
    public double getTarifHarian() {
        return tarifHarian;
    }

    /**
     * Mengembalikan biaya tambahan flat barang.
     *
     * @return biaya tambahan
     */
    public double getBiayaTambahan() {
        return biayaTambahan;
    }

    /**
     * Mengembalikan total biaya sewa untuk item ini.
     *
     * @return biaya total item
     */
    public double getBiayaTotal() {
        return biayaTotal;
    }
}
