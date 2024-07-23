C2C-RI Release Notes
----------------------------

Version 1.0.1, released on Feb 14th, 2024
----------------------------------------

### **Summary**
Center-To-Center Reference Implementation (C2C RI) Tool release version 1.0.1 was to address some of the results of the SonarQube static code analysis performed on release version 1.0.0.  The SonarQube static code analysis identified a list of issues that may not comply with coding standards or best practices. SonarQube provides a detailed
report of bugs, vulnerabilities, and code smells. In addition, minor application issues
were addressed in this release.

### **Fixes in This Release**

**<ins>Fixes related to SonarQube static analysis:</ins>**
- All bugs regardless of severity. These are coding mistakes that can lead to an
error or unexpected behavior at runtime.
- All vulnerabilities regardless of severity. These are points in the code that may be
open to attack.
- Code smells that have a severity of blocker and critical. These are maintainability
issues that makes code confusing and difficult to maintain.

**<ins>Fixes related to the application:</ins>**
- Fixed Entity Emulation lookup values which were missing in TMDDv303c and
TMDDv303d databases for Link, Node, and Center Active Verification entities.
- Fixed lookup values for entity-id which were incorrect in the TMDDv303c and
TMDDv303d databases.
- Fixed invalid configuration files related to test cases expecting specific
configuration parameters that caused test cases to fail.
- Fixed bug causing log files to be written incorrectly to disk.

Version 1.1.0, released on July 23rd, 2024
----------------------------------------

### **Summary**
Center-To-Center Reference Implementation (C2C RI) Tool release version 1.1.0 focuses on updating the logging framework to the current version of Log4j2 and simplifying the logging configuration. This release includes updating all instances of Log4j and SLF4j usage, ensuring consistent and efficient logging across the application. Along with these enhancements and fixes, we verified the code quality of our source code by executing SonarQube analysis to ensure no new issues are introduced.

**<ins>Enhancements related to this release:</ins>**
- Updated imports to use the current version of Log4j2 (2.23.1).
- Simplified and consolidated Log4j configuration for improved maintainability and readability.
- Implemented analogous or new APIs to update all instances of Log4j and SLF4j usage, ensuring consistent logging accoss application.
- Added Plugin code to use custom layout in configuration file.
- Updated classpath for new jar's and removed references to old log4j version in all individual projects directories of application. 

**<ins>Fixes related to the application:</ins>**
- Fixed the Log4j version, which was still 1.2, the same as the old version.
- Fixed new log event issues where the locationInfo tag had a trailing $1 and some new log event methods had a less than sign (<).
- Fixed <rawOTWMessage> not appearing in the output log file compared to the previous release.
