# TCP Chat — C Server / Java Client

A small two-way TCP chat application, with the server written in C using POSIX sockets and pthreads, and the client written in Java using its higher-level `Socket` API. The project connects one server and one client over a TCP connection and lets each side send and receive text messages concurrently.

The client and server are intentionally implemented in different languages to demonstrate that their implementations are decoupled from the underlying communication protocol. Both endpoints communicate through TCP and an agreed text-based application protocol.

---

## What this is

A single-client, single-server TCP chat exercise. The C server accepts one incoming connection, then spawns two threads — one to read from the socket and print incoming messages, and one to read from standard input and write outgoing messages to the socket. The Java client mirrors this structure on its side, using its own reader/writer thread pair.

The project works directly with POSIX socket and threading APIs on the C side and buffered stream APIs on the Java side, providing two implementations of the same TCP communication model at different levels of abstraction.

### Architecture

```text
C Server                         Java Client

 stdin                              stdin
   │                                  │
   ▼                                  ▼
Writer ─────────── TCP ───────────► Writer
Reader ◄────────── TCP ──────────── Reader
   │                                  │
   ▼                                  ▼
stdout                             stdout
```

The TCP connection is full-duplex: both sides can independently read from and write to the connection.

---

## What it does

* Opens a TCP server on port `8080` and accepts one client connection.
* Once connected, both sides can send and receive text messages concurrently, each in its own thread (one thread per direction).
* Either side can type `./Exit` to close its own socket and stop reading/writing further input.
* The Java client is split into a small set of classes: `ConnectionHandler` sets up the socket and starts both threads, while `ConsoleWriter` and `SocketReader` each implement `Runnable` and handle one direction of the conversation.
* The C server and Java client communicate successfully despite being implemented in different languages.

---

## What it does not do (current limitations)

* **Single client only.** The C server calls `accept()` exactly once; it does not loop to accept additional connections, so only one client can connect during a given run of the server.
* **No graceful handling of an asymmetric disconnect.** If one side of a connection closes first (e.g., the remote peer disconnects), the other side's writer thread can still be blocked waiting on console input, with no signal that the connection is gone. On the Java client, this is partially mitigated by marking the writer thread as a daemon thread, so the JVM won't hang on exit — but there is no active notification to the user that the peer disconnected.
* **No application-level message framing.** TCP is treated as a byte stream. The current implementation assumes text messages but does not implement explicit framing such as length-prefixed messages or another structured message protocol.
* **No authentication, encryption, or error recovery.** This is a plain, unauthenticated TCP connection intended for local/learning use, not a hardened network service.

---

## Project structure

```text
TcpServer.c              — C server: socket setup, accept, spawns reader/writer threads

Client/
  TcpClient.java          — Entry point; opens the socket and delegates to ConnectionHandler

Builder/
  ConnectionHandler.java  — Wraps a connected socket, sets up I/O streams, starts both threads

Runnables/
  ConsoleWriter.java      — Reads from stdin, writes to the socket
  SocketReader.java       — Reads from the socket, writes to stdout
```

---

## How to run

### Server (C)

Compile:

```bash
gcc TcpServer.c -o tcpserver -lpthread
```

Run:

```bash
./tcpserver
```

### Client (Java)

Compile:

```bash
javac Client/TcpClient.java Builder/ConnectionHandler.java Runnables/ConsoleWriter.java Runnables/SocketReader.java
```

Run:

```bash
java Client.TcpClient
```

Start the server first, then start the client. Once connected, type messages in either terminal.

Type `./Exit` to close the local socket.

---

## Technologies used

### C

* POSIX sockets
* `socket()`
* `bind()`
* `listen()`
* `accept()`
* `read()`
* `write()`
* `close()`
* POSIX threads (`pthread`)

### Java

* `java.net.Socket`
* `BufferedReader`
* `PrintWriter`
* `Thread`
* `Runnable`

---

## Key concepts demonstrated

* TCP client/server architecture
* IPv4 socket addressing
* TCP connection establishment
* File descriptors
* Blocking I/O
* Full-duplex communication
* Concurrent reading and writing
* POSIX sockets and threads
* Java socket programming
* Cross-language TCP interoperability
* Basic connection lifecycle management

---

## Possible next steps

* Loop around `accept()` on the server so it can serve more than one client, spawning a handler for each connection.
* Add a shared shutdown mechanism so both threads on a side stop cleanly when either the local or remote end closes the connection.
* Define an explicit application-level message protocol, such as newline-delimited or length-prefixed messages.
* Replace the per-direction thread model with an event-driven approach using `select()`, `poll()`, or `epoll()`.
* Extend the protocol to support multiple clients and server-side message routing.
