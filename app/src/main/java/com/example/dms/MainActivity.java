package com.example.dms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.media.MediaPlayer;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context con=this;
        setContentView(R.layout.activity_main);
        // Write a message to the database
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("access");
//        DatabaseReference myRef1 = database.getReference("User");
//        TextView access=findViewById(R.id.access);
        TextView user_name = findViewById(R.id.user_name);
        TextView user_name2 = findViewById(R.id.user_name2);
        TextView user_name3 = findViewById(R.id.user_name3);
        // Reference to the collection
        CollectionReference petsCollection = db.collection("Pets");
        CollectionReference seatbeltCollection = db.collection("Seatbelt");
        CollectionReference faceCollection = db.collection("Face");
// Fetch all documents
        seatbeltCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }

                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    String status = document.getString("status");
                    user_name2.setText(status);
                }
            }
        });
        petsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }
                String sentence="";
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    String status = document.getString("status");
                    sentence+="Pet "+document.getId()+": "+status+"\n";
                    user_name.setText(sentence);
                    if (status != null && status.equals("wandering")) {
                        showNotification(this, "Alert", "The status is wandering!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(con,R.raw.alerting);

                        // Start playing the sound
                        mediaPlayer.start();

                        // Schedule a task to stop the sound after 1 second
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Check if the media player is playing
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    // Stop the media player
                                    mediaPlayer.stop();
                                    // Release the media player resources
                                    mediaPlayer.release();
                                }
                            }
                        }, 1000);
                    }
                }
            }
        });

        faceCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }

                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    String face = document.getString("name");
                    user_name3.setText(face);
                }
            }
        });




//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                access.setText(value);
//                Log.d(TAG, "Value is: " + value);
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//        myRef1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                user_name.setText(value);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
    }
    private void showNotification(EventListener<QuerySnapshot> context, String title, String message) {
        // Define a unique notification channel ID
        String channelId = "my_channel_id";

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Register the channel with the system
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icony)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, builder.build());
    }
}