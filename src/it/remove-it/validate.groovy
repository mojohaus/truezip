def file = new File(basedir, 'target/truezip-maven-plugin-test.jar')
if ( file.exists())
{
   throw new RuntimeException("Found deleted " + file );
}

