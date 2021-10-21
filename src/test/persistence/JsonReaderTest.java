//package persistence;
//
//import model.exceptions.NameException;
//import model.exceptions.NegativeAmountException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.fail;
//
//class JsonReaderTest {
//
//    @ParameterizedTest
//    @ValueSource(strings = {"./data/n|@$$oFile", "./data/myFi|\0le.txt", "./data/f\\s123124ile.json",
//                            "./data/noFile", "./data/myFile.txt", "./data/f123124ile.json"})
//    void testReadInvalidFileName(String filePath) {
//        JsonReader reader = new JsonReader(filePath);
//        assertThrows(Exception.class, reader::openFile);
//    }
//
//    @Test
//    void testReaderCorruptedCategories() {
//        String path = "./data/testReadCorruptCategories.json";
//        JsonReader reader = new JsonReader(path);
//        try {
//            reader.openFile();
//        } catch (Exception e) {
//            fail("Should open file without any problems");
//        }
//        assertThrows(NameException.class, reader::readCategories);
//    }
//
//    @Test
//    void testReaderCorruptedAmountInSpendingList() {
//        String path = "./data/testReaderCorruptedAmountInSpendingList.json";
//        JsonReader reader = new JsonReader(path);
//        try {
//            reader.openFile();
//        } catch (Exception e) {
//            fail("Should open file without any problems");
//        }
//        assertThrows(NegativeAmountException.class, reader::readSpendingList);
//    }
//
//    @Test
//    void testReaderCorruptedTitleInSpendingList() {
//        String path = "./data/testReaderCorruptedTitleInSpendingList.json";
//        JsonReader reader = new JsonReader(path);
//        try {
//            reader.openFile();
//        } catch (Exception e) {
//            fail("Should open file without any problems");
//        }
//        assertThrows(NameException.class, reader::readSpendingList);
//    }
//
//}