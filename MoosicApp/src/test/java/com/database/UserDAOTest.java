package com.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest { 

   
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOMock();
    }

  
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
        boolean result = userDAO.register("error_case", "anypass", "any@example.com", "Error User", "Any Address");
        assertFalse(result, "Registrasi seharusnya gagal pada kasus error yang disimulasikan.");
    }

 
    @Test
    void testLogout() {
        assertDoesNotThrow(() -> userDAO.logout());
    }
}