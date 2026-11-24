package com.rentalmusik.model;

import com.rentalmusik.util.FormatHelper;
import java.util.ArrayList;

/**
 * Kelas pencetak dokumen Invoice penyewaan alat musik dengan format lebar 60 karakter.
 * Menghasilkan invoice sewa (saat status DISEWA) dan invoice akhir (saat status SELESAI).
 *
 * @author Kelompok 4
 */
public class Invoice {

    private final Transaksi transaksi;
    private final double total;
    private final double denda;

    /**
     * Membuat objek Invoice berdasarkan transaksi tertentu.
     *
     * @param transaksi objek transaksi yang akan dicetak invoicenya
     */
    public Invoice(Transaksi transaksi) {
        this.transaksi = transaksi;
        this.total = transaksi.hitungTotal();
        this.denda = transaksi.hitungDenda();
    }

    /**
     * Mencetak invoice lengkap ke layar sesuai spesifikasi format (lebar 60 karakter).
     */
    public void cetakInvoice() {
        int lebar = 60;
        String garisGanda = FormatHelper.garis('=', lebar);
        String garisTunggal = FormatHelper.garis('-', lebar);

        System.out.println(garisGanda);
        System.out.println(pusatkanTeks("INVOICE RENTAL ALAT MUSIK", lebar));
        System.out.println(garisGanda);

        String idTrx = transaksi.getId();
        String noInvoice = idTrx.startsWith("TRX-") ? idTrx.replace("TRX-", "INV-") : "INV-" + idTrx;

        System.out.printf("%-15s: %s%n", "No. Invoice", noInvoice);
        System.out.printf("%-15s: %s%n", "No. Transaksi", idTrx);
        System.out.printf("%-15s: %s%n", "Penyewa",
                transaksi.getPenyewa() != null ? transaksi.getPenyewa().getNama() : "-");
        System.out.printf("%-15s: %s%n", "Status", transaksi.getStatus().name());
        System.out.printf("%-15s: %s%n", "Tgl Transaksi", FormatHelper.tanggal(transaksi.getTanggalDibuat()));
        System.out.printf("%-15s: %s%n", "Tgl Sewa", FormatHelper.tanggal(transaksi.getTanggalSewa()));
        System.out.printf("%-15s: %s%n", "Jatuh Tempo", FormatHelper.tanggal(transaksi.getTanggalJatuhTempo()));
        System.out.printf("%-15s: %s%n", "Tgl Kembali", FormatHelper.tanggal(transaksi.getTanggalKembali()));
        System.out.printf("%-15s: %d hari%n", "Lama Sewa", transaksi.getLamaSewa());
        System.out.printf("%-15s: %s%n", "Metode Bayar",
                transaksi.getPembayaran() != null ? transaksi.getPembayaran().getNama() : "-");

        System.out.println(garisTunggal);

        ArrayList<ItemSewa> items = transaksi.getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemSewa item = items.get(i);
            int noUrut = i + 1;
            String headerItem = String.format("%d. %s (%s) - %s",
                    noUrut, item.getNamaBarang(), item.getIdBarang(), item.getKategori().getNamaTampil());
            System.out.println(headerItem);

            double biayaHarianTotal = item.getTarifHarian() * transaksi.getLamaSewa();
            String barisHarianKiri = String.format("   %s x %d hari",
                    FormatHelper.rupiah(item.getTarifHarian()), transaksi.getLamaSewa());
            System.out.println(FormatHelper.rataKanan(barisHarianKiri, FormatHelper.rupiah(biayaHarianTotal), lebar));

            String barisTambahanKiri = "   " + item.getKategori().getNamaBiayaTambahan();
            System.out.println(FormatHelper.rataKanan(barisTambahanKiri, FormatHelper.rupiah(item.getBiayaTambahan()), lebar));

            String barisSubtotalItemKiri = "   Subtotal item";
            System.out.println(FormatHelper.rataKanan(barisSubtotalItemKiri, FormatHelper.rupiah(item.getBiayaTotal()), lebar));
        }

        System.out.println(garisTunggal);

        System.out.println(FormatHelper.rataKanan("Subtotal sewa", FormatHelper.rupiah(transaksi.hitungSubtotal()), lebar));
        String labelAdmin = "Biaya admin (" + (transaksi.getPembayaran() != null ? transaksi.getPembayaran().getNama() : "-") + ")";
        System.out.println(FormatHelper.rataKanan(labelAdmin, FormatHelper.rupiah(transaksi.hitungBiayaAdmin()), lebar));
        System.out.println(FormatHelper.rataKanan("TOTAL TAGIHAN SEWA", FormatHelper.rupiah(total), lebar));

        if (transaksi.getStatus() == StatusTransaksi.SELESAI) {
            int hariTerlambat = transaksi.hitungHariTerlambat();
            if (hariTerlambat > 0) {
                String labelDenda = String.format("Denda (%d hari x %d barang x Rp20.000)",
                        hariTerlambat, items.size());
                System.out.println(FormatHelper.rataKanan(labelDenda, FormatHelper.rupiah(denda), lebar));
            } else {
                System.out.println(FormatHelper.rataKanan("Denda (0 hari)", "Rp0", lebar));
            }
            System.out.println(garisTunggal);
            System.out.println(FormatHelper.rataKanan("TOTAL AKHIR", FormatHelper.rupiah(transaksi.hitungTotalAkhir()), lebar));
        } else if (transaksi.getStatus() == StatusTransaksi.DISEWA) {
            System.out.println("Catatan: denda Rp20.000 per barang per hari keterlambatan.");
        }

        System.out.println(garisGanda);
    }

    private String pusatkanTeks(String teks, int lebar) {
        if (teks == null) {
            teks = "";
        }
        int totalSpasi = lebar - teks.length();
        if (totalSpasi <= 0) {
            return teks;
        }
        int spasiKiri = totalSpasi / 2;
        return " ".repeat(spasiKiri) + teks;
    }
}
