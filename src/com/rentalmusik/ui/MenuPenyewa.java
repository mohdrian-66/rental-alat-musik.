package com.rentalmusik.ui;

import com.rentalmusik.exception.BarangTidakDitemukanException;
import com.rentalmusik.exception.InputTidakValidException;
import com.rentalmusik.exception.RentalException;
import com.rentalmusik.exception.TransaksiTidakValidException;
import com.rentalmusik.model.Barang;
import com.rentalmusik.model.Invoice;
import com.rentalmusik.model.KategoriBarang;
import com.rentalmusik.model.Penyewa;
import com.rentalmusik.model.StatusTransaksi;
import com.rentalmusik.model.Transaksi;
import com.rentalmusik.pembayaran.Pembayaran;
import com.rentalmusik.pembayaran.QRIS;
import com.rentalmusik.pembayaran.TransferBank;
import com.rentalmusik.pembayaran.Tunai;
import com.rentalmusik.util.FormatHelper;
import com.rentalmusik.util.InputHelper;
import com.rentalmusik.util.Konstanta;
import java.util.ArrayList;

/**
 * Pengendali antarmuka menu CLI untuk pengguna dengan peran Penyewa.
 *
 * @author Kelompok 4
 */
public class MenuPenyewa {

    private final Penyewa penyewa;

    /**
     * Membuat objek MenuPenyewa dengan referensi akun penyewa yang sedang aktif.
     *
     * @param penyewa akun penyewa aktif
     */
    public MenuPenyewa(Penyewa penyewa) {
        this.penyewa = penyewa;
    }

    /**
     * Menjalankan siklus menu utama Penyewa hingga pengguna memilih Logout.
     */
    public void jalankan() {
        boolean berjalan = true;
        while (berjalan) {
            System.out.println(FormatHelper.garis('=', 50));
            System.out.println("           MENU PENYEWA (" + penyewa.getNama() + ")");
            System.out.println(FormatHelper.garis('=', 50));
            System.out.println("1. Lihat Daftar Barang");
            System.out.println("2. Tambah Barang ke Keranjang");
            System.out.println("3. Lihat / Hapus Isi Keranjang");
            System.out.println("4. Buat Transaksi Sewa");
            System.out.println("5. Status Transaksi Saya");
            System.out.println("6. Riwayat Sewa");
            System.out.println("7. Logout");

            int pilihan;
            try {
                pilihan = InputHelper.bacaIntRentang("Pilih menu [1-7]: ", 1, 7, "Pilihan tidak valid.");
            } catch (InputTidakValidException e) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }

            try {
                switch (pilihan) {
                    case 1 -> subMenuLihatBarang();
                    case 2 -> aksiTambahKeKeranjang();
                    case 3 -> subMenuKeranjang();
                    case 4 -> aksiBuatTransaksi();
                    case 5 -> menuStatusTransaksiSaya();
                    case 6 -> menuRiwayatSewa();
                    case 7 -> berjalan = false;
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (RentalException e) {
                cetakError(e);
            }
        }
    }

    private void subMenuLihatBarang() throws RentalException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- LIHAT DAFTAR BARANG ---");
            System.out.println("1. Semua barang");
            System.out.println("2. Hanya yang tersedia");
            System.out.println("3. Filter kategori");
            System.out.println("4. Cari berdasarkan nama");
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
                    case 1 -> penyewa.lihatBarang();
                    case 2 -> {
                        ArrayList<Barang> tersedia = penyewa.getDaftarBarang().filterTersedia();
                        cetakTabelBarang(tersedia);
                    }
                    case 3 -> {
                        System.out.println("\nPilih Kategori:");
                        System.out.println("1. Petik");
                        System.out.println("2. Pukul");
                        System.out.println("3. Tiup");
                        int kat = InputHelper.bacaIntRentang("Pilih [1-3]: ", 1, 3, "Kategori tidak valid.");
                        KategoriBarang k = switch (kat) {
                            case 1 -> KategoriBarang.PETIK;
                            case 2 -> KategoriBarang.PUKUL;
                            case 3 -> KategoriBarang.TIUP;
                            default -> throw new InputTidakValidException("Kategori tidak valid.");
                        };
                        ArrayList<Barang> filtered = penyewa.getDaftarBarang().filterKategori(k);
                        cetakTabelBarang(filtered);
                    }
                    case 4 -> {
                        String kata = InputHelper.bacaString("Masukkan kata kunci pencarian: ");
                        if (kata.isEmpty()) {
                            throw new InputTidakValidException("Kata kunci tidak boleh kosong.");
                        }
                        ArrayList<Barang> hasil = penyewa.getDaftarBarang().cariByNama(kata);
                        cetakTabelBarang(hasil);
                    }
                    case 5 -> kembali = true;
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (RentalException e) {
                cetakError(e);
            }
        }
    }

    private void cetakTabelBarang(ArrayList<Barang> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("[INFO] Tidak ada barang yang sesuai.");
            return;
        }
        System.out.printf("%-8s  %-20s  %-8s  %10s  %14s  %-9s%n",
                "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Status");
        System.out.println("--------  --------------------  --------  ----------  --------------  ---------");
        for (Barang b : list) {
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

    private void aksiTambahKeKeranjang() throws RentalException {
        System.out.println("\n--- TAMBAH BARANG KE KERANJANG ---");
        String id = InputHelper.bacaString("Masukkan ID barang: ");
        penyewa.tambahKeKeranjang(id);
    }

    private void subMenuKeranjang() throws RentalException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- ISI KERANJANG SEWA ---");
            penyewa.lihatKeranjang();

            if (penyewa.getKeranjang().isKosong()) {
                return;
            }

            System.out.println("\n1. Hapus satu barang");
            System.out.println("2. Kosongkan keranjang");
            System.out.println("3. Kembali");

            int pilihan;
            try {
                pilihan = InputHelper.bacaIntRentang("Pilih menu [1-3]: ", 1, 3, "Pilihan tidak valid.");
            } catch (InputTidakValidException e) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }

            try {
                switch (pilihan) {
                    case 1 -> {
                        String id = InputHelper.bacaString("Masukkan ID barang yang akan dihapus: ");
                        penyewa.hapusDariKeranjang(id);
                        System.out.printf("[OK] Barang %s berhasil dihapus dari keranjang.%n", id.toUpperCase());
                    }
                    case 2 -> {
                        boolean yakin = InputHelper.bacaYaTidak("Yakin ingin mengosongkan keranjang? (y/n): ");
                        if (yakin) {
                            penyewa.getKeranjang().kosongkan();
                            System.out.println("[OK] Keranjang berhasil dikosongkan.");
                            kembali = true;
                        } else {
                            System.out.println("[INFO] Pembatalan pengosongan keranjang.");
                        }
                    }
                    case 3 -> kembali = true;
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (RentalException e) {
                cetakError(e);
            }
        }
    }

    private void aksiBuatTransaksi() throws RentalException {
        System.out.println("\n--- BUAT TRANSAKSI SEWA ---");
        if (penyewa.getKeranjang().isKosong()) {
            throw new InputTidakValidException("[GAGAL] Keranjang kosong. Tambahkan barang terlebih dahulu.");
        }

        // Tampilkan isi keranjang
        System.out.println("Isi keranjang saat ini:");
        penyewa.lihatKeranjang();

        int lamaSewa = InputHelper.bacaIntRentang("Masukkan lama sewa (1-14 hari): ",
                Konstanta.LAMA_SEWA_MIN, Konstanta.LAMA_SEWA_MAKS,
                "Lama sewa harus antara 1 dan 14 hari.");

        // Rincian biaya sewa
        System.out.println("\nRincian Biaya Sewa:");
        double subtotal = 0.0;
        for (Barang b : penyewa.getKeranjang().getBarang()) {
            double biayaItem = b.hitungBiaya(lamaSewa);
            subtotal += biayaItem;
            System.out.printf("  - %s (%s): %s%n",
                    b.getNama(), b.getId(), FormatHelper.rupiah(biayaItem));
        }
        System.out.println("Subtotal     : " + FormatHelper.rupiah(subtotal));

        // Pilih metode pembayaran
        System.out.println("\nPilih Metode Pembayaran:");
        System.out.println("1. Transfer Bank (Biaya admin Rp2.500)");
        System.out.println("2. QRIS (Biaya admin Rp0)");
        System.out.println("3. Tunai (Biaya admin Rp0)");

        int pilihBayar = InputHelper.bacaIntRentang("Pilih metode [1-3]: ", 1, 3, "Pilihan tidak valid.");
        Pembayaran pembayaran = switch (pilihBayar) {
            case 1 -> new TransferBank();
            case 2 -> new QRIS();
            case 3 -> new Tunai();
            default -> throw new InputTidakValidException("Metode pembayaran tidak valid.");
        };

        double totalTagihan = subtotal + pembayaran.getBiayaAdmin();
        System.out.println("Biaya admin  : " + FormatHelper.rupiah(pembayaran.getBiayaAdmin()));
        System.out.println("TOTAL TAGIHAN: " + FormatHelper.rupiah(totalTagihan));

        boolean lanjut = InputHelper.bacaYaTidak("Lanjutkan pembayaran? (y/n): ");
        if (!lanjut) {
            System.out.println("[INFO] Pembuatan transaksi dibatalkan.");
            return;
        }

        Transaksi t = penyewa.buatTransaksi(lamaSewa, pembayaran);
        System.out.printf("[OK] Transaksi %s berhasil dibuat dan menunggu persetujuan admin.%n", t.getId());
    }

    private void menuStatusTransaksiSaya() throws RentalException {
        ArrayList<Transaksi> semua = penyewa.getDaftarTransaksi().filterPenyewa(penyewa);
        if (semua.isEmpty()) {
            System.out.println("[INFO] Belum ada transaksi.");
            return;
        }

        System.out.printf("%-8s  %-10s  %13s  %-10s%n",
                "ID", "Tgl Dibuat", "Total Tagihan", "Status");
        System.out.println("--------  ----------  -------------  ----------");
        for (Transaksi t : semua) {
            System.out.printf("%-8s  %-10s  %13s  %-10s%n",
                    t.getId(),
                    FormatHelper.tanggal(t.getTanggalDibuat()),
                    FormatHelper.rupiah(t.hitungTotal()),
                    t.getStatus().name());
        }

        String inputId = InputHelper.bacaString("\nMasukkan ID transaksi MENUNGGU untuk dibatalkan (0 untuk kembali): ");
        if ("0".equals(inputId)) {
            return;
        }

        Transaksi target = null;
        for (Transaksi t : semua) {
            if (t.getId().equalsIgnoreCase(inputId)) {
                target = t;
                break;
            }
        }

        if (target == null || target.getStatus() != StatusTransaksi.MENUNGGU) {
            throw new TransaksiTidakValidException(
                    "[GAGAL] Transaksi " + inputId.toUpperCase() + " tidak ditemukan atau tidak berstatus MENUNGGU.");
        }

        boolean yakin = InputHelper.bacaYaTidak("Yakin membatalkan transaksi " + target.getId() + "? (y/n): ");
        if (!yakin) {
            System.out.println("[INFO] Pembatalan transaksi dibatalkan.");
            return;
        }

        penyewa.batalkanTransaksi(target);
        System.out.printf("[OK] Transaksi %s dibatalkan. Pembayaran %s dianggap dikembalikan.%n",
                target.getId(), FormatHelper.rupiah(target.hitungTotal()));
    }

    private void menuRiwayatSewa() throws RentalException {
        penyewa.lihatRiwayat();
        ArrayList<Transaksi> riwayat = penyewa.getRiwayat();
        if (riwayat.isEmpty()) {
            return;
        }

        String id = InputHelper.bacaString("\nMasukkan ID transaksi untuk cetak ulang invoice (0 untuk kembali): ");
        if ("0".equals(id)) {
            return;
        }

        Transaksi target = null;
        for (Transaksi t : riwayat) {
            if (t.getId().equalsIgnoreCase(id)) {
                target = t;
                break;
            }
        }

        if (target == null) {
            throw new TransaksiTidakValidException("[GAGAL] Transaksi " + id + " tidak ditemukan di riwayat sewa.");
        }

        new Invoice(target).cetakInvoice();
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
