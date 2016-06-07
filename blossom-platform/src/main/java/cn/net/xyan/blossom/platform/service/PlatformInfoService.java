package cn.net.xyan.blossom.platform.service;

import java.util.Collection;

/**
 * Created by zarra on 16/6/7.
 */
public interface PlatformInfoService extends Installer {
    String ArtifactInfo = "/blossom/bls_version.properties";

    String PlatformArtifactId = "blossom-platform";

    String PropertyName = "name";

    String PropertyVersion = "version";

    String PropertyTimestamp = "timestamp";

    class ArtifactInfo{
        public String artifactId;
        public String version;
        public String timestamp;

        public ArtifactInfo(){

        }

        public ArtifactInfo(String artifactId,String version,String timestamp){
            this.artifactId = artifactId;
            this.version = version;
            this.timestamp = timestamp;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    Collection<ArtifactInfo> allArtifactInfo();

    ArtifactInfo platformArtifactInfo();

}
