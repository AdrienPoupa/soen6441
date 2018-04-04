# TweetAnalytics

SOEN6441 Assignment 2. Now Tweet search is reactive!

## Running

Run this using [sbt](http://www.scala-sbt.org/).  

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

## Team Members


| Name                      | Student ID    | Contributions                        |
| :-----------------------: |:-------------:| :-----------------------------------:|
| Adrien Poupa              | 40059458      | TwitterController, Models, JavaDoc   |
| Jialu Wang                | 40019673      | Framework architecture, Twitter API  |
| Wei Li                    | 40027433      | Junit tests, JavaDoc, UI             |
| Vamsikrishna Tamil Selvan | 40010750      | Test cases, JavaDoc, Documentation   |
| Manoj Kumar Muppavarapu   | 40042560      | JavaDoc, Test cases, Documentation   |

## Controllers

- WebSocketController.java:

  Handles the WebSocket

- HomeController.java:

  Displays the homepage

- TwitterController.java:

  Handles the search and profile pages

## Models

- SearchResult.java:

  Handles the JSON object returned by Twitter's search API.
  
- Status.java:

  Handles the status included in both Search and Profile.
  
- User.java:

  Handles the user included in both Search and Profile.
  

## Tests

- testGetQuery: For search result 

- testSetQuery: For search result

- testSetStatuses:For search result 

- testSetFullText:For Statusch result

- testGetStatuses:For search result

- testUserParentActorCreate: For Messages

- testWatchSearchResults: For Messages

- testUnWatchSearchResults: For Messages

- testSearchResults: For Messages

- testStatusesMessages: For Messages

- testRegisterActor: For Messages

- testMessages: For Messages

- testTickMessage : For SearchResultsActorMessages 


- testWatchSearchResults: For SearchResultsActorMessages

- testGetKeyword: For SearchResultsActorMessages

- testSetKeyword: For SearchResultsActorMessages

- testGetStatuses:For SearchResultsActorMessages

- testSetStatuses:For SearchResultsActorMessages

- testGetTwitterService :For SearchResultsActorMessages

- testSetTwitterService: For SearchResultsActorMessages

- testTickClass: For SearchResultsActorMessages

- testSearchResultsActor: For SearchResultActor

- testGetUser: For Status

- testSetUser: For Status

- testGetFullText: For Status

- testSetFullText: For Status

- testGetType : For Status

- testGetId : For Status

- testSetId:For Status

- equalsContract: For Status

- testUserActor: For UserActor

- testSetSearchResultsActor: For UserActor

- testSetMaterializer: For UserActor

- testSetSearchResultsMap: For UserActor

- testGetLocation: For User

- testSetLocation: For User

- testGetDescription: For User

- testSetDescription : For User

- testGetFollowers :For User

- testSetFollowers: For User

- testGetFriends: For User

- testSetFriends: For User

- testGetId :For User

- testSetName: For User

- testGetScreenName : For User

- testSetScreenName : For User
