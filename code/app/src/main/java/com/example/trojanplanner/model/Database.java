package com.example.trojanplanner.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trojanplanner.App;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that handles adding/querying/modifying/removing documents from the Firestore Database,
 * as well as uploading/downloading/deleting images from the Firebase Storage.
 */
public class Database {
    private FirebaseFirestore db;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Activity activity;

    private PhotoPicker photoPicker;

    private OnSuccessListener defaultSuccessListener;
    private OnFailureListener defaultFailureListener;


    /**
     * The default constructor which creates a working Database object
     */
    public Database() {
        this(FirebaseFirestore.getInstance());
    }


    /**
     * An alternative constructor which explicitly states the database instance. Only really
     * made so that making a mock database instance with things like Mockito is possible.
     */
    public Database(FirebaseFirestore firestore) {
        db = firestore;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        activity = App.activityManager.getActivity();
        defaultSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("FIRESTORE SUCCESS");
                Toast myToast = Toast.makeText(activity, R.string.firestore_db_upload_success, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        defaultFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FIRESTORE FAIL");
                Toast myToast = Toast.makeText(activity, R.string.firestore_db_upload_fail, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
    }


// ======================== PhotoPicker integration ========================
    /**
     * A method that initializes the PhotoPicker if you intend to use the uploadFromPhotoPicker method.
     * THIS METHOD MUST BE CALLED IN THE ACTIVITY'S ONCREATE METHOD
     * @author Jared Gourley
     */
    public void initPhotoPicker() {
        photoPicker = new PhotoPicker();
        photoPicker.initPhotoPicker(this);
    }

    // TODO: Is this function necessary?
    /**
     * A method that uninitializes the PhotoPicker if initPhotoPicker was called.
     * Should be called before switching activities
     * @author Jared Gourley
     */
    public void deinitPhotoPicker() {
        photoPicker.deinitPhotoPicker();
    }


    /**
     * Method that creates a PhotoPicker and uploads whatever image gets selected to the database.
     * MUST first call initPhotoPicker to use this function.
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @author Jared Gourley
     */
    public void uploadFromPhotoPicker(User owner) {
        // Throw error if init wasn't called or if database was not added during init
        if (photoPicker == null) {
            throw new RuntimeException("initPhotoPicker was not called.");
        }
        if (!photoPicker.hasDatabase()) {
            throw new RuntimeException("Database object was not passed during init");
        }

        // Open PhotoPicker (will upload on selection since database was passed on init
        photoPicker.openPhotoPicker(owner);

    }


// ======================== Upload to Firebase Storage ========================

    /**
     * Uploads an image to the Firebase Storage area tied to a given user.
     * Sets the owner's pfpFilePath and pfpBitmap attributes when called.
     * @param bitmap The bitmap of the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @param filepath The filepath to store the image as (USE CORRECT CONVENTIONS FOR THIS)
     * @param successListener The action that should be taken on a successful upload
     * @param failureListener The action that should be taken on a failed upload
     * @author Jared Gourley
     *
     */
    public void uploadImage(Bitmap bitmap, @NonNull User owner, String filepath, OnSuccessListener successListener, OnFailureListener failureListener) {
        //String filePath = owner.getDeviceId() + "/" + System.currentTimeMillis() + ".png";
        StorageReference refToSave = storageRef.child(filepath);

        // Attempt to get a bitmap of the uri reference
//        Bitmap bitmap;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
//        }
//        catch (IOException e) {
//            System.out.println("uri invalid/no permissions");
//            return;
//        }

        // Compress and convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Set filepath and bitmap as user attributes
        owner.setPfpFilePath(filepath);
        owner.setPfpBitmap(bitmap);

        // Upload!
        UploadTask uploadTask = refToSave.putBytes(data);
        uploadTask.addOnSuccessListener(successListener);
        uploadTask.addOnFailureListener(failureListener);

    }

    public void uploadImage(Bitmap bitmap, User owner, String filepath) {
        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("SUCCESS");
                Toast myToast = Toast.makeText(activity, R.string.firebase_storage_upload_success, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
                Toast myToast = Toast.makeText(activity, R.string.firebase_storage_upload_fail, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        // Cal the proper uploadImage method
        uploadImage(bitmap, owner, filepath, successListener, failureListener);
    }


    /**
     * Uploads an image to the Firebase Storage area tied to a given user.
     * Sets the owner's pfpFilePath and pfpBitmap attributes when called.
     * A wrapper function for the 5-parameter uploadImage designed for easier use. Uses our convention
     * for image filepaths automatically. Sets a generic successlistener
     * and failurelistener which should be suitable for most cases.
     * @param bitmap The bitmap of the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @author Jared Gourley
     */
    public void uploadImage(Bitmap bitmap, User owner) {
        String filePath = owner.getDeviceId() + "/" + System.currentTimeMillis() + ".png";

        // Call the proper uploadImage method
        uploadImage(bitmap, owner, filePath);

    }


    // ===================== Download from Firebase Storage =====================
    /**
     * Downloads a file from Firebase Storage with the given path. Executes the function defined in
     * successListener on success and the function defined in failureListener on failure.
     * <br>
     * To receive a byte array of the downloaded image, you can initialize an {@code OnSuccessListener<Byte[]>()}
     * and use the value given. Ex.
     * <pre>
     * {@code
     * OnSuccessListener successListener = new OnSuccessListener<byte[]>() {
     *      @Override
     *      public void onSuccess(byte[] bytes) {
     *           Bitmap decodedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
     *           imageView.setImageBitmap(decodedImage);
     *           System.out.println("success!!");
     *      }
     * };
     * }
     * </pre>
     *
     *
     * @param filePath The Firebase Storage path to download the image from
     * @param successListener The action to take on successful download
     * @param failureListener The action to take on failed download
     * @author Jared Gourley
     */
    public void downloadImage(String filePath, OnSuccessListener successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(filePath);

        final long TEN_MEGABYTES = 1024 * 1024 * 10; // Max download size
        pathReference.getBytes(TEN_MEGABYTES).addOnSuccessListener(successListener).addOnFailureListener(failureListener);

    }

    // ===================== Add documents to Firestore Database =====================

    private Map<String, Object> initUserMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("lastName", user.getLastName());
        userMap.put("firstName", user.getFirstName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhoneNumber());
        userMap.put("deviceID", user.getDeviceId());
        userMap.put("hasOrganizerRights", user.isOrganizer());
        userMap.put("hasAdminRights", user.isAdmin());
        userMap.put("pfp", user.getPfpFilePath());
        return userMap;
    }


    /**
     * Inserts a document into the users collection of the Firestore Database. Overwrites the previous
     * document if one with the same device ID exists.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param successListener The action to take on successful user insert
     * @param failureListener The action to take on failed user insert
     * @param user The user to insert into the database
     * @author Jared Gourley
     */
    public void insertUserDocument(OnSuccessListener successListener, OnFailureListener failureListener, User user) {
        Map<String, Object> userMap = initUserMap(user);

        if (user.getClass() == Entrant.class) {
            userMap.put("currentWaitlistedEvents", ((Entrant) user).getCurrentWaitlistedEvents());
            userMap.put("currentEnrolledEvents", ((Entrant) user).getCurrentEnrolledEvents());
            userMap.put("currentDeclinedEvents", ((Entrant) user).getCurrentDeclinedEvents());
            userMap.put("currentPendingEvents", ((Entrant) user).getCurrentPendingEvents());
        } else if (user.getClass() == Organizer.class) {
            userMap.put("createdEvents", ((Organizer) user).getCreatedEvents());
        } // (no special attributes for admins)


        db.collection("users")
            .document(user.getDeviceId())
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener);
    }


    /**
     * Inserts a document into the users collection of the Firestore Database. Overwrites the previous
     * document if one with the same device ID exists.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param user The user to insert into the database
     * @author Jared Gourley
     */
    public void insertUserDocument(User user) {
        insertUserDocument(defaultSuccessListener, defaultFailureListener, user);
    }


    /**
     * Inserts a document into the users collection of the Firestore Database. Overwrites the previous
     * document if one with the same device ID exists.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     * 
     * @param successListener The action to take on successful user insert
     * @param failureListener The action to take on failed user insert
     * @param event The event to insert
     * @author Jared Gourley
     */
    public void insertEvent(OnSuccessListener<Void> successListener, OnFailureListener failureListener, Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventID", event.getEventId()); // Uncommented
        eventMap.put("name", event.getName()); // Uncommented
        eventMap.put("description", event.getDescription()); // Uncommented
        eventMap.put("facility", event.getFacility()); // Uncommented
        eventMap.put("status", event.getStatus()); // Uncommented
        eventMap.put("eventCapacity", event.getTotalSpots()); // Uncommented
        eventMap.put("requiresGeolocation", event.isRequiresGeolocation()); // Uncommented

        // Optional fields can remain commented or be left out for now
        eventMap.put("creationTime", System.currentTimeMillis()); // Uncommented if you need to track creation time
        eventMap.put("eventStart", event.getStartDateTime()); // Uncommented if you need to track start time
        eventMap.put("eventEnd", event.getEndDateTime()); // Uncommented if you need to track end time

        // If you want to track waitlist or enrolled users, you can uncomment those lines as well
        // eventMap.put("enrolledlist", event.getEnrolledList()); // Uncomment if you need to track enrolled users
        // eventMap.put("waitlist", event.getWaitingList()); // Uncomment if you need to track waiting list users
        // eventMap.put("pendinglist", event.getPendingList()); // Uncomment if you need to track pending users
        // eventMap.put("cancelledlist", event.getCancelledList()); // Uncomment if you need to track cancelled users

        db.collection("events")
                .document(event.getEventId())
                .set(eventMap, SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    /**
     * Inserts a document into the users collection of the Firestore Database. Overwrites the previous
     * document if one with the same device ID exists.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param event The event to insert
     * @author Jared Gourley
     */
    public void insertEvent(Event event) {
        insertEvent(defaultSuccessListener, defaultFailureListener, event);
    }


    /**
     * Inserts a document into the eventHashes collection with the key being the QR hash and the
     * value being the event ID.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param successListener The action to take on successful user insert
     * @param failureListener The action to take on failed user insert
     * @param QRHash the hash data of the QR code to store
     * @param event the event object that the QR hash points to
     * @author Jared Gourley
     */
    public void insertQRHash(OnSuccessListener successListener, OnFailureListener failureListener, String QRHash, Event event) {
        Map<String, Object> QRMap = new HashMap<>();
        QRMap.put("eventID", event.getEventId());

        db.collection("eventHashes")
                .document(QRHash)
                .set(QRMap)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Inserts a document into the eventHashes collection with the key being the QR hash and the
     * value being the event ID.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param QRHash the hash data of the QR code to store
     * @param event the event object that the QR hash points to
     * @author Jared Gourley
     */
    public void insertQRHash(String QRHash, Event event) {
        insertQRHash(defaultSuccessListener, defaultFailureListener, QRHash, event);
    }


    /**
     * Inserts a facility object into the database.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param successListener The action to take on successful user insert
     * @param failureListener The action to take on failed user insert
     * @param facility The facility to store in the database
     * @author Jared Gourley
     */
    public void insertFacility(OnSuccessListener successListener, OnFailureListener failureListener, Facility facility) {
        Map<String, Object> facilityMap = new HashMap<>();
        facilityMap.put("facilityID", facility.getFacilityId());
        facilityMap.put("name", facility.getName());
        facilityMap.put("facilityPhoto", facility.getPfpFacilityFilePath());
        facilityMap.put("owner", facility.getOwner());
        //facilityMap.put("currentEvents", );

        db.collection("facilities")
                .document(facility.getFacilityId())
                .set(facilityMap, SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Inserts a facility object into the database.
     * <br>
     * Can optionally be given a custom success and failure listener to perform a more specialized
     * action on success or failure.
     *
     * @param facility The facility to store in the database
     * @author Jared Gourley
     */
    public void insertFacility(Facility facility) {
        insertFacility(defaultSuccessListener, defaultFailureListener, facility);
    }


    // ===================== Get documents from Firestore Database =====================

    // First, the two callback interfaces which must be implemented for any query action

    /**
     * Callback interface for a successful query action from Firestore Database. Any class which
     * wants to receive results from a query must implement the methods required to define their
     * own action to take on query results. Commonly the OnSuccess method can either set attributes
     * or update UI features with the returned values. The object parameter can be safely downcasted
     * to the expected type received from the query function which takes this callback.
     * <br>
     * An example of a QuerySuccessAction which simply receives the attribute waited on by the query:
     *
     * <pre>
     * {@code
     *  Database.QuerySuccessAction querySuccessAction = new Database.QuerySuccessAction(){
     *       @Override
     *       public void OnSuccess(Object object) {
     *           myLocalVar = (ExpectedType) object;
     *       }
     * };
     * }
     * </pre>
     * @author Jared Gourley
     */
    public interface QuerySuccessAction {
        void OnSuccess(Object object); // The queried object can be accessed using the object parameter
    }


    /**
     * Callback interface for a failed query action from Firestore Database. This can occur if the
     * document with the requested ID does not exist or if it cannot downcast safely into the type
     * intended by the query (Ex. querying for an admin but provided a regular user ID). Handle errors
     * from the query using this callback.
     * <br>
     * An example of a QueryFailureAction which simply logs the failed attempt of the query:
     *
     * <pre>
     * {@code
     * Database.QueryFailureAction queryFailureAction = new Database.QueryFailureAction(){
     *      @Override
     *      public void OnFailure() {
     *          System.out.println("Document could not be found!");
     *      }
     * };
     * }
     * </pre>
     * @author Jared Gourley
     */
    public interface QueryFailureAction {
        void OnFailure();
    }





    private Event unpackEventMap(Map<String, Object> eventMap) {
//        Event event = new Event();
//
//        event.setName();
        // etc etc etc


//        return event;
        return null;
    }


    /**
     * Gets an event document from the Firestore Database if the given eventId exists. This action is
     * asynchronous and so the class calling this must initialize a QuerySuccessAction and
     * QueryFailureAction and pass it into the function to determine what action should be taken when
     * receiving the results. On a success, the event object can be received by downcasting the
     * object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful event query
     * @param failureAction The action to take on failed event query
     * @param eventId The event ID of the event desired to be received
     * @author Jared Gourley
     */
    public void getEvent(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String eventId) {
        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = unpackEventMap(document.getData());
                        if (event != null) {
                            successAction.OnSuccess(event);
                        }
                        else {
                            failureAction.OnFailure();
                        }
                    } else {
                        failureAction.OnFailure();
                    }
                } else {
                    Log.d("WARN", "get failed with ", task.getException());
                }
            }
        });

    }




    /**
     * Helper function to unpack a hashmap received from the database into an Entrant object
     *
     * @param entrantMap The hashmap to unpack into an Entrant
     * @return The unpacked Entrant object
     */
    private Entrant unpackEntrantMap(Map<String, Object> entrantMap) {
        Map<String, Object> m = entrantMap;
        Entrant entrant = new Entrant((String) m.get("lastName"), (String) m.get("firstName"), (String) m.get("email"), (String) m.get("phone"), (String) m.get("deviceID"), "Entrant", (boolean) m.get("hasOrganizerRights"), (boolean) m.get("hasAdminRights"));

        return entrant;
    }

    /**
     * Gets an entrant document from the Firestore Database if the given androidId exists. Note that
     * admins and organizers are allowed to be queried as entrants because they also have entrant
     * abilities. This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the entrant object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful entrant query
     * @param failureAction The action to take on failed entrant query
     * @param androidId The android ID of the entrant desired to be received
     * @author Jared Gourley
     */
    public void getEntrant(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId) {
        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Entrant entrant = unpackEntrantMap(document.getData());
                        if (entrant != null) { // TODO: query for every event reference found as well?
                            successAction.OnSuccess(entrant);
                        }
                        else {
                            failureAction.OnFailure();
                        }
                    } else {
                        failureAction.OnFailure();
                    }
                } else {
                    Log.d("WARN", "get failed with ", task.getException());
                }
            }
        });

    }




    /**
     * Helper function to unpack a hashmap received from the database into an Organizer object
     *
     * @param organizerMap The hashmap to unpack into an Organizer
     * @return The unpacked Organizer object
     */
    private Organizer unpackOrganizerMap(Map<String, Object> organizerMap) {
        Map<String, Object> m = organizerMap;
        // If hasOrganizerRights is false, this is not allowed to be set as an organizer
        if (! (boolean) m.get("hasOrganizerRights")) {
            return null;
        }

        Organizer organizer = new Organizer((String) m.get("lastName"), (String) m.get("firstName"), (String) m.get("email"), (String) m.get("phone"), (String) m.get("deviceID"), "Organizer", true, (boolean) m.get("hasAdminRights"), (ArrayList<Event>) m.get("createdEvents"), (Facility) m.get("facility"));

        return organizer;
    }

    /**
     * Gets an organizer document from the Firestore Database if the given androidId exists. Note that
     * admins could be allowed to be queried as organizers if they also have organizer rights.
     * <strong> If hasOrganizerRights is false on a valid document, the QueryFailureAction
     * will be called since the requested document is not a valid organizer. </strong>
     * This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the organizer object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful organizer query
     * @param failureAction The action to take on failed organizer query
     * @param androidId The android ID of the organizer desired to be received
     * @author Jared Gourley
     */
    public void getOrganizer(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId) {
        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Organizer organizer = unpackOrganizerMap(document.getData());
                        if (organizer != null) {
                            successAction.OnSuccess(organizer);
                        }
                        else {
                            failureAction.OnFailure();
                        }
                    } else {
                        failureAction.OnFailure();
                    }
                } else {
                    Log.d("WARN", "get failed with ", task.getException());
                }
            }
        });

    }




    /**
     * Helper function to unpack a hashmap received from the database into an Admin object
     *
     * @param adminMap The hashmap to unpack into an Admin
     * @return The unpacked Admin object
     */
    private Admin unpackAdminMap(Map<String, Object> adminMap) {
        Map<String, Object> m = adminMap;
        // If hasAdminRights is false, this is not allowed to be set as an admin
        if (! (boolean) m.get("hasAdminRights")) {
            return null;
        }

        Admin admin = new Admin((String) m.get("lastName"), (String) m.get("firstName"), (String) m.get("email"), (String) m.get("phone"), (String) m.get("deviceID"), "Admin", (boolean) m.get("hasOrganizerRights"), true);

        return admin;
    }


    /**
     * Gets an admin document from the Firestore Database if the given androidId exists.
     * <strong> If hasAdminRights is false on a valid document, the QueryFailureAction
     * will be called since the requested document is not a valid admin. </strong>
     * This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the admin object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful admin query
     * @param failureAction The action to take on failed admin query
     * @param androidId The android ID of the admin desired to be received
     * @author Jared Gourley
     */
    public void getAdmin(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId) {
        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Admin admin = unpackAdminMap(document.getData());
                        if (admin != null) {
                            successAction.OnSuccess(admin);
                        }
                        else {
                            failureAction.OnFailure();
                        }
                    } else {
                        failureAction.OnFailure();
                    }
                } else {
                    Log.d("WARN", "get failed with ", task.getException());
                }
            }
        });

    }







    /**
     * Helper function to unpack a hashmap received from the database into an event ID
     *
     * @param QRMap The hashmap to collect the event ID from
     * @return The unpacked event ID
     */
    private String unpackQRMap(Map<String, Object> QRMap) {
        String eventId = (String) QRMap.get("eventID"); // TODO: should this be error checked for if it exists?

        return eventId;
    }

    /**
     * Gets the event ID from the Firestore Database associated with a given QR hash. This action
     * is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the eventId string can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful QR query
     * @param failureAction The action to take on failed QR query
     * @param QRHash The hash of the QR code desired to be received
     * @author Jared Gourley
     */
    public void getQRData(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String QRHash) {
        DocumentReference docRef = db.collection("eventHashes").document(QRHash);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String eventId = unpackQRMap(document.getData());
                        if (eventId != null) {
                            successAction.OnSuccess(eventId);
                        }
                        else {
                            failureAction.OnFailure();
                        }
                    } else {
                        failureAction.OnFailure();
                    }
                } else {
                    Log.d("WARN", "get failed with ", task.getException());
                }
            }
        });

    }






    // ============================= TEST FUNCTIONS ==============================


    // Must be called from a database object where initPhotoPicker was called in the onCreate function
    public void uploadImageTest() {
        // fake user with android id "Testfolder" (uploads to testfolder folder)
        Entrant user = new Entrant("", "", "", "", "Testfolder", "", false, false);
        this.uploadFromPhotoPicker(user);

    }


    public static void downloadImageTest() {
        Database database = new Database();
        OnSuccessListener successListener = new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap decodedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                System.out.println("success!!");
                System.out.println(decodedImage); // not very useful but at least it proves it works
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed :((((");
            }
        };

        database.downloadImage("1234567890/1729746211299.png", successListener, failureListener);
    }






    /**
     * Function to test querying an entrant. You can run this test by setting up a temp button
     * in MainActivity to run this function
     */
    public static void getEntrantTest() {
        Database database = new Database();
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



    public static void getOrganizerTest() {
        Database database = new Database();
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
            @Override
            public void OnSuccess(Object object) {
                Organizer organizer = (Organizer) object;
                System.out.println("deviceId: " + organizer.getDeviceId());
                System.out.println("email: " + organizer.getEmail());
                System.out.println("firstName: " + organizer.getFirstName());
                System.out.println("lastName: " + organizer.getLastName());
                System.out.println("hasAdminRights: " + organizer.isAdmin());
                System.out.println("hasOrganizerRights: " + organizer.isOrganizer());
                System.out.println("createdEvents: " + organizer.getCreatedEvents());
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
            @Override
            public void OnFailure() {
                System.out.println("Query attempt failed!");
            }
        };

        database.getOrganizer(successAction, failureAction, "testOrganizer");
    }


    public static void getQRTest() {
        Database database = new Database();
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
            @Override
            public void OnSuccess(Object object) {
                String eventID = (String) object;
                System.out.println("eventID: " + eventID);
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
            @Override
            public void OnFailure() {
                System.out.println("Query attempt failed!");
            }
        };

        database.getQRData(successAction, failureAction, "awoi42A(*@M#NFAOaskwlqo");
    }









}
