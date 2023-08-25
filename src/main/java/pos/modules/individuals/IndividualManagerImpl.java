package pos.modules.individuals;

import pos.auth.Permission;
import pos.auth.PermissionImpl;
import pos.entities.individuals.Individual;
import pos.modules.sale.ReturnImpl;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;

public abstract class IndividualManagerImpl implements IndividualManager{

    protected IndividualDbEnd individualDbEnd;
    protected String tableName = "individuals";
    protected String IDName = "individualID";

    public IndividualManagerImpl() {
        individualDbEnd = new IndividualDbEndImpl(tableName, IDName);
    }

    @Override
    public void addIndividual(User user, Individual individual) {
        individualDbEnd.addIndividual(individual);
    }

    @Override
    public void removeIndividual(User user, Individual individual) {
        individualDbEnd.removeIndividual(individual);
    }

    @Override
    public void updateIndividual(User user, Individual individual) {
        individualDbEnd.updateIndividual(individual);
    }

    @Override
    public Individual getIndividual(User user, int id) {
        return individualDbEnd.getIndividual(id);
    }

    @Override
    public ArrayList<Individual> getIndividual(User user, String name) {
        return individualDbEnd.getIndividual(name);
    }

    @Override
    public ArrayList<Individual> getIndividualsAlike(User user, String name) {
        return individualDbEnd.getIndividualsAlike(name);
    }

    @Override
    public ArrayList<Individual> getIndividuals(User user, int begin, int end) {
        return individualDbEnd.getIndividuals(begin, end);
    }

    @Override
    public void close(User user) {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to access individual manager.");
        individualDbEnd.close();
    }
}
