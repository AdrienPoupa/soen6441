# TweetAnalytics

SOEN6441 Assignments.

## Running

Run this using [sbt](http://www.scala-sbt.org/).  

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

## Team Members


| Name                      | Student ID    | Contributions                      |
| :-----------------------: |:-------------:| :---------------------------------:|
| Adrien Poupa              | 40059458      | TwitterController, Models, JavaDoc |
| Jialu Wang                | 40019673      | Framework architecture, twitter api|
| Wei Li                    | 40027433      | TBD                                |
| Vamsikrishna Tamil Selvan | 40010750      | Test cases,JavaDoc,Documentation                               |
| Manoj Kumar Muppavarapu   | 40042560      | JavaDoc,Test cases,Documentation                               |

## Controllers

There are several demonstration files available in this template.

- TwitterController.java:

  Home to the TweetAnalytics application. Handles searching for the last 10 tweets based on a keyword, 
  and displays the latest tweets of a user.

- HomeController.java:

  Shows how to handle simple HTTP requests.

- AsyncController.java:

  Shows how to do asynchronous programming when handling a request.

- CountController.java:

  Shows how to inject a component into a controller and use the component when
  handling requests.
  
## Models

There are several models used in this project. They are simple POJO (Plain Old Java Object) used by Jackon's Object Mapper to map the JSON returned by Twitter's API to a Java Object.

- SearchResult.java:

  Handles the JSON object returned by Twitter's search API.
  
- Status.java:

  Handles the status included in both Search and Profile.
  
- User.java:

  Handles the user included in both Search and Profile.

## Components

- Module.java:

  Shows how to use Guice to bind all the components needed by your application.

- Counter.java:

  An example of a component that contains state, in this case a simple counter.

- ApplicationTimer.java:

  An example of a component that starts when the application starts and stops
  when the application stops.

## Filters

- ExampleFilter.java

  A simple filter that adds a header to every response.
