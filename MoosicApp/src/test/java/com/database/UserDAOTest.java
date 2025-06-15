package com.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest { // Ubah nama kelas dari UserAuthTest menjadi UserDAOTest

    // Ganti UserAuth dengan UserDAO
    // Kita akan menggunakan mock untuk menghindari koneksi database saat unit testing
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        // Inisialisasi mock sebelum setiap tes
        userDAO = new UserDAOMock();
    }

    // --- Tes untuk Fitur Login ---
    @Test
    void testLoginBerhasilSebagaiAdminUsername() {
        String result = userDAO.login("admin", "admin123");
        assertTrue(result.contains("Berhasil"), "Login seharusnya berhasil untuk admin dengan username.");
        assertTrue(result.contains("admin"), "Login seharusnya mengidentifikasi sebagai admin.");
    }

    @Test
    void testLoginBerhasilSebagaiAdminEmail() {
        String result = userDAO.login("admin@example.com", "admin123");
        assertTrue(result.contains("Berhasil"), "Login seharusnya berhasil untuk admin dengan email.");
        assertTrue(result.contains("admin"), "Login seharusnya mengidentifikasi sebagai admin.");
    }

    @Test
    void testLoginBerhasilSebagaiCustomer() {
        // Asumsi ada user "user" di mock Anda atau tambahkan di UserDAOMock
        String result = userDAO.login("user", "user123");
        assertTrue(result.contains("Berhasil"), "Login seharusnya berhasil untuk customer.");
        assertTrue(result.contains("customer"), "Login seharusnya mengidentifikasi sebagai customer.");
    }

    @Test
    void testLoginGagalPasswordSalah() {
        String result = userDAO.login("admin", "salah_password");
        assertTrue(result.contains("Gagal"), "Login seharusnya gagal jika password salah.");
        assertTrue(result.contains("Username atau password salah."), "Pesan kesalahan seharusnya menunjukkan username atau password salah.");
    }

    @Test
    void testLoginGagalUsernameSalah() {
        String result = userDAO.login("unknown_user", "admin123");
        assertTrue(result.contains("Gagal"), "Login seharusnya gagal jika username tidak ada.");
        assertTrue(result.contains("Username atau password salah."), "Pesan kesalahan seharusnya menunjukkan username atau password salah.");
    }

    @Test
    void testLoginGagalInputKosong() {
        String result = userDAO.login("", "");
        assertTrue(result.contains("Gagal"), "Login seharusnya gagal jika input kosong.");
    }

    // --- Tes untuk Fitur Register ---
    @Test
    void testRegisterBerhasil() {
        boolean result = userDAO.register("new_user", "pass123", "newuser@example.com", "New User", "Jl. Baru");
        assertTrue(result, "Registrasi seharusnya berhasil.");
    }

    @Test
    void testRegisterGagalUsernameSudahAda() {
        boolean result = userDAO.register("existing_user", "pass123", "another@example.com", "Existing User", "Jl. Lama");
        assertFalse(result, "Registrasi seharusnya gagal jika username sudah ada.");
    }

    @Test
    void testRegisterGagalEmailSudahAda() {
        boolean result = userDAO.register("another_username", "pass123", "existing@example.com", "Existing Email", "Jl. Lain");
        assertFalse(result, "Registrasi seharusnya gagal jika email sudah ada.");
    }

    @Test
    void testRegisterGagalKasusErrorMock() {
        // Contoh skenario di mana mock mengembalikan kegagalan yang disimulasikan
        boolean result = userDAO.register("error_case", "anypass", "any@example.com", "Error User", "Any Address");
        assertFalse(result, "Registrasi seharusnya gagal pada kasus error yang disimulasikan.");
    }

    // --- Tes untuk Fitur Logout ---
    @Test
    void testLogout() {
        // Metode logout di UserDAO hanya mencetak pesan, jadi kita bisa tes bahwa ia tidak menyebabkan error
        // atau jika Anda menggunakan framework mock seperti Mockito, Anda bisa memverifikasi bahwa metode logout dipanggil.
        // Untuk saat ini, karena hanya mencetak, kita asumsikan jika tidak ada exception berarti berhasil.
        assertDoesNotThrow(() -> userDAO.logout());
    }
}