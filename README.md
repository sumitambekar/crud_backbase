# crud-tests
Project description:
**Automation test project for regression testing of "Computer database sample application".**    
Application location: https://computer-database.herokuapp.com/     
1. Java 8 + Maven + TestNG + Selenium + Selenide.  
2. TestNG suite location: \src\main\resources\testng\testng.xml  
3. By a default suite will be executed in 5 parallel threads > could be changed in testng.xml, property <thread-count>  
4. By default tests will be started on Chrome browser (chromedriver provided in project resources)   

How to start test execution in IDE:  
1. To run full test suite: make right click on testng.xml file > Run with testNG  
2. To run separate package/class: make right click on package/class file > Run with testNG   
