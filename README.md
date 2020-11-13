# bank-customers

A bank that uses Datomic as a database to store customers.

## Usage

Run database (in datomic folder with bank-customers db created on port 4334):
`bin/transactor config/dev-transactor-template.properties`

Run app locally (given that the transactor is running): 
`lein run`

## Available endpoints

### /customers -  get tax-id of customers in database (GET) 
Usage: `curl -X GET http://localhost:4000/customers`

Expected response (happy path):
```
{
  "tax-ids": [
    "12345655590",
    "12345678911",
    "12345677511"
  ]
}
```

### /customer?tax-id=(number) - get data from a specific customer tax-id (GET)
Usage: `curl -X GET http://localhost:4000/customer?tax-id=12345678910`

Expected response (happy path):
```
{
  "customer": {
    "name": "John Smith",
    "email": "john@gmail.com",
    "tax-id": "12345678911"
  },
  "result": "is-customer"
}
```

### /addcustomer - add a customer to database (POST)
Usage: `curl -X POST -H "Content-Type: application/json" -d @./json/customer.json http://localhost:4000/addcustomer`
(expects a JSON payload containing name, email and tax-id of customer to be added)

Expected response (happy path): 
```
{
  "customer": {
    "name": "Peter Parker",
    "email": "peter@gmail.com",
    "tax-id": "12345655590"
  },
  "result": "customer-added-to-db"
}
```