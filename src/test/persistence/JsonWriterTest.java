package persistence;

import model.Categories;
import model.Entry;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private Categories categories;
    private SpendingList spendingList;

    @ParameterizedTest
    @ValueSource(strings = {"./data/n|@$$oFile", "./data/myFi|\0le.txt", "./f\\s123124ile.json"})
    void testWriteUnacceptableFileName(String filePath) {
        JsonWriter jsonWriter = new JsonWriter(filePath);
        assertThrows(IOException.class, jsonWriter::open);
    }

    @Test
    void testWriteEmptyFile() {
        String path = "./data/testWriterEmptyFile.json";
        try (JsonWriter jsonWriter = new JsonWriter(path)) {
            jsonWriter.open();
            jsonWriter.write(new SpendingList(), new Categories());
        } catch (FileNotFoundException e) {
            fail("File should be found");
        }

        try {
            JsonReader reader = new JsonReader(path);
            reader.openFile();

            SpendingList fileSpendingList = reader.readSpendingList();
            Categories fileCategories = reader.readCategories();

            assertEquals(new SpendingList(), fileSpendingList);
            assertEquals(new Categories(), fileCategories);
        } catch (IOException e) {
            fail("Shouldn't fail");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Categories are not corrupted");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Spending entries in the file are actually not corrupted");
        }
    }

    @Test
    void testWriteEmptyCategory() {
        initSpendingList();
        String path = "./data/testWriteEmptyCategory.json";
        try (JsonWriter jsonWriter = new JsonWriter(path)) {
            jsonWriter.open();
            jsonWriter.write(spendingList, new Categories());
        } catch (FileNotFoundException e) {
            fail("File should be found");
        }

        try {
            JsonReader reader = new JsonReader(path);
            reader.openFile();

            SpendingList fileSpendingList = reader.readSpendingList();
            Categories fileCategories = reader.readCategories();

            assertEquals(spendingList, fileSpendingList);
            assertEquals(new Categories(), fileCategories);
        } catch (IOException e) {
            fail("Shouldn't fail");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Categories are not corrupted");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Spending entries in the file are actually not corrupted");
        }
    }

    @Test
    void testWriteEmptySpendingList() {
        initCategories();
        String path = "./data/testWriteEmptySpendingList.json";
        try (JsonWriter jsonWriter = new JsonWriter(path)) {
            jsonWriter.open();
            jsonWriter.write(new SpendingList(), categories);
        } catch (FileNotFoundException e) {
            fail("File should be found");
        }

        try {
            JsonReader reader = new JsonReader(path);
            reader.openFile();

            SpendingList fileSpendingList = reader.readSpendingList();
            Categories fileCategories = reader.readCategories();

            assertEquals(new SpendingList(), fileSpendingList);
            assertEquals(categories, fileCategories);
        } catch (IOException e) {
            fail("Shouldn't fail");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Categories are not corrupted");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Spending entries in the file are actually not corrupted");
        }
    }

    @Test
    void testWriteRegularFile() {
        initSpendingList();
        initCategories();
        String path = "./data/testWriteRegularFile.json";
        try (JsonWriter jsonWriter = new JsonWriter(path)) {
            jsonWriter.open();
            jsonWriter.write(spendingList, categories);
        } catch (FileNotFoundException e) {
            fail("File should be found");
        }

        try {
            JsonReader reader = new JsonReader(path);
            reader.openFile();

            SpendingList fileSpendingList = reader.readSpendingList();
            Categories fileCategories = reader.readCategories();

            assertEquals(spendingList, fileSpendingList);
            assertEquals(categories, fileCategories);
        } catch (IOException e) {
            fail("Shouldn't fail");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Categories are not corrupted");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Spending entries in the file are actually not corrupted");
        }
    }

    @Test
    void testReaderAddNewEntryAndRetreave() {
        initCategories();
        initSpendingList();
        try {
            Entry entry = new Entry("Theatre", 40, "Rest");
            spendingList.addEntry(entry);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually valid");
        }
        String path = "./data/testReaderAddNewEntryAndRetreave.json";
        try(JsonWriter writer = new JsonWriter(path)) {
            writer.open();
            writer.write(spendingList, categories);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(path);
            reader.openFile();
            assertEquals(spendingList, reader.readSpendingList());
            assertEquals(categories, reader.readCategories());
        } catch (NegativeAmountException e) {
            fail("Spending list in the file is actually valid");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Spending list and categories names in the file are actually valid");
        } catch (IOException e) {
            fail("Should open the file");
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: initialises categories
    private void initCategories() {
        categories = new Categories();
        try {
            categories.addCategory("Travel");
            categories.addCategory("Groceries");
        } catch (NameException e) {
            fail("Categories are actually valid");
        }
    }

    // MODIFIES: this
    // EFFECTS: initialises spending list
    private void initSpendingList() {
        spendingList = new SpendingList();
        try {
            spendingList.addEntry(new Entry("Went to Toronto",
                    400.42, "Travel"));
            Thread.sleep(10);
            spendingList.addEntry(new Entry("Went to SaveOnFoods",
                    56.93, "Groceries"));
            Thread.sleep(10);
        } catch (NameException | NegativeAmountException e) {
            fail("Spending list is initialised correctly");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}