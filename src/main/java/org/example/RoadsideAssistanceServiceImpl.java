package org.example;

import java.util.*;
import net.sf.javaml.core.kdtree.KDTree;

public class
RoadsideAssistanceServiceImpl implements RoadsideAssistanceService {
    // Stores all available assistants
    KDTree availableAssistants = new KDTree(2);
    int numAvailable = 0;
    // Stores all assistants - both reserved and available
    Map<Assistant, Geolocation> allAssistants = new HashMap();

    /**
     * This method is used to update the location of the roadside assistance service provider.
     *
     * @param assistant         represents the roadside assistance service provider
     * @param assistantLocation represents the location of the roadside assistant
     */
    @Override
    public synchronized void updateAssistantLocation(Assistant assistant, Geolocation assistantLocation) {
        deleteAvailableAssistant(assistant);
        makeAvailable(assistant, assistantLocation);
    }

    synchronized void deleteAvailableAssistant(Assistant assistant) {
        Geolocation location = allAssistants.get(assistant);
        if (location == null)
            return;
        List<Assistant> set = (List<Assistant>)availableAssistants.search(location.toDoubleArray());
        if (set != null) {
            set.remove(assistant);
        }

        if (set.isEmpty()) {
            availableAssistants.delete(location.toDoubleArray());
        }
        numAvailable--;
    }

    synchronized void makeAvailable(Assistant assistant, Geolocation assistantLocation) {
        List<Assistant> assistantsAtNew = (List<Assistant>) availableAssistants.search(assistantLocation.toDoubleArray());
        if (assistantsAtNew == null) {
            assistantsAtNew = new LinkedList();
        }
        assistantsAtNew.add(assistant);
        assistant.setLocation(assistantLocation);
        availableAssistants.insert(assistantLocation.toDoubleArray(), assistantsAtNew);
        allAssistants.put(assistant, assistantLocation);
        numAvailable++;
    }

    /**
     * This method returns a collection of roadside assistants ordered by their distance from the input geo location.
     *
     * @param geolocation - geolocation from which to search for assistants
     * @param limit       - the number of assistants to return
     * @return a sorted collection of assistants ordered ascending by distance from geoLocation
     */
    @Override
    public synchronized SortedSet<Assistant> findNearestAssistants(Geolocation geolocation, int limit) {
        if (limit > numAvailable)
            limit = numAvailable;

        SortedSet<Assistant> result = new TreeSet(new DistanceComparator(geolocation));

        if (limit == 0)
            return result;

        Object[] assistantArray = availableAssistants.nearest(geolocation.toDoubleArray(), limit);
        int i = 0;
        for (Object assistantsObj : assistantArray) {
            List<Assistant> assistants = (List<Assistant>)assistantsObj;
            for (Assistant assistant : assistants) {
                result.add(assistant);
                i++;
                if (i >= limit) {
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * This method reserves an assistant for a Geico customer that is stranded on the roadside due to a disabled vehicle.  *
     *
     * @param customer         - Represents a Geico customer
     * @param customerLocation - Location of the customer
     * @return The Assistant that is on their way to help
     */
    @Override
    public synchronized Optional<Assistant> reserveAssistant(Customer customer, Geolocation customerLocation) {
        SortedSet<Assistant> assistants = findNearestAssistants(customerLocation, 1);
        if (assistants.size() == 0) {
            return Optional.empty();
        }

        Assistant assistant = assistants.first();
        deleteAvailableAssistant(assistant);

        return Optional.of(assistant);
    }

    /**
     * This method releases an assistant either after they have completed work, or the customer no longer needs help.
     *
     * @param customer  - Represents a Geico customer
     * @param assistant - An assistant that was previously reserved by the customer
     */
    @Override
    public synchronized void releaseAssistant(Customer customer, Assistant assistant) {
        Geolocation location = allAssistants.get(assistant);
        makeAvailable(assistant, location);
    }

    // Orders by who is closest to the given point
    private class DistanceComparator implements Comparator<Assistant> {
        Geolocation distanceFrom;

        DistanceComparator(Geolocation distanceFrom) {
            this.distanceFrom = distanceFrom;
        }

        @Override
        public int compare(Assistant o1, Assistant o2) {
            Geolocation l1 = o1.getLocation();
            Geolocation l2 = o2.getLocation();
            double d1 = distanceFrom.distance(l1);
            double d2 = distanceFrom.distance(l2);
            return (int)((d1 - d2)* 100);
        }
    }
}
