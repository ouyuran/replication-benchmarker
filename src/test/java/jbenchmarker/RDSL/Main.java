package jbenchmarker.RDSL;

import crdt.PreconditionException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.*;
import jbenchmarker.logootRDSL.LogootRDSLMerge;
import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rgasplit.RgaSMerge;
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

    @Before
    public void setUp() throws Exception {
        this.rga = (jbenchmarker.rga.RGAMerge) new RGAFactory().create(REPLICA_ID);
        this.rgaTree = (jbenchmarker.rgaTreeList.RGAMerge) new RGATreeListFactory().create(REPLICA_ID);
        this.rgaRDSL = (jbenchmarker.rgaRDSL.RgaRDSLMerge) new RgaRDSLFactory().create(REPLICA_ID);

        this.rgaSplit = (jbenchmarker.rgasplit.RgaSMerge) new RGASplitFactory().create(REPLICA_ID);
        this.rgaSplitTree = (jbenchmarker.rgaTreeSplitBalanced.RgaSMerge) new RGATreeSplitBalancedFactory().create(REPLICA_ID);
        this.rgaSplitRDSL = (jbenchmarker.rgaSplitRDSL.RgaSplitRDSLMerge) new RgaSplitRDSLFactory().create(REPLICA_ID);

        this.logoot = (jbenchmarker.logoot.LogootMerge) new LogootFactory().create(REPLICA_ID);
        this.logootTree = (jbenchmarker.logoot.tree.LogootTreeMerge) new LogootTreeFactory().create(REPLICA_ID);
        this.logootRDSL = (jbenchmarker.logootRDSL.LogootRDSLMerge) new LogootRDSLFactory().create(REPLICA_ID);
    }

    private void runTestCase(MergeAlgorithm replica, TestDataElement[] testData) throws PreconditionException {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for(TestDataElement t : testData) {
            replica.applyLocal(SequenceOperation.insert(t.getPos(), t.getContent()));
        }
        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        NumberFormat formatter = NumberFormat.getInstance();
        System.out.println(GraphLayout.parseInstance(replica.getDoc()).toFootprint());
        System.out.println("Execution time     : " + formatter.format(endTime - startTime) + " nanoseconds");
        System.out.println("Memory total usage : " + formatter.format(Runtime.getRuntime().totalMemory()) + " bytes");
        System.out.println("Memory usage       : " + formatter.format(endMemory - startMemory) + " bytes");
    }

    @Test
    public void testRgaEndInsert100() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100");
        runTestCase(rga, testData);
    }

    @Test
    public void testRgaTreeEndInsert100() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100");
        runTestCase(rgaTree, testData);
    }

    @Test
    public void testRgaRDSLEndInsert100() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100");
        runTestCase(rgaRDSL, testData);
    }

    @Test
    public void testRgaEndInsert1000() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
        runTestCase(rga, testData);
    }
    @Test
    public void testRgaTreeEndInsert1000() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
        runTestCase(rgaTree, testData);
    }
    @Test
    public void testRgaRDSLEndInsert1000() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end1000");
        runTestCase(rgaRDSL, testData);
    }

    @Test
    public void testRgaEndInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
        runTestCase(rga, testData);
    }

    @Test
    public void testRgaTreeEndInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
        runTestCase(rgaTree, testData);
    }

    @Test
    public void testRgaRDSLEndInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end10000");
        runTestCase(rgaRDSL, testData);
    }

    @Test
    public void testRgaEndInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
        runTestCase(rga, testData);
    }

    @Test
    public void testRgaTreeEndInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
        runTestCase(rgaTree, testData);
    }

    @Test
    public void testRgaRDSLEndInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "end100000");
        runTestCase(rgaRDSL, testData);
    }

    @Test
    public void testRgaRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rga, testData);
    }

    @Test
    public void testRgaTreeRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rgaTree, testData);
    }

    @Test
    public void testRgaRDSLRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rgaRDSL, testData);
    }

    @Test
    public void testRgaSplitRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rgaSplit, testData);
    }

    @Test
    public void testRgaSplitTreeRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rgaSplitTree, testData);
    }

    @Test
    public void testRgaSplitRDSLRandomInsert100k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random100000");
        runTestCase(rgaSplit, testData);
    }

    @Test
    public void testRgaSplitRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(rgaSplit, testData);
    }
    @Test
    public void testRgaSplitTreeRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(rgaSplitTree, testData);
    }

    @Test
    public void testRgaSplitRDSLRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(rgaSplitRDSL, testData);
    }

    @Test
    public void testLogootRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(logoot, testData);
    }
    @Test
    public void testLogootTreeRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(logootTree, testData);
    }

    @Test
    public void testLogootRDSLRandomInsert10k() throws PreconditionException {
        TestDataElement[] testData = TestDataFile.getTestDataFromFile(TestDataFile.filePath + "random10000");
        runTestCase(logootRDSL, testData);
    }

}
