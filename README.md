This project uses RESTful APIs for money transfers between accounts.

- Servlet-container : Jetty
- REST framework : Jersey
- In-memory database : Debry
- Build System: Maven

Follow the below steps to test the project:

- mvn clean install
- mvn package
- java -jar target/revolut-1.0-SNAPSHOT.jar

Testing the code:
The test code has been written. However, one can use REST clients like Postman to test the code manually.
Here are a sample test run for this application:

- POST method
URL: http://localhost:8080/bankAccounts
	
		{
			"BankAccount":
				{	
					"number":123,
   			       "balance":1000
              }
      	}
                
                
Response received: 
	
		{
        	"BankAccount": 
        		{
                  "id": 1,
                  "number": 123,
                  "balance": "1000"
          	}
       }
      
      
- POST method
URL: http://localhost:8080/bankAccounts
	
		{
			"BankAccount":
				{
					"number":125,
          	   	"balance":1000
          	}
    	}
    
Response received: 

		{
        	"BankAccount": 
        		{
                 "id": 2,
                 "number": 125,
                 "balance": "1000"
             }
       }
       
- Testing the above two bank accounts:

	- GET       http://localhost:8080/bankAccounts/1
	- GET       http://localhost:8080/bankAccounts/2

- Make a payment from one account to another

POST method URL: http://localhost:8080/payments

	{
   		"Payment":
   			{
	   			"withdrawalBankAccountNumber": 123,
				"depositBankAccountNumber": 125,
	   			"funds": "500"
   			}
	}
	
Response received:

	{
   		 "Payment": 
			{
				"id": 1,
				"withdrawalBankAccountNumber": 123,
				"depositBankAccountNumber": 125,
				"funds": 500,
				"date": "2019-08-11T20:52:38.468+05:30"
			}	
	}

- Test out if the withdrawal and deposit has actually taken place correctly

GET       http://localhost:8080/bankAccounts/1
		
		{
			"BankAccount":
				{
					"id":1,
					"number":123,
					"balance":"500.0000"
				}
		}

GET       http://localhost:8080/bankAccounts/2

	{
		"BankAccount":
			{	
				"id":2,
				"number":125,
				"balance":"1500.0000"
			}
	}


