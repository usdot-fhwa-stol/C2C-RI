C2C-RI Release Notes
----------------------------

Version 1.0.1, released on Feb 2nd, 2024
----------------------------------------

### **Summary**
Center-To-Center Reference Implementation (C2C RI) Tool release version 1.0.0 was
to address some of the SonarQube static code analysis, which identifies a list of issues
that may not comply with coding standard or practice. SonarQube provides a detailed
report of bugs, vulnerabilities, and code smells. In addition, minor application issues
were addressed in this release.

Fixes in this release:
- All bugs regardless of severity. These are coding mistakes that can lead to an
error or unexpected behavior at runtime.
- All vulnerabilities regardless of severity. These are points in the code that may be
open to attack.
- Code smells that have a severity of blocker and critical. These are maintainability
issues that makes code confusing and difficult to maintain.
- Fixed Entity Emulation lookup values which were missing in TMDDv303c and
TMDDv303d databases for Link, Node, and Center Active Verification entities.
- Fixed lookup values for entity-id which were incorrect in the TMDDv303c and
TMDDv303d databases.
- Fixed invalid configuration files related to test cases expecting specific
configuration parameters that caused test cases to fail.
- Fixed log files on disk are not being populated in selection dialog for reports.
- Fixed library used to parse/write XML files does not recognize the suggested
security properties.
