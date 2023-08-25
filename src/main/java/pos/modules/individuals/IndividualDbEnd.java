package pos.modules.individuals;

import pos.entities.individuals.Individual;

import java.util.ArrayList;

public interface IndividualDbEnd {
    Individual getIndividual(int ID);
    ArrayList<Individual> getIndividual(String name);
    ArrayList<Individual> getIndividuals(int begin, int end);
    ArrayList<Individual> getIndividualsAlike(String name);
    void addIndividual(Individual individual);
    void removeIndividual(Individual individual);
    void updateIndividual(Individual individual);
    int count();
    void close();
}
