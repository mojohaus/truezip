listFile = new File( basedir, 'target/move-a-file.list' )

def buffer = new String()

listFile.eachLine
{
   buffer << it
}

println buffer
 