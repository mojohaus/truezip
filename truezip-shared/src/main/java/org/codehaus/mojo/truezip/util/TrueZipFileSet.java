package org.codehaus.mojo.truezip.util;

import org.apache.maven.shared.model.fileset.FileSet;

public class TrueZipFileSet
    extends FileSet
{
    public static final long serialVersionUID = -1;

    private boolean followArchive = false;

    public boolean isFollowArchive()
    {
        return followArchive;
    }

    public void setFollowArchive( boolean followArchive )
    {
        this.followArchive = followArchive;
    }
    
    
}
