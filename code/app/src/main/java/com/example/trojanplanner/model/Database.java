package com.example.trojanplanner.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.example.trojanplanner.App;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that handles adding/querying/modifying/removing documents from the Firestore Database,
 * as well as uploading/downloading/deleting images from the Firebase Storage.
 * <br>
 * Implemented as a singleton, so all classes that instantiate a Database get the same object.
 * This allows adding listeners to ongoing queries instead of having to send new ones.
 */
public class Database {
    private static Database database; // The global singleton database object

    private FirebaseFirestore db;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private PhotoPicker photoPicker;

    private OnSuccessListener defaultSuccessListener;
    private OnFailureListener defaultFailureListener;

    // States for if a query is running already. Set to the ID being queried, or null if none happening
    private String activeEntrantQuery = null;
    private String activeOrganizerQuery = null;
    private String activeAdminQuery = null;
    private String activeEventQuery = null;
    private String activeFacilityQuery = null;
    private String activeImageQuery = null;

    // Listeners to invoke upon query result
    private ArrayList<QuerySuccessAction> entrantSuccessListeners = new ArrayList<QuerySuccessAction>();
    private ArrayList<QuerySuccessAction> organizerSuccessListeners = new ArrayList<QuerySuccessAction>();
    private ArrayList<QuerySuccessAction> adminSuccessListeners = new ArrayList<QuerySuccessAction>();
    private ArrayList<QuerySuccessAction> eventSuccessListeners = new ArrayList<QuerySuccessAction>();
    private ArrayList<QuerySuccessAction> facilitySuccessListeners = new ArrayList<QuerySuccessAction>();
    private ArrayList<QueryFailureAction> entrantFailureListeners = new ArrayList<QueryFailureAction>();
    private ArrayList<QueryFailureAction> organizerFailureListeners = new ArrayList<QueryFailureAction>();
    private ArrayList<QueryFailureAction> adminFailureListeners = new ArrayList<QueryFailureAction>();
    private ArrayList<QueryFailureAction> eventFailureListeners = new ArrayList<QueryFailureAction>();
    private ArrayList<QueryFailureAction> facilityFailureListeners = new ArrayList<QueryFailureAction>();

    private ArrayList<OnSuccessListener> imageSuccessListeners = new ArrayList<OnSuccessListener>();
    private ArrayList<OnFailureListener> imageFailureListeners = new ArrayList<OnFailureListener>();


    // ================== METHODS TO GET THE GLOBAL DATABASE OBJECT ==================

    public static Database getDB() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public static Database getDB(FirebaseFirestore firestore) {
        if (database != null) {
            throw new RuntimeException("Cannot inject dependency if global database is already created");
        }
        database = new Database(firestore);
        return database;
    }

    // ========================= (private) Constructors ==========================
    /**
     * The default constructor which creates a working Database object
     */
    private Database() {
        this(FirebaseFirestore.getInstance());
    }

    /**
     * An alternative constructor which explicitly states the database instance. Only really
     * made so that making a mock database instance with things like Mockito is possible.
     */
    private Database(FirebaseFirestore firestore) {
        db = firestore;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        defaultSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("FIRESTORE SUCCESS");
                Toast myToast = Toast.makeText(App.activity, R.string.firestore_db_upload_success, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        defaultFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FIRESTORE FAIL");
                Toast myToast = Toast.makeText(App.activity, R.string.firestore_db_upload_fail, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
    }

// ========================== Getters (no setters) =========================


    public String getActiveEntrantQuery() {
        return activeEntrantQuery;
    }

    public String getActiveOrganizerQuery() {
        return activeOrganizerQuery;
    }

    public String getActiveAdminQuery() {
        return activeAdminQuery;
    }

    public String getActiveImageQuery() {
        return activeImageQuery;
    }

    public String getActiveFacilityQuery() {
        return activeFacilityQuery;
    }

    public String getActiveEventQuery() {
        return activeEventQuery;
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

    public void initPhotoPicker(PhotoPicker.PhotoPickerCallback callback) {
        photoPicker = new PhotoPicker();
        photoPicker.initPhotoPicker(callback, this);
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
    public void uploadImage(@NonNull Bitmap bitmap, @NonNull User owner, String filepath, OnSuccessListener successListener, OnFailureListener failureListener) {
        StorageReference refToSave = storageRef.child(filepath);

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
                Toast myToast = Toast.makeText(App.activity, R.string.firebase_storage_upload_success, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
                Toast myToast = Toast.makeText(App.activity, R.string.firebase_storage_upload_fail, Toast.LENGTH_SHORT);
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
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
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
     * @param escapeSharing If true, ignore the activeImage query and listeners and run a private separate query
     * @author Jared Gourley
     */
    private void downloadImage(String filePath, OnSuccessListener successListener, OnFailureListener failureListener, boolean escapeSharing) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeImageQuery != null && activeImageQuery == filePath) {
                // Add listeners to current lists
                imageSuccessListeners.add(successListener);
                imageFailureListeners.add(failureListener);
                return;
            } else if (activeImageQuery != null && activeImageQuery != filePath) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                imageSuccessListeners.add(successListener);
                imageFailureListeners.add(failureListener);
                activeImageQuery = filePath;
            }
        }

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(filePath);

        final long TEN_MEGABYTES = 1024 * 1024 * 10; // Max download size
        if (!escapeSharing) {
            pathReference.getBytes(TEN_MEGABYTES).addOnSuccessListener(callImageSuccessListeners).addOnFailureListener(callImageFailureListeners);
        }
        else {
            pathReference.getBytes(TEN_MEGABYTES).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
        }

    }

    // Private listener that calls all success listeners
    private OnSuccessListener callImageSuccessListeners = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            for (OnSuccessListener listener : imageSuccessListeners) {
                if (listener != null) {
                    listener.onSuccess(o);
                }
            }
            activeImageQuery = null;
            imageSuccessListeners.clear();
            imageFailureListeners.clear();
        }
    };

    // Private listener that calls all failure listeners
    private OnFailureListener callImageFailureListeners = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            for (OnFailureListener listener : imageFailureListeners) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
            activeImageQuery = null;
            imageSuccessListeners.clear();
            imageFailureListeners.clear();
        }
    };

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
        downloadImage(filePath, successListener, failureListener, false);
    }

    // ===================== Add documents to Firestore Database =====================


    private ArrayList<DocumentReference> convertEventArrayToDocRefs(ArrayList<Event> eventList) {
        ArrayList<DocumentReference> docRefList = new ArrayList<DocumentReference>();
        if (eventList != null) { // Check if eventList is not null
            for (Event event : eventList) {
                if (event != null && event.getEventId() != null) {
                    DocumentReference docRef = db.document("events/" + event.getEventId());
                    docRefList.add(docRef);
                } else {
                    Log.e("Database", "Event or eventId is null");
                }
            }
        } else {
            Log.e("Database", "Event list is null");
        }


        return docRefList;
    }

    private ArrayList<DocumentReference> convertUserArrayToDocRefs(ArrayList<User> userList) {
        ArrayList<DocumentReference> docRefList = new ArrayList<>();

        if (userList != null) { // Check if userList is not null
            for (User user : userList) {
                if (user != null && user.getDeviceId() != null) {
                    DocumentReference docRef = db.document("users/" + user.getDeviceId());
                    docRefList.add(docRef);
                } else {
                    Log.e("Database", "User or deviceId is null");
                }
            }
        } else {
            Log.e("Database", "User list is null");
        }
        return docRefList;
    }



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
            Entrant entrant = (Entrant) user;
            userMap.put("currentWaitlistedEvents", convertEventArrayToDocRefs(entrant.getCurrentWaitlistedEvents()));
            userMap.put("currentEnrolledEvents", convertEventArrayToDocRefs(entrant.getCurrentEnrolledEvents()));
            userMap.put("currentDeclinedEvents", convertEventArrayToDocRefs(entrant.getCurrentDeclinedEvents()));
            userMap.put("currentPendingEvents", convertEventArrayToDocRefs(entrant.getCurrentPendingEvents()));
        } else if (user.getClass() == Organizer.class) {
            userMap.put("createdEvents", convertEventArrayToDocRefs( ((Organizer) user).getCreatedEvents() ));
            if (((Organizer) user).getFacility() != null) {
                userMap.put("facilityID", ((Organizer) user).getFacility().getFacilityId());
            }
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
    public void insertEvent(OnSuccessListener successListener, OnFailureListener failureListener, Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventID", event.getEventId());
        eventMap.put("name", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("price", event.getPrice());
        eventMap.put("status", event.getStatus());
        eventMap.put("eventCapacity", event.getTotalSpots());
        eventMap.put("waitlistCapacity", event.getWaitlistCapacity());
        eventMap.put("eventPhoto", event.getPictureFilePath());
        eventMap.put("requiresGeolocation", event.isRequiresGeolocation());

        eventMap.put("creationTime", System.currentTimeMillis());
        eventMap.put("eventStart", event.getStartDateTime());
        eventMap.put("eventEnd", event.getEndDateTime());
        eventMap.put("waitlistOpen", event.getWaitlistOpen());
        eventMap.put("watlistClose", event.getWaitlistClose());


        eventMap.put("isRecurring", event.isRecurring());
        if (event.isRecurring()) {
            eventMap.put("recurrenceFormat", event.getRecurrenceType()); // note: database uses UNTIL_DATE standard but remembers what the organizer prefers
            eventMap.put("recurringEndDate", event.getRecurrenceEndDate());
            eventMap.put("recurringOn", (ArrayList) event.getRecurrenceDays());
        }

        // Insert references
        DocumentReference facilityRef;
        if (event.getFacility() != null) {
            facilityRef = db.document("facilities/" + event.getFacility().getFacilityId());
        }
        else {
            facilityRef = null;
        }
        eventMap.put("facility", facilityRef);

        eventMap.put("enrolledlist", convertUserArrayToDocRefs(event.getEnrolledList()));
        eventMap.put("waitlist", convertUserArrayToDocRefs(event.getWaitingList()));
        eventMap.put("pendinglist", convertUserArrayToDocRefs(event.getPendingList()));
        eventMap.put("cancelledlist", convertUserArrayToDocRefs(event.getCancelledList()));



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
        facilityMap.put("location", facility.getLocation());
        facilityMap.put("facilityPhoto", facility.getPfpFacilityFilePath());

        DocumentReference ownerRef;
        if (facility.getOwner() != null) {
            ownerRef = db.document("users/" + facility.getOwner().getDeviceId());
        }
        else {
            ownerRef = null;
        }
        facilityMap.put("owner", ownerRef);

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


    /**
     * A class created to allow many sub-queries to use the same shared variables to track the
     * number of queries finished and the amount of total queries expected to finish.
     * E.g. An Entrant being queried has 10 joined events. We need to send out 10 sub-queries to
     * retrieve all of these events, so totalNeeded will equal 10 and currentReceived will track
     * the amount of sub-queries that have already finished. If any sub-query sees stillGoing is false
     * by the time it returns, it will not do its success action anymore.
     *
     */
    private class QueryTracker {
        private int currentReceived;
        private int totalNeeded;
        private boolean stillGoing;

        private QueryTracker(int totalNeeded) {
            this.currentReceived = 0;
            this.totalNeeded = totalNeeded;
            this.stillGoing = true;
        }

    }


    private String getIdFromDocRef(DocumentReference docRef) {
        String[] idPath = docRef.getId().split("/");
        return idPath[idPath.length - 1];
    }



    private Event unpackEventMap(Map<String, Object> eventMap) {
        Map<String, Object> m = eventMap;
        // Make a minimal event and then add in all other attributes
        Event event = new Event((String) m.get("eventID"));

        event.setName((String) m.get("name"));
        event.setDescription((String) m.get("description"));
        event.setPriceDouble((double) m.get("price"));

        event.setPictureFilePath((String) m.get("eventPhoto"));

        Date eventStart = (m.get("eventStart") != null) ? ((Timestamp) m.get("eventStart")).toDate() : null;
        Date eventEnd = (m.get("eventEnd") != null) ? ((Timestamp) m.get("eventEnd")).toDate() : null;
        Date waitlistOpen = (m.get("waitlistOpen") != null) ? ((Timestamp) m.get("waitlistOpen")).toDate() : null;
        Date waitlistClose = (m.get("waitlistClose") != null) ? ((Timestamp) m.get("waitlistClose")).toDate() : null;
        event.setStartDateTime(eventStart);
        event.setEndDateTime(eventEnd);
        event.setWaitlistOpen(waitlistOpen);
        event.setWaitlistClose(waitlistClose);

        event.setRequiresGeolocation((boolean) m.get("requiresGeolocation"));
        event.setTotalSpots((Long) m.get("eventCapacity"));
        event.setWaitlistCapacity((Long) m.get("waitlistCapacity"));
        event.setStatus((String) m.get("status"));

        event.setRecurring((boolean) m.get("isRecurring"));
        if (event.isRecurring()) {
            event.setRecurrenceType((Event.RecurrenceType) m.get("reccurrenceFormat"));
            event.setRecurrenceEndDate(((Timestamp) m.get("recurringEndDate")).toDate());
            event.setRecurrenceDays((ArrayList<String>) m.get("recurringOn"));
        }

        // Create incomplete facility from document id
        Facility facility;
        if ((DocumentReference) m.get("facility") != null) {
            facility = new Facility(getIdFromDocRef((DocumentReference) m.get("facility")));
        }
        else {
            facility = null;
        }
        event.setFacility(facility);

        // Create incomplete entrant objects from document id
        ArrayList<User> userArray = new ArrayList<User>();
        if (m.get("enrolledlist") != null) {
            for (DocumentReference userDocRef : (ArrayList<DocumentReference>) m.get("enrolledlist")) {
                userArray.add(new Entrant(getIdFromDocRef(userDocRef)));
            }
            Log.d("unpackEventMap", "Enrolled List: " + userArray);
        }
        Log.d("unpackEventMap", "Enrolled List is empty");
        event.setEnrolledList(userArray);
        userArray = new ArrayList<User>(); // This instead of .clear in case it clears the entrant current enrolled array because same reference
        if (m.get("pendinglist") != null) {
            for (DocumentReference userDocRef : (ArrayList<DocumentReference>) m.get("pendinglist")) {
                userArray.add(new Entrant(getIdFromDocRef(userDocRef)));
            }
            Log.d("unpackEventMap", "Pending List: " + userArray);
        }
        Log.d("unpackEventMap", "Pending List is empty");
        event.setPendingList(userArray);
        userArray = new ArrayList<User>();
        if (m.get("waitlist") != null) {
            for (DocumentReference userDocRef : (ArrayList<DocumentReference>) m.get("waitlist")) {
                userArray.add(new Entrant(getIdFromDocRef(userDocRef)));
            }
            Log.d("unpackEventMap", "Waitlist: " + userArray);
        }
        Log.d("unpackEventMap", "WaitList is empty ");
        event.setWaitingList(userArray);
        userArray = new ArrayList<User>();
        if (m.get("cancelledlist") != null) {
            for (DocumentReference userDocRef : (ArrayList<DocumentReference>) m.get("cancelledlist")) {
                userArray.add(new Entrant(getIdFromDocRef(userDocRef)));
            }

            Log.d("unpackEventMap", "Cancelled List: " + userArray);
        }
        Log.d("unpackEventMap", "Cancelled List is empty ");
        event.setCancelledList(userArray);

        return event;
    }


    /**
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
     * Gets an event document from the Firestore Database if the given eventId exists. This action is
     * asynchronous and so the class calling this must initialize a QuerySuccessAction and
     * QueryFailureAction and pass it into the function to determine what action should be taken when
     * receiving the results. On a success, the event object can be received by downcasting the
     * object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful event query
     * @param failureAction The action to take on failed event query
     * @param eventId The event ID of the event desired to be received
     * @param escapeSharing If true, ignore the activeEntrant query and listeners and run a private separate query
     * @param facility If non-null, use this facility instead of querying for it (prevents infinite loop)
     * @author Jared Gourley
     */
    public void getEvent(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String eventId, boolean escapeSharing, Facility facility) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeEventQuery != null && activeEventQuery == eventId) {
                // Add listeners to current lists
                eventSuccessListeners.add(successAction);
                eventFailureListeners.add(failureAction);
                return;
            } else if (activeEventQuery != null && activeEventQuery != eventId) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                eventSuccessListeners.add(successAction);
                eventFailureListeners.add(failureAction);
                activeEventQuery = eventId;
            }
        }
        final boolean finalEscapeSharing = escapeSharing; // because of https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Failure handling
                if (!task.isSuccessful()) { // If query got blocked completely
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    System.out.println("WARN: get failed with " + task.getException());
                    if (!finalEscapeSharing) {
                        sendToEventListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) { // If document with requested ID doesn't exist
                    if (!finalEscapeSharing) {
                        sendToEventListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                Event event = unpackEventMap(document.getData());
                if (event == null) { // If received event is invalid (hopefully should always be valid)
                    if (!finalEscapeSharing) {
                        sendToEventListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }

                // Now that we have the event object, either send success signals or expand the facility and users if needed

                // Figure out how many sub-queries we need
                int subQueryCount = 0;
                boolean facilityNeeded = false;
                subQueryCount += event.getEnrolledList().size();
                subQueryCount += event.getPendingList().size();
                subQueryCount += event.getWaitingList().size();
                subQueryCount += event.getCancelledList().size();
                if (facility != null || event.getFacility() == null) {
                    event.setFacility(facility);
                }
                else {
                    facilityNeeded = true;
                    subQueryCount += 1;
                }

                if (subQueryCount > 0) {
                    QueryTracker queryTracker = new QueryTracker(subQueryCount);

                    QuerySuccessAction queryTrackerSuccess = new QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            // Quit if a different query failed
                            if (!queryTracker.stillGoing) {
                                return;
                            }

                            // Actually assign the value
                            // Object could be an entrant (from any of the lists) or the facility
                            if (Facility.class.isAssignableFrom(object.getClass())) {
                                System.out.println("getEvent QueryTracker success!" + ((Facility) object));
                                event.setFacility((Facility) object);
                            }
                            else {
                                Entrant entrant = (Entrant) object;
                                System.out.println("getEvent QueryTracker success! " + event);
                                if (event.replaceEntrantMatchingId(entrant) == false) {
                                    throw new RuntimeException("Could not find the entrant to replace!");
                                }
                            }

                            queryTracker.currentReceived += 1;
                            System.out.println("getEvent queryTracker.currentReceived: " + queryTracker.currentReceived);

                            // Call the success listeners if all sub queries finished
                            if (queryTracker.currentReceived == queryTracker.totalNeeded) {
                                System.out.println("getEvent queryTracker finishing!");
                                if (!finalEscapeSharing) {
                                    sendToEventListeners(event, true);
                                } else {
                                    successAction.OnSuccess(event);
                                }
                            }

                        }
                    };
                    QueryFailureAction queryTrackerFailure = new QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            // Mark that this query has failed so other queries should not trigger successes
                            System.out.println("getEvent queryTracker FAILED");
                            queryTracker.stillGoing = false;
                            return;
                        }
                    };

                    // Launch necessary queries
                    for (User user : event.getEnrolledList()) {
                        System.out.println("Launching query for entrant: " + user.getDeviceId());
                        getEntrant(queryTrackerSuccess, queryTrackerFailure, user.getDeviceId(), true, false);
                    }
                    for (User user : event.getPendingList()) {
                        System.out.println("Launching query for entrant: " + user.getDeviceId());
                        getEntrant(queryTrackerSuccess, queryTrackerFailure, user.getDeviceId(), true, false);
                    }
                    for (User user : event.getWaitingList()) {
                        System.out.println("Launching query for entrant: " + user.getDeviceId());
                        getEntrant(queryTrackerSuccess, queryTrackerFailure, user.getDeviceId(), true, false);
                    }
                    for (User user : event.getCancelledList()) {
                        System.out.println("Launching query for entrant: " + user.getDeviceId());
                        getEntrant(queryTrackerSuccess, queryTrackerFailure, user.getDeviceId(), true, false);
                    }

                    if (facilityNeeded) {
                        System.out.println("Launching query for facility: " + event.getFacility().getFacilityId());
                        getFacility(queryTrackerSuccess, queryTrackerFailure, event.getFacility().getFacilityId(), true, null, false);
                    }

                }
                // Otherwise just consider this a success!
                else {
                    if (!finalEscapeSharing) {
                        sendToEventListeners(event, true);
                    } else {
                        successAction.OnSuccess(event);
                    }
                }

            }

        });

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
        getEvent(successAction, failureAction, eventId, false, null);
    }


    /**
     * Notifies all listeners waiting on the event query.
     * @param event The event received (if successful)
     * @param isSuccessful True if success listeners should be called, false if failure listeners should be called.
     * @author Jared Gourley
     */
    private void sendToEventListeners(Event event, boolean isSuccessful) {
        if (isSuccessful) {
            for (QuerySuccessAction listener : eventSuccessListeners) {
                if (listener != null) {
                    listener.OnSuccess(event);
                }
            }
        }
        else {
            for (QueryFailureAction listener : eventFailureListeners) {
                if (listener != null) {
                    listener.OnFailure();
                }
            }
        }

        activeEventQuery = null;
        eventSuccessListeners.clear();
        eventFailureListeners.clear();
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
        entrant.setPfpFilePath((String) m.get("pfp"));

        // Convert all event references into incomplete event objects
        ArrayList<Event> eventArray = new ArrayList<Event>();
        if (m.get("currentAcceptedEvents") != null) {
            for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentAcceptedEvents")) {
                eventArray.add(new Event(getIdFromDocRef(eventDocRef)));
            }

            Log.d("unpackEntrantMap", "Current Accepted Events: " + eventArray);
        }
        entrant.setCurrentEnrolledEvents(eventArray);
        eventArray = new ArrayList<Event>(); // This instead of .clear in case it clears the entrant current enrolled array because same reference
        if (m.get("currentPendingEvents") != null) {
            for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentPendingEvents")) {
                eventArray.add(new Event(getIdFromDocRef(eventDocRef)));
            }
            Log.d("unpackEntrantMap", "Current Pending Events: " + eventArray);
        }
        entrant.setCurrentPendingEvents(eventArray);
        eventArray = new ArrayList<Event>();
        if (m.get("currentWaitlistedEvents") != null) {
            for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentWaitlistedEvents")) {
                eventArray.add(new Event(getIdFromDocRef(eventDocRef)));
            }
            Log.d("unpackEntrantMap", "Current Waitlisted Events: " + eventArray);
        }
        entrant.setCurrentWaitlistedEvents(eventArray);
        eventArray = new ArrayList<Event>();
        if (m.get("currentDeclinedEvents") != null) {
            for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentDeclinedEvents")) {
                eventArray.add(new Event(getIdFromDocRef(eventDocRef)));
            }
            Log.d("unpackEntrantMap", "Current Declined Events: " + eventArray);
        }
        entrant.setCurrentDeclinedEvents(eventArray);

        return entrant;
    }


    /**
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
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
     * @param escapeSharing If true, ignore the activeEntrant query and listeners and run a private separate query
     * @param expandEvents If true, send subqueries for all events, if false then leave them as only eventIds. (can be toggled to prevent events querying entrants querying events etc...)
     * @author Jared Gourley
     */
    private void getEntrant(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId, boolean escapeSharing, boolean expandEvents) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeEntrantQuery != null && activeEntrantQuery == androidId) {
                // Add listeners to current lists
                entrantSuccessListeners.add(successAction);
                entrantFailureListeners.add(failureAction);
                return;
            }
            else if (activeEntrantQuery != null && activeEntrantQuery != androidId) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                entrantSuccessListeners.add(successAction);
                entrantFailureListeners.add(failureAction);
                activeEntrantQuery = androidId;
            }
        }
        final boolean finalEscapeSharing = escapeSharing; // because of https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Failure handling
                if (!task.isSuccessful()) { // If query got blocked completely
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    if (!finalEscapeSharing) {
                        sendToEntrantListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) { // If document with requested ID doesn't exist
                    if (!finalEscapeSharing) {
                        sendToEntrantListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                Entrant entrant = unpackEntrantMap(document.getData());
                if (entrant == null) { // If received entrant is invalid (entrant should always be valid)
                    if (!finalEscapeSharing) {
                        sendToEntrantListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }

                // Now that we have the entrant object, figure out how many sub-queries we need
                // Only expand event references if expandEvents is true
                if (!expandEvents) {
                    // Send success signals
                    if (!finalEscapeSharing) {
                        sendToEntrantListeners(entrant, true);
                    } else {
                        successAction.OnSuccess(entrant);
                    }
                    return;
                }

                // Now expand event references
                int subQueryCount = 0;
                subQueryCount += entrant.getCurrentPendingEvents().size();
                subQueryCount += entrant.getCurrentEnrolledEvents().size();
                subQueryCount += entrant.getCurrentDeclinedEvents().size();
                subQueryCount += entrant.getCurrentWaitlistedEvents().size();

                if (subQueryCount > 0) {
                    QueryTracker queryTracker = new QueryTracker(subQueryCount);

                    QuerySuccessAction queryTrackerSuccess = new QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            // Quit if a different query failed
                            if (!queryTracker.stillGoing) {
                                return;
                            }

                            // Actually assign the value
                            if (entrant.replaceEventMatchingId((Event) object) == false) {
                                throw new RuntimeException("Could not find the event to replace!");
                            }

                            queryTracker.currentReceived += 1;
                            System.out.println("getEntrant queryTracker.currentReceived: " + queryTracker.currentReceived);

                            // Call the success listeners if all sub queries finished
                            if (queryTracker.currentReceived == queryTracker.totalNeeded) {
                                System.out.println("getEntrant queryTracker finishing!");
                                if (!finalEscapeSharing) {
                                    sendToEntrantListeners(entrant, true);
                                } else {
                                    successAction.OnSuccess(entrant);
                                }
                            }

                        }
                    };
                    QueryFailureAction queryTrackerFailure = new QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            // Mark that this query has failed so other queries should not trigger successes
                            System.out.println("getEntrant queryTracker FAILED");
                            queryTracker.stillGoing = false;
                            return;
                        }
                    };

                    // Launch queries
                    for (Event event : entrant.getCurrentEnrolledEvents()) {
                        System.out.println("Launching query for event: " + event.getEventId());
                        getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
                    }
                    for (Event event : entrant.getCurrentPendingEvents()) {
                        System.out.println("Launching query for event: " + event.getEventId());
                        getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
                    }
                    for (Event event : entrant.getCurrentWaitlistedEvents()) {
                        System.out.println("Launching query for event: " + event.getEventId());
                        getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
                    }
                    for (Event event : entrant.getCurrentDeclinedEvents()) {
                        System.out.println("Launching query for event: " + event.getEventId());
                        getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
                    }

                }
                // Otherwise just consider this a success!
                else {
                    if (!finalEscapeSharing) {
                        sendToEntrantListeners(entrant, true);
                    } else {
                        successAction.OnSuccess(entrant);
                    }
                }


            }


        });

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
        getEntrant(successAction, failureAction, androidId, false, true);
    }


    /**
     * Notifies all listeners waiting on the entrant query.
     * @param entrant The entrant received (if successful)
     * @param isSuccessful True if success listeners should be called, false if failure listeners should be called.
     * @author Jared Gourley
     */
    private void sendToEntrantListeners(Entrant entrant, boolean isSuccessful) {
        if (isSuccessful) {
            for (QuerySuccessAction listener : entrantSuccessListeners) {
                if (listener != null) {
                    listener.OnSuccess(entrant);
                }
            }
        }
        else {
            for (QueryFailureAction listener : entrantFailureListeners) {
                if (listener != null) {
                    listener.OnFailure();
                }
            }
        }

        activeEntrantQuery = null;
        entrantSuccessListeners.clear();
        entrantFailureListeners.clear();
    }


    /**
     * Helper function to unpack a hashmap received from the database into an Organizer object
     * Unpacks everything except for the Facility, which has to be properly expanded in the caller function.
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

        //Organizer organizer = new Organizer((String) m.get("lastName"), (String) m.get("firstName"), (String) m.get("email"), (String) m.get("phone"), (String) m.get("deviceID"), "Organizer", true, (boolean) m.get("hasAdminRights"), (ArrayList<Event>) m.get("createdEvents"), (Facility) m.get("facility"));
        Organizer organizer = new Organizer((String) m.get("deviceID"));

        organizer.setFirstName((String) m.get("firstName"));
        organizer.setLastName((String) m.get("lastName"));
        organizer.setEmail((String) m.get("email"));
        organizer.setPhoneNumber((String) m.get("phone"));

        organizer.setRole("organizer");
        organizer.setIsOrganizer(true);
        organizer.setIsAdmin((boolean) m.get("hasAdminRights"));

        organizer.setPfpFilePath((String) m.get("pfp"));

        // Get id of facility document and then split on the / character to remove the collection name
        Facility facility;
        if ((DocumentReference) m.get("facility") != null) {
            String[] facilityIdPath = ((DocumentReference) m.get("facility")).getId().split("/");
            String facilityId = facilityIdPath[facilityIdPath.length - 1];
            facility = new Facility(facilityId);
        }
        else {
            facility = null;
        }
        organizer.setFacility(facility);

        // Get id of every created event document and split + make them into incomplete objects
        organizer.setCreatedEvents(new ArrayList<Event>());
        ArrayList<DocumentReference> createdEventRefs = (ArrayList<DocumentReference>) m.get("createdEvents");
        for (DocumentReference docRef : createdEventRefs) {
            String[] eventIdPath = docRef.getId().split("/");
            String eventId = eventIdPath[eventIdPath.length - 1];
            organizer.addEvent(new Event(eventId));
        }

        return organizer;
    }

    /**
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
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
     * @param escapeSharing If true, ignore the activeOrganizer query and listeners and run a private separate query
     * @param facility If non-null, use this facility instead of querying for it (prevents Organizer-Facility infinite loop)
     * @param expandEvents If true, send subqueries for all events, if false then leave them as only eventIds. (can be toggled to prevent events querying facilities querying organizers querying events etc...)
     * @author Jared Gourley
     */
    private void getOrganizer(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId, boolean escapeSharing, Facility facility, boolean expandEvents) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeOrganizerQuery != null && activeOrganizerQuery == androidId) {
                // Add listeners to current lists
                organizerSuccessListeners.add(successAction);
                organizerFailureListeners.add(failureAction);
                return;
            } else if (activeOrganizerQuery != null && activeOrganizerQuery != androidId) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                organizerSuccessListeners.add(successAction);
                organizerFailureListeners.add(failureAction);
                activeOrganizerQuery = androidId;
            }
        }
        final boolean finalEscapeSharing = escapeSharing; // because of https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final
        System.out.println("finalEscapeSharing: " + finalEscapeSharing);

        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Failure handling
                if (!task.isSuccessful()) { // If query got blocked completely
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    sendToOrganizerListeners(null, false);
                    if (!finalEscapeSharing) {
                        sendToOrganizerListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) { // If document with requested ID doesn't exist
                    // Send failure signals
                    if (!finalEscapeSharing) {
                        sendToOrganizerListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                Organizer organizer = unpackOrganizerMap(document.getData());
                if (organizer == null) { // If received organizer is invalid (if user doesn't have organizer rights)
                    // Send failure signals
                    if (!finalEscapeSharing) {
                        sendToOrganizerListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }

                // Now that we have the organizer object, figure out how many sub-queries we need
                boolean facilityNeeded = false;
                boolean createdEventsNeeded = false;
                int subQueryCount = 0;
                if (facility == null && organizer.getFacility() != null) {
                    facilityNeeded = true;
                    subQueryCount += 1;
                }
                else {
                    organizer.setFacility(facility);
                }
                // Only send subqueries for events if expandEvents is true, else leave them as only eventIds
                if (expandEvents && organizer.getCreatedEvents().size() > 0) {
                    createdEventsNeeded = true;
                    subQueryCount += organizer.getCreatedEvents().size();
                }

                System.out.println("SubQueryCount: " + subQueryCount);
                // If we need to run subqueries, run them all and the last one to finish will trigger the listeners
                if (subQueryCount > 0) {
                    QueryTracker queryTracker = new QueryTracker(subQueryCount);

                    QuerySuccessAction queryTrackerSuccess = new QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            // Quit if a different query failed
                            if (!queryTracker.stillGoing) {
                                return;
                            }

                            // Actually assign the value
                            // If it's not a facility then it must be an event
                            if (Facility.class.isAssignableFrom(object.getClass())) {
                                System.out.println("getOrganizer QueryTracker success!" + ((Facility) object));
                                organizer.setFacility((Facility) object);
                            }
                            else {
                                Event event = (Event) object;
                                System.out.println("getOrganizer QueryTracker success! " + event);
                                int index = organizer.findIndexWithEventId(event.getEventId());
                                System.out.println("createdEvents: " + organizer.getCreatedEvents());
                                System.out.println("index to insert: " + index);
                                organizer.setEventAtIndex(event, index);
                            }

                            queryTracker.currentReceived += 1;
                            System.out.println("getOrganizer queryTracker.currentReceived: " + queryTracker.currentReceived);

                            // Call the success listeners if all sub queries finished
                            // We also need to set the facility of all of the events since we put in the fake facility attribute
                            if (queryTracker.currentReceived == queryTracker.totalNeeded) {
                                System.out.println("getOrganizer queryTracker finishing!");
                                for (Event event : organizer.getCreatedEvents()) {
                                    event.setFacility(organizer.getFacility());
                                }
                                if (!finalEscapeSharing) {
                                    sendToOrganizerListeners(organizer, true);
                                } else {
                                    successAction.OnSuccess(organizer);
                                }
                            }

                        }
                    };
                    QueryFailureAction queryTrackerFailure = new QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            // Mark that this query has failed so other queries should not trigger successes
                            System.out.println("getOrganizer queryTracker FAILED");
                            queryTracker.stillGoing = false;
                            return;
                        }
                    };

                    if (facilityNeeded) {
                        System.out.println("Launching query for facility: " + organizer.getFacility().getFacilityId());
                        expandFacilityAttribute(queryTrackerSuccess, queryTrackerFailure, organizer, true); // TODO i think making this funciton was overrated i could just call getFacility directly
                    }
                    if (createdEventsNeeded) {
                        for (Event event : organizer.getCreatedEvents()) {
                            System.out.println("Launching query for event: " + event.getEventId());
                            getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
                        }
                    }

                }
                // Otherwise just consider this a success!
                else {
                    if (!finalEscapeSharing) {
                        sendToOrganizerListeners(organizer, true);
                    } else {
                        successAction.OnSuccess(organizer);
                    }
                }

            }

        });

    }


    /**
     * Given an Organizer object, expands the facility attribute of it from just a facility ID into a
     * fully-queried Facility object. On success, calls the successAction if escapeSharing is true
     * or calls all listeners if escapeSharing is false.
     * @param successAction The action to take on successful organizer query
     * @param failureAction The action to take on failed organizer query
     * @param organizer The organizer object to expand the facility of
     * @param escapeSharing Whether the success/failure parameter should be called or all listeners
     */
    private void expandFacilityAttribute(QuerySuccessAction successAction, QueryFailureAction failureAction, Organizer organizer, boolean escapeSharing) {

        QuerySuccessAction facilityReceivedSuccess = new QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                organizer.setFacility((Facility) object);
                if (!escapeSharing) {
                    sendToOrganizerListeners(organizer, true);
                }
                else {
                    successAction.OnSuccess(object);
                }
            }
        };
        QueryFailureAction facilityReceivedFailure = new QueryFailureAction() {
            @Override
            public void OnFailure() {
                if (!escapeSharing) {
                    sendToOrganizerListeners(null, false);
                }
                else {
                    failureAction.OnFailure();
                }
            }
        };

        // Run the query outside of sharing mode so that it won't get blocked by different queries
        getFacility(facilityReceivedSuccess, facilityReceivedFailure, organizer.getFacility().getFacilityId(), true, organizer, false);
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
        getOrganizer(successAction, failureAction, androidId, false, null, true);
    }

    /**
     * Notifies all listeners waiting on the organizer query.
     * @param organizer The organizer received (if successful)
     * @param isSuccessful True if success listeners should be called, false if failure listeners should be called.
     * @author Jared Gourley
     */
    private void sendToOrganizerListeners(Organizer organizer, boolean isSuccessful) {
        if (isSuccessful) {
            for (QuerySuccessAction listener : organizerSuccessListeners) {
                if (listener != null) {
                    listener.OnSuccess(organizer);
                }
            }
        }
        else {
            for (QueryFailureAction listener : organizerFailureListeners) {
                if (listener != null) {
                    listener.OnFailure();
                }
            }
        }

        activeOrganizerQuery = null;
        organizerSuccessListeners.clear();
        organizerFailureListeners.clear();
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
        admin.setPfpFilePath((String) m.get("pfp"));

        return admin;
    }


    /**
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
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
     * @param escapeSharing If true, ignore the activeAdmin query and listeners and run a private separate query
     * @author Jared Gourley
     */
    private void getAdmin(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String androidId, boolean escapeSharing) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeAdminQuery != null && activeAdminQuery == androidId) {
                // Add listeners to current lists
                adminSuccessListeners.add(successAction);
                adminFailureListeners.add(failureAction);
                return;
            } else if (activeAdminQuery != null && activeAdminQuery != androidId) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                adminSuccessListeners.add(successAction);
                adminFailureListeners.add(failureAction);
                activeAdminQuery = androidId;
            }
        }
        final boolean finalEscapeSharing = escapeSharing; // because of https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

        DocumentReference docRef = db.collection("users").document(androidId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Admin admin = unpackAdminMap(document.getData());
                        if (admin != null) {
                            // Send success signals
                            if (!finalEscapeSharing) {
                                sendToAdminListeners(admin, true);
                            } else {
                                successAction.OnSuccess(admin);
                            }
                            return;
                        }
                    }
                    // Send failure signals
                    if (!finalEscapeSharing) {
                        sendToAdminListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;

                }
                else {
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    if (!finalEscapeSharing) {
                        sendToAdminListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                }
            }
        });

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
        getAdmin(successAction, failureAction, androidId, false);
    }


    /**
     * Notifies all listeners waiting on the admin query.
     * @param admin The admin received (if successful)
     * @param isSuccessful True if success listeners should be called, false if failure listeners should be called.
     * @author Jared Gourley
     */
    private void sendToAdminListeners(Admin admin, boolean isSuccessful) {
        if (isSuccessful) {
            for (QuerySuccessAction listener : adminSuccessListeners) {
                if (listener != null) {
                    listener.OnSuccess(admin);
                }
            }
        }
        else {
            for (QueryFailureAction listener : adminFailureListeners) {
                if (listener != null) {
                    listener.OnFailure();
                }
            }
        }

        activeAdminQuery = null;
        adminSuccessListeners.clear();
        adminFailureListeners.clear();
    }



    /**
     * Helper function to unpack a hashmap received from the database into a Facility object
     *
     * @param facilityMap The hashmap to collect the facility data from
     * @return The unpacked facility object
     */
    private Facility unpackFacilityMap(Map<String, Object> facilityMap) {
        // Make an incomplete organizer object which we will expand in the caller function

        // Get id of document and then split on the / character to remove the collection name
        Organizer owner;
        if ((DocumentReference) facilityMap.get("owner") != null) {
            String[] ownerIdPath = ((DocumentReference) facilityMap.get("owner")).getId().split("/");
            String ownerId = ownerIdPath[ownerIdPath.length - 1];
            owner = new Organizer(ownerId);
        }
        else {
            owner = null;
        }

        Facility facility = new Facility((String) facilityMap.get("name"), (String) facilityMap.get("facilityID"), (String) facilityMap.get("location"), owner, (String) facilityMap.get("facilityPhoto"));

        return facility;
    }


    /**
     * (Private method so that other classes cannot escape sharing - wrapper method enforces this)
     * Gets a facility document from the Firestore Database if the given facilityId exists.
     * This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the facility object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful organizer query
     * @param failureAction The action to take on failed organizer query
     * @param facilityId The facility ID of the facility desired to be received
     * @param escapeSharing If true, ignore the activeFacility query and listeners and run a private separate query
     * @param owner If non-null, use this owner instead of querying for it (prevents Organizer-Facility infinite loop)
     * @param expandOwnerEvents If true, send subqueries for all created events of the owner, if false then leave them as only eventIds. (can be toggled to prevent events querying facilities querying organizers querying events etc...)
     * @author Jared Gourley
     */
    private void getFacility(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String facilityId, boolean escapeSharing, Organizer owner, boolean expandOwnerEvents) {
        if (!escapeSharing) { // escapeSharing can only be set true by the Database class itself for backdoor functionality
            // If this query is already happening, simply add listeners instead of re-running
            if (activeFacilityQuery != null && activeFacilityQuery == facilityId) {
                // Add listeners to current lists
                facilitySuccessListeners.add(successAction);
                facilityFailureListeners.add(failureAction);
                return;
            } else if (activeFacilityQuery != null && activeFacilityQuery != facilityId) {
                // Run as a separate query if the shared query is being used for a different document
                escapeSharing = true;
            }
            else {
                // Else no query running so run one
                facilitySuccessListeners.add(successAction);
                facilityFailureListeners.add(failureAction);
                activeFacilityQuery = facilityId;
            }
        }
        final boolean finalEscapeSharing = escapeSharing; // because of https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

        DocumentReference docRef = db.collection("facilities").document(facilityId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Failure handling
                if (!task.isSuccessful()) { // If query got blocked completely
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    if (!finalEscapeSharing) {
                        sendToFacilityListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) { // If document with requested ID doesn't exist
                    // Send failure signals
                    if (!finalEscapeSharing) {
                        sendToFacilityListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }
                Facility facility = unpackFacilityMap(document.getData());
                if (facility == null) { // If received facility is invalid (facilities should always be valid right now)
                    // Send failure signals
                    if (!finalEscapeSharing) {
                        sendToFacilityListeners(null, false);
                    } else {
                        failureAction.OnFailure();
                    }
                    return;
                }

                // Now that we have the facility object, either send success signals or expand the owner if needed

                // If owner is provided by parameter or there is no owner, we are done
                if (owner != null || facility.getOwner() == null) {
                    facility.setOwner(owner);
                    // Send success signals
                    if (!finalEscapeSharing) {
                        sendToFacilityListeners(facility, true);
                    } else {
                        successAction.OnSuccess(facility);
                    }
                }
                // Otherwise we have to get the owner which currently has not been expanded yet
                else {
                    expandOwnerAttribute(successAction, failureAction, facility, finalEscapeSharing, expandOwnerEvents);
                }

            }

        });

    }


    /**
     * Given a Facility object, expands the owner attribute of it from just a device ID into a
     * fully-queried Organizer object. On success, calls the successAction if escapeSharing is true
     * or calls all listeners if escapeSharing is false.
     * @param successAction The action to take on successful organizer query
     * @param failureAction The action to take on failed organizer query
     * @param facility The facility object to expand the owner of
     * @param escapeSharing Whether the success/failure parameter should be called or all listeners
     * @param expandEvents If true, send subqueries for all events, if false then leave them as only eventIds. (can be toggled to prevent events querying facilities querying organizers querying events etc...)
     */
    private void expandOwnerAttribute(QuerySuccessAction successAction, QueryFailureAction failureAction, Facility facility, boolean escapeSharing, boolean expandEvents) {

        QuerySuccessAction ownerReceivedSuccess = new QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                facility.setOwner((Organizer) object);
                if (!escapeSharing) {
                    sendToFacilityListeners(facility, true);
                }
                else {
                    successAction.OnSuccess(facility);
                }
            }
        };
        QueryFailureAction ownerReceivedFailure = new QueryFailureAction() {
            @Override
            public void OnFailure() {
                if (!escapeSharing) {
                    sendToFacilityListeners(null, false);
                }
                else {
                    failureAction.OnFailure();
                }
            }
        };

        // Run the query outside of sharing mode so that it won't get blocked by different queries
        getOrganizer(ownerReceivedSuccess, ownerReceivedFailure, facility.getOwner().getDeviceId(), true, facility, expandEvents);

    }


    /**
     * Gets a facility document from the Firestore Database if the given facilityId exists.
     * This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the facility object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful organizer query
     * @param failureAction The action to take on failed organizer query
     * @param facilityId The facility ID of the facility desired to be received
     * @author Jared Gourley
     */
    public void getFacility(@NonNull QuerySuccessAction successAction, @NonNull QueryFailureAction failureAction, String facilityId) {
        getFacility(successAction, failureAction, facilityId, false, null, true);
    }

    /**
     * Notifies all listeners waiting on the facility query.
     * @param facility The facility received (if successful)
     * @param isSuccessful True if success listeners should be called, false if failure listeners should be called.
     * @author Jared Gourley
     */
    private void sendToFacilityListeners(Facility facility, boolean isSuccessful) {
        if (isSuccessful) {
            for (QuerySuccessAction listener : facilitySuccessListeners) {
                if (listener != null) {
                    listener.OnSuccess(facility);
                }
            }
        }
        else {
            for (QueryFailureAction listener : facilityFailureListeners) {
                if (listener != null) {
                    listener.OnFailure();
                }
            }
        }

        activeFacilityQuery = null;
        facilitySuccessListeners.clear();
        facilityFailureListeners.clear();
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


    /**
     *
     * @param successAction
     * @param failureAction
     * @param deviceId
     * @author Jared Gourley
     */
    public void getAllEventsFromDeviceId(QuerySuccessAction successAction, QueryFailureAction failureAction, String deviceId) {
        // Query the entrant version of the deviceId, and the organizer version if it is valid

        DocumentReference docRef = db.collection("users").document(deviceId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Failure handling
                if (!task.isSuccessful()) { // If query got blocked completely
                    // This case should not be happening under normal circumstances
                    Log.d("WARN", "get failed with ", task.getException());
                    failureAction.OnFailure();
                    return;

                }
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) { // If document with requested ID doesn't exist
                    failureAction.OnFailure();
                    return;
                }

                // Actually get the events now

                ArrayList<Event> eventsList = new ArrayList<Event>();
                Map<String, Object> m = document.getData();

                assert m != null;
                if (m.get("currentEnrolledEvents") != null) {
                    for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentEnrolledEvents")) {
                        eventsList.add(new Event(getIdFromDocRef(eventDocRef)));
                    }
                }
                if (m.get("currentPendingEvents") != null) {
                    for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentPendingEvents")) {
                        eventsList.add(new Event(getIdFromDocRef(eventDocRef)));
                    }
                }
                if (m.get("currentWaitlistedEvents") != null) {
                    for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentWaitlistedEvents")) {
                        eventsList.add(new Event(getIdFromDocRef(eventDocRef)));
                    }
                }
                if (m.get("currentDeclinedEvents") != null) {
                    for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("currentDeclinedEvents")) {
                        eventsList.add(new Event(getIdFromDocRef(eventDocRef)));
                    }
                }

                if ((boolean) m.get("hasOrganizerRights") && m.get("createdEvents") != null) {
                    System.out.println();
                    for (DocumentReference eventDocRef : (ArrayList<DocumentReference>) m.get("createdEvents")) {
                        eventsList.add(new Event(getIdFromDocRef(eventDocRef)));
                    }
                }

                int subQueryCount = eventsList.size();

                if (subQueryCount == 0) {
                    successAction.OnSuccess(eventsList);
                    return;
                }

                QueryTracker queryTracker = new QueryTracker(subQueryCount);

                QuerySuccessAction queryTrackerSuccess = new QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        // Quit if a different query failed
                        if (!queryTracker.stillGoing) {
                            return;
                        }

                        // Actually assign the value
                        Event event = (Event) object;
                        for (int i = 0; i < eventsList.size(); i++) {
                            if (eventsList.get(i).getEventId().equals(event.getEventId())) {
                                eventsList.set(i, event);
                                break;
                            }
                        }

                        queryTracker.currentReceived += 1;
                        System.out.println("getAllEventsFromDeviceId queryTracker.currentReceived: " + queryTracker.currentReceived);

                        // Call the success listeners if all sub queries finished
                        if (queryTracker.currentReceived == queryTracker.totalNeeded) {
                            System.out.println("getAllEventsFromDeviceId queryTracker finishing!");
                            successAction.OnSuccess(eventsList);
                        }

                    }
                };
                QueryFailureAction queryTrackerFailure = new QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        // Mark that this query has failed so other queries should not trigger successes
                        System.out.println("getAllEventsFromDeviceId queryTracker FAILED");
                        queryTracker.stillGoing = false;
                        return;
                    }
                };

                // Launch queries

                System.out.println("eventsList: " + eventsList);
                for (Event event : eventsList) {
                    System.out.println("Launching query for event: " + event.getEventId());
                    getEvent(queryTrackerSuccess, queryTrackerFailure, event.getEventId(), true, new Facility("0")); // Pass a fake facility just so the event doesn't go query for it
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
        Database database = Database.getDB();
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


    public static void insertEventTest() {
        // fake user with android id "Testfolder" (uploads to testfolder folder)
        Database database = Database.getDB();
        Event event = new Event("UPLOAD_EVENT_TEST", "TESTEVENTNAME", "TESTEVENT DESC", 0);
        database.insertEvent(event);
    }


    public static void insertEntrantTest() {
        Database database = Database.getDB();

        Entrant entrant = new Entrant("testlastname", "testfirstname", "testemail", "testphonenumber", "TESTENTRANT", "entrant", false, false);

        Event event1 = new Event("TESTEVENT1", "TESTNAME1", "TESTDESC1", 1);
        Event event2 = new Event("TESTEVENT2", "TESTNAME2", "TESTDESC2", 2);
        Event event3 = new Event("TESTEVENT3", "TESTNAME3", "TESTDESC3", 3);
        entrant.addPendingEvent(event1);
        entrant.addWaitlistedEvent(event2);
        entrant.addWaitlistedEvent(event3);

        database.insertUserDocument(entrant);
    }


    public static void getEventTest() {
        Database database = Database.getDB();

        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
            @Override
            public void OnSuccess(Object object) {
                Event event = (Event) object;

                // Check out the event details themselves:
                System.out.println("Event id: " + event.getEventId());
                System.out.println("Event name: " + event.getName());
                System.out.println("Event description: " + event.getDescription());
                System.out.println("Event price: " + event.getPrice());

                // Check out all the entrants
                for (int i = 0; i < 4; i++) {
                    ArrayList<User> userArray = event.returnEntrantsArrayByIndex(i);
                    System.out.println("ENTRANT ARRAY NUMBER " + i);
                    for (int j = 0; j < userArray.size(); j++) {
                        Entrant entrant = (Entrant) userArray.get(j);
                        System.out.println("\tEntrant id: " + entrant.getDeviceId());
                        System.out.println("\tEntrant name: " + entrant.getFirstName() + " " + entrant.getLastName());
                        System.out.println("\tEntrant email: " + entrant.getEmail());
                        System.out.println("\tEvent phone number: " + entrant.getPhoneNumber());
                        System.out.println("\tEvent enrolled events: " + entrant.getCurrentEnrolledEvents());
                        System.out.println("\tEvent pending events: " + entrant.getCurrentPendingEvents());
                        System.out.println("\tEvent waitlisted events: " + entrant.getCurrentWaitlistedEvents());
                        System.out.println("\tEvent declined events: " + entrant.getCurrentDeclinedEvents());
                    }
                }

                // Check out the facility
                Facility facility = (Facility) event.getFacility();
                if (facility == null) {
                    System.out.println("Facility: null");
                    return;
                }
                System.out.println("facilityId: " + facility.getFacilityId());
                System.out.println("Name: " + facility.getName());
                System.out.println("Location: " + facility.getLocation());
                System.out.println("Owner: " + facility.getOwner());
                Organizer owner = facility.getOwner();
                if (owner != null) {
                    System.out.println("\tdeviceId: " + owner.getDeviceId());
                    System.out.println("\temail: " + owner.getEmail());
                    System.out.println("\tfirstName: " + owner.getFirstName());
                    System.out.println("\tlastName: " + owner.getLastName());
                    System.out.println("\thasAdminRights: " + owner.isAdmin());
                    System.out.println("\thasOwnerRights: " + owner.isOrganizer());
                    System.out.println("\tcreatedEvents: " + owner.getCreatedEvents());
                    System.out.println("\tfacility: " + owner.getFacility());
                }

            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
            @Override
            public void OnFailure() {
                System.out.println("Query attempt failed!");
            }
        };

        database.getEvent(successAction, failureAction, "testEvent");

    }


    /**
     * Function to test querying an entrant. You can run this test by calling
     * Database.getEntrantTest(); somewhere on app launch.
     */
    public static void getEntrantTest() {
        Database database = Database.getDB();
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
                for (int i = 0; i < 4; i++) {
                    ArrayList<Event> eventArray = entrant.returnEventsArrayByIndex(i);
                    System.out.println("EVENT ARRAY NUMBER " + i);
                    for (int j = 0; j < eventArray.size(); j++) {
                        System.out.println("\tEvent id: " + eventArray.get(j).getEventId());
                        System.out.println("\tEvent name: " + eventArray.get(j).getName());
                        System.out.println("\tEvent description: " + eventArray.get(j).getDescription());
                        System.out.println("\tEvent price: " + eventArray.get(j).getPrice());
                    }
                }
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
        Database database = Database.getDB();
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
                System.out.println("facility: " + organizer.getFacility());
                Facility facility = organizer.getFacility();
                if (facility != null) {
                    System.out.println("\tfacilityId: " + facility.getFacilityId());
                    System.out.println("\tFacility Name: " + facility.getName());
                    System.out.println("\tFacility Location: " + facility.getLocation());
                    System.out.println("\tFacility Owner: " + facility.getOwner());
                }
                for (Event event : organizer.getCreatedEvents()) {
                    System.out.println("Event name: " + event.getName());
                }

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


    public static void getFacilityTest() {
        Database database = Database.getDB();
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
            @Override
            public void OnSuccess(Object object) {
                Facility facility = (Facility) object;
                System.out.println("facilityId: " + facility.getFacilityId());
                System.out.println("Name: " + facility.getName());
                System.out.println("Location: " + facility.getLocation());
                System.out.println("Owner: " + facility.getOwner());
                Organizer owner = facility.getOwner();
                if (owner != null) {
                    System.out.println("\tdeviceId: " + owner.getDeviceId());
                    System.out.println("\temail: " + owner.getEmail());
                    System.out.println("\tfirstName: " + owner.getFirstName());
                    System.out.println("\tlastName: " + owner.getLastName());
                    System.out.println("\thasAdminRights: " + owner.isAdmin());
                    System.out.println("\thasOwnerRights: " + owner.isOrganizer());
                    System.out.println("\tcreatedEvents: " + owner.getCreatedEvents());
                    System.out.println("\tfacility: " + owner.getFacility());
                }
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
            @Override
            public void OnFailure() {
                System.out.println("Query attempt failed!");
            }
        };

        database.getFacility(successAction, failureAction, "1a2b3c4567890-0001239389382");
    }


    public static void getQRTest() {
        Database database = Database.getDB();
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


    //I added these for map - Dric
    public void insertLocation(String eventID, String userID, double latitude, double longitude, QuerySuccessAction successAction, QueryFailureAction failureAction) {
        // Reference to the Firestore document for the event location
        DocumentReference eventRef = db.collection("maps").document(eventID);

        // Create a GeoPoint object with the provided latitude and longitude
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

        // Use a transaction to ensure the update/insert is atomic
        db.runTransaction(transaction -> {
            // Get the current document snapshot
            DocumentSnapshot snapshot = transaction.get(eventRef);

            // Check if the document exists
            if (snapshot.exists()) {
                // Retrieve the existing 'locations' map, or create a new one if null
                Map<String, Object> locationMap = (Map<String, Object>) snapshot.get("locations");
                if (locationMap == null) {
                    locationMap = new HashMap<>();
                }

                // Add or update the user's location
                locationMap.put(userID, geoPoint);

                // Update the document
                transaction.update(eventRef, "locations", locationMap);
            } else {
                // If the document doesn't exist, create it with the initial location data
                Map<String, Object> initialLocationMap = new HashMap<>();
                initialLocationMap.put(userID, geoPoint);

                Map<String, Object> data = new HashMap<>();
                data.put("locations", initialLocationMap);

                transaction.set(eventRef, data);
            }

            return null;
        }).addOnSuccessListener(unused -> {
            // Transaction successful
            Log.d("FirestoreInsert", "Location successfully updated/inserted for event: " + eventID);
            successAction.OnSuccess(null); // Pass null or any relevant result as needed
        }).addOnFailureListener(e -> {
            // Transaction failed
            Log.e("FirestoreInsert", "Error updating/inserting location: " + e.getMessage(), e);
            failureAction.OnFailure();
        });
    }

    public void getLocation(String eventId, String userId,
                            OnSuccessListener<LatLng> onSuccessListener,
                            OnFailureListener onFailureListener) {
        Log.d("FirestoreDebug", "Fetching location for eventId: " + eventId + ", userId: " + userId);

        db.collection("maps").document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("FirestoreDebug", "Document fetched successfully for eventId: " + eventId);
                        Log.d("FirestoreDebug", "Document data: " + document.getData());

                        // Get the 'locations' field
                        Map<String, Object> locationMap = (Map<String, Object>) document.get("locations");
                        if (locationMap != null && locationMap.containsKey(userId)) {
                            Object locationObject = locationMap.get(userId);
                            if (locationObject instanceof GeoPoint) {
                                // Handle GeoPoint
                                GeoPoint geoPoint = (GeoPoint) locationObject;
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                onSuccessListener.onSuccess(latLng);
                            } else {
                                onFailureListener.onFailure(new Exception("Invalid location type for user: " + userId));
                            }
                        } else {
                            onFailureListener.onFailure(new Exception("User not found in location map for event: " + eventId));
                        }
                    } else {
                        onFailureListener.onFailure(task.getException() != null
                                ? task.getException()
                                : new Exception("Failed to fetch document for event: " + eventId));
                    }
                });
    }

    //This WILL ONLY BE CALLED WITH THE ASSUMPTION THE ID is a organizer
    public void getFacilityIDbyUserID(String userID, QuerySuccessAction successAction, QueryFailureAction failureAction) {
        // Query the "users" collection for the Organizer's document using the userID
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Check if the query was successful
                if (!task.isSuccessful()) {
                    // If the query fails, handle the failure
                    Log.d("WARN", "getOrganizer failed with ", task.getException());
                    failureAction.OnFailure();
                    return;
                }

                // Retrieve the document snapshot from the task result
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the facilityId from the "facilityID" field
                    String facilityId = document.getString("facilityID"); // Retrieve the facilityId directly from the document

                    if (facilityId != null && !facilityId.isEmpty()) {
                        // Facility ID found, pass it to success callback
                        successAction.OnSuccess(facilityId);
                    } else {
                        // If there's no facilityId, handle accordingly
                        Log.d("WARN", "Facility ID is missing for this organizer.");
                        failureAction.OnFailure();
                    }
                } else {
                    // If the user document doesn't exist
                    Log.d("WARN", "No such document for user ID: " + userID);
                    failureAction.OnFailure();
                }
            }
        });
    }

    // Admin specific Queries


}