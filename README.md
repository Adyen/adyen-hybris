# Adyen Payment plugin for Hybris 5.5
	
Hybris is a Java based eCommerce framework built on the Spring MVC framework. The purpose of providing a plugin for Adyen is to aid integration of the Adyen payment gateway into a hybris implementation. Because Hybris is built on the Spring framework this makes it highly customizable and extensible. The plugin also utilizes this framework so can also easily be extended to add specific behavior if required.
  
## Requirements
  
This extension support version 5.5 of Hybris.
Hybris 5.1 is running on Java 8.
  
## Installation
  
1. Download the 2.0.0 release
2. Unzip the file to the ${HYBRIS_HOME} directory.
3. Re-build Hybris by running “ant clean all” command from ${HYBRIS_HOME}/bin/platform.
4. Start Hybris server by running “hybrisserver.sh” from ${HYBRIS_HOME}/bin/platform.
5. Initialize the platform from HAC. 
  * Full initialization will remove all data. If this is not acceptable an update can be done instead. However, there is data loaded during the initialization/update that is required for the integration to function correctly.
  
## Usage & Documentation
  
This is described in the documentation inside the document folder of this repository
  
## Licence
  
Mention the relevant license (MIT).