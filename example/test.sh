cd .. && mvn clean install && cd example/
java -jar ../target/ilrfilter-*.jar hash -S hsa -I ../src/test/resources/reads_R1.fastq ../src/test/resources/reads_R2.fastq -O ./out_test