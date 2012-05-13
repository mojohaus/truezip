package org.codehaus.mojo.truezip;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.FsSyncException;

/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 *
 * Generic interface to manipulate recognizable archives
 *
 */
public interface TrueZip
{
    List<TFile> list( TrueZipFileSet fileSet, boolean verbose, Log logger );

    List<TFile> list( TrueZipFileSet fileSet );

    void copy( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void copy( TrueZipFileSet oneFileSet )
        throws IOException;

    void copyFile( TFile source, TFile dest )
        throws IOException;

    void moveFile( TFile source, TFile dest )
       throws IOException;

    void move( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void move( TrueZipFileSet oneFileSet )
        throws IOException;

    void remove( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void remove( TrueZipFileSet oneFileSet )
        throws IOException;

    void sync()
        throws FsSyncException;
    
    void sync( TFile file )
        throws FsSyncException;
    
}
