all: 
	javac -d ./classes/ src/ru/aptu/dissection_generator/*.java
	jar cfe dissection_generator.jar ru.aptu.dissection_generator.Main -C classes/ .
clean:
	rm -rf ./classes/*
	rm ./dissection_generator.jar