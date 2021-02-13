## BANKING SYSTEM
### FIRST STEPS
First, you have to grant privileges to your user in the database. Then, you have to execute the banking-system-one script.
To start the server you have to execute this command: **mvn spring-boot:run**. If you want to test the programm, you must execute the banking-system-one-test script.

### Programm Functionalities
In order to get access to the following methods you must authenticate using **Basic Authentication**.

#### GET Methods

***Get:'/account/:id'***

This method return the account corresponding to the id that you introduce.
    
***Get:'/account/balance/:id'***

This method return the account balance corresponding to the id that you introduce. 

#### POST Methods

***Post:'/user/accountholder'***

This method allows you to create a new account holder. Body:
```json
{
    "username": "string",
    "password": "string",
    "name": "string",
    "birth" : "YYYY-MM-DD",
    "primaryStreet" : "string",
    "primaryCity": "string",
    "primaryPostalCode": "string",
    "mailingStreet": "string",
    "mailingCity": "string",
    "mailingPostalCode": "string"
}
```
***Post:'/user/admin'***

This method allows you to create a new admin. Body:
```json
{
    "username": "string",
    "password": "string",
    "name":"string"
}
```
***Post:'/user/thirdparty'***
This method allows you to create a new thirdparty. Body:
```json
{
    "name":"string",
    "hashedKey": "string"
}
```
***Post:'/account/checking'***
This method allows you to create a new checking account. Body:
```json
{
    "balance": 0.0,
    "primaryOwnerId": 0,
    "secondaryOwnerId": 0,
    "secretKey": "string"
}
```
***Post:'/account/creditcard'***
This method allows you to create a new creditCard account. Body:
```json5
{
    "balance": 0.0,
    "primaryOwnerId": 0,
    "creditLimit": 0.0, // Default:100 Max: 100000 | Min: 100
    "interestRate": 0.0 // Default:0.2 Max: 0.2 | Min: 0.1
}
```
***Post:'/account/savings'***
This method allows you to create a new savings account. Body:
```json5
{
    "balance": 0.0,
    "primaryOwnerId": 0,
    "secretKey": "string",
    "minimumBalance": 0.0, // Default:1000 Max:1000 Min:100
    "interestRate": 0.0 // Default:0.0025 Max:0.5 
}
```
***Post:'/transaction'***
This method allows you to do a new transaction to other account or third party . Body:
```json5
{
    "originAccount": 0,
    "destinationAccount": 0, // Fill to send money to another account
    "thirdPartyDestinationId": 0, // Fill to send money to third party
    "destinationOwnerName": "string",
    "quantity": 0.0,
    "currency": "EUR | USD | GBP | ..."
}
```
***Post:'/transaction/thirdparty'***
This method allows thirdparty do transactions to other account. 
You **MUST** include in the request header the field: `HASHED_KEY: value`
```json5
{
    "thirdPartyId": 0,
    "accountId": 0,
    "amount": 0.0,
    "secretKey": "string"
}
```
#### PATCH Methods
***Patch:'account/balance/:id'***
This method allows to the admin update the balance account. Body:
```json5
{
    "amount": 0.0,
    "currency": "EUR | USD | GBP | ..."
}
```
### Author
Silvia Sánchez Heras