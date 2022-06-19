package rs.etf.sab.student.operations;

import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.student.enums.CourierStatus;
import rs.etf.sab.student.helpers.Util;
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
import java.util.stream.Collectors;

public class na170675_CourierOperations implements CourierOperations {
    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_CourierOperations.class.getName());

    public na170675_CourierOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public boolean insertCourier(String username, String licencePlate) {
        boolean retVal = false;
        int idUser = Util.getUserId(username);
        if (idUser != -1) {
            try (PreparedStatement ps = mConnection.prepareStatement("INSERT INTO Courier(idUser, licencePlate, numOfdelivery, income, status) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, idUser);
                ps.setString(2, licencePlate);
                ps.setInt(3, 0);
                ps.setBigDecimal(4, BigDecimal.ZERO);
                ps.setInt(5, CourierStatus.NOT_DRIVE.ordinal());
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public boolean deleteCourier(String userName) {
        boolean retVal = false;
        int userId = Util.getUserId(userName);
        if (userId != -1) {
            try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Courier WHERE idUser=?")) {
                ps.setInt(1, userId);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
        List<String> userList = new LinkedList<>();
        if (Arrays.stream(CourierStatus.values()).map(CourierStatus::ordinal).anyMatch(one -> one == status)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("SELECT username FROM Courier INNER JOIN PERSON U on U.idUser = Courier.idUser WHERE status=?")
            ) {
                ps.setInt(1, status);
                try (
                        ResultSet rs = ps.executeQuery()
                ) {
                    while (rs.next()) {
                        userList.add(rs.getString(1));
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return userList;
    }

    @Override
    public List<String> getAllCouriers() {
        return getAllCourierId().stream().map(Util::getUsername).collect(Collectors.toList());
    }

    private List<Integer> getAllCourierId() {
        List<Integer> couriers = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("SELECT idUser FROM Courier "); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cityId = rs.getInt(1);
                couriers.add(cityId);
            }

        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return couriers;
    }


    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        List<String> users;
        if (numberOfDeliveries == -1) {
            users = getAllCouriers();
        } else {
            users = getAllCouriersWithNumOfDeliveries(numberOfDeliveries);
        }


        BigDecimal averageProfit = BigDecimal.ZERO;
        if (!users.isEmpty()) {
            BigDecimal overallProfit = BigDecimal.ZERO;
            for (String userName : users) {
                overallProfit = overallProfit.add(getCourierProfit(userName));
            }
            averageProfit = overallProfit.divide(BigDecimal.valueOf(users.size()));
        }
        return averageProfit;
    }

    private BigDecimal getCourierProfit(String userName) {
        BigDecimal retVal = BigDecimal.ZERO;
        int idUser = Util.getUserId(userName);
        if (idUser != -1) {
            try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT income FROM Courier WHERE idUser=?")) {
                ps1.setInt(1, idUser);
                try (ResultSet rs = ps1.executeQuery()) {
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

    private List<String> getAllCouriersWithNumOfDeliveries(int numberOfDeliveries) {
        List<Integer> couriers = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("SELECT idUser FROM Courier WHERE numOfdelivery = ?")) {
            ps.setInt(1, numberOfDeliveries);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cityId = rs.getInt(1);
                    couriers.add(cityId);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return couriers.stream().map(Util::getUsername).collect(Collectors.toList());
    }

}
