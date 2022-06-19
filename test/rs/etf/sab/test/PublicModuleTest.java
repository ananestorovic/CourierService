//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.etf.sab.test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.operations.*;

public class PublicModuleTest {
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private UserOperations userOperations;
    private CourierRequestOperation courierRequestOperation;
    private VehicleOperations vehicleOperations;
    private CourierOperations courierOperation;
    private StockroomOperations stockroomOperations;
    private PackageOperations packageOperations;
    private DriveOperation driveOperation;
    Map<Integer, Pair<Integer, Integer>> addressesCoords = new HashMap();
    Map<Integer, BigDecimal> packagePrice = new HashMap();

    public PublicModuleTest() {
    }

    @Before
    public void setUp() {
        this.cityOperations = new na170675_CityOperations();
        this.addressOperations = new na170675_AddressOperations();
        this.userOperations = new na170675_UserOperations();
        this.courierRequestOperation = new na170675_CourierRequestOperation();
        this.courierOperation = new na170675_CourierOperations();
        this.vehicleOperations = new na170675_VehicleOperations();
        this.stockroomOperations = new na170675_StockroomOperations();
        this.packageOperations = new na170675_PackageOperations();
        this.driveOperation = new na170675_DriveOperation();
        this.generalOperations = new na170675_GeneralOperations();
        this.generalOperations.eraseAll();
    }

    @After
    public void tearUp() {
        this.generalOperations.eraseAll();
    }

    int insertCity(String name, String postalCode) {
        int idCity = this.cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1L, (long) idCity);
        Assert.assertTrue(this.cityOperations.getAllCities().contains(idCity));
        return idCity;
    }

    int insertAddress(String street, int number, int idCity, int x, int y) {
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, x, y);
        Assert.assertNotEquals(-1L, (long) idAddress);
        Assert.assertTrue(this.addressOperations.getAllAddresses().contains(idAddress));
        this.addressesCoords.put(idAddress, new Pair(x, y));
        return idAddress;
    }

    String insertUser(String username, String firstName, String lastName, String password, int idAddress) {
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }

    String insertCourier(String username, String firstName, String lastName, String password, int idAddress, String driverLicenceNumber) {
        this.insertUser(username, firstName, lastName, password, idAddress);
        Assert.assertTrue(this.courierOperation.insertCourier(username, driverLicenceNumber));
        return username;
    }

    public void insertAndParkVehicle(String licencePlateNumber, BigDecimal fuelConsumption, BigDecimal capacity, int fuelType, int idStockroom) {
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }

    public int insertStockroom(int idAddress) {
        int stockroomId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1L, (long) stockroomId);
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(stockroomId));
        return stockroomId;
    }

    int insertAndAcceptPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
        int idPackage = this.packageOperations.insertPackage(addressFrom, addressTo, userName, packageType, weight);
        Assert.assertNotEquals(-1L, (long) idPackage);
        Assert.assertTrue(this.packageOperations.acceptAnOffer(idPackage));
        Assert.assertTrue(this.packageOperations.getAllPackages().contains(idPackage));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage));
        BigDecimal price = Util.getPackagePrice(packageType, weight, Util.getDistance(new Pair[]{(Pair) this.addressesCoords.get(addressFrom), (Pair) this.addressesCoords.get(addressTo)}));
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(1.05))) < 0);
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(0.95))) > 0);
        this.packagePrice.put(idPackage, price);
        return idPackage;
    }

    @Test
    public void publicOne() {
        int BG = this.insertCity("Belgrade", "11000");
        int KG = this.insertCity("Kragujevac", "550000");
        int VA = this.insertCity("Valjevo", "14000");
        int CA = this.insertCity("Cacak", "32000");
        int idAddressBG1 = this.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        int idAddressBG2 = this.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        int idAddressBG3 = this.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        int idAddressBG4 = this.insertAddress("Takovska", 7, BG, 11, 12);
        this.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        int idAddressKG1 = this.insertAddress("Daniciceva", 1, KG, 4, 310);
        int idAddressKG2 = this.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        int idAddressVA1 = this.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        this.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        this.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        int idAddressCA1 = this.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        this.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        int idStockroomBG = this.insertStockroom(idAddressBG1);
        int idStockroomVA = this.insertStockroom(idAddressVA1);
        this.insertAndParkVehicle("BG1675DA", new BigDecimal(6.3), new BigDecimal(1000.5), 2, idStockroomBG);
        this.insertAndParkVehicle("VA1675DA", new BigDecimal(7.3), new BigDecimal(500.5), 1, idStockroomVA);
        String username = "crno.dete";
        this.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        String courierUsernameBG = "postarBG";
        this.insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");
        String courierUsernameVA = "postarVA";
        this.insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressBG2, "123456");
        int type1 = 0;
        BigDecimal weight1 = new BigDecimal(2);
        int idPackage1 = this.insertAndAcceptPackage(idAddressBG2, idAddressCA1, username, type1, weight1);
        int type2 = 1;
        BigDecimal weight2 = new BigDecimal(4);
        int idPackage2 = this.insertAndAcceptPackage(idAddressBG3, idAddressVA1, username, type2, weight2);
        int type3 = 2;
        BigDecimal weight3 = new BigDecimal(5);
        int idPackage3 = this.insertAndAcceptPackage(idAddressBG4, idAddressKG1, username, type3, weight3);
        Assert.assertEquals(0L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertTrue(this.courierOperation.getCouriersWithStatus(1).contains(courierUsernameBG));
        int type4 = 3;
        BigDecimal weight4 = new BigDecimal(2);
        int idPackage4 = this.insertAndAcceptPackage(idAddressBG2, idAddressKG2, username, type4, weight4);
        Assert.assertEquals(4L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(3L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(1L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(2L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(3L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage2, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage1, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(CA).size());
        Assert.assertEquals(1L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage3, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertNotEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(KG).size());
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllUndeliveredPackages().size());
        Assert.assertTrue(this.packageOperations.getAllUndeliveredPackages().contains(idPackage4));
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(0).size());
        double distance = Util.getDistance(new Pair[]{(Pair) this.addressesCoords.get(idAddressBG1), (Pair) this.addressesCoords.get(idAddressBG2), (Pair) this.addressesCoords.get(idAddressBG3), (Pair) this.addressesCoords.get(idAddressBG4), (Pair) this.addressesCoords.get(idAddressVA1), (Pair) this.addressesCoords.get(idAddressCA1), (Pair) this.addressesCoords.get(idAddressKG1), (Pair) this.addressesCoords.get(idAddressBG1)});
        BigDecimal profit = ((BigDecimal) this.packagePrice.get(idPackage1)).add((BigDecimal) this.packagePrice.get(idPackage2)).add((BigDecimal) this.packagePrice.get(idPackage3));
        profit = profit.subtract((new BigDecimal(36)).multiply(new BigDecimal(6.3)).multiply(new BigDecimal(distance)));
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(1.05))) < 0);
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(0.95))) > 0);
    }

    @Test
    public void publicTwo() {
        int BG = this.insertCity("Belgrade", "11000");
        int KG = this.insertCity("Kragujevac", "550000");
        int VA = this.insertCity("Valjevo", "14000");
        int CA = this.insertCity("Cacak", "32000");
        int idAddressBG1 = this.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        int idAddressBG2 = this.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        int idAddressBG3 = this.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        int idAddressBG4 = this.insertAddress("Takovska", 7, BG, 11, 12);
        this.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        int idAddressKG1 = this.insertAddress("Daniciceva", 1, KG, 4, 310);
        int idAddressKG2 = this.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        int idAddressVA1 = this.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        int idAddressVA2 = this.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        int idAddressVA3 = this.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        int idAddressCA1 = this.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        int idAddressCA2 = this.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        int idStockroomBG = this.insertStockroom(idAddressBG1);
        int idStockroomVA = this.insertStockroom(idAddressVA1);
        this.insertAndParkVehicle("BG1675DA", new BigDecimal(6.3), new BigDecimal(1000.5), 2, idStockroomBG);
        this.insertAndParkVehicle("VA1675DA", new BigDecimal(7.3), new BigDecimal(500.5), 1, idStockroomVA);
        String username = "crno.dete";
        this.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        String courierUsernameBG = "postarBG";
        this.insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");
        String courierUsernameVA = "postarVA";
        this.insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressVA2, "123456");
        int type = 1;
        BigDecimal weight = new BigDecimal(4);
        int idPackage1 = this.insertAndAcceptPackage(idAddressBG2, idAddressKG1, username, type, weight);
        int idPackage2 = this.insertAndAcceptPackage(idAddressKG2, idAddressBG4, username, type, weight);
        int idPackage3 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        int idPackage4 = this.insertAndAcceptPackage(idAddressCA2, idAddressBG4, username, type, weight);
        Assert.assertEquals(0L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        this.driveOperation.planingDrive(courierUsernameBG);
        this.driveOperation.planingDrive(courierUsernameVA);
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals((long) idPackage1, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals((long) idPackage3, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(KG).contains(idPackage1));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(CA).contains(idPackage3));
        int idPackage5 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        int idPackage6 = this.insertAndAcceptPackage(idAddressBG3, idAddressVA3, username, type, weight);
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertFalse(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage6));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage2));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage6));
        Assert.assertEquals((long) idPackage2, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals((long) idPackage6, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage6));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, (long) this.packageOperations.getAllUndeliveredPackagesFromCity(BG).size());
        Assert.assertEquals(3L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage5));
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals((long) idPackage4, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals((long) idPackage5, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, (long) this.packageOperations.getAllUndeliveredPackages().size());
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(0).size());
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(1).compareTo(new BigDecimal(0)) > 0);
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(5).compareTo(new BigDecimal(0)) > 0);
    }
}
