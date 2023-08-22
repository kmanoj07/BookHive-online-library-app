# Bookhive-app
Build your reading habbits. Track the books you read, review it and fuel your reading.

# Application tier
- Backend: Java, Spring Boot Framework
- Database: NoSQL (Cassandra DB)
- cloud: Hosted Cassandra instance on Google cloud
- Front end: HTML/CSS/JAVASCRIPT/Thymeleaf template
- Auth: Spring Security (github OAuth2.0 OAuthentication)

# Application Features
 <div> 
    <p>User can login into Bookhive App using there Github login.<p>
    <p>Login can be extended via Google authentication as well.<p/>
    <img src="/bookhive-images/loging.png" width="600px"/>
 </div>
  <div> 
    <p>User can browser the library of extensive books by searching with title.<p>
    <img src="/bookhive-images/search.png" width="600px"/>
 </div>
  <div> 
  <p>Users are not restricted to search the library even if they are not logged in</p>
      <p>Track the book the need to log into the applicaiton.<p>
    <img src="/bookhive-images/notloggedin.png" width="600px"/>
 </div>
 
 <div> 
  <p>Once User Logged in. It allow to track the book and mark the status as following.</p>
  <ul>
     <li>Reading</li>
     <li>Finished</li>
     <li>Do not finished<l/i>
  </ul>
   <img src="/bookhive-images/trackbook-status.png" width="600px"/>
 </div>
 <div>
  <p>Modified your Reading Completion date</p>
  <img src="/bookhive-images/trackbook-calender.png" width="600px"/>
 </div>
      <div>
        <p>Track Rating</p>
       <img src="/bookhive-images/trackbook-rating.png" width="600px"/>
      </div>
 
 <div>
  Overall the Bookhive is a highly scalable and available application as it handles the big data using Cassandra node on CLUSTER. Where user can browse millions of books and build their reading habit.
  Feel free to contribute and add new feature to this application such as navigate to authors page via link and show the author's book.
 </div>
