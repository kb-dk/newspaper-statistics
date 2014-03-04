package dk.statsbiblioteket.medieplatform.newspaper.statistics.collector;

import java.util.TreeSet;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;

/**
 * Extends the <code>StatisticsKey</code> class with functionality for sorting the keys according to
 * a page number. The <code>SectionStatisticsKey</code> maintains a list of pages included in the section
 * modeled by this key. The page list is build by adding <code>SectionStatisticsKey</code>. The keys are
 * then sorted according to the getFirstPage() values for the keys.
 *
 * Note that the pages information is not included in the hashcode and equals operation, so the key in's still immutable
 * as seen from these two method. This means the key may still be used as key in hash based collections. This also means
 * two keys with the same type and name are considered equal even if the page list differs.
 */
public class SectionStatisticsKey extends StatisticsKey {
    private TreeSet<String> pages = new TreeSet();

    public SectionStatisticsKey(String type, String name, String page) {
        super(type, name);
        pages.add(page);
    }

    /**
     * Used for sorting the keys according to the first page in the section.
     * @return The first page in the page list.
     */
    public String getFirstPage() {
        return pages.first();
    }

    /**
     * Adds the pages from the supplied kay to this key.
     * @param key
     */
    @Override
    public void add(StatisticsKey key) {
        if (key instanceof SectionStatisticsKey) {
            pages.addAll(((SectionStatisticsKey)key).pages);
        }
    }

    @Override
    public int compareTo(StatisticsKey sk) {
        if (sk instanceof SectionStatisticsKey) {
            SectionStatisticsKey other = (SectionStatisticsKey)sk;
            return getFirstPage().compareTo(other.getFirstPage());
        } else {
            return super.compareTo(sk);
        }
    }

    @Override
    public String toString() {
        return "SectionStatisticsKey{" +
                "pages=" + pages +
                "} " + super.toString();
    }
}
