package persistence;

import model.Entry;
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
    private Entry entryTravel;
    private Entry entryGroceries;

    @BeforeEach
    void setUp() {
        initEntries();
        initSpendingList();
    }

    @ParameterizedTest
    @ValueSource(strings = {"./data/n|@$$oFile", "./data/myFi|\0le.txt", "./f\\s123124ile.json"})
    void testWriteUnacceptableFileName(String fileName) {
        JsonWriter jsonWriter = new JsonWriter(fileName);
        assertThrows(IOException.class, jsonWriter::open);
    }

    @Test
    void testWriteEmptyFile() {
        String path = "./data/testWriterEmptyFile.json";

        try (JsonWriter writer = new JsonWriter(path)){
            writer.open();
            writer.write(new SpendingList());
        } catch (FileNotFoundException e) {
            fail("File actually exists");
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            SpendingList fromFile = reader.read();
            assertEquals(new SpendingList(), fromFile);
        } catch (IOException | NegativeAmountException | NameException e) {
            fail("File exists and is not corrupted");
            e.printStackTrace();
        }
    }

    @Test
    void testWriteEmptySpendingList() {
        Stream.of(entryGroceries, entryTravel).forEach(spToWrite::removeEntry);
        String path = "./data/testWriteEmptySpendingList.json";

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
    }

    @Test
    void testWriteEmptyCategories() {
        Stream.of(entryGroceries, entryTravel)
                .map(Entry::getCategory)
                .forEach(spToWrite::removeCategory);

        String path = "./data/testWriteEmptyCategory.json";
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
    }

    @Test
    void testWriteRegularFile() {
        String path = "./data/testWriteRegularFile.json";

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
    }

    // EFFECTS: inits test entries
    private void initEntries() {
        try {
            entryTravel = new Entry("Went to Toronto", 401.34, "Travel");
            Thread.sleep(10);
            entryGroceries = new Entry("Went to SaveOnFoods", 100.76, "Groceries");
            Thread.sleep(10);
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initSpendingList() {
        spToWrite = new SpendingList();
        try {
            spToWrite.addEntry(entryTravel);
            Thread.sleep(10);
            spToWrite.addEntry(entryGroceries);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}