const admin = require('firebase-admin');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');

// Initialize Firebase Admin SDK
const serviceAccount = require('./serviceKey.json'); // Path to your Firebase service account key

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.use(bodyParser.json()); // To parse incoming JSON requests

// Send FCM message to a topic
app.post('/sendNotification', (req, res) => {
  const { topic, title, message } = req.body;

  if (!topic || !title || !message) {
    return res.status(400).send('Event ID, title, and message are required');
  }

  const to_check = topic;
  // Log the topic to the console
  console.log('Sending notification to topic:', topic);

  // Prepare the message payload
  const messagePayload = {
    notification: {
      title: title,
      body: message,
    },
    topic: topic,  // Send to the topic
  };

  // Send notification
  admin.messaging()
    .send(messagePayload)
    .then((response) => {
      console.log('Successfully sent message:', response);
      res.status(200).send('Notification sent successfully!');
    })
    .catch((error) => {
      console.error('Error sending message:', error);
      res.status(500).send('Error sending notification');
    });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
