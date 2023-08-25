package pos.modules.individuals;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.individuals.Individual;
import pos.user.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IndividualDbEndImpl implements IndividualDbEnd {

    private final DBConnection conn;
    private String tableName = "individuals";
    private String IDName = "IndividualID";
    private final PreparedStatement insert;
    private final PreparedStatement delete;
    private final PreparedStatement update;

    public IndividualDbEndImpl(String tableName, String IDName) {
        this.tableName = tableName;
        this.IDName = IDName;
        ConfigManager configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.Admin),
                configManager.getDBPassword(Role.Admin));
        conn.setTableName(tableName);
        conn.setIDName(IDName);
        conn.setLabelName("Name");
        insert = conn.createPreparedStatement("INSERT INTO pos." + tableName + " (Name, Contact, Address) VALUES (?, ?, ?)");
        delete = conn.createPreparedStatement("DELETE FROM pos." + tableName + " WHERE " + IDName + " = ?");
        update = conn.createPreparedStatement("UPDATE pos." + tableName + " SET Name = ?, Contact = ?, Address = ? WHERE " + IDName + " = ?");
    }

    @Override
    public Individual getIndividual(int ID) throws IllegalArgumentException {
        ResultSet rs = conn.selectIDQuery(ID);
        if (rs != null) {
            try {
                if (!rs.next())
                    throw new IllegalArgumentException("Individual not found.");
                return new Individual(rs.getInt(IDName),
                        rs.getString("Name"),
                        rs.getString("Contact"),
                        rs.getString("Address"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Individual> getIndividual(String name) {
        ResultSet rs = conn.selectStringQuery(name);
        return getIndividuals(rs);
    }

    @Override
    public ArrayList<Individual> getIndividuals(int begin, int end) {
        ResultSet rs = conn.selectRangeQuery(end - begin, begin);
        return getIndividuals(rs);
    }

    @Override
    public ArrayList<Individual> getIndividualsAlike(String name) {
        ResultSet rs = conn.selectStringAlikeQuery(name);
        return getIndividuals(rs);
    }

    private ArrayList<Individual> getIndividuals(ResultSet rs) {
        if (rs != null) {
            try {
                ArrayList<Individual> individuals = new ArrayList<>();
                while (rs.next()) {
                    individuals.add(new Individual(rs.getInt(IDName),
                            rs.getString("Name"),
                            rs.getString("Contact"),
                            rs.getString("Address")));
                }
                return individuals;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void addIndividual(Individual individual) {
        try {
            insert.setString(1, individual.getName());
            insert.setString(2, individual.getContact());
            insert.setString(3, individual.getAddress());
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeIndividual(Individual individual) {
        try {
            // 检查是否存在
            ResultSet rs = conn.selectIDQuery(individual.getID());
            if (rs == null || !rs.next())
                throw new IllegalArgumentException("Individual not found.");
            delete.setInt(1, individual.getID());
            delete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIndividual(Individual individual) {
        try {
            update.setString(1, individual.getName());
            update.setString(2, individual.getContact());
            update.setString(3, individual.getAddress());
            update.setInt(4, individual.getID());
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int count() {
        return conn.selectCountQuery();
    }

    @Override
    public void close() {
        conn.close();
    }
}
