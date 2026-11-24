package com.rentalmusik.model;

/**
 * Kelas abstrak dasar untuk entitas akun pengguna dalam sistem rental.
 * Menerapkan prinsip Abstraction dan Encapsulation.
 *
 * @author Kelompok 4
 */
public abstract class Akun {

    private String id;
    private String username;
    private String password;
    private String nama;

    /**
     * Konstruktor default untuk kelas turunan.
     */
    protected Akun() {
    }

    /**
     * Menginisialisasi akun pengguna dengan kredensial dan identitas yang ditentukan.
     *
     * @param id kode pengenal akun
     * @param username nama pengguna (case-sensitive)
     * @param password kata sandi (case-sensitive)
     * @param nama nama lengkap yang ditampilkan
     */
    public Akun(String id, String username, String password, String nama) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    /**
     * Memverifikasi kredensial login pengguna. Keduanya diperiksa secara case-sensitive.
     *
     * @param username input nama pengguna
     * @param password input kata sandi
     * @return true jika kedua kredensial cocok persis, false jika salah
     */
    public boolean login(String username, String password) {
        if (this.username == null || this.password == null) {
            return false;
        }
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Mengembalikan ID akun.
     *
     * @return ID akun
     */
    public String getId() {
        return id;
    }

    /**
     * Mengembalikan username akun.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Mengembalikan nama lengkap pengguna yang ditampilkan.
     *
     * @return nama tampilan
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengembalikan peran pengguna ("Admin" atau "Penyewa").
     *
     * @return nama peran
     */
    public abstract String getPeran();
}
