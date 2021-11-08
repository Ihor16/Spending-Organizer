package persistence;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonReaderTest {

    @ParameterizedTest
    @ValueSource(strings = {"./data/testing/noFile", "./data/testing/myFile.txt", "./data/testing/f123124ile.json"})
    void testReadInvalidPath(String path) {
        JsonReader reader = new JsonReader(path);
        assertThrows(IOException.class, reader::read);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "./data/testing/testReadCorruptCategories.json",
            "./data/testing/testReadCorruptCategoriesInRecords.json"
    })
    void testReaderCorruptedCategories(String path) {
        JsonReader reader = new JsonReader(path);
        assertThrows(NameException.class, reader::read);
    }

    @Test
    void testReaderCorruptedAmountInSpendingList() {
        String path = "./data/testing/testReaderCorruptedAmountInSpendingList.json";
        JsonReader reader = new JsonReader(path);
        assertThrows(NegativeAmountException.class, reader::read);
    }

    @Test
    void testReaderCorruptedTitleInSpendingList() {
        String path = "./data/testing/testReaderCorruptedTitleInSpendingList.json";
        JsonReader reader = new JsonReader(path);
        assertThrows(NameException.class, reader::read);
    }
}