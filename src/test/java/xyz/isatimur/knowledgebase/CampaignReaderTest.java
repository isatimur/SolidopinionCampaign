package xyz.isatimur.knowledgebase;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Timur Isachenko {@literal <tisachenko@at-consulting.ru>} on 09.07.2017.
 */
public class CampaignReaderTest {
    Stream<String> lines;
    Stream<String> inputLines;
    CampaignReader campaignReader;

    @Before
    public void setUp() throws Exception {
        String pathToInputFIle = "newInput.txt";

        lines = Stream.of(
                "campaign_a 3 4 10 2",
                "campaign_b 9 14 15 21 3",
                "campaign_c 12 1024 200 3 9 4");
        inputLines = Stream.of(
                "1 3 4 5 10 2 200",
                "2 3",
                "3 3",
                "4 4 10 15",
                "5 1024 15 200 21 9 14 15",
                "6 9000 29833 65000");

    }

    @Test
    public void testSimpleInputData() {
        List<Optional<Map.Entry<String, Long>>> optionals = CampaignReader.solution(lines, inputLines.limit(1));
        assertThat(1, is(optionals.size()));
        assertThat("campaign_a", is(optionals.get(0).map(Map.Entry<String, Long>::getKey).orElse("no campaign")));
        assertThat(4l, is(optionals.get(0).map(Map.Entry<String, Long>::getValue).get()));
        lines = Stream.of(
                "campaign_a 3 4 10 2",
                "campaign_b 9 14 15 21 3",
                "campaign_c 12 1024 200 3 9 4");
        optionals = CampaignReader.solution(lines, Stream.of("6 9000 29833 65000"));
        assertThat("no campaign", is(optionals.get(0).map(Map.Entry<String, Long>::getKey).orElse("no campaign")));
    }
}