package org.codehaus.mojo.truezip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.truezip.internal.DefaultTrueZip;
import org.codehaus.mojo.truezip.internal.DefaultTrueZipArchiveDetector;
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

    public void testEmptyList()
        throws Exception
    {
        TFile emptyFile = new TFile( basedir, "target/empty-file.zip" );
        if ( emptyFile.exists() )
        {
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
        truezip.sync( outputDirectory );

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
        if ( outputDirectory.exists() )
        {
            outputDirectory.rm_r();
        }

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );

        truezip.copy( fileSet, false, log );
        truezip.sync( outputDirectory );

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
        truezip.sync( dest );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setFollowArchive( true );
        fileSet.setDirectory( dest.getPath() );

        List<TFile> fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 26, fileList.size() );

        fileSet.setFollowArchive( false );
        fileList = truezip.list( fileSet, false, log );
        assertEquals( "Invalid file list in " + dest, 7, fileList.size() );

        //subarchive checksum changes when use TrueZip's copyFile
        assertFalse( "9ea19802ed109db944a2fb02fff8c035"
            .equals( hash( new File( basedir, "target/dependency/calculator/calculator-war-2.1.2.war" ), "md5" ) ) );

        assertFalse( "1fa71ddc35645e41e88a251fafb239cb"
            .equals( hash( new File( basedir, "target/dependency/calculator/calculator-ejb-2.1.2.jar" ), "md5" ) ) );

    }
    
    public void testArchiveToDirectoryCopyUsingFileSetConfiguration()
        throws Exception
    {

        TFile source = new TFile( basedir, "target/dependency/calculator.ear" );
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( source.getPath() );
        
        TFile dest = new TFile( basedir, "target/dependency/calculator" );
        fileSet.setOutputDirectory( dest.getAbsolutePath() );
        truezip.copy( fileSet );
        truezip.sync( dest );

        //subarchive checksum keep the same when use TrueZip's copy
        assertTrue( "9ea19802ed109db944a2fb02fff8c035"
            .equals( hash( new File( basedir, "target/dependency/calculator/calculator-war-2.1.2.war" ), "md5" ) ) );

        assertTrue( "1fa71ddc35645e41e88a251fafb239cb"
            .equals( hash( new File( basedir, "target/dependency/calculator/calculator-ejb-2.1.2.jar" ), "md5" ) ) );

    }
    

    public void testDirectoryToArchiveCopy()
        throws Exception
    {

        TFile source = new TFile( basedir, "target/dependency/calculator" );
        TFile dest = new TFile( basedir, "target/dependency/calculator.zip" );
        truezip.copyFile( source, dest );
        truezip.sync( dest );

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
        if ( outputDirectory.exists() )
        {
            outputDirectory.rm_r();
        }

        fileSet.setOutputDirectory( outputDirectory.getAbsolutePath() );
        truezip.copy( fileSet, false, log );

        //do the move
        fileSet.setDirectory( outputDirectory.getPath() );

        outputDirectory = new TFile( basedir, "target/test/test-move" );
        if ( outputDirectory.exists() )
        {
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

    public void testOvaIsTarArchive()
        throws Exception
    {
        DefaultTrueZipArchiveDetector trueZipArchiveDetector = new DefaultTrueZipArchiveDetector();
        trueZipArchiveDetector.init();
        
        TFile file = new TFile( basedir, "src/test/data/test.ova" );
        assertTrue( file.exists() );

        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( file.getPath() );

        List<TFile> files = truezip.list( fileSet );

        assertEquals( 2, files.size() );

    }

    /////////////////////////////////////////////////////////////////////////////////
    public static String hash( File file, String type )
        throws IOException, NoSuchAlgorithmException
    {
        MessageDigest sum = MessageDigest.getInstance( type );
        InputStream is = null;

        try
        {
            is = new FileInputStream( file );
            byte[] buf = new byte[8192];
            int i;
            while ( ( i = is.read( buf ) ) > 0 )
            {
                sum.update( buf, 0, i );
            }
        }
        finally
        {
            if ( is != null )
            {
                is.close();
            }
        }

        return encode( sum.digest() );
    }

    private static String encode( byte[] binaryData )
    {
        if ( binaryData.length != 16 && binaryData.length != 20 )
        {
            int bitLength = binaryData.length * 8;
            throw new IllegalArgumentException( "Unrecognized length for binary data: " + bitLength + " bits" );
        }

        String retValue = "";

        for ( int i = 0; i < binaryData.length; i++ )
        {
            String t = Integer.toHexString( binaryData[i] & 0xff );
            if ( t.length() == 1 )
            {
                retValue += ( "0" + t );
            }
            else
            {
                retValue += t;
            }
        }

        return retValue.trim();
    }

}
