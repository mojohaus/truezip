list = new File( basedir, 'target/move-a-file.list' ).text

assert ! list.contains( 'MANIFEST.MF' )
assert   list.contains( 'MANIFEST2.MF' )


return true