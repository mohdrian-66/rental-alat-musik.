package com.rentalmusik.ui;

import com.rentalmusik.exception.BarangTidakDitemukanException;
import com.rentalmusik.exception.BarangTidakTersediaException;
import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.PenyimpananException;
import com.rentalmusik.exception.RentalException;
import com.rentalmusik.exception.TransaksiTidakValidException;
import com.rentalmusik.model.Admin;
import com.rentalmusik.model.AlatPetik;
import com.rentalmusik.model.AlatPukul;
import com.rentalmusik.model.AlatTiup;
import com.rentalmusik.model.Barang;
import com.rentalmusik.model.Invoice;
import com.rentalmusik.model.ItemSewa;
import com.rentalmusik.model.KategoriBarang;
import com.rentalmusik.model.StatusTransaksi;
import com.rentalmusik.model.Transaksi;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.InputHelper;
import com.rentalmusik.util.Konstanta;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Pengendali antarmuka menu CLI untuk pengguna dengan peran Administrator.
 *
 * @author Kelompok 4
 */
public class MenuAdmin {

    private final Admin admin;

    /**
     * Membuat objek MenuAdmin dengan referensi akun admin yang sedang aktif.
     *
     * @param admin akun admin aktif
     */
    public MenuAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Menjalankan siklus menu utama Admin hingga pengguna memilih Logout.
     */
    public void jalankan() {
        boolean berjalan = true;
        while (berjalan) {
            System.out.println(FormatHelper.garis('=', 50));
            System.out.println("           MENU ADMIN (" + admin.getNama() + ")");
            System.out.println(FormatHelper.garis('=', 50));
            System.out.println("1. Kelola Barang");
            System.out.println("2. Transaksi Menunggu (Terima / Tolak)");
            System.out.println("3. Konfirmasi Pengembalian");
            System.out.println("4. Semua Riwayat Transaksi");
            System.out.println("5. Logout");

            int pilihan;
            try {
                pilihan = InputHelper.bacaIntRentang("Pilih menu [1-5]: ", 1, 5, "Pilihan tidak valid.");
            } catch (InputTidakValidException e) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }

            try {
                switch (pilihan) {
                    case 1 -> menuKelolaBarang();
                    case 2 -> menuTransaksiMenunggu();
                    case 3 -> menuKonfirmasiPengembalian();
                    case 4 -> menuSemuaRiwayat();
                    case 5 -> berjalan = false;
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (RentalException e) {
                cetakError(e);
            }
        }
    }

    private void menuKelolaBarang() throws RentalException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- KELOLA BARANG ---");
            System.out.println("1. Lihat Semua Barang");
            System.out.println("2. Tambah Barang");
            System.out.println("3. Edit Barang");
            System.out.println("4. Hapus Barang");
            System.out.println("5. Kembali");

            int pilihan;
            try {
                pilihan = InputHelper.bacaIntRentang("Pilih menu [1-5]: ", 1, 5, "Pilihan tidak valid.");
            } catch (InputTidakValidException e) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }

            try {
                switch (pilihan) {
                    case 1 -> tampilkanSemuaBarang();
                    case 2 -> aksiTambahBarang();
                    case 3 -> aksiEditBarang();
                    case 4 -> aksiHapusBarang();
                    case 5 -> kembali = true;
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (RentalException e) {
                cetakError(e);
            }
        }
    }

    private void tampilkanSemuaBarang() {
        ArrayList<Barang> daftar = admin.getDaftarBarang().getSemuaBarang();
        if (daftar.isEmpty()) {
            System.out.println("[INFO] Belum ada barang.");
            return;
        }

        System.out.printf("%-8s  %-20s  %-8s  %10s  %14s  %-9s%n",
                "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Status");
        System.out.println("--------  --------------------  --------  ----------  --------------  ---------");
        for (Barang b : daftar) {
            String status = b.isTersedia() ? "Tersedia" : "Disewa";
            System.out.printf("%-8s  %-20s  %-8s  %10s  %14s  %-9s%n",
                    b.getId(),
                    b.getNama(),
                    b.getKategori().getNamaTampil(),
                    FormatHelper.rupiah(b.getTarifHarian()),
                    FormatHelper.rupiah(b.getBiayaTambahan()),
                    status);
        }
    }

    private void aksiTambahBarang() throws RentalException {
        System.out.println("\n--- TAMBAH BARANG ---");
        System.out.println("Pilih Kategori:");
        System.out.println("1. Petik");
        System.out.println("2. Pukul");
        System.out.println("3. Tiup");

        int katPilihan = InputHelper.bacaIntRentang("Pilih kategori [1-3]: ", 1, 3, "Kategori tidak valid.");
        KategoriBarang kategori = switch (katPilihan) {
            case 1 -> KategoriBarang.PETIK;
            case 2 -> KategoriBarang.PUKUL;
            case 3 -> KategoriBarang.TIUP;
            default -> throw new InputTidakValidException("Kategori tidak valid.");
        };

        String nama = InputHelper.bacaString("Nama barang: ");
        if (nama.isEmpty() || nama.length() > Konstanta.PANJANG_NAMA_MAKS
                || nama.contains("|") || nama.contains(";") || nama.contains("~")) {
            throw new InputTidakValidException("Nama barang tidak valid atau sudah ada.");
        }

        int tarif = InputHelper.bacaIntRentang("Tarif harian (Rp1.000 - Rp10.000.000): ",
                (int) Konstanta.TARIF_MIN, (int) Konstanta.TARIF_MAKS,
                "Tarif harian harus antara Rp1.000 dan Rp10.000.000.");

        double defaultBiaya = kategori.getBiayaTambahanDefault();
        String promptBiaya = String.format("%s [ENTER untuk default %s]: ",
                kategori.getNamaBiayaTambahan(), FormatHelper.rupiah(defaultBiaya));
        Integer biayaInput = InputHelper.bacaIntOpsional(promptBiaya);

        double biayaTambahan = (biayaInput != null) ? (double) biayaInput : defaultBiaya;
        if (biayaTambahan < 0 || biayaTambahan > Konstanta.BIAYA_TAMBAHAN_MAKS) {
            throw new InputTidakValidException("Biaya tambahan harus antara Rp0 dan Rp10.000.000.");
        }

        String idBaru = admin.getDaftarBarang().buatIdBaru(kategori);
        Barang b = switch (kategori) {
            case PETIK -> new AlatPetik(idBaru, nama, tarif, biayaTambahan, true);
            case PUKUL -> new AlatPukul(idBaru, nama, tarif, biayaTambahan, true);
            case TIUP -> new AlatTiup(idBaru, nama, tarif, biayaTambahan, true);
        };

        admin.tambahBarang(b);
        System.out.printf("[OK] Barang %s - %s berhasil ditambahkan.%n", idBaru, nama);
    }

    private void aksiEditBarang() throws RentalException {
        System.out.println("\n--- EDIT BARANG ---");
        String id = InputHelper.bacaString("Masukkan ID barang: ");
        Barang b = admin.getDaftarBarang().cariById(id);
        if (b == null) {
            throw new BarangTidakDitemukanException("Barang dengan ID " + id + " tidak ditemukan.");
        }

        System.out.println("Data saat ini:");
        System.out.println("ID             : " + b.getId());
        System.out.println("Kategori       : " + b.getKategori().getNamaTampil());
        System.out.println("Nama           : " + b.getNama());
        System.out.println("Tarif Harian   : " + FormatHelper.rupiah(b.getTarifHarian()));
        System.out.printf("Biaya Tambahan : %s (%s)%n",
                FormatHelper.rupiah(b.getBiayaTambahan()), b.getNamaBiayaTambahan());
        System.out.println("Status         : " + (b.isTersedia() ? "Tersedia" : "Disewa"));

        String namaBaruInput = InputHelper.bacaString("Nama baru [ENTER jika tidak diubah]: ");
        String namaBaru = namaBaruInput.isEmpty() ? null : namaBaruInput;

        Integer tarifInput = InputHelper.bacaIntOpsional("Tarif harian baru [ENTER jika tidak diubah]: ");
        Double tarifBaru = null;
        if (tarifInput != null) {
            if (tarifInput < Konstanta.TARIF_MIN || tarifInput > Konstanta.TARIF_MAKS) {
                throw new InputTidakValidException("Tarif harian harus antara Rp1.000 dan Rp10.000.000.");
            }
            tarifBaru = (double) tarifInput;
        }

        Integer biayaInput = InputHelper.bacaIntOpsional("Biaya tambahan baru [ENTER jika tidak diubah]: ");
        Double biayaBaru = null;
        if (biayaInput != null) {
            if (biayaInput < 0 || biayaInput > Konstanta.BIAYA_TAMBAHAN_MAKS) {
                throw new InputTidakValidException("Biaya tambahan harus antara Rp0 dan Rp10.000.000.");
            }
            biayaBaru = (double) biayaInput;
        }

        admin.editBarang(b.getId(), namaBaru, tarifBaru, biayaBaru);
        System.out.printf("[OK] Barang %s berhasil diperbarui.%n", b.getId());
    }

    private void aksiHapusBarang() throws RentalException {
        System.out.println("\n--- HAPUS BARANG ---");
        String id = InputHelper.bacaString("Masukkan ID barang: ");
        Barang b = admin.getDaftarBarang().cariById(id);
        if (b == null) {
            throw new BarangTidakDitemukanException("Barang dengan ID " + id + " tidak ditemukan.");
        }

        System.out.printf("Barang ditemukan: %s - %s (%s)%n",
                b.getId(), b.getNama(), (b.isTersedia() ? "Tersedia" : "Disewa"));
        boolean yakin = InputHelper.bacaYaTidak("Yakin menghapus? (y/n): ");
        if (!yakin) {
            System.out.println("[INFO] Penghapusan dibatalkan.");
            return;
        }

        admin.hapusBarang(id);
        System.out.printf("[OK] Barang %s berhasil dihapus.%n", id.toUpperCase());
    }

    private void menuTransaksiMenunggu() throws RentalException {
        ArrayList<Transaksi> list = admin.lihatTransaksiMenunggu();
        if (list.isEmpty()) {
            System.out.println("[INFO] Tidak ada transaksi yang menunggu.");
            return;
        }

        System.out.printf("%-8s  %-14s  %-10s  %4s  %6s  %13s  %-13s%n",
                "ID", "Penyewa", "Tgl Dibuat", "Lama", "Barang", "Total Tagihan", "Metode");
        System.out.println("--------  --------------  ----------  ----  ------  -------------  -------------");
        for (Transaksi t : list) {
            System.out.printf("%-8s  %-14s  %-10s  %4d  %6d  %13s  %-13s%n",
                    t.getId(),
                    t.getPenyewa() != null ? t.getPenyewa().getNama() : "-",
                    FormatHelper.tanggal(t.getTanggalDibuat()),
                    t.getLamaSewa(),
                    t.getItems().size(),
                    FormatHelper.rupiah(t.hitungTotal()),
                    t.getPembayaran() != null ? t.getPembayaran().getNama() : "-");
        }

        String inputId = InputHelper.bacaString("\nMasukkan ID transaksi (0 untuk kembali): ");
        if ("0".equals(inputId)) {
            return;
        }

        Transaksi target = null;
        for (Transaksi t : list) {
            if (t.getId().equalsIgnoreCase(inputId)) {
                target = t;
                break;
            }
        }

        if (target == null) {
            throw new TransaksiTidakValidException(
                    "[GAGAL] Transaksi " + inputId.toUpperCase() + " tidak ditemukan atau tidak berstatus menunggu.");
        }

        // Tampilkan detail transaksi
        System.out.println("\n--- DETAIL TRANSAKSI MENUNGGU ---");
        System.out.println("ID Transaksi : " + target.getId());
        System.out.println("Penyewa      : " + (target.getPenyewa() != null ? target.getPenyewa().getNama() : "-"));
        System.out.println("Lama Sewa    : " + target.getLamaSewa() + " hari");
        System.out.println("Metode Bayar : " + (target.getPembayaran() != null ? target.getPembayaran().getNama() : "-"));
        System.out.println("Daftar Barang:");
        for (ItemSewa it : target.getItems()) {
            System.out.printf("  - %s (%s) [%s]: %s%n",
                    it.getNamaBarang(), it.getIdBarang(), it.getKategori().getNamaTampil(),
                    FormatHelper.rupiah(it.getBiayaTotal()));
        }
        System.out.println("Subtotal     : " + FormatHelper.rupiah(target.hitungSubtotal()));
        System.out.println("Biaya Admin  : " + FormatHelper.rupiah(target.hitungBiayaAdmin()));
        System.out.println("Total Tagihan: " + FormatHelper.rupiah(target.hitungTotal()));

        System.out.println("\n1. Terima");
        System.out.println("2. Tolak");
        System.out.println("3. Kembali");
        int aksi = InputHelper.bacaIntRentang("Pilih menu [1-3]: ", 1, 3, "Pilihan tidak valid.");

        if (aksi == 1) {
            admin.terimaTransaksi(target);
            System.out.printf("[OK] Transaksi %s diterima. Barang berstatus disewa.%n", target.getId());
        } else if (aksi == 2) {
            boolean yakin = InputHelper.bacaYaTidak("Yakin menolak transaksi? (y/n): ");
            if (yakin) {
                admin.tolakTransaksi(target);
                System.out.printf("[OK] Transaksi %s ditolak. Pembayaran %s dianggap dikembalikan.%n",
                        target.getId(), FormatHelper.rupiah(target.hitungTotal()));
            } else {
                System.out.println("[INFO] Penolakan transaksi dibatalkan.");
            }
        }
    }

    private void menuKonfirmasiPengembalian() throws RentalException {
        ArrayList<Transaksi> semua = admin.getDaftarTransaksi().filterStatus(StatusTransaksi.DISEWA);
        if (semua.isEmpty()) {
            System.out.println("[INFO] Tidak ada barang yang sedang disewa.");
            return;
        }

        System.out.printf("%-8s  %-14s  %-10s  %-11s  %-6s%n",
                "ID", "Penyewa", "Tgl Sewa", "Jatuh Tempo", "Barang");
        System.out.println("--------  --------------  ----------  -----------  ------");
        for (Transaksi t : semua) {
            System.out.printf("%-8s  %-14s  %-10s  %-11s  %6d%n",
                    t.getId(),
                    t.getPenyewa() != null ? t.getPenyewa().getNama() : "-",
                    FormatHelper.tanggal(t.getTanggalSewa()),
                    FormatHelper.tanggal(t.getTanggalJatuhTempo()),
                    t.getItems().size());
        }

        String id = InputHelper.bacaString("\nMasukkan ID transaksi (0 untuk kembali): ");
        if ("0".equals(id)) {
            return;
        }

        Transaksi target = null;
        for (Transaksi t : semua) {
            if (t.getId().equalsIgnoreCase(id)) {
                target = t;
                break;
            }
        }

        if (target == null) {
            throw new TransaksiTidakValidException(
                    "[GAGAL] Transaksi " + id.toUpperCase() + " tidak ditemukan atau tidak berstatus disewa.");
        }

        LocalDate tglKembali = InputHelper.bacaTanggal("Masukkan tanggal kembali (dd-MM-yyyy): ");
        if (target.getTanggalSewa() != null && tglKembali.isBefore(target.getTanggalSewa())) {
            throw new TransaksiTidakValidException(
                    "Tanggal kembali tidak boleh sebelum tanggal sewa ("
                            + FormatHelper.tanggal(target.getTanggalSewa()) + ").");
        }

        long selisih = ChronoUnit.DAYS.between(target.getTanggalJatuhTempo(), tglKembali);
        int hariTerlambat = (int) Math.max(0, selisih);
        double denda = (double) hariTerlambat * target.getItems().size() * Konstanta.DENDA_PER_BARANG_PER_HARI;

        System.out.println("\n--- RINGKASAN PENGEMBALIAN ---");
        System.out.println("Jatuh Tempo    : " + FormatHelper.tanggal(target.getTanggalJatuhTempo()));
        System.out.println("Tanggal Kembali: " + FormatHelper.tanggal(tglKembali));
        System.out.println("Hari Terlambat : " + hariTerlambat + " hari");
        System.out.println("Jumlah Barang  : " + target.getItems().size() + " barang");
        System.out.println("Total Denda    : " + FormatHelper.rupiah(denda));

        if (denda > 0) {
            String prompt = String.format("Denda %s harus dibayar tunai. Denda sudah diterima? (y/n): ",
                    FormatHelper.rupiah(denda));
            boolean diterima = InputHelper.bacaYaTidak(prompt);
            if (!diterima) {
                System.out.println("[INFO] Konfirmasi pengembalian dibatalkan.");
                return;
            }
        }

        admin.konfirmasiKembali(target, tglKembali);
        System.out.printf("[OK] Pengembalian transaksi %s dikonfirmasi. Barang kembali tersedia.%n", target.getId());
    }

    private void menuSemuaRiwayat() throws RentalException {
        ArrayList<Transaksi> list = admin.lihatSemuaTransaksi();
        if (list.isEmpty()) {
            System.out.println("[INFO] Belum ada transaksi.");
            return;
        }

        System.out.printf("%-8s  %-14s  %-10s  %13s  %10s  %-10s%n",
                "ID", "Penyewa", "Tgl Dibuat", "Total Tagihan", "Denda", "Status");
        System.out.println("--------  --------------  ----------  -------------  ----------  ----------");
        for (Transaksi t : list) {
            System.out.printf("%-8s  %-14s  %-10s  %13s  %10s  %-10s%n",
                    t.getId(),
                    t.getPenyewa() != null ? t.getPenyewa().getNama() : "-",
                    FormatHelper.tanggal(t.getTanggalDibuat()),
                    FormatHelper.rupiah(t.hitungTotal()),
                    FormatHelper.rupiah(t.hitungDenda()),
                    t.getStatus().name());
        }

        String id = InputHelper.bacaString("\nMasukkan ID transaksi untuk cetak invoice / lihat detail (0 untuk kembali): ");
        if ("0".equals(id)) {
            return;
        }

        Transaksi target = admin.getDaftarTransaksi().cariById(id);
        if (target == null) {
            throw new TransaksiTidakValidException("[GAGAL] Transaksi " + id + " tidak ditemukan.");
        }

        if (target.getStatus() == StatusTransaksi.DISEWA || target.getStatus() == StatusTransaksi.SELESAI) {
            new Invoice(target).cetakInvoice();
        } else {
            System.out.println("\n--- DETAIL TRANSAKSI ---");
            System.out.println("ID           : " + target.getId());
            System.out.println("Penyewa      : " + (target.getPenyewa() != null ? target.getPenyewa().getNama() : "-"));
            System.out.println("Status       : " + target.getStatus().name());
            System.out.println("Tgl Dibuat   : " + FormatHelper.tanggal(target.getTanggalDibuat()));
            System.out.println("Lama Sewa    : " + target.getLamaSewa() + " hari");
            System.out.println("Metode Bayar : " + (target.getPembayaran() != null ? target.getPembayaran().getNama() : "-"));
            System.out.println("Total Tagihan: " + FormatHelper.rupiah(target.hitungTotal()));
            System.out.println("Rincian Barang:");
            for (ItemSewa it : target.getItems()) {
                System.out.printf("  - %s (%s) [%s]: %s%n",
                        it.getNamaBarang(), it.getIdBarang(), it.getKategori().getNamaTampil(),
                        FormatHelper.rupiah(it.getBiayaTotal()));
            }
        }
    }

    private void cetakError(RentalException e) {
        String msg = e.getMessage();
        if (msg != null && msg.startsWith("[GAGAL]")) {
            System.out.println(msg);
        } else {
            System.out.println("[GAGAL] " + msg);
        }
    }
}
