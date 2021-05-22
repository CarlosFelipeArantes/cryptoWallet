# CrypoWallet Challenge

This repository concerns with the development of a challenge.

For a quick briefing, parallel processing of assets was used.
No more than three requests can be made in parallel to the API; this was ensured using a FixedThreadPool.
Also, sensible operations that required synchronized operations where used only synchronized methods and blocks.


## Installation

* Should be pre-installed:
  * Java 11 or up
  * Maven or Maven supported IDE, like IntelliJ and more.
  
* Running the program using Maven
  
  Commands
   * mvn clean install
   * mvn exec:java

## Libraries Used

* Mockito (for testing)
* Jackson (for object mapping)
* Apache Commons CSV (for reading .CSV files)
* Apache Log4j2 (for logging)
* Lmax Disruptor (for parallel logging)
* JUnit (testing framework)
* Other libraries for building
