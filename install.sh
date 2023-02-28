rm -rf target
mvn install
rm ~/.m2/repository/dev/mv/mvengine/1.0/mvengine-1.0.pom
cp installed.xml ~/.m2/repository/dev/mv/mvengine/1.0/mvengine-1.0.pom