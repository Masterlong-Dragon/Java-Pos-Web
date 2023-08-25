package pos.modules.individuals;

import pos.auth.Permission;
import pos.auth.PermissionImpl;
import pos.entities.individuals.Individual;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;

public class CustomerManager extends IndividualManagerImpl {

    private static CustomerManager instance;

    public static CustomerManager Instance() {
        return CustomerManager.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private CustomerManager instance;

        InstanceHolder() {
            instance = new CustomerManager();
        }

        public CustomerManager getInstance() {
            return instance;
        }
    }

    public CustomerManager() {
        tableName = "customers";
        IDName = "CustomerID";
        individualDbEnd = new IndividualDbEndImpl(tableName, IDName);
    }

    @Override
    public void addIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to add customer.");
        super.addIndividual(user, individual);
    }

    @Override
    public void removeIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to remove customer.");
        super.removeIndividual(user, individual);
    }

    @Override
    public void updateIndividual(User user, Individual individual) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to update customer.");
        super.updateIndividual(user, individual);
    }

    @Override
    public Individual getIndividual(User user, int id) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to get customer.");
        return super.getIndividual(user, id);
    }

    @Override
    public ArrayList<Individual> getIndividual(User user, String name) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to get customer.");
        return super.getIndividual(user, name);
    }

    @Override
    public ArrayList<Individual> getIndividualsAlike(User user, String name) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to get customer.");
        return super.getIndividualsAlike(user, name);
    }

    @Override
    public ArrayList<Individual> getIndividuals(User user, int begin, int end) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to get customer.");
        return super.getIndividuals(user, begin, end);
    }

}
