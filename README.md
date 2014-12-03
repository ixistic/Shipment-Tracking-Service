# Shipment Tracking Service

Shipment tracking system is used for the shipping and the observing of product order on the move that can manage order on the network.

## Features
* Online Shipment
* Online Track Shipment
* Android Application
* Web Application
* OAuth Authentication

## User Story
*   Customer
    * Reciever : ( Android Application )
        -  <i>Authenticate Not Required</i>
        ```
          I can check shipment status using shipment number.
        ```
    * Courier : ( Android Application )
        - <i>Authenticate Required</i>
        ```
          I can create shipment.
          I can check all the shipments that I create.
          I can cancel shipment.
        ```
        - <i>Authenticate Not Required</i>
        ```
          I can check shipment status using shipment number.
          I can check the shipment's cost by sending item information.
        ```
* Delivery Person : ( Web Application ) 
    -   <i>Authenticate Required</i>
    ```
      I can edit shipment's status.
    ```


## Use Cases
####Use Case Number : 1
<b>Use Case Name</b> : Reciever and  Courier check shipment's status.<br>
<b>Main Success Scenario</b> : A reciever and courier check shipment's status by using number of shipment after they sign in application with their ID. Then the application shows the shipment's status.<br>
<b>Exception</b> : They didn't fill shipment's number in form or shipment's number doesn't exist. The application will notify them.

####Use Case Number : 2
<b>Use Case Name</b> : Courier and Delivery person check all shipments.<br>
<b>Main Success Scenario</b> : A courier and deliver person sign in and they can see all shipment after courier created the shipment.

####Use Case Number : 3
<b>Use Case Name</b> : Courier create the shipment.<br>
<b>Main Success Scenario</b> : A courier sign in and create shipment with completed information. After that the application calculate shipment's cost and show shipment's detail with status.<br>
<b>Exception</b> : Courier fill an imperfect information then the application notify him.

####Use Case Number : 4
<b>Use Case Name</b> : Courier cancel the shipment.<br>
<b>Main Success Scenario</b> : A courier sign in and decided to cancel the shipment. Then he cancel and conform so the application notify a successful cancellation.

####Use Case Number : 5
<b>Use Case Name</b> : Courier check the shipment's cost.<br>
<b>Main Success Scenario</b> : When a courier fill a completed information to check the shipment's cost and submit it ,so the shipment's cost will be shown.<br>
<b>Exception</b> : A courier check the shipment's cost with incomplete information then the application notify him.

####Use Case Number : 6
<b>Use Case Name</b> : Delivery person edit shipment's status.<br>
<b>Main Success Scenario</b> : A delivery person change the shipment's status after he open the web site. Then the status will be updated ,and application notify the delivery person.

## Functions
* <b>OAuth Request</b>
  ```
  GET /shipments/auth 
  GET /shipments/oauth2callback
  ```

* <b>Customer</b>

    - <i>Authenticate Not Required</i>
    ```
  	POST /shipments/calculate 
    ```
	* <b>Reciever</b>

    - <i>Authenticate Not Required</i>
    ```
    GET /shipments/{id}/status
    ```
    Example : [Demo](http://track-trace.tk:8080/shipments/2/status)
	* <b>Courier</b>

    - <i>Authenticate Required</i>
    ```
    GET /shipments/{id}
    GET /shipments 
    POST /shipments
    DELETE /shipments/{id}
    ```
* <b>Delivery Person</b>

    - <i>Authenticate Required</i>
    ```
    GET /shipments/{id}
    GET /shipments 
    ```
    - <i>Authenticate Required (user's type : 1)</i>
    ```
    PUT /shipments/{id} 
    ```

## API Specification Document

* See it in [API wiki](https://github.com/ixistic/Shipment-Tracking-Service/wiki/API-Specification-Document)

## Software Design
* Sequence Diagram (Web client) : [Link](https://docs.google.com/drawings/d/1c0_B0CL5km4ttUjANvumr18zXrdh5E2BGpbB5BKmH_8/edit)

## Installation 
* <i>comming soon</i>

## Requirements

* JavaSE 1.7
* Jersey
* Jetty
* Jpa
* Json
* Mysql
* OAuth
* Android SDK

## Other Documents

* [BDD](https://github.com/ixistic/Shipment-Tracking-Service/wiki/Behavior-Driven-Development) - Behavior-Driven Development


## Members

- Suwijak Chaipipat (5510545046) - Github: [Aunsuwijak](https://github.com/aunsuwijak)
- Veerapat Threeravipark (5510547022) - Github: [iXisTiC](https://github.com/ixistic)
- Juthamas Utamaphethai (5510546964) - Github: [PeterpanHiHi](https://github.com/peterpanhihi)

