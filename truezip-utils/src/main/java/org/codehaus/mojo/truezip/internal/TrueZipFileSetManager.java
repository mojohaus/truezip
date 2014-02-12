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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.mappers.FileNameMapper;
import org.apache.maven.shared.model.fileset.mappers.MapperException;
import org.apache.maven.shared.model.fileset.mappers.MapperUtil;
import org.codehaus.mojo.truezip.TrueZipFileSet;

import de.schlichtherle.truezip.file.TFile;

/**
 * Provides operations for use with FileSet instances, such as retrieving the included/excluded files, deleting all
 * matching entries, etc. This is a fork of maven's shared FileSetManager with the following changes - Use
 * TrueZipDirectoryScanner instead DirectoryScanner - use java.io.File in isSymLink(); Note: symbolic support is
 * unsupported
 *
 * @author jdcasey
 * @version $Id$
 */
public class TrueZipFileSetManager
{
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public TrueZipFileSetManager()
    {
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * @param fileSet
     * @return the included files as map
     * @throws MapperException if any
     * @see #getIncludedFiles(FileSet)
     */
    public Map mapIncludedFiles( TrueZipFileSet fileSet )
        throws MapperException
    {
        String[] sourcePaths = getIncludedFiles( fileSet );
        Map mappedPaths = new LinkedHashMap();

        FileNameMapper fileMapper = MapperUtil.getFileNameMapper( fileSet.getMapper() );

        for ( int i = 0; i < sourcePaths.length; i++ )
        {
            String sourcePath = sourcePaths[i];

            String destPath;
            if ( fileMapper != null )
            {
                destPath = fileMapper.mapFileName( sourcePath );
            }
            else
            {
                destPath = sourcePath;
            }

            mappedPaths.put( sourcePath, destPath );
        }

        return mappedPaths;
    }

    /**
     * Get all the filenames which have been included by the rules in this fileset.
     *
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of matching filenames, relative to the basedir of the file-set.
     */
    public String[] getIncludedFiles( TrueZipFileSet fileSet )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner != null )
        {
            return scanner.getIncludedFiles();
        }

        return EMPTY_STRING_ARRAY;
    }

    /**
     * Get all the directory names which have been included by the rules in this fileset.
     *
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of matching dirnames, relative to the basedir of the file-set.
     */
    public String[] getIncludedDirectories( TrueZipFileSet fileSet )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner != null )
        {
            return scanner.getIncludedDirectories();
        }

        return EMPTY_STRING_ARRAY;
    }

    /**
     * Get all the filenames which have been excluded by the rules in this fileset.
     *
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of non-matching filenames, relative to the basedir of the file-set.
     */
    public String[] getExcludedFiles( TrueZipFileSet fileSet )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner != null )
        {
            return scanner.getExcludedFiles();
        }

        return EMPTY_STRING_ARRAY;
    }

    /**
     * Get all the directory names which have been excluded by the rules in this fileset.
     *
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of non-matching dirnames, relative to the basedir of the file-set.
     */
    public String[] getExcludedDirectories( TrueZipFileSet fileSet )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner != null )
        {
            return scanner.getExcludedDirectories();
        }

        return EMPTY_STRING_ARRAY;
    }

    /**
     * Delete the matching files and directories for the given file-set definition.
     *
     * @param fileSet The file-set matching rules, along with search base directory
     * @throws IOException If a matching file cannot be deleted
     */
    public void delete( TrueZipFileSet fileSet )
        throws IOException
    {
        delete( fileSet, true );
    }

    /**
     * Delete the matching files and directories for the given file-set definition.
     *
     * @param fileSet The file-set matching rules, along with search base directory.
     * @param throwsError Throw IOException when errors have occurred by deleting files or directories.
     * @throws IOException If a matching file cannot be deleted and <code>throwsError=true</code>, otherwise print
     *             warning messages.
     */
    public void delete( TrueZipFileSet fileSet, boolean throwsError )
        throws IOException
    {
        Set deletablePaths = findDeletablePaths( fileSet );

        List warnMessages = new LinkedList();

        for ( Iterator it = deletablePaths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();

            TFile file = new TFile( fileSet.getDirectory(), path );

            if ( file.exists() )
            {
                if ( file.isDirectory() )
                {
                    if ( fileSet.isFollowSymlinks() || !isSymlink( file ) )
                    {
                        removeDir( file, fileSet.isFollowSymlinks(), throwsError, warnMessages );
                    }
                    else
                    {

                        if ( !file.delete() )
                        {
                            String message = "Unable to delete symlink " + file.getAbsolutePath();
                            if ( throwsError )
                            {
                                throw new IOException( message );
                            }
                        }
                    }
                }
                else
                {
                    if ( !delete( file ) )
                    {
                        String message = "Failed to delete file " + file.getAbsolutePath() + ". Reason is unknown.";
                        if ( throwsError )
                        {
                            throw new IOException( message );
                        }
                    }
                }
            }
        }

    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private boolean isSymlink( TFile file )
        throws IOException
    {
        TFile fileInCanonicalParent = null;
        java.io.File parentDir = file.getParentFile(); // truezip-plugin specific change
        if ( parentDir == null )
        {
            fileInCanonicalParent = file;
        }
        else
        {
            fileInCanonicalParent = new TFile( parentDir.getCanonicalPath(), file.getName() );
        }
        return !fileInCanonicalParent.getCanonicalFile().equals( fileInCanonicalParent.getAbsoluteFile() );

    }

    private Set findDeletablePaths( TrueZipFileSet fileSet )
    {
        Set includes = findDeletableDirectories( fileSet );
        includes.addAll( findDeletableFiles( fileSet, includes ) );

        return includes;
    }

    private Set findDeletableDirectories( TrueZipFileSet fileSet )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner == null )
        {
            return Collections.EMPTY_SET;
        }

        Set includes = new HashSet( Arrays.asList( scanner.getIncludedDirectories() ) );
        Collection excludes = new ArrayList( Arrays.asList( scanner.getExcludedDirectories() ) );
        Collection linksForDeletion = new ArrayList();

        if ( !fileSet.isFollowSymlinks() )
        {

            // we need to see which entries were only excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();

            List includedDirsAndSymlinks = Arrays.asList( scanner.getIncludedDirectories() );

            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( includedDirsAndSymlinks );

            excludes.removeAll( includedDirsAndSymlinks );
        }

        excludeParentDirectoriesOfExcludedPaths( excludes, includes );

        includes.addAll( linksForDeletion );

        return includes;
    }

    private Set findDeletableFiles( TrueZipFileSet fileSet, Set deletableDirectories )
    {
        TrueZipDirectoryScanner scanner = scan( fileSet );

        if ( scanner == null )
        {
            return deletableDirectories;
        }

        Set includes = deletableDirectories;
        includes.addAll( Arrays.asList( scanner.getIncludedFiles() ) );
        Collection excludes = new ArrayList( Arrays.asList( scanner.getExcludedFiles() ) );
        Collection linksForDeletion = new ArrayList();

        if ( !fileSet.isFollowSymlinks() )
        {
            // we need to see which entries were only excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();

            List includedFilesAndSymlinks = Arrays.asList( scanner.getIncludedFiles() );

            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( includedFilesAndSymlinks );

            excludes.removeAll( includedFilesAndSymlinks );
        }

        excludeParentDirectoriesOfExcludedPaths( excludes, includes );

        includes.addAll( linksForDeletion );

        return includes;
    }

    /**
     * Removes all parent directories of the already excluded files/directories from the given set of deletable
     * directories. I.e. if "subdir/excluded.txt" should not be deleted, "subdir" should be excluded from deletion, too.
     *
     * @param excludedPaths The relative paths of the files/directories which are excluded from deletion, must not be
     *            <code>null</code>.
     * @param deletablePaths The relative paths to files/directories which are scheduled for deletion, must not be
     *            <code>null</code>.
     */
    private void excludeParentDirectoriesOfExcludedPaths( Collection excludedPaths, Set deletablePaths )
    {
        for ( Iterator it = excludedPaths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();

            String parentPath = new TFile( path ).getParent();

            while ( parentPath != null )
            {
                boolean removed = deletablePaths.remove( parentPath );

                parentPath = new TFile( parentPath ).getParent();
            }
        }

        if ( !excludedPaths.isEmpty() )
        {
            boolean removed = deletablePaths.remove( "" );
        }
    }

    /**
     * Delete a directory
     *
     * @param dir the directory to delete
     * @param followSymlinks whether to follow symbolic links, or simply delete the link
     * @param throwsError Throw IOException when errors have occurred by deleting files or directories.
     * @param warnMessages A list of warning messages used when <code>throwsError=false</code>.
     * @throws IOException If a matching file cannot be deleted and <code>throwsError=true</code>.
     */
    private void removeDir( TFile dir, boolean followSymlinks, boolean throwsError, List warnMessages )
        throws IOException
    {
        String[] list = dir.list();
        if ( list == null )
        {
            list = new String[0];
        }

        for ( int i = 0; i < list.length; i++ )
        {
            String s = list[i];
            TFile f = new TFile( dir, s );
            if ( f.isDirectory() && ( followSymlinks || !isSymlink( f ) ) )
            {
                removeDir( f, followSymlinks, throwsError, warnMessages );
            }
            else
            {
                if ( !delete( f ) )
                {
                    String message = "Unable to delete file " + f.getAbsolutePath();
                    if ( throwsError )
                    {
                        throw new IOException( message );
                    }

                    if ( !warnMessages.contains( message ) )
                    {
                        warnMessages.add( message );
                    }
                }
            }
        }

        if ( !delete( dir ) )
        {
            String message = "Unable to delete directory " + dir.getAbsolutePath();
            if ( throwsError )
            {
                throw new IOException( message );
            }

        }
    }

    /**
     * Delete a file
     *
     * @param f a file
     */
    private boolean delete( TFile f )
    {
        try
        {
            FileUtils.forceDelete( f );
        }
        catch ( IOException e )
        {
            return false;
        }

        return true;
    }

    private TrueZipDirectoryScanner scan( TrueZipFileSet fileSet )
    {
        TFile basedir = new TFile( fileSet.getDirectory() );
        if ( !basedir.exists() || !basedir.isDirectory() )
        {
            return null;
        }

        TrueZipDirectoryScanner scanner = new TrueZipDirectoryScanner();

        String[] includesArray = fileSet.getIncludesArray();
        String[] excludesArray = fileSet.getExcludesArray();

        if ( includesArray.length > 0 )
        {
            scanner.setIncludes( includesArray );
        }

        if ( excludesArray.length > 0 )
        {
            scanner.setExcludes( excludesArray );
        }

        if ( fileSet.isUseDefaultExcludes() )
        {
            scanner.addDefaultExcludes();
        }

        scanner.setBasedir( basedir );
        scanner.setFollowSymlinks( fileSet.isFollowSymlinks() );
        scanner.setFollowArchive( fileSet.isFollowArchive() );

        scanner.scan();

        return scanner;
    }

}
