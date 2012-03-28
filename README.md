# ListMaker

A fluent interface list maker for use with Guava.

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform
	
Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *net.gageot-XXX* repository then release it.
Synchro to Maven Central is done hourly.