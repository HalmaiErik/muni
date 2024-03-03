# Muni
Muni is a personal finance application, available only as a web application as of now. It lets registered users connect multiple bank accounts, getting live balance and transaction information for each one of them. Users can then categorize each transaction manually or with custom rules and analyse spending.  

The application is built with Java, TypeScript, Spring Boot, React and PostgreSQL. It uses Firebase Authentication for registering and authenticating each user. To connect to internet banking accounts securely GoCardless' Bank Account Data API is used. The application takes advantage of multithreading and caching to optimize response time.  
  
The application is not deployed as of now. You will need to clone it, add application.properties file to the bank-account-data spring app with your GoCardless, Firebase and PostgreSQL authentication info. You will also need to create a config/firebase.tsx file inside muni-web with the firebaseConfig information that you get when registering a new app to Firebase Authentication.
  
Screenshots: (some details are censored by a black box :P)
![1](https://github.com/HalmaiErik/muni/assets/44140608/672a587d-7917-4ea6-ba9f-f7fa9e0ada2f)  

![2](https://github.com/HalmaiErik/muni/assets/44140608/8910f447-b535-45f4-9e9b-3b5db55ed427)  

![3](https://github.com/HalmaiErik/muni/assets/44140608/d140ea16-2f47-4ad1-81a4-4bc21a151df5)  
  
![4](https://github.com/HalmaiErik/muni/assets/44140608/e10d9786-0983-4398-9707-9e85be7b8ff9)  
  
