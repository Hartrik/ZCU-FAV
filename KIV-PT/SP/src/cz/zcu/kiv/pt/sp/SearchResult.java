package cz.zcu.kiv.pt.sp;

/**
 * Uchovává výsledek hledání.
 *
 * @version 2016-09-30
 * @author Patrik Harag
 */
public class SearchResult {

    private final int start;
    private final int end;

    public SearchResult(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.start;
        hash = 17 * hash + this.end;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SearchResult other = (SearchResult) obj;
        if (this.start != other.start) {
            return false;
        }
        return this.end == other.end;
    }

    @Override
    public String toString() {
        return "Result{" + "start=" + start + ", end=" + end + '}';
    }
}
