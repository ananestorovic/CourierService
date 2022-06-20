package rs.etf.sab.student.operations;

import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.enums.FuelType;
import rs.etf.sab.student.sql_helper.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_VehicleOperations implements VehicleOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_VehicleOperations.class.getName());

    public na170675_VehicleOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumption, BigDecimal capacity) {
        boolean retVal = false;
        if (isLicencePlateValid(licencePlateNumber) && isFuelTypeValid(fuelType) && isFuelConsumptionValid(fuelConsumption) && isCapacityValid(capacity)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("INSERT INTO Vehicle (licencePlate, fuelType, fuelConsumption, capacity) values (?, ?, ?, ?)")
            ) {
                ps.setString(1, licencePlateNumber);
                ps.setInt(2, fuelType);
                ps.setBigDecimal(3, fuelConsumption);
                ps.setBigDecimal(4, capacity);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isCapacityValid(BigDecimal capacity) {
        return capacity != null && BigDecimal.ZERO.compareTo(capacity) <= 0;
    }

    private boolean isFuelConsumptionValid(BigDecimal fuelConsumption) {
        return fuelConsumption != null && BigDecimal.ZERO.compareTo(fuelConsumption) <= 0;
    }

    private boolean isFuelTypeValid(int fuelType) {
        return Arrays.stream(FuelType.values()).map(FuelType::ordinal).anyMatch(one -> one.equals(fuelType));
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        int deletedVehiclesCount = 0;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE from Vehicle WHERE licencePlate = ?")
        ) {
            for (String licencePlate : licencePlateNumbers) {
                if (licencePlate != null && !licencePlate.isEmpty()) {
                    ps.setString(1, licencePlate);
                    deletedVehiclesCount += ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return deletedVehiclesCount;
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> vehicles = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT licencePlate FROM Vehicle");
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                vehicles.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return vehicles;
    }

    @Override
    public boolean changeFuelType(String licencePlate, int newFuelType) {
        boolean retVal = false;
        if (isLicencePlateValid(licencePlate) && isVehicleParked(licencePlate) && isFuelTypeValid(newFuelType)) {
            try (PreparedStatement ps = mConnection.prepareStatement("Update Vehicle set fuelType=? where licencePlate=?")) {
                ps.setInt(1, newFuelType);
                ps.setString(2, licencePlate);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isVehicleParked(String licencePlate) {
        boolean retVal = false;
        int vehicleId = getVehicleId(licencePlate);
        if (vehicleId != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT idWarehouse FROM VehicleInWareHouse WHERE IdVehicle=?")
            ) {
                ps1.setInt(1, vehicleId);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        retVal = true;
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isLicencePlateValid(String licencePlate) {
        return licencePlate != null;
    }

    @Override
    public boolean changeConsumption(String licencePlate, BigDecimal newConsumption) {
        boolean retVal = false;
        if (isLicencePlateValid(licencePlate) && isVehicleParked(licencePlate) && isFuelConsumptionValid(newConsumption)) {
            try (PreparedStatement ps = mConnection.prepareStatement("Update Vehicle set fuelConsumption=? where licencePlate=?")) {
                ps.setBigDecimal(1, newConsumption);
                ps.setString(2, licencePlate);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public boolean changeCapacity(String licencePlate, BigDecimal newCapacity) {
        boolean retVal = false;
        if (isLicencePlateValid(licencePlate) && isVehicleParked(licencePlate) && isCapacityValid(newCapacity)) {
            try (PreparedStatement ps = mConnection.prepareStatement("Update Vehicle set capacity=? where licencePlate=?")) {
                ps.setBigDecimal(1, newCapacity);
                ps.setString(2, licencePlate);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public boolean parkVehicle(String licencePlateNumber, int idWareHouse) {
        boolean retVal = false;
        int vehicleId = getVehicleId(licencePlateNumber);
        if (vehicleId != -1 && !isVehicleInDrive(vehicleId)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("INSERT INTO VehicleInWareHouse(IdVehicle, idWarehouse) VALUES (?, ?)")
            ) {
                ps.setInt(1, vehicleId);
                ps.setInt(2, idWareHouse);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isVehicleInDrive(int idVehicle) {
        boolean retVal = false;
        try (
                PreparedStatement ps1 = mConnection.prepareStatement("SELECT IdVehicle FROM VehicleInDrive WHERE IdVehicle = ?")
        ) {
            ps1.setInt(1, idVehicle);
            try (
                    ResultSet rs = ps1.executeQuery()) {
                retVal = rs.next();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }

        return retVal;
    }

    private int getVehicleId(String licencePlateNumber) {
        int retVal = -1;
        if (licencePlateNumber != null) {
            try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT IdVehicle FROM Vehicle WHERE licencePlate=?")) {
                ps1.setString(1, licencePlateNumber);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        retVal = rs.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

}
