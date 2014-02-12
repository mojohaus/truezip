package org.codehaus.mojo.truezip.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.mojo.truezip.TrueZip;
import org.codehaus.mojo.truezip.TrueZipFileSet;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;

public class DefaultTrueZip
    implements TrueZip
{

    public void sync()
        throws FsSyncException
    {
        TVFS.umount();
    }

    public void sync( TFile file )
        throws FsSyncException
    {
        TVFS.umount( file );
    }

    public List<TFile> list( TrueZipFileSet fileSet )
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        return list( fileSet, fileSetManager );
    }

    private List<TFile> list( TrueZipFileSet fileSet, TrueZipFileSetManager fileSetManager )
    {
        if ( StringUtils.isBlank( fileSet.getDirectory() ) )
        {
            fileSet.setDirectory( "." );
        }

        String[] files = fileSetManager.getIncludedFiles( fileSet );

        List<TFile> fileLists = new ArrayList<TFile>();

        for ( int i = 0; i < files.length; ++i )
        {
            TFile source = new TFile( fileSet.getDirectory(), files[i] );
            fileLists.add( source );
        }

        return fileLists;

    }

    public void move( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        move( fileSet, fileSetManager );
    }

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////

    public void copy( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        copy( fileSet, fileSetManager );
    }

    public void copy( TrueZipFileSet oneFileSet, TrueZipFileSetManager fileSetManager )
        throws IOException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            oneFileSet.setDirectory( "." );
        }

        String[] files = fileSetManager.getIncludedFiles( oneFileSet );

        for ( int i = 0; i < files.length; ++i )
        {
            String relativeDestPath = files[i];
            if ( !StringUtils.isBlank( oneFileSet.getOutputDirectory() ) )
            {
                relativeDestPath = oneFileSet.getOutputDirectory() + "/" + relativeDestPath;
            }
            TFile dest = new TFile( relativeDestPath );

            TFile source = new TFile( oneFileSet.getDirectory(), files[i] );

            this.copyFile( source, dest );
        }

    }

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////

    public void copyFile( TFile source, TFile dest )
        throws IOException
    {
        TFile destParent = (TFile) dest.getParentFile();

        if ( !destParent.isDirectory() )
        {
            if ( !destParent.mkdirs() )
            {
                throw new IOException( "Unable to create " + destParent );
            }
        }

        if ( source.isArchive() )
        {
            if ( dest.isArchive() && getFileExtension( dest.getPath() ).equals( getFileExtension( source.getPath() ) ) )
            {
                // we want fast verbatim copy and keep hash value intact.
                // convert source and dest to have NO associate archive type so that verbatim copy can happen
                source = new TFile( source.getParentFile(), source.getName(), TArchiveDetector.NULL );
                dest = new TFile( dest.getParentFile(), dest.getName(), TArchiveDetector.NULL );
                // this looks expensive, but selective umounts for both source and dest are not working
                TVFS.umount();
                source.cp_rp( dest );
            }
            else
            {
                source.cp_rp( dest );

            }
        }
        else if ( source.isDirectory() )
        {
            source.cp_rp( dest );
        }
        else
        {
            TFile.cp_p( source, dest );
        }
    }

    public void moveFile( TFile source, TFile dest )
        throws IOException
    {

        TFile file = new TFile( source );

        TFile tofile = new TFile( dest );

        file.mv( tofile );
    }

    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////

    public void remove( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        remove( fileSet, fileSetManager );
    }

    private void remove( TrueZipFileSet oneFileSet, TrueZipFileSetManager fileSetManager )
        throws IOException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            throw new IOException( "FileSet's directory is required." );
        }

        TFile directory = new TFile( oneFileSet.getDirectory() );

        if ( !directory.isDirectory() )
        {
            throw new IOException( "FileSet's directory: " + directory + " not found." );
        }

        fileSetManager.delete( oneFileSet, true );

    }

    private void move( TrueZipFileSet fileSet, TrueZipFileSetManager fileSetManager )
        throws IOException
    {
        this.copy( fileSet, fileSetManager );
        this.remove( fileSet, fileSetManager );
    }

    private String getFileExtension( String filePath )
    {

        String[] tokens = filePath.split( "\\." );
        if ( tokens.length >= 1 )
        {
            return tokens[tokens.length - 1];
        }

        return null;
    }
}
