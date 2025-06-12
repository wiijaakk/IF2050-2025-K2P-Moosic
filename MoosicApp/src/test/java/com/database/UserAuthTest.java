package com.database;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserAuthTest {

    UserAuth userAuth = new UserAuthMock(); // Pakai versi mock

    @Test
    void testLoginBerhasil() {
        String result = userAuth.login("admin", "admin123");
        assertTrue(result.contains("Berhasil"));
    }

    @Test
    void testLoginGagalPasswordSalah() {
        String result = userAuth.login("admin", "salah");
        assertTrue(result.contains("Gagal"));
    }

    @Test
    void testLoginGagalUsernameSalah() {
        String result = userAuth.login("unknown", "admin123");
        assertTrue(result.contains("Gagal"));
    }
}
