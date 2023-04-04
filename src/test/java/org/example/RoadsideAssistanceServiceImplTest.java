package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Optional;


class RoadsideAssistanceServiceImplTest {

    Customer customer = new Customer();

    /**
     * Make sure we can set and get a single nearest advisor
     */
    @Test
    void singleAdvisor() {
        String [][] advisorOperations = {
                { "update", "bob", "0.0", "0.0" },
                { "nearest", "0.0", "0.0", "1", "1", "bob"}
        };
        runTest(advisorOperations);
    }

    /**
     * Put 2 at same location, ask for 2 back - check for corner case
     */
    @Test
    void allAdvisorsOnSingleNode() {
        String[][] advisorOperations = {
                {"update", "a", "0.0", "0.0"},
                {"update", "b", "0.0", "0.0"},
                {"nearest", "0.0", "0.0", "2", "2", "a", "b"}
        };
        runTest(advisorOperations);
    }
    /** Make sure we can reserve, release a single advisor
     */
    @Test
    void singleAdvisorReserveRelease() {
        String [][] advisorOperations = {
                { "update", "bob", "0.0", "0.0" },
                { "nearest", "0.0", "0.0", "1", "1", "bob"},
                { "reserve", "2.0", "2.0", "bob"},
                { "nearest", "0.0", "0.0", "1", "0", null},
                { "release", "bob" },
                { "nearest", "0.0", "0.0", "1", "1", "bob"}
        };
        runTest(advisorOperations);
    }

    /**
     * Test with three advisors, makign sure the ones closest are getting the right values
     */
    @Test
    void threeAdvisors() {
        String [][] advisorOperations = {
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "10.0", "10.0" },
                { "update", "c", "7.0", "7.0" },
                { "nearest", "5.0", "5.0", "1", "1", "c"},
                { "nearest", "1.0", "1.0", "2", "2", "a", "c"},
                { "reserve", "2.0", "2.0", "a"},
                { "nearest", "2.0", "2.0", "1", "1", "c"}
        };
        runTest(advisorOperations);
    }

    /**
     * Test with three advisors, makign sure the ones closest are getting the right values
     */
    @Test
    void threeAdvisorsWithNegatives() {
        String [][] advisorOperations = {
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "-10.0", "10.0" },
                { "update", "c", "7.0", "-7.0" },
                { "nearest", "5.0", "-5.0", "1", "1", "c"},
                { "nearest", "1.0", "1.0", "2", "2", "a", "c"},
                { "reserve", "2.0", "2.0", "a"},
                { "nearest", "2.0", "-2.0", "1", "1", "c"}
        };
        runTest(advisorOperations);
    }

    /**
     * Move advisors around, ensure that we get the right values after moving
     */
    @Test
    void movingAdvisors() {
        String [][] advisorOperations = {
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "10.0", "10.0" },
                { "update", "c", "7.0", "7.0" },
                { "nearest", "5.0", "5.0", "1", "1", "c"},
                { "update", "b", "5.1", "5.1" },
                { "nearest", "5.0", "5.0", "1", "1", "b"},
        };
        runTest(advisorOperations);
    }

    /**
     * Test two advisors in the same location
     */
    @Test
    void sameLocationAdvisors() {
        String [][] advisorOperations = {
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "7.0", "7.0" },
                { "update", "c", "7.0", "7.0" },
                { "nearest", "5.0", "5.0", "1", "1", "b"},
                { "update", "c", "5.1", "5.1" },
                { "nearest", "5.0", "5.0", "1", "1", "c"},
                { "update", "c", "7.0", "7.0" },
                { "nearest", "5.0", "5.0", "1", "1", "b"},
        };
        runTest(advisorOperations);
    }

    /**
     * Check the behavior if we have no advisors in the set
     */
    @Test
    void noAdvisorsLeft() {
        String [][] advisorOperations = {
                { "reserve", "2.0", "2.0", null},
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "10.0", "10.0" },
                { "update", "c", "7.0", "7.0" },
                { "reserve", "2.0", "2.0", "a"},
                { "reserve", "2.0", "2.0", "c"},
                { "reserve", "2.0", "2.0", "b"},
                { "reserve", "2.0", "2.0", null},
        };
        runTest(advisorOperations);
    }

    /**
     * Check the behavior reserving and releasing
     */
    @Test
    void reserveReleaseAdvisors() {
        String [][] advisorOperations = {
                { "update", "a", "0.0", "0.0" },
                { "update", "b", "10.0", "10.0" },
                { "update", "c", "7.0", "7.0" },
                { "nearest", "6.0", "6.0", "1", "1", "c"},
                { "reserve", "6.0", "6.0", "c"},
                { "nearest", "6.0", "6.0", "1", "1", "b"},
                { "release", "c" },
                { "nearest", "6.0", "6.0", "1", "1", "c"},
        };
        runTest(advisorOperations);
    }


    void runTest(String [][] operations) {
        RoadsideAssistanceServiceImpl service = new RoadsideAssistanceServiceImpl();
        HashMap<String, Assistant> assistants = new HashMap();
        doOperations(service, assistants, operations);
    }
    void doOperations(RoadsideAssistanceService service, HashMap<String, Assistant> assistants, String [][] operations) {
        for (String []operation : operations) {
            doOperation(service, assistants, operation);
        }
    }
    void doOperation(RoadsideAssistanceService service, HashMap<String, Assistant> assistants, String[] operation) {
        System.out.println("Executing " + operationString(operation));
        String operator = operation[0];
        if (operator.equals("update")) {
            doUpdate(service, assistants, operation);
        } else if (operator.equals("nearest")) {
            doNearest(service, operation);
        } else if (operator.equals("reserve")) {
            doReserve(service, operation);
        } else if (operator.equals("release")) {
            doRelease(service, assistants, operation);
        }
    }

    private void doRelease(RoadsideAssistanceService service, HashMap<String, Assistant> assistants, String[] operation) {
        String assistantName = operation[1];
        Assistant assistant = assistants.get(assistantName);
        service.releaseAssistant(customer, assistant);
    }

    private void doReserve(RoadsideAssistanceService service, String[] operation) {
        double latitude = Double.valueOf(operation[1]);
        double longitude = Double.valueOf(operation[2]);
        Geolocation location = new Geolocation(latitude, longitude);
        String expectedReserve = operation[3];
        Optional<Assistant> assistant = service.reserveAssistant(customer, location);
        if (expectedReserve == null) {
            assertTrue(assistant.isEmpty(),  operationString(operation) + "Expected no reserved agents, got " + assistant);
        } else {
            assertFalse(assistant.isEmpty(), operationString(operation) + "Expected reserved agent " + expectedReserve + " got none");
            assertEquals(expectedReserve, assistant.get().getName());
        }
    }

    private void doNearest(RoadsideAssistanceService service, String[] operation) {
        double latitude = Double.valueOf(operation[1]);
        double longitude = Double.valueOf(operation[2]);
        int numResultsRequested = Integer.valueOf(operation[3]);
        int numResultsExpected = Integer.valueOf(operation[4]);
        SortedSet<Assistant> assistantsFound = service.findNearestAssistants(new Geolocation(latitude,longitude), numResultsRequested);
        assertEquals(numResultsExpected, assistantsFound.size(), operationString(operation));
        Iterator<Assistant> assistantIterator = assistantsFound.iterator();
        for (int i = 0 ; i < numResultsExpected;  i++) {
            Assistant assistant = assistantIterator.next();
            assertEquals(operation[5+i], assistant.getName(), operationString(operation) + " index " + i);
        }
    }

    private static void doUpdate(RoadsideAssistanceService service, HashMap<String, Assistant> assistants, String[] operation) {
        String assistantName = operation[1];
        Assistant assistant = assistants.get(assistantName);
        if (assistant == null) {
            assistant = new Assistant(assistantName);
            assistants.put(assistantName, assistant);
        }
        double latitude = Double.valueOf(operation[2]);
        double longitude = Double.valueOf(operation[3]);
        service.updateAssistantLocation(assistant, new Geolocation(latitude, longitude));
    }

    String operationString(String [] operation) {
        return "Operation: " + String.join(",", operation);
    }
}