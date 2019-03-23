
# Description
A visitor Management app which keeps track of people visiting the app

# Workflow
1. The user has to first enter his mobile number in the app
 <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/f.png" width="200" height="300"/>
 </p>
2. If he is a new user, using app for the first time <br>
  2.1 He will have to upload his pictures first <br>
  <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/se.png" width="200" height="300"/>
  <img src="https://github.com/vaibnak/testapp/blob/master/th.png" width="200" height="300"/>
  </p>
  2.2 After clicking his pic, he will be sent an otp using which he will register <br>
  2.3 The pic is compressed using Compressor(Android compression library) using in order to reduce Firebase storage usage <br>
  2.4 He has to enter the otp within 30 seconds <br>
  <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/fo.png" width="200" height="300"/>
  </p>
  2.5 If he enters  wrong otp he will be listed as a suspicious user <br>
  <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/fif.png" width="200" height="300"/>
  </p>
  2.6 If otp entered by him is alright he will be listed as one of the visitors <br>
  <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/six.png" width="200" height="300"/>
  </p>
3. If he is an existing user, his visit will be noted and his visitcount will be incremented by one <br>
 <p align="center">
  <img src="https://github.com/vaibnak/testapp/blob/master/eig.png" width="200" height="300"/>
  <img src="https://github.com/vaibnak/testapp/blob/master/sev.png" width="200" height="300"/>
  </p>
  
# Technologies Used
Java, XML, Android Studio, Firebase realtime Database, Firebase Storage, Firebase authentication, Rxjava, Camerakit, Compressor(Android compression library)
  
