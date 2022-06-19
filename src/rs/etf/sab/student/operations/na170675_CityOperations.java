package rs.etf.sab.student.operations;

import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.student.sql_helper.DB;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_CityOperations implements CityOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_CityOperations.class.getName());


    public na170675_CityOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public int deleteCity(String... cities) {
        AtomicInteger deletedCities = new AtomicInteger();
        Arrays.stream(cities).forEach(oneCity -> {
            try (PreparedStatement ps = mConnection.prepareStatement("Delete from City where name=?")) {
                ps.setString(1, oneCity);
                deletedCities.addAndGet(ps.executeUpdate());
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        });
        return deletedCities.get();
    }

    @Override
    public int insertCity(String cityName, String cityPostalNumber) {
        try (PreparedStatement ps = mConnection.prepareStatement("INSERT INTO City(name, postalNumber) values(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cityName);
            ps.setString(2, cityPostalNumber);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return -1;
    }

    @Override
    public boolean deleteCity(int cityId) {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE from City where idCity=?")) {
            ps.setInt(1, cityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> cityIds = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("Select idCity from City")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cityId = rs.getInt(1);
                    cityIds.add(cityId);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return cityIds;
    }

}
