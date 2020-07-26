# Messaging demo

### Build and run (Docker)
* to build the project, run `./gradlew build` from the top level folder
* then run it with `docker-compose up`
* Once it's running, you can test the application through [Swagger-UI](http://localhost:8080/swagger-ui.html). 
(You may be asked by Swagger to set  base path, in that case enter `http://localhost:8080/` in the dialog)

### Description

* To create a new user, you can post to the `/users` endpoint a json object like the following
```
{
 "nickName": "nickname of the user"
}
```
If the request succeeds, the service answers with status code 200, and a representation of the newly created user.
```
{
    "id": "33f336a366794522ab0c237cc64edddb",
    "nickName": "string"
}
```

* To send a new message, you can post to the `messages` endpoint a json object like the following
```
{
  "message": "the text that I want to send",
  "receiverId": "idOfTheReceiver"
}
```
the id of the user that is sending the message should be passed in the `userid` header.\
If the request succeeds, the service will return status code 200, and a json representation of the newly created message
```
{
  "id": 123123123,
  "message": "the text that I want to send",
  "receiver": {
    "id": "idOfTheReceiver",
    "nickName": "Reece"
  },
  "sender": {
    "id": "idOfTheSender",
    "nickName": "Sandy"
  }
}
```
* To view all the messages that you received, you can make a get request to the `/messages/received` endpoint.\
The id of the user that is sending the message should be passed in the `userid` header.\
Some optional query parameters can be passed:
  * `senderId` : the ID of the sender, to restrict the answer to the messages sent by a specific user
  * `limit` : the maximum amount of messages to be returned (if not specified, the default value is 50)
  * `startingId` : the minimum allowed ID of the messages that will be returned (The default value is -1)
The messages will be returned in a json object like the following
```
{
  "hasMore": false,
  "messages": [
    {
      "id": 0,
      "message": "some text",
      "receiver": {
        "id": "123",
        "nickName": "Richie"
      },
      "sender": {
        "id": "456",
        "nickName": "Sam"
      }
    }
  ]
}
```
The messages are sorted by id (decreasing), or rather, by creation date (newest first).\
The field `hasMore` specifies whether there are more messages that can be loaded but have been filtered out (because the `limit` was hit)\
In case `hasMore` is true, you can make a new request specifying the last message's id as`startingId` to load the next "page" of results.


* To view all the messages that you sent, you can make a get request to the `/messages/sent` endpoint.\
As usual The id of the user that is sending the message should be passed in the `userid` header.\
Some optional query parameters can be passed, like in `/messages/received`:
  * `limit` : the maximum amount of messages to be returned (if not specified, the default value is 50)
  * `startingId` : the minimum allowed ID of the messages that will be returned (The default value is -1)
The messages will be returned with the same json structure as `/messages/received`

* The endpoints handle some incorrect input, and answer with status code 400 and a descriptive error message in the following cases:
  * you try to create an user, and an user with the same nickname already exists
  * you don't pass the `userid` header when required
  * the user identified by `userid` or the one in the `senderId` parameter does not exist
  * you try send a message to yourself, 
  * you try to get the received messages, and you specify your user id in the `senderId` parameter
  
* When a message gets created, a message is put on a RabbitMQ queue


