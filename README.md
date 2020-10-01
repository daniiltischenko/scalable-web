# WAES: Assignment Scalable Web
The backend application that gives API to save pairs of Base64 encoded data to the database and to calculate offsets if data differs.

### Tech Stack and Tools
The application had been written with use of standard set of Java technologies:
- Java 8
- Maven 3.3
- Spring Boot 2.3.4
- H2
- JUnit, AssertJ
- Swagger 3
- JaCoCo

### What can be improved
- Consider using queuing to support big amount of requests.
- Consider using caching to support big amount of requests.
- Make logging more verbose.
- Add static code analyzers (PMD, Checkstyle, FindBugs etc.) to help keep code clean.
- Support production ready databases. MySQL or PostgreSQL will do the job.
- Containerize with Docker to get ready for deploy.
- Add git hooks to run static code analyzers and tests on the local machine before pushing to remote branches.
- Add Spring profiles: prod, dev, test.

### How To...
#### ... prepare environment
1. Install Java 8 and Maven 3.3 on the machine.
2. Clone repository with the code base.

#### ... build and run
1. Navigate to the project directory and build the command:
```mvn clean install```
2. Run command 
```java -jar target/scalable-web-0.0.1-SNAPSHOT.jar```

Or lternatively just run the command: 
```mvn spring-boot:run```

#### ... get test report
Building the application with generate directory: `target/code-analysis/jacoco`. Run file `index.html` in the web browser of your choice to see the report.

Alternatively run ```mvn test``` to generate the report.

#### ... get API documentation
Run the application and open http://localhost:9000/swagger-ui/.

#### ... get H2 console
Run the application and open http://localhost:9000/h2-console/.
- ```username: sa```
- ```password: password```

#### ... use the API
The application provides 2 endpoints
- POST http://localhost:9000/v1/diff/{id}/{side}
    - `id`: any numeric identifier to bind pair of binary strings.
    - `side`: can be `right` or `left`.

Returns 201 in case of success.
- GET http://localhost:9000/v1/diff/{id}
    - `id`: the numeric identifier of the pair of binary strings to be compared.
    - requires JSON of defined structure in the request body:
```
{
	"payload" : "ewoJIm5hbWUiOiAicXdlcnR5Igp9"
}
```
Returns 200 and response body with offsets and status.
##### Notes
- Both left and right payloads should be uploaded before comparing.
- Only valid Base64 decoded JSON can be saved.

#### Example
Step 1.
Send POST request with `application/json` content type and request body to http://localhost:9000/v1/diff/1/left
```
{
	"payload" : "ewoJIm5hbWUiOiAicXdlcnR5Igp9"
}

```

Step 2.
Send POST request with `application/json` content type and request body to http://localhost:9000/v1/diff/1/right
```
{
	"payload" : "ewoJIm5tYWUiOiAicXdlcmRvIgp9"
}

```

Step 3.
Send GET to http://localhost:9000/v1/diff/1

The response:
```
{
    "message": "Left and right sides of equal length but payload is different",
    "offsets": [
        {
            "offset": 5,
            "length": 2
        },
        {
            "offset": 16,
            "length": 2
        }
    ]
}
```
This means that decoded strings have 2 differs of length 2 both and start at indexes 5 and 16.

https://www.base64encode.org/ may be used to encode/decode strings.