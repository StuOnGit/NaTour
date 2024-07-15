package com.example.natour.mytests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.natour.controllers.ControllerRegistration;
import com.example.natour.interfacce.PasswordError;

import org.junit.Test;
// Test white box.
public class CheckPasswordTest {

    final String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$"; // 8 caratteri di cui 1 maiuscolo, 1 speciale e un numero
    final ControllerRegistration controllerRegistration = new ControllerRegistration();
    @Test
    public void testCheckPasswordEmpty(){
        int error = controllerRegistration.checkPassword("", regex, "Password");
        assertEquals(PasswordError.EMPTY, error);
    }

    @Test
    public void testCheckPasswordScorrectRegex(){
        int error = controllerRegistration.checkPassword("Password", regex, "Password");
        assertEquals(PasswordError.REGEX, error);
    }

    @Test
    public void testCheckPasswordNotEquale(){
        int error = controllerRegistration.checkPassword("Password1@", regex, "Password");
        assertEquals(PasswordError.NOT_EQUAL, error);
    }

    @Test
    public void testCheckPasswordCorrect(){
        Integer error = controllerRegistration.checkPassword("Password1@", regex, "Password1@");
        assertNull(error);
    }

}
