assert ! new File(basedir, 'target/truezip-maven-plugin-test.jar').exists();

list = new File(basedir,'target/remove-a-file.list').text;
assert ! list.contains( 'pom.properties' )
assert list.contains( 'maven' )

list = new File(basedir,'target/remove-a-directory.list').text;
assert ! list.contains( 'maven' )
assert list.contains( 'MANIFEST.MF' )

    