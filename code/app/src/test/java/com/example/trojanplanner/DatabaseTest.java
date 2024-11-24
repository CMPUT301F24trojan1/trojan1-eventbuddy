package com.example.trojanplanner;

import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * A test class to test for database functionality.
 * <br>
 * <strong> Note: NOT TO BE RAN IN AUTOMATED TESTS! Due to the nature of modifying the
 * database these tests cannot be automated by nature. </strong> For this reason keep the @Test
 * tags commented out so automated testing does not detect them.
 *
 * <br>
 * <br>
 * You might not even be able to run firebase commands in JUnit tests so these will be moved into
 * the database class itself until further notice
 *
 */
public class DatabaseTest {
    // Add test functions below!
    private Database database;

//    @Before
//    public void initMockDatabase() {
//        mockDB = Mockito.mock(FirebaseFirestore.class);
//    }

    @Before
    public void initDatabase() {
        database = Database.getDB();
    }

    //@Test
    public void getEntrantTest() {
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
            @Override
            public void OnSuccess(Object object) {
                Entrant entrant = (Entrant) object;
                System.out.println("deviceId: " + entrant.getDeviceId());
                System.out.println("email: " + entrant.getEmail());
                System.out.println("firstName: " + entrant.getFirstName());
                System.out.println("lastName: " + entrant.getLastName());
                System.out.println("hasAdminRights: " + entrant.isAdmin());
                System.out.println("hasOrganizerRights: " + entrant.isOrganizer());
                System.out.println("currentAcceptedEvents: " + entrant.getCurrentEnrolledEvents());
                System.out.println("currentPendingEvents: " + entrant.getCurrentPendingEvents());
                System.out.println("currentWaitlistedEvents: " + entrant.getCurrentWaitlistedEvents());
                System.out.println("currentDeclinedEvents: " + entrant.getCurrentDeclinedEvents());
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
            @Override
            public void OnFailure() {
                System.out.println("Query attempt failed!");
            }
        };

        database.getEntrant(successAction, failureAction, "testEntrant");


    }



}
