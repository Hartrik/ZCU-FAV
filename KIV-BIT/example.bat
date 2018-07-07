call gradle build --no-daemon

java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -e -ECB -k "26CA2B3C1D25971C" -in "example-plaintext.txt" -out "example-ecb-encrypted.txt"
java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -d -ECB -k "26CA2B3C1D25971C" -in "example-ecb-encrypted.txt" -out "example-ecb-decrypted.txt"

java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -e -Parallel-ECB -k "26CA2B3C1D25971C" -in "example-plaintext.txt" -out "example-pecb-encrypted.txt"
java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -d -Parallel-ECB -k "26CA2B3C1D25971C" -in "example-pecb-encrypted.txt" -out "example-pecb-decrypted.txt"

java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -e -CBC -k "26CA2B3C1D25971C" -in "example-plaintext.txt" -out "example-cbc-encrypted.txt" -iv "65C525891DA5872D"
java -jar "build/libs/kiv-bit-sp-1.0.0.jar" -d -CBC -k "26CA2B3C1D25971C" -in "example-cbc-encrypted.txt" -out "example-cbc-decrypted.txt" -iv "65C525891DA5872D"
