def latestSnapshot = new GroovyShell().parse(new File('build/latestsnapshot.groovy'))
def shouldFail = { Class<? extends Throwable> expectedType, Closure block ->
  try {
    block()
    assert false: "Expected ${expectedType.simpleName} to be thrown"
  } catch (Throwable throwable) {
    assert expectedType.isInstance(throwable): "Expected ${expectedType.simpleName}, but got ${throwable.class.simpleName}"
    return throwable.message
  }
}

def versionMetadata = latestSnapshot.createXmlSlurper().parseText('''
<metadata>
  <versioning>
    <latest>4.0.0-REACT19-SNAPSHOT</latest>
    <versions>
      <version>4.0.0-REACT19-SNAPSHOT</version>
      <version>3.11.10-SNAPSHOT</version>
      <version>3.9.20-SNAPSHOT</version>
      <version>3.11.11-SNAPSHOT</version>
      <version>2.48.2-SNAPSHOT</version>
      <version>3.12.0-RC1-SNAPSHOT</version>
    </versions>
  </versioning>
</metadata>
''')

assert latestSnapshot.selectLatestSnapshotVersion(versionMetadata, 3) == '3.11.11-SNAPSHOT'

def noMatchingVersionMetadata = latestSnapshot.createXmlSlurper().parseText('''
<metadata>
  <versioning>
    <versions>
      <version>4.0.0-SNAPSHOT</version>
    </versions>
  </versioning>
</metadata>
''')

def noMatchingVersionError = shouldFail(IllegalStateException) {
  latestSnapshot.selectLatestSnapshotVersion(noMatchingVersionMetadata, 3)
}
assert noMatchingVersionError == 'No 3.x snapshot version found in metadata'

def snapshotMetadata = latestSnapshot.createXmlSlurper().parseText('''
<metadata>
  <versioning>
    <snapshotVersions>
      <snapshotVersion>
        <extension>pom</extension>
        <value>3.11.11-20260724.093126-11</value>
      </snapshotVersion>
      <snapshotVersion>
        <extension>tar.gz</extension>
        <value>3.11.11-20260724.093126-11</value>
      </snapshotVersion>
    </snapshotVersions>
  </versioning>
</metadata>
''')

assert latestSnapshot.selectSnapshotArtifactVersion(
  snapshotMetadata,
  '3.11.11-SNAPSHOT',
  'tar.gz'
) == '3.11.11-20260724.093126-11'

def missingExtensionError = shouldFail(IllegalStateException) {
  latestSnapshot.selectSnapshotArtifactVersion(snapshotMetadata, '3.11.11-SNAPSHOT', 'smp')
}
assert missingExtensionError == "No snapshot artifact with extension 'smp' found for 3.11.11-SNAPSHOT"
