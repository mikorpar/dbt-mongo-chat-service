
# ![English](https://cdn3.iconfinder.com/data/icons/142-mini-country-flags-16x16px/32/flag-usa2x.png) Mongo chat service

Repository with backend for chat app built in Java using Spring Boot and MongoDB.

# Description

## Technologies and tools
  - **Java 8** with **Spring Boot** framework
  - **MongoDB** database
  - **GridFS** (file storage)
  - **JWT** (security)
  - [**REST**](#rest-operations-and-examples) (login, user operations, file operations, email verification, password reset, token refresh)
  - [**WebSocket**](#websocket-messages-and-examples) (active users, groups and group messages)
  - Java **Keytool** (SMTP server certificate storage)

## Installation

&nbsp;
1\. Clone the repository
```bash
git clone git@github.com:mikorpar/dbt-mongo-chat-service.git
```
&nbsp;
2\. Go to the project directory and navigate to **application.properties** file

```bash
cd ./src/main/resources/application.properties
```
&nbsp;
3\. In application.properties fill missing values for properties 

```yaml
# mongodb properties
spring.data.mongodb.uri=
spring.data.mongodb.database=
spring.data.mongodb.gridfs.bucket=

# jwt properties
jwt.secret=

# mail properties
spring.mail.host= 
spring.mail.port=
spring.mail.username=
spring.mail.password=
```
&nbsp;
4\. Import SMTP server certificate to Java Keytool. Example for [gmail](http://notepad2.blogspot.com/2012/04/import-gmail-certificate-into-java.html). 
*Note: AV tool may intercept email communication. In that case AV certificate needs to be imported to keystore in order to complete certificate chain.*
   
### Compile and run in terminal 

&nbsp;
5\. Go to the root of the project directory and execute 

```bash
mvn compile # compiles project and generates class files in target directory
```

```bash
mvn package # generates jar archive in target directory
```

&nbsp;
6\. Run the app 
```bash
java -jar ./target/mongo_chat_api.jar
```

### Compile and run in IntelliJ

&nbsp;
5\. Load project (File > Open > Select project directory > click on "OK" button)

&nbsp;
6\. Click on **play** button

# REST operations and examples

This section contains operations for users and bucket.files collections in database. Every collection in DB has its own path(s). 

All operations, except [those](#endpoints-with-open-access), require **Authentication** header parameter with JWT token passed as value. 

## Login

`POST /login` - user login 

| HTTP stats code | Explanation       |
|:---------------:|:------------------|
|       200       | Successful login  |
|       403       | Wrong credentials |

Request body (x-www-form-urlencoded) example.
```json
{
  "username": "user1",
  "password": "Q9mfQh&PA{+9"
}
```

If login is **successful**, response contains `access-token` and `refresh-token` header parameters with corresponding JWT tokens.

When client makes request, Authentication header parameter must be present and contain `access-token` value (Bearer schema is used). When `access-token` expires, client can re-login or send request to [that](#tokens) endpoint.

## Users

`GET /users` - all users   

| HTTP stats code | Explanation          |
|:---------------:|:---------------------|
|       200       | OK                   |
|       403       | Non valid auth token |

Response body example.
```json
[
    {
        "id": "6120c969de8ea15910ccb17c",
        "email": "usr1@gmail.com",
        "username": "user1",
        "activated": true
    },
    {
        "id": "61227466d7c1f56410265961",
        "email": "john.doe@hotmail.com",
        "username": "user2",
        "activated": true
    },
    {
        "id": "61262ef0fd1b0238349a043d",
        "email": "jane.doe@gmail.com",
        "username": "user3",
        "activated": true
    },
    {
        "id": "6200f802691127352cfb37e6",
        "email": "marcus.miller@yahoo.com",
        "username": "mmiller",
        "activated": true
    },
    {
        "id": "6200fd05691127352cfb3830",
        "email": "steve_t@gmail.com",
        "username": "steve312",
        "activated": true
    }
]
```

`GET /users/me` - current user

| HTTP stats code | Explanation          |
|:---------------:|:---------------------|
|       200       | OK                   |
|       403       | Non valid auth token |

Response body example.
```json
{
    "id": "6247a915ed7e4f2466f4298d",
    "email": "gglbksdyacvmemaqvo@bvhrk.com",
    "username": "user1",
    "activated": true
}
```

`GET /users/activate?token=<token>` - registration verification (`token` query parameter is required)

| HTTP stats code | Explanation                                                     |
|:---------------:|:----------------------------------------------------------------|
|       200       | Successful activation                                           |
|       400       | Token expired, token already confirmed, more recent token found |
|       404       | User with passed token not found                                |

Response body example.
```json
{
  "message": "Verification is successful"
}
```

`GET /users/passwd-reset?email=<email>` - password reset (`email` query parameter is required)

| HTTP stats code | Explanation |
|:---------------:|:-----------:|
|       200       |     OK      |

Response body example.
```json
{
  "message": "If a matching account was found an email with new password was sent"
}
```

`POST /users` - user registration

| HTTP stats code | Explanation                     |
|:---------------:|:--------------------------------|
|       201       | User created                    |
|       403       | Non valid auth token            |
|       422       | Username or email already taken |

Request body example.
```json
{
    "email": "gglbksdyacvmemaqvo@bvhrk.com",
    "username": "user1",
    "password": "Q9mfQh&PA{+9"
}
```

Response body example.
```json
{
    "id": "6247a915ed7e4f2466f4298d",
    "email": "gglbksdyacvmemaqvo@bvhrk.com",
    "username": "user1",
    "activated": false
}
```

`PUT /users/me` - change current user credentials (username and/or password)

| HTTP stats code | Explanation          |
|:---------------:|:---------------------|
|       200       | OK                   |
|       400       | Username taken       |
|       403       | Non valid auth token |

Response body example.
```json
{
    "id": "6247a915ed7e4f2466f4298d",
    "email": "gglbksdyacvmemaqvo@bvhrk.com",
    "username": "user1!",
    "activated": true
}
```

`DELETE /users/me` - delete current user

| HTTP stats code | Explanation          |
|:---------------:|:---------------------|
|       200       | OK                   |
|       403       | Non valid auth token |

Examples.

`http://localhost:8080/users/reg-ver?token=f3ba404e-75bf-418f-baa3-c4f825d99b0d`

## Files

`GET /files/{id}` - file download

| HTTP stats code | Explanation              |
|:---------------:|:-------------------------|
|       200       | OK                       |
|       403       | Non valid auth token     |
|       404       | File with {id} not found |

`POST /files` - file upload

| HTTP stats code | Explanation          |
|:---------------:|:---------------------|
|       200       | OK                   |
|       403       | Non valid auth token |

Response body example.
```json
{
    "id": "624260b9fe9374185e21121e"
}
```

`DELETE /files/{id}` - delete file

| HTTP stats code | Explanation              |
|:---------------:|:-------------------------|
|       200       | OK                       |
|       403       | User not file owner      |
|       404       | File with {id} not found |

Examples.

`http://localhost:8080/files/61fdd251b9acc367388d1722`

## Tokens

`GET /tokens/refresh` - get new access and refresh tokens (Authentication header parameter must contain `refresh-token` value (Bearer schema is used))

| HTTP stats code | Explanation             |
|:---------------:|:------------------------|
|       200       | Successful login        |
|       403       | Non valid refresh token |

If request is **successful**, response contains `access-token` and `refresh-token` header parameters with corresponding values. 

## Endpoints with open access

| Endpoints                 | 
|:--------------------------|
| `POST /login`             |
| `GET /users/activate`     |
| `GET /users/passwd-reset` |

# WebSocket messages and examples

This section contains WS messages for groups and users collections in database. Every collection in DB has its own path(s). 

## Users

### Subscribe

`app/users/online` - active (online) users

Response body example.
```json
[
  "mmiller",
  "steve312",
  "user1"
]
```

`topic/users` - user event notifications (connect, disconnect)

Response body examples.

Connect event
```json
{
  "type":"CONNECTED",
  "user_id":"6200fd05691127352cfb3830",
  "username":"steve312"
}
```

Disconnect event
```json
{
  "type":"DISCONNECTED",
  "user_id":"6200fd05691127352cfb3830",
  "username":"steve312"
}
```

## Groups

### Subscribe

`app/groups/all` - all user groups

Response body example.
```json
[
  {
    "id":"6257a074d4ab34303643eb81",
    "admin":"6247a915ed7e4f2466f4298d",
    "users":[
      "6247a915ed7e4f2466f4298d",
      "61227466d7c1f56410265961"
    ],
    "last_message_seeners":[
      "6247a915ed7e4f2466f4298d"
    ],
    "name":"G1",
    "created_at":"2022-04-14T04:17:56.275+00:00",
    "updated_at":"2022-04-24T17:12:50.252+00:00"
  },
  {
    "id":"624a7bf18630a84dac51b4f7",
    "admin":"6247a915ed7e4f2466f4298d",
    "users":[
      "6247a915ed7e4f2466f4298d",
      "61227466d7c1f56410265961"
    ],
    "last_message_seeners":[
      "6247a915ed7e4f2466f4298d"
    ],
    "name":"G5",
    "created_at":"2022-04-04T05:02:41.801+00:00",
    "updated_at":"2022-04-04T05:02:41.801+00:00"
  }
]
```

`user/{username}/topic/groups` - group notifications (new group, group update, group delete) 

*Note: {username} is current user username*

Response body examples.

New group
```json
{
  "type":"ADD",
  "id":"6265858d5999623b23ba77a3",
  "content":
  {
    "id":"6265858d5999623b23ba77a3",
    "admin":"6247a915ed7e4f2466f4298d",
    "users":[
      "6247a915ed7e4f2466f4298d",
      "61227466d7c1f56410265961"
    ],
    "last_message_seeners":[],
    "name":"G10",
    "created_at":"2022-04-24T17:14:53.382+00:00",
    "updated_at":"2022-04-24T17:14:53.382+00:00"
  }
}
```

Group update
```json
{
  "type":"UPDATE",
  "id":"6257a074d4ab34303643eb81",
  "content": {
    "id":"6257a074d4ab34303643eb81",
    "admin":"6247a915ed7e4f2466f4298d",
    "users": [
      "6247a915ed7e4f2466f4298d",
      "61227466d7c1f56410265961"
    ],
    "last_message_seeners": [
      "6247a915ed7e4f2466f4298d"
    ],
    "name":"test14",
    "created_at":"2022-04-14T04:17:56.275+00:00",
    "updated_at":"2022-04-24T17:12:50.252+00:00"
  }
}
```

Group delete
```json
{
  "type":"DELETE",
  "id":"6265858d5999623b23ba77a3",
  "content":null
}
```

`user/{username}/groups/exceptions` - exception messages for group operations (create, update, delete) 

*Note: {username} represents current user username*

Response body example.
```json
{
  "message":"Group with id: '6257a074d4ab34303643ab71' does not exist"
}
```

### Publish

`app/groups/add` - create new group

Request body example.
```json
{
  "name":"G10",
  "users":[
    "6247a915ed7e4f2466f4298d",
    "61227466d7c1f56410265961"
  ]
}
```

`app/groups/delete/{id}` - delete group **|** *{id} represent group id* 

`app/groups/update/{id}` - change name and/or users for group **|** *{id} represent group id*

*Note: Only group admin has access*

Request body example.
```json
{
  "name":"test14",
  "users":[
    "6247a915ed7e4f2466f4298d",
    "61227466d7c1f56410265961",
    "61262ef0fd1b0238349a043d"
  ]
}
```

`app/groups/{id}/remove-me` - remove current user from group **|** *{id} represent group id*

## Messages

### Subscribe

`app/messages/all/{gid}` - all group messages **|** *{gid} represents group id*

Response body example
```json
[
  {
    "id":"626587585999623b23ba77a4",
    "text":"Test message 1",
    "created_at":"2022-04-24T17:22:32.504+00:00",
    "replied_on":null,
    "file_id":null,
    "user":{
      "id":"6247a915ed7e4f2466f4298d",
      "email":"gglbksdyacvmemvqvo@bvhrk.com",
      "username":"test3",
      "activated":true
    }
  },
  {
    "id":"626587665999623b23ba77a5",
    "text":"Message 2",
    "created_at":"2022-04-24T17:22:46.959+00:00",
    "replied_on":null,
    "file_id":null,
    "user":{
      "id":"6247a915ed7e4f2466f4298d",
      "email":"gglbksdyacvmemvqvo@bvhrk.com",
      "username":"test3",
      "activated":true
    }
  },
  {
    "id":"626588415999623b23ba77a6",
    "user_id":"6247a915ed7e4f2466f4298d",
    "text":"Message 3",
    "created_at":"2022-04-24T17:26:25.389+00:00",
    "replied_on":"626587665999623b23ba77a5",
    "file_id":null,
    "user":{
      "id":"6247a915ed7e4f2466f4298d",
      "email":"gglbksdyacvmemvqvo@bvhrk.com",
      "username":"test3",
      "activated":true
    }
  }
]
```

`topic/messages/{gid}` - message related notifications (message create, message delete) **|** *{gid} represents group id*

Response body examples.

New message

```json
{
  "type":"ADD",
  "id":"626588415999623b23ba77a6",
  "content":{
    "id":"626588415999623b23ba77a6",
    "user_id":"6247a915ed7e4f2466f4298d",
    "text":"Message 3",
    "created_at":"2022-04-24T17:26:25.389+00:00",
    "replied_on":"626587665999623b23ba77a5",
    "file_id":null,
    "user": {
      "id":"6247a915ed7e4f2466f4298d",
      "email":"gglbksdyacvmemvqvo@bvhrk.com",
      "username":"test3",
      "activated":true
    }
  }
}
```

Message delete 

Response body example.
```json
{
  "type":"DELETE",
  "id":"626588415999623b23ba77a6",
  "content":null
}
```

`user/{username}/messages/exceptions` - exception messages for message operations (create, delete) 

*Note: {username} represents current user username*

Response body example.
```json
{
  "message":"Group with id '6257a074d4ab34303643ab71' doesn't contain current user"
}
```

### Publish

`app/messages/add/{gid}` - create new group messages for group **|** *{gid} represents group id*

Request body examples.

Text message
```json
{
  "text":"Message 3"
}
```

Reply text message
```json
{
  "text":"Message 3",
  "replied_on":"626587665999623b23ba77a5"
}
```

Message with multimedia file
```json
{
  "file_id":"624260b9fe9374185e21121e" 
}
```

Text message with multimedia file
```json
{
  "text":"Message 3",
  "file_id":"624260b9fe9374185e21121e" 
}
```

Reply text message with multimedia file
```json
{
  "text":"Message 3",
  "replied_on":"626587665999623b23ba77a5",
  "file_id":"624260b9fe9374185e21121e" 
}
```

`app/messages/delete/{gid}/{id}` - delete message **|** *{gid} represents group id* **|** *{id} represents message id*