Taskr is our CS130 group project.  It is Uber for tasks.

=========================================================================
REST REVIEW

show function in review_controller.rb
Get a comment, rating for specific request id and both parties who review and being reviewed id
@param {} 
@returns {JSON}
JSON with a review object with the most recently review first
Each review object contains
id: review objectId
reviewer_id: id of people who review
reviewee_id: id of people being reviewed
request_id: id of the request being reviewed
rating: reviewee's rating
comment: additional info from reviewer to reviewee

Usage examples:

Endpoint: Get api/v1/reviews/{reviewee_id}

Request body: {"id":1,"reviewer_id":2,"reviewee_id":1,"request_id":1,"rating":5,"created_at":"2016-11-23T21:49:59.087-08:00","updated_at":"2016-11-23T21:49:59.087-08:00","comment":"Test comment"}


create function in review_controller.rb
Create a review for the reviewee including the rating and comment.
@param{}
@return {JSON}
Json with the id of new review creation
Each review object contains
id: review objectId
reviewer_id: id of people who review
reviewee_id: id of people being reviewed
request_id: id of the request being reviewed
rating: reviewee's rating
comment: additional info from reviewer to reviewee

Usage examples:

Endpoint: Post api/v1/reviews

Request body: 

=========================================================================
REST TRANSACTION

show function in transaction_controller.rb
Get a transaction for specific payer_id which has a user class
@param {}
@returns {JSON}
JSON with a transaction object with the most recently transaction first
Each transaction object contains
id: transaction objectID
payer_id: the id of the payer
payee_id: the id of the payee
amount: the number that the payer pay payee
request_id: id of the request being payed

Usage examples:

Endpoint: Get api/v1/transaction/{id}

Request body: 


create function in transaction_controller.rb
Create a transaction for specific payer_id which has a user class, make a charge to the payer card number through Stripe API
@param{}
@returns {JSON}
Json with the token being used to create new trasaction object
Each transaction object contains
id: transaction objectID
payer_id: the id of the payer
payee_id: the id of the payee
amount: the number that the payer pay payee
request_id: id of the request being payed

Usage examples

Endpoint: Post api/v1/transaction/{id}

Request body:

=========================================================================
REST REQUEST

nearby function in request_controller.rb
Show all nearby open requests
@param{}
@returns{JSON}
//need info
JSON for the request object
Each request object contains
Endpoint: 
Request body:
```
{
	"id": 1,
	"title": "bringing out the trash",
	"user_id": 2,
	"amount": 100,
	"lat": 34.0688,
	"longitude": -118.4453,
	"due": "2017-09-01T12:00:00.000-07:00",
	"description": null,
	"created_at": "2016-11-24T15:26:39.819-08:00",
	"updated_at": "2016-11-24T15:26:39.819-08:00",
	"status": "open",
	"actor_id": null
}
```

create function in request_controller.rb
Create a new request
@params{}
@return{JSON}
Json with the id of the new request object

Usage examples

Endpoint: Post api/v1/request/{id}

Request body:

//need info
accept function in request_controller.rb
accepting the request and push the notification to the client with the status accept
@param{}
@return{}
raise error if there is an error

Usage examples

Endpoint: 

Request body:


//need info
reject function in request_controller.rb
rejecting the request and push the notification to the client with the status reject
@param{}
@return{}
raise error if there is an error

Usage examples

Endpoint: 

Request body:

//need info
complete function in request_controller.rb
mark completing the request and push the notification to the client with the status complete
@param{}
@return{}
raise error if there is an error

Usage examples

Endpoint: 

Request body:

//need info
pay function in request_controller.rb

Usage examples

Endpoint: 

Request body:


//need info
cancel function in request_controller.rb
canceling the request and push the notification to the client with the status cancel
@param{}
@return{}
raise error if there is an error

Usage examples

Endpoint: 

Request body:

//need info
findByUid function in request_controller.rb
find request by user_id
@param{}
@return{}

//need info
show function in request_controller.rb

Usage examples

Endpoint: 

Request body:


//need info
edit function in request_controller.rb
editing the request associate with specific certain request id
@param{}

Usage examples

Endpoint: 

Request body:


=========================================================================
REST DEVICE

show function in device_controller.rb
Get a device with specific user_id associate with the device
@params{}
@returns{JSON}
JSON for the Device object
Each device object contains
registration_id: the id obtained from gcm
device_type: the type of device being used by the user: android/ios
user_id: the id of user being associated with the device

Usage Examples

Endpoint: Get api/v1/gcm/{user_id}

Request body:


create function in device_controller.rb
Create a device with specific user_id associate with the device
@params{}
@returns{JSON}
JSON for the Device object
Each device object contains
registration_id: the id obtained from gcm
device_type: the type of device being used by the user: android/ios
user_id: the id of user being associated with the device

Usage Examples

Endpoint: POST api/v1/gcm

Request body:

notify function in device_controller.rb
notify all the devices associate with the users through gcm server
@params{data, collapse_key}
data = array of json
collapse_key = content of data
@returns{}

Usage Examples

Endpoint: POST api/v1/{id}/{device_id}

Request Body:

=========================================================================
REST PROFILE

//need info
login function in profile_controller.rb
Login through fb


show function in profile_controller.rb
get specific profile that associate with {user_id}
@params{}
@returns{JSON}
JSON of the profile object contains
//need info

Usage Examples

Endpoint: GET api/v1/{id}

Request body:







