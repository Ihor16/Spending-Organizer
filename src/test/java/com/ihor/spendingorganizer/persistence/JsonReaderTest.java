package com.ihor.spendingorganizer.persistence;

import com.ihor.spendingorganizer.model.exceptions.NameException;
import com.ihor.spendingorganizer.model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonReaderTest {

    @ParameterizedTest
    @ValueSource(strings = {"noFile", "myFile.txt", "f123124ile.json"})
    void testReadInvalidPath(String path) {
        JsonReader reader = new JsonReader(path);
        assertThrows(IOException.class, reader::read);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/resources/testReadCorruptCategories.json",
            "src/test/resources/testReadCorruptCategoriesInRecords.json"
    })
    void testReaderCorruptedCategories(String path) {
        JsonReader reader = new JsonReader(path);
        assertThrows(NameException.class, reader::read);
    }

    @Test
    void testReaderCorruptedAmountInSpendingList() {
        String path = "src/test/resources/testReaderCorruptedAmountInSpendingList.json";
        JsonReader reader = new JsonReader(path);
        assertThrows(NegativeAmountException.class, reader::read);
    }

    @Test
    void testReaderCorruptedTitleInSpendingList() {
        String path = "src/test/resources/testReaderCorruptedTitleInSpendingList.json";
        JsonReader reader = new JsonReader(path);
        assertThrows(NameException.class, reader::read);
    }
}