#!/bin/bash

#Creer logErrors.txt si ce n'est pas fait.
if [ ! -e logErrors.txt ]
then
    sudo touch logErrors.txt
    sudo chmod 777 logErrors.txt
    sudo chmod 777 gradlew
    sudo chmod 777 ./app
fi

#-------------- Builder et Tester App ----------------
#/usr/android-sdk-linux/platform-tools/adb uninstall com.bombardier_gabriel.wizzenger.test
./gradlew connectedAndroidTest | tee logErrors.txt
wait

#Si un test échoue, la ligne suivante est printée : Task :app:connectedDebugAndroidTest FAILED
result="$(grep 'Task :app:connectedDebugAndroidTest FAILED' logErrors.txt)" 
echo "Le resultat est: $result"

if [ "$result" != "" ]
then
  #Si failure, on sort du jenkins
  echo "Test failure, not building new apk."
  exit 0
else
  #Si fonctionne, on build
  echo "Tests succeeded, building new apk."
  ./gradlew assembleDebug
fi
