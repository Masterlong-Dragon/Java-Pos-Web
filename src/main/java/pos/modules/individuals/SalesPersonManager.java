package pos.modules.individuals;

import pos.auth.PermissionImpl;
import pos.entities.individuals.Individual;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;

public class SalesPersonManager extends IndividualManagerImpl {

    private static SalesPersonManager instance;

    public static SalesPersonManager Instance() {
        return SalesPersonManager.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private SalesPersonManager instance;

        InstanceHolder() {
            instance = new SalesPersonManager();
        }

        public SalesPersonManager getInstance() {
            return instance;
        }
    }

    public SalesPersonManager() {
        tableName = "salespersons";
        IDName = "SalespersonID";
        individualDbEnd = new IndividualDbEndImpl(tableName, IDName);
    }

    @Override
    public void addIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to add salesperson.");
        super.addIndividual(user, individual);
    }

    @Override
    public void removeIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to remove salesperson.");
        super.removeIndividual(user, individual);
    }

    @Override
    public void updateIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to update salesperson.");
        super.updateIndividual(user, individual);
    }

    @Override
    public Individual getIndividual(User user, int id) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to get salesperson.");
        return super.getIndividual(user, id);
    }

    @Override
    public ArrayList<Individual> getIndividual(User user, String name) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to get salesperson.");
        return super.getIndividual(user, name);
    }

    @Override
    public ArrayList<Individual> getIndividualsAlike(User user, String name) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to get salesperson.");
        return super.getIndividualsAlike(user, name);
    }

    @Override
    public ArrayList<Individual> getIndividuals(User user, int begin, int end) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to get salesperson.");
        return super.getIndividuals(user, begin, end);
    }
}
