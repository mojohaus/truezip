package org.codehaus.mojo.truezip.util;

import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.util.FileUtils;

import de.schlichtherle.io.File;

public class TrueZipTest
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

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( emptyFile.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "File list is not empty", 0, fileList.size() );
    }

    public void testListRealArchive()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear" );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );
        fileSet.setFollowArchive( true );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

    }

    public void testListRealArchiveNotFollowInnerArchive()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear" );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( false );
        fileSet.setDirectory( file.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

    }

    public void testListInnerArchive()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear/calculator-ejb-2.1.2.jar" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 9, fileList.size() );
    }

    public void testListInnerArchiveDirectory()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear/calculator-ejb-2.1.2.jar/META-INF" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 6, fileList.size() );
    }

    public void testCopyToDirectory()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        File outputDirectory = new File( basedir, "target/test/test-copy" );
        FileUtils.deleteDirectory( outputDirectory );

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );

        truezip.copy( fileSet, false, log );

        new File( basedir, "target/dependency/calculator.ear" );

        fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( outputDirectory.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

        //test verbatime copy, ie inner archive size unchanged after unpack
        File.update(); //quick sync needed, otherwise the file length is incorrectly calculated
        assertEquals( 16158, new java.io.File( outputDirectory, "calculator-war-2.1.2.war" ).length() );
        assertEquals( 8762, new java.io.File( outputDirectory, "calculator-ejb-2.1.2.jar" ).length() );
        
    }

    public void testCopyToArchive()
        throws Exception
    {
        File file = new File( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        File outputDirectory = new File( basedir, "target/test/test-copy.zip" );
        outputDirectory.delete();
        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );

        truezip.copy( fileSet, false, log );

        new File( basedir, "target/dependency/calculator.ear" );

        fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( outputDirectory.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + file, 7, fileList.size() );

    }

    public void testDirectArchiveToArchiveCopy()
        throws Exception
    {

        File source = new File( basedir, "target/dependency/calculator.ear" );
        File dest = new File( basedir, "target/dependency/calculator.tar" );
        truezip.copyFile( source, dest );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( dest.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testDirectArchiveToDirectoryCopy()
        throws Exception
    {

        File source = new File( basedir, "target/dependency/calculator.ear" );
        File dest = new File( basedir, "target/dependency/calculator" );
        truezip.copyFile( source, dest );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( dest.getPath() );

        List fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testDirectDirectoryToArchiveCopy()
        throws Exception
    {

        File source = new File( basedir, "target/dependency/calculator" );
        File dest = new File( basedir, "target/dependency/calculator.zip" );
        truezip.copyFile( source, dest );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( false );
        fileSet.setDirectory( dest.getPath() );

        List fileList = truezip.list( fileSet );

        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

    }

    public void testMove()
        throws Exception
    {
        //make a copy of the original test archive
        File file = new File( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        File outputDirectory = new File( basedir, "target/test/test-move.zip" );
        outputDirectory.delete();

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );
        truezip.copy( fileSet, false, log );

        //do the move
        fileSet.setDirectory( outputDirectory.getPath() );

        outputDirectory = new File( basedir, "target/test/test-move" );
        outputDirectory.delete();

        fileSet.setOutputDirectory( outputDirectory.getPath() );
        fileSet.addExclude( "**/*.jar" );
        fileSet.addExclude( "**/*.war" );

        truezip.move( fileSet, false, log );

        //test what left in there
        fileSet = new TrueZipFileSet();
        fileSet.setDirectory( outputDirectory.getPath() );
        List fileList = truezip.list( fileSet );

        assertEquals( "Invalid file list in " + outputDirectory, 5, fileList.size() );
    }

}
