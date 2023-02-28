rm -rf target
mvn install
rm ~/.m2/repository/dev/mv/engine/1.0/engine-1.0.pom
cp installed.xml ~/.m2/repository/dev/mv/engine/1.0/engine-1.0.pom