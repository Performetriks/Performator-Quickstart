pipeline{
	agent {
			label 'builtin'
	}
	parameters {
		//---------------------------------
		// Load Percent
		choice(name: 'LoadPercent', choices: ['10', '30','50', '100', '150', '200'],
			description:'''
				<!DOCTYPE html>
				<html>
					<head>
						<h1>Following load can be simulated, please choose one:</h1>
					</head>
					<body>
						<ul>
							<li><b>10%</b> -> 100 User,  1000 transactions/hour</li>
							<li><b>25%</b> -> AverageLoad 250 User,  2500 transactions/hour</li>
							<li><b>50%</b> -> AverageLoad 500 User,  5000 transactions/hour</li>
							<li><b>100%</b> -> AverageLoad 1000 User, 10000 transactions/hour</li>
							<li><b>150%</b> -> AverageLoad 1500 User, 15000 transactions/hour</li>
							<li><b>200%</b> -> AverageLoad 2000 User, 20000 transactions/hour</li>
						</ul>
					</body>
				</html>
			''')

		//---------------------------------
		// Environment
		choice(
			  name: 'Environment'
			, choices: ['DEV', 'TEST']
			, description:''' '''
		)
		
		//---------------------------------
		// Test Duration Minutes
		string(
			  name: 'Duration'
			, defaultValue: '10'
			, description: 'Test duration in minutes'
		)
				
		//---------------------------------
		// Log Level
		choice(
			  name: 'LogLevel'
			, choices: ['INFO', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL', 'OFF']
			, description:'Level of the root logger'
		)

   }

	stages{

		stage("Build Maven"){
			steps{
				bat "mvn -B clean package -DskipTests"
			}
		}

		stage("Run Performator Test"){
			steps{
				echo "LoadPercent: ${params.LoadPercent}"
				echo "Environment: ${params.Environment}"
				echo "Duration: ${params.Duration}"
				echo "LogLevel: ${params.LogLevel}"
				
				// e.g. mvn verify -Dpfr_test=com.performetriks.performator.quickstart.tests.basics.PFRTestJenkins -DLoadPercent=33 -DEnvironment=DEV -Duration=5 -DLogLevel=INFO
				bat "mvn verify -Dpfr_test=com.performetriks.performator.quickstart.tests.basics.PFRTestJenkins -DLoadPercent=${params.LoadPercent} -DEnvironment=${params.Environment} -DDuration=${params.Duration} -DLogLevel=${params.LogLevel}"
			}
		}
		

		stage("Report") {
			steps{
				
				archiveArtifacts (
					artifacts: 'target/report.zip',
					fingerprint: true
				)
				
				publishHTML (
					target : [
						allowMissing: false,
						alwaysLinkToLastBuild: true,
						keepAll: true,
						reportDir: 'target/report/HTMLReport',
						reportFiles: 'report.html',
						reportName: 'PerformatorReport',
						reportTitles: 'PerformatorReport'
					]
				 )
			}
		}
	}
}


