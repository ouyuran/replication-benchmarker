package jbenchmarker.rgaRDSL;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RgaRDSLDocumentTest {
    @Test
    public void testview() 	{
        System.out.println("Test RGADocument ...");
        RgaRDSLDocument doc = new RgaRDSLDocument();

        assertEquals("", doc.view());
    }
}
