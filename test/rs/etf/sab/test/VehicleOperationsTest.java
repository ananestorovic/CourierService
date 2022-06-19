//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.etf.sab.test;

import java.math.BigDecimal;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.operations.*;
import rs.etf.sab.tests.TestHandler;

public class VehicleOperationsTest {
    private GeneralOperations generalOperations;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private StockroomOperations stockroomOperations;
    private VehicleOperations vehicleOperations;
    private TestHandler testHandler;

    public VehicleOperationsTest() {
    }

    @Before
    public void setUp() {
        this.cityOperations = new na170675_CityOperations();
        this.addressOperations = new na170675_AddressOperations();
        this.stockroomOperations = new na170675_StockroomOperations();
        this.vehicleOperations = new na170675_VehicleOperations();
        this.generalOperations = new na170675_GeneralOperations();
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    int insertStockroom() {
        String street = "Bulevar kralja Aleksandra";
        int number = 73;
        int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, (long)idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, (long)idAddress);
        int idStockroom = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long)idStockroom);
        Assert.assertEquals(1L, (long)this.stockroomOperations.getAllStockrooms().size());
        return idStockroom;
    }

    @Test
    public void insertVehicle() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }

    @Test
    public void insertVehicle_UniqueLicencePlateNumber() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertFalse(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }

    @Test
    public void deleteVehicles() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
        Assert.assertEquals(1L, (long)this.vehicleOperations.deleteVehicles(new String[]{licencePlateNumber}));
        Assert.assertEquals(0L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
    }

    @Test
    public void parkVehicle() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }

    @Test
    public void parkVehicle_NoVehicle() {
        String licencePlateNumber = "BG1675DA";
        int idStockroom = this.insertStockroom();
        Assert.assertFalse(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }

    @Test
    public void parkVehicle_NoStockroom() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Random random = new Random();
        int idStockroom = random.nextInt();
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }

    @Test
    public void changeFuelType() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
        int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeFuelType(licencePlateNumber, 2));
    }

    @Test
    public void changeConsumption() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
        int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeConsumption(licencePlateNumber, new BigDecimal(7.3)));
    }

    @Test
    public void changeCapacity() {
        String licencePlateNumber = "BG1675DA";
        BigDecimal fuelConsumption = new BigDecimal(6.3);
        BigDecimal capacity = new BigDecimal(100.5);
        int fuelType = 1;
        Assert.assertFalse(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertEquals(1L, (long)this.vehicleOperations.getAllVehichles().size());
        Assert.assertFalse(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
        int idStockroom = this.insertStockroom();
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
        Assert.assertTrue(this.vehicleOperations.changeCapacity(licencePlateNumber, new BigDecimal(107.3)));
    }
}
