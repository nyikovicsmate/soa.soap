# Ticketing Service

 Simple cinema ticketing service implemented as a web servcie.
 (*homework for BME's Service Oriented System Integration course - 2018*)

## 1. Requirements
JDK 1.8

WildFly 10.1.0.

## 2. Functions

- *Init*
  - parameters: row, col
  - Initializes a cinema with 'row' long 'col' wide seats. 
- *GetAllSeats*
  - parameters: -
  - Returns the unique id's (seatId) of each seat in the cinema.
- *GetSeatStatus*
  - parameters: seatId
  - Returns the status of the seat identified by it's 'seatId' parameter.
- *Lock*
  - parameters: row, col
  - Locks the seat at position (row, col). 
- *Unlock*
  - parameters: seatId
  - Unlocks the seat.
- *Reserve*
  - parameters: seatId
  - Reserves a seat.
- *Buy*
  - parameters: seatId
  - Buys a seat.

## 3. Usage

### 1. Start the WildFly server

`<wildfy directory>\bin\standalone.bat`

### 2. Deploy the service

`..\WebService> mvn wildfly:deploy`

### 3. Issue the commands by running the client

`..\WebService> mvn exec:java -Dexec.mainClass=cinema.Program -Dexec.args="http://localhost:8080/WebService/Cinema <COMMAND>"`

where COMMAND syntax is: `<row> <col> <function>`

e.g. 

Initialize cinema

`..\WebService> mvn exec:java -Dexec.mainClass=cinema.Program -Dexec.args="http://localhost:8080/WebService/Cinema 20 20 Init"`

Reserve a seat in the first row

`..\WebService> mvn exec:java -Dexec.mainClass=cinema.Program -Dexec.args="http://localhost:8080/WebService/Cinema A 6 Reserve"`

Get the status of the newly reserved setat

`..\WebService> mvn exec:java -Dexec.mainClass=cinema.Program -Dexec.args="http://localhost:8080/WebService/Cinema A 6 GetSeatStatus"`

