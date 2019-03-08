node {
	stage('Update'){
		checkout([$class: 'GitSCM', branches: [[name: '*/master']],
     		userRemoteConfigs: [[url: 'https://github.com/gbombardier/Android_Wizzenger.git']]])	
	}
	
    	stage('Build') {
		//sh 'make' 
		bat 'gradlew.bat assembleDebug'
	}
       
	stage('UI Test') {
        	echo 'Testing..' 
		bat 'emulator -avd Pixel_2_API_28'
		bat 'gradlew.bat test'
    	}
	
	//stage('Archivage') {
        	//archiveArtifacts artifacts: 'app/build/outputs/apk/*', fingerprint: true    
    	//}
}
