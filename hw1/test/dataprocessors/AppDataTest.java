/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

import java.nio.file.Path;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aaronknoll
 */
public class AppDataTest {
    
    public AppDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of loadData method, of class AppData.
     */
    @Test
    public void testLoadData_Path() {
        System.out.println("loadData");
        Path dataFilePath = null;
        AppData instance = null;
        instance.loadData(dataFilePath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadData method, of class AppData.
     */
    @Test
    public void testLoadData_String() {
        System.out.println("loadData");
        String dataString = "";
        AppData instance = null;
        boolean expResult = false;
        boolean result = instance.loadData(dataString);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveData method, of class AppData.
     */
    @Test
    public void testSaveData() {
        System.out.println("saveData");
        Path dataFilePath = null;
        AppData instance = null;
        instance.saveData(dataFilePath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class AppData.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        AppData instance = null;
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of displayData method, of class AppData.
     */
    @Test
    public void testDisplayData() {
        System.out.println("displayData");
        AppData instance = null;
        instance.displayData();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
