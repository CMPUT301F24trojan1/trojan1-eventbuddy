package com.example.trojanplanner.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public Database() {
        db = FirebaseFirestore.getInstance();
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
     * @param uri The uri reference to the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @param successListener The action that should be taken on a successful upload
     * @param failureListener The action that should be taken on a failed upload
     * @author Jared Gourley
     *
     */
    public void uploadImage(Uri uri, @NonNull User owner, OnSuccessListener successListener, OnFailureListener failureListener) {
        String filePath = owner.getDeviceId() + "/" + System.currentTimeMillis() + ".png";
        StorageReference refToSave = storageRef.child(filePath);

        // Attempt to get a bitmap of the uri reference
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        }
        catch (IOException e) {
            System.out.println("uri invalid/no permissions");
            return;
        }

        // Compress and convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Set filepath and bitmap as user attributes
        owner.setPfpFilePath(filePath);
        owner.setPfpBitmap(bitmap);

        // Upload!
        UploadTask uploadTask = refToSave.putBytes(data);
        uploadTask.addOnSuccessListener(successListener);
        uploadTask.addOnFailureListener(failureListener);

    }

    /**
     * Uploads an image to the Firebase Storage area tied to a given user.
     * A wrapper function for the 4-parameter uploadImage designed for easier use. Sets a generic successlistener
     * and failurelistener which should be suitable for most cases.
     * @param uri The uri reference to the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @author Jared Gourley
     */
    public void uploadImage(Uri uri, User owner) {

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

        // Call the proper uploadImage method
        uploadImage(uri, owner, successListener, failureListener);

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
        userMap.put("pfp", null);
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

        db.collection("users")
            .document(user.getDeviceId())
            .set(userMap)
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
        eventMap.put("facility", event.getFacility());
        eventMap.put("price", event.getPrice());
        eventMap.put("status", event.getStatus());
        eventMap.put("eventCapacity", event.getTotalSpots());
        eventMap.put("waitlistCapacity", );
        eventMap.put("eventPhoto", null);
        eventMap.put("requiresGeolocation", );

        eventMap.put("creationTime", System.currentTimeMillis());
        eventMap.put("eventStart", event.getStartDateTime());
        eventMap.put("eventEnd", event.getEndDateTime());
        eventMap.put("waitlistOpen", );
        eventMap.put("watlistClose", );

        eventMap.put("enrolledlist", );
        eventMap.put("waitlist", );
        eventMap.put("pendinglist", );
        eventMap.put("cancelledlist", );

        eventMap.put("isRecurring", event.isRecurring());
        if (event.isRecurring()) {
            eventMap.put("recurrenceFormat", event.getRecurrenceType()); // note: database uses UNTIL_DATE standard but remembers what the organizer prefers
            eventMap.put("recurringEndDate", event.getRecurrenceEndDate());
            eventMap.put("recurringOn", event.getRecurrenceDays());
        }

        db.collection("events")
                .document(event.getEventId())
                .set(eventMap)
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
        successListener.onSuccess();
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
        facilityMap.put("name", );
        facilityMap.put("facilityPhoto", );
        facilityMap.put("owner", );
        facilityMap.put("currentEvents", );

        db.collection("facilities")
                .document(facility.getFacilityId())
                .set(facilityMap)
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
        Event event = new Event();

        event.setName();
        // etc etc etc


        return event;
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



    private Entrant unpackEntrantMap(Map<String, Object> entrantMap) {
        Entrant entrant = new Entrant();

        entrant.setFirstName();
        // etc etc etc


        return entrant;
    }

    /**
     * Gets an entrant document from the Firestore Database if the given androidId exists. Note that
     * admins are organizers are allowed to be queries as entrants because they also have entrant
     * abilities. This action is asynchronous and so the class calling this must initialize a
     * QuerySuccessAction and QueryFailureAction and pass it into the function to determine what
     * action should be taken when receiving the results. On a success, the entrant object can be
     * received by downcasting the object parameter in the QuerySuccessAction.
     *
     * @param successAction The action to take on successful event query
     * @param failureAction The action to take on failed event query
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
                        if (entrant != null) {
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

}
