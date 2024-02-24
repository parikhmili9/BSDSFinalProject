# Multi-Threaded Key-Value Store using RPC : PROJECT 2
By Mili Bimal Parikh

## Overview

The project includes a Key-Value data store that accepts a string as the value and an integer as the key.
Multiple remote clients can alter and query this key-value data stored by remotely invoking methods on the objects 
bound to the registry by our server by sending requests to our multi-threaded server.

To support RPC, the server and client use Java's RMI. Currently, the server supports 3 operations on key-value pair
stored:

- `PUT, <key>, <value>` - Creates a new entry in the key-value store for the key, or, if the key is already present,
changes the existing record with the new value.
- `GET, <key>` - If the key is found in the key-value store, it returns its value.
- `DELETE, <key>` - removes the entry related to the "key" from the key-value store, if there is such a key present.

This project's goals were to improve knowledge of how RPC operates and create a system that can 
withstand errors or inappropriate requests. This goal, in my opinion, has been achieved. As I worked on this project, 
it became clear to me that many small aspects must be taken into account before we can come up with a workable answer.
This project's development has given me a brief exposure to the process of creating complex backends for production
apps.

## Technical Impressions

There were numerous minute considerations that had to be made while developing this project, as was covered in the
previous section.

### Structure

First, I came to the conclusion that the `client` and the `server` should each have their own distinct package.
I placed all classes and interfaces that pertained to the client in the `client` package, and I placed all classes
and interfaces that pertained to the server in the `server` package.

### Data and Logger
I added yet another `data` package to the server package. This `data` package possesses a `KeyValue` interface and
declares methods for PUT, GET, and DELETE operations. The `KeyValue` interface is implemented by a class called
`KeyValueImpl`, which also contains methods that can be used to implement operations such as get, put, and delete on
key-value pairs.

I made the decision to make `KeyValueImpl` class a singleton class, meaning that only one instance of it can be
created, because it stores some data in a manner similar to a database. The instance() method can be used to retrieve
this instance. The actual data is stored internally on a map. To maintain the integrity of our data in the event that
numerous clients seek to access or edit the data, it specifically uses a ConcurrentHashMap. Instead of explicitly 
synchronizing, I chose to utilize a ConcurrentHashMap after realizing that only actions carried out on the Map 
had any impact on the integrity of the data.

Next, I added another package for a custom-built logger. This logger has a `Logger` interface, and the `log` method
is declared in it. Initially, I attempted to work with log4j; however, I encountered a number of challenges
during the setup process, and as a result, I chose to construct a bespoke simple logger. 

The `TimeStampLogger` class is included in this package, and it is the class that implements the `Logger`
interface. The timestamp can be retrieved for each and every log that was recorded using this method. Next, I 
crafted the `ServerLogger` class in `server` package, which is derived from the `TimeStampLogger` class and 
contains methods that generate `Logger` instances for the server. This class extends the `TimeStampLogger` class.

Similarly, I crafted the `ClientLogger` class in `server` package, which is derived from the
`TimeStampLogger` class and contains methods that generate `Logger` instances for the server.

In addition, in order to deal with improper requests, I added a `BadRequestException`
that represents a runtime exception that is thrown whenever the request that is received from the client is
improperly formatted.

### Server
The server module is used to build a server and initializes the server objects.
The constructor of my RMI Server, `RMIServer.java`, accepts the two parameters `name` and `port`. It has a 
`start()` method as well. The server starts a registry at the specified port after the start method has been called, 
and it binds the `KeyValue` remote object to the specified name. Any `RemoteExceptions` that may arise while 
the program is running are caught by the start method, which then prints the message to the console.

`RMIServer.java` has an implementation for the start method and also for the main method, which allows it to run the
server and produce a log (.txt) file.

### Client
The client module finds a registry, initializes the client, and finds the distant object.
The constructor of our RMI client, `RMIClient.java`, accepts three parameters: `name`, `host`, and `port`. There is a
`start()` method as well. When this method is invoked, the client uses the specified host and port to find a registry. 
After locating this register, it searches the registry for the remote object bound to name. The remaining tasks 
are then handed off to the `Controller`. Additionally, the client detects any `RemoteExceptions` or `NotBoundException` 
that might arise from the registry not being located or the object not being tied to the specified name, and 
emits an error message to the console.

`RMIClient.java` has an implementation for the start method and also for the main method, which allows it to run 
the server and produce a log (.txt) file.

### Controller for Client
The controller is in charge of receiving user input, processing it, performing simple validations, and invoking the 
necessary methods on the `KeyValue` remote object. 

My `KeyValueController's` constructor has three inputs: a `KeyValue` object, a `Readable` for reading inputs, and
an `Appendable` for printing results. My controller initially displays a welcome message with a list of potential
commands after executing the start function, and then it starts to listen for user inputs. The controller completes
its execution and the program is terminated if the command is `q` or `Q`. If the command is not legitimate, it is 
examined to determine whether it is, and if it is, the necessary `KeyValue` functions are executed.

[//]: # (### Docker)

[//]: # (I faced several issues while creating the docker file. But after spending some time with the youtube videos and the)

[//]: # (documentation provided, I successfully created 4 dockerfiles for)

[//]: # (- DockerFile &#40;for TCPServer.java&#41;)

[//]: # (- DockerFile1 &#40;for TCPClient.java&#41;)

[//]: # (- DockerFile2 &#40;for UDPServer.java&#41;)

[//]: # (- DockerFile3 &#40;for UDPClient.java&#41;)

[//]: # (-)

[//]: # (I successfully created the Docker images for all of there 4 docker files and tested them with the servers)

[//]: # (and clients running locally. However, I was unable to establish a cutsom virtual network To facilitate the docker containers to communicate with each other.)

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

The `port` number (Integer) is a command-line argument the server accepts.
The port number must fall within the [1024, 65535] range.
The server also needs a `name` (String) as a command-line argument.

The server may not work if the port number is already in use somewhere else.

#### Running RMI Server

Example usage:
(jar file is in res folder)
```
java -jar rmiserver.jar 1029 localKeyValue
```

### Running Client

The client takes in three command line arguments - `host`, `name` and the `port` number.

Host and name should be non-empty string and the port number must be a Integer in the range [1024, 65535].

The client may not work if the host ip/name is invalid or the port number is already in use somewhere else.

#### Running RMI Client

example usage:
```
java -jar rmiclient.jar 1029 localhost localKeyValue
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
The log files in the project has as detailed record of when I implemented my RMI Server and RMI client.
This record all the details along with the timestamps and can be considered as an example run for this project.

Files are (in /res folder):
- rmi-client-log.txt
- rmi-server-log.txt

## Citations

- https://docs.oracle.com/en/java/
- https://www.jetbrains.com/help/idea/docker.html#managing-images
