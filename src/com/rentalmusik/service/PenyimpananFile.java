package com.rentalmusik.service;

import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PenyimpananException;
import com.rentalmusik.model.AlatPetik;
import com.rentalmusik.model.AlatPukul;
import com.rentalmusik.model.AlatTiup;
import com.rentalmusik.model.Barang;
import com.rentalmusik.model.ItemSewa;
import com.rentalmusik.model.KategoriBarang;
import com.rentalmusik.model.Penyewa;
import com.rentalmusik.model.StatusTransaksi;
import com.rentalmusik.model.Transaksi;
import com.rentalmusik.pembayaran.Pembayaran;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.Konstanta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Komponen penyedia akses baca dan tulis file teks lokal pada folder data/.
 * Menangani persistensi barang, transaksi, dan counter nomor transaksi.
 *
 * @author Kelompok 4
 */
public class PenyimpananFile {

    private final String folderData;
    private final File fileBarang;
    private final File fileTransaksi;
    private final File fileCounter;

    /**
     * Membuat objek PenyimpananFile dengan lokasi folder default ("data").
     */
    public PenyimpananFile() {
        this(Konstanta.FOLDER_DATA);
    }

    /**
     * Membuat objek PenyimpananFile dengan folder kustom.
     *
     * @param folderData nama atau path direktori penyimpanan
     */
    public PenyimpananFile(String folderData) {
        this.folderData = folderData;
        this.fileBarang = new File(folderData, "barang.txt");
        this.fileTransaksi = new File(folderData, "transaksi.txt");
        this.fileCounter = new File(folderData, "counter.txt");
        pastikanFolderAda();
    }

    private void pastikanFolderAda() {
        File folder = new File(folderData);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Memuat daftar data barang dari file data/barang.txt.
     * Jika file belum ada, sistem akan membuat data awal (seed) 12 barang.
     *
     * @return daftar objek {@link Barang}
     * @throws PenyimpananException jika terjadi kesalahan I/O saat membaca/menulis file
     */
    public ArrayList<Barang> muatBarang() throws PenyimpananException {
        pastikanFolderAda();
        if (!fileBarang.exists()) {
            ArrayList<Barang> seed = inisialisasiSeedBarang();
            simpanBarang(seed);
            return seed;
        }

        ArrayList<Barang> daftar = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileBarang), StandardCharsets.UTF_8))) {
            String baris;
            int nomorBaris = 0;
            while ((baris = reader.readLine()) != null) {
                nomorBaris++;
                String trimmed = baris.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] bagian = trimmed.split("\\|", -1);
                if (bagian.length < 6) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: jumlah kolom kurang (%d/6)%n",
                            nomorBaris, fileBarang.getName(), bagian.length);
                    continue;
                }

                String id = bagian[0].trim();
                String kategoriStr = bagian[1].trim();
                String nama = bagian[2].trim();
                String tarifStr = bagian[3].trim();
                String biayaStr = bagian[4].trim();
                String tersediaStr = bagian[5].trim();

                try {
                    KategoriBarang kategori = KategoriBarang.valueOf(kategoriStr.toUpperCase());
                    double tarif = Double.parseDouble(tarifStr);
                    double biaya = Double.parseDouble(biayaStr);
                    boolean tersedia = Boolean.parseBoolean(tersediaStr);

                    Barang barang = switch (kategori) {
                        case PETIK -> new AlatPetik(id, nama, tarif, biaya, tersedia);
                        case PUKUL -> new AlatPukul(id, nama, tarif, biaya, tersedia);
                        case TIUP -> new AlatTiup(id, nama, tarif, biaya, tersedia);
                    };
                    daftar.add(barang);
                } catch (IllegalArgumentException e) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: data kategori/angka tidak valid (%s)%n",
                            nomorBaris, fileBarang.getName(), e.getMessage());
                } catch (InputTidakValidException e) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: validasi data gagal (%s)%n",
                            nomorBaris, fileBarang.getName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal membaca file data barang: " + e.getMessage(), e);
        }

        return daftar;
    }

    /**
     * Menyimpan seluruh daftar barang ke file data/barang.txt.
     *
     * @param list daftar barang yang akan disimpan
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public void simpanBarang(ArrayList<Barang> list) throws PenyimpananException {
        pastikanFolderAda();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileBarang), StandardCharsets.UTF_8))) {
            writer.write("# id|kategori|nama|tarifHarian|biayaTambahan|tersedia");
            writer.newLine();
            for (Barang b : list) {
                long tarifBulat = Math.round(b.getTarifHarian());
                long biayaBulat = Math.round(b.getBiayaTambahan());
                String baris = String.format("%s|%s|%s|%d|%d|%b",
                        b.getId(),
                        b.getKategori().name(),
                        b.getNama(),
                        tarifBulat,
                        biayaBulat,
                        b.isTersedia());
                writer.write(baris);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal menyimpan data barang: " + e.getMessage(), e);
        }
    }

    /**
     * Memuat daftar seluruh transaksi dari file data/transaksi.txt.
     *
     * @param penyewaById peta referensi akun penyewa berdasarkan ID
     * @return daftar objek {@link Transaksi}
     * @throws PenyimpananException jika terjadi kesalahan I/O saat membaca file
     */
    public ArrayList<Transaksi> muatTransaksi(HashMap<String, Penyewa> penyewaById) throws PenyimpananException {
        pastikanFolderAda();
        if (!fileTransaksi.exists()) {
            return new ArrayList<>();
        }

        ArrayList<Transaksi> daftar = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileTransaksi), StandardCharsets.UTF_8))) {
            String baris;
            int nomorBaris = 0;
            while ((baris = reader.readLine()) != null) {
                nomorBaris++;
                String trimmed = baris.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] bagian = trimmed.split("\\|", -1);
                if (bagian.length < 10) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: jumlah kolom kurang (%d/10)%n",
                            nomorBaris, fileTransaksi.getName(), bagian.length);
                    continue;
                }

                String idTrx = bagian[0].trim();
                String idPenyewa = bagian[1].trim();
                String statusStr = bagian[2].trim();
                String lamaSewaStr = bagian[3].trim();
                String tglDibuatStr = bagian[4].trim();
                String tglSewaStr = bagian[5].trim();
                String tglJatuhTempoStr = bagian[6].trim();
                String tglKembaliStr = bagian[7].trim();
                String kodeBayarStr = bagian[8].trim();
                String itemsStr = bagian[9].trim();

                Penyewa penyewa = penyewaById != null ? penyewaById.get(idPenyewa) : null;
                if (penyewa == null) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: idPenyewa '%s' tidak dikenal%n",
                            nomorBaris, fileTransaksi.getName(), idPenyewa);
                    continue;
                }

                try {
                    StatusTransaksi status = StatusTransaksi.valueOf(statusStr.toUpperCase());
                    int lamaSewa = Integer.parseInt(lamaSewaStr);
                    LocalDate tglDibuat = parseTanggalFile(tglDibuatStr);
                    LocalDate tglSewa = parseTanggalFile(tglSewaStr);
                    LocalDate tglJatuhTempo = parseTanggalFile(tglJatuhTempoStr);
                    LocalDate tglKembali = parseTanggalFile(tglKembaliStr);
                    Pembayaran pembayaran = Pembayaran.dariKode(kodeBayarStr);

                    ArrayList<ItemSewa> items = parseItems(itemsStr);
                    if (items.isEmpty()) {
                        System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: rincian items kosong%n",
                                nomorBaris, fileTransaksi.getName());
                        continue;
                    }

                    Transaksi t = new Transaksi(idTrx, penyewa, items, lamaSewa, tglDibuat,
                            tglSewa, tglJatuhTempo, tglKembali, status, pembayaran);
                    daftar.add(t);
                } catch (IllegalArgumentException e) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: format data salah (%s)%n",
                            nomorBaris, fileTransaksi.getName(), e.getMessage());
                } catch (InputTidakValidException e) {
                    System.out.printf("[PERINGATAN] Baris %d di %s diabaikan: metode pembayaran/item tidak valid (%s)%n",
                            nomorBaris, fileTransaksi.getName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal membaca file data transaksi: " + e.getMessage(), e);
        }

        return daftar;
    }

    /**
     * Menyimpan seluruh daftar transaksi ke file data/transaksi.txt.
     *
     * @param list daftar transaksi yang akan disimpan
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public void simpanTransaksi(ArrayList<Transaksi> list) throws PenyimpananException {
        pastikanFolderAda();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileTransaksi), StandardCharsets.UTF_8))) {
            writer.write("# id|idPenyewa|status|lamaSewa|tglDibuat|tglSewa|tglJatuhTempo|tglKembali|kodePembayaran|items");
            writer.newLine();
            for (Transaksi t : list) {
                String idPenyewa = t.getPenyewa() != null ? t.getPenyewa().getId() : "";
                String tglDibuat = formatTanggalFile(t.getTanggalDibuat());
                String tglSewa = formatTanggalFile(t.getTanggalSewa());
                String tglJatuhTempo = formatTanggalFile(t.getTanggalJatuhTempo());
                String tglKembali = formatTanggalFile(t.getTanggalKembali());
                String kodeBayar = t.getPembayaran() != null ? t.getPembayaran().getKode() : "TUNAI";

                StringBuilder sbItems = new StringBuilder();
                for (int i = 0; i < t.getItems().size(); i++) {
                    ItemSewa it = t.getItems().get(i);
                    if (i > 0) {
                        sbItems.append(";");
                    }
                    sbItems.append(it.getIdBarang()).append("~")
                            .append(it.getNamaBarang()).append("~")
                            .append(it.getKategori().name()).append("~")
                            .append(Math.round(it.getTarifHarian())).append("~")
                            .append(Math.round(it.getBiayaTambahan())).append("~")
                            .append(Math.round(it.getBiayaTotal()));
                }

                String baris = String.format("%s|%s|%s|%d|%s|%s|%s|%s|%s|%s",
                        t.getId(),
                        idPenyewa,
                        t.getStatus().name(),
                        t.getLamaSewa(),
                        tglDibuat,
                        tglSewa,
                        tglJatuhTempo,
                        tglKembali,
                        kodeBayar,
                        sbItems.toString());
                writer.write(baris);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal menyimpan data transaksi: " + e.getMessage(), e);
        }
    }

    /**
     * Memuat nilai counter nomor urut transaksi berikutnya dari file data/counter.txt.
     * Mengembalikan -1 jika file belum tersedia.
     *
     * @return nomor transaksi berikutnya atau -1 bila file belum ada
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public int muatCounter() throws PenyimpananException {
        pastikanFolderAda();
        if (!fileCounter.exists()) {
            return -1;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileCounter), StandardCharsets.UTF_8))) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                String trimmed = baris.trim();
                if (trimmed.startsWith("transaksiBerikutnya=")) {
                    String nilaiStr = trimmed.substring("transaksiBerikutnya=".length()).trim();
                    try {
                        return Integer.parseInt(nilaiStr);
                    } catch (NumberFormatException e) {
                        System.out.printf("[PERINGATAN] File %s rusak, akan dihitung ulang: %s%n",
                                fileCounter.getName(), e.getMessage());
                        return -1;
                    }
                }
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal membaca file counter: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Menyimpan nilai counter nomor urut transaksi berikutnya ke file data/counter.txt.
     *
     * @param n nomor transaksi berikutnya
     * @throws PenyimpananException jika terjadi kesalahan I/O
     */
    public void simpanCounter(int n) throws PenyimpananException {
        pastikanFolderAda();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileCounter), StandardCharsets.UTF_8))) {
            writer.write("transaksiBerikutnya=" + n);
            writer.newLine();
        } catch (IOException e) {
            throw new PenyimpananException("Gagal menyimpan data counter: " + e.getMessage(), e);
        }
    }

    private LocalDate parseTanggalFile(String teks) {
        if (teks == null || teks.trim().isEmpty() || teks.trim().equals("-")) {
            return null;
        }
        try {
            return LocalDate.parse(teks.trim(), FormatHelper.FORMATTER_FILE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String formatTanggalFile(LocalDate tanggal) {
        if (tanggal == null) {
            return "-";
        }
        return tanggal.format(FormatHelper.FORMATTER_FILE);
    }

    private ArrayList<ItemSewa> parseItems(String itemsStr) {
        ArrayList<ItemSewa> hasil = new ArrayList<>();
        if (itemsStr == null || itemsStr.trim().isEmpty()) {
            return hasil;
        }
        String[] itemTokens = itemsStr.split(";");
        for (String token : itemTokens) {
            String[] atribut = token.split("~", -1);
            if (atribut.length < 6) {
                continue;
            }
            try {
                String idBarang = atribut[0].trim();
                String namaBarang = atribut[1].trim();
                KategoriBarang kategori = KategoriBarang.valueOf(atribut[2].trim().toUpperCase());
                double tarifHarian = Double.parseDouble(atribut[3].trim());
                double biayaTambahan = Double.parseDouble(atribut[4].trim());
                double biayaTotal = Double.parseDouble(atribut[5].trim());

                hasil.add(new ItemSewa(idBarang, namaBarang, kategori, tarifHarian, biayaTambahan, biayaTotal));
            } catch (Exception ignored) {
            }
        }
        return hasil;
    }

    private ArrayList<Barang> inisialisasiSeedBarang() {
        ArrayList<Barang> seed = new ArrayList<>();
        try {
            seed.add(new AlatPetik("PTK-001", "Gitar Akustik", 50000, 5000, true));
            seed.add(new AlatPetik("PTK-002", "Gitar Elektrik", 75000, 5000, true));
            seed.add(new AlatPetik("PTK-003", "Bass", 75000, 5000, true));
            seed.add(new AlatPetik("PTK-004", "Ukulele", 30000, 5000, true));

            seed.add(new AlatPukul("PKL-001", "Drum Set", 150000, 50000, true));
            seed.add(new AlatPukul("PKL-002", "Cajon", 35000, 10000, true));
            seed.add(new AlatPukul("PKL-003", "Marakas", 15000, 10000, true));
            seed.add(new AlatPukul("PKL-004", "Tamborin", 15000, 10000, true));
            seed.add(new AlatPukul("PKL-005", "Xylophone", 40000, 10000, true));

            seed.add(new AlatTiup("TPU-001", "Saksofon", 100000, 5000, true));
            seed.add(new AlatTiup("TPU-002", "Seruling", 20000, 5000, true));
            seed.add(new AlatTiup("TPU-003", "Harmonika", 25000, 5000, true));
        } catch (InputTidakValidException e) {
            System.err.println("Gagal menginisialisasi seed barang: " + e.getMessage());
        }
        return seed;
    }
}
