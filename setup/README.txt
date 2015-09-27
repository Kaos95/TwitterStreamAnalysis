Setting up the project:
	
	1. Environment setup:
		The /environment directory contains a script that will auto-
		install the version of Apache Maven and Apache Spark that were
		used to successfully run the program. To install both Spark
		and Maven, run these commands in the /environment directory:
			
			sudo ./environment.sh --install 
			source /etc/profile

			or

			sudo ./environment.sh -i
			source /etc/profile
		
		These commands will install BOTH Spark and Maven. If you already
		have either one in your PATH variable issues may arise due to 
		duplication binaries with the same name. I'd recommend removing
		the version you currently have from the PATH and running the 
		environment.sh --install or running environment.sh --download
		to download the tar files and manually install the software.
		IMPORTANT: If you use the --download option, make sure you don't
		commit the project to your git branch without removing the tar
		files. Git has a 100 MB push limit and it can be difficult to 
		get it to forget that commit leading to issues pushing your
		changes. After installation, use the following to verify the 
		installation: 

			spark-submit

			and
	
			mvn -version

		Both should print text to the screen indicating that some form
		of binary was found. spark-submit will print out usage inform-
		ation and mvn is self-explanatory. If these commands respond 
		successfully, the environment is setup as I had it when test-
		ing the project. I've included an uninstall option in the env-
		ironment setup script, but that's crude. After running it, the
		PATH modifications need to be manually removed from the 
		/etc/profile file and your terminal restarted to see those 
		changes. The necessary modifications are printed in a text file
		created by the uninstall process in the directory in which
		the environment.sh script resides. There is also -d or 
		--download option that allows you to simply download the 
		related programs and install them manually. If this is done, 
		the uninstall function won't be applicable because your 
		directory structure will probably be different. ASSUME IT WILL 
		NOT UNINSTALL CORRECTLY.

	2. Compiling, Building and Running the Project:
		When building a maven project, you need to make sure you're
		in the directory with the pom.xml file (the project root 
		directory). The relative pathname is:
		
			<our class repo name>/phase1
		
		From this point forward, I'll refer to our repo base dir as *.
		When in that directory run the following Maven command to down-
		load the dependencies, clean the project target directory (MAKE
		SURE YOU DON'T HAVE ANYTHING IMPORTANT IN THE */phase1/target 
		DIRECTORY WHEN YOU RUN THIS COMMAND -- IT'LL BE DELETED BY THE 
		CLEAN COMMAND !!!), compile the project, build it and finally
		assembly the project jar file containing all of the dependen-
		cies needed to run the project. This command is:

			mvn clean compile assembly:single

		Remember to run this in the dir with the pom.xml otherwise the
		project won't be detected and you'll get an error message. 
		After running this command, the */phase1/target directory will
		be created if it wasn't already and will be populated with 
		a couple of dirs and the project jar file. At this point, the
		project can be run by using the following command:

			spark-submit 
				--class cs5540.bigdata.project.SparkStreamConsumer
				--master local[2]
				<jar file name in */phase1/target dir>

		NOTE: The command above needs to be written on one line and is
		one command.

