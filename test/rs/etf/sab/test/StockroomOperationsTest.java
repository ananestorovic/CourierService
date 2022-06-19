//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.etf.sab.test;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.student.operations.na170675_AddressOperations;
import rs.etf.sab.student.operations.na170675_CityOperations;
import rs.etf.sab.student.operations.na170675_GeneralOperations;
import rs.etf.sab.student.operations.na170675_StockroomOperations;

public class StockroomOperationsTest {
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private StockroomOperations stockroomOperations;

    public StockroomOperationsTest() {
    }

    @Before
    public void setUp() {
        this.cityOperations = new na170675_CityOperations();
        this.addressOperations = new na170675_AddressOperations();
        this.stockroomOperations = new na170675_StockroomOperations();
        this.generalOperations = new na170675_GeneralOperations();
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    int insertAddress() {
        String street = "Bulevar kralja Aleksandra";
        int number = 73;
        int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, (long) idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, (long) idAddress);
        Assert.assertEquals(1L, (long) this.addressOperations.getAllAddresses().size());
        return idAddress;
    }

    int insertAddress_SameCity() {
        String street = "Kraljice Natalije";
        int number = 37;
        Assert.assertEquals(1L, (long) this.cityOperations.getAllCities().size());
        int idCity = (Integer) this.cityOperations.getAllCities().get(0);
        Assert.assertNotEquals(-1L, (long) idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 30, 30);
        Assert.assertNotEquals(-1L, (long) idAddress);
        return idAddress;
    }

    int insertAddress_DifferentCity() {
        String street = "Vojvode Stepe";
        int number = 73;
        int idCity = this.cityOperations.insertCity("Nis", "700000");
        Assert.assertNotEquals(-1L, (long) idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 100, 100);
        Assert.assertNotEquals(-1L, (long) idAddress);
        return idAddress;
    }

    @Test
    public void insertStockroom_OnlyOne() {
        int idAddress = this.insertAddress();
        int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) rowId);
        Assert.assertEquals(1L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }

    @Test
    public void insertStockrooms_SameCity() {
        int idAddress = this.insertAddress();
        int idAddress2 = this.insertAddress_SameCity();
        int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) rowId);
        Assert.assertEquals(-1L, (long) this.stockroomOperations.insertStockroom(idAddress2));
        Assert.assertEquals(1L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }

    @Test
    public void insertStockrooms_DifferentCity() {
        int idAddress = this.insertAddress();
        int idAddress2 = this.insertAddress_DifferentCity();
        int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) rowId);
        int rowId2 = this.stockroomOperations.insertStockroom(idAddress2);
        Assert.assertNotEquals(-1L, (long) rowId);
        Assert.assertEquals(2L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId2));
    }

    @Test
    public void deleteStockroom() {
        int idAddress = this.insertAddress();
        int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) rowId);
        Assert.assertEquals(1L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertTrue(this.stockroomOperations.deleteStockroom(rowId));
        Assert.assertEquals(0L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertFalse(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }

    @Test
    public void deleteStockroom_NoStockroom() {
        Random random = new Random();
        int rowId = random.nextInt();
        Assert.assertFalse(this.stockroomOperations.deleteStockroom(rowId));
        Assert.assertEquals(0L, (long) this.stockroomOperations.getAllStockrooms().size());
    }

    @Test
    public void deleteStockroomFromCity() {
        int idAddress = this.insertAddress();
        int rowId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) rowId);
        Assert.assertEquals(1L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(rowId));
        Assert.assertEquals(1L, (long) this.cityOperations.getAllCities().size());
        int idCity = (Integer) this.cityOperations.getAllCities().get(0);
        Assert.assertEquals((long) rowId, (long) this.stockroomOperations.deleteStockroomFromCity(idCity));
        Assert.assertEquals(0L, (long) this.stockroomOperations.getAllStockrooms().size());
        Assert.assertFalse(this.stockroomOperations.getAllStockrooms().contains(rowId));
    }

    @Test
    public void deleteStockroomFromCity_NoCity() {
        Random random = new Random();
        int rowId = random.nextInt();
        Assert.assertEquals(-1L, (long) this.stockroomOperations.deleteStockroomFromCity(rowId));
        Assert.assertEquals(0L, (long) this.stockroomOperations.getAllStockrooms().size());
    }

    @Test
    public void deleteStockroomFromCity_NoStockroom() {
        int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, (long) idCity);
        Assert.assertEquals(-1L, (long) this.stockroomOperations.deleteStockroomFromCity(idCity));
        Assert.assertEquals(0L, (long) this.stockroomOperations.getAllStockrooms().size());
    }
}
