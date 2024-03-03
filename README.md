# Muni
Muni is a personal finance application, as of now, available only as a web application. It lets registered users connect multiple bank accounts, getting live balance and transaction information for each one of them. Users can then categorize each transaction manually or with custom rules and analyse spending.  

The application is built with Java, TypeScript, Spring Boot, React and PostgreSQL. It uses Firebase Authentication for registering and authenticating each user. To connect to internet banking accounts securely GoCardless' Bank Account Data API is used. The application takes advantage of multithreading and caching to optimize response time.  
  
The application is not deployed as of now. You will need to clone it, add application.properties file to the bank-account-data spring app with your GoCardless, Firebase and PostgreSQL authentication info. You will also need to create a config/firebase.tsx file inside muni-web with the firebaseConfig information that you get when registering a new app to Firebase Authentication.
