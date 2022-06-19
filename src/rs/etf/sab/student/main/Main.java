package rs.etf.sab.student.main;

import rs.etf.sab.operations.*;
import rs.etf.sab.student.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;
import rs.etf.sab.student.sql_helper.DB;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {


    public static void main(String[] args) {
        StudentMain.main(args);
    }

    public static class StudentMain {

        public static void main(String[] args) {

            AddressOperations addressOperations = new na170675_AddressOperations();
            CityOperations cityOperations = new na170675_CityOperations();
            CourierOperations courierOperations =  new na170675_CourierOperations();
            CourierRequestOperation courierRequestOperation = new na170675_CourierRequestOperation();
            DriveOperation driveOperation = new na170675_DriveOperation();
            GeneralOperations generalOperations = new na170675_GeneralOperations();
            PackageOperations packageOperations = new na170675_PackageOperations();
            StockroomOperations stockroomOperations = new na170675_StockroomOperations();
            UserOperations userOperations = new na170675_UserOperations();
            VehicleOperations vehicleOperations = new na170675_VehicleOperations();


            TestHandler.createInstance(
                    addressOperations,
                    cityOperations,
                    courierOperations,
                    courierRequestOperation,
                    driveOperation,
                    generalOperations,
                    packageOperations,
                    stockroomOperations,
                    userOperations,
                    vehicleOperations);

            TestRunner.runTests();
        }
    }
}
