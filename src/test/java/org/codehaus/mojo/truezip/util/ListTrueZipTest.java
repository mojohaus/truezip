package org.codehaus.mojo.truezip.util;


import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.shared.model.fileset.FileSet;

import de.schlichtherle.io.File;

public class ListTrueZipTest
    extends TestCase
{
    private Log log = new SystemStreamLog();
    private TrueZip truezip = new DefaultTrueZip();
    private File basedir = new File( System.getProperty( "basedir", "." ) );
    
    
    public void setUp()
        throws Exception
    {
        super.setUp();
    }
    
    public void testEmptyList()
        throws Exception
    {
        File emptyFile = new File( basedir, "target/empty-file.zip" );
        emptyFile.delete();
        emptyFile.createNewFile();
        
        FileSet fileSet = new FileSet();
        fileSet.setDirectory( emptyFile.getPath() );
        
        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "File list is not empty", 0, fileList.size() );
    }
    
    public void testListRealArchive()
        throws Exception
    {
        File file = new File( basedir, "target/calculator.ear" );
        
        FileSet fileSet = new FileSet();
        fileSet.setDirectory( file.getPath() );
        
        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );
        
    }
}
