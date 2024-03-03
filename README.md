# Muni
Muni is a personal finance application, available only as a web application as of now. It lets registered users connect multiple bank accounts, getting live balance and transaction information for each one of them. Users can then categorize each transaction manually or with custom rules and analyse spending.  

The application is built with Java, TypeScript, Spring Boot, React and PostgreSQL. It uses Firebase Authentication for registering and authenticating each user. To connect to internet banking accounts securely GoCardless' Bank Account Data API is used. The application takes advantage of multithreading and caching to optimize response time.  
  
The application is not deployed as of now. You will need to clone it, add application.properties file to the bank-account-data spring app with your GoCardless, Firebase and PostgreSQL authentication info. You will also need to create a config/firebase.tsx file inside muni-web with the firebaseConfig information that you get when registering a new app to Firebase Authentication.
  
Screenshots:  
![1](https://github.com/HalmaiErik/muni/assets/44140608/ddba425e-1519-48bb-b6c6-6ddf80d79499)  

![2](https://github.com/HalmaiErik/muni/assets/44140608/f5f9df46-928a-43f0-908a-b2baf9e82fba)  

![3](https://github.com/HalmaiErik/muni/assets/44140608/eee79432-23cc-4ea5-8f8b-f4bc59742147)  

![4](https://github.com/HalmaiErik/muni/assets/44140608/e10d9786-0983-4398-9707-9e85be7b8ff9)  
  
