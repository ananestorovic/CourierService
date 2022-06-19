package rs.etf.sab.student.operations;

import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.student.enums.PackageStatus;
import rs.etf.sab.student.enums.PackageType;
import rs.etf.sab.student.helpers.Util;
import rs.etf.sab.student.sql_helper.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_PackageOperations implements PackageOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_PackageOperations.class.getName());

    public na170675_PackageOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, java.math.BigDecimal weight) {
        int retVal = -1;
        int idUser = Util.getUserId(userName);
        if (idUser != -1 && isValidType(packageType)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("INSERT INTO RequestToDelivery(idUser, type, deliveryAdress, pickUpAdress, weight) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
            ) {
                ps.setInt(1, idUser);
                ps.setInt(2, packageType);
                ps.setInt(3, addressTo);
                ps.setInt(4, addressFrom);
                ps.setBigDecimal(5, weight);
                ps.executeUpdate();
                try (
                        ResultSet rs = ps.getGeneratedKeys()
                ) {
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

    private boolean isValidType(int packageType) {
        return Arrays.stream(PackageType.values()).map(PackageType::ordinal).anyMatch(one -> one == packageType);
    }

    @Override
    public boolean acceptAnOffer(int packageId) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT deliveryStatus, acceptanceTime FROM Package WHERE idRequest = ? AND deliveryStatus = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ) {
            ps.setInt(1, packageId);
            ps.setInt(2, PackageStatus.CREATED.ordinal());
            try (
                    ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    rs.updateInt(1, PackageStatus.ACCEPTED.ordinal());
                    rs.updateDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
                    rs.updateRow();
                    retVal = true;
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    @Override
    public boolean rejectAnOffer(int packageId) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT deliveryStatus, acceptanceTime FROM Package WHERE idRequest = ? AND deliveryStatus = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ) {
            ps.setInt(1, packageId);
            ps.setInt(2, PackageStatus.CREATED.ordinal());
            try (
                    ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    rs.updateInt(1, PackageStatus.REJECTED.ordinal());
                    rs.updateRow();
                    retVal = true;
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT idRequest FROM Package")
        ) {
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    packages.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        List<Integer> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT idRequest FROM RequestToDelivery WHERE type = ?")
        ) {
            ps.setInt(1, type);
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    packages.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        List<Integer> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT idRequest FROM Package WHERE deliveryStatus = ? OR deliveryStatus = ? ")
        ) {
            ps.setInt(1, PackageStatus.ACCEPTED.ordinal());
            ps.setInt(2, PackageStatus.TAKEN.ordinal());
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    packages.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int cityId) {
        List<Integer> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement(
                        "SELECT P.idRequest FROM RequestToDelivery" +
                                " INNER JOIN Address A on RequestToDelivery.pickUpAdress = A.IdAddress " +
                                " INNER JOIN Package P on RequestToDelivery.idRequest = P.idRequest " +
                                " WHERE idCity = ? AND (deliveryStatus = ? OR deliveryStatus = ?) ")
        ) {
            ps.setInt(1, cityId);
            ps.setInt(1, PackageStatus.ACCEPTED.ordinal());
            ps.setInt(2, PackageStatus.TAKEN.ordinal());
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    packages.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int cityId) {
        List<Integer> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement(
                        "SELECT idRequest FROM Package" +
                                " INNER JOIN Address A on A.IdAddress = Package.idAddress" +
                                " WHERE Package.idAddress IS NOT NULL AND idCity = ?")
        ) {
            ps.setInt(1, cityId);
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    packages.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    @Override
    public boolean deletePackage(int packageId) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Package WHERE (deliveryStatus = ? OR deliveryStatus = ?) AND idRequest = ?");
        ) {
            ps.setInt(1, PackageStatus.CREATED.ordinal());
            ps.setInt(2, PackageStatus.REJECTED.ordinal());
            ps.setInt(3, packageId);
            if (ps.executeUpdate() > 0) {
                retVal = deleteRequestToDeliver(packageId);
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private boolean deleteRequestToDeliver(int packageId) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM RequestToDelivery WHERE  idRequest = ?");
        ) {
            ps.setInt(1, packageId);
            retVal = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    @Override
    public boolean changeWeight(int packageId, BigDecimal newWeight) {
        boolean retVal = false;
        if (getDeliveryStatus(packageId) == PackageStatus.CREATED.ordinal() && newWeight != null && newWeight.compareTo(BigDecimal.ZERO) > 0) {

            try (
                    PreparedStatement ps = mConnection.prepareStatement("UPDATE RequestToDelivery SET weight = ? WHERE idRequest = ?")
            ) {
                ps.setBigDecimal(1, newWeight);
                ps.setInt(2, packageId);
                if (ps.executeUpdate() > 0) {
                    retVal = recalculatePrice(packageId);
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }

        }
        return retVal;
    }

    private boolean recalculatePrice(int packageId) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("UPDATE Package SET price = ? WHERE idRequest = ?")
        ) {
            ps.setBigDecimal(1, calculateNewPrice(packageId));
            ps.setInt(2, packageId);
            retVal = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private BigDecimal calculateNewPrice(int packageId) {
        BigDecimal newPrice = BigDecimal.ZERO;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT type, pickUpAdress, deliveryAdress, weight FROM RequestToDelivery  WHERE idRequest = ?")
        ) {
            ps.setInt(1, packageId);
            try (
                    ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    int type = rs.getInt(1);
                    int pickUpAddress = rs.getInt(2);
                    int deliveryAddress = rs.getInt(3);
                    BigDecimal weight = rs.getBigDecimal(4);
                    newPrice = callProcedureToCalculateNewPrice(type, pickUpAddress, deliveryAddress, weight);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return newPrice;
    }


    private BigDecimal callProcedureToCalculateNewPrice(int type, int pickUpAddress, int deliveryAddress, BigDecimal weight) {
        BigDecimal retVal = BigDecimal.ZERO;
        try (CallableStatement stmt = mConnection.prepareCall("{ ? =  call calculatePackagePrice(?,?,?,?) }")) {
            stmt.registerOutParameter(1, Types.DECIMAL);
            stmt.setInt(2, type);
            stmt.setInt(3, pickUpAddress);
            stmt.setInt(4, deliveryAddress);
            stmt.setBigDecimal(5, weight);
            stmt.execute();
            retVal = stmt.getBigDecimal(1);
        } catch (SQLException e) {
            mLogger.log(Level.WARNING, null, e);
        }
        return retVal;
    }

    @Override
    public boolean changeType(int packageId, int newType) {
        boolean retVal = false;
        if (getDeliveryStatus(packageId) == PackageStatus.CREATED.ordinal() && isValidType(newType)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("UPDATE RequestToDelivery SET type = ? WHERE idRequest = ?")
            ) {
                ps.setInt(1, PackageStatus.CREATED.ordinal());
                ps.setInt(2, packageId);
                if (ps.executeUpdate() > 0) {
                    retVal = recalculatePrice(packageId);
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public int getDeliveryStatus(int idPackage) {
        int retVal = -1;
        if (idPackage != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT deliveryStatus  FROM Package WHERE idRequest = ?")
            ) {
                ps1.setInt(1, idPackage);
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
        }
        return retVal;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int idPackage) {
        BigDecimal retVal = null;
        if (idPackage != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT price  FROM Package WHERE idRequest = ?")
            ) {
                ps1.setInt(1, idPackage);
                try (
                        ResultSet rs = ps1.executeQuery()
                ) {
                    if (rs.next()) {
                        retVal = rs.getBigDecimal(1);
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public int getCurrentLocationOfPackage(int idPackage) {
        int retVal = -1;
        if (idPackage != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT idAddress FROM Package WHERE idRequest = ?")
            ) {
                ps1.setInt(1, idPackage);
                try (
                        ResultSet rs = ps1.executeQuery()
                ) {
                    if (rs.next()) {
                        retVal = rs.getInt(1);
                        if (rs.wasNull()) {
                            retVal = -1;
                        }
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public Date getAcceptanceTime(int idPackage) {
        Date retVal = null;
        if (idPackage != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT acceptanceTime  FROM Package WHERE acceptanceTime IS NOT NULL AND idRequest = ?")
            ) {
                ps1.setInt(1, idPackage);
                try (
                        ResultSet rs = ps1.executeQuery()
                ) {
                    if (rs.next()) {
                        retVal = rs.getDate(1);
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }
}
