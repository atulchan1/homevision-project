This project calls a rest service and downloads and persists the home images of the first 100 homes returned by the service. The calls
are paginated and 10 calls are made to download the images. There is also an option to pass the start and end pages as command line arguments to download
images for other page ranges.

Pre-requistites:
Ensure jdk 17 is installed 


How to run:

cd project
./gradlew bootRun

To specify different page ranges:
cd project
./gradlew bootRun --args '1 2'

In this case only 20 images from the first 2 pages will be downloaded.

You can also run  the app  as below
cd project
./gradlew clean build
java  -jar build/libs/homevision-project-0.0.1-SNAPSHOT.jar 

This can be also containarized by adding a Dockerfile. May need to change the System.exit() command and keet it running to check the images.

