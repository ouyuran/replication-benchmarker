package jbenchmarker.RDSL;

import crdt.PreconditionException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.*;
import jbenchmarker.logootRDSL.LogootRDSLDocument;
import jbenchmarker.logootRDSL.LogootRDSLMerge;
import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rgasplit.RgaSMerge;
import jbenchmarker.rgasplit.RgaSNode;
import org.junit.Before;
import org.junit.Test;
import org.openjdk.jol.info.GraphLayout;

import java.text.NumberFormat;

public class Main {
    private static final int REPLICA_ID = 7;
    private jbenchmarker.rga.RGAMerge rga;
    private jbenchmarker.rgaTreeList.RGAMerge rgaTree;
    private jbenchmarker.rgaRDSL.RgaRDSLMerge rgaRDSL;

    private jbenchmarker.rgasplit.RgaSMerge rgaSplit;
    private jbenchmarker.rgaTreeSplitBalanced.RgaSMerge rgaSplitTree;
    private jbenchmarker.rgaSplitRDSL.RgaSplitRDSLMerge rgaSplitRDSL;

    private jbenchmarker.logoot.LogootMerge logoot;
    private jbenchmarker.logoot.tree.LogootTreeMerge logootTree;
    private jbenchmarker.logootRDSL.LogootRDSLMerge logootRDSL;
    private int defaultRunTimes = 10;

    private MergeAlgorithm createReplica(String type) {
        switch (type) {
            case "rga":
                return new RGAFactory().create(REPLICA_ID);
            case "rgaTree":
                return new RGATreeListFactory().create(REPLICA_ID);
            case "rgaRDSL":
                return new RgaRDSLFactory().create(REPLICA_ID);
            case "rgaSplit":
                return new RGASplitFactory().create(REPLICA_ID);
            case "rgaSplitTree":
                return new RGATreeSplitBalancedFactory().create(REPLICA_ID);
            case "rgaSplitRDSL":
                return new RgaSplitRDSLFactory().create(REPLICA_ID);
            case "logoot":
                return new LogootFactory().create(REPLICA_ID);
            case "logootTree":
                return new LogootTreeFactory().create(REPLICA_ID);
            case "logootRDSL":
                return new LogootRDSLFactory().create(REPLICA_ID);
            default:
                return null;
        }
    }

    private void preRun() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runTestCase(String replicaType, String testDataFile) throws PreconditionException {
        doRunTestCase(replicaType, testDataFile, defaultRunTimes);
    }

    private void runTestCase(String replicaType, String testDataFile, int runTimes) throws PreconditionException {
        doRunTestCase(replicaType, testDataFile, runTimes);
    }

    private void doRunTestCase(String replicaType, String testDataFile, int runTimes) throws PreconditionException {
        long totalTimeSpent = 0, totalMemorySpent = 0;
        NumberFormat formatter = NumberFormat.getInstance();
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + testDataFile);
        long memorySpent = 0;
        for(int i = 0; i <= runTimes; i++) {
            MergeAlgorithm replica = createReplica(replicaType);
            long[] result = runTestCaseOneTime(replica, testData, i == 0);
            // First run normally take much longer time for env setup, ignore it
            if(i > 0) {
                totalTimeSpent += result[0];
//                totalMemorySpent += result[1];
            } else {
                // only measure memory once
                memorySpent = result[1];
            }
        }
        long avgTimeSpent = (totalTimeSpent / runTimes);
//        long avgMemorySpent = (totalMemorySpent / runTimes);
        System.out.println(String.format("Replica %s run case %s, avg time %s, avg Memory %s. (%d, %d)",
                replicaType,
                testDataFile,
                formatter.format(avgTimeSpent),
                formatter.format(memorySpent),
                avgTimeSpent,
                memorySpent
        ));
    }


    private long[] runTestCaseOneTime(MergeAlgorithm replica, TestDataElement[] testData, boolean measureMemory) throws PreconditionException {
        NumberFormat formatter = NumberFormat.getInstance();
        preRun();
        long startTime = System.nanoTime();
        for(TestDataElement t : testData) {
            replica.applyLocal(SequenceOperation.insert(t.getPos(), t.getContent()));
        }
        long endTime = System.nanoTime();
        long timeSpent = endTime - startTime;
        long memorySpent = 0;
        if(measureMemory) {
            GraphLayout g = GraphLayout.parseInstance(replica.getDoc());
            System.out.println(g.toFootprint());
            memorySpent = g.totalSize();
        }
        System.out.println("Doc total length: " + replica.lookup().length());
        System.out.println(String.format("Time %s, Memory %s", formatter.format(timeSpent), formatter.format(memorySpent)));
        return new long[]{timeSpent, memorySpent};
    }

    @Test
    public void testG() {
//        int[] i = new int[100];
//        short[] s = new short[100];
//        System.out.println(GraphLayout.parseInstance(i).toFootprint());
//        System.out.println(GraphLayout.parseInstance(s).toFootprint());
//        RDSLNode n1 = new RDSLNode(new RgaSNode<String>(), 6);
//        System.out.println(GraphLayout.parseInstance(n1).toFootprint());
        System.out.println(System.getProperty("java.vendor") + System.getProperty("java.version"));
    }
    @Test
    public void testRgaEndInsert() throws PreconditionException {
        runTestCase("rga", "end100000");
    }

    @Test
    public void testRgaRandomInsert() throws PreconditionException {
        runTestCase("rga", "random1000");
    }

    @Test
    public void testRgaTreeEndInsert() throws PreconditionException {
        runTestCase("rgaTree", "end100000", 10);
    }

    @Test
    public void testRgaTreeRandomInsert() throws PreconditionException {
        runTestCase("rgaTree", "random100000", 10);
    }

    @Test
    public void testRgaRDSLEndInsert() throws PreconditionException {
        runTestCase("rgaRDSL", "end10000", 10);
    }

    @Test
    public void testRgaRDSLRandomInsert() throws PreconditionException {
        runTestCase("rgaRDSL", "random100");
        runTestCase("rgaRDSL", "random1000");
        runTestCase("rgaRDSL", "random10000");
        runTestCase("rgaRDSL", "random100000");
    }

    @Test
    public void all() throws PreconditionException {
//        runTestCase("rga", "random1000", 1);
//        runTestCase("rgaTree", "random1000", 1);
        runTestCase("rgaRDSL", "random1000", 1);
//        runTestCase("rgaSplit", "random100");
//        runTestCase("rgaSplit", "random1000");
//        runTestCase("rgaSplit", "random10000");
//        runTestCase("rgaSplit", "random100000", 1);
//        runTestCase("rgaSplitTree", "random100");
//        runTestCase("rgaSplitTree", "random1000");
//        runTestCase("rgaSplitTree", "random10000");
//        runTestCase("rgaSplitTree", "random100000");
//        runTestCase("rgaSplitRDSL", "random100");
//        runTestCase("rgaSplitRDSL", "random1000");
//        runTestCase("rgaSplitRDSL", "random10000");
//        runTestCase("rgaSplitRDSL", "random100000", 1);
//        runTestCase("logoot", "random100");
//        runTestCase("logoot", "random1000");
//        runTestCase("logoot", "random10000");
//        runTestCase("logoot", "random100000");
//        runTestCase("logootTree", "random100");
//        runTestCase("logootTree", "random1000");
//        runTestCase("logootTree", "random10000");
//        runTestCase("logootTree", "random100000");
//        runTestCase("logootRDSL", "random100");
//        runTestCase("logootRDSL", "random1000");
//        runTestCase("logootRDSL", "random10000");
//        runTestCase("logootRDSL", "random80000");
    }


//    @Test
//    public void testRgaTreeEndInsert100() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100");
//        runTestCase(rgaTree, testData);
//    }
//
//    @Test
//    public void testRgaRDSLEndInsert100() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100");
//        runTestCase(rgaRDSL, testData);
//    }
//
//    @Test
//    public void testRgaEndInsert1000() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
//        runTestCase(rga, testData);
//    }
//    @Test
//    public void testRgaTreeEndInsert1000() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
//        runTestCase(rgaTree, testData);
//    }
//    @Test
//    public void testRgaRDSLEndInsert1000() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
//        runTestCase(rgaRDSL, testData);
//    }
//
//    @Test
//    public void testRgaEndInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
//        runTestCase(rga, testData);
//    }
//
//    @Test
//    public void testRgaTreeEndInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
//        runTestCase(rgaTree, testData);
//    }
//
//    @Test
//    public void testRgaRDSLEndInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
//        runTestCase(rgaRDSL, testData);
//    }
//
//    @Test
//    public void testRgaEndInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
//        runTestCase(rga, testData);
//    }
//
//    @Test
//    public void testRgaTreeEndInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
//        runTestCase(rgaTree, testData);
//    }
//
//    @Test
//    public void testRgaRDSLEndInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
////        runTestCase(rgaRDSL, testData);
//        long total = 0;
//        for(int i = 0; i< 100; i++) {
//            jbenchmarker.rgaRDSL.RgaRDSLMerge re = (jbenchmarker.rgaRDSL.RgaRDSLMerge) new RgaRDSLFactory().create(REPLICA_ID);
////            total += runTestCase(re, testData);
//        }
//        System.out.println(total);
//    }
//
//    @Test
//    public void testRgaRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rga, testData);
//    }
//
//    @Test
//    public void testRgaTreeRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rgaTree, testData);
//    }
//
//    @Test
//    public void testRgaRDSLRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rgaRDSL, testData);
//    }
//
//    @Test
//    public void testRgaSplitRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rgaSplit, testData);
//    }
//
//    @Test
//    public void testRgaSplitTreeRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rgaSplitTree, testData);
//    }
//
//    @Test
//    public void testRgaSplitRDSLRandomInsert100k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
//        runTestCase(rgaSplit, testData);
//    }
//
//    @Test
//    public void testRgaSplitRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(rgaSplit, testData);
//    }
//    @Test
//    public void testRgaSplitTreeRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(rgaSplitTree, testData);
//    }
//
//    @Test
//    public void testRgaSplitRDSLRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(rgaSplitRDSL, testData);
//    }
//
//    @Test
//    public void testLogootRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(logoot, testData);
//    }
//    @Test
//    public void testLogootTreeRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(logootTree, testData);
//    }
//
//    @Test
//    public void testLogootRDSLRandomInsert10k() throws PreconditionException {
//        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
//        runTestCase(logootRDSL, testData);
//    }

}
