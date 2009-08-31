assert new File(basedir, 'target/copy-file/pom.properties').exists();
assert new File(basedir, 'target/copy-file/pom.with.xml.extension').exists();

assert new File(basedir, 'target/copy-fileset/META-INF/MANIFEST.MF').exists();
assert new File(basedir, 'target/copy-fileset/META-INF/maven/org.codehaus.mojo.truezip.it/truezip-maven-plugin-test/pom.xml').exists();


list = new File(basedir,'target/copy-into.list').text;

assert list.contains( 'build.log' )
assert list.contains( 'pom.xml' )
assert list.contains( 'goals.txt' )
assert ! list.contains( 'validate.groovy' )


return true