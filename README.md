ZapposSoftwareEngineeringChallenge
==================================

This is the software engineering challenge project

In this project I connect to the Zappos API and fetch some data using their API Key.
The aim of this project is to take two user inputs 1. number of gifts 2. total cost of all the gifts
Depending on these two inputs I make calls to the Zappos API and calculate the best possible Zappos
products(equal to the number of gifts) that sum up to the total cost (suggested by the user).

In this project I try to optimmize the data going into the actual computation code and try to make 
very least web API calls to Zappos. Also the code to calculate the combinations of gifts is optimized to 
perform only the necessary checks.

I give out products whose prices are roughly equal to each other and still they add up to the total target
cost. This is one way I thought I could optimize the input data from Zappos API.

I tested the code for the number of gifts as 3 and total target price as 150 dollars and I got 363 unique
products (unique product keys) lists within 4.16 minutes.
I have attached the output file too.
