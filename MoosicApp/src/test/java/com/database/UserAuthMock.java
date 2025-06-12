package com.database;

/**
 * Versi mock dari UserAuth untuk keperluan unit test tanpa koneksi database.
 */
public class UserAuthMock extends UserAuth {

    @Override
    public String login(String usernameOrEmail, String password) {
        if (usernameOrEmail.equals("admin") && password.equals("admin123")) {
            return "Login Berhasil sebagai admin!";
        } else {
            return "Login Gagal: Username atau password salah.";
        }
    }
}