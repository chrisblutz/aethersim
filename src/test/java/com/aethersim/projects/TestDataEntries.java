package com.aethersim.projects;

import com.aethersim.projects.io.data.DataValue;
import com.aethersim.projects.io.data.exceptions.DataException;
import com.aethersim.tests.annotations.AetherSimTest;
import com.aethersim.tests.annotations.AetherSimTests;
import com.aethersim.tests.annotations.ProjectTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@ProjectTests
@AetherSimTests("Data Entries")
public class TestDataEntries {

    @AetherSimTest("Null Value (Prohibited)")
    void testNullValue() {
        // Try to create a DataValue using a null string, and expect an exception
        Assertions.assertThrows(DataException.class, () -> {
            DataValue.from(null);
        });
    }

    @AetherSimTest("String Value")
    void testStringValue() {
        // Test that strings get stored properly (and can be retrieved properly)
        String data = "This-Is_A Test.String";
        DataValue value = DataValue.from(data);
        Assertions.assertEquals(data, value.getString());
    }

    @AetherSimTest("Boolean Value")
    void testBooleanValue() {
        // Test that values get stored properly (and can be retrieved properly)
        Assertions.assertTrue(DataValue.from(true).getBoolean());
        Assertions.assertFalse(DataValue.from(false).getBoolean());

        // Test that converting to string succeeds
        Assertions.assertEquals("true", DataValue.from(true).getString());
        Assertions.assertEquals("false", DataValue.from(false).getString());

        // Test that converting from string succeeds on valid values, but that invalid values fail
        Assertions.assertTrue(DataValue.from("true").getBoolean());
        Assertions.assertFalse(DataValue.from("false").getBoolean());
        Assertions.assertTrue(DataValue.from("TrUe").getBoolean());
        Assertions.assertFalse(DataValue.from("fAlSe").getBoolean());
        Assertions.assertThrows(DataException.class, () -> DataValue.from("NotABoolean").getBoolean());

        // Test that converting from char succeeds on valid values, but that invalid values fail
        Assertions.assertTrue(DataValue.from('t').getBoolean());
        Assertions.assertFalse(DataValue.from('f').getBoolean());
        Assertions.assertTrue(DataValue.from('T').getBoolean());
        Assertions.assertFalse(DataValue.from('F').getBoolean());
        Assertions.assertThrows(DataException.class, () -> DataValue.from('z').getBoolean());

        // Test that converting from numeric values succeeds
        Assertions.assertFalse(DataValue.from(0).getBoolean());
        Assertions.assertTrue(DataValue.from(1).getBoolean());
        Assertions.assertTrue(DataValue.from(-1).getBoolean());
        Assertions.assertFalse(DataValue.from(0L).getBoolean());
        Assertions.assertTrue(DataValue.from(1L).getBoolean());
        Assertions.assertTrue(DataValue.from(-1L).getBoolean());
        Assertions.assertFalse(DataValue.from(0f).getBoolean());
        Assertions.assertTrue(DataValue.from(1f).getBoolean());
        Assertions.assertTrue(DataValue.from(-1f).getBoolean());
        Assertions.assertFalse(DataValue.from(0d).getBoolean());
        Assertions.assertTrue(DataValue.from(1d).getBoolean());
        Assertions.assertTrue(DataValue.from(-1d).getBoolean());
    }

    @AetherSimTest("Character Value")
    void testCharacterValue() {
        // Test that values get stored properly (and can be retrieved properly)
        char data = '%';
        DataValue value = DataValue.from(data);
        Assertions.assertEquals(data, value.getChar());

        // Test that converting to and from string succeeds
        Assertions.assertEquals(Character.toString(data), value.getString());
        Assertions.assertEquals('$', DataValue.from("$").getChar());
    }

    @AetherSimTest("Integer Value")
    void testIntegerValue() {
        // Test that values get stored properly (and can be retrieved properly)
        int data = 123456;
        DataValue value = DataValue.from(data);
        Assertions.assertEquals(data, value.getInt());

        // Test that converting to string succeeds
        value = DataValue.from(data);
        Assertions.assertEquals(Integer.toString(data), value.getString());
        String intString = Integer.toString(data, 16);
        value = DataValue.from(intString);
        Assertions.assertEquals(intString, value.getString());
        Assertions.assertEquals(data, value.getInt(16));

        // Test that converting from string succeeds
        Assertions.assertEquals(24680, DataValue.from("24680").getInt());
        Assertions.assertEquals(0xABCD, DataValue.from("ABCD").getInt(16));
    }

    @AetherSimTest("Long Value")
    void testLongValue() {
        // Test that values get stored properly (and can be retrieved properly)
        long data = 1234567890123L;
        DataValue value = DataValue.from(data);
        Assertions.assertEquals(data, value.getLong());
//        value = DataValue.from(data, 16);
//        Assertions.assertEquals(data, value.getLong(16));

        // Test that converting to string succeeds
        value = DataValue.from(data);
        Assertions.assertEquals(Long.toString(data), value.getString());
//        value = DataValue.from(data, 16);
//        Assertions.assertEquals(Long.toString(data, 16), value.getString());

        // Test that converting from string succeeds
        Assertions.assertEquals(2468013579L, DataValue.from("2468013579").getLong());
        Assertions.assertEquals(0xABCDABCDABCDL, DataValue.from("ABCDABCDABCD").getLong(16));
    }

    @AetherSimTest("Float Value")
    void testFloatValue() {
        // Test that values get stored properly (and can be retrieved properly)
        float data = 1.234567f;
        DataValue value = DataValue.from(data);
        Assertions.assertTrue(Math.abs(data - value.getFloat()) < 0.0000001);
    }

    @AetherSimTest("Double Value")
    void testDoubleValue() {
        // Test that values get stored properly (and can be retrieved properly)
        double data = 1.2345678901d;
        DataValue value = DataValue.from(data);
        Assertions.assertTrue(Math.abs(data - value.getDouble()) < 0.00000000001);
    }
}
