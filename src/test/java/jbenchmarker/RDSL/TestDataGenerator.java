package jbenchmarker.RDSL;

import java.util.Random;

public class TestDataGenerator {
    private int mean;
    private int min;
    private int max;
    private int stdDev;
    private Random random;
    public TestDataGenerator(int mean, int stdDev) {
        this.mean = mean;
        this.min = 1;
        this.stdDev = stdDev;
        this.random = new Random();
    }
    private int getRandomLength() {
        int value = (int) (random.nextGaussian() * stdDev + mean);
        return Math.max(min, value);
    }

    public TestDataElement[] generateEndInsert(int number) {
        int totalLength = 0;
        TestDataElement[] testData = new TestDataElement[number];
        for(int i = 0; i < number; i++) {
            int length = this.getRandomLength();
            String s = "a".repeat(length);
            testData[i] = new TestDataElement(s, totalLength);
            totalLength += length;
        }
        return testData;
    }

    public TestDataElement[] generateRandomInsert(int number) {
        int totalLength = 0;
        TestDataElement[] testData = new TestDataElement[number];
        for(int i = 0; i < number; i++) {
            int length = this.getRandomLength();
            String s = "a".repeat(length);
            int pos = (int) (Math.random() * (totalLength + 1));
            testData[i] = new TestDataElement(s, pos);
            totalLength += length;
        }
        return testData;
    }

}
