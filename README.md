```
 _______    ______   _______  ________   ______   ________   ______    ______   __        ______   ______  
|       \  /      \ |       \|        \ /      \ |        \ /      \  /      \ |  \      |      \ /      \ 
| $$$$$$$\|  $$$$$$\| $$$$$$$\\$$$$$$$$|  $$$$$$\| $$$$$$$$|  $$$$$$\|  $$$$$$\| $$       \$$$$$$|  $$$$$$\
| $$__/ $$| $$  | $$| $$__| $$  | $$   | $$__| $$| $$__    | $$  | $$| $$ __\$$| $$        | $$  | $$  | $$
| $$    $$| $$  | $$| $$    $$  | $$   | $$    $$| $$  \   | $$  | $$| $$|    \| $$        | $$  | $$  | $$
| $$$$$$$ | $$  | $$| $$$$$$$\  | $$   | $$$$$$$$| $$$$$   | $$  | $$| $$ \$$$$| $$        | $$  | $$  | $$
| $$      | $$__/ $$| $$  | $$  | $$   | $$  | $$| $$      | $$__/ $$| $$__| $$| $$_____  _| $$_ | $$__/ $$
| $$       \$$    $$| $$  | $$  | $$   | $$  | $$| $$       \$$    $$ \$$    $$| $$     \|   $$ \ \$$    $$
 \$$        \$$$$$$  \$$   \$$   \$$    \$$   \$$ \$$        \$$$$$$   \$$$$$$  \$$$$$$$$ \$$$$$$  \$$$$$$ 
                                                                                                           
                                                                                                           
                                                                                                           
Wallet service at YOUR service
```
Wallet service to track customers account balance

## Prerequisites
* Java 17
* Docker (for database)

## Start development
* Run dev environment (sets up database in docker):
  * `./dev-env.sh up`
  * `./dev-env.sh down`
  
## Run tests
#### 1. Running tests
* Run dev environment:
  * `./dev-env.sh up`
* Run tests:
  * `gradle test`

#### 2. Testing the app locally
* Make sure dev environment is up: `./dev-env.sh up`
* This starts:
  * Portafoglio database

* Accessing the local database: (So you can keep track of the transactions)
  * in terminal window:
    * `mysql -h 127.0.0.1 -u portafoglio -p`
    * password is `portapass` and database is `portafoglio`


* To test account creation (so you can after that test transactions) run app with:
  * `gradle bootrun`
* Then in new terminal window:
  * `curl -X POST -k https://localhost:8443/api/v1/customer -H 'Content-Type: application/json' -d '{"customerId": "12346", "name":"Bob", "balance": 50.0}'`
* This will create a new account for customer named Bob that has balance of 50.1 readily available (of course you can try and create other accounts too, but keep in mind that customerId's are unique)
* After this you may try the transactions API:
  * `curl -X POST -k https://localhost:8443/api/v1/transaction/WIN -H 'Content-Type: application/json' -d '{"eventId":3,"customerId" :"12346", "amount":"130.0"}'`
  * `curl -X POST -k https://localhost:8443/api/v1/transaction/PURCHASE -H 'Content-Type: application/json' -d '{"eventId":4,"customerId" :"12346", "amount":"130.0"}'`
* These API's will send a WIN and PURCHASE events to the wallet service which in turn will record the changed balance to the
  corresponding account and responds the new balance to the caller.

#### COUPLE OF CAVEATS (Errors and such)
* When creating accounts, customer id's and for creating events event id's need to be unique everytime.
* If you try to call the transaction API with PURCHASE larger than the account balance, it will respond 400 NOT_ENOUGH_BALANCE, since the account cannot be on a negative balance
* Also if you try to call it with a customer id that isnt found in the database (returns empty customer) you will receive 404 CUSTOMER_NOT_FOUND errorcode
* This HTTPS setup uses a self-signed certificate for local development. In a production environment, it should be replaced with a trusted CA-signed certificate.
