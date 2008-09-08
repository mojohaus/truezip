assert new File(basedir, 'target/copy-file/pom.properties').exists();
assert new File(basedir, 'target/copy-file/pom.with.xml.extension').exists();

assert new File(basedir, 'target/copy-fileset/META-INF/MANIFEST.MF').exists();
assert new File(basedir, 'target/copy-fileset/META-INF/maven/org.codehaus.mojo/truezip-maven-plugin-test/pom.xml').exists();
