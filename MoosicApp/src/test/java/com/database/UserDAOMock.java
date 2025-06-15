package com.database;

import com.model.User; // Pastikan Anda memiliki kelas User di package com.model

/**
 * Versi mock dari UserDAO untuk keperluan unit test tanpa koneksi database.
 * Ini meniru perilaku UserDAO, mengembalikan nilai berdasarkan kondisi tertentu.
 */
public class UserDAOMock extends UserDAO {

    // Override metode login
    @Override
    public String login(String usernameOrEmail, String password) {
        if (("admin".equals(usernameOrEmail) || "admin@example.com".equals(usernameOrEmail)) && "admin123".equals(password)) {
            return "Login Berhasil sebagai admin!";
        } else if (("user".equals(usernameOrEmail) || "user@example.com".equals(usernameOrEmail)) && "user123".equals(password)) {
            return "Login Berhasil sebagai customer!";
        } else {
            return "Login Gagal: Username atau password salah.";
        }
    }

    // Override metode register
    @Override
    public boolean register(String username, String password, String email, String fullName, String address) {
        // Untuk mock, kita bisa simulasikan beberapa skenario:
        // - username/email yang sudah ada
        // - registrasi berhasil
        if ("existing_user".equals(username) || "existing@example.com".equals(email)) {
            System.out.println("UserDAOMock: Registration failed. Username or email already exists in mock DB.");
            return false; // Simulasikan username atau email sudah ada
        } else if ("error_case".equals(username)) {
            System.err.println("UserDAOMock: Simulating a registration error.");
            return false; // Simulasikan kegagalan registrasi lainnya
        } else {
            System.out.println("UserDAOMock: New user registered successfully in mock DB: " + username);
            return true; // Simulasikan registrasi berhasil
        }
    }

    // Metode logout bisa di-override jika perlu memverifikasi log di test, tapi untuk sekarang tidak perlu perubahan signifikan
    @Override
    public void logout() {
        System.out.println("UserDAOMock: User logged out (mocked).");
    }
}