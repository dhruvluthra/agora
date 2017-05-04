## Introducing **Agora**: A New Mobile Platform to Manage Office Hours

Running office hours is difficult. A lot of people show up, and they all need help- most of the time, from one person. Agora is a new mobile application that allows professor and teaching assistants to more efficiently manage their time, allowing them to help as many people as possible.

### How It Works
Agora simplifies office hours by allowing students to sign up for office hours in a queue once a session is created, ensuring they don't waste time and are only at office hours when the teacher is available to help them. For each office hours session, a messaging forum exists in which students can receive help from their peers, messaging each other questions and answers about the various issues they are having.


### Progress

The application is completed.

- Alpha Stage Check Up (April 18, 2017): We have created a formal home screen which allows the user to login to a previously created account or create a new account if they do not have one. This account feature was implemented using Firebase's authentication feature. After the user has logged in, they are taken to the home screen which will be the pivot point for the rest of the functionality of application, with the option to create or join an office hours session and/or quit the application. The functionality of the home screen is the next step of completion: mainly, the creation and management of the session, likely using Firebase's realtime database functionality.

- Beta Stage Check Up (April 24, 2017): We have added more basic functionality to the application, including fundamental user experience features such as logging out. We have also started to build the corefunctionality of the application- mainly, the ability to start or join an office hours session. The two keyparts of the office hour SessionActivity will be to join the queue for help or access the forum to post questions that the teacher can view. Both the queue and the forum will be managed by the realtime database service offered by Firebase. We are using JSON data formats for each of the databases. To complete the application, we need to complete the realtime database, including finalizing the data format and the JSON parser. To meet the requirements, we hope to implement a location service that alerts those with the app (using a notification service) both when a session has started and where the session has started. We also plan on supporting images in the forum, so that students can post pictures that the teacher can respond to.

- Final Turn In (April 30, 2017): We have completed the general functionality of the application. We now have a functioning queue that students can sign up for help and a messaging forum.

- Completion (May 4, 2017): Updated and refactored the code.

The general breakdown of our five optional features is as follows:

  * Authentication: Using Firebase, we implemented a login service. The user can create an account using their email and password and then login with the same account.
  * Realtime Database: To implement the queue feature of the application, we used Firebase's realtime database
service that allows us to write to and read from stored JSON data files.
  * Messaging Service using a 3rd Party API: We used SendBird's chat messaging API for Android to implement the messaging forum. This is an update from our original plan to use a realtime database.
  * Camera: We use the camera on the phone to allow the user to attach their picture to their queue sign-up to make it easier for the teacher to identify them. The resulting photo is then uploaded to Firebase storage.
  * Location Services: When a session is created, the teacher's location is saved in Firebase. Students joining the session can open Google Maps to the location of office hours to help them navigate to the desired location.

User and programmer documentation can be found in the Github repository. The user documentation outlines how
a typical user can use the application and the programmer documentation gives an overview of the different
classes used in the application.

### Team
- Dhruv Luthra
- Lisa Sapozhnikov
- Lara Sonmez
- Jenny Luo

#### About
This project was developed for CS290: Mobile Software Development at Duke University in the Spring of 2017.
