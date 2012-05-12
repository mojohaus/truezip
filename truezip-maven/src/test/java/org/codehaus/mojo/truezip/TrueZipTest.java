package org.codehaus.mojo.truezip;

import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.truezip.TrueZip;
import org.codehaus.mojo.truezip.TrueZipFileSet;
import org.codehaus.mojo.truezip.internal.DefaultTrueZip;
import org.codehaus.plexus.util.FileUtils;

import de.schlichtherle.truezip.file.TFile;

public class TrueZipTest
    extends TestCase
{
    private Log log = new SystemStreamLog();

    private TrueZip truezip = new DefaultTrueZip();

    private TFile basedir = new TFile( System.getProperty( "basedir", "." ) );

    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void tearDown()
        throws Exception
    {
        truezip.sync();
    }
    
    public void testEmptyList()
        throws Exception
    {
        TFile emptyFile = new TFile( basedir, "target/empty-file.zip" );
        if ( emptyFile.exists() ) {
            emptyFile.rm_r();
        }
        emptyFile.createNewFile();

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( emptyFile.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "File list is not empty", 0, fileList.size() );
    }

    public void testListRealArchive()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear" );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );
        fileSet.setFollowArchive( true );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

    }

    public void testListRealArchiveNotFollowInnerArchive()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear" );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( false );
        fileSet.setDirectory( file.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

    }

    public void testListInnerArchive()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear/calculator-ejb-2.1.2.jar" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 9, fileList.size() );
    }

    public void testListInnerArchiveDirectory()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear/calculator-ejb-2.1.2.jar/META-INF" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 6, fileList.size() );
    }

    public void testCopyToDirectory()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        TFile outputDirectory = new TFile( basedir, "target/test/test-copy" );
        FileUtils.deleteDirectory( outputDirectory );

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );

        truezip.copy( fileSet, false, log );
        truezip.sync();

        new TFile( basedir, "target/dependency/calculator.ear" );

        fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( outputDirectory.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

        //test verbatime copy, ie inner archive size unchanged after unpack
        assertEquals( 16158, new java.io.File( outputDirectory, "calculator-war-2.1.2.war" ).length() );
        assertEquals( 8762, new java.io.File( outputDirectory, "calculator-ejb-2.1.2.jar" ).length() );
        
    }

    public void testCopyToArchive()
        throws Exception
    {
        TFile file = new TFile( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        TFile outputDirectory = new TFile( basedir, "target/test/test-copy.zip" );
        if ( outputDirectory.exists() ) {
            outputDirectory.rm_r();
        }
        
        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );

        truezip.copy( fileSet, false, log );
        truezip.sync();

        fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( outputDirectory.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

    }

    public void testDirectArchiveToArchiveCopy()
        throws Exception
    {

        TFile source = new TFile( basedir, "target/dependency/calculator.ear" );
        TFile dest = new TFile( basedir, "target/dependency/calculator.tar" );
        truezip.copyFile( source, dest );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( dest.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testDirectArchiveToDirectoryCopy()
        throws Exception
    {

        TFile source = new TFile( basedir, "target/dependency/calculator.ear" );
        TFile dest = new TFile( basedir, "target/dependency/calculator" );
        truezip.copyFile( source, dest );
        truezip.sync();

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( dest.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testDirectDirectoryToArchiveCopy()
        throws Exception
    {

        TFile source = new TFile( basedir, "target/dependency/calculator" );
        TFile dest = new TFile( basedir, "target/dependency/calculator.zip" );
        truezip.copyFile( source, dest );
        truezip.sync();

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( false );
        fileSet.setDirectory( dest.getPath() );

        List<TFile> fileList = truezip.list( fileSet );

        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testMove()
        throws Exception
    {
        //make a copy of the original test archive
        TFile file = new TFile( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        TFile outputDirectory = new TFile( basedir, "target/test/test-move.zip" );
        if ( outputDirectory.exists() ) {
            outputDirectory.rm_r();
        }

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );
        truezip.copy( fileSet, false, log );

        //do the move
        fileSet.setDirectory( outputDirectory.getPath() );

        outputDirectory = new TFile( basedir, "target/test/test-move" );
        if ( outputDirectory.exists() ) {
            outputDirectory.rm_r();
        }

        fileSet.setOutputDirectory( outputDirectory.getPath() );
        fileSet.addExclude( "**/*.jar" );
        fileSet.addExclude( "**/*.war" );

        truezip.move( fileSet, false, log );

        //test what left in there
        fileSet = new TrueZipFileSet();
        fileSet.setDirectory( outputDirectory.getPath() );
        List<TFile> fileList = truezip.list( fileSet );

        assertEquals( "Invalid file list in " + outputDirectory, 5, fileList.size() );
    }

    public void notestOvaisTarArchive()
        throws Exception
    {
        TFile file = new TFile( basedir, "src/test/data/test.ova" );
        assertTrue( file.exists() );
        
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );
        
        List<TFile> files = truezip.list( fileSet );
        
        assertEquals( 2, files.size() );

        
    }
    
}
