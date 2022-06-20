package rs.etf.sab.student.operations;

import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.student.sql_helper.DB;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_StockroomOperations implements StockroomOperations {
    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_StockroomOperations.class.getName());

    public na170675_StockroomOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public int insertStockroom(int idAddress) {
        int retVal = -1;
        if (!warehouseExistInCity(idAddress)) {
            try (PreparedStatement ps = mConnection.prepareStatement("INSERT INTO WarehouseLocation(idAddress) values(?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idAddress);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
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

    private boolean warehouseExistInCity(int idAddress) {
        boolean retVal = false;
        int idCity = getAddressCity(idAddress);
        if (idCity != -1) {
            try (PreparedStatement ps = mConnection.prepareStatement("SELECT * FROM WarehouseLocation INNER JOIN Address A on A.IdAddress = WarehouseLocation.idAddress WHERE A.idCity = ?")) {
                ps.setInt(1, idCity);
                try (ResultSet rs = ps.executeQuery()) {
                    retVal = rs.next();
                }

            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public boolean deleteStockroom(int idWareHouse) {
        boolean retVal = false;
        if (isEmpty(idWareHouse)) {
            try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM WarehouseLocation WHERE idWarehouse = ?")) {
                ps.setInt(1, idWareHouse);
                retVal = ps.executeUpdate() > 0;

            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isEmpty(int idWareHouse) {
        boolean haveSomething  = haveSomePackage(idWareHouse);
        if (!haveSomething){
            haveSomething = haveSomeVehicle(idWareHouse);
        }
        return !haveSomething;
    }

    private boolean haveSomePackage(int idWareHouse) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT * FROM PackageInWareHouse WHERE idWareHouse = ?")
        ) {
            ps.setInt(1, idWareHouse);
            try (
                    ResultSet rs = ps.executeQuery();
            ) {
                retVal = rs.next();

            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private boolean haveSomeVehicle(int idWareHouse) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT * FROM VehicleInWareHouse WHERE idWareHouse = ?")
        ) {
            ps.setInt(1, idWareHouse);
            try (
                    ResultSet rs = ps.executeQuery();
            ) {
                retVal = rs.next();

            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    @Override
    public int deleteStockroomFromCity(int idCity) {
        int retVal = -1;
        int idWareHouse = getWareHouse(idCity);
        if (idWareHouse != -1 && deleteStockroom(idWareHouse)) {
            retVal = idWareHouse;
        }
        return retVal;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        List<Integer> wareHouses = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT idWarehouse FROM WarehouseLocation");
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                wareHouses.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return wareHouses;
    }


    public int getWareHouse(int idCity) {
        int retVal = -1;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT WarehouseLocation.idWareHouse FROM WarehouseLocation INNER JOIN Address ON WarehouseLocation.idAddress = Address.IdAddress WHERE Address.idCity = ?")
        ) {
            ps.setInt(1, idCity);
            try (
                    ResultSet resultSet = ps.executeQuery();
            ) {
                if (resultSet.next()) {
                    retVal = resultSet.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private int getAddressCity(int idAddress) {
        int retVal = -1;
        try (
                PreparedStatement ps1 = mConnection.prepareStatement("SELECT idCity FROM Address WHERE IdAddress=?")
        ) {
            ps1.setInt(1, idAddress);
            try (
                    ResultSet rs = ps1.executeQuery()
            ) {
                if (rs.next()) {
                    retVal = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

}
