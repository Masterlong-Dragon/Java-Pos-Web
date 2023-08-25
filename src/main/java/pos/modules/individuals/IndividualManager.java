package pos.modules.individuals;

import pos.entities.individuals.Individual;
import pos.user.User;

import java.util.ArrayList;

public interface IndividualManager {
    void addIndividual(User user, Individual individual);
    void removeIndividual(User user, Individual individual);
    void updateIndividual(User user,Individual individual);
    Individual getIndividual(User user, int id);
    ArrayList<Individual> getIndividual(User user, String name);
    ArrayList<Individual> getIndividualsAlike(User user, String name);
    ArrayList<Individual> getIndividuals(User user, int begin, int end);
    void close(User user);
}
