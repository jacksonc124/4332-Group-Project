
#!/bin/bash
cd 4332_Library
javac -d target/classes src/main/java/delft/*.java
java -cp target/classes delft.LibInterface
cd ..