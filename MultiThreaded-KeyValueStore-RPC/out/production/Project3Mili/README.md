# PROJECT 3 : Multi-threaded Key-Value Store using RPC
By - Mili Bimal Parikh

## Overview

The project includes a Key-Value store that accepts a string as the value and an integer as the key. 
A remote client can alter and query this data store by remotely invoking methods on the objects our 
server has bound to the registry. The server and client both make use of Java's RMI to support RPC. 
The server now supports the following 3 operations on the data store:

- `PUT, <key>, <value>` - Creates a new entry in the key-value store for the `key`, or, if the `key`
- is already present, changes the existing record with the new value.
- `GET, <key>` -  If the `key` is found in the key-value store, it returns its `value`.
- `DELETE, <key>` - Removes the entry related to the `key` from the key-value store, if there is such a `key` present.

The application allows for the creation of as many servers as necessary and gives clients the 
option to connect to any of them.
However, the application internally employs the two-phase-commit protocol to synchronize the states 
of each server replica and ensure database consistency, even though the client may choose to perform 
methods like PUT and DELETE on the server they are connected to.

This project's goals were to improve one's comprehension of how RPC and 2PC operate and create a system that
is resilient to failures. This goal, in my opinion, has been achieved. As I worked on this project,
it became clear to me that many small aspects must be taken into account before we can come up with a workable
answer. This project's development has given me a brief exposure to the process of creating complex backends
for production apps.

## Technical Impressions

Java and Java's RMI are both used in the project's development. After doing some research on both RMI and
Apache Thrift, I came to the conclusion that while Thrift offers much more granular control, my project
doesn't need it. I therefore made the decision to use RMI, which is a component of the JDK.

### Structure
First, I came to the conclusion that the client and the server should each have their own distinct package.
I placed all classes and interfaces that pertained to the client in the client package, and I
placed all classes and interfaces that pertained to the server in the server package.

The Server package further has separate packages for `data` related classes/interfaces, `driver` for the
Server-Manager Driver and the main Server Driver classes, `serverimpl` for all server implementation 
classes/interfaces and `servermanager` for the classes/interfaces related to the Server-Manager.

Similarly, clint package further has packages for `clientimpl` that contains classes for implementing the
RMI client and `driver` for the client driver class.

### Initialization

The first part to start up is the `ServerManagerDriver`. The server-manager produces a remote object of 
type `ServerManager` upon startup. Servers will eventually use this object to carry out actions.

The servers are then started up later. The server finds the remote object of the server-manager when the 
`ServerDriver` is started. Next, a remote object of type `RemoteServer` is created. Clients will utilize 
this object, which has a user-facing object, to call server methods. A remote object of type 
`ExecutableRemoteServer` is also created by the server. The server-manager will use this object as a 
reference for preparing and executing server calls.

Finally, we launch the `ClientDriver` program, which will connect to a server, to start the client.

### Communication between Client, Server and Coordinator

- The server that the client is directly connected to receives the request first if a client requests 
any actions like PUT or DELETE that can change the state of the key-value store. The server then sends 
this request to the `ServerManager`.
- A list of each server replica is kept up to date by the server-manager. When one of those replicas 
makes a request, it immediately produces an `Action` corresponding to the request and gives it a 
special `actionId`.
- The server-manager then instructs each linked replica to `prepare` for the `Action` identified by 
`actionId`.
- A server replica first records the `Action` and its `actionId` in a hashmap after getting the `prepare` 
request from the server-manager so that it may be referenced later.
-The resource (key) that is about to be updated is then `locked` by the replica. The lock forbids 
using that specific key for any other `Action`.
- It returns true if a replica is successful in locking the resource.
- The server-manager then sends an `execute` message to the replicas if all of them respond 
favorably to the `prepare` message.
- The resource that is to be modified is first unlocked when a replica receives an execution command.
- The replica then makes the necessary changes to the resource and deletes the `Action` from its 
list of actions. Finally, replica responds positively to the call to execute if there are no errors.
- On the other side, the server-manager sends an `abort` message to the replicas if any of them are 
not prepared for the `Action`.
- The replica removes any locks it may have set after getting the `abort` message, and it then 
deletes the `Action` from its list of actions.


### Key-Value Store

The entity known as `KeyValue` is used for CRUD operations and for storing key-value pairs.
Every server manages a separate key-value store.

- We have a `ImmutableKeyValue` for our program that can only read the value for a specific key.
By adding a new key-value pair or removing an existing pair, the `KeyValue` interface
introduces functionality that can change the state of the store, that is, put a new key-value pair or
delete an existing key-value pair.
- `LockKeyValue` is a specific kind of `KeyValue` that enables us to _lock_ a particular key. Once a key 
has been _locked_, it cannot be modified or deleted unless it is specifically _unlocked_.
  \
  There are two implementations of the store:
  - `KeyValueImpl`: Internally stores the key-value pairs in a concurrent hashmap.
  - `LockKeyValueImpl`:This implementation, which is built on top of `KeyValueImpl`, enables the locking mechanism.

### Server-Manager 

To guarantee consistency of the key-value stores across these servers, the server-manager 
interacts with each replica of the server.

- The server-manager ports 1 remote object: `ServerManager`
- The methods in the `ServerManager` interface enable the linked servers to request that the 
server-manager carry out the distributed transaction. 
A connected server can request inclusion in the manager's list of servers using the `addServer` method.

- *put or delete actions*:
    - The server-manager first creates a new `Action`.
    - After then, this `Action` is given a special `actionId`.
    - The linked servers are then sent a `prepare` message from the server-manager along with the 
  `actionId` and `Action`.
    - If all the servers are "prepared", the server-manager sends an `execute` message to them, 
  along with the `actionId` for the `Action` to be executed.
    - If any of the servers are not `prepared`, the coordinator sends an `abort` message to them, 
  alerting them that the `Action` defined by the provided `actionId` must be canceled.

### Server

The server module is used to build a server and initializes the server objects. 
Every server manages a separate key-value store.
- The server looks for the remote `ServerManager` object when it first starts up.
- The server then passes the server-manager down to its own `RemoteServer` object, 
which it then initializes.
- The `RemoteServer` object's remote is then created.
- In order to add this new server to its list of already-existing servers, the server calls the
ServerManager's `addServer` function after creating a remote for the `ExecutableRemoteServer` object.
- Now, the Server is up and running.

#### RemoteServer

- The class `RemoteServer` has methods the client can use to alter and view the status of the key-value store.
- When a method on the remote is called that is designed to change the state of the key-value store, 
it delegated the task to the server-manager by calling the corresponding method on the server-manager. 
After that, the server-manager is in charge of database consistency.
- `RemoteServer` transmits the requested value straight to the client for methods like `GET` that don't 
actually alter the state of the key-value store.

#### ExecutableRemoteServer

- The server-manager facing module, called `ExecutableRemoteServer`, enables 
the server-manager to change the state of the key-value store.
- Any action needs 2PC to be executed.

### Client

The client module locates a registry, initializes the client, and finds the remote object.

`RMIClient.java`: Our `RMIclient` constructor requires three parameters: `name`, `host`, and `port`. 
Additionally, it has a `start()` method. After calling this function, the client uses the 
supplied `host` and `port` to find a registry. 
After locating this registry, it searches the registry for the remote object connected to `name`. 
The remainder of the tasks are then turned over to `ClientController`. In addition, the client 
detects any exceptions that might arise from errors such as the registry not being located or 
the object not being associated with the specified "name," and it logs these to the console.

### Client Controller

The client controller is in charge of receiving user inputs, processing those inputs, 
performing simple validations, and calling the relevant methods on the remote object known as 
`RemoteServer`. 

Three arguments are accepted by the constructor of our `ClientKeyValueController`: a `RemoteServer` 
object, a `Readable` to read inputs from, and an `Appendable` to display the results to. 

Our controller initially displays a welcome message with a list of potential commands after 
executing the start function, and then it starts to listen for user inputs. The controller 
completes its execution and the program is ended if the command is 'q' or 'Q'. In the 
absence of that, the command is examined to determine its validity; if it is, the 
necessary "RemoteServer" methods are called.

The helper methods `processGet`, `processPut` and `processDelete` are used to complete this task. 
If the user's command contains errors, the controller will strive to be as detailed as it can while 
pointing them out and will ask the user to type the command again.

## How To Use This Program

- All JARS can be found in `/res`.

### First, start the Server-Manager

- To start the Server-Manager, run `server.drivers.ServerManagerDriver.java` or the corresponding JAR.
- ServerManagerDriver takes two command-line arguments in any order:
    1. `-p <port>`: port where the server-manager should start. 1024 < `port` < 65535
    2. `-n <name>`: Name of Server-Manager's remote object
- E.g.:
```shell
java -jar ServerManager.jar -p 4000 -n ServerManager
```

### Next, run the server

- To start the server, run `server.drivers.ServerDriver.java` or the corresponding JAR.
- ServerDriver.java takes 5 command line arguments in any order:
    1. `-p <port>`: port where the server should start. 1024 < `port` < 65535
    2. `-n <name>`: Name of server's remote object
    3. `-mp <port>`: Port of the server-manager's remote object
    4. `-mn <name>` name of the server-manager's remote object
    5. `-mh <host>`: host for the server-manager's remote object
- Example :
```shell
 java -jar Server.jar -p 4001 -n Server1 -mp 4000 -mn ServerManager -mh localhost
```
- The server can be operated in several instances with various names on various ports.
```shell
 java -jar Server.jar -p 4002 -n Server2 -mp 4000 -mn ServerManager -mh localhost
```

```shell
 java -jar Server.jar -p 4003 -n Server3 -mp 4000 -mn ServerManager -mh localhost
```

```shell
 java -jar Server.jar -p 4004 -n Server4 -mp 4000 -mn ServerManager -mh localhost
```

```shell
 java -jar Server.jar -p 4005 -n Server5 -mp 4000 -mn ServerManager -mh localhost
```


### Finally, the client

- Run `client.driver.ClientDriver.java` or the corresponding JAR file.
- The client takes 3 command line arguments in any order:
    1. `-p <port number>`: the port that the server registry is accessible at. 
  The port number must fall within the [1024, 65535] range.
    2. `-h <host name>`: server's host name
    3. `-n <name>`: binding name for `RemoteServer` object.
- Example:
- 
```shell
java -jar Client.jar -n Server1 -p 4001 -h localhost
```

The list of authorized commands is below. Case insensitivity applies to all commands. 
The command's parameters are separated by commas (",").

1. `PUT, <key>, <value>`, example, `PUT, 1, Mili`
2. `GET, <key>`, e.g. `GET, 1`
3. `DELETE, <key>` e.g. `DELETE, 1`

Here, the keys must be valid integers. The values are strings.

Here, the keys should be integers. The values should be strings.

The server will try to respond with an error message that is as precise as possible to let the client 
know what elements of the request are incorrect, but if that is not possible, it will respond with a 
message indicating that the command was successfully executed.

The client will ask the user for input after starting up and print the results to standard output. 
The client and server both keep thorough logs of requests, responses, and errors in an appropriately 
labeled txt file created after executing the program in the same directory.

The server pre-populates the datastore with the following keys:

- 1 -> one
- 2 -> two
- 3 -> three
- 4 -> four
- 5 -> five

To run the .jar files, navigate to /res.

## Example Commands

Following are some valid example commands that you can try:

- `PUT, 20, hello`
- `PUT, 51, crazy`
- `DELETE, 20`
- `GET, 51`
- `DELETE, 51`

### Example Run

- First I run the ServerManagerDriver.java and then 2 instances of ServerDriver.java using :
```shell
java -jar ServerManager.jar -p 4000 -n ServerManager
```

```shell
 java -jar Server.jar -p 4001 -n Server1 -mp 4000 -mn ServerManager -mh localhost
```

```shell
 java -jar Server.jar -p 4002 -n Server2 -mp 4000 -mn ServerManager -mh localhost
```
- Next, I run the ClientDriver.java using : 
```shell
java -jar Client.jar -n Server2 -p 4002 -h localhost
```
And I implemented the following commands :
  - GET, 2
  - GET, 1
  - PUT, 1, Nice

```shell
"C:\Users\Mili Parikh\.jdks\corretto-17.0.4.1\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.3.3\lib\idea_rt.jar=58902:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.3.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Users\Mili Parikh\NEU_SEM1\BSDS\Project3Mili\out\production\Project3Mili" client.driver.ClientDriver -n Server2 -p 4002 -h localhost
Port: 4002
Host: localhost
Name: Server2
Welcome! 
You can enter the following case-insensitive commands to perform operations on the key and value: 
		PUT, <(integer) key>, <(string) value>
		GET, <(integer) key>
		DELETE, <(integer) key>
or Enter 'q' or 'Q' to quit

Enter command : 
GET, 2
Success: GET(2) = Two
Enter command : 
GET, 1
Success: GET(1) = One
Enter command : 
PUT, 1, Nice
Success: PUT(1, Nice) executed.
Enter command : 
```

- Next, I run another instance of client which is connected to Server1 and try to get the data that I stored
about at key 1.
I used the following command to run another client instance:
```shell
java -jar Client.jar -n Server1 -p 4001 -h localhost
```
And I enter the command : "GET, 1" :

```shell
"C:\Users\Mili Parikh\.jdks\corretto-17.0.4.1\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.3.3\lib\idea_rt.jar=58916:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.3.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Users\Mili Parikh\NEU_SEM1\BSDS\Project3Mili\out\production\Project3Mili" client.driver.ClientDriver -n Server1 -p 4001 -h localhost
Port: 4001
Host: localhost
Name: Server1
Welcome! 
You can enter the following case-insensitive commands to perform operations on the key and value: 
		PUT, <(integer) key>, <(string) value>
		GET, <(integer) key>
		DELETE, <(integer) key>
or Enter 'q' or 'Q' to quit

Enter command : 
GET, 1
Success: GET(1) = Nice
Enter command : 

```


## Citations

- https://docs.oracle.com/en/java/
