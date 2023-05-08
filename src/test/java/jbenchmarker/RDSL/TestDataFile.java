package jbenchmarker.RDSL;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class TestDataFile {
    public static String filePath = "/Users/yurano/tmp/";

    public static TestDataElement[] getTestDataFromFile(String fullPath) {
        try {
            FileInputStream fis = new FileInputStream(fullPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            TestDataElement[] t = (TestDataElement[]) obj;
            return t;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeTestData(String fullpath, Serializable testData) {
        File outFile = new File(fullpath);
        if(outFile.exists()) outFile.delete();
        try {
            outFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(fullpath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(testData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateEndInsert(int number) {
        TestDataGenerator g = new TestDataGenerator(10, 3);
        TestDataElement[] testData = g.generateEndInsert(number);
        String filename = filePath + "end" + number;
        this.writeTestData(filename, testData);
    }

    private void generateRandomInsert(int number) {
        TestDataGenerator g = new TestDataGenerator(10, 3);
        TestDataElement[] testData = g.generateRandomInsert(number);
        String filename = filePath + "random" + number;
        this.writeTestData(filename, testData);
    }

    @Test
    public void generateEndInsert100() {
        generateEndInsert(100);
    }

    @Test
    public void generateEndInsert1k() {
        generateEndInsert(1000);
    }

    @Test
    public void generateEndInsert10k() {
        generateEndInsert(10000);
    }

    @Test
    public void generateEndInsert100k() {
        generateEndInsert(100000);
    }

    @Test
    public void generateRandomInsert100() {
        generateRandomInsert(100);
    }

    @Test
    public void generateRandomInsert1000() {
        generateRandomInsert(1000);
    }

    @Test
    public void generateRandomInsert10k() {
        generateRandomInsert(10000);
    }
    @Test
    public void generateRandomInsert100k() {
        generateRandomInsert(100000);
    }

    @Test
    public void generateRandomInsert50k() {
        generateRandomInsert(50000);
    }

    @Test
    public void generateRandomInsert80k() {
        generateRandomInsert(80000);
    }
}
