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

