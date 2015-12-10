# CodingExerciseAsyncRPCClientServer
An asynchronous RPC client-server using a message bus

Pull the following projects
  GenericMessageBus
  RPCServer
  RPCClient
  
Pre-requisite:
RabbitMQ server needs to be installed and running. The host/port of the RabbitMQ server needs to be configured in the project.properties file for both RPCServer and RPCClient:
  - RPCServer/src/main/resources
  - RPCClient/src/main/resources

Run “mvn install” in each of the project directories (GenericMessageBus needs to be built first)

Start the RPC Server
  Set the classpath to the dependent libraries and the target/classes directory
  java com.exercise.server.RPCServer

Run the RPC Client
  Set the classpath to the dependent libraries and the target/classes directory
  java com.exercise.client.RPCClient 2+(8-3)*6/2;.
  
The statement needs to be ended with a ";" followed by a "."
