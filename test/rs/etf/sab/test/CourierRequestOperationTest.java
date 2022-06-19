//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.etf.sab.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.student.operations.*;

public class CourierRequestOperationTest {
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private UserOperations userOperations;
    private CourierRequestOperation courierRequestOperation;

    public CourierRequestOperationTest() {
    }

    @Before
    public void setUp() {
        this.cityOperations = new na170675_CityOperations();
        this.addressOperations = new na170675_AddressOperations();
        this.userOperations = new na170675_UserOperations();
        this.courierRequestOperation = new na170675_CourierRequestOperation();
        this.generalOperations = new na170675_GeneralOperations();
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    String insertUser() {
        String street = "Bulevar kralja Aleksandra";
        int number = 73;
        int idCity = this.cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1L, (long) idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 10, 10);
        Assert.assertNotEquals(-1L, (long) idAddress);
        String username = "crno.dete";
        String firstName = "Svetislav";
        String lastName = "Kisprdilov";
        String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }

    String insertUser2() {
        String street = "Vojvode Stepe";
        int number = 73;
        int idCity = this.cityOperations.insertCity("Nis", "70000");
        Assert.assertNotEquals(-1L, (long) idCity);
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, 100, 100);
        Assert.assertNotEquals(-1L, (long) idAddress);
        String username = "crno.dete.2";
        String firstName = "Svetislav";
        String lastName = "Kisprdilov";
        String password = "Test_123";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }

    @Test
    public void insertCourierRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void insertCourierRequest_NoUser() {
        String username = "crno.dete";
        String driverLicenceNumber = "1234567";
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void insertCourierRequest_RequestExists() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void insertCourierRequest_AlreadyCourier() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void grantRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void grantRequest_NoRequest() {
        String username = "crno.dete";
        String driverLicenceNumber = "1234567";
        Assert.assertFalse(this.courierRequestOperation.grantRequest(username));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void insertCourierRequest_multipleDifferentLicence() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        String username2 = this.insertUser2();
        String driverLicenceNumber2 = "1234561";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username2, driverLicenceNumber2));
        Assert.assertEquals(2L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username2));
    }

    @Test
    public void insertCourierRequest_multipleSameLicence() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        String username2 = this.insertUser2();
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.insertCourierRequest(username2, driverLicenceNumber));
        Assert.assertEquals(1L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username2));
    }

    @Test
    public void deleteCourierRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertEquals(1L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertTrue(this.courierRequestOperation.getAllCourierRequests().contains(username));
        Assert.assertTrue(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void deleteCourierRequest_NoRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertFalse(this.courierRequestOperation.deleteCourierRequest(username));
        Assert.assertEquals(0L, (long) this.courierRequestOperation.getAllCourierRequests().size());
        Assert.assertFalse(this.courierRequestOperation.getAllCourierRequests().contains(username));
    }

    @Test
    public void changeLicenceInCourierRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        String newDriverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username, newDriverLicenceNumber));
    }

    @Test
    public void changeLicenceInCourierRequest_NoUser() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        String newDriverLicenceNumber = "1234567";
        String username2 = "crno.dete.2";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertFalse(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username2, newDriverLicenceNumber));
    }

    @Test
    public void changeLicenceInCourierRequest_NoRequest() {
        String username = this.insertUser();
        String driverLicenceNumber = "1234567";
        String newDriverLicenceNumber = "1234567";
        Assert.assertTrue(this.courierRequestOperation.insertCourierRequest(username, driverLicenceNumber));
        Assert.assertTrue(this.courierRequestOperation.grantRequest(username));
        Assert.assertFalse(this.courierRequestOperation.changeDriverLicenceNumberInCourierRequest(username, newDriverLicenceNumber));
    }
}
