package jbenchmarker.RDSL;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class GenerateDate {

    @Test
    public void generate100() {
        TestDataGenerator g = new TestDataGenerator(10, 3);
        TestDataElement[] testData = g.generateEndInsert(100);
        String filename = "/Users/ouyuran/tmp/100";
        File outFile = new File(filename);
        if(outFile.exists()) outFile.delete();
        try {
            outFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(testData);

            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            TestDataElement[] t = (TestDataElement[]) obj;
            int i = 1;
            } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
