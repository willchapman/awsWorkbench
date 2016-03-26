# awsWorkbench
The goal of this project is to provide a workbench type tool for AWS.  
This is for personal uses, but would welcome feedback/contributions if 
anyone is interested.

### Build
I did not include the project files for IntelliJ IDEA 2016.1 (current IDE).
But I will eventually incorporate a Maven build into the project.

### Libraries
Right now the only library requirements are the AWS SDK (and its associated
requirements).  Using version 1.10.62.

### Notice
This is still in active development, and almost certainly contains bugs. 
You use this at your own risk.

### Credentials
We are using the default provider chain for with 1 additional modification.
We are looking for a "s3workbench" profile first, then fallback to the
default provider chain.