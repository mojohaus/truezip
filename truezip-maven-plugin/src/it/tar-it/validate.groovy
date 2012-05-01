list = new File(basedir,'target/list.txt').text;

assert list.contains( 'truezip-maven-plugin-test' )
assert list.contains( 'subarchive.jar' )

return true