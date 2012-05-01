import java.util.regex.Matcher
import java.util.regex.Pattern

assert ! new File(basedir, 'target/truezip-maven-plugin-test.jar').exists();

list = new File(basedir,'target/remove-a-file.list').text;
assert ! list.contains( 'pom.properties' )
assert   list =~ /.*META-INF.maven.org.codehaus.mojo.truezip.it.truezip-maven-plugin-test.pom.xml.*/
assert   list =~ /.*META-INF.MANIFEST.MF.*/

list = new File(basedir,'target/remove-a-directory.list').text;
assert  list.contains( 'maven' )
assert   list =~ /.*META-INF.MANIFEST.MF.*/


return true