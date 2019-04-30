/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.nio.file.Path;
import java.util.Map;
import javafx.geometry.Point2D;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aaronknoll
 */
public class DataSetTest {
    
    public DataSetTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getLabels method, of class DataSet.
     */
    @Test
    public void testGetLabels() {
        System.out.println("getLabels");
        DataSet instance = new DataSet();
        Map<String, String> expResult = null;
        Map<String, String> result = instance.getLabels();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocations method, of class DataSet.
     */
    @Test
    public void testGetLocations() {
        System.out.println("getLocations");
        DataSet instance = new DataSet();
        Map<String, Point2D> expResult = null;
        Map<String, Point2D> result = instance.getLocations();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateLabel method, of class DataSet.
     */
    @Test
    public void testUpdateLabel() {
        System.out.println("updateLabel");
        String instanceName = "";
        String newlabel = "";
        DataSet instance = new DataSet();
        instance.updateLabel(instanceName, newlabel);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fromTSDFile method, of class DataSet.
     */
    @Test
    public void testFromTSDFile() throws Exception {
        System.out.println("fromTSDFile");
        Path tsdFilePath = null;
        DataSet expResult = null;
        DataSet result = DataSet.fromTSDFile(tsdFilePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBounds method, of class DataSet.
     */
    @Test
    public void testGetBounds() {
        System.out.println("getBounds");
        String boundName = "";
        DataSet instance = new DataSet();
        double expResult = 0.0;
        double result = instance.getBounds(boundName);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
