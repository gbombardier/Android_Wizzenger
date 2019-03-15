#!/bin/bash

#Actions à faire lors du build (builder l'app et le test)


echo 'Building and testing..'
chmod +x ./gradlew
script jenkinsLog.txt
# ./gradlew connectedAndroidTest 
./gradlew connectedAndroidTest

#Si failure, on sort du jenkins
wait
exit 0

#Si fonctionne, on build
#./gradlew assembleDebug


  

#Si on voulait installer lapp pour lutiliser et non tester

#phone1=1215fcf92c900304
#phone2=05157df5bd0d0412

#./gradlew assembleDebug

#/usr/android-sdk-linux/platform-tools/adb -s $phone1 install -r  /var/lib/jenkins/workspace/Wizzenger_Pipeline/app/build/outputs/apk/debug/app-debug.apk
#/usr/android-sdk-linux/platform-tools/adb -s $phone2 install -r  /var/lib/jenkins/workspace/Wizzenger_Pipeline/app/build/outputs/apk/debug/app-debug.apk

