# Key-Value Store : PROJECT 1
By Mili Bimal Parikh

## Overview

The project includes a Key-Value data store that accepts a string as the value and an integer as the key.
By submitting requests to our single-threaded server, a remote client can change and query this data storage.

- `PUT, <key>, <value>` - Creates a new entry in the key-value store for the key, or, if the key is already present, changes the existing record with the new value.
- `GET, <key>` - If the key is found in the key-value store, it returns its value.
- `DELETE, <key>` - removes the entry related to the "key" from the key-value store, if there is such a key present.

This project's goals were to improve knowledge of how TCP and UDP sockets operate and create a system that can withstand errors or inappropriate requests. This goal, in my opinion, has been achieved. As I worked on this project, it became clear to me that many small aspects must be taken into account before we can come up with a workable answer.
This project's development has given me a brief exposure to the process of creating complex backends for production apps.

## Technical Impressions

There were numerous minute considerations that had to be made while developing this project, as was covered in the previous section.

### Structure

First, I came to the conclusion that the `client` and the `server` should each have their own distinct package. I placed all classes and interfaces that pertained to the client in the `client` package, and I placed all classes and interfaces that pertained to the server in the `server` package.

#### Data and Logger 
I added yet another `data` package to the server package. This `data` package possesses a `KeyValue` interface and declares methods for PUT, GET, and DELETE operations. The `KeyValue` interface is implemented by a class called `KeyValueImpl`, which also contains methods that can be used to implement operations such as get, put, and delete on key-value pairs.
Next, I added another package for a custom built logger. This logger has a `Logger` interface, and the `log` method is declared in it. Initially, I attempted to work with log4j; however, I encountered a number of challenges during the setup process, and as a result, I chose to construct a bespoke simple logger. The `TimeStampLogger` class is included in this package, and it is the class that implements the `Logger` interface. The timestamp can be retrieved for each and every log that was recorded using this method. Next, I crafted the `ServerLogger` class, which is derived from the `TimeStampLogger` class and contains methods that generate `Logger` instances for the server. This class extends the `TimeStampLogger` class.

In addition, in order to deal with improper requests, I added a `BadRequestException` that represents a runtime exception that is thrown whenever the request that is received from the client is improperly formatted.

#### Server
I added a start method to the abstract class `AbstractServer` that I created for the purpose of implementing the server. In addition to this, a `ClientHandler` interface was developed, which includes method declarations for both response and execution. Both the `TCPHandler` class and the `UDPHandler` class implement the `ClientHandler` interface. In addition, both classes have private methods for recording all three types of log entries: response, request, and error.
In addition, the server package includes a `CommandController` interface, which declares a method to process commands that are passed in as input. The `ServerCommandController` class implements this interface, and in addition to implementing the overridden methods, it also has private methods to process the "GET," "PUT," and "DELETE" commands. The server package also includes a `ServerCommandController` class, which processes the commands given as input.
Next, the `TCPServer` and `UDPServer` classes extend the `AbstractServer` class. They each have an implementation for the start method and also for the main method, which allows them to run the servers and produce a log (.txt) file.

#### Client
The first thing that I did was create an abstract class called `AbstractClient` within the client package. This class has a method for a welcome message that appears on the screen whenever the client is running, and it also has a method called start that initiates the client. After that, I added the TCPClient class, which extends the AbstractClient, as well as the UDPClient class, which also extends the abstract class. Both of these classes were added in a similar fashion.
The primary methods are contained within TCPClient and UDPClient, both of which, when they are put into operation, generate a log file that logs responses, requests, and errors of every kind.

#### Docker
I faced several issues while creating the docker file. But after spending some time with the youtube videos and the 
documentation provided, I successfully created 4 dockerfiles for 
- DockerFile (for TCPServer.java)
- DockerFile1 (for TCPClient.java)
- DockerFile2 (for UDPServer.java)
- DockerFile3 (for UDPClient.java)
- 
I successfully created the Docker images for all of there 4 docker files and tested them with the servers
and clients running locally. However, I was unable to establish a cutsom virtual network To facilitate the docker containers to communicate with each other.

## How To Use This Program

The list of authorized commands is below. Case insensitivity applies to every command. The command's parameters are
separated by commas (",").
1. `PUT, <key>, <value>`, e.g. `PUT, 36, Mili`
2. `GET, <key>`, e.g. `GET, 36`
3. `DELETE, <key>` e.g. `DELETE, 36`

Here, the keys should be integers. The values should be strings.

The server will try to respond with an error message that is as precise as possible to let the client know what elements
of the request are incorrect, but if that is not possible, it will respond with a message indicating that the command
was successfully executed.

The client will ask the user for input after starting up and print the results to standard output. The client and server
both keep thorough logs of requests, responses, and errors in an appropriately labeled txt file created after executing
the program in the same directory.

The server pre-populates the datastore with the following keys:
- 1 value = one
- 2 value = two
- 3 value = three
- 4 value = four
- 5 value = five

To run the .jar files, navigate to /res.
### Running Server

The port number is the only command-line argument the server accepts.
The port number must fall within the [1024, 65535] range.

The server may not work if the port number is already in use somewhere else.

#### Running TCP Server && TCP Client

Example usage:
(jar file is in res folder)
```
java -jar tcpserver.jar 1025
```
```
java -jar tcpclient.jar 1025 localhost
```

#### Running UDP Server && UDP Client
(jar file is in res folder)
Example Usage:
```
java -jar udpserver.jar 1026
```
```
java -jar udpclient.jar 1026 localhost
```

### Running Client

The client takes in two command line arguments - host ip/name, the port number.

Host IP or name should be non-empty string and the port number must be a number in the range [1024, 65535].

The client may not work if the host ip/name is invalid or the port number is already in use somewhere else.

#### Running TCP Client

example usage:
```
java -jar tcpclient.jar 1025 localhost
```

#### Running UDP Client

Example usage:
```
java -jar udpclient.jar 1026 localhost
```

### Example Requests After Running Client

Here are some legitimate examples of requests you can make:

- `PUT, 2, hello`
- `PUT, 5, crazy`
- `DELETE, 2`
- `GET, 5`
- `DELETE, 5`

To exit (Quit) from the client, you can use either 'q' or 'Q'.

## Example Runs
The log files in the project has as detailed record of when I implemented my TCP Server and TCP client as well
as UDPServer and UDPClient.
This record all the details along with the timestamps and can be considered as an example run for this project.
Files are:
- tcp-client-log.txt
- tcp-server-log.txt
- udp-client-log.txt
- udp-server-log.txt

## Citations

- https://docs.oracle.com/en/java/
- https://www.jetbrains.com/help/idea/docker.html#managing-images
