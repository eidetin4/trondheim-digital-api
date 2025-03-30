# Student grade API

This project is a Scala 3 based API for serving student grades by subject.

## Prerequisites

- choco (for Windows users)
- apt-get (for Linux users)
- sbt 1.9.x or higher
- Java 11 or higher

## Getting Started
1. Install Java
   - Follow the instructions for your operating system [here](https://www.oracle.com/java/technologies/downloads).
2. Install sbt
   - Follow the instructions for your operating system [here](http://scala-sbt.org/download).
3. Install git
   - Windows:
   ```bash
   choco install git
   ```
   - Linux:
   ```bash
    sudo apt-get install git
   ```
4. Clone the repository:
```bash
   git clone https://github.com/eidetin4/trondheim-digital-api
```
Or download the zip file from the repository and extract it.
5. Navigate to the project directory:
```bash
   cd trondheim-digital-api
```
6. Run the application
```bash
   sbt run
```
The server will start on `localhost:8080`.
7. Test the API

Linux/Mac:
```bash
   curl http://localhost:8080/students?subjectCode=ARB0008
```
Powershell:
```Powershell
   Invoke-RestMethod -Uri http://localhost:8080/students?subjectCode=ARB0008
```
Or use Postman or any other API testing tool to send a GET request to `http://localhost:8080/students?subjectCode=ARB0008`.
## Api endpoints
- GET `/students?subjectCode=ARB0008`
  - Returns a list of students with their grades for the specified subject code.
  - Example response:
```json
{
  "students": [
    {
      "name": "John Doe",
      "grade": "B"
    },
    {
      "name": "Jane Smith",
      "grade": "C"
    }
  ]
}
```

## Running tests
To run the tests, use the following command:
```bash
   sbt test
```

## Project Structure
- `src/main/scala` - Contains the main application code.
    - `api` - Contains the API routes.
    - `models` - Contains the data models.
    - `modules` - Contains the data transformation logic.
    - `services` - Contains the service logic.
    - `contracts` - Contains the contract definitions.
- `src/test/scala` - Contains the test code.
- `build.sbt` - SBT build file.

## Technologies used
- Scala 3.3.5
- Http4s for HTTP server and client
- Cats Effect for functional programming
- Circe for JSON handling
