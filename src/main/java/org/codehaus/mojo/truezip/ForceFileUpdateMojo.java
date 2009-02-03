package org.codehaus.mojo.truezip;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;

/**
 * Update open archives immediately, flush cached data to disk.
 * 
 * @goal update
 * @phase process-resources
 * @version $Id: $
 */
public class ForceFileUpdateMojo
    extends AbstractArchiveMojo
{

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            File.update();
        }
        catch ( ArchiveException e )
        {
            throw new MojoExecutionException( "Forced file update failed!", e );
        }
    }

}
