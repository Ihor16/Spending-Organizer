package persistence;

import model.Categories;
import model.Category;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonWriterTest {

    private SpendingList spToWrite;
    private Record recordTravel;
    private Record recordGroceries;
    private Categories categories;

    @BeforeEach
    void setUp() {
        initEntries();
        initSpendingList();
    }

    @ParameterizedTest
    @ValueSource(strings = {"./data/testing/n|@$$oFile", "./data/testing/myFi|\0le.txt", "./f\\s123124ile.json"})
    void testWriteUnacceptableFileName(String fileName) {
        JsonWriter jsonWriter = new JsonWriter(fileName);
        assertThrows(IOException.class, jsonWriter::open);
    }

    @Test
    void testWriteEmptyFile() {
        String path = "./data/testing/testWriterEmptyFile.json";
        Categories categories = new Categories();

        try (JsonWriter writer = new JsonWriter(path)){
            writer.open();
            writer.write(new SpendingList(categories));
        } catch (FileNotFoundException e) {
            fail("File actually exists");
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            SpendingList fromFile = reader.read();
            assertEquals(new SpendingList(categories), fromFile);
        } catch (IOException | NegativeAmountException | NameException e) {
            fail("File exists and is not corrupted");
            e.printStackTrace();
        }
        assertEquals(1, categories.getCategories().size());
    }

    @Test
    void testWriteEmptySpendingList() {
        Stream.of(recordGroceries, recordTravel).forEach(spToWrite::removeRecord);
        String path = "./data/testing/testWriteEmptySpendingList.json";
        Categories categories = new Categories();

        try (JsonWriter writer = new JsonWriter(path)){
            writer.open();
            writer.write(spToWrite);
        } catch (FileNotFoundException e) {
            fail("File actually exists");
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            SpendingList fromFile = reader.read();
            assertEquals(spToWrite, fromFile);
        } catch (IOException | NegativeAmountException | NameException e) {
            fail("File exists and is not corrupted");
            e.printStackTrace();
        }
        assertEquals(1, categories.getCategories().size());
    }

    @Test
    void testWriteRegularFile() {
        String path = "./data/testing/testWriteRegularFile.json";

        try (JsonWriter writer = new JsonWriter(path)){
            writer.open();
            writer.write(spToWrite);
        } catch (FileNotFoundException e) {
            fail("File actually exists");
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            SpendingList fromFile = reader.read();
            assertEquals(spToWrite.getCategories(), fromFile.getCategories());
            assertEquals(spToWrite.getRecords(), fromFile.getRecords());
        } catch (IOException | NegativeAmountException | NameException e) {
            fail("File exists and is not corrupted");
            e.printStackTrace();
        }
        assertEquals(3, categories.getCategories().size());
    }

    @Test
    void testWriteRegularFileChangedDefaultCategory() {
        String path = "./data/testing/testWriteRegularFileChangedDefaultCategory.json";
        try {
            new Category("new default", categories, true, true);
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }

        try (JsonWriter writer = new JsonWriter(path)){
            writer.open();
            writer.write(spToWrite);
        } catch (FileNotFoundException e) {
            fail("File actually exists");
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            SpendingList fromFile = reader.read();
            assertEquals(spToWrite.getRecords(), fromFile.getRecords());
            assertNotEquals(spToWrite.getCategories().getDefaultCategory(),
                    fromFile.getCategories().getDefaultCategory());
            assertEquals(spToWrite.getCategories().getCategories().size() - 1,
                    fromFile.getCategories().getCategories().size());
        } catch (IOException | NegativeAmountException | NameException e) {
            fail("File exists and is not corrupted");
            e.printStackTrace();
        }
    }

    // EFFECTS: inits test entries
    private void initEntries() {
        categories = new Categories();
        try {
            Category categoryTravel = new Category("Travel", categories);
            Category categoryGroceries = new Category("Groceries", categories);
            recordTravel = new Record("Went to Toronto", 401.34, categoryTravel);
            Thread.sleep(10);
            recordGroceries = new Record("Went to SaveOnFoods", 100.76, categoryGroceries);
            Thread.sleep(10);
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initSpendingList() {
        spToWrite = new SpendingList(categories);
        try {
            spToWrite.addRecord(recordTravel);
            Thread.sleep(10);
            spToWrite.addRecord(recordGroceries);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}