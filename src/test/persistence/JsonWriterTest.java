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
import java.util.Formatter;
import java.util.Random;

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
        try {
            initSpendingList();
        } catch (NegativeAmountException | NameException e) {
            fail("Spending list is initialised correctly");
        }
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
        try {
            initCategories();
        } catch (NameException e) {
            fail("Categories are actually valid");
            e.printStackTrace();
        }
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
        try {
            initSpendingList();
            initCategories();
        } catch (NegativeAmountException | NameException e) {
            fail("Spending list and categories are actually initialised correctly");
            e.printStackTrace();
        }
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

    private void initCategories() throws NameException {
        categories = new Categories();
        categories.addCategory("Travel");
        categories.addCategory("Groceries");
    }

    private void initSpendingList() throws NegativeAmountException, NameException {
        spendingList = new SpendingList();
        spendingList.addEntry(new Entry("Went to Toronto",
                400.42, "Travel"));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        spendingList.addEntry(new Entry("Went to SaveOnFoods",
                56.93, "Groceries"));
    }
}