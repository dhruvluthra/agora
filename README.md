## Introducing **Agora**: A New Mobile Platform to Manage Office Hours

Running office hours is difficult. A lot of people show up, and they all need help- most of the time, from one person. Agora is a new mobile application that allows professor and teaching assistants to more efficiently manage their time, allowing them to help as many people as possible.

### How It Works
Agora simplifies office hours by allowing students to sign up for office hours ahead of time, ensuring they don't waste time and are only at office hours when the teacher is available to help them. Students can enter what they need help with- so that if a group of students needs help on the same topic, the teacher can address the entire group instead of having to address each student individually. Each office hours session also creates a forum on which the teacher can answer common questions, post pictures, and interact with with the students.


### Progress

The application is currently in early stage development.

- Alpha Stage Check Up (April 18, 2017): We have created a formal home screen which allows the user to login to a previously created account or create a new account if they do not have one. This account feature was implemented using Firebase's authentication feature. After the user has logged in, they are taken to the home screen which will be the pivot point for the rest of the functionality of application, with the option to create or join an office hours session and/or quit the application. The functionality of the home screen is the next step of completion: mainly, the creation and management of the session, likely using Firebase's realtime database functionality.

- Beta Stage Check Up (April 24, 2017): We have added more basic functionality to the application, including fundamental user experience features such as logging out. We have also started to build the core
functionality of the application- mainly, the ability to start or join an office hours session. The two key
parts of the office hour SessionActivity will be to join the queue for help or access the forum to post questions that the teacher can view. Both the queue and the forum will be managed by the realtime database service offered by Firebase. We are using JSON data formats for each of the databases. To complete the application, we need to complete the realtime database, including finalizing the data format and the JSON parser. To meet the requirements, we hope to implement a location service that alerts those with the app (using a notification service) both when a session has started and where the session has started. We also plan on supporting images in the forum, so that students can post pictures that the teacher can respond to.


### Team
- Dhruv Luthra
- Lisa Sapozhnikov
- Lara Sonmez
- Jenny Luo

#### About
This project was developed for CS290: Mobile Software Development at Duke University in the Spring of 2017.
