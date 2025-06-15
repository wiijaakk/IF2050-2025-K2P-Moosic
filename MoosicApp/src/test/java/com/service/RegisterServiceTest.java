package com.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    RegisterService service = new RegisterService();

    @Test
    void testValidUsername() {
        assertTrue(service.isValidUsername("irdina123"));
        assertFalse(service.isValidUsername("a")); 
    }

    @Test
    void testValidPassword() {
        assertTrue(service.isValidPassword("Passw0rd!"));
        assertFalse(service.isValidPassword("short"));
    }

    @Test
    void testPasswordMatch() {
        assertTrue(service.isPasswordMatch("abc123", "abc123"));
        assertFalse(service.isPasswordMatch("abc", "xyz"));
    }

    @Test
    void testValidEmail() {
        assertTrue(service.isValidEmail("irdina@example.com"));
        assertFalse(service.isValidEmail("invalid.email@"));
    }
}
