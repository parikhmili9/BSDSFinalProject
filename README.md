Author - Mili Parikh

Time Frame - September 2022 to December 2022

Languages and Tools - Java, Visual Studio Code, Docker, GitHub 

_____________________________________________________________________________________________________________________________

These the projects I did while taking the course "Building Scalable Distributed Systems" at Khoury College of Computer Sciences, Northeastern University.

1. Single Server - Key Value Store (TCP and UDP)
   The project includes a Key-Value data store that accepts a string as the value and an integer as the key. By submitting requests to our single-threaded server, a remote client can change and query this data storage.

    - `PUT, <key>, <value>` - Creates a new entry in the key-value store for the key, or, if the key is already present, changes the existing record with the new value.
    - `GET, <key>` - If the key is found in the key-value store, it returns its value.
    - `DELETE, <key>` - removes the entry related to the "key" from the key-value store, if there is such a key present.

    This project's goals were to improve knowledge of how TCP and UDP sockets operate and create a system that can withstand errors or inappropriate requests. This goal, in my opinion, has been achieved. As I worked on this project, it became clear to me that many small aspects must be taken into account before we can come up with a workable answer.
    This project's development has given me a brief exposure to the process of creating complex backends for production apps.

2. Multi-Threaded Key Value Store using Java's RMI
   The project includes a Key-Value data store that accepts a string as the value and an integer as the key.
    Multiple remote clients can alter and query this key-value data stored by remotely invoking methods on the objects bound to the registry by our server by sending requests to our multi-threaded server.

    To support RPC, the server and client use Java's RMI. Currently, the server supports 3 operations on key-value pair stored:

    - `PUT, <key>, <value>` - Creates a new entry in the key-value store for the key, or, if the key is already present,
    changes the existing record with the new value.
    - `GET, <key>` - If the key is found in the key-value store, it returns its value.
    - `DELETE, <key>` - removes the entry related to the "key" from the key-value store, if there is such a key present.

    This project's goals were to improve knowledge of how RPC operates and create a system that can withstand errors or inappropriate requests. This goal, in my opinion, has been achieved. As I worked on this project, it became clear to me that many small aspects must be taken into account before we can come up with a workable answer.
    This project's development has given me a brief exposure to the process of creating complex backends for production apps.

3. Multi-Threaded Key Value Store using Java's RPC
   The project includes a Key-Value store that accepts a string as the value and an integer as the key. A remote client can alter and query this data store by remotely invoking methods on the objects our server has bound to the registry. The server and client both make use of Java's RMI to support RPC. 
   The server now supports the following 3 operations on the data store:

    - `PUT, <key>, <value>` - Creates a new entry in the key-value store for the `key`, or, if the `key`
    - is already present, changes the existing record with the new value.
    - `GET, <key>` -  If the `key` is found in the key-value store, it returns its `value`.
    - `DELETE, <key>` - Removes the entry related to the `key` from the key-value store, if there is such a `key` present.

    The application allows for the creation of as many servers as necessary and gives clients the option to connect to any of them.
    However, the application internally employs the two-phase-commit protocol to synchronize the states of each server replica and ensure database consistency, even though the client may choose to perform  methods like PUT and DELETE on the server they are connected to.

    This project's goals were to improve one's comprehension of how RPC and 2PC operate and create a system that is resilient to failures. This goal, in my opinion, has been achieved. As I worked on this project, it became clear to me that many small aspects must be taken into account before we can come up with a workable answer.
    This project's development has given me a brief exposure to the process of creating complex backends for production apps.

4. Key Value Store using PAXOS

    The application lets you make as many servers as you want, and the client can connect to any of them. The client can choose to call methods like PUT and DELETE on the server they are connected to. 
    However, the application uses the PAXOS algorithm to manage the distributed database.
    The goal of this project was to help learn more about how RPC and PAXOS work and to create a system that can handle failures. I think that this goal has been met.
    As I worked on this project, I quickly realized that we need to think about a lot of small things before we can come up with a solid solution.
    Putting together this project has given me a short taste of what it might be like to make complicated backends for apps that people actually use.
