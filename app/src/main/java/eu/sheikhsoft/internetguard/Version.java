package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


public class Version implements Comparable<Version> {

    private String version;

    public Version(String version) {
        this.version = version.replace("-beta", "");
    }

    @Override
    public int compareTo(Version other) {
        String[] lhs = this.version.split("\\.");
        String[] rhs = other.version.split("\\.");
        int length = Math.max(lhs.length, rhs.length);
        for (int i = 0; i < length; i++) {
            int vLhs = (i < lhs.length ? Integer.parseInt(lhs[i]) : 0);
            int vRhs = (i < rhs.length ? Integer.parseInt(rhs[i]) : 0);
            if (vLhs < vRhs)
                return -1;
            if (vLhs > vRhs)
                return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return version;
    }
}
